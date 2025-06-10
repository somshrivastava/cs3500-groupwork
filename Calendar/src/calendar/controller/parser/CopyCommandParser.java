package calendar.controller.parser;

import java.time.LocalDateTime;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for copy commands.
 */
class CopyCommandParser extends AbstractCommandParser {
  // Specific indices for use command structure
  private static final int MIN_COMMAND_LENGTH = 8;
  private static final int NAME_COMMAND_INDEX = 2;
  private static final int CAL_NAME_INDEX = 3;

  public CopyCommandParser(ICalendarManager manager, ICalendarView view) {
    super(manager, view);
    if (manager == null || view == null) {
      throw new IllegalArgumentException("Manager or view is null.");
    }
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateMinimumLength(commandParts, MIN_COMMAND_LENGTH, "Incomplete copy command. Format: " +
            "copy events on [date] --target [calendarName] to [date]");

    String copyType = validateCopyType(commandParts[COMMAND_SUBTYPE_INDEX]);

    // parse based on copy type
    if (copyType.equals(EVENT)) {
      parseCopySingleEvent(commandParts);
    } else {
      parseCopyMultipleEvent(commandParts);
    }
  }

  /**
   * Validates and returns the copy type.
   */
  private String validateCopyType(String copyType) {
    String type = copyType.toLowerCase();
    if (!type.equals(EVENT) && !type.equals(EVENTS)) {
      throw new IllegalArgumentException("Invalid copy type '" + copyType +
              "'. Must be 'event' or 'events'");
    }
    return type;
  }

  /**
   * Parses single event copy command.
   */
  private void parseCopySingleEvent(String[] parts) {
    final int eventNameIndex = 2;

    // find event name
    int eventEndIndex = extractQuotedText(parts, eventNameIndex);
    String eventName = buildQuotedText(parts, eventNameIndex, eventEndIndex);

    // Validate we have the "on" keyword where expected
    validateKeyword(parts[eventEndIndex], ON, "event name");

    // Parse the date (should be dateStringTtimeString format)
    LocalDateTime originalDate = parseDateTime(parts[eventEndIndex + 1]);

    // Validate the --target keyword appears before the calendar name
    validateKeyword(parts[eventEndIndex + 2], "--target", "date");

    // Get the new calendar name
    int calendarNameIndex = eventEndIndex + 3;
    int nameEndIndex = extractQuotedText(parts, calendarNameIndex);
    String calendarName = buildQuotedText(parts, calendarNameIndex, nameEndIndex);

    // Validate we have the "to" keyword where expected
    validateKeyword(parts[nameEndIndex], TO, "calendar name");

    // Parse the new date (should be dateStringTtimeString format)
    LocalDateTime newDate = parseDateTime(parts[nameEndIndex + 1]);

    // Call manager to copy event
    manager.copyEvent(eventName, originalDate, calendarName, newDate);
  }

  /**
   * Parses multiple events copy command.
   */
  private void parseCopyMultipleEvent(String[] parts) {
    final int keyWord = 2;
    final int date1Offset = 1;
    int targetOffset = 0;
    LocalDateTime date2 = null;

    // check which copy command and parse accordingly
    if (parts[keyWord].equals("on")) {
      // Parse the first date (dateString format)
      LocalDateTime date1 = parseDate(parts[keyWord + date1Offset]);
      targetOffset = 2;
      
      // Validate --target keyword
      validateKeyword(parts[keyWord + targetOffset], "--target", "date");
      
      // Get the calendar name
      int calendarNameIndex = keyWord + targetOffset + 1;
      int nameEndIndex = extractQuotedText(parts, calendarNameIndex);
      String calendarName = buildQuotedText(parts, calendarNameIndex, nameEndIndex);

      // Validate we have the "to" keyword where expected
      validateKeyword(parts[nameEndIndex], TO, "calendar name");

      // Parse the last date (dateString format)
      LocalDateTime lastDate = parseDate(parts[nameEndIndex + 1]);
      
      manager.copyEventsOnDate(date1, calendarName, lastDate);
    }
    else if (parts[keyWord].equals("between")) {
      // Parse the first date (dateString format)
      LocalDateTime date1 = parseDate(parts[keyWord + date1Offset]);
      targetOffset = 4;
      final int andOffset = 2;
      validateKeyword(parts[keyWord + andOffset], "and", "date");
      // get date2 (dateString format)
      date2 = parseDate(parts[keyWord + andOffset + 1]);
      
      // Validate --target keyword
      validateKeyword(parts[keyWord + targetOffset], "--target", "second date");
      
      // Get the calendar name
      int calendarNameIndex = keyWord + targetOffset + 1;
      int nameEndIndex = extractQuotedText(parts, calendarNameIndex);
      String calendarName = buildQuotedText(parts, calendarNameIndex, nameEndIndex);

      // Validate we have the "to" keyword where expected
      validateKeyword(parts[nameEndIndex], TO, "calendar name");

      // Parse the last date (dateString format)
      LocalDateTime lastDate = parseDate(parts[nameEndIndex + 1]);
      
      manager.copyEventsBetweenDates(date1, date2, calendarName, lastDate);
    }
    else {
      throw new IllegalArgumentException("Invalid copy command. Format: " +
              "copy events on [date] --target [calendarName] to [date] or " +
              "copy events between [date] and [date] --target [calendarName] to [date]");
    }
  }
}