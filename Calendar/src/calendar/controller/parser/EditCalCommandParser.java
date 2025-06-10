package calendar.controller.parser;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for edit calendar commands.
 */
class EditCalCommandParser extends AbstractCommandParser {
  // Specific indices for use command structure
  private static final int MIN_COMMAND_LENGTH = 7;
  private static final int NAME_COMMAND_INDEX = 2;
  private static final int CAL_NAME_INDEX = 3;

  public EditCalCommandParser(ICalendarManager manager, ICalendarView view) {
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

    validateKeyword(commandParts[nameEndIndex + 1], "--property", "'calendar name'");

    // Extract property name
    String propertyName = validateProperty(commandParts[nameEndIndex + 2]);

    // extract new value
    int propertyIndex = nameEndIndex + 3;
    int propertyEndIndex = extractQuotedText(commandParts, propertyIndex);
    String newValue = buildQuotedText(commandParts, propertyIndex, propertyEndIndex);

    manager.editCalendar(calendarName, propertyName, newValue);
  }

  /**
   * Validates create calendar command format.
   */
  private void validateFormat(String[] commandParts) {
    validateMinimumLength(commandParts, MIN_COMMAND_LENGTH, "Invalid use command. " +
            "Format should be: edit calendar --name [name-of-calendar] --property [property-name]" +
            " [new-property-value]");
    validateKeyword(commandParts[COMMAND_SUBTYPE_INDEX], "calendar", "'edit'");
    validateKeyword(commandParts[NAME_COMMAND_INDEX], "--name", "'calendar'");
  }

  /**
   * Validates and returns the property name.
   */
  private String validateProperty(String property) {
    String prop = property.toLowerCase();
    if (!isValidProperty(prop)) {
      throw new IllegalArgumentException("Invalid property '" + property +
              "'. Valid properties are: name or timezone");
    }
    return prop;
  }

  /**
   * Checks if a property name is valid.
   */
  private boolean isValidProperty(String property) {
    return property.equals("name") ||
            property.equals("timezone");
  }
}