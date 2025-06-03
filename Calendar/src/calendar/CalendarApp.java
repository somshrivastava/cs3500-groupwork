package calendar;

import java.io.FileReader;
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

    String mode = null;
    String filename = null;

    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
      throw new IllegalArgumentException("Usage: java CalendarApp --mode [interactive|headless] [filename]");
    }

    mode = args[1].toLowerCase();
    if (!mode.equals("interactive") && !mode.equals("headless")) {
      throw new IllegalArgumentException("Mode must be either 'interactive' or 'headless'");
    }

    if (mode.equals("headless")) {
      if (args.length < 3) {
        throw new IllegalArgumentException("Headless mode requires a filename");
      }
      filename = args[2];
    }

    try {
      calendarController.go(mode, filename);
    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
}
