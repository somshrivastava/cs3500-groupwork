package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the calendar model interface that defines the core operations
 * for managing calendar events.
 */
public interface ICalendarModel {
  /**
   * Creates a single timed event.
   *
   * @param subject       the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @throws IllegalArgumentException if the parameters are invalid
   */
  void createSingleTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime
          endDateTime);

  /**
   * Creates a single all-day event.
   *
   * @param subject the subject/title of the event
   * @param date    the date of the event
   * @throws IllegalArgumentException if the parameters are invalid
   */
  void createSingleAllDayEvent(String subject, LocalDateTime date);

  /**
   * Creates a recurring timed event with a count.
   *
   * @param subject       the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param weekdays      the days of the week when the event occurs
   * @param count         the number of occurrences
   * @throws IllegalArgumentException if the parameters are invalid
   */
  void createRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime
                                         endDateTime,
                                 ArrayList<DayOfWeek> weekdays, int count);

  /**
   * Creates a recurring timed event until a specific date.
   *
   * @param subject       the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   * @param weekdays      the days of the week when the event occurs
   * @param untilDate     the date until which the event should recur
   * @throws IllegalArgumentException if the parameters are invalid
   */
  void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime
                                              endDateTime,
                                      ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate);

  /**
   * Creates a recurring all-day event with a count.
   *
   * @param subject   the subject/title of the event
   * @param startDate the start date of the event
   * @param weekdays  the days of the week when the event occurs
   * @param count     the number of occurrences
   * @throws IllegalArgumentException if the parameters are invalid
   */
  void createRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                  ArrayList<DayOfWeek> weekdays, int count);

  /**
   * Creates a recurring all-day event until a specific date.
   *
   * @param subject   the subject/title of the event
   * @param startDate the start date of the event
   * @param weekdays  the days of the week when the event occurs
   * @param untilDate the date until which the event should recur
   * @throws IllegalArgumentException if the parameters are invalid
   */
  void createRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                       ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate);

  /**
   * Edits a single event's property.
   *
   * @param subject       the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param endDateTime   the end date/time of the event to edit
   * @param property      the property to edit (subject, start, end, description, location, status)
   * @param newValue      the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  void editEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                 String property, String newValue);

  /**
   * Edits all events in a series that start at or after the given date/time.
   *
   * @param subject       the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property      the property to edit (subject, start, end, description, location, status)
   * @param newValue      the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  void editEvents(String subject, LocalDateTime startDateTime, String property, String newValue);

  /**
   * Edits all events in a series.
   *
   * @param subject       the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property      the property to edit (subject, start, end, description, location, status)
   * @param newValue      the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  void editSeries(String subject, LocalDateTime startDateTime, String property, String newValue);

  /**
   * Gets all events that occur on a specific date.
   *
   * @param date the date to filter events for
   * @return a list of events that occur on the given date
   */
  List<IEvent> printEvents(LocalDateTime date);

  /**
   * Gets all events that occur within a time interval.
   *
   * @param startDateTime the start of the interval (inclusive)
   * @param endDateTime   the end of the interval (inclusive)
   * @return a list of events that overlap with the given interval
   */
  List<IEvent> printEvents(LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Gets a limited number of events that occur on or after a specific date/time.
   *
   * @param startDateTime the start date/time to search from (inclusive)
   * @param maxEvents the maximum number of events to return
   * @return a list of events that start on or after the given date/time, limited to maxEvents,
   *         sorted by start time
   */
  List<IEvent> getUpcomingEvents(LocalDateTime startDateTime, int maxEvents);

  /**
   * Checks if the given time is busy (has an event scheduled).
   *
   * @param dateTime the time to check
   * @return true if there is an event at the given time, false otherwise
   */
  boolean showStatus(LocalDateTime dateTime);
}
