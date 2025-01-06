package com.jamesdegroot.calendar;

public class Duty {
    private String name;
    private String description;
    private String teacher;
    private String room;
    private String timeSlot;

     // Simple array of duty names - add or remove as needed
    public static final String[] DUTY_NAMES = {
        "Morning Hall", "Morning Parking", "Morning Cafeteria",
        "Lunch A Hall", "Lunch A Cafeteria", "Lunch A Library",
        "Lunch B Hall", "Lunch B Cafeteria", "Lunch B Library",
        "After School Hall", "After School Parking", "After School Bus"
    };

    public Duty(String name, String description, String teacher, String room, String time) {
        this.name = name;
        this.description = description;
        this.teacher = teacher;
        this.room = room;
        this.timeSlot = time;
    }   

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRoom() {
        return room;
    }

    public String getTimeSlot() {
        return timeSlot;
    }   

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }   

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }   

    public void setRoom(String room) {
        this.room = room;
    }   

    public void setTimeSlot(String time) {
        this.timeSlot = time;
    }   
}
