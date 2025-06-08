package calendar.controller.parser;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Parser for show status commands.
 * Checks if the user is busy at a specific time.
 */
class ShowCommandParser extends AbstractCommandParser {
  // Specific indices for show command structure
  private static final int SHOW_COMMAND_LENGTH = 4;
  private static final int STATUS_INDEX = 1;
  private static final int ON_INDEX = 2;
  private static final int DATETIME_INDEX = 3;

  public ShowCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] parts = commandLine.trim().split("\\s+");
    if (parts.length != SHOW_COMMAND_LENGTH) {
      throw new IllegalArgumentException("Show status requires exactly 4 parts. " +
              "Format: show status on YYYY-MM-DDThh:mm");
    }

    // Validate keywords in expected positions
    validateKeyword(parts[STATUS_INDEX], STATUS, "'show'");
    validateKeyword(parts[ON_INDEX], ON, "'show status'");

    // Parse and check the date/time
    LocalDateTime dateTime = parseDateTime(parts[DATETIME_INDEX]);
    boolean isBusy = model.showStatus(dateTime);
    view.displayStatus(parts[DATETIME_INDEX], isBusy);
  }
}