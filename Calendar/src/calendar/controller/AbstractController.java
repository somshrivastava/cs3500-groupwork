package calendar.controller;

import calendar.controller.parser.CommandParserFactory;
import calendar.controller.parser.ICommandParser;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * This class represents implementations of a controller of a calendar application.
 */
public abstract class AbstractController implements ICalendarController {
  protected final ICalendarModel calendarModel;
  protected final ICalendarView calendarView;
  protected final CommandParserFactory factory;

  protected AbstractController(ICalendarModel calendarModel, ICalendarView calendarView) {
    this.calendarModel = calendarModel;
    this.calendarView = calendarView;
    this.factory = new CommandParserFactory(calendarModel, calendarView);
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