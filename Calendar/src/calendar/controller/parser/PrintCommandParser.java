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

  public PrintCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String[] parts) throws IllegalArgumentException {
    validateMinimumLength(parts, 4, "Incomplete print command. Use: " +
            "print events on YYYY-MM-DD OR print events from [start] to [end]");

    validateKeyword(parts[1], EVENTS, "'print'");

    // Check query type and route accordingly
    // parts[2] determines whether single date or range
    if (parts[2].equals(ON)) {
      parsePrintOnDate(parts);
    } else if (parts[2].equals(FROM)) {
      parsePrintDateRange(parts);
    } else {
      throw new IllegalArgumentException("After 'print events', use either 'on' for single date " +
              "or 'from' for date range. Found: '" + parts[2] + "'");
    }
  }

  /**
   * Handles printing events on a specific date.
   */
  private void parsePrintOnDate(String[] parts) {
    if (parts.length != 4) {
      throw new IllegalArgumentException("For 'print events on', provide exactly one date. " +
              "Format: print events on YYYY-MM-DD");
    }

    // fourth word should be the date
    LocalDateTime date = parseDate(parts[3]);
    List<IEvent> events = model.printEvents(date);
    String header = "Events on " + date.toLocalDate().toString();
    view.displayEvents(header, events);
  }

  /**
   * Handles printing events in a date range.
   */
  private void parsePrintDateRange(String[] parts) {
    if (parts.length != 6) {
      throw new IllegalArgumentException("For date range, use: print events from [start] to [end]");
    }

    // 5th word should be "to"
    validateKeyword(parts[4], TO, "dates");

    // 4th word should be start date and 6th word shoule be end date
    LocalDateTime startDate = parseDateTime(parts[3]);
    LocalDateTime endDate = parseDateTime(parts[5]);
    List<IEvent> events = model.printEvents(startDate, endDate);
    String header = "Events from " + startDate.toLocalDate() + " to " + endDate.toLocalDate();
    view.displayEvents(header, events);
  }
}