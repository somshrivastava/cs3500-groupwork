package controller;

import calendar.model.ICalendarManager;
import calendar.model.ISmartCalendarModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    log.append("Created calendar ").append(calendarName).append(" with timezone ").append(timezone);
  }

  @Override
  public void useCalendar(String calendarName) {
    log.append("Switched to calendar ").append(calendarName);
    // In a real implementation, this would set the current calendar
    // For testing, we'll create a mock calendar if one isn't already set
    if (currentCalendar == null) {
      currentCalendar = new MockSmartCalendarModel(new StringBuilder());
    }
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    log.append("Edited calendar ").append(calendarName).append(" property ").append(property)
        .append(" to ").append(newValue);
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime sourceDateTime, 
                       String targetCalendarName, LocalDateTime targetDateTime) {
    log.append("Copied event ").append(eventName).append(" from ").append(sourceDateTime)
        .append(" to calendar ").append(targetCalendarName).append(" at ").append(targetDateTime);
  }

  @Override
  public void copyEventsOnDate(LocalDateTime sourceDate, String targetCalendarName, 
                              LocalDateTime targetDate) {
    log.append("Copied events on ").append(sourceDate).append(" to calendar ")
        .append(targetCalendarName).append(" starting at ").append(targetDate);
  }

  @Override
  public void copyEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, 
                                    String targetCalendarName, LocalDateTime targetStartDate) {
    log.append("Copied events between ").append(startDate).append(" and ").append(endDate)
        .append(" to calendar ").append(targetCalendarName).append(" starting at ")
        .append(targetStartDate);
  }

  // Additional methods for testing setup
  public void setCurrentCalendar(ISmartCalendarModel calendar) {
    this.currentCalendar = calendar;
  }

  public String getLog() {
    return log.toString();
  }

  public void clearLog() {
    log.setLength(0);
  }
} 