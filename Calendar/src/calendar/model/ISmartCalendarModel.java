package calendar.model;

import java.time.ZoneId;

public interface ISmartCalendarModel extends ICalendarModel {

  String getCalendarName();

  ZoneId getTimezone();

  void setCalendarName(String calendarName);

  void setTimezone(ZoneId timezone);
}
