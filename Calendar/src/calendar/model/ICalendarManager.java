package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public interface ICalendarManager {

  ICalendarModel createCalendar(String calendarName, ZoneId timezone);

  void useCalendar(String calendarName);

  void copyEvent(String eventName, LocalDateTime eventDateTime, String calendarName, LocalDateTime newEventDateTime);

  void copyEvents(LocalDateTime dateTime, String calendarName, LocalDateTime newDateTime);

  void copyEvents(LocalDateTime startDatetime, LocalDateTime endDateTime, String calendarName, LocalDateTime newDatetime);

  ISmartCalendarModel getCurrentCalendar();

  ISmartCalendarModel getCalendar(String calendarName);

  Map<String, ISmartCalendarModel> getAllCalendars();
}
