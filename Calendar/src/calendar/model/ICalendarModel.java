package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
  int getNextSeriesId();
}
