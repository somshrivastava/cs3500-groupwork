package calendar.controller;

import calendar.model.ICalendarModel;

public interface ICalendarCommand {

  void execute(ICalendarModel calendarModel);
}
