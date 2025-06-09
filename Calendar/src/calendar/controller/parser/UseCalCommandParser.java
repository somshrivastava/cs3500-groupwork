package calendar.controller.parser;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for use calendar commands.
 */
class UseCalCommandParser extends AbstractCommandParser {
  // Specific indices for use command structure
  private static final int COMMAND_LENGTH = 4;
  private static final int NAME_COMMAND_INDEX = 2;
  private static final int CAL_NAME_INDEX = 3;

  public UseCalCommandParser(ICalendarManager manager, ICalendarView view) {
    super(manager, view);
    if (manager == null || view == null) {
      throw new IllegalArgumentException("Manager or view is null.");
    }
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateFormat(commandParts);

    // get calendar name
    String calendarName = commandParts[CAL_NAME_INDEX];

    if (this.manager != null) {
      this.manager.useCalendar(calendarName);
    } else {
      throw new IllegalArgumentException("Calendar manager is null.");
    }
  }

  /**
   * Validates use calendar command format.
   */
  private void validateFormat(String[] commandParts) {
    validateMinimumLength(commandParts, COMMAND_LENGTH, "Invalid use command. " +
            "Format should be: use calendar --name [name-of-calendar]");
    validateKeyword(commandParts[COMMAND_SUBTYPE_INDEX], "calendar", "'use'");
    validateKeyword(commandParts[NAME_COMMAND_INDEX], "--name", "'calendar'");
  }
}