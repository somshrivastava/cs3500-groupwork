package calendar.controller;

import java.util.Scanner;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * This class represents the controller of an interactive calendar application.
 * This controller offers a simple text interface in which the user can
 * type instructions to edit a calendar.
 * This controller works with any Readable to read its inputs.
 */
public class InteractiveController extends AbstractController {
  private final ICalendarModel calendarModel;
  private final ICalendarView calendarView;
  private final Readable in;

  /**
   * Create a controller to work with the specified calendar (model),
   * readable (to take inputs) and view.
   *
   * @param model the calendar to work with (the model)
   * @param view  the calendar view where results are displayed
   * @param in    the Readable object for inputs
   */
  public InteractiveController(ICalendarModel model, ICalendarView view, Readable in) {
    if ((model == null) || (view == null) || (in == null)) {
      throw new IllegalArgumentException("model, view or readable is null");
    }
    this.calendarModel = model;
    this.calendarView = view;
    this.in = in;
  }

  @Override
  public void go() {
    Scanner sc = new Scanner(in);
    boolean quit = false;

    //print the welcome message
    this.calendarView.displayMessage("Welcome to the Calendar Application - Interactive Mode");
    this.calendarView.displayMessage("Type 'exit' to quit");
    this.calendarView.displayBlankLine();

    while (!quit && sc.hasNext()) { //continue until the user quits
      this.calendarView.displayPrompt(); //prompt for the instruction name
      //String commandLine = sc.next(); //take an instruction name
      String commandLine = sc.nextLine().trim();

      if (commandLine.equals("exit") || commandLine.equals("q")) {
        //after the user has quit, print farewell message
        this.calendarView.displayMessage("Goodbye");
        quit = true;
      } else {
        //processCommand(userInstruction, sc, sheet);
        try {
          parseCommand(commandLine);
        } catch (Exception e) {
          this.calendarView.displayError(e.getMessage());
          //System.exit(1);
        }
        this.calendarView.displayBlankLine();
      }
    }
  }
}
