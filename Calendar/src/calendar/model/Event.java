package calendar.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a calendar event with a subject, description, location (physical or online),
 * status (public or private), start and end times, and an optional series ID for recurring events.
 * This class is immutable. Use the {@code EventBuilder} to create instances of this class.
 * Two events are considered equal if they have the same subject, start date/time, and end
 * date/time.
 */
public class Event implements IEvent {
  protected final String subject;
  protected final String description;
  protected final EventLocation location;
  protected final EventStatus status;
  protected final LocalDateTime startDateTime;
  protected final LocalDateTime endDateTime;
  protected final Integer seriesId;

  /**
   * Constructs a new {@code Event} with the specified properties.
   *
   * @param subject       the subject of the event (required)
   * @param description   the description of the event (can be null)
   * @param location      the location of the event (can be null)
   * @param status        the status of the event (can be null)
   * @param startDateTime the start date and time (required)
   * @param endDateTime   the end date and time (can be null for all-day events)
   * @param seriesId      the ID of a recurring event (null for single events)
   */
  private Event(String subject, String description, EventLocation location, EventStatus status,
                LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    this.subject = subject;
    this.description = description;
    this.location = location;
    this.status = status;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.seriesId = seriesId;
  }

  /**
   * Gets the subject of the event.
   * @return the subject of the event
   */
  @Override
  public String getSubject() {
    return this.subject;
  }

  /**
   * Gets the start date and time of the event.
   * @return the start date and time
   */
  @Override
  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  /**
   * Gets the end date and time of the event.
   * @return the end date and time
   */
  @Override
  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  /**
   * Gets the description of the event.
   * @return the description of the event
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Gets the location of the event.
   * @return the location of the event
   */
  @Override
  public EventLocation getLocation() {
    return this.location;
  }

  /**
   * Gets the status of the event.
   * @return the status of the event
   */
  @Override
  public EventStatus getStatus() {
    return this.status;
  }

  /**
   * Gets the series ID of the event.
   * @return the series ID, or null if this is not part of a series
   */
  @Override
  public Integer getSeriesId() {
    return this.seriesId;
  }

  /**
   * Compares this event with another object for equality.
   * Two events are considered equal if they have the same subject, start date/time, and
   * end date/time.
   * @param obj the object to compare with
   * @return true if the events are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Event)) {
      return false;
    }

    Event that = (Event) obj;

    return Objects.equals(this.subject, that.subject) &&
            Objects.equals(this.startDateTime, that.startDateTime) &&
            Objects.equals(this.endDateTime, that.endDateTime);
  }

  /**
   * Returns a hash code value for this event.
   * The hash code is based on the event's subject and start date/time.
   * @return a hash code value for this event
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.subject, this.startDateTime, this.endDateTime);
  }

  /**
   * Gets a new EventBuilder instance for constructing an {@code Event}.
   *
   * @return a new EventBuilder instance
   */
  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }

  /**
   * Builder class for constructing {@code Event} instances.
   */
  public static class EventBuilder {
    protected String subject;
    protected String description;
    protected EventLocation location;
    protected EventStatus status;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    protected Integer seriesId;

    /**
     * Sets the subject of the event.
     *
     * @param subject the given subject
     * @return this builder instance
     */
    public EventBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the given description
     * @return this builder instance
     */
    public EventBuilder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the given location
     * @return this builder instance
     */
    public EventBuilder location(EventLocation location) {
      this.location = location;
      return this;
    }

    /**
     * Sets the status of the event.
     *
     * @param status the given status
     * @return this builder instance
     */
    public EventBuilder status(EventStatus status) {
      this.status = status;
      return this;
    }

    /**
     * Sets the start date and time of the event.
     *
     * @param startDateTime the given start date and time
     * @return this builder instance
     */
    public EventBuilder startDateTime(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
      return this;
    }

    /**
     * Sets the end date and time of the event.
     *
     * @param endDateTime the given end date and time
     * @return this builder instance
     */
    public EventBuilder endDateTime(LocalDateTime endDateTime) {
      this.endDateTime = endDateTime;
      return this;
    }

    /**
     * Sets the series ID of the event.
     *
     * @param seriesId the series ID to set (null for single events)
     * @return this builder instance
     */
    public EventBuilder seriesId(Integer seriesId) {
      this.seriesId = seriesId;
      return this;
    }

    /**
     * Builds a new {@code Event} instance with the current builder state.
     *
     * @return a new {@code Event} instance with the properties set in this builder
     */
    public IEvent build() {
      return new Event(
              subject,
              description,
              location,
              status,
              startDateTime,
              endDateTime,
              seriesId
      );
    }
  }
}