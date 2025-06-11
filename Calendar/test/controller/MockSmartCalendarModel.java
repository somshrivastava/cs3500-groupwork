package controller;

import calendar.model.IEvent;
import calendar.model.ISmartCalendarModel;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mock implementation of ISmartCalendarModel for testing purposes.
 * Extends MockCalendarModel functionality with smart calendar features.
 */
public class MockSmartCalendarModel extends MockCalendarModel implements ISmartCalendarModel {
  private String calendarName;
  private ZoneId timezone;

  public MockSmartCalendarModel(StringBuilder log) {
    super(log);
    this.calendarName = "DefaultTestCalendar";
    this.timezone = ZoneId.of("America/New_York");
  }

  public MockSmartCalendarModel(StringBuilder log, String name, ZoneId timezone) {
    super(log);
    this.calendarName = name;
    this.timezone = timezone;
  }

  @Override
  public String getCalendarName() {
    return calendarName;
  }

  @Override
  public ZoneId getTimezone() {
    return timezone;
  }

  @Override
  public void setCalendarName(String calendarName) {
    log.append("Set calendar name to ").append(calendarName);
    this.calendarName = calendarName;
  }

  @Override
  public void setTimezone(ZoneId timezone) {
    log.append("Set calendar timezone to ").append(timezone);
    this.timezone = timezone;
  }

  @Override
  public IEvent findEventBySubjectAndTime(String subject, LocalDateTime startDateTime) {
    log.append("Found event ").append(subject).append(" at ").append(startDateTime);
    // Return a mock event for testing
    return null; // In real implementation would return actual event
  }

  @Override
  public void convertAllEventsToNewTimezone(ZoneId oldTimezone, ZoneId newTimezone) {
    log.append("Converted all events from ").append(oldTimezone).append(" to ").append(newTimezone);
  }

  @Override
  public IEvent createCopiedEvent(String eventName, LocalDateTime sourceDateTime, 
                                 LocalDateTime targetDateTime) {
    log.append("Created copied event ").append(eventName).append(" from ").append(sourceDateTime)
        .append(" to ").append(targetDateTime);
    // Return a mock event for testing
    return null; // In real implementation would return actual event
  }

  @Override
  public void copyAllEventsToCalendar(LocalDateTime sourceDate, ISmartCalendarModel targetCalendar, 
                                     LocalDateTime targetDate) {
    log.append("Copied all events from ").append(sourceDate).append(" to calendar ")
        .append(targetCalendar.getCalendarName()).append(" on ").append(targetDate);
  }

  @Override
  public void copyEventsInRangeToCalendar(LocalDateTime startDate, LocalDateTime endDate, 
                                         ISmartCalendarModel targetCalendar, 
                                         LocalDateTime targetStartDate) {
    log.append("Copied events from ").append(startDate).append(" to ").append(endDate)
        .append(" to calendar ").append(targetCalendar.getCalendarName())
        .append(" starting at ").append(targetStartDate);
  }

  @Override
  public void addEvent(IEvent event) {
    log.append("Added pre-built event ").append(event.getSubject());
  }

  @Override
  public Integer generateUniqueSeriesId() {
    log.append("Generated unique series ID");
    return 1; // Mock series ID
  }
} 