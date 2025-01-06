package com.jamesdegroot;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jamesdegroot.calendar.Calendar;
import com.jamesdegroot.calendar.Holiday;
import com.jamesdegroot.io.ReadCalendarFromDisk;
import com.jamesdegroot.io.ReadTeachersFromDisk;
import com.jamesdegroot.teacher.Teacher;
import com.jamesdegroot.teacher.TeacherScheduleStatusEnum;
import com.jamesdegroot.teacher.TeacherTypeEnum;

/**
 * Generates a duty calendar for a school year.
 */
public class GenerateDutyCalendar {
    public static final int NUM_OF_SEPERATORS_CHAR = 135; // maximum number of duties per semester
    @SuppressWarnings("unused")
    private File selectedFile;
    private List<Teacher> teachers;
    private Calendar calendar;
    private List<String> warnings = new ArrayList<>();
    
    /**
     * Creates a new GenerateDutyCalendar instance.
     * Initializes empty lists for teachers and warnings.
     */
    public GenerateDutyCalendar() {
        teachers = new ArrayList<>();
        calendar = new Calendar();
    }
    
    /**
     * Processes a teacher schedule file and updates the teacher list.
     * @param file The CSV file containing teacher schedules
     */
    public void processFile(File file) {
        this.selectedFile = file;
        teachers = ReadTeachersFromDisk.readTeachersNames(file.getAbsolutePath());
        printSummary();
    }
    
    /**
     * Loads and processes an ICS calendar file.
     * Finds the start and end dates of the school year.
     * @param file The ICS file to process
     */
    public void loadCalendar(File file) {
        ReadCalendarFromDisk.loadCalendarFromICS(calendar, file);
        System.out.println("Calendar loaded with " + calendar.getEvents().size() + " events");
        
        // Set start date to September 3rd
        LocalDate startDate = LocalDate.of(2024, 9, 3);
        
        // Find the "Last Day of School" event
        LocalDate endDate = calendar.getEvents().stream()
            .filter(event -> event.getSummary().contains("Last Day of School"))
            .map(Holiday::getStartDate)
            .findFirst()
            .orElse(LocalDate.of(2025, 6, 28));
        
        System.out.println("School Year Start Date: " + startDate);
        System.out.println("Last Day of School: " + endDate);
        
        // Initialize the calendar days before printing
        calendar.initializeDaysOfYear();
        
        printSchoolDays(startDate, endDate);
    }
    
    /**
     * Prints a summary of the teacher schedule statuses.
     */
    private void printSummary() {
        System.out.println("\nDetailed Teacher Schedules:");
        System.out.println("=".repeat(NUM_OF_SEPERATORS_CHAR));
        
        // Print individual teacher schedules
        teachers.forEach(teacher -> {
            System.out.println(teacher.toString());
            System.out.println("-".repeat(NUM_OF_SEPERATORS_CHAR));
        });
        
        // Print summary statistics
        System.out.println("\nTeacher Summary Statistics:");
        System.out.println("=".repeat(NUM_OF_SEPERATORS_CHAR));
        System.out.println("Total Teachers: " + teachers.size());
        
        long fullTime = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.OVER_FULL_TIME)
            .peek(t -> {
                if (t.getName() != null && !t.getName().trim().isEmpty()) {
                    warnings.add(t.getName() + " has " + t.getFilledPeriods() + " classes (over full-time load)");
                }
            })
            .count() + 
            teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.FULL_TIME)
            .count();
        long fiveSixths = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.FIVE_SIXTHS)
            .count();
        long fourSixths = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.FOUR_SIXTHS)
            .count();
        long threeSixths = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.THREE_SIXTHS)
            .count();
        long twoSixths = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.TWO_SIXTHS)
            .count();
        long oneSixth = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.ONE_SIXTH)
            .count();
        long noLoad = teachers.stream()
            .filter(t -> t.getClassScheduleStatus() == TeacherScheduleStatusEnum.NO_LOAD)
            .count();
            
        System.out.println("Full Time (6/6): " + fullTime);
        System.out.println("Five Sixths (5/6): " + fiveSixths);
        System.out.println("Four Sixths (4/6): " + fourSixths);
        System.out.println("Three Sixths (3/6): " + threeSixths);
        System.out.println("Two Sixths (2/6): " + twoSixths);
        System.out.println("One Sixth (1/6): " + oneSixth);
        System.out.println("No Load (0/6): " + noLoad);
        
        // Add teacher type breakdown
        System.out.println("\nTeacher Type Breakdown:");
        System.out.println("-".repeat(NUM_OF_SEPERATORS_CHAR));
        
        long regularCount = teachers.stream()
            .filter(t -> t.getJobType() == TeacherTypeEnum.REGULAR)
            .count();
        long coopCount = teachers.stream()
            .filter(t -> t.getJobType() == TeacherTypeEnum.COOP)
            .count();
        long gymCount = teachers.stream()
            .filter(t -> t.getJobType() == TeacherTypeEnum.GYM)
            .count();
        long guidanceCount = teachers.stream()
            .filter(t -> t.getJobType() == TeacherTypeEnum.GUIDANCE)
            .count();
        long creditRecoveryCount = teachers.stream()
            .filter(t -> t.getJobType() == TeacherTypeEnum.CREDIT_RECOVERY)
            .count();
            
        System.out.println("Regular Teachers: " + regularCount);
        System.out.println("Co-op Teachers: " + coopCount);
        System.out.println("Gym Teachers: " + gymCount);
        System.out.println("Guidance: " + guidanceCount);
        System.out.println("Credit Recovery: " + creditRecoveryCount);
        
        // Print warnings only if they exist
        if (!warnings.isEmpty()) {
            System.out.println("\nWarnings:");
            System.out.println("-".repeat(NUM_OF_SEPERATORS_CHAR));
            warnings.forEach(System.out::println);
            warnings.clear();
        }
    }
    
    /**
     * Gets the list of teachers.
     * @return the list of teachers
     */
    public List<Teacher> getTeachers() {
        return teachers;
    }
    
    /**
     * Checks if a given date is a school day.
     * @param date the date to check
     * @return true if the date is a school day, false otherwise
     */
    public boolean isSchoolDay(LocalDate date) {
        return calendar.isSchoolDay(date);
    }
    
    /**
     * Prints the school days for a given date range.
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     */
    public void printSchoolDays(LocalDate startDate, LocalDate endDate) {
        calendar.printSchoolDays(startDate, endDate);
    }
    
    /**
     * Gets the calendar.
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }
}