package com.jamesdegroot.gui;

import com.jamesdegroot.GenerateDutyCalendar;
import com.jamesdegroot.calendar.Day;
import com.jamesdegroot.calendar.Duty;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DayEditor extends JDialog {
    private Day day;
    private List<Day> allDays;
    private JComboBox<String> daySearch;
    private JPanel formPanel;
    
    public DayEditor(JFrame parent, Day day, List<Day> allDays) {
        super(parent, "Day Schedule Editor", true);
        this.day = day;
        this.allDays = allDays;
        
        setLayout(new BorderLayout(10, 10));
        
        // Create search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        searchPanel.add(new JLabel("Search Date:"));
        String[] dates = allDays.stream()
            .map(d -> d.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")))
            .toArray(String[]::new);
        daySearch = new JComboBox<>(dates);
        daySearch.setSelectedItem(day.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        daySearch.setPreferredSize(new Dimension(250, 25));
        daySearch.addActionListener(e -> updateDay());
        searchPanel.add(daySearch);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Create form panel
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateForm();
        
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void updateDay() {
        String selectedDate = (String) daySearch.getSelectedItem();
        LocalDate date = LocalDate.parse(selectedDate, 
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        day = allDays.stream()
            .filter(d -> d.getDate().equals(date))
            .findFirst()
            .orElse(day);
        updateForm();
        System.out.println("\nDay Schedule:");
        System.out.println("=".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
        System.out.println(day.toString());
        pack();
    }
    
    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
    }
    
    private void updateForm() {
        formPanel.removeAll();
        
        // School Day Checkbox
        JCheckBox schoolDayCheck = new JCheckBox("School Day", day.isSchoolDay());
        schoolDayCheck.addActionListener(e -> day.setSchoolDay(schoolDayCheck.isSelected()));
        formPanel.add(createRow("Day Type:", schoolDayCheck));
        
        // Duties
        JPanel dutiesPanel = new JPanel();
        dutiesPanel.setLayout(new BoxLayout(dutiesPanel, BoxLayout.Y_AXIS));
        dutiesPanel.setBorder(BorderFactory.createTitledBorder("Duties"));
        
        Duty[][] duties = day.getDutySchedule();
        for (int timeSlot = 0; timeSlot < Day.TIME_SLOTS; timeSlot++) {
            for (int position = 0; position < Day.DUTIES_PER_SLOT; position++) {
                Duty duty = duties[timeSlot][position];
                if (duty != null) {
                    JPanel dutyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
                    
                    // Duty Name
                    JTextField nameField = new JTextField(duty.getName(), 20);
                    dutyPanel.add(createLabeledField("Name:", nameField));
                    
                    // Room
                    JTextField roomField = new JTextField(duty.getRoom(), 10);
                    dutyPanel.add(createLabeledField("Room:", roomField));
                    
                    // Teacher
                    JTextField teacherField = new JTextField(
                        duty.getTeacher() != null ? duty.getTeacher() : "", 20);
                    dutyPanel.add(createLabeledField("Teacher:", teacherField));
                    
                    dutiesPanel.add(dutyPanel);
                }
            }
        }
        
        formPanel.add(dutiesPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        // Style buttons
        styleButton(saveButton);
        styleButton(cancelButton);
        
        Dimension buttonSize = new Dimension(80, 25);
        saveButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        saveButton.addActionListener(e -> {
            // Save changes
            System.out.println("\nUpdated Day Schedule:");
            System.out.println("=".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
            System.out.println("Date: " + day.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            System.out.println("School Day: " + (day.isSchoolDay() ? "Yes" : "No"));
            if (day.isSchoolDay()) {
                System.out.println("\nDuty Schedule:");
                System.out.println("-".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
                System.out.printf("%-12s | %-25s | %-30s | %-20s%n", "Time Slot", "Duty", "Location", "Teacher");
                System.out.println("-".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
                Duty[][] dutySchedule = day.getDutySchedule();
                for (int timeSlot = 0; timeSlot < Day.TIME_SLOTS; timeSlot++) {
                    for (int position = 0; position < Day.DUTIES_PER_SLOT; position++) {
                        Duty duty = dutySchedule[timeSlot][position];
                        if (duty != null) {
                            System.out.printf("%-12s | %-25s | %-30s | %-20s%n",
                                "Slot " + (timeSlot + 1),
                                duty.getName(),
                                duty.getRoom(),
                                duty.getTeacher() != null ? duty.getTeacher() : "UNASSIGNED");
                        }
                    }
                }
            }
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel);
        
        formPanel.revalidate();
        formPanel.repaint();
    }
    
    private JPanel createRow(String label, JComponent component) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(120, 20));
        row.add(jLabel);
        row.add(component);
        return row;
    }
    
    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.add(new JLabel(label));
        panel.add(field);
        return panel;
    }
} 