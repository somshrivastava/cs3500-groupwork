package calendar.controller.parser;

import calendar.model.ICalendarManager;
import calendar.model.ISmartCalendarModel;
import calendar.view.ICalendarView;

/**
 * Parser for edit calendar commands.
 * Handles: edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>
 */
class EditCalendarCommandParser extends AbstractCommandParser {
  private static final String NAME_FLAG = "--name";
  private static final String PROPERTY_FLAG = "--property";
  private static final int MIN_COMMAND_LENGTH = 7; // edit calendar --name <name> --property <prop> <value>

  private final ICalendarManager calendarManager;

  public EditCalendarCommandParser(ICalendarManager calendarManager, ICalendarView view) {
    super(null, view); // No individual calendar model needed
    this.calendarManager = calendarManager;
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateMinimumLength(commandParts, MIN_COMMAND_LENGTH, 
        "Invalid edit calendar command. Format: edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>");

    // Validate command structure
    validateKeyword(commandParts[0], "edit", "");
    validateKeyword(commandParts[1], "calendar", "'edit'");

    // Parse flags and values
    String calendarName = null;
    String propertyName = null;
    String newValue = null;

    for (int i = 2; i < commandParts.length; i++) {
      if (commandParts[i].equals(NAME_FLAG)) {
        if (i + 1 >= commandParts.length) {
          throw new IllegalArgumentException("Missing calendar name after --name flag");
        }
        calendarName = commandParts[i + 1];
        i++; // Skip the value
      } else if (commandParts[i].equals(PROPERTY_FLAG)) {
        if (i + 2 >= commandParts.length) {
          throw new IllegalArgumentException("Missing property name and/or value after --property flag");
        }
        propertyName = commandParts[i + 1];
        newValue = commandParts[i + 2];
        i += 2; // Skip both property name and value
      } else if (commandParts[i].startsWith("--")) {
        throw new IllegalArgumentException("Unknown flag: " + commandParts[i] + 
            ". Valid flags are: --name, --property");
      }
    }

    // Validate required parameters
    if (calendarName == null) {
      throw new IllegalArgumentException("Missing required --name flag");
    }
    if (propertyName == null || newValue == null) {
      throw new IllegalArgumentException("Missing required --property flag with property name and value");
    }

    // Validate property name
    if (!propertyName.equals("name") && !propertyName.equals("timezone")) {
      throw new IllegalArgumentException("Invalid property '" + propertyName + 
          "'. Valid properties are: name, timezone");
    }

    // Get the calendar to edit
    ISmartCalendarModel calendar = calendarManager.getCalendar(calendarName);
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar '" + calendarName + "' does not exist");
    }

    // Edit the calendar
    try {
      calendar.editCalendar(calendarName, propertyName, newValue);
      view.displayMessage("Calendar property '" + propertyName + "' updated successfully to '" + newValue + "'");
      
      // If the name was changed, inform the user
      if (propertyName.equals("name")) {
        view.displayMessage("Calendar renamed from '" + calendarName + "' to '" + newValue + "'");
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to edit calendar: " + e.getMessage());
    }
  }
} 