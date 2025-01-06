package com.jamesdegroot.calendar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jamesdegroot.GenerateDutyCalendar;

/**
 * This class will define the object that will be used to store the calendar.
 * store the events and the teachers that are assigned to the events.   
 */ 
public class Calendar {
    private List<Holiday> events;
    private List<Day> daysOfYear = new ArrayList<>(365); // CAN CHANGE THIS TO 194 *POSSIBLY* 
                                                                         // (MANDATED AMOUNT OF SCHOOL DAYS)

   

    public Calendar() {
        this.events = new ArrayList<>();
    }

    /**
     * Loads the calendar from an ICS file.
     * @param filename the name of the ICS file to load
     * @throws IOException if there is an error reading the ICS file    
     */
    public void loadFromICS(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Holiday currentEvent = null;
            String summary = null;
            LocalDate startDate = null;
            LocalDate endDate = null;
            String description = "";
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    summary = null;
                    startDate = null;
                    endDate = null;
                    description = "";
                } else if (line.startsWith("SUMMARY:")) {
                    summary = line.substring(8);
                } else if (line.startsWith("DTSTART;VALUE=DATE:")) {
                    startDate = parseDate(line.substring(17));
                } else if (line.startsWith("DTEND;VALUE=DATE:")) {
                    endDate = parseDate(line.substring(15));
                } else if (line.startsWith("DESCRIPTION:")) {
                    description = line.substring(12);
                } else if (line.startsWith("END:VEVENT") && summary != null && startDate != null && endDate != null) {
                    currentEvent = new Holiday(summary, startDate, endDate, description);
                    events.add(currentEvent);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading ICS file: " + e.getMessage());
        }
    }

    /**
     * Parses a date string into a LocalDate object.
     * @param dateStr the date string to parse
     * @return the parsed LocalDate object
     */
    private LocalDate parseDate(String dateStr) {
        // Clean up the date string by removing any non-digit characters
        dateStr = dateStr.replaceAll("[^0-9]", "");
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + dateStr);
            return null;
        }
    }

    /**
     * Checks if a given date is a school day.
     * @param date the date to check
     * @return true if the date is a school day, false otherwise
     */
    public boolean isSchoolDay(LocalDate date) {
        return daysOfYear.stream()
            .filter(d -> d.getDate().equals(date))
            .findFirst()
            .map(Day::isSchoolDay)
            .orElse(false);
    }

    /**
     * Returns the list of events.
     * @return the list of events
     */
    public List<Holiday> getEvents() {
        return events;
    }

    /**
     * Prints the school days between two dates.
     * @param startDate the start date
     * @param endDate the end date
     */
    public void printSchoolDays(LocalDate startDate, LocalDate endDate) {
        String format = "%-12s | %-25s | %-30s | %-20s\n";
        System.out.println("\nDuty Schedule:");
        System.out.println("=".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            final LocalDate date = current;
            Optional<Day> dayOpt = daysOfYear.stream()
                .filter(d -> d.getDate().equals(date))
                .findFirst();
                
            if (dayOpt.isPresent()) {
                Day day = dayOpt.get();
                if (day.isSchoolDay()) {
                    System.out.println("\n" + day.getDate().format(
                        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
                    System.out.println("-".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
                    System.out.printf(format, "Time Slot", "Duty", "Location", "Teacher");
                    System.out.println("-".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
                    
                    Duty[][] duties = day.getDutySchedule();
                    for (int timeSlot = 0; timeSlot < duties.length; timeSlot++) {
                        for (int pos = 0; pos < duties[timeSlot].length; pos++) {
                            Duty duty = duties[timeSlot][pos];
                            if (duty != null) {
                                System.out.printf(format,
                                    String.format("Slot %d", timeSlot + 1),
                                    duty.getName(),
                                    duty.getRoom(),
                                    duty.getTeacher() != null ? duty.getTeacher() : "UNASSIGNED"
                                );
                            }
                        }
                    }
                }
            }
            current = current.plusDays(1);
        }
    }

    /**
     * Initializes the days of the year for 2024
     */
    public void initializeDaysOfYear() {
        LocalDate startDate = LocalDate.of(2024, 9, 3);
        LocalDate endDate = LocalDate.of(2025, 6, 28);
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Day day = new Day(currentDate);
            
            // Set school day status based on weekends and holidays
            boolean isSchoolDay = true;
            if (currentDate.getDayOfWeek().getValue() >= 6) {
                isSchoolDay = false;
            }
            for (Holiday event : events) {
                if (currentDate.equals(event.getStartDate()) || 
                    currentDate.equals(event.getEndDate()) || 
                    (currentDate.isAfter(event.getStartDate()) && 
                     currentDate.isBefore(event.getEndDate()))) {
                    isSchoolDay = false;
                    break;
                }
            }
            day.setSchoolDay(isSchoolDay);
            
            // Initialize duties if it's a school day
            if (isSchoolDay) {
                for (int timeSlot = 0; timeSlot < Day.TIME_SLOTS; timeSlot++) {
                    for (int position = 0; position < Day.DUTIES_PER_SLOT; position++) {
                        int dutyIndex = (timeSlot * Day.DUTIES_PER_SLOT) + position;
                        String dutyName = dutyIndex < Duty.DUTY_NAMES.length ? Duty.DUTY_NAMES[dutyIndex] : "Duty " + (dutyIndex + 1);
                        Duty duty = new Duty(
                            dutyName,
                            "",  // No description needed
                            null,
                            "Various",
                            String.format("Time Slot %d", timeSlot + 1)
                        );
                        day.addDuty(timeSlot, position, duty);
                    }
                }
            }
            
            daysOfYear.add(day);
            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Gets the list of days in the year
     * @return List of Day objects
     */
    public List<Day> getDaysOfYear() {
        return daysOfYear;
    }

    /**
     * Adds a holiday to the calendar.
     * @param event the holiday to add
     */
    public void addHoliday(Holiday event) {
        if (event != null) {
            events.add(event);
        }
    }
}
