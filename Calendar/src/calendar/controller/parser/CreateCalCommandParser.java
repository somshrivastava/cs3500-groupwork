package calendar.controller.parser;

import java.time.ZoneId;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for create calendar commands.
 */
class CreateCalCommandParser extends AbstractCommandParser {
  // Specific indices for use command structure
  private static final int COMMAND_LENGTH = 6;
  private static final int NAME_COMMAND_INDEX = 2;
  private static final int CAL_NAME_INDEX = 3;
  private static final int TIMEZONE_COMMAND_INDEX = 4;
  private static final int TIMEZONE_INDEX = 5;

  public CreateCalCommandParser(ICalendarManager manager, ICalendarView view) {
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

    // get timezone
    String timeZone = commandParts[TIMEZONE_INDEX];
    if (!validateTimeZone(timeZone)) {
      throw new IllegalArgumentException("Invalid region time zone.");
    }

    if (this.manager != null) {
      ZoneId zone = ZoneId.of(timeZone);
      this.manager.createCalendar(calendarName, zone);
    } else {
      throw new IllegalArgumentException("Calendar manager is null.");
    }
  }

  /**
   * Validates create calendar command format.
   */
  private void validateFormat(String[] commandParts) {
    validateMinimumLength(commandParts, COMMAND_LENGTH, "Invalid use command. " +
            "Format should be: create calendar --name [calName] --timezone [area/location]");
    validateKeyword(commandParts[COMMAND_SUBTYPE_INDEX], "calendar", "'create'");
    validateKeyword(commandParts[NAME_COMMAND_INDEX], "--name", "'calendar'");
    validateKeyword(commandParts[TIMEZONE_COMMAND_INDEX], "--timezone", "'calendar name'");
  }

  /**
   * Validates whether the provided String is an existing timezone in the correct format.
   *
   * @param timeZone the time zone to check
   * @return true if the time zone is valid, false otherwise
   */
  private boolean validateTimeZone(String timeZone) {
    return ZoneId.getAvailableZoneIds().contains(timeZone);
  }
}