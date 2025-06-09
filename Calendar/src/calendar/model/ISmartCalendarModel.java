package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface ISmartCalendarModel extends ICalendarModel {

  String getCalendarName();

  ZoneId getTimezone();

  void setCalendarName(String calendarName);

  void setTimezone(ZoneId timezone);

  /**
   * Finds and returns a specific event by subject and start date/time.
   *
   * @param subject the subject of the event to find
   * @param startDateTime the start date/time of the event to find
   * @return the matching event
   * @throws IllegalArgumentException if no event is found with the given criteria
   */
  IEvent findEventBySubjectAndTime(String subject, LocalDateTime startDateTime);

  /**
   * Creates a copy of an existing event with new timing and timezone conversion.
   * Finds the source event and creates a new event with the specified target time,
   * preserving the original duration and handling timezone conversion.
   *
   * @param eventName the name/subject of the event to copy
   * @param sourceDateTime the start date/time of the source event
   * @param targetTimezone the timezone of the target calendar
   * @param targetDateTime the desired start time in the target calendar's timezone
   * @return a new event ready to be added to the target calendar
   * @throws IllegalArgumentException if the source event is not found
   */
  IEvent createCopiedEvent(String eventName, LocalDateTime sourceDateTime, ZoneId targetTimezone, LocalDateTime targetDateTime);
}
