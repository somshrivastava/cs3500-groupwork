package calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.time.ZoneId;

import calendar.controller.ControllerGUI;
import calendar.controller.HeadlessController;
import calendar.controller.ICalendarController;
import calendar.controller.InteractiveController;
import calendar.model.CalendarManager;
import calendar.model.ICalendarManager;
import calendar.view.CalendarView;
import calendar.view.ICalendarView;
import calendar.view.ICalendarViewGUI;
import calendar.view.JFrameView;

/**
 * The driver of this application.
 * This application supports three modes as well as the management of multiple calendars:
 * - Interactive mode: User enters commands through the console
 * - Headless mode: Commands are read from a file
 * How to run each mode:
 * - GUI: java CalendarApp --mode gui
 * - Interactive: java CalendarApp --mode interactive
 * - Headless: java CalendarApp --mode headless {file name}
 */
public class CalendarApp {

  /**
   * Initializes and runs the calendar application.
   *
   * @param args command line arguments in either format:
   *             --mode interactive (for interactive mode)
   *             --mode headless {file name} (for headless mode)
   *             --mode gui (for gui mode)
   * @throws IllegalArgumentException if the mode is invalid or arguments are missing
   * @throws RuntimeException         if the file cannot be found in headless mode
   */
  public static void main(String[] args) {
    try {
      validateArguments(args);
      String mode = args[1];
      ICalendarController controller = createController(mode, args);
      controller.execute();
    } catch (Exception e) {
      System.exit(1);
    }
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
              "Invalid arguments. Use --mode followed by 'interactive', 'headless', or 'gui'");
    }
  }

  /**
   * Creates the controller based on the mode.
   *
   * @param mode the mode (interactive, headless, or gui)
   * @param args the command line arguments
   * @return the controller
   * @throws IllegalArgumentException if the mode is invalid
   * @throws RuntimeException         if file cannot be found in headless mode
   */
  private static ICalendarController createController(String mode, String[] args) {
    switch (mode.toLowerCase()) {
      case "interactive":
        return createInteractiveController();
      case "headless":
        return createHeadlessController(args);
      case "gui":
        return createGUIController();
      default:
        throw new IllegalArgumentException("Mode must be 'interactive', 'headless', or 'gui'");
    }
  }

  /**
   * Creates an interactive controller.
   *
   * @return an interactive controller
   */
  private static ICalendarController createInteractiveController() {
    ICalendarManager manager = new CalendarManager();
    ICalendarView calendarView = new CalendarView(System.out);
    Readable readable = new InputStreamReader(System.in);
    return new InteractiveController(manager, calendarView, readable);
  }

  /**
   * Creates a headless controller.
   *
   * @param args the command line arguments containing the filename
   * @return a headless controller
   * @throws RuntimeException if the file is not found or not enough arguments
   */
  private static ICalendarController createHeadlessController(String[] args) {
    if (args.length < 3) {
      throw new IllegalArgumentException(
              "Headless mode requires the following format: java CalendarApp --mode headless " +
                      "{file name}");
    }

    ICalendarManager manager = new CalendarManager();
    ICalendarView calendarView = new CalendarView(System.out);
    File file = new File(args[2]);

    try {
      return new HeadlessController(manager, calendarView, file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + args[2]);
    }
  }

  /**
   * Creates a GUI controller with default calendar setup.
   *
   * @return a GUI controller
   */
  private static ICalendarController createGUIController() {
    ICalendarManager manager = setupDefaultCalendar();
    ICalendarViewGUI viewGUI = new JFrameView();
    ControllerGUI controllerGUI = new ControllerGUI(manager);
    controllerGUI.setView(viewGUI);
    return controllerGUI;
  }

  /**
   * Sets up a default calendar with system timezone.
   *
   * @return configured calendar manager with default calendar
   */
  private static ICalendarManager setupDefaultCalendar() {
    ICalendarManager manager = new CalendarManager();

    // Create a default calendar with system timezone
    manager.createCalendar("MyCalendar", ZoneId.systemDefault());
    manager.useCalendar("MyCalendar");

    return manager;
  }
}