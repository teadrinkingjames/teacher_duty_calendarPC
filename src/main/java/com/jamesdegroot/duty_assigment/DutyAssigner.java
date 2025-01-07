package com.jamesdegroot.duty_assigment;

import com.jamesdegroot.teacher.Teacher;
import com.jamesdegroot.calendar.Calendar;
import com.jamesdegroot.calendar.Day;
import com.jamesdegroot.calendar.Duty;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class DutyAssigner {
    // Calendar and teacher references
    private final Calendar calendar;
    private final List<Teacher> teachers;
    private final DutyScheduleTemplate[] termTemplates;
    
    // Random number generator for teacher selection
    private final Random random = new Random();
    
    public DutyAssigner(Calendar calendar, List<Teacher> teachers) {
        this.calendar = calendar;
        this.teachers = teachers;
        this.termTemplates = new DutyScheduleTemplate[4];
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        for (int i = 0; i < 4; i++) {
            termTemplates[i] = new DutyScheduleTemplate(i);
        }
    }
    
    /**
     * Main method to assign duties for the entire semester
     */
    public void assignDuties() {
        List<Day> schoolDays = getSchoolDays();
        
        // Create templates for each term
        createTemplates(schoolDays);
        
        // Apply templates to all school days
        for (Day day : schoolDays) {
            applyTemplateToDay(day);
        }
    }
    
    /**
     * Creates duty schedule templates for each term
     */
    private void createTemplates(List<Day> schoolDays) {
        // Process one week of each term to create templates
        for (Day day : schoolDays) {
            int termNumber = getTermNumber(day.getDate());
            if (isFirstWeekOfTerm(day.getDate())) {
                // Create and assign duties for Day 1
                Day day1Template = new Day(day.getDate());
                assignDutiesForDay(day1Template, true);  // true for Day 1
                
                // Create and assign duties for Day 2
                Day day2Template = new Day(day.getDate());
                assignDutiesForDay(day2Template, false); // false for Day 2
                
                // Store both templates
                termTemplates[termNumber].setDayTemplate(
                    day.getDate().getDayOfWeek(),
                    new Day[]{day1Template, day2Template}
                );
            }
        }
    }
    
    /**
     * Applies the appropriate template to a given day
     */
    private void applyTemplateToDay(Day day) {
        int termNumber = getTermNumber(day.getDate());
        boolean isDay1 = DutyAssignmentRules.isDay1(day.getDate());
        
        // Create a new day with the same duties as the template
        Day templateDay = new Day(day.getDate());
        assignDutiesForDay(templateDay, isDay1);
        
        // Copy the duties from the template to the target day
        Duty[][] templateDuties = templateDay.getDutySchedule();
        for (int timeSlot = 0; timeSlot < templateDuties.length; timeSlot++) {
            for (int pos = 0; pos < templateDuties[timeSlot].length; pos++) {
                if (templateDuties[timeSlot][pos] != null) {
                    day.addDuty(timeSlot, pos, templateDuties[timeSlot][pos]);
                }
            }
        }
    }
    
    /**
     * Determines which term a date falls into
     */
    private int getTermNumber(LocalDate date) {
        if (date.getMonth().getValue() >= Month.SEPTEMBER.getValue() && 
            date.getMonth().getValue() <= Month.JANUARY.getValue()) {
            return date.getMonth().getValue() < Month.NOVEMBER.getValue() ? 
                DutyScheduleTemplate.TERM_1 : DutyScheduleTemplate.TERM_2;
        } else {
            return date.getMonth().getValue() < Month.APRIL.getValue() ? 
                DutyScheduleTemplate.TERM_3 : DutyScheduleTemplate.TERM_4;
        }
    }
    
    /**
     * Checks if a date is in the first week of its term
     */
    private boolean isFirstWeekOfTerm(LocalDate date) {
        int dayOfMonth = date.getDayOfMonth();
        Month month = date.getMonth();
        
        return (month == Month.SEPTEMBER && dayOfMonth <= 7) ||  // Term 1
               (month == Month.NOVEMBER && dayOfMonth <= 7) ||   // Term 2
               (month == Month.FEBRUARY && dayOfMonth <= 7) ||   // Term 3
               (month == Month.APRIL && dayOfMonth <= 7);        // Term 4
    }
    
    /**
     * Gets a list of all school days from the calendar
     */
    private List<Day> getSchoolDays() {
        return calendar.getDaysOfYear().stream()
            .filter(Day::isSchoolDay)
            .toList();
    }
    
    /**
     * Assigns duties for a specific day
     */
    private void assignDutiesForDay(Day day, boolean isDay1) {
        LocalDate date = day.getDate();
        Duty[][] dutySchedule = day.getDutySchedule();
        
        // For each time slot
        for (int timeSlot = 0; timeSlot < dutySchedule.length; timeSlot++) {
            // For each duty position in the time slot
            for (int position = 0; position < dutySchedule[timeSlot].length; position++) {
                Duty duty = dutySchedule[timeSlot][position];
                if (duty != null) {
                    assignTeacherToDuty(duty, timeSlot, date, isDay1);
                }
            }
        }
    }
    
    /**
     * Assigns a teacher to a specific duty
     */
    private void assignTeacherToDuty(Duty duty, int timeSlot, LocalDate date, boolean isDay1) {
        List<Teacher> availableTeachers = findAvailableTeachers(timeSlot, date, isDay1);
        
        if (!availableTeachers.isEmpty()) {
            // Get number of teachers needed for this duty (could be configurable per duty type)
            int teachersNeeded = 2;  // Default to 2 teachers per duty
            
            // Assign up to teachersNeeded teachers
            for (int i = 0; i < Math.min(teachersNeeded, availableTeachers.size()); i++) {
                // Randomly select a teacher from remaining available ones
                int selectedIndex = random.nextInt(availableTeachers.size());
                Teacher selectedTeacher = availableTeachers.get(selectedIndex);
                
                // Assign the teacher based on the day rotation
                if (isDay1) {
                    duty.addDay1Teacher(selectedTeacher.getName());
                } else {
                    duty.addDay2Teacher(selectedTeacher.getName());
                }
                
                // Increment the teacher's duty count
                selectedTeacher.incrementDutiesThisSemester();
                
                // Remove selected teacher from available list
                availableTeachers.remove(selectedIndex);
            }
        }
    }
    
    /**
     * Finds available teachers for a duty
     */
    private List<Teacher> findAvailableTeachers(int timeSlot, LocalDate date, boolean isDay1) {
        List<Teacher> availableTeachers = new ArrayList<>();
        
        for (Teacher teacher : teachers) {
            if (DutyAssignmentRules.canAssignDuty(teacher, timeSlot, date, isDay1)) {
                availableTeachers.add(teacher);
            }
        }
        
        return availableTeachers;
    }
} 