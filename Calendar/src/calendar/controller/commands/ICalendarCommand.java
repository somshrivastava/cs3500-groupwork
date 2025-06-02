package calendar.controller.commands;

import calendar.model.ICalendarModel;

public interface ICalendarCommand {

  void execute(ICalendarModel calendarModel);
}
