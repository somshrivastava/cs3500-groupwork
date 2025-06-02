package calendar.controller;

import java.io.IOException;
import java.util.Scanner;

import calendar.controller.commands.CreateEventCommand;
import calendar.controller.commands.ICalendarCommand;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

public class CalendarController implements ICalendarController {
  private final ICalendarModel calendarModel;
  private final ICalendarView calendarView;
  private final Readable in;

  public CalendarController(ICalendarModel model, ICalendarView view,
                            Readable in) {
    this.calendarModel = model;
    this.calendarView = view;
    this.in = in;
  }

  @Override
  public void go(String mode, String filename) throws IllegalArgumentException {
    if (mode == null) {
      throw new IllegalArgumentException("Mode cannot be null.");
    }

    try {
      switch (mode.toLowerCase()) {
        case "interactive":
          this.runInteractiveMode();
          break;
        case "headless":
          if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null in headless mode.");
          }
          this.runHeadlessMode();
          break;
        default:
          throw new IllegalArgumentException("Invalid mode: " + mode);
      }
    } catch (IOException error) {
      throw new IllegalArgumentException("IO error occurred: " + error.getMessage());
    }
  }

  private ICalendarCommand parseCommand(String commandLine) {
    Scanner scanner = new Scanner(commandLine);

    String action = scanner.next();

    switch (action) {
      case "create":
        return new CreateEventCommand(commandLine);
      case "edit":
        break;
      case "print":
        break;
      case "show":
        break;
      default:
        break;
    }

    return null;
  }

  public void runInteractiveMode() throws IOException {
    this.calendarView.displayMessage("Welcome to the Calendar Application - Interactive Mode");
    this.calendarView.displayMessage("Type 'exit' to quit");
    this.calendarView.displayBlankLine();

    Scanner scanner = new Scanner(this.in);

    while (scanner.hasNextLine()) {
      this.calendarView.displayPrompt();

      String commandLine = scanner.nextLine().trim();

      if (commandLine.equalsIgnoreCase("exit")) {
        this.calendarView.displayMessage("Goodbye");
        break;
      }

      if (commandLine.isEmpty()) {
        continue;
      }

      try {
        ICalendarCommand command = this.parseCommand(commandLine);
        command.execute(this.calendarModel);
      } catch (Exception e) {
        this.calendarView.displayError(e.getMessage());
        System.exit(1);
      }

      this.calendarView.displayBlankLine();
    }
  }

  public void runHeadlessMode() throws IOException {

  }
}
