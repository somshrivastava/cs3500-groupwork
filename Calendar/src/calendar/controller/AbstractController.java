package calendar.controller;

import calendar.controller.parser.ICommandFactory;
import calendar.controller.parser.ICommandParser;
import calendar.controller.parser.SmartCommandParserFactory;
import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Represents the abstract controller for the calendar application.
 * It contains the common setup and methods for the controller.
 * It also contains the abstract methods for creating the controller and converting the input
 * string.
 */
public abstract class AbstractController implements ICalendarController {
  protected final ICalendarView calendarView;
  protected final ICommandFactory factory;
  protected final ICalendarManager manager;

  protected AbstractController(ICalendarManager manager, ICalendarView calendarView) {
    //this.calendarModel = calendarModel;
    this.manager = manager;
    this.calendarView = calendarView;
    this.factory = createFactory();
  }

  /**
   * Creates the corresponding factory for the controller.
   */
  protected ICommandFactory createFactory() {
    return new SmartCommandParserFactory(this.manager, this.calendarView);
  }

  /**
   * Takes a command line and parses it, creating the corresponding command or throwing an
   * exception.
   *
   * @param commandLine the line to parse
   */
  protected void parseCommand(String commandLine) {
    ICommandParser parser = factory.createParser(commandLine);
    parser.parse(commandLine);
  }
}