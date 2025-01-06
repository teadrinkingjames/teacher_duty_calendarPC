package com.jamesdegroot.gui;

import com.jamesdegroot.GenerateDutyCalendar;
import com.jamesdegroot.teacher.Teacher;
import com.jamesdegroot.teacher.TeacherTypeEnum;
import com.jamesdegroot.teacher.TeacherScheduleStatusEnum;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeacherProfileEditor extends JDialog {
    private Teacher teacher;
    private List<Teacher> allTeachers;
    private JComboBox<String> teacherSearch;
    private JPanel formPanel;
    
    public TeacherProfileEditor(JFrame parent, Teacher teacher, List<Teacher> allTeachers) {
        super(parent, "Teacher Profile Editor", true);
        this.teacher = teacher;
        this.allTeachers = allTeachers;
        
        setLayout(new BorderLayout(10, 10));
        
        // Create search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        searchPanel.add(new JLabel("Search Teacher:"));
        String[] teacherNames = allTeachers.stream()
            .map(Teacher::getName)
            .toArray(String[]::new);
        teacherSearch = new JComboBox<>(teacherNames);
        teacherSearch.setSelectedItem(teacher.getName());
        teacherSearch.setPreferredSize(new Dimension(200, 25));
        teacherSearch.addActionListener(e -> updateTeacher());
        searchPanel.add(teacherSearch);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Create form panel using BoxLayout for better height control
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateForm();
        
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void updateTeacher() {
        String selectedName = (String) teacherSearch.getSelectedItem();
        teacher = allTeachers.stream()
            .filter(t -> t.getName().equals(selectedName))
            .findFirst()
            .orElse(teacher);
        updateForm();
        System.out.println("\nTeacher Profile:");
        System.out.println(teacher.toString());
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
        
        // Name
        JTextField nameField = new JTextField(teacher.getName());
        formPanel.add(createRow("Name:", nameField));
        
        // Type
        JComboBox<TeacherTypeEnum> typeCombo = new JComboBox<>(TeacherTypeEnum.values());
        typeCombo.setSelectedItem(teacher.getJobType());
        formPanel.add(createRow("Type:", typeCombo));
        
        // Status
        JComboBox<TeacherScheduleStatusEnum> statusCombo = new JComboBox<>(TeacherScheduleStatusEnum.values());
        statusCombo.setSelectedItem(teacher.getClassScheduleStatus());
        formPanel.add(createRow("Status:", statusCombo));
        
        // Time Allocation
        JTextField allocationField = new JTextField(String.format("%.2f", teacher.getTimeAllocation()));
        formPanel.add(createRow("Time Allocation:", allocationField));
        
        // Duties This Semester
        JTextField dutiesField = new JTextField(String.valueOf(teacher.getDutiesThisSemester()));
        formPanel.add(createRow("Duties This Semester:", dutiesField));
        
        // Max Duties
        JTextField maxDutiesField = new JTextField(String.valueOf(teacher.getMaxDutiesPerSemester()));
        formPanel.add(createRow("Max Duties:", maxDutiesField));
        
        // Schedule
        JPanel schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < 10; i++) {
            JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel periodLabel = new JLabel("Period " + (i + 1) + ":");
            periodLabel.setPreferredSize(new Dimension(70, 20));
            periodPanel.add(periodLabel);
            
            JTextField scheduleField = new JTextField(teacher.getSchedule().get(i));
            scheduleField.setPreferredSize(new Dimension(250, 25));
            periodPanel.add(scheduleField);
            schedulePanel.add(periodPanel);
        }
        
        JPanel scheduleContainer = new JPanel(new BorderLayout());
        scheduleContainer.add(new JLabel("Schedule:"), BorderLayout.NORTH);
        JScrollPane scheduleScroll = new JScrollPane(schedulePanel);
        scheduleScroll.setPreferredSize(new Dimension(350, 200));
        scheduleContainer.add(scheduleScroll, BorderLayout.CENTER);
        formPanel.add(scheduleContainer);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        // Style buttons
        styleButton(saveButton);
        styleButton(cancelButton);
        
        // Set fixed button size
        Dimension buttonSize = new Dimension(80, 25);
        saveButton.setMaximumSize(buttonSize);
        saveButton.setPreferredSize(buttonSize);
        cancelButton.setMaximumSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        saveButton.addActionListener(e -> {
            // Save changes
            teacher.setJobType((TeacherTypeEnum) typeCombo.getSelectedItem());
            // Add other save logic here
            System.out.println("\nUpdated Teacher Summary:");
            System.out.println("=".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
            for (Teacher t : allTeachers) {
                System.out.println(t.toString());
                System.out.println("-".repeat(GenerateDutyCalendar.NUM_OF_SEPERATORS_CHAR));
            }
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        formPanel.revalidate();
        formPanel.repaint();
    }
    
    private JPanel createRow(String label, JComponent component) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(120, 20));
        row.add(jLabel);
        component.setPreferredSize(new Dimension(200, 25));
        row.add(component);
        return row;
    }
} 