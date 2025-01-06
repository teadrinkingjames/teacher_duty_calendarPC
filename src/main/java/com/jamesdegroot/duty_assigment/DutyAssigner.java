package com.jamesdegroot.duty_assigment;

import com.jamesdegroot.teacher.Teacher;
import com.jamesdegroot.calendar.Calendar;
import java.util.List;

public class DutyAssigner {
    @SuppressWarnings("unused")
    private final Calendar CALENDAR;
    @SuppressWarnings("unused")
    private final List<Teacher> TEACHERS;
    
    public DutyAssigner(Calendar calendar, List<Teacher> teachers) {
        this.CALENDAR = calendar;
        this.TEACHERS = teachers;
    }
    
    public void assignDuties() {
        // Implementation for assigning duties using DutyAssignmentRules
    }
} 