package calendar.controller;

import java.util.Scanner;

import calendar.controller.parser.CommandParserFactory;
import calendar.controller.parser.ICommandFactory;
import calendar.controller.parser.SmartCommandParserFactory;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * This class represents the controller of a calendar application.
 * This controller offers a simple text interface in which it reads off commands from the user and
 * executes the instructions.
 */
public class InteractiveController extends AbstractController {
  private final Readable in;

  /**
   * Create a controller to work with the specified calendar (model),
   * readable (to take inputs) and view.
   *
   * @param model the calendar to work with (the model)
   * @param view  the calendar view where results are displayed
   * @param in    the readable to take inputs from
   */
  public InteractiveController(ICalendarModel model, ICalendarView view, Readable in) {
    super(model, view);
    if ((model == null) || (view == null) || (in == null)) {
      throw new IllegalArgumentException("model, view or readable is null");
    }
    this.in = in;
  }

  @Override
  protected ICommandFactory createFactory() {
    // as of now, the factory still intakes these parameters to support backward compatibility
    // if the user wants to use the former app, it still works
    // BEWARE, model is null if app2 is run, however, it should never call the method in this class
    // in that case.
    return new CommandParserFactory(this.calendarModel, this.calendarView);
  }

  @Override
  public void execute() {
    Scanner sc = new Scanner(in);
    boolean quit = false;

    //print the welcome message
    this.calendarView.displayMessage("Welcome to the Calendar Application - Interactive Mode");
    this.calendarView.displayMessage("Type 'exit' to quit");
    this.calendarView.displayMessage("");
    this.calendarView.displayPrompt();

    while (!quit && sc.hasNext()) { //continue until the user quits
      String commandLine = sc.nextLine().trim();

      if (commandLine.equals("exit") || commandLine.equals("q")) {
        //after the user has quit, print farewell message
        this.calendarView.displayMessage("Goodbye");
        quit = true;
      } else {
        try {
          parseCommand(commandLine);
        } catch (Exception e) {
          this.calendarView.displayError(e.getMessage());
        }
        this.calendarView.displayMessage("");
        this.calendarView.displayPrompt(); //prompt for the instruction name
      }
    }
  }
}