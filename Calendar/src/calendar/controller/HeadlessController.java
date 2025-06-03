package calendar.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import calendar.controller.commands.ICalendarCommand;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * This class represents the controller of a calendar application.
 * This controller offers a simple text interface in which it reads off commands from a file and
 * executes the file instructions.
 */
public class HeadlessController extends AbstractController {
  private final ICalendarModel calendarModel;
  private final ICalendarView calendarView;
  private final File file;

  /**
   * Create a controller to work with the specified calendar (model),
   * readable (to take inputs) and view.
   *
   * @param model the calendar to work with (the model)
   * @param view  the calendar view where results are displayed
   * @param file  the file to read commands from
   */
  public HeadlessController(ICalendarModel model, ICalendarView view, File file)
          throws FileNotFoundException {
    if ((model == null) || (view == null) || (file == null)) {
      throw new IllegalArgumentException("model, view or readable is null");
    } else if (!file.exists() || !file.canRead()) {
      throw new FileNotFoundException("File does not exist or cannot be read.");
    }
    this.calendarModel = model;
    this.calendarView = view;
    this.file = file;
  }

  @Override
  public void go() {
    Scanner sc;
    try {
      sc = new Scanner(this.file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    while (sc.hasNext()) {
      String commandLine = sc.nextLine().trim();
      if (commandLine.equals("exit") || commandLine.equals("q")) {
        System.exit(0);
      } else {
        try {
          ICalendarCommand command = parseCommand(commandLine);
          command.execute(this.calendarModel);
        } catch (Exception e) {
          this.calendarView.displayError(e.getMessage());
        }
      }
    }
    this.calendarView.displayError("No exit command.");
    System.exit(1);
  }
}
