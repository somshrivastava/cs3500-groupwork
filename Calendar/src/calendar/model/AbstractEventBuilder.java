package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class AbstractEventBuilder<T extends AbstractEventBuilder<T>> implements IEventBuilder<T> {
  protected String subject;
  protected String description;
  protected EventLocation location;
  protected EventStatus status;
  protected LocalDateTime startDateTime;
  protected LocalDateTime endDateTime;
  protected Integer seriesId;

  protected AbstractEventBuilder() {
    this.subject = "";
    this.description = "";
    this.location = null;
    this.status = null;
    this.startDateTime = null;
    this.endDateTime = null;
    this.seriesId = null;
  }

  public T subject(String subject) {
    this.subject = subject;
    return this.getBuilder();
  }

  public T description(String description) {
    this.description = description;
    return this.getBuilder();
  }

  public T location(EventLocation location) {
    this.location = location;
    return this.getBuilder();
  }

  public T status(EventStatus status) {
    this.status = status;
    return this.getBuilder();
  }

  public T startDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
    return this.getBuilder();
  }

  public T endDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
    return this.getBuilder();
  }

  public T seriesId(Integer seriesId) {
    this.seriesId = seriesId;
    return this.getBuilder();
  }

  protected abstract IEvent build();

  protected abstract T getBuilder();
}
