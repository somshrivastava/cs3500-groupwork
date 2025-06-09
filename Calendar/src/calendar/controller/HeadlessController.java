package calendar.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * This class represents the controller of a calendar application.
 * This controller offers a simple text interface in which it reads off commands from a file and
 * executes the file instructions.
 */
public class HeadlessController extends AbstractController {
  private final File file;

  /**
   * Create a controller to work with the specified calendar (model),
   * readable (to take inputs) and view.
   *
   * @param manager the calendar manager (model)
   * @param view    the calendar view where results are displayed
   * @param file    the file to read commands from
   */
  public HeadlessController(ICalendarManager manager, ICalendarView view, File file)
          throws FileNotFoundException {
    super(manager, view);
    if ((manager == null) || (view == null) || (file == null)) {
      throw new IllegalArgumentException("model, view or readable is null");
    } else if (!file.exists() || !file.canRead()) {
      throw new FileNotFoundException("File does not exist or cannot be read.");
    }
    this.file = file;
  }

  @Override
  public void execute() {
    try (Scanner sc = new Scanner(this.file)) {
      while (sc.hasNext()) {
        String commandLine = sc.nextLine().trim();
        if (commandLine.equals("exit") || commandLine.equals("q")) {
          return;
        } else {
          try {
            parseCommand(commandLine);
          } catch (Exception e) {
            this.calendarView.displayError(e.getMessage());
          }
        }
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + e.getMessage(), e);
    }

    this.calendarView.displayError("No exit command.");
  }
}