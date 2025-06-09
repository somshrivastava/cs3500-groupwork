package calendar.controller;

import calendar.controller.parser.ICommandFactory;
import calendar.controller.parser.ICommandParser;
import calendar.controller.parser.SmartCommandParserFactory;
import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * This class represents implementations of a controller of a calendar application.
 */
public abstract class AbstractController implements ICalendarController {
  protected final ICalendarView calendarView;
  protected final ICommandFactory factory;
  protected final ICalendarManager manager;

  protected AbstractController(ICalendarManager manager, ICalendarView calendarView) {
    //this.calendarModel = calendarModel;
    this.calendarView = calendarView;
    this.factory = createFactory();
    this.manager = manager;
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