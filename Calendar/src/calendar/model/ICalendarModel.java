package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the calendar model interface that defines the core operations
 * for managing calendar events.
 */
public interface ICalendarModel {
  /**
   * Creates a new event in the calendar.
   * @param subject the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime the end date and time of the event
   * @param seriesId the ID of the event series (null for single events)
   */
  void createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId);

  /**
   * Gets the next available series ID for recurring events.
   * @return the next series ID
   */
  Integer getNextSeriesId();

  /**
   * Gets all events in the calendar.
   * @return a list of all events
   */
  List<IEvent> getEvents();

  /**
   * Gets all events that occur on a specific date.
   * @param date the date to filter events for
   * @return a list of events that occur on the given date
   */
  List<IEvent> getEventsOnDate(LocalDate date);

  /**
   * Gets all events that occur within a time interval.
   * @param startTime the start of the interval (inclusive)
   * @param endTime the end of the interval (inclusive)
   * @return a list of events that overlap with the given interval
   */
  List<IEvent> getEventsInInterval(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Checks if the given time is busy (has an event scheduled).
   * @param time the time to check
   * @return true if there is an event at the given time, false otherwise
   */
  boolean isTimeBusy(LocalDateTime time);

  /**
   * Edits a single event's property.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param endDateTime the end date/time of the event to edit
   * @param property the property to edit (subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  void editEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, String property, String newValue);

  /**
   * Edits all events in a series that start at or after the given date/time.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property the property to edit (subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  void editEvents(String subject, LocalDateTime startDateTime, String property, String newValue);

  /**
   * Edits all events in a series.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property the property to edit (subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  void editSeries(String subject, LocalDateTime startDateTime, String property, String newValue);
}
