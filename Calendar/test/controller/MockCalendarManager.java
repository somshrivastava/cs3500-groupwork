package controller;

import calendar.model.ICalendarManager;
import calendar.model.ISmartCalendarModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * Mock implementation of ICalendarManager for testing purposes.
 * Logs all method calls for verification in tests.
 */
public class MockCalendarManager implements ICalendarManager {
  private final StringBuilder log;
  private ISmartCalendarModel currentCalendar;

  public MockCalendarManager(StringBuilder log) {
    this.log = Objects.requireNonNull(log);
    this.currentCalendar = null;
  }

  @Override
  public ISmartCalendarModel getCurrentCalendar() {
    return currentCalendar;
  }

  @Override
  public void createCalendar(String calendarName, ZoneId timezone) {
    String s = "Created calendar " + calendarName + " with timezone " + timezone;
    log.append(s);
  }

  @Override
  public void useCalendar(String calendarName) {
    String s = "Switched to calendar " + calendarName;
    log.append(s);
    // In a real implementation, this would set the current calendar
    // For testing, we'll create a mock calendar if one isn't already set
    if (currentCalendar == null) {
      currentCalendar = new MockSmartCalendarModel(log);
    }
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    String s = "Edited calendar " + calendarName + " property " + property + " to " + newValue;
    log.append(s);
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime sourceDateTime,
                        String targetCalendarName, LocalDateTime targetDateTime) {
    String s = "Copied event " + eventName + " from " + sourceDateTime + " to calendar "
            + targetCalendarName + " at " + targetDateTime;
    log.append(s);
  }

  @Override
  public void copyEventsOnDate(LocalDateTime sourceDate, String targetCalendarName,
                               LocalDateTime targetDate) {
    String s = "Copied events on " + sourceDate + " to calendar " + targetCalendarName
            + " starting at " + targetDate;
    log.append(s);
  }

  @Override
  public void copyEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate,
                                     String targetCalendarName, LocalDateTime targetStartDate) {
    String s = "Copied events between " + startDate + " and " + endDate
            + " to calendar " + targetCalendarName + " starting at " + targetStartDate;
    log.append(s);
  }

  @Override
  public List<String> getCalendarNames() {
    log.append("Retrieved list of calendar names");
    return List.of();
  }

  // Additional methods for testing setup
  public void setCurrentCalendar(ISmartCalendarModel calendar) {
    this.currentCalendar = calendar;
  }

  public String getLog() {
    return log.toString();
  }

  public String getModelLog() {
    return ((MockSmartCalendarModel) currentCalendar).getLog();
  }

  public void clearLog() {
    log.setLength(0);
  }
} 