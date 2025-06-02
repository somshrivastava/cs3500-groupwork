package calendar;

import java.io.InputStreamReader;

import calendar.controller.CalendarController;
import calendar.controller.ICalendarController;
import calendar.model.CalendarModel;
import calendar.model.ICalendarModel;
import calendar.view.CalendarView;
import calendar.view.ICalendarView;

public class CalendarApp {

  public static void main(String[] args) {
    ICalendarModel calendarModel = new CalendarModel();
    ICalendarView calendarView = new CalendarView(System.out);
    ICalendarController calendarController = new CalendarController(calendarModel, calendarView, new InputStreamReader(System.in));
    calendarController.go("interactive", null);
  }
}
