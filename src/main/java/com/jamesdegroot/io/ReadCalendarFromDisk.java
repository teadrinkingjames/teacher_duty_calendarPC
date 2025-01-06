package com.jamesdegroot.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.jamesdegroot.calendar.Calendar;
import com.jamesdegroot.calendar.Holiday;

public class ReadCalendarFromDisk {
    
    /**
     * Loads calendar events from an ICS file into a Calendar object.
     * @param calendar The Calendar object to populate
     * @param file The ICS file to read
     * @throws IOException if file reading fails (caught internally)
     */
    public static void loadCalendarFromICS(Calendar calendar, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Holiday currentHoliday = null;
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
                    currentHoliday = new Holiday(summary, startDate, endDate, description);
                    calendar.addHoliday(currentHoliday);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading ICS file: " + e.getMessage());
        }
    }

    /**
     * Parses a date string from ICS format to LocalDate.
     * @param dateStr The date string in yyyyMMdd format
     * @return LocalDate object, or null if parsing fails
     * @throws Exception if parsing fails
     */
    private static LocalDate parseDate(String dateStr) {
        dateStr = dateStr.replaceAll("[^0-9]", "");
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + dateStr);
            return null;
        }
    }
}
