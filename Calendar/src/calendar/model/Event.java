package calendar.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a calendar event with a subject, description, location, status,
 * start and end times, and an optional series ID for recurring events.
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
   * Constructs a new Event with the specified properties.
   * @param subject the subject/title of the event
   * @param description the description of the event
   * @param location the location of the event
   * @param status the status of the event
   * @param startDateTime the start date and time
   * @param endDateTime the end date and time
   * @param seriesId the ID of the event series (null for single events)
   */
  protected Event(String subject, String description, EventLocation location, EventStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    this.subject = subject;
    this.description = description;
    this.location = location;
    this.status = status;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.seriesId = seriesId;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Event)) return false;

    Event that = (Event) obj;

    return Objects.equals(this.subject, that.subject) &&
            Objects.equals(this.startDateTime, that.startDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.subject, this.startDateTime);
  }

  /**
   * Gets a new EventBuilder instance for constructing an Event.
   * @return a new EventBuilder instance
   */
  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }

  /**
   * Builder class for constructing Event instances.
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
     * @param subject the subject to set
     * @return this builder instance
     */
    public EventBuilder subject(String subject) {
      this.subject = subject;
      return this.getBuilder();
    }

    /**
     * Sets the description of the event.
     * @param description the description to set
     * @return this builder instance
     */
    public EventBuilder description(String description) {
      this.description = description;
      return this.getBuilder();
    }

    /**
     * Sets the location of the event.
     * @param location the location to set
     * @return this builder instance
     */
    public EventBuilder location(EventLocation location) {
      this.location = location;
      return this.getBuilder();
    }

    /**
     * Sets the status of the event.
     * @param status the status to set
     * @return this builder instance
     */
    public EventBuilder status(EventStatus status) {
      this.status = status;
      return this.getBuilder();
    }

    /**
     * Sets the start date and time of the event.
     * @param startDateTime the start date and time to set
     * @return this builder instance
     */
    public EventBuilder startDateTime(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
      return this.getBuilder();
    }

    /**
     * Sets the end date and time of the event.
     * @param endDateTime the end date and time to set
     * @return this builder instance
     */
    public EventBuilder endDateTime(LocalDateTime endDateTime) {
      this.endDateTime = endDateTime;
      return this.getBuilder();
    }

    /**
     * Sets the series ID of the event.
     * @param seriesId the series ID to set
     * @return this builder instance
     */
    public EventBuilder seriesId(Integer seriesId) {
      this.seriesId = seriesId;
      return this.getBuilder();
    }

    /**
     * Gets this builder instance.
     * @return this builder instance
     */
    public EventBuilder getBuilder() {
      return this;
    }

    /**
     * Builds a new Event instance with the current builder state.
     * @return a new Event instance
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
