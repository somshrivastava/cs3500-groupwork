package controller.tests;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;

/**
 * This class is a mock CalendarModel used for testing the CalendarController.
 */
public class MockCalendarModel implements ICalendarModel {
  final StringBuilder log;

  public MockCalendarModel(StringBuilder log) {
    this.log = Objects.requireNonNull(log);
  }

  /**
   * Creates a single timed event.
   */
  @Override
  public void createSingleTimedEvent(String subject, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime) {
    log.append("Created single timed event ").append(subject).append(" starting at ")
            .append(startDateTime).append(" until ").append(endDateTime);
  }

  /**
   * Creates a single all-day event.
   */
  @Override
  public void createSingleAllDayEvent(String subject, LocalDateTime date) {
    log.append("Created single all day event ").append(subject).append(" on ").append(date);
  }

  /**
   * Creates a recurring timed event with a count.
   */
  @Override
  public void createRecurringTimedEvent(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                        int count) {
    log.append("Created recurring timed event ").append(subject).append(" starting at ")
            .append(startDateTime).append(" until ").append(endDateTime).append(" for a count of ")
            .append(count);
  }

  /**
   * Creates a recurring timed event until a specific date.
   */
  @Override
  public void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime,
                                             LocalDateTime endDateTime,
                                             ArrayList<DayOfWeek> weekdays,
                                             LocalDateTime untilDate) {
    log.append("Created recurring timed event ").append(subject).append(" starting at ")
            .append(startDateTime).append(" until ").append(endDateTime).append(" to the date ")
            .append(untilDate);
  }

  /**
   * Creates a recurring all-day event with a count.
   */
  @Override
  public void createRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                         ArrayList<DayOfWeek> weekdays, int count) {
    log.append("Created recurring all day event ").append(subject).append(" starting on the date ")
            .append(startDate).append(" for a count of ").append(count);
  }

  /**
   * Creates a recurring all-day event until a specific date.
   */
  @Override
  public void createRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                              ArrayList<DayOfWeek> weekdays,
                                              LocalDateTime untilDate) {
    log.append("Created recurring timed event ").append(subject).append(" starting on the date ")
            .append(startDate).append(" to the date ").append(untilDate);
  }

  /**
   * Edits a single event's property.
   */
  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                        String property, String newValue) {
    log.append("Edited single event's property: ").append(subject).append(" starting on ")
            .append(startDateTime).append(" until ").append(endDateTime).append(". Changed ")
            .append(property).append(" to ").append(newValue);
  }

  /**
   * Edits all events in a series that start at or after the given date/time.
   */
  @Override
  public void editEvents(String subject, LocalDateTime startDateTime, String property,
                         String newValue) {
    log.append("Edited series of event's properties: ").append(subject)
            .append(" starting on or after date ").append(startDateTime).append(". Changed ")
            .append(property).append(" to ").append(newValue);
  }

  /**
   * Edits all events in a series.
   */
  @Override
  public void editSeries(String subject, LocalDateTime startDateTime, String property,
                         String newValue) {
    log.append("Edited all series of event's properties: ").append(subject).append(" with start time ")
            .append(startDateTime).append(". Changed ").append(property).append(" to ")
            .append(newValue);
  }

  /**
   * Gets all events that occur on a specific date.
   */
  @Override
  public List<IEvent> printEvents(LocalDateTime date) {
    log.append("Queried for all events that occur on ").append(date);
    return List.of();
  }

  /**
   * Gets all events that occur within a time interval.
   */
  @Override
  public List<IEvent> printEvents(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    log.append("Queried for all events that occur from ").append(startDateTime).append(" to ")
            .append(endDateTime);
    return List.of();
  }

  /**
   * Checks if the given time is busy (has an event scheduled).
   */
  @Override
  public boolean showStatus(LocalDateTime dateTime) {
    log.append("Checked if there is an event during ").append(dateTime);
    return false;
  }
}
