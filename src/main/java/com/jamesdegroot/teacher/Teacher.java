package com.jamesdegroot.teacher;

import java.util.List;
import java.util.ArrayList;

public class Teacher {
    private String name;
    private List<String> schedule;
    private double timeAllocation; // percentage of time allocated to teaching
    private TeacherScheduleStatusEnum classScheduleStatus; // status of the teacher's schedule
    private TeacherTypeEnum jobType; // type of the teacher's job
    private int dutiesThisSemester = 0; // counter for duties assigned this semester
    private int maxDutiesPerSemester;   // maximum number of duties per semester

    /**
     * Creates a new Teacher with the given name and initializes their schedule.
     * @param name The full name of the teacher
     */
    public Teacher(String name) {
        this.name = name;
        this.schedule = new ArrayList<>();
        this.classScheduleStatus = TeacherScheduleStatusEnum.NO_LOAD;
        this.jobType = TeacherTypeEnum.REGULAR;
        
        // Initialize all 10 periods as empty
        for (int i = 0; i < 10; i++) {
            schedule.add("");
        }
    }

    /**
     * Adds a class period to the teacher's schedule and updates their type.
     * @param item The class/period details to add
     * @param index The period number (0-9)
     */
    public void addScheduleItem(String item, int index) {
        if (index >= 0 && index < 10) {
            schedule.set(index, item);
            this.jobType = determineTeacherType(); // Update type when schedule changes
        }
    }

    /**
     * Calculates the teacher's time allocation based on their schedule.
     * Considers periods 5 and 10 only if they contain classes.
     * Updates both timeAllocation and status fields.
     */
    public void calculateTimeAllocation() {
        int filledPeriods = 0;
        int totalPeriods = 8;  // Default to 8 periods
        
        // Check if teacher has classes in columns 5 or 10 (index 4 and 9)
        if (schedule.size() > 4 && !schedule.get(4).trim().isEmpty()) {
            totalPeriods++;
        }
        if (schedule.size() > 9 && !schedule.get(9).trim().isEmpty()) {
            totalPeriods++;
        }
        
        // Count filled periods (skipping columns 5 and 10 if empty)
        for (int i = 0; i < schedule.size(); i++) {
            if (i != 4 && i != 9) {  // Regular periods
                if (!schedule.get(i).trim().isEmpty()) {
                    filledPeriods++;
                }
            } else if (!schedule.get(i).trim().isEmpty()) {  // Extra periods (5 and 10)
                filledPeriods++;
            }
        }
        
        this.timeAllocation = (double) filledPeriods / totalPeriods;
        calculateScheduleStatus();
    }

    /**
     * Updates the teacher's status based on their time allocation.
     * Maps exact number of periods to status.
     */
    private void calculateScheduleStatus() {
        int filledPeriods = 0;
        // Count non-empty periods (excluding periods 5 and 10)
        for (int i = 0; i < schedule.size(); i++) {
            if (i != 4 && i != 9 && !schedule.get(i).trim().isEmpty()) {
                filledPeriods++;
            }
        }
        
        // Calculate time allocation based on 6 periods
        this.timeAllocation = (double) filledPeriods / 6.0;
        
        // Map exact number of periods to status
        switch (filledPeriods) {
            case 7:
            case 8: 
                classScheduleStatus = TeacherScheduleStatusEnum.OVER_FULL_TIME;  // >100%
                break;
            case 6: 
                classScheduleStatus = TeacherScheduleStatusEnum.FULL_TIME;    // 100%
                break;
            case 5: 
                classScheduleStatus = TeacherScheduleStatusEnum.FIVE_SIXTHS;  // 83.3%
                break;
            case 4: 
                classScheduleStatus = TeacherScheduleStatusEnum.FOUR_SIXTHS;  // 66.7%
                break;
            case 3: 
                classScheduleStatus = TeacherScheduleStatusEnum.THREE_SIXTHS; // 50%
                break;
            case 2: 
                classScheduleStatus = TeacherScheduleStatusEnum.TWO_SIXTHS;   // 33.3%
                break;
            case 1: 
                classScheduleStatus = TeacherScheduleStatusEnum.ONE_SIXTH;    // 16.7%
                break;
            default: 
                classScheduleStatus = TeacherScheduleStatusEnum.NO_LOAD;     // 0%
                break;
        }
    }
    /**
     * Calculates the teacher's job type based on their schedule.
     * Considers periods 5 and 10 only if they contain classes.
     * Updates both timeAllocation and status fields.
     * 
     * IMPORTANT CODES:
     * 1CO = CO-OP Teacher = classes are 2 hours long
     * PPL = Gym teacher (they are in gym during lunch)
     * RCR = credit recovery teacher (guidance counselor)
     * 2GU = Guidance Counselor
     * GLE = Guidance Counselor
     */
    public void calculateJobType() {
        // TODO: Implement job type calculation logic
    }

    /**
     * Sets the teacher's job type.
     * @param jobType The TeacherType enum value representing their job type
     */
    public void setJobType(TeacherTypeEnum jobType) {
        this.jobType = jobType;
    }

    /**
     * Gets the teacher's full name.
     * @return The teacher's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the teacher's calculated time allocation.
     * @return A decimal between 0 and 1 representing their teaching load
     */
    public double getTimeAllocation() {
        return timeAllocation;
    }

    /**
     * Gets the teacher's current status.
     * @return The TeacherStatus enum value representing their time status
     */
    public TeacherScheduleStatusEnum getClassScheduleStatus() {
        return classScheduleStatus;
    }

    /**
     * Gets the number of filled teaching periods (excluding 5 and 10).
     * @return Number of non-empty teaching periods
     */
    public int getFilledPeriods() {
        int count = 0;
        for (int i = 0; i < schedule.size(); i++) {
            if (i != 4 && i != 9 && !schedule.get(i).trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a string representation of the teacher's schedule.
     * @return A string containing the teacher's name, status, and schedule
     */ 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Teacher: %s\n", name));
        sb.append(String.format("Type: %s\n", jobType.toString()));
        sb.append(String.format("Status: %s (%.2f load)\n", 
            classScheduleStatus.toString().replace("_", " "), 
            timeAllocation));
        sb.append(String.format("Duties: %d/%d\n", dutiesThisSemester, maxDutiesPerSemester));
        sb.append("Schedule:\n");
        
        // Print schedule with period numbers
        for (int i = 0; i < schedule.size(); i++) {
            String period = schedule.get(i).trim();
            if (period.isEmpty()) {
                period = "FREE";
            }
            // Add special notation for periods 5 and 10
            String periodLabel = (i == 4 || i == 9) ? String.format("Period %d*", i + 1) : String.format("Period %d ", i + 1);
            sb.append(String.format("  %-10s: %s\n", periodLabel, period));
        }
        
        // Add note about special periods if they exist
        if (schedule.size() > 4) {
            sb.append("\n* Periods 5 and 10 are optional periods\n");
        }
        
        return sb.toString();
    }

    //EXAMPLE OUTPUT
    // Teacher: John Smith
    // Status: FULL_TIME (0.75 load)
    // Schedule:
    //   Period 1 : Math Grade 10
    //   Period 2 : Math Grade 11
    //   Period 3 : FREE
    //   Period 4 : Math Grade 9
    //   Period 5*: FREE
    //   Period 6 : Math Grade 12
    //   Period 7 : FREE
    //   Period 8 : Math Grade 10
    //   Period 9 : Math Grade 11
    //   Period 10*: FREE

    // * Periods 5 and 10 are optional periods

    /**
     * Determines the teacher type based on their course schedule.
     * @return TeacherTypeEnum representing the teacher's primary teaching area
     */
    public TeacherTypeEnum determineTeacherType() {
        // Initialize maxDutiesPerSemester to default value
        maxDutiesPerSemester = -1;
        
        // Count occurrences of different course types
        int coopCount = 0;
        int gymCount = 0;
        int guidanceCount = 0;
        int creditRecoveryCount = 0;
        
        // Check all course codes
        for (String timeSlot : schedule) {
            if (timeSlot == null || timeSlot.trim().isEmpty()) {
                continue;
            }
            
            // Split multiple courses in the same time slot
            String[] courses = timeSlot.split(",");
            for (String course : courses) {
                String courseCode = course.toUpperCase()
                    .replaceAll("\"", "")         // Remove quotes
                    .split("[ ,-]")[0];           // Split on space, comma, or dash and take first part
                
                // Check for excluded course codes
                if (courseCode.contains("PPL") || 
                    courseCode.contains("1CO") ||
                    courseCode.contains("1RC") ||
                    courseCode.contains("RCR") ||
                    courseCode.contains("2GU") ||
                    courseCode.contains("GLE") ||
                    courseCode.contains("2LI")) {
                    maxDutiesPerSemester = 0;
                }
                
                // Count course types for teacher type determination
                if (courseCode.contains("1CO")) {
                    coopCount++;
                    return TeacherTypeEnum.COOP;
                } else if (courseCode.contains("PPL")) {
                    gymCount++;
                    return TeacherTypeEnum.GYM;
                } else if (courseCode.contains("2GU") || courseCode.contains("GLE")) {
                    guidanceCount++;
                    return TeacherTypeEnum.GUIDANCE;
                } else if (courseCode.contains("1RC") || courseCode.contains("RCR")) {
                    creditRecoveryCount++;
                    return TeacherTypeEnum.CREDIT_RECOVERY;
                }
            }
        }
        
        // Determine primary role based on most frequent course type
        if (coopCount > 2) return TeacherTypeEnum.COOP;
        if (gymCount > 2) return TeacherTypeEnum.GYM;
        if (guidanceCount > 2) return TeacherTypeEnum.GUIDANCE;
        if (creditRecoveryCount > 2) return TeacherTypeEnum.CREDIT_RECOVERY;
        
        return TeacherTypeEnum.REGULAR;
    }

    /**
     * Gets the teacher's job type.
     * @return The TeacherTypeEnum representing their job type
     */
    public TeacherTypeEnum getJobType() {
        return jobType;
    }

    /**
     * Calculates the maximum number of duties per semester based on type and allocation
     */
    public void calculateMaxDutiesPerSemester() {
        // Skip calculation if already set to 0 by excluded course codes
        if (maxDutiesPerSemester == 0) {
            return;
        }
        
        // Calculate based on teacher type and status
        if (jobType == TeacherTypeEnum.GUIDANCE) {
            maxDutiesPerSemester = 25;
        } else if (jobType == TeacherTypeEnum.HEAD) {
            maxDutiesPerSemester = 10;
        } else {
            // Map duties based on schedule status
            switch (classScheduleStatus) {
                case FULL_TIME: maxDutiesPerSemester = 14; break;
                case FIVE_SIXTHS: maxDutiesPerSemester = 11; break;
                case FOUR_SIXTHS: maxDutiesPerSemester = 9; break;
                case THREE_SIXTHS: maxDutiesPerSemester = 7; break;
                case TWO_SIXTHS: maxDutiesPerSemester = 6; break;
                default: maxDutiesPerSemester = 0; break;
            }
        }
    }

    public int getMaxDutiesPerSemester() {
        return maxDutiesPerSemester;
    }

    public int getDutiesThisSemester() {
        return dutiesThisSemester;
    }

    public void incrementDutiesThisSemester() {
        dutiesThisSemester++;
    }

    public List<String> getSchedule() {
        return schedule;
    }
}
