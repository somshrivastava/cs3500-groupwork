package calendar.controller.parser;

import java.time.LocalDateTime;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Parser for copy event commands.
 * Handles:
 * - copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>
 * - copy events on <dateString> --target <calendarName> to <dateString>
 * - copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
 */
class CopyEventCommandParser extends AbstractCommandParser {
  private static final String TARGET_FLAG = "--target";
  private static final String EVENT = "event";
  private static final String EVENTS = "events";
  private static final String ON = "on";
  private static final String BETWEEN = "between";
  private static final String AND = "and";
  private static final String TO = "to";

  private final ICalendarManager calendarManager;

  public CopyEventCommandParser(ICalendarManager calendarManager, ICalendarView view) {
    super(null, view); // No individual calendar model needed
    this.calendarManager = calendarManager;
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateMinimumLength(commandParts, 4, 
        "Invalid copy command. Use 'copy event' or 'copy events'");

    // Validate command structure
    validateKeyword(commandParts[0], "copy", "");

    String subCommand = commandParts[1].toLowerCase();
    
    if (subCommand.equals(EVENT)) {
      parseCopyEventCommand(commandParts);
    } else if (subCommand.equals(EVENTS)) {
      parseCopyEventsCommand(commandParts);
    } else {
      throw new IllegalArgumentException("Invalid copy command. Use 'copy event' or 'copy events'");
    }
  }

  /**
   * Parses: copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>
   */
  private void parseCopyEventCommand(String[] commandParts) {
    // Minimum: copy event "name" on datetime --target cal to datetime
    validateMinimumLength(commandParts, 8, 
        "Invalid copy event command. Format: copy event <eventName> on <dateTime> --target <calendarName> to <dateTime>");

    // Find event name (may be quoted)
    int nameStartIndex = 2;
    int nameEndIndex = extractQuotedText(commandParts, nameStartIndex);
    String eventName = buildQuotedText(commandParts, nameStartIndex, nameEndIndex);

    // Validate 'on' keyword
    if (nameEndIndex >= commandParts.length) {
      throw new IllegalArgumentException("Missing 'on' keyword after event name");
    }
    validateKeyword(commandParts[nameEndIndex], ON, "event name");

    // Parse source date/time
    int sourceDateTimeIndex = nameEndIndex + 1;
    if (sourceDateTimeIndex >= commandParts.length) {
      throw new IllegalArgumentException("Missing source date/time after 'on'");
    }
    LocalDateTime sourceDateTime = parseDateTime(commandParts[sourceDateTimeIndex]);

    // Find --target flag
    int targetFlagIndex = findTargetFlag(commandParts, sourceDateTimeIndex + 1);
    if (targetFlagIndex == -1 || targetFlagIndex + 1 >= commandParts.length) {
      throw new IllegalArgumentException("Missing --target <calendarName>");
    }
    String targetCalendar = commandParts[targetFlagIndex + 1];

    // Find 'to' keyword
    int toIndex = findToKeyword(commandParts, targetFlagIndex + 2);
    if (toIndex == -1 || toIndex + 1 >= commandParts.length) {
      throw new IllegalArgumentException("Missing 'to <dateTime>' after target calendar");
    }

    // Parse target date/time
    LocalDateTime targetDateTime = parseDateTime(commandParts[toIndex + 1]);

    // Execute copy
    try {
      calendarManager.copyEvent(eventName, sourceDateTime, targetCalendar, targetDateTime);
      view.displayMessage("Event '" + eventName + "' copied successfully to calendar '" + targetCalendar + "'");
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to copy event: " + e.getMessage());
    }
  }

  /**
   * Parses copy events commands (both single date and date range).
   */
  private void parseCopyEventsCommand(String[] commandParts) {
    validateMinimumLength(commandParts, 6,
        "Invalid copy events command");

    String thirdKeyword = commandParts[2].toLowerCase();
    
    if (thirdKeyword.equals(ON)) {
      parseCopyEventsOnDate(commandParts);
    } else if (thirdKeyword.equals(BETWEEN)) {
      parseCopyEventsBetweenDates(commandParts);
    } else {
      throw new IllegalArgumentException("Invalid copy events command. Use 'on <date>' or 'between <date> and <date>'");
    }
  }

  /**
   * Parses: copy events on <dateString> --target <calendarName> to <dateString>
   */
  private void parseCopyEventsOnDate(String[] commandParts) {
    validateMinimumLength(commandParts, 8,
        "Invalid copy events command. Format: copy events on <date> --target <calendarName> to <date>");

    // Parse source date
    LocalDateTime sourceDate = parseDate(commandParts[3]);

    // Find --target flag
    int targetFlagIndex = findTargetFlag(commandParts, 4);
    if (targetFlagIndex == -1 || targetFlagIndex + 1 >= commandParts.length) {
      throw new IllegalArgumentException("Missing --target <calendarName>");
    }
    String targetCalendar = commandParts[targetFlagIndex + 1];

    // Find 'to' keyword
    int toIndex = findToKeyword(commandParts, targetFlagIndex + 2);
    if (toIndex == -1 || toIndex + 1 >= commandParts.length) {
      throw new IllegalArgumentException("Missing 'to <date>' after target calendar");
    }

    // Parse target date
    LocalDateTime targetDate = parseDate(commandParts[toIndex + 1]);

    // Execute copy
    try {
      calendarManager.copyEvents(sourceDate, targetCalendar, targetDate);
      view.displayMessage("Events copied successfully from " + sourceDate.toLocalDate() + 
          " to calendar '" + targetCalendar + "' on " + targetDate.toLocalDate());
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to copy events: " + e.getMessage());
    }
  }

  /**
   * Parses: copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
   */
  private void parseCopyEventsBetweenDates(String[] commandParts) {
    validateMinimumLength(commandParts, 10,
        "Invalid copy events command. Format: copy events between <date> and <date> --target <calendarName> to <date>");

    // Parse start date
    LocalDateTime startDate = parseDate(commandParts[3]);

    // Validate 'and' keyword
    validateKeyword(commandParts[4], AND, "start date");

    // Parse end date
    LocalDateTime endDate = parseDate(commandParts[5]);

    // Find --target flag
    int targetFlagIndex = findTargetFlag(commandParts, 6);
    if (targetFlagIndex == -1 || targetFlagIndex + 1 >= commandParts.length) {
      throw new IllegalArgumentException("Missing --target <calendarName>");
    }
    String targetCalendar = commandParts[targetFlagIndex + 1];

    // Find 'to' keyword
    int toIndex = findToKeyword(commandParts, targetFlagIndex + 2);
    if (toIndex == -1 || toIndex + 1 >= commandParts.length) {
      throw new IllegalArgumentException("Missing 'to <date>' after target calendar");
    }

    // Parse target date
    LocalDateTime targetDate = parseDate(commandParts[toIndex + 1]);

    // Execute copy
    try {
      calendarManager.copyEvents(startDate, endDate, targetCalendar, targetDate);
      view.displayMessage("Events copied successfully from " + startDate.toLocalDate() + 
          " to " + endDate.toLocalDate() + " to calendar '" + targetCalendar + "' starting " + targetDate.toLocalDate());
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to copy events: " + e.getMessage());
    }
  }

  /**
   * Finds the --target flag in the command parts.
   */
  private int findTargetFlag(String[] commandParts, int startIndex) {
    for (int i = startIndex; i < commandParts.length; i++) {
      if (commandParts[i].equals(TARGET_FLAG)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Finds the 'to' keyword in the command parts.
   */
  private int findToKeyword(String[] commandParts, int startIndex) {
    for (int i = startIndex; i < commandParts.length; i++) {
      if (commandParts[i].equalsIgnoreCase(TO)) {
        return i;
      }
    }
    return -1;
  }
} 