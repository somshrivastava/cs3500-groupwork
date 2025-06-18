package calendar.view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import calendar.controller.Features;

/**
 * A Swing-based GUI view for the calendar application.
 * This view provides a calendar display and schedule view without storing any model data.
 */
public class JFrameView extends JFrame implements ICalendarViewGUI {

  // UI Components
  private JPanel topPanel;
  private JButton prevButton;
  private JButton nextButton;
  private JPanel calendarPanel;
  private JLabel monthLabel;
  private JLabel calendarNameLabel;
  private JButton scheduleViewButton;
  private JPanel schedulePanel;
  private JScrollPane scheduleScrollPane;

  // Current display state (NOT model data - just what's currently shown)
  private YearMonth displayedMonth;
  private String displayedCalendarName;

  // Reference to features for callbacks
  private Features features;

  public JFrameView() {
    super("Calendar App");
    initializeComponents();
    layoutComponents();
    this.displayedMonth = YearMonth.now();
    this.displayedCalendarName = "Default";
    this.setVisible(true);
  }

  private void initializeComponents() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(800, 600);
    this.setLayout(new BorderLayout());

    // Top panel components
    topPanel = new JPanel(new FlowLayout());
    prevButton = new JButton("< Previous");
    nextButton = new JButton("Next >");
    monthLabel = new JLabel();
    calendarNameLabel = new JLabel();
    scheduleViewButton = new JButton("Schedule View");

    // Main content panels
    calendarPanel = new JPanel();
    schedulePanel = new JPanel();
    schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
    scheduleScrollPane = new JScrollPane(schedulePanel);
    scheduleScrollPane.setPreferredSize(new Dimension(300, 400));
  }

  private void layoutComponents() {
    // Top panel layout
    topPanel.add(prevButton);
    topPanel.add(monthLabel);
    topPanel.add(nextButton);
    topPanel.add(Box.createHorizontalStrut(20)); // spacing
    topPanel.add(calendarNameLabel);
    topPanel.add(Box.createHorizontalStrut(20)); // spacing
    topPanel.add(scheduleViewButton);

    this.add(topPanel, BorderLayout.NORTH);

    // Add instruction label
    JLabel instructionLabel = new JLabel("Left click: View events | Right click: Create event",
            SwingConstants.CENTER);
    instructionLabel.setFont(instructionLabel.getFont().deriveFont(Font.ITALIC, 11f));
    instructionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Main content area with calendar and schedule side by side
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(instructionLabel, BorderLayout.NORTH);
    mainPanel.add(calendarPanel, BorderLayout.CENTER);
    mainPanel.add(scheduleScrollPane, BorderLayout.EAST);

    this.add(mainPanel, BorderLayout.CENTER);
  }

  @Override
  public void addFeatures(Features features) {
    this.features = features;

    // Wire up button callbacks to features
    prevButton.addActionListener(e -> {
      if (features != null) {
        features.changeMonth(displayedMonth, -1);
      }
    });

    nextButton.addActionListener(e -> {
      if (features != null) {
        features.changeMonth(displayedMonth, 1);
      }
    });

    scheduleViewButton.addActionListener(e -> {
      if (features != null) {
        // Show schedule starting from first day of current month
        LocalDate startDate = displayedMonth.atDay(1);
        features.showScheduleView(startDate);
      }
    });

    // Keyboard shortcuts
    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        // Could add keyboard shortcuts here if needed
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (features != null) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
              features.changeMonth(displayedMonth, -1);
              break;
            case KeyEvent.VK_RIGHT:
              features.changeMonth(displayedMonth, 1);
              break;
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // Not needed for this implementation
      }
    });
  }

  @Override
  public void updateCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7)); // 7 columns for days of week

    // Add day labels
    String[] dayLabels = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String day : dayLabels) {
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      dayLabel.setBackground(Color.LIGHT_GRAY);
      dayLabel.setOpaque(true);
      calendarPanel.add(dayLabel);
    }

    // Add day buttons
    for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
      LocalDate date = displayedMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.setPreferredSize(new Dimension(80, 60));

      // Add mouse listener for both left and right clicks
      dayButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (features != null) {
            if (SwingUtilities.isRightMouseButton(e)) {
              // Right click - create event
              showCreateEventDialog(date);
            } else if (SwingUtilities.isLeftMouseButton(e)) {
              // Left click - view events
              features.viewEvents(date);
            }
          }
        }
      });

      calendarPanel.add(dayButton);
    }

    this.revalidate();
    this.repaint();
  }

  @Override
  public void displayScheduleView(String scheduleContent) {
    schedulePanel.removeAll();

    // Add title
    JLabel titleLabel = new JLabel("Schedule View");
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    schedulePanel.add(titleLabel);
    schedulePanel.add(Box.createVerticalStrut(10));

    // Display the schedule content in a text area
    JTextArea scheduleTextArea = new JTextArea(scheduleContent);
    scheduleTextArea.setEditable(false);
    scheduleTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    scheduleTextArea.setBackground(this.getBackground());

    JScrollPane textScrollPane = new JScrollPane(scheduleTextArea);
    textScrollPane.setBorder(null);
    textScrollPane.setPreferredSize(new Dimension(280, 350));

    schedulePanel.add(textScrollPane);

    this.revalidate();
    this.repaint();
  }

  @Override
  public void updateCurrentMonth(YearMonth month) {
    this.displayedMonth = month;
    monthLabel.setText(month.getMonth() + " " + month.getYear());
    updateCalendar();
  }

  @Override
  public void updateCurrentCalendar(String calendarName) {
    this.displayedCalendarName = calendarName;
    calendarNameLabel.setText("Calendar: " + calendarName);
  }

  @Override
  public String getInputString() {
    // Could be used for text input fields if needed
    return "";
  }

  @Override
  public void clearInputString() {
    // Could be used for text input fields if needed
  }

  @Override
  public void resetFocus() {
    this.setFocusable(true);
    this.requestFocus();
  }

  @Override
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Information",
            JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showError(String error) {
    JOptionPane.showMessageDialog(this, error, "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void display() {
    this.setVisible(true);
    this.requestFocus(); // Ensure keyboard events work
  }

  @Override
  public void showEvents(String eventList) {
    JOptionPane.showMessageDialog(this, eventList, "Events",
            JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows a simple dialog to create a new event.
   *
   * @param selectedDate the date that was clicked on the calendar
   */
  private void showCreateEventDialog(LocalDate selectedDate) {
    if (features == null) {
      return;
    }

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Event name
    panel.add(new JLabel("Event Name:"));
    JTextField eventNameField = new JTextField(20);
    panel.add(eventNameField);
    panel.add(Box.createVerticalStrut(10));

    // Start date and time
    panel.add(new JLabel("Start Date & Time:"));
    JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    // Start date spinner
    SpinnerDateModel startDateModel = new SpinnerDateModel();
    startDateModel.setValue(java.sql.Date.valueOf(selectedDate));
    JSpinner startDateSpinner = new JSpinner(startDateModel);
    JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
    startDateSpinner.setEditor(startDateEditor);

    // Start time spinner
    Calendar startTimeCal = Calendar.getInstance();
    startTimeCal.set(Calendar.HOUR_OF_DAY, 9);
    startTimeCal.set(Calendar.MINUTE, 0);
    startTimeCal.set(Calendar.SECOND, 0);
    startTimeCal.set(Calendar.MILLISECOND, 0);

    SpinnerDateModel startTimeModel = new SpinnerDateModel();
    startTimeModel.setValue(startTimeCal.getTime());
    JSpinner startTimeSpinner = new JSpinner(startTimeModel);
    JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
    startTimeSpinner.setEditor(startTimeEditor);

    startPanel.add(startDateSpinner);
    startPanel.add(startTimeSpinner);
    panel.add(startPanel);
    panel.add(Box.createVerticalStrut(10));

    // End date and time
    panel.add(new JLabel("End Date & Time:"));
    JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    // End date spinner
    SpinnerDateModel endDateModel = new SpinnerDateModel();
    endDateModel.setValue(java.sql.Date.valueOf(selectedDate));
    JSpinner endDateSpinner = new JSpinner(endDateModel);
    JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
    endDateSpinner.setEditor(endDateEditor);

    // End time spinner
    Calendar endTimeCal = Calendar.getInstance();
    endTimeCal.set(Calendar.HOUR_OF_DAY, 10);
    endTimeCal.set(Calendar.MINUTE, 0);
    endTimeCal.set(Calendar.SECOND, 0);
    endTimeCal.set(Calendar.MILLISECOND, 0);

    SpinnerDateModel endTimeModel = new SpinnerDateModel();
    endTimeModel.setValue(endTimeCal.getTime());
    JSpinner endTimeSpinner = new JSpinner(endTimeModel);
    JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
    endTimeSpinner.setEditor(endTimeEditor);

    endPanel.add(endDateSpinner);
    endPanel.add(endTimeSpinner);
    panel.add(endPanel);

    // Show dialog
    int result = JOptionPane.showConfirmDialog(this, panel, "Create New Event",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      try {
        String eventName = eventNameField.getText().trim();
        if (eventName.isEmpty()) {
          showError("Event name cannot be empty.");
          return;
        }

        // Get dates from spinners
        Date startDateValue = (Date) startDateSpinner.getValue();
        Date startTimeValue = (Date) startTimeSpinner.getValue();
        Date endDateValue = (Date) endDateSpinner.getValue();
        Date endTimeValue = (Date) endTimeSpinner.getValue();

        // Convert to LocalDateTime using Calendar
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDateValue);
        LocalDate startDate = LocalDate.of(startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH) + 1,
                startCal.get(Calendar.DAY_OF_MONTH));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDateValue);
        LocalDate endDate = LocalDate.of(endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH) + 1,
                endCal.get(Calendar.DAY_OF_MONTH));

        startTimeCal = Calendar.getInstance();
        startTimeCal.setTime(startTimeValue);
        LocalDateTime startDateTime = startDate.atTime(
                startTimeCal.get(Calendar.HOUR_OF_DAY),
                startTimeCal.get(Calendar.MINUTE));

        endTimeCal = Calendar.getInstance();
        endTimeCal.setTime(endTimeValue);
        LocalDateTime endDateTime = endDate.atTime(
                endTimeCal.get(Calendar.HOUR_OF_DAY),
                endTimeCal.get(Calendar.MINUTE));

        // Create the event
        features.createEvent(eventName, startDateTime, endDateTime);

      } catch (Exception ex) {
        showError("Error creating event: " + ex.getMessage());
      }
    }
  }
}
