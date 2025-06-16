package calendar.view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.controller.Features;

public class JFrameView extends JFrame implements ICalendarViewGUI {

  private JPanel topPanel = new JPanel();
  private JButton prevButton = new JButton("<");
  private JButton nextButton = new JButton(">");
  private JPanel calendarPanel;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;
  private Map<String, Color> calendars;
  private Map<LocalDate, List<String>> events;
  private YearMonth currentMonth;
  private String selectedCalendar;

  public JFrameView() {
    super("Calendar App");

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(500, 500);
    this.setLayout(new BorderLayout());

    // start with a default calendar
    currentMonth = YearMonth.now();
    calendars = new HashMap<>();
    events = new HashMap<>();
    // allow for access and creation of multiple calendars
    calendars.put("Work", Color.BLUE);
    calendars.put("Personal", Color.GREEN);
    calendars.put("Holidays", Color.RED);
    selectedCalendar = "Personal";

    monthLabel = new JLabel();
    calendarDropdown = new JComboBox<>(calendars.keySet().toArray(new String[0]));
    topPanel.add(prevButton);
    topPanel.add(monthLabel);
    topPanel.add(nextButton);
    topPanel.add(calendarDropdown);

    this.add(topPanel, BorderLayout.NORTH);

    calendarPanel = new JPanel();
    this.add(calendarPanel, BorderLayout.CENTER);

    prevButton.setActionCommand("<");
    nextButton.setActionCommand(">");
    calendarDropdown.setActionCommand("dropdown");
//    prevButton.addActionListener(e -> changeMonth(-1));
//    nextButton.addActionListener(e -> changeMonth(1));
//    calendarDropdown.addActionListener(e -> changeCalendar());

    updateCalendar();
    this.setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    prevButton.addActionListener(e -> features.changeMonth(currentMonth, -1));
    nextButton.addActionListener(e -> features.changeMonth(currentMonth, 1));
    calendarDropdown.addActionListener(e -> features.changeCalendar(calendarDropdown.getSelectedItem()));

//    echoButton.addActionListener(evt -> features.echoOutput(input.getText()));
//    toggleButton.addActionListener(evt -> features.toggleColor());
//    exitButton.addActionListener(evt -> features.exitProgram());

    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 't') {
          //features.toggleColor();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
          //features.makeUppercase();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
          //features.restoreLowercase();
        }
      }
    });
  }


  @Override
  public String getInputString() {
    return "";
  }

  @Override
  public void clearInputString() {

  }

  /*
      In order to make this frame respond to keyboard events, it must be within strong focus.
      Since there could be multiple components on the screen that listen to keyboard events,
      we must set one as the "currently focussed" one so that all keyboard events are
      passed to that component. This component is said to have "strong focus".

      We do this by first making the component focusable and then requesting focus to it.
      Requesting focus makes the component have focus AND removes focus from whoever had it
      before.
    */
  @Override
  public void resetFocus() {
    this.setFocusable(true);
    this.requestFocus();
  }

  public void updateCalendar() {
    // where to call update calendar once a change is made?
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7));
    monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
    calendarPanel.setBackground(calendars.get(selectedCalendar));

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.addActionListener(e -> showEvents(date));
      calendarPanel.add(dayButton);
    }

    this.revalidate();
    this.repaint();
  }

  private void showEvents(LocalDate date) {
    List<String> dayEvents = events.getOrDefault(date, new ArrayList<>());
    String eventList = dayEvents.isEmpty() ? "No events" : String.join("\n", dayEvents);
    String newEvent = JOptionPane.showInputDialog(this, "Events on " + date + ":\n" + eventList + "\n\nAdd new event:");
    if (newEvent != null && !newEvent.trim().isEmpty()) {
      dayEvents.add(newEvent);
      events.put(date, dayEvents);
    }
  }
}
