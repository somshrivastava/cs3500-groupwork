package calendar.view;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractAction;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import calendar.controller.Features;
import calendar.model.IEvent;

/**
 * A Swing-based GUI view for the calendar application.
 * This view provides a calendar display and schedule view without storing any model data.
 * Note: This class exceeds 100 lines due to complex GUI layout requirements.
 */
public class JFrameView extends JFrame implements ICalendarViewGUI {

  // Constants for UI dimensions and styling
  private static final int WINDOW_WIDTH = 800;
  private static final int WINDOW_HEIGHT = 600;
  private static final int SCHEDULE_PANEL_WIDTH = 300;
  private static final int SCHEDULE_PANEL_HEIGHT = 400;
  private static final int DAY_BUTTON_WIDTH = 80;
  private static final int DAY_BUTTON_HEIGHT = 60;
  private static final int EVENT_LIST_DIALOG_WIDTH = 500;
  private static final int EVENT_LIST_DIALOG_HEIGHT = 400;
  private static final int SCHEDULE_TEXT_AREA_WIDTH = 280;
  private static final int SCHEDULE_TEXT_AREA_HEIGHT = 300;
  

  
  // Constants for spacing
  private static final int HORIZONTAL_STRUT_SIZE = 20;
  private static final int VERTICAL_STRUT_SIZE = 10;
  private static final int BORDER_SIZE = 10;
  
  // Constants for colors
  private static final Color EVENT_INDICATOR_COLOR = new Color(173, 216, 230);
  
  // Constants for strings
  private static final String WINDOW_TITLE = "Calendar App";
  private static final String[] DAY_LABELS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private static final String INSTRUCTION_TEXT = 
      "Left click: View/Edit events | Right click: Create event";

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
  
  // Keyboard support component
  private KeyboardComponent keyboardComponent;

  // Current display state
  private YearMonth displayedMonth;
  
  // Event data for display
  private Map<LocalDate, List<IEvent>> eventData;

  // Reference to features for callbacks
  private Features features;

  /**
   * Constructs a new JFrameView and initializes the GUI components.
   */
  public JFrameView() {
    super(WINDOW_TITLE);
    this.eventData = new HashMap<>();
    initializeComponents();
    layoutComponents();
    this.displayedMonth = YearMonth.now();
    this.setVisible(true);
  }

  private void initializeComponents() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
    scheduleScrollPane = createScrollPane(schedulePanel, SCHEDULE_PANEL_WIDTH, 
                                          SCHEDULE_PANEL_HEIGHT);
    
    // Keyboard support
    keyboardComponent = new KeyboardComponent();
  }

  private void layoutComponents() {
    layoutTopPanel();
    layoutMainPanel();
  }
  
  private void layoutTopPanel() {
    topPanel.add(prevButton);
    topPanel.add(monthLabel);
    topPanel.add(nextButton);
    topPanel.add(Box.createHorizontalStrut(HORIZONTAL_STRUT_SIZE));
    
    // Calendar management section
    topPanel.add(new JLabel("Calendar:"));
    topPanel.add(calendarNameLabel);
    topPanel.add(Box.createHorizontalStrut(HORIZONTAL_STRUT_SIZE));
    topPanel.add(scheduleViewButton);

    this.add(topPanel, BorderLayout.NORTH);
  }
  
  private void layoutMainPanel() {
    // Add instruction label
    JLabel instructionLabel = createInstructionLabel();

    // Main content area with calendar and schedule side by side
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(instructionLabel, BorderLayout.NORTH);
    mainPanel.add(calendarPanel, BorderLayout.CENTER);
    mainPanel.add(scheduleScrollPane, BorderLayout.EAST);
    
    // Add keyboard component for hotkey support
    mainPanel.add(keyboardComponent, BorderLayout.SOUTH);

    this.add(mainPanel, BorderLayout.CENTER);
  }
  
  private JLabel createInstructionLabel() {
    JLabel instructionLabel = new JLabel(INSTRUCTION_TEXT, SwingConstants.CENTER);
    instructionLabel.setFont(instructionLabel.getFont().deriveFont(Font.ITALIC, 11f));
    instructionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    return instructionLabel;
  }
  
  private JScrollPane createScrollPane(JComponent component, int width, int height) {
    JScrollPane scrollPane = new JScrollPane(component);
    scrollPane.setPreferredSize(new Dimension(width, height));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return scrollPane;
  }

  @Override
  public void addFeatures(Features features) {
    this.features = features;
    setupButtonListeners();
    keyboardComponent.addFeatures(features);
  }
  
  /**
   * Sets up keyboard shortcuts for the application.
   */
  public void setHotKey(KeyStroke key, String featureName) {
    keyboardComponent.setHotKey(key, featureName);
  }
  
  /**
   * Updates the event data that the view uses for display.
   */
  public void updateEventData(Map<LocalDate, List<IEvent>> eventData) {
    this.eventData = new HashMap<>(eventData);
    updateCalendar();
  }
  
  private void setupButtonListeners() {
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
        LocalDate startDate = displayedMonth.atDay(1);
        features.showScheduleView(startDate);
      }
    });
  }

  @Override
  public void updateCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7)); // 7 columns for days of week

    addDayLabels();
    addDayButtons();

    this.revalidate();
    this.repaint();
  }
  
  private void addDayLabels() {
    for (String day : DAY_LABELS) {
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      dayLabel.setBackground(Color.LIGHT_GRAY);
      dayLabel.setOpaque(true);
      calendarPanel.add(dayLabel);
    }
  }
  
  private void addDayButtons() {
    for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
      LocalDate date = displayedMonth.atDay(day);
      JButton dayButton = createDayButton(day, date);
      calendarPanel.add(dayButton);
    }
  }
  
  private JButton createDayButton(int day, LocalDate date) {
    JButton dayButton = new JButton(String.valueOf(day));
    dayButton.setPreferredSize(new Dimension(DAY_BUTTON_WIDTH, DAY_BUTTON_HEIGHT));
    
    setupDayButtonAppearance(dayButton, date);
    setupDayButtonListener(dayButton, date);
    
    return dayButton;
  }
  
  private void setupDayButtonAppearance(JButton dayButton, LocalDate date) {
    List<IEvent> eventsOnDate = eventData.getOrDefault(date, List.of());
    if (!eventsOnDate.isEmpty()) {
      dayButton.setBackground(EVENT_INDICATOR_COLOR);
      dayButton.setOpaque(true);
      
      String tooltipText = eventsOnDate.size() == 1 ? 
          "1 event" : eventsOnDate.size() + " events";
      dayButton.setToolTipText(tooltipText);
    } else {
      dayButton.setBackground(UIManager.getColor("Button.background"));
      dayButton.setToolTipText(null);
    }
  }
  
  private void setupDayButtonListener(JButton dayButton, LocalDate date) {
    dayButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (features != null) {
          if (SwingUtilities.isRightMouseButton(e)) {
            features.requestCreateEvent(date);
          } else if (SwingUtilities.isLeftMouseButton(e)) {
            features.requestViewEvents(date);
          }
        }
      }
    });
  }

  @Override
  public void displayScheduleView(String scheduleContent) {
    schedulePanel.removeAll();

    addScheduleTitle();
    addDateSelectionPanel();
    addScheduleContent(scheduleContent);

    this.revalidate();
    this.repaint();
  }
  
  private void addScheduleTitle() {
    JPanel titlePanel = new JPanel(new FlowLayout());
    JLabel titleLabel = new JLabel("Schedule View");
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
    titlePanel.add(titleLabel);
    schedulePanel.add(titlePanel);
  }
  
  private void addDateSelectionPanel() {
    JPanel datePanel = new JPanel(new FlowLayout());
    datePanel.add(new JLabel("Starting from:"));
    
    JSpinner dateSpinner = DialogUtils.createDateSpinner(displayedMonth.atDay(1));
    datePanel.add(dateSpinner);
    
    JButton updateButton = createUpdateButton(dateSpinner);
    datePanel.add(updateButton);
    
    schedulePanel.add(datePanel);
    schedulePanel.add(Box.createVerticalStrut(VERTICAL_STRUT_SIZE));
  }
  
  private JButton createUpdateButton(JSpinner dateSpinner) {
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(e -> {
      if (features != null) {
        try {
          Date selectedDate = (Date) dateSpinner.getValue();
          LocalDate startDate = DialogUtils.convertDateToLocalDate(selectedDate);
          features.showScheduleView(startDate);
        } catch (Exception ex) {
          showError("Invalid date selected.");
        }
      }
    });
    return updateButton;
  }
  
  private void addScheduleContent(String scheduleContent) {
    JTextArea scheduleTextArea = new JTextArea(scheduleContent);
    scheduleTextArea.setEditable(false);
    scheduleTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
    scheduleTextArea.setBackground(this.getBackground());
    scheduleTextArea.setLineWrap(true);
    scheduleTextArea.setWrapStyleWord(true);

    JScrollPane textScrollPane = createScrollPane(scheduleTextArea, 
        SCHEDULE_TEXT_AREA_WIDTH, SCHEDULE_TEXT_AREA_HEIGHT);
    textScrollPane.setBorder(BorderFactory.createTitledBorder("Next 10 Events"));

    schedulePanel.add(textScrollPane);
  }

  @Override
  public void updateCurrentMonth(YearMonth month) {
    this.displayedMonth = month;
    monthLabel.setText(month.getMonth() + " " + month.getYear());
    updateCalendar();
  }

  @Override
  public void updateCurrentCalendar(String calendarName) {
    calendarNameLabel.setText(calendarName);
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
    this.requestFocus();
  }

  @Override
  public void showEvents(String eventList) {
    JOptionPane.showMessageDialog(this, eventList, "Events",
            JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows a dialog to create a new event.
   * Called by controller, not directly by view event handlers.
   */
  public void showCreateEventDialog(LocalDate selectedDate) {
    CreateEventDialog dialog = new CreateEventDialog(this, selectedDate);
    dialog.setVisible(true);
    
    if (dialog.isConfirmed() && features != null) {
      // View makes a request to controller, which handles validation
      features.createEvent(dialog.getEventName(), dialog.getStartDateTime(), 
                           dialog.getEndDateTime());
    }
  }

  /**
   * Shows a dialog listing events for a specific date.
   * Called by controller, not directly by view event handlers.
   */
  public void showEventsForDate(LocalDate date, List<IEvent> events) {
    if (events.isEmpty()) {
      handleNoEventsFound(date);
      return;
    }
    
    showEventListDialog(date, events);
  }
  
  private void handleNoEventsFound(LocalDate date) {
    int result = JOptionPane.showConfirmDialog(this,
        "No events found for " + date + ".\nWould you like to create a new event?",
        "No Events",
        JOptionPane.YES_NO_OPTION);
    
    if (result == JOptionPane.YES_OPTION && features != null) {
      features.requestCreateEvent(date);
    }
  }
  
  private void showEventListDialog(LocalDate date, List<IEvent> events) {
    JDialog dialog = createDialog("Events for " + date);
    
    JPanel mainPanel = new JPanel(new BorderLayout());
    
    // Title
    JLabel titleLabel = new JLabel("Events for " + date, SwingConstants.CENTER);
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, 
                                                         BORDER_SIZE, BORDER_SIZE));
    mainPanel.add(titleLabel, BorderLayout.NORTH);
    
    // Event list
    JList<String> eventList = createEventList(events);
    JScrollPane listScrollPane = new JScrollPane(eventList);
    listScrollPane.setBorder(BorderFactory.createTitledBorder("Select an event to edit:"));
    mainPanel.add(listScrollPane, BorderLayout.CENTER);
    
    // Button panel
    JPanel buttonPanel = createEventListButtonPanel(dialog, eventList, events, date);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    dialog.add(mainPanel);
    dialog.setVisible(true);
  }
  
  private JList<String> createEventList(List<IEvent> events) {
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> eventList = new JList<>(listModel);
    eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    for (IEvent event : events) {
      String eventDisplay = String.format("%s (%s - %s)%s",
          event.getSubject(),
          event.getStartDateTime().toLocalTime(),
          event.getEndDateTime().toLocalTime(),
          event.getSeriesId() != null ? " [SERIES]" : "");
      listModel.addElement(eventDisplay);
    }
    
    return eventList;
  }
  
  private JPanel createEventListButtonPanel(JDialog dialog, JList<String> eventList, 
                                          List<IEvent> events, LocalDate date) {
    JPanel buttonPanel = new JPanel(new FlowLayout());
    
    JButton editButton = new JButton("Edit Selected");
    editButton.addActionListener(e -> {
      int selectedIndex = eventList.getSelectedIndex();
      if (selectedIndex >= 0) {
        IEvent selectedEvent = events.get(selectedIndex);
        dialog.dispose();
        if (features != null) {
          features.requestEditEvent(selectedEvent);
        }
      } else {
        JOptionPane.showMessageDialog(dialog, "Please select an event to edit.");
      }
    });
    
    JButton createButton = new JButton("Create New");
    createButton.addActionListener(e -> {
      dialog.dispose();
      if (features != null) {
        features.requestCreateEvent(date);
      }
    });
    
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dialog.dispose());
    
    buttonPanel.add(editButton);
    buttonPanel.add(createButton);
    buttonPanel.add(closeButton);
    
    return buttonPanel;
  }
  
  /**
   * Shows a dialog to edit the specified event.
   * Called by controller, not directly by view event handlers.
   */
  public void showEditEventDialog(IEvent event) {
    EditEventDialog dialog = new EditEventDialog(this, event);
    dialog.setVisible(true);
    
    if (dialog.isConfirmed() && features != null) {
      // View makes a request to controller, which handles validation
      features.editEvent(event.getSubject(), event.getStartDateTime(), 
                        event.getEndDateTime(), dialog.getProperty(), dialog.getNewValue());
    }
  }

  private JDialog createDialog(String title) {
    JDialog dialog = new JDialog(this, title, true);
    dialog.setSize(JFrameView.EVENT_LIST_DIALOG_WIDTH, JFrameView.EVENT_LIST_DIALOG_HEIGHT);
    dialog.setLocationRelativeTo(this);
    return dialog;
  }

  /**
   * Keyboard component that handles hotkey mappings following the lecture's 
   * InputMap/ActionMap pattern.
   * This encapsulates keyboard event handling and translates to high-level Features callbacks.
   */
  private class KeyboardComponent extends JPanel {
    private Features features;
    
    public KeyboardComponent() {
      setupDefaultKeyMappings();
      setFocusable(true);
    }
    
    public void addFeatures(Features features) {
      this.features = features;
    }
    
    public void setHotKey(KeyStroke key, String featureName) {
      this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, featureName);
    }
    
    private void setupDefaultKeyMappings() {
      this.getActionMap().put("previousMonth", new AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (features != null) {
            features.changeMonth(displayedMonth, -1);
          }
        }
      });
      
      this.getActionMap().put("nextMonth", new AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (features != null) {
            features.changeMonth(displayedMonth, 1);
          }
        }
      });
      
      this.getActionMap().put("showSchedule", new AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (features != null) {
            features.showScheduleView(displayedMonth.atDay(1));
          }
        }
      });
      
      this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
          KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "previousMonth");
      this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
          KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "nextMonth");
      this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
          KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "showSchedule");
    }
  }
}
