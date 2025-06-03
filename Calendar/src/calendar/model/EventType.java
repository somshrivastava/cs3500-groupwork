package calendar.model;

/**
 * Represents the type of a calendar event.
 * An event can be either a single occurrence or part of a recurring series.
 * For recurring events, it can be either timed (with specific start and end times)
 * or all-day (spanning the entire day).
 */
public enum EventType {
  SINGLE_ALL_DAY,
  SINGLE_TIMED,
  RECURRING_ALL_DAY_COUNT,
  RECURRING_ALL_DAY_UNTIL,
  RECURRING_TIMED_COUNT,
  RECURRING_TIMED_UNTIL,
  INVALID
}