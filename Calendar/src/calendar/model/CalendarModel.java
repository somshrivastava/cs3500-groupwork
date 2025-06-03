package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the calendar model that manages a collection of events.
 * This class handles the creation and storage of calendar events, both single and recurring.
 */
public class CalendarModel implements ICalendarModel {
  private final Set<IEvent> events;
  private static Integer nextSeriesId = 1;

  /**
   * Constructs a new CalendarModel with an empty set of events.
   */
  public CalendarModel() {
    this.events = new HashSet<IEvent>();
  }

  @Override
  public void createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    IEvent newEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .seriesId(seriesId)
            .build();
    this.events.add(newEvent);
  }

  /**
   * Validates that the subject is not null or empty.
   * @param subject the subject to validate
   * @throws IllegalArgumentException if the subject is null or empty
   */
  private void validateSubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }
  }

  /**
   * Validates that a date/time is not null.
   * @param dateTime the date/time to validate
   * @param fieldName the name of the field being validated (for error messages)
   * @throws IllegalArgumentException if the date/time is null
   */
  private void validateDateTime(LocalDateTime dateTime, String fieldName) {
    if (dateTime == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }
  }

  /**
   * Validates the parameters for a timed event.
   * @param subject the subject to validate
   * @param startDateTime the start time to validate
   * @param endDateTime the end time to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    validateSubject(subject);
    validateDateTime(startDateTime, "Start time");
    validateDateTime(endDateTime, "End time");
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }
  }

  /**
   * Validates the parameters for an all-day event.
   * @param subject the subject to validate
   * @param date the date to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateAllDayEvent(String subject, LocalDateTime date) {
    validateSubject(subject);
    validateDateTime(date, "Date");
  }

  /**
   * Validates the weekdays list for recurring events.
   * @param weekdays the weekdays list to validate
   * @throws IllegalArgumentException if the list is null or empty
   */
  private void validateWeekdays(ArrayList<DayOfWeek> weekdays) {
    if (weekdays == null || weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
  }

  /**
   * Validates the count parameter for recurring events.
   * @param count the count to validate
   * @throws IllegalArgumentException if the count is not positive
   */
  private void validateCount(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }
  }

  /**
   * Validates the until date for recurring events.
   * @param startDate the start date to validate against
   * @param untilDate the until date to validate
   * @throws IllegalArgumentException if the until date is null or not after the start date
   */
  private void validateUntilDate(LocalDateTime startDate, LocalDateTime untilDate) {
    validateDateTime(untilDate, "Until date");
    if (!untilDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Until date must be after start date");
    }
  }

  /**
   * Validates the parameters for a recurring timed event with count.
   * @param subject the subject to validate
   * @param startDateTime the start time to validate
   * @param endDateTime the end time to validate
   * @param weekdays the weekdays list to validate
   * @param count the count to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                           ArrayList<DayOfWeek> weekdays, int count) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates the parameters for a recurring timed event with until date.
   * @param subject the subject to validate
   * @param startDateTime the start time to validate
   * @param endDateTime the end time to validate
   * @param weekdays the weekdays list to validate
   * @param untilDate the until date to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateUntilDate(startDateTime, untilDate);
  }

  /**
   * Validates the parameters for a recurring all-day event with count.
   * @param subject the subject to validate
   * @param startDate the start date to validate
   * @param weekdays the weekdays list to validate
   * @param count the count to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                            ArrayList<DayOfWeek> weekdays, int count) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates the parameters for a recurring all-day event with until date.
   * @param subject the subject to validate
   * @param startDate the start date to validate
   * @param weekdays the weekdays list to validate
   * @param untilDate the until date to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                                 ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateUntilDate(startDate, untilDate);
  }

  @Override
  public int getNextSeriesId() {
    return nextSeriesId++;
  }
}
