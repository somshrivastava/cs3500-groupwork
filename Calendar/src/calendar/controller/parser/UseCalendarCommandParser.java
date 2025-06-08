package calendar.controller.parser;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for use calendar commands.
 * Handles: use calendar --name <name-of-calendar>
 */
class UseCalendarCommandParser extends AbstractCommandParser {
  private static final String NAME_FLAG = "--name";
  private static final int MIN_COMMAND_LENGTH = 4; // use calendar --name <name>

  private final ICalendarManager calendarManager;

  public UseCalendarCommandParser(ICalendarManager calendarManager, ICalendarView view) {
    super(null, view); // No individual calendar model needed
    this.calendarManager = calendarManager;
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateMinimumLength(commandParts, MIN_COMMAND_LENGTH, 
        "Invalid use calendar command. Format: use calendar --name <name-of-calendar>");

    // Validate command structure
    validateKeyword(commandParts[0], "use", "");
    validateKeyword(commandParts[1], "calendar", "'use'");

    // Parse flags and values
    String calendarName = null;

    for (int i = 2; i < commandParts.length; i++) {
      if (commandParts[i].equals(NAME_FLAG)) {
        if (i + 1 >= commandParts.length) {
          throw new IllegalArgumentException("Missing calendar name after --name flag");
        }
        calendarName = commandParts[i + 1];
        i++; // Skip the value
      } else if (commandParts[i].startsWith("--")) {
        throw new IllegalArgumentException("Unknown flag: " + commandParts[i] + 
            ". Valid flag is: --name");
      } else {
        throw new IllegalArgumentException("Unexpected parameter: " + commandParts[i] + 
            ". Use --name flag to specify calendar name");
      }
    }

    // Validate required parameters
    if (calendarName == null) {
      throw new IllegalArgumentException("Missing required --name flag");
    }

    // Switch to the calendar
    try {
      calendarManager.useCalendar(calendarName);
      view.displayMessage("Now using calendar: " + calendarName);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to switch calendar: " + e.getMessage());
    }
  }
} 