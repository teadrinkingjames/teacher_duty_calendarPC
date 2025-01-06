package com.jamesdegroot.gui;

import com.jamesdegroot.GenerateDutyCalendar;
import com.jamesdegroot.calendar.Day;
import com.jamesdegroot.teacher.Teacher;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * This class contains the main window for the application.
 * 
 */
public class AppWindow {
    private JFrame frame;
    private JPanel mainPanel;
    private JFileChooser fileChooser;
    private JTextField inputField;
    private JTextArea consoleOutput;
    private String userInput;
    private GenerateDutyCalendar appLogic;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private FileNameExtensionFilter csvFilter;
    private FileNameExtensionFilter icsFilter;

    /**
     * Constructor for the AppWindow class.
     * @param appLogic the logic for the application
     */
    public AppWindow(GenerateDutyCalendar appLogic) {
        this.appLogic = appLogic;
        initialize();
        redirectSystemOut();
    }

    /**
     * Initializes the main window for the application.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Teacher Duty Scheduler");
        frame.setResizable(true);
        frame.setUndecorated(false);
        frame.setBounds(100, 100, 1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize file chooser with filters
        fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(1000, 800));
        String downloadsFolderPath = System.getProperty("user.home") + File.separator + "Downloads";
        fileChooser.setCurrentDirectory(new File(downloadsFolderPath));
        
        // Create file filters
        FileNameExtensionFilter csvFilter = 
            new FileNameExtensionFilter(
                "CSV Files (*.csv)", "csv");
        
        FileNameExtensionFilter icsFilter = 
            new FileNameExtensionFilter(
                "Calendar Files (*.ics)", "ics");
        
        // Store filters for later use
        this.csvFilter = csvFilter;
        this.icsFilter = icsFilter;
        
        mainPanel = new JPanel(new BorderLayout(5, 5));
        frame.setContentPane(mainPanel);
        
        // Create card layout panel for console
        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
        
        // Create console panel with padding
        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        
        // Create padded panel for console
        JPanel paddedConsolePanel = new JPanel(new BorderLayout());
        paddedConsolePanel.add(scrollPane);
        paddedConsolePanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        scrollPane.setPreferredSize(new Dimension(800, 600));
        
        // Add console and empty panel to card layout
        cardPanel.add(paddedConsolePanel, "console");
        cardPanel.add(new JPanel(), "empty");
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        addInputField();
    }

    /**
     * Redirects the system output to the console output.
     */
    private void redirectSystemOut() {
        PrintStream printStream = new PrintStream(new CustomOutputStream(consoleOutput));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    /**
     * Shows the main window.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Adds the input field to the main window.
     */
    private void addInputField() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1, 5, 5));
        
        // First row - Teacher Schedule File
        JPanel teacherPanel = new JPanel();
        teacherPanel.setLayout(new BorderLayout(5, 0));
        teacherPanel.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));
        
        String teacherBarText = "Click Browse and enter path to teacher schedule file";
        inputField = new JTextField(40) {
            @Override
            public boolean isFocusable() {
                return false;
            }
        };
        inputField.setText(teacherBarText);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setForeground(Color.BLACK);
        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                inputField.setFocusable(true);
                inputField.requestFocusInWindow();
            }
        });
        
        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (inputField.getText().equals(teacherBarText)) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText(teacherBarText);
                    inputField.setForeground(Color.GRAY);
                }
                inputField.setFocusable(false);
            }
        });
        
        JButton teacherBrowseButton = new JButton("Browse");
        styleButton(teacherBrowseButton);
        
        teacherPanel.add(inputField, BorderLayout.CENTER);
        teacherPanel.add(teacherBrowseButton, BorderLayout.EAST);
        
        // Second row - Calendar File
        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout(5, 0));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));
        
        String calendarBarText = "Click Browse and enter path to calendar file";
        JTextField calendarField = new JTextField(35) {
            @Override
            public boolean isFocusable() {
                return false;
            }
        };
        calendarField.setText(calendarBarText);
        calendarField.setFont(new Font("Arial", Font.PLAIN, 14));
        calendarField.setForeground(Color.BLACK);
        calendarField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                calendarField.setFocusable(true);
                calendarField.requestFocusInWindow();
            }
        });
        
        calendarField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (calendarField.getText().equals(calendarBarText)) {
                    calendarField.setText("");
                    calendarField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (calendarField.getText().isEmpty()) {
                    calendarField.setText(calendarBarText);
                    calendarField.setForeground(Color.GRAY);
                }
                calendarField.setFocusable(false);
            }
        });
        
        JButton calendarBrowseButton = new JButton("Browse");
        styleButton(calendarBrowseButton);
        
        calendarPanel.add(calendarField, BorderLayout.CENTER);
        calendarPanel.add(calendarBrowseButton, BorderLayout.EAST);
        
        // Add toggle button panel
        JPanel togglePanel = new JPanel(new BorderLayout(5, 0));
        togglePanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        
        JButton helpButton = new JButton("Help");
        JButton toggleConsoleButton = new JButton("Hide Console");
        styleButton(helpButton);
        styleButton(toggleConsoleButton);
        
        // Create help button action
        helpButton.addActionListener(e -> showHelpDialog());
        
        // Add toggle console action
        toggleConsoleButton.addActionListener(e -> {
            boolean isVisible = toggleConsoleButton.getText().equals("Hide Console");
            cardLayout.show(cardPanel, isVisible ? "empty" : "console");
            toggleConsoleButton.setText(isVisible ? "Show Console" : "Hide Console");
            frame.revalidate();
        });
        
        // Add buttons to toggle panel
        JPanel leftButtonPanel = new JPanel(new BorderLayout(5, 0));
        leftButtonPanel.add(helpButton, BorderLayout.WEST);
        
        JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton editTeacherButton = new JButton("Edit Teacher");
        styleButton(editTeacherButton);
        editTeacherButton.addActionListener(e -> showTeacherEditor());
        JButton editDayButton = new JButton("Edit Day");
        styleButton(editDayButton);
        editDayButton.addActionListener(e -> showDayEditor());
        centerButtonPanel.add(editTeacherButton);
        centerButtonPanel.add(editDayButton);
        leftButtonPanel.add(centerButtonPanel, BorderLayout.CENTER);
        
        JPanel rightButtonPanel = new JPanel(new BorderLayout());
        rightButtonPanel.add(toggleConsoleButton, BorderLayout.EAST);
        
        togglePanel.add(leftButtonPanel, BorderLayout.WEST);
        togglePanel.add(rightButtonPanel, BorderLayout.EAST);
        
        // Add action listeners
        teacherBrowseButton.addActionListener(e -> {
            fileChooser.resetChoosableFileFilters();
            fileChooser.setFileFilter(csvFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().toLowerCase().endsWith(".csv")) {
                    inputField.setText(selectedFile.getAbsolutePath());
                    userInput = selectedFile.getAbsolutePath();
                    appLogic.processFile(selectedFile);
                    System.out.println("Selected teacher file: " + userInput);
                } else {
                    System.err.println("Error: Please select a CSV file for teacher schedules");
                }
            }
        });
        
        calendarBrowseButton.addActionListener(e -> {
            fileChooser.resetChoosableFileFilters();
            fileChooser.setFileFilter(icsFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().toLowerCase().endsWith(".ics")) {
                    calendarField.setText(selectedFile.getAbsolutePath());
                    appLogic.loadCalendar(selectedFile);
                    System.out.println("Selected calendar file: " + selectedFile.getAbsolutePath());
                } else {
                    System.err.println("Error: Please select an ICS file for calendar");
                }
            }
        });
        
        // Add all panels
        inputPanel.add(teacherPanel);
        inputPanel.add(calendarPanel);
        inputPanel.add(togglePanel);
        
        // Add padding around the entire input section
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.add(inputPanel, BorderLayout.NORTH);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        mainPanel.add(paddedPanel, BorderLayout.NORTH);
        
        // Move focus to frame initially
        frame.requestFocusInWindow();
    }

    /**
     * Shows the help dialog with application instructions.
     */
    private void showHelpDialog() {
        JDialog helpDialog = new JDialog(frame, "Application Help", true);
        helpDialog.setLayout(new BorderLayout(10, 10));
        
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setFont(new Font("Dialog", Font.PLAIN, 14));
        helpText.setText(
            "Teacher Duty Scheduler - Help Guide\n\n" +
            "1. Loading Teacher Schedule:\n" +
            "   - Click 'Browse' next to 'Teacher Schedule' to select your CSV file\n" +
            "   - The file should contain teacher names and their teaching schedules\n\n" +
            "2. Loading Calendar:\n" +
            "   - Click 'Browse' next to 'Calendar File' to select your ICS calendar file\n" +
            "   - The calendar should contain school events, holidays, and PA days\n\n" +
            "3. Viewing Output:\n" +
            "   - Use the 'Show/Hide Console' button to toggle the console view\n" +
            "   - The console displays teacher schedules and calendar information\n\n" +
            "4. Understanding Results:\n" +
            "   - Teacher schedules show their teaching load and type\n" +
            "   - Calendar view shows school days and non-school days\n" +
            "   - Warnings will appear for teachers with unusual schedules"
        );
        
        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> helpDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        
        helpDialog.add(scrollPane, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        helpDialog.pack();
        helpDialog.setLocationRelativeTo(frame);
        helpDialog.setVisible(true);
    }

    /**
     * Shows the teacher profile editor dialog.
     */
    private void showTeacherEditor() {
        if (appLogic.getTeachers() == null || appLogic.getTeachers().isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please load a teacher schedule file first.", 
                "No Teachers Loaded", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Teacher firstTeacher = appLogic.getTeachers().get(0);
        TeacherProfileEditor editor = new TeacherProfileEditor(frame, firstTeacher, appLogic.getTeachers());
        editor.setVisible(true);
    }

    /**
     * Shows the day editor dialog.
     */
    private void showDayEditor() {
        if (appLogic.getCalendar() == null || appLogic.getCalendar().getDaysOfYear().isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please load a calendar file first.", 
                "No Calendar Loaded", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Day> days = appLogic.getCalendar().getDaysOfYear();
        DayEditor editor = new DayEditor(frame, days.get(0), days);
        editor.setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
    }
}

/**
 * Custom output stream for redirecting system output to the console output.
 */
class CustomOutputStream extends OutputStream {
    private JTextArea textArea;
    /**
     * Constructor for the CustomOutputStream class.
     * @param textArea the text area to write to
     */
    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }
    
    /**
     * Writes a byte to the text area.
     * @param b the byte to write
     */
    @Override
    public void write(int b) {
        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
