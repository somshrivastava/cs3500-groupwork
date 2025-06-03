package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

public interface ICalendarCommand {

  void execute(ICalendarModel calendarModel, ICalendarView calendarView);
}
