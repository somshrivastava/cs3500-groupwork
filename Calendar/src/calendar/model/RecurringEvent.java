package calendar.model;

import java.time.LocalDateTime;

public class RecurringEvent extends AbstractEvent {

  private RecurringEvent(String subject, String description, EventLocation location, EventStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    super(subject, description, location, status, startDateTime, endDateTime, seriesId);
  }

  public static AbstractEventBuilder<RecurringEventBuilder> getBuilder() {
    return new RecurringEventBuilder();
  }

  public static class RecurringEventBuilder extends AbstractEventBuilder<RecurringEventBuilder> {

    @Override
    protected IEvent build() {
      return new RecurringEvent(
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
    protected RecurringEventBuilder getBuilder() {
      return this;
    }
  }
}
