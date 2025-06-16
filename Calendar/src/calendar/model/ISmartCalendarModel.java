package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Interface for a smart calendar model that extends basic calendar functionality
 * with calendar-specific properties like name and timezone.
 */
public interface ISmartCalendarModel extends ICalendarModel {

  /**
   * Gets the name of this calendar.
   * 
   * @return the calendar name
   */
  String getCalendarName();

  /**
   * Gets the timezone of this calendar.
   * 
   * @return the calendar timezone
   */
  ZoneId getTimezone();

  /**
   * Sets the name of this calendar.
   * 
   * @param calendarName the new calendar name
   */
  void setCalendarName(String calendarName);

  /**
   * Sets the timezone of this calendar.
   * When the timezone changes, all existing events are converted to the new timezone.
   * 
   * @param timezone the new calendar timezone
   */
  void setTimezone(ZoneId timezone);

  /**
   * Finds and returns a specific event by subject and start date/time.
   *
   * @param subject the subject of the event to find
   * @param startDateTime the start date/time of the event to find
   * @return the matching event
   * @throws IllegalArgumentException if no event is found with the given criteria
   */
  IEvent findEventBySubjectAndTime(String subject, LocalDateTime startDateTime);

  /**
   * Converts all events in this calendar from the old timezone to a new timezone.
   * This is called when the calendar's timezone is changed.
   * 
   * @param oldTimezone the original timezone
   * @param newTimezone the new timezone to convert to
   */
  void convertAllEventsToNewTimezone(ZoneId oldTimezone, ZoneId newTimezone);

  /**
   * Creates a copy of an existing event with new timing.
   * Finds the source event and creates a new event with the specified target time,
   * preserving the original duration.
   *
   * @param eventName the name/subject of the event to copy
   * @param sourceDateTime the start date/time of the source event
   * @param targetDateTime the desired start time for the copied event
   * @return a new event ready to be added to the target calendar
   * @throws IllegalArgumentException if the source event is not found
   */
  IEvent createCopiedEvent(String eventName, LocalDateTime sourceDateTime, 
                          LocalDateTime targetDateTime);

  /**
   * Copies all events from a source date to a target calendar on a target date with timezone
   * conversion.
   * Events are converted from this calendar's timezone to the target calendar's timezone.
   *
   * @param sourceDate the date to copy events from
   * @param targetCalendar the calendar to copy events to
   * @param targetDate the date to place the copied events on
   */
  void copyAllEventsToCalendar(LocalDateTime sourceDate, ISmartCalendarModel targetCalendar, 
                              LocalDateTime targetDate);

  /**
   * Copies all events in a date range from this calendar to a target calendar with timezone
   * conversion.
   * Events are converted from this calendar's timezone to the target calendar's timezone.
   * If an event series partly overlaps with the range, only overlapping events are copied
   * but retain series status.
   *
   * @param startDate the start date of the range (inclusive)
   * @param endDate the end date of the range (inclusive)
   * @param targetCalendar the calendar to copy events to
   * @param targetStartDate the start date to place the copied events on
   */
  void copyEventsInRangeToCalendar(LocalDateTime startDate, LocalDateTime endDate, 
                                  ISmartCalendarModel targetCalendar, LocalDateTime
                                           targetStartDate);

  /**
   * Adds a pre-built event to this calendar.
   * This allows adding events with specific properties like series ID.
   *
   * @param event the event to add to the calendar
   */
  void addEvent(IEvent event);

  /**
   * Generates a unique series ID that doesn't conflict with existing series in this calendar.
   * 
   * @return a new unique series ID
   */
  Integer generateUniqueSeriesId();
}
