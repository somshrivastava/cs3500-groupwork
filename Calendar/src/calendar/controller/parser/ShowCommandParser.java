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

  /**
   * Constructs a new ShowCommandParser with the given model and view.
   *
   * @param model the calendar model for checking status
   * @param view  the calendar view for displaying results
   */
  public ShowCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Parses and executes a show status command.
   * Checks if a specific date/time is busy or available.
   *
   * @param parts the command parts array starting with "show"
   * @throws IllegalArgumentException if the command syntax is invalid or execution fails
   */
  @Override
  public void parse(String[] parts) throws IllegalArgumentException {
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