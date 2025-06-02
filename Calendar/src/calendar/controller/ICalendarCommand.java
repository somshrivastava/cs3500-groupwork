package calendar.controller;

import model.ICalendarModel;

public interface ICalendarCommand {

  void execute(ICalendarModel calendarModel);
}
