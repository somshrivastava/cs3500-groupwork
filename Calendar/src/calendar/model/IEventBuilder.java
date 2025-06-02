package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IEventBuilder<T> {

  public T subject(String subject);

  public T description(String description);

  public T location(EventLocation location);

  public T status(EventStatus status);

  public T startDateTime(LocalDateTime startDateTime);

  public T endDateTime(LocalDateTime endDateTime);

  public T seriesId(Integer seriesId);
}
