package calendar.model;

import java.time.LocalDateTime;

/**
 * Represents an event in the calendar system.
 * This interface defines the core properties and behaviors that all calendar events must have.
 */
public interface IEvent {
  /**
   * Gets the subject/title of the event.
   * @return the subject of the event
   */
  String getSubject();

  /**
   * Gets the start date and time of the event.
   * @return the start date and time
   */
  LocalDateTime getStartDateTime();

  /**
   * Gets the end date and time of the event.
   * @return the end date and time
   */
  LocalDateTime getEndDateTime();

  /**
   * Gets the description of the event.
   * @return the description of the event
   */
  String getDescription();

  /**
   * Gets the location of the event.
   * @return the location of the event
   */
  EventLocation getLocation();

  /**
   * Gets the status of the event.
   * @return the status of the event
   */
  EventStatus getStatus();

  /**
   * Gets the series ID of the event.
   * @return the series ID, or null if this is not part of a series
   */
  Integer getSeriesId();

  /**
   * Compares this event with another object for equality.
   * Two events are considered equal if they have the same subject and start date/time.
   * @param obj the object to compare with
   * @return true if the events are equal, false otherwise
   */
  boolean equals(Object obj);

  /**
   * Returns a hash code value for this event.
   * The hash code is based on the event's subject and start date/time.
   * @return a hash code value for this event
   */
  int hashCode();
}
