package calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

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
 * This application supports two modes as well as the management of multiple calendars:
 * - Interactive mode: User enters commands through the console
 * - Headless mode: Commands are read from a file
 * How to run each mode:
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
              "Application must be ran in the following format: " +
                      "java CalendarApp --mode {'interactive' or 'headless' or 'gui'} {filename}");
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
    ICalendarManager manager = new CalendarManager();
    ICalendarView calendarView = new CalendarView(System.out);

    switch (mode.toLowerCase()) {
      case "interactive":
        return createInteractiveController(manager, calendarView);
      case "headless":
        return createHeadlessController(manager, calendarView, args);
      case "gui":
        // change to interface?
        ICalendarViewGUI viewGUI = new JFrameView();
        ControllerGUI controllerGUI = (ControllerGUI) createGUIController(manager);
        controllerGUI.setView(viewGUI);
        return controllerGUI;
      default:
        throw new IllegalArgumentException("Mode must be 'interactive' or 'headless' or 'gui'");
    }
  }

  /**
   * Creates an interactive controller.
   *
   * @param manager the calendar manager model
   * @param view    the calendar view
   * @return an interactive controller
   */
  private static ICalendarController createInteractiveController(ICalendarManager manager,
                                                                 ICalendarView view) {
    Readable readable = new InputStreamReader(System.in);
    return new InteractiveController(manager, view, readable);
  }

  /**
   * Creates a headless controller.
   *
   * @param manager the calendar model manager
   * @param view    the calendar view
   * @param args    the command line arguments containing the filename
   * @return a headless controller
   * @throws RuntimeException if the file is not found or not enough arguments
   */
  private static ICalendarController createHeadlessController(ICalendarManager manager,
                                                              ICalendarView view,
                                                              String[] args) {
    if (args.length < 3) {
      throw new IllegalArgumentException(
              "Headless mode requires the following format: java CalendarApp --mode headless " +
                      "{file name}");
    }

    File file = new File(args[2]);

    try {
      return new HeadlessController(manager, view, file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + args[2]);
    }
  }

  /**
   * Creates an interactive controller.
   *
   * @param manager the calendar manager model
   * @return a gui controller
   */
  private static ICalendarController createGUIController(ICalendarManager manager) {
    return new ControllerGUI(manager);
  }
}