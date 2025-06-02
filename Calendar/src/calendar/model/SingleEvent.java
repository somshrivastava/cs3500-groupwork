package calendar.model;

import java.time.LocalDateTime;

public class SingleEvent extends AbstractEvent {

  private SingleEvent(String subject, String description, EventLocation location, EventStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    super(subject, description, location, status, startDateTime, endDateTime, seriesId);
  }

  public static AbstractEventBuilder<SingleEventBuilder> getBuilder() {
    return new SingleEventBuilder();
  }

  public static class SingleEventBuilder extends AbstractEventBuilder<SingleEventBuilder> {

    @Override
    protected IEvent build() {
      return new SingleEvent(
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
    protected SingleEventBuilder getBuilder() {
      return this;
    }
  }
}
