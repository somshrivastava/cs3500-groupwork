package controller;

import calendar.model.IEvent;
import calendar.model.ISmartCalendarModel;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mock implementation of ISmartCalendarModel for testing purposes.
 * Extends MockCalendarModel functionality with smart calendar features.
 */
public class MockSmartCalendarModel extends MockCalendarModel implements ISmartCalendarModel {
  private String calendarName;
  private ZoneId timezone;

  /**
   * Creates a mock smart calendar model.
   * @param log the string builder log
   */
  public MockSmartCalendarModel(StringBuilder log) {
    super(log);
    this.calendarName = "DefaultTestCalendar";
    this.timezone = ZoneId.of("America/New_York");
  }

  /**
   * Creates a mock smart calendar model.
   * @param log the string builder log
   * @param name the name of the calendar
   * @param timezone the timezone of the calendar
   */
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
    String s = "Set calendar name to " + calendarName;
    log.append(s);
    this.calendarName = calendarName;
  }

  @Override
  public void setTimezone(ZoneId timezone) {
    String s = "Set calendar timezone to " + timezone;
    log.append(s);
    this.timezone = timezone;
  }

  @Override
  public IEvent findEventBySubjectAndTime(String subject, LocalDateTime startDateTime) {
    String s = "Found event " + subject + " at " + startDateTime;
    log.append(s);
    // Return a mock event for testing
    return null; // In real implementation would return actual event
  }

  @Override
  public void convertAllEventsToNewTimezone(ZoneId oldTimezone, ZoneId newTimezone) {
    String s = "Converted all events from " + oldTimezone + " to " + newTimezone;
    log.append(s);
  }

  @Override
  public IEvent createCopiedEvent(String eventName, LocalDateTime sourceDateTime,
                                  LocalDateTime targetDateTime) {
    String s = "Created copied event " + eventName + " from " + sourceDateTime + " to "
            + targetDateTime;
    log.append(s);
    // Return a mock event for testing
    return null; // In real implementation would return actual event
  }

  @Override
  public void copyAllEventsToCalendar(LocalDateTime sourceDate, ISmartCalendarModel targetCalendar,
                                      LocalDateTime targetDate) {
    String s = "Copied all events from " + sourceDate + " to calendar "
            + targetCalendar.getCalendarName() + " on " + targetDate;
    log.append(s);
  }

  @Override
  public void copyEventsInRangeToCalendar(LocalDateTime startDate, LocalDateTime endDate,
                                          ISmartCalendarModel targetCalendar,
                                          LocalDateTime targetStartDate) {
    String s = "Copied events from " + startDate + " to " + endDate + " to calendar "
            + targetCalendar.getCalendarName() + " starting at " + targetStartDate;
    log.append(s);
  }

  @Override
  public void addEvent(IEvent event) {
    String s = "Added pre-built event " + event.getSubject();
    log.append(s);
  }

  @Override
  public Integer generateUniqueSeriesId() {
    log.append("Generated unique series ID");
    return 1; // Mock series ID
  }

  // for testing
  public String getLog() {
    return log.toString();
  }
} 