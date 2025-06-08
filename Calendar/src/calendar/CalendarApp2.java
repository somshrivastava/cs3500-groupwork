package calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import calendar.controller.ICalendarController;
import calendar.controller.SmartHeadlessController;
import calendar.controller.SmartInteractiveController;
import calendar.model.CalendarManager;
import calendar.model.ICalendarManager;
import calendar.view.CalendarView;
import calendar.view.ICalendarView;

/**
 * The driver of the smart calendar application that supports multiple calendars.
 * This application supports two modes:
 * - Interactive mode: User enters commands through the console
 * - Headless mode: Commands are read from a file
 * 
 * New features include:
 * - Multiple calendar management
 * - Timezone support
 * - Event copying between calendars
 * - Calendar creation, editing, and switching
 * 
 * How to run each mode:
 * - Interactive: java CalendarApp2 --mode interactive
 * - Headless: java CalendarApp2 --mode headless {file name}
 */
public class CalendarApp2 {

  /**
   * Initializes and runs the smart calendar application.
   *
   * @param args command line arguments in either format:
   *             --mode interactive (for interactive mode)
   *             --mode headless {file name} (for headless mode)
   * @throws IllegalArgumentException if the mode is invalid or arguments are missing
   * @throws RuntimeException         if the file cannot be found in headless mode
   */
  public static void main(String[] args) {
    validateArguments(args);

    String mode = args[1];
    ICalendarController controller = createController(mode, args);

    controller.execute();
  }

  /**
   * Validates the format of the command line arguments.
   *
   * @param args the command line arguments
   * @throws IllegalArgumentException if arguments are invalid
   */
  private static void validateArguments(String[] args) {
    if (args.length < 2 || !args[0].equals("--mode")) {
      throw new IllegalArgumentException(
              "Application must be run in the following format: " +
                      "java CalendarApp2 --mode {'interactive' or 'headless'} {filename}");
    }
  }

  /**
   * Creates the controller based on the mode.
   *
   * @param mode the mode (interactive or headless)
   * @param args the command line arguments
   * @return the controller
   * @throws IllegalArgumentException if the mode is invalid
   * @throws RuntimeException         if file cannot be found in headless mode
   */
  private static ICalendarController createController(String mode, String[] args) {
    ICalendarManager calendarManager = new CalendarManager();
    ICalendarView calendarView = new CalendarView(System.out);

    switch (mode.toLowerCase()) {
      case "interactive":
        return createInteractiveController(calendarManager, calendarView);
      case "headless":
        return createHeadlessController(calendarManager, calendarView, args);
      default:
        throw new IllegalArgumentException("Mode must be 'interactive' or 'headless'");
    }
  }

  /**
   * Creates a smart interactive controller.
   *
   * @param calendarManager the calendar manager
   * @param view            the calendar view
   * @return a smart interactive controller
   */
  private static ICalendarController createInteractiveController(ICalendarManager calendarManager,
                                                                 ICalendarView view) {
    Readable readable = new InputStreamReader(System.in);
    return new SmartInteractiveController(calendarManager, view, readable);
  }

  /**
   * Creates a smart headless controller.
   *
   * @param calendarManager the calendar manager
   * @param view            the calendar view
   * @param args            the command line arguments containing the filename
   * @return a smart headless controller
   * @throws RuntimeException if the file is not found or not enough arguments
   */
  private static ICalendarController createHeadlessController(ICalendarManager calendarManager,
                                                              ICalendarView view,
                                                              String[] args) {
    if (args.length < 3) {
      throw new IllegalArgumentException(
              "Headless mode requires the following format: java CalendarApp2 --mode headless " +
                      "{file name}");
    }

    File file = new File(args[2]);

    try {
      return new SmartHeadlessController(calendarManager, view, file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + args[2] + ". Error: " + e.getMessage());
    }
  }
}
