package calendar;

import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import calendar.controller.HeadlessController;
import calendar.controller.InteractiveController;
import calendar.controller.ICalendarController;
import calendar.model.CalendarModel;
import calendar.model.ICalendarModel;
import calendar.view.CalendarView;
import calendar.view.ICalendarView;

/**
 * The driver of this application.
 */
public class CalendarApp {

  /**
   * The main method of the program.
   *
   * @param args any command line arguments
   * @throws IllegalArgumentException if the mode is invalid or the file cannot be accessed
   */
  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equals("--mode")) {
      throw new IllegalArgumentException("Must specify mode.");
    }
    // get mode
    String mode = args[1];

    ICalendarModel calendarModel = new CalendarModel();
    ICalendarView calendarView = new CalendarView(System.out);
    Readable readable = new InputStreamReader(System.in);
    ICalendarController controller;

    if (mode.equalsIgnoreCase("interactive")) {
      controller = new InteractiveController(calendarModel, calendarView, readable);
    } else if (mode.equalsIgnoreCase("headless")) {
      // get file from path name
      File file = new File(args[2]);
      try {
        controller = new HeadlessController(calendarModel, calendarView, file);
      } catch (FileNotFoundException e) {
        throw new RuntimeException("File not found.");
      }
    } else {
      throw new IllegalArgumentException("Invalid mode.");
    }
    controller.go();
  }
}
