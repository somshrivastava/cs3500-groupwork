package calendar.controller;

import calendar.controller.parser.ICommandFactory;
import calendar.controller.parser.ICommandParser;
import calendar.controller.parser.SmartCommandParserFactory;
import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Abstract base class for smart calendar controllers that work with a CalendarManager.
 * This allows managing multiple calendars instead of just a single calendar.
 */
public abstract class AbstractSmartController implements ICalendarController {
  protected final ICalendarManager calendarManager;
  protected final ICalendarView calendarView;
  protected final ICommandFactory factory;

  /**
   * Constructor for smart controllers.
   *
   * @param calendarManager the calendar manager to work with
   * @param calendarView    the view for displaying output
   */
  protected AbstractSmartController(ICalendarManager calendarManager, ICalendarView calendarView) {
    if (calendarManager == null || calendarView == null) {
      throw new IllegalArgumentException("Calendar manager and view cannot be null");
    }
    this.calendarManager = calendarManager;
    this.calendarView = calendarView;
    this.factory = createFactory();
  }

  /**
   * Creates the command parser factory for smart controllers.
   *
   * @return a SmartCommandParserFactory
   */
  protected ICommandFactory createFactory() {
    return new SmartCommandParserFactory(this.calendarManager, this.calendarView);
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