package calendar.controller;

import calendar.controller.parser.CommandParser;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * This class represents implementations of a controller of a calendar application.
 */
public abstract class AbstractController implements ICalendarController {
  protected final ICalendarModel calendarModel;
  protected final ICalendarView calendarView;

  protected AbstractController(ICalendarModel calendarModel, ICalendarView calendarView) {
    this.calendarModel = calendarModel;
    this.calendarView = calendarView;
  }

  /**
   * Takes a command line and parses it, creating the corresponding command or throwing an exception.
   *
   * @param commandLine the line to parse
   */
  protected void parseCommand(String commandLine) {
    CommandParser commandParser = new CommandParser(this.calendarModel, this.calendarView);
    commandParser.parse(commandLine);
  }
}
