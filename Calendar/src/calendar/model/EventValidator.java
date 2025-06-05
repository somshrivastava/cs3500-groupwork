package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Package-private validator for calendar events.
 * Centralizes all validation logic for event creation and modification.
 */
class EventValidator {

  /**
   * Validates that a subject is not null or empty.
   *
   * @param subject the subject to validate
   * @throws IllegalArgumentException if invalid
   */
  void validateSubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }
  }

  /**
   * Validates that a date/time is not null.
   *
   * @param dateTime  the date/time to validate
   * @param fieldName the name of the field (for error messages)
   * @throws IllegalArgumentException if null
   */
  void validateDateTime(LocalDateTime dateTime, String fieldName) {
    if (dateTime == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }
  }

  /**
   * Validates that start time is before end time.
   *
   * @param start the start time
   * @param end   the end time
   * @throws IllegalArgumentException if end is not after start
   */
  void validateStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
    if (!end.isAfter(start)) {
      throw new IllegalArgumentException("End time must be after start time");
    }
  }

  /**
   * Validates parameters for a timed event.
   *
   * @param subject       the event subject
   * @param startDateTime the start time
   * @param endDateTime   the end time
   * @throws IllegalArgumentException if any parameter is invalid
   */
  void validateTimedEvent(String subject, LocalDateTime startDateTime,
                          LocalDateTime endDateTime) {
    validateSubject(subject);
    validateDateTime(startDateTime, "Start time");
    validateDateTime(endDateTime, "End time");
    validateStartBeforeEnd(startDateTime, endDateTime);
  }

  /**
   * Validates parameters for an all-day event.
   *
   * @param subject the event subject
   * @param date    the event date
   * @throws IllegalArgumentException if any parameter is invalid
   */
  void validateAllDayEvent(String subject, LocalDateTime date) {
    validateSubject(subject);
    validateDateTime(date, "Date");
  }

  /**
   * Validates that weekdays list is not null or empty.
   *
   * @param weekdays the list to validate
   * @throws IllegalArgumentException if null or empty
   */
  void validateWeekdays(ArrayList<DayOfWeek> weekdays) {
    if (weekdays == null || weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
  }

  /**
   * Validates that count is positive.
   *
   * @param count the count to validate
   * @throws IllegalArgumentException if not positive
   */
  void validateCount(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }
  }

  /**
   * Validates that until date is after start date.
   *
   * @param startDate the start date
   * @param untilDate the until date
   * @throws IllegalArgumentException if until date is not after start date
   */
  void validateUntilDate(LocalDateTime startDate, LocalDateTime untilDate) {
    validateDateTime(untilDate, "Until date");
    if (!untilDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Until date must be after start date");
    }
  }

  /**
   * Validates that an event starts and ends on the same day (for series events).
   *
   * @param start the start time
   * @param end   the end time
   * @throws IllegalArgumentException if not on the same day
   */
  void validateSingleDayEvent(LocalDateTime start, LocalDateTime end) {
    if (!start.toLocalDate().equals(end.toLocalDate())) {
      throw new IllegalArgumentException(
              "Events in a series must start and end on the same day");
    }
  }

  /**
   * Validates parameters for a recurring timed event with count.
   */
  void validateRecurringTimedEvent(String subject, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                   int count) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates parameters for a recurring timed event with until date.
   */
  void validateRecurringTimedEventUntil(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                        LocalDateTime untilDate) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateUntilDate(startDateTime, untilDate);
  }

  /**
   * Validates parameters for a recurring all-day event with count.
   */
  void validateRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                    ArrayList<DayOfWeek> weekdays, int count) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates parameters for a recurring all-day event with until date.
   */
  void validateRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                         ArrayList<DayOfWeek> weekdays,
                                         LocalDateTime untilDate) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateUntilDate(startDate, untilDate);
  }
}