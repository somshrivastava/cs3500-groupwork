package calendar.controller.parser;

import java.time.LocalDateTime;
import java.util.List;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarView;

/**
 * Parser for print events commands.
 * Handles printing events on a specific date or within a date range.
 */
class PrintCommandParser extends AbstractCommandParser {
  // Specific indices for print command structure
  private static final int MIN_PRINT_COMMAND_LENGTH = 4;
  private static final int EVENTS_INDEX = 1;
  private static final int QUERY_TYPE_INDEX = 2;
  private static final int DATE_INDEX = 3;

  // For date range queries
  private static final int TO_KEYWORD_INDEX = 4;
  private static final int END_DATE_INDEX = 5;
  private static final int DATE_RANGE_LENGTH = 6;

  /**
   * Constructs a new PrintCommandParser with the given model and view.
   *
   * @param model the calendar model for querying events
   * @param view  the calendar view for displaying results
   */
  public PrintCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Parses and executes a print command.
   * Supports printing events for a single date or date range.
   *
   * @param parts the command parts array starting with "print"
   * @throws IllegalArgumentException if the command syntax is invalid or execution fails
   */
  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] parts = commandLine.trim().split("\\s+");
    validateMinimumLength(parts, MIN_PRINT_COMMAND_LENGTH, "Incomplete print command. Use: " +
            "print events on YYYY-MM-DD OR print events from [start] to [end]");

    validateKeyword(parts[EVENTS_INDEX], EVENTS, "'print'");

    // Check query type and route accordingly
    String queryType = parts[QUERY_TYPE_INDEX];
    if (queryType.equalsIgnoreCase(ON)) {
      parsePrintOnDate(parts);
    } else if (queryType.equalsIgnoreCase(FROM)) {
      parsePrintDateRange(parts);
    } else {
      throw new IllegalArgumentException("After 'print events', use either 'on' for single date " +
              "or 'from' for date range. Found: '" + queryType + "'");
    }
  }

  /**
   * Handles printing events on a specific date.
   */
  private void parsePrintOnDate(String[] parts) {
    if (parts.length != MIN_PRINT_COMMAND_LENGTH) {
      throw new IllegalArgumentException("For 'print events on', provide exactly one date. " +
              "Format: print events on YYYY-MM-DD");
    }

    LocalDateTime date = parseDate(parts[DATE_INDEX]);
    List<IEvent> events = model.printEvents(date);
    view.displayEventsForDate(date.toLocalDate(), events);
  }

  /**
   * Handles printing events in a date range.
   */
  private void parsePrintDateRange(String[] parts) {
    if (parts.length != DATE_RANGE_LENGTH) {
      throw new IllegalArgumentException("For date range, use: print events from [start] to [end]");
    }

    validateKeyword(parts[TO_KEYWORD_INDEX], TO, "dates");

    LocalDateTime startDate = parseDateTime(parts[DATE_INDEX]);
    LocalDateTime endDate = parseDateTime(parts[END_DATE_INDEX]);
    List<IEvent> events = model.printEvents(startDate, endDate);
    view.displayEventsForDateRange(startDate, endDate, events);
  }
}