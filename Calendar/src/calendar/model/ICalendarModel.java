package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface ICalendarModel {
  /**
   * Creates a single event.
   *
   * @param event the event to be created
   */
  void createEvent(IEvent event);

  /**
   * Creates a single timed event.
   *
   * @param subject       the subject of the event
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   */
  void createSingleTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Creates a single all-day event.
   *
   * @param subject the subject of the event
   * @param date    the date of the event
   */
  void createSingleAllDayEvent(String subject, LocalDateTime date);

  /**
   * Creates a series of timed events that repeat N times.
   *
   * @param subject       the subject of the events
   * @param startDateTime the start date and time of the first event
   * @param endDateTime   the end date and time of the first event
   * @param weekdays      the days of the week when events should occur
   * @param count         the number of times to repeat the event
   */
  void createRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                 ArrayList<DayOfWeek> weekdays, int count);

  /**
   * Creates a series of timed events that repeat until a specific date.
   *
   * @param subject       the subject of the events
   * @param startDateTime the start date and time of the first event
   * @param endDateTime   the end date and time of the first event
   * @param weekdays      the days of the week when events should occur
   * @param untilDate     the date until which events should be created
   */
  void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate);

  /**
   * Creates a series of all-day events that repeat N times.
   *
   * @param subject   the subject of the events
   * @param startDate the start date of the first event
   * @param weekdays  the days of the week when events should occur
   * @param count     the number of times to repeat the event
   */
  void createRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                  ArrayList<DayOfWeek> weekdays, int count);

  /**
   * Creates a series of all-day events that repeat until a specific date.
   *
   * @param subject   the subject of the events
   * @param startDate the start date of the first event
   * @param weekdays  the days of the week when events should occur
   * @param untilDate the date until which events should be created
   */
  void createRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                       ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate);
}
