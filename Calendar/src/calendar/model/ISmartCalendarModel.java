package calendar.model;

import java.time.ZoneId;

public interface ISmartCalendarModel extends ICalendarModel {

  void editCalendar(String calendarName, String property, String newValue);

  String getCalendarName();

  ZoneId getTimezone();
}
