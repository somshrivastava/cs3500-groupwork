package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public interface ICalendarManager {

  void createCalendar(String calendarName, ZoneId timezone);

  void useCalendar(String calendarName);

  void editCalendar(String calendarName, String property, String newValue);
}
