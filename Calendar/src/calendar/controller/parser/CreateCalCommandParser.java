package calendar.controller.parser;

import java.time.ZoneId;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for create calendar commands.
 */
class CreateCalCommandParser extends AbstractCommandParser {
  // Specific indices for use command structure
  private static final int MIN_COMMAND_LENGTH = 6;
  private static final int NAME_COMMAND_INDEX = 2;
  private static final int CAL_NAME_INDEX = 3;

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

    // Extract calendar name
    int nameEndIndex = extractQuotedText(commandParts, CAL_NAME_INDEX);
    String calendarName = buildQuotedText(commandParts, CAL_NAME_INDEX, nameEndIndex);

    validateKeyword(commandParts[nameEndIndex + 1], "--timezone", "'calendar name'");

    // get timezone
    String timeZone = commandParts[nameEndIndex + 2];
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
    validateMinimumLength(commandParts, MIN_COMMAND_LENGTH, "Invalid create command. " +
            "Format should be: create calendar --name [calName] --timezone [area/location]");
    validateKeyword(commandParts[COMMAND_SUBTYPE_INDEX], "calendar", "'create'");
    validateKeyword(commandParts[NAME_COMMAND_INDEX], "--name", "'calendar'");
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