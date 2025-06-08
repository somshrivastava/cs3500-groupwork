package calendar.controller;

import java.util.Scanner;

import calendar.controller.parser.ICommandFactory;
import calendar.controller.parser.SmartCommandParserFactory;
import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Smart interactive controller for calendar management.
 * This controller handles multiple calendars through a CalendarManager and supports
 * both calendar management commands and individual event commands.
 */
public class SmartInteractiveController extends AbstractSmartController {
  private final Readable in;

  /**
   * Create a smart controller to work with the specified calendar manager,
   * readable (to take inputs) and view.
   *
   * @param calendarManager the calendar manager to work with
   * @param view            the calendar view where results are displayed
   * @param in              the readable to take inputs from
   */
  public SmartInteractiveController(ICalendarManager calendarManager, ICalendarView view, Readable in) {
    super(calendarManager, view);
    if (in == null) {
      throw new IllegalArgumentException("Readable input cannot be null");
    }
    this.in = in;
  }

  @Override
  public void execute() {
    Scanner sc = new Scanner(in);
    boolean quit = false;

    // Print the welcome message
    this.calendarView.displayMessage("Welcome to the Smart Calendar Application - Interactive Mode");
    this.calendarView.displayMessage("Available commands: create calendar, edit calendar, use calendar, copy event/events, create event, edit event/events/series, print events, show status");
    this.calendarView.displayMessage("Type 'exit' to quit");
    this.calendarView.displayMessage("");
    this.calendarView.displayPrompt();

    while (!quit && sc.hasNext()) { // Continue until the user quits
      String commandLine = sc.nextLine().trim();

      if (commandLine.equals("exit") || commandLine.equals("q")) {
        // After the user has quit, print farewell message
        this.calendarView.displayMessage("Goodbye");
        quit = true;
      } else if (commandLine.isEmpty()) {
        // Skip empty lines
        this.calendarView.displayPrompt();
      } else {
        try {
          parseCommand(commandLine);
        } catch (Exception e) {
          this.calendarView.displayError(e.getMessage());
        }
        this.calendarView.displayMessage("");
        this.calendarView.displayPrompt(); // Prompt for the next instruction
      }
    }
  }
} 