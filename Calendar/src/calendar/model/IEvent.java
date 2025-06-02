package calendar.model;

import java.time.LocalDateTime;

public interface IEvent {

  public String getSubject();

  public LocalDateTime getEndDateTime();

  public LocalDateTime getStartDateTime();

  public boolean equals(Object ob);

  public int hashCode();
}
