package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Interface for managing a collection of smart calendars.
 * Provides functionality for creating, editing, and managing multiple calendars,
 * as well as copying events between calendars with timezone conversion.
 */
public interface ICalendarManager {

  /**
   * Gets the currently active calendar.
   * 
   * @return the current calendar, or null if no calendar is currently active
   */
  ISmartCalendarModel getCurrentCalendar();

  /**
   * Creates a new calendar with the specified name and timezone.
   * 
   * @param calendarName the name of the new calendar
   * @param timezone the timezone for the new calendar
   * @throws IllegalArgumentException if a calendar with the given name already exists
   */
  void createCalendar(String calendarName, ZoneId timezone);

  /**
   * Sets the specified calendar as the currently active calendar.
   * 
   * @param calendarName the name of the calendar to use
   * @throws IllegalArgumentException if no calendar with the given name exists
   */
  void useCalendar(String calendarName);

  /**
   * Edits a property of the specified calendar.
   * Supported properties are "name" and "timezone".
   * 
   * @param calendarName the name of the calendar to edit
   * @param property the property to edit ("name" or "timezone")
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the calendar doesn't exist, property is invalid, 
   *         or the new value is invalid
   */
  void editCalendar(String calendarName, String property, String newValue);

  /**
   * Copies a single event from the current calendar to a target calendar.
   * The event is copied with timezone conversion if the calendars have different timezones.
   * 
   * @param eventName the subject/name of the event to copy
   * @param sourceDateTime the start date/time of the source event
   * @param targetCalendarName the name of the target calendar
   * @param targetDateTime the desired start time for the copied event
   * @throws IllegalArgumentException if no current calendar is set, target calendar doesn't exist,
   *         or the source event is not found
   */
  void copyEvent(String eventName, LocalDateTime sourceDateTime, 
                 String targetCalendarName, LocalDateTime targetDateTime);

  /**
   * Copies all events from a specific date in the current calendar to a target calendar.
   * Events are copied with timezone conversion if the calendars have different timezones.
   * 
   * @param sourceDate the date to copy events from
   * @param targetCalendarName the name of the target calendar
   * @param targetDate the date to place the copied events on
   * @throws IllegalArgumentException if no current calendar is set or target calendar doesn't exist
   */
  void copyEventsOnDate(LocalDateTime sourceDate, String targetCalendarName, 
                        LocalDateTime targetDate);

  /**
   * Copies all events within a date range from the current calendar to a target calendar.
   * Events are copied with timezone conversion if the calendars have different timezones.
   * The relative date offsets are preserved in the target calendar.
   * 
   * @param startDate the start date of the range (inclusive)
   * @param endDate the end date of the range (inclusive)
   * @param targetCalendarName the name of the target calendar
   * @param targetStartDate the start date to place the copied events on
   * @throws IllegalArgumentException if no current calendar is set or target calendar doesn't exist
   */
  void copyEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, 
                              String targetCalendarName, LocalDateTime targetStartDate);
}
