package calendar;

import java.io.InputStreamReader;

import controller.CalendarController;
import controller.ICalendarController;
import model.CalendarModel;
import model.ICalendarModel;
import view.CalendarView;
import view.ICalendarView;

public class CalendarApp {

  public static void main(String[] args) {
    ICalendarModel calendarModel = new CalendarModel();
    ICalendarView calendarView = new CalendarView(System.out);
    ICalendarController calendarController = new CalendarController(calendarModel, calendarView, new InputStreamReader(System.in));
    calendarController.go("interactive", null);
  }
}
