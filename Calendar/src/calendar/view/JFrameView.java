package calendar.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import calendar.controller.Features;
import calendar.model.IEvent;

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

    // Main content area with calendar and schedule side by side
    JPanel mainPanel = new JPanel(new BorderLayout());
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
      
      // When a day is clicked, show events for that date
      dayButton.addActionListener(e -> {
        if (features != null) {
          features.viewEvents(date);
        }
      });
      
      calendarPanel.add(dayButton);
    }

    this.revalidate();
    this.repaint();
  }

  @Override
  public void displayScheduleView(LocalDate startDate, List<IEvent> events) {
    schedulePanel.removeAll();
    
    // Add title
    JLabel titleLabel = new JLabel("Schedule from " + startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    schedulePanel.add(titleLabel);
    schedulePanel.add(Box.createVerticalStrut(10));

    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events to display");
      noEventsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      schedulePanel.add(noEventsLabel);
    } else {
      // Display up to 10 events
      int eventsToShow = Math.min(events.size(), 10);
      for (int i = 0; i < eventsToShow; i++) {
        IEvent event = events.get(i);
        JPanel eventPanel = createEventPanel(event);
        schedulePanel.add(eventPanel);
        schedulePanel.add(Box.createVerticalStrut(5));
      }
      
      if (events.size() > 10) {
        JLabel moreLabel = new JLabel("... and " + (events.size() - 10) + " more events");
        moreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        moreLabel.setFont(moreLabel.getFont().deriveFont(Font.ITALIC));
        schedulePanel.add(moreLabel);
      }
    }

    schedulePanel.revalidate();
    schedulePanel.repaint();
  }

  private JPanel createEventPanel(IEvent event) {
    JPanel eventPanel = new JPanel();
    eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
    eventPanel.setBorder(BorderFactory.createEtchedBorder());
    eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, eventPanel.getPreferredSize().height));

    JLabel subjectLabel = new JLabel(event.getSubject());
    subjectLabel.setFont(subjectLabel.getFont().deriveFont(Font.BOLD));
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    JLabel timeLabel = new JLabel(event.getStartDateTime().format(formatter) + 
                                 " - " + event.getEndDateTime().format(formatter));
    
    eventPanel.add(subjectLabel);
    eventPanel.add(timeLabel);
    
    return eventPanel;
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
    JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showError(String error) {
    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void display() {
    this.setVisible(true);
  }
}
