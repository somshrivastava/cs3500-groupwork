package calendar.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event implements IEvent {

  protected final String subject;
  protected final String description;
  protected final EventLocation location;
  protected final EventStatus status;

  protected final LocalDateTime startDateTime;
  protected final LocalDateTime endDateTime;
  protected final Integer seriesId;

  protected Event(String subject, String description, EventLocation location, EventStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    this.subject = subject;
    this.description = description;
    this.location = location;
    this.status = status;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.seriesId = seriesId;
  }


  public static AbstractEventBuilder<EventBuilder> getBuilder() {
    return new Event.EventBuilder();
  }

  public static class EventBuilder extends AbstractEventBuilder<EventBuilder> {

    @Override
    protected IEvent build() {
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

    @Override
    protected EventBuilder getBuilder() {
      return this;
    }
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
}
