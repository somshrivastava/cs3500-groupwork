package calendar.controller.parser;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for create calendar commands.
 * Handles: create calendar --name <calName> --timezone area/location
 */
class CreateCalendarCommandParser extends AbstractCommandParser {
  private static final String NAME_FLAG = "--name";
  private static final String TIMEZONE_FLAG = "--timezone";
  private static final int MIN_COMMAND_LENGTH = 6; // create calendar --name <name> --timezone <tz>

  private final ICalendarManager calendarManager;

  public CreateCalendarCommandParser(ICalendarManager calendarManager, ICalendarView view) {
    super(null, view); // No individual calendar model needed
    this.calendarManager = calendarManager;
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateMinimumLength(commandParts, MIN_COMMAND_LENGTH, 
        "Invalid create calendar command. Format: create calendar --name <calName> --timezone <area/location>");

    // Validate command structure
    validateKeyword(commandParts[0], "create", "");
    validateKeyword(commandParts[1], "calendar", "'create'");

    // Parse flags and values
    String calendarName = null;
    String timezoneStr = null;

    for (int i = 2; i < commandParts.length; i++) {
      if (commandParts[i].equals(NAME_FLAG)) {
        if (i + 1 >= commandParts.length) {
          throw new IllegalArgumentException("Missing calendar name after --name flag");
        }
        calendarName = commandParts[i + 1];
        i++; // Skip the value
      } else if (commandParts[i].equals(TIMEZONE_FLAG)) {
        if (i + 1 >= commandParts.length) {
          throw new IllegalArgumentException("Missing timezone after --timezone flag");
        }
        timezoneStr = commandParts[i + 1];
        i++; // Skip the value
      } else if (commandParts[i].startsWith("--")) {
        throw new IllegalArgumentException("Unknown flag: " + commandParts[i] + 
            ". Valid flags are: --name, --timezone");
      }
    }

    // Validate required parameters
    if (calendarName == null) {
      throw new IllegalArgumentException("Missing required --name flag");
    }
    if (timezoneStr == null) {
      throw new IllegalArgumentException("Missing required --timezone flag");
    }

    // Parse timezone
    ZoneId timezone;
    try {
      timezone = ZoneId.of(timezoneStr);
    } catch (ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid timezone: " + timezoneStr + 
          ". Expected IANA format like 'America/New_York'");
    }

    // Create the calendar
    try {
      calendarManager.createCalendar(calendarName, timezone);
      view.displayMessage("Calendar '" + calendarName + "' created successfully with timezone " + timezone);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to create calendar: " + e.getMessage());
    }
  }
} 