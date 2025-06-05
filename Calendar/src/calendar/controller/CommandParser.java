package calendar.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarView;

/**
 * Parser for calendar commands. Handles parsing user input and calling appropriate model methods.
 */
public class CommandParser {
  // Formatters for parsing dates and times in ISO format
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  // Mapping of character codes to days of week
  private static final Map<Character, DayOfWeek> WEEKDAY_MAP = new HashMap<>();

  // Command keywords
  private static final String CREATE = "create";
  private static final String EDIT = "edit";
  private static final String PRINT = "print";
  private static final String SHOW = "show";
  private static final String EVENT = "event";
  private static final String EVENTS = "events";
  private static final String SERIES = "series";
  private static final String FROM = "from";
  private static final String TO = "to";
  private static final String ON = "on";
  private static final String WITH = "with";
  private static final String REPEATS = "repeats";
  private static final String FOR = "for";
  private static final String UNTIL = "until";
  private static final String TIMES = "times";
  private static final String STATUS = "status";

  private final ICalendarModel model;
  private final ICalendarView view;

  static {
    WEEKDAY_MAP.put('M', DayOfWeek.MONDAY);
    WEEKDAY_MAP.put('T', DayOfWeek.TUESDAY);
    WEEKDAY_MAP.put('W', DayOfWeek.WEDNESDAY);
    WEEKDAY_MAP.put('R', DayOfWeek.THURSDAY);
    WEEKDAY_MAP.put('F', DayOfWeek.FRIDAY);
    WEEKDAY_MAP.put('S', DayOfWeek.SATURDAY);
    WEEKDAY_MAP.put('U', DayOfWeek.SUNDAY);
  }

  /**
   * Constructs a new CommandParser with the given calendar model and view.
   * @param model the calendar model to use for event operations
   * @param view the calendar view to use for displaying events and messages
   */
  public CommandParser(ICalendarModel model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Main entry point for parsing commands. Routes to appropriate command handler.
   *
   * @param commandLine the command line to parse
   * @throws IllegalArgumentException if the command line is invalid or malformed
   */
  public void parse(String commandLine) throws IllegalArgumentException {
    validateCommandNotEmpty(commandLine);

    String[] commandParts = commandLine.trim().split("\\s+");
    String commandType = commandParts[0].toLowerCase();

    switch (commandType) {
      case CREATE:
        parseCreateCommand(commandParts);
        break;
      case EDIT:
        parseEditCommand(commandParts);
        break;
      case PRINT:
        parsePrintCommand(commandParts);
        break;
      case SHOW:
        parseShowCommand(commandParts);
        break;
      default:
        throw new IllegalArgumentException("Unknown command: '" + commandType +
                "'. Valid commands are: create, edit, print, show");
    }
  }

  /**
   * Handles the "create event" command for both timed and all-day events.
   */
  private void parseCreateCommand(String[] commandParts) throws IllegalArgumentException {
    validateCreateCommandFormat(commandParts);

    int subjectEndIndex = extractQuotedText(commandParts, 2);
    String subject = buildQuotedText(commandParts, 2, subjectEndIndex);

    String eventType = getEventTypeKeyword(commandParts, subjectEndIndex);

    String[] remainingParts = Arrays.copyOfRange(commandParts, subjectEndIndex + 1, commandParts.length);

    if (eventType.equals(FROM)) {
      parseTimedEvent(subject, remainingParts);
    } else {
      parseAllDayEvent(subject, remainingParts);
    }
  }

  /**
   * Parses a timed event (has start and end times).
   */
  private void parseTimedEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateMinimumLength(remainingParts, 3, "Incomplete timed event. Format: " +
            "from YYYY-MM-DDThh:mm to YYYY-MM-DDThh:mm");

    LocalDateTime startTime = parseDateTime(remainingParts[0]);
    validateKeyword(remainingParts[1], TO, "start and end times");
    LocalDateTime endTime = parseDateTime(remainingParts[2]);

    if (remainingParts.length == 3) {
      model.createSingleTimedEvent(subject, startTime, endTime);
    } else {
      parseRecurringTimedEvent(subject, startTime, endTime, remainingParts);
    }
  }

  /**
   * Parses an all-day event.
   */
  private void parseAllDayEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateMinimumLength(remainingParts, 1, "Missing date for all-day event. Format: on YYYY-MM-DD");

    LocalDateTime eventDate = parseDate(remainingParts[0]);

    if (remainingParts.length == 1) {
      model.createSingleAllDayEvent(subject, eventDate);
    } else {
      parseRecurringAllDayEvent(subject, eventDate, remainingParts);
    }
  }

  /**
   * Parses recurring options for a timed event.
   */
  private void parseRecurringTimedEvent(String subject, LocalDateTime startTime,
                                        LocalDateTime endTime, String[] parts) {
    validateKeyword(parts[3], REPEATS, "end time");

    int weekdaysIndex = 3 + 1;
    validateMinimumLength(parts, weekdaysIndex + 3, "Incomplete recurring event");

    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[weekdaysIndex]);
    String recurType = parts[weekdaysIndex + 1].toLowerCase();

    if (recurType.equals(FOR)) {
      int count = parseCount(parts[weekdaysIndex + 2]);
      validateTimesKeyword(parts, weekdaysIndex + 3);
      model.createRecurringTimedEvent(subject, startTime, endTime, weekdays, count);
    } else if (recurType.equals(UNTIL)) {
      LocalDateTime untilDate = parseDate(parts[weekdaysIndex + 2]);
      model.createRecurringTimedEventUntil(subject, startTime, endTime, weekdays, untilDate);
    } else {
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[weekdaysIndex + 1] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }

  /**
   * Parses recurring options for an all-day event.
   */
  private void parseRecurringAllDayEvent(String subject, LocalDateTime eventDate,
                                         String[] parts) {
    validateKeyword(parts[1], REPEATS, "date");

    int weekdaysIndex = 1 + 1;
    validateMinimumLength(parts, weekdaysIndex + 3, "Incomplete recurring event");

    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[weekdaysIndex]);
    String recurType = parts[weekdaysIndex + 1].toLowerCase();

    if (recurType.equals(FOR)) {
      int count = parseCount(parts[weekdaysIndex + 2]);
      validateTimesKeyword(parts, weekdaysIndex + 3);
      model.createRecurringAllDayEvent(subject, eventDate, weekdays, count);
    } else if (recurType.equals(UNTIL)) {
      LocalDateTime untilDate = parseDate(parts[weekdaysIndex + 2]);
      model.createRecurringAllDayEventUntil(subject, eventDate, weekdays, untilDate);
    } else {
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[weekdaysIndex + 1] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }

  /**
   * Parses an edit command.
   */
  private void parseEditCommand(String[] parts) throws IllegalArgumentException {
    validateMinimumLength(parts, 7, "Incomplete edit command. Format: " +
            "edit event [property] \"subject\" from [start] to [end] with [value]");

    String editType = validateEditType(parts[1]);
    String property = validateProperty(parts[2]);

    int subjectEndIndex = extractQuotedText(parts, 3);
    String subject = buildQuotedText(parts, 3, subjectEndIndex);

    validateKeyword(parts[subjectEndIndex], FROM, "subject");

    LocalDateTime startTime = parseDateTime(parts[subjectEndIndex + 1]);

    if (editType.equals(EVENT)) {
      parseEditSingleEvent(parts, subjectEndIndex + 2, subject, startTime, property);
    } else {
      parseEditSeriesEvent(parts, subjectEndIndex + 2, subject, startTime, property, editType);
    }
  }

  /**
   * Parses the remaining parts of a single event edit command.
   */
  private void parseEditSingleEvent(String[] parts, int index, String subject,
                                    LocalDateTime startTime, String property) {
    validateKeyword(parts[index], TO, "start time in single event edit");

    LocalDateTime endTime = parseDateTime(parts[index + 1]);

    validateKeyword(parts[index + 2], WITH, "new value");

    int valueIndex = index + 3;
    int valueEndIndex = extractQuotedText(parts, valueIndex);
    String newValue = buildQuotedText(parts, valueIndex, valueEndIndex);

    model.editEvent(subject, startTime, endTime, property, newValue);
  }

  /**
   * Parses the remaining parts of a series edit command.
   */
  private void parseEditSeriesEvent(String[] parts, int index, String subject,
                                    LocalDateTime startTime, String property, String editType) {
    validateKeyword(parts[index], WITH, "new value");

    int valueIndex = index + 1;
    int valueEndIndex = extractQuotedText(parts, valueIndex);
    String newValue = buildQuotedText(parts, valueIndex, valueEndIndex);

    if (editType.equals(EVENTS)) {
      model.editEvents(subject, startTime, property, newValue);
    } else {
      model.editSeries(subject, startTime, property, newValue);
    }
  }

  /**
   * Parses a print command.
   */
  private void parsePrintCommand(String[] parts) throws IllegalArgumentException {
    validateMinimumLength(parts, 4, "Incomplete print command. Use: " +
            "print events on YYYY-MM-DD OR print events from [start] to [end]");

    validateKeyword(parts[1], EVENTS, "'print'");

    if (parts[2].equals(ON)) {
      parsePrintOnDate(parts);
    } else if (parts[2].equals(FROM)) {
      parsePrintDateRange(parts);
    } else {
      throw new IllegalArgumentException("After 'print events', use either 'on' for single date " +
              "or 'from' for date range. Found: '" + parts[2] + "'");
    }
  }

  /**
   * Handles printing events on a specific date.
   */
  private void parsePrintOnDate(String[] parts) {
    if (parts.length != 4) {
      throw new IllegalArgumentException("For 'print events on', provide exactly one date. " +
              "Format: print events on YYYY-MM-DD");
    }

    LocalDateTime date = parseDate(parts[3]);
    List<IEvent> events = model.printEvents(date);
    String header = "Events on " + date.toLocalDate().toString();
    view.displayEvents(header, events);
  }

  /**
   * Handles printing events in a date range.
   */
  private void parsePrintDateRange(String[] parts) {
    if (parts.length != 6) {
      throw new IllegalArgumentException("For date range, use: print events from [start] to [end]");
    }

    validateKeyword(parts[4], TO, "dates");

    LocalDateTime startDate = parseDateTime(parts[3]);
    LocalDateTime endDate = parseDateTime(parts[5]);
    List<IEvent> events = model.printEvents(startDate, endDate);
    String header = "Events from " + startDate.toLocalDate() + " to " + endDate.toLocalDate();
    view.displayEvents(header, events);
  }

  /**
   * Parses a show status command.
   */
  private void parseShowCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length != 4) {
      throw new IllegalArgumentException("Show status requires exactly 4 parts. " +
              "Format: show status on YYYY-MM-DDThh:mm");
    }

    validateKeyword(parts[1], STATUS, "'show'");
    validateKeyword(parts[2], ON, "'show status'");

    LocalDateTime dateTime = parseDateTime(parts[3]);
    boolean isBusy = model.showStatus(dateTime);
    view.displayStatus(parts[3], isBusy);
  }

  /**
   * Validates that a command is not null or empty.
   */
  private void validateCommandNotEmpty(String commandLine) {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty. Please enter a valid command.");
    }
  }

  /**
   * Validates the basic structure of a create command.
   */
  private void validateCreateCommandFormat(String[] commandParts) {
    validateMinimumLength(commandParts, 4, "Invalid create command. Format should be: " +
            "create event \"subject\" from/on [date/time]");

    validateKeyword(commandParts[0], CREATE, "commands");
    validateKeyword(commandParts[1], EVENT, "'create'");
  }

  /**
   * Validates that an array has at least the specified length.
   */
  private void validateMinimumLength(String[] parts, int minLength, String errorMessage) {
    if (parts.length < minLength) {
      throw new IllegalArgumentException(errorMessage);
    }
  }

  /**
   * Validates that a keyword matches the expected value.
   */
  private void validateKeyword(String actual, String expected, String context) {
    if (!actual.equalsIgnoreCase(expected)) {
      throw new IllegalArgumentException("Expected '" + expected + "' " +
              (context.isEmpty() ? "" : "after " + context + " ") +
              "but found '" + actual + "'");
    }
  }

  /**
   * Gets the event type keyword (from/on) at the specified index.
   */
  private String getEventTypeKeyword(String[] parts, int index) {
    if (index >= parts.length) {
      throw new IllegalArgumentException("Incomplete command. After the event subject, specify either " +
              "'from' for timed events or 'on' for all-day events.");
    }

    String eventType = parts[index].toLowerCase();
    if (!eventType.equals(FROM) && !eventType.equals(ON)) {
      throw new IllegalArgumentException("Invalid keyword '" + parts[index] +
              "'. Use 'from' for timed events or 'on' for all-day events");
    }

    return eventType;
  }

  /**
   * Validates and returns the edit type.
   */
  private String validateEditType(String editType) {
    String type = editType.toLowerCase();
    if (!type.equals(EVENT) && !type.equals(EVENTS) && !type.equals(SERIES)) {
      throw new IllegalArgumentException("Invalid edit type '" + editType +
              "'. Must be 'event', 'events', or 'series'");
    }
    return type;
  }

  /**
   * Validates and returns the property name.
   */
  private String validateProperty(String property) {
    String prop = property.toLowerCase();
    if (!isValidProperty(prop)) {
      throw new IllegalArgumentException("Invalid property '" + property +
              "'. Valid properties are: subject, start, end, description, location, status");
    }
    return prop;
  }

  /**
   * Checks if a property name is valid.
   */
  private boolean isValidProperty(String property) {
    return property.equals("subject") ||
            property.equals("start") ||
            property.equals("end") ||
            property.equals("description") ||
            property.equals("location") ||
            property.equals("status");
  }

  /**
   * Finds the end index of quoted text starting at the given index.
   * @param parts the command parts
   * @param startIndex the starting index
   * @return the index after the closing quote, or startIndex+1 if not quoted
   */
  private int extractQuotedText(String[] parts, int startIndex) {
    if (startIndex >= parts.length) {
      throw new IllegalArgumentException("Missing text at expected position");
    }

    if (!parts[startIndex].startsWith("\"")) {
      // Single word, not quoted
      return startIndex + 1;
    }

    // Find closing quote
    int currentIndex = startIndex;
    while (currentIndex < parts.length && !parts[currentIndex].endsWith("\"")) {
      currentIndex++;
    }

    if (currentIndex >= parts.length) {
      throw new IllegalArgumentException("Unclosed quote in text");
    }

    return currentIndex + 1;
  }

  /**
   * Builds the text content from parts array, handling quotes.
   * @param parts the command parts
   * @param startIndex the starting index
   * @param endIndex the ending index (exclusive)
   * @return the extracted text without quotes
   */
  private String buildQuotedText(String[] parts, int startIndex, int endIndex) {
    if (startIndex >= parts.length) {
      throw new IllegalArgumentException("Invalid text position");
    }

    // Single word case
    if (endIndex == startIndex + 1) {
      return stripQuotes(parts[startIndex]);
    }

    // Multi-word case
    StringBuilder builder = new StringBuilder();
    for (int i = startIndex; i < endIndex; i++) {
      if (i > startIndex) {
        builder.append(" ");
      }
      builder.append(parts[i]);
    }

    return stripQuotes(builder.toString());
  }

  /**
   * Parses a count value.
   */
  private int parseCount(String countStr) {
    try {
      int count = Integer.parseInt(countStr);
      if (count <= 0) {
        throw new IllegalArgumentException("Count must be positive, but found: " + count);
      }
      return count;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid count '" + countStr +
              "'. Expected a positive number.");
    }
  }

  /**
   * Validates the 'times' keyword after a count.
   */
  private void validateTimesKeyword(String[] parts, int index) {
    if (index >= parts.length || !parts[index].equalsIgnoreCase(TIMES)) {
      throw new IllegalArgumentException("Expected 'times' after count. " +
              "Format: repeats [weekdays] for [count] times");
    }
  }

  /**
   * Parses a date-time string.
   */
  private LocalDateTime parseDateTime(String dateTimeStr) throws IllegalArgumentException {
    try {
      return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date-time format: '" + dateTimeStr +
              "'. Expected format: YYYY-MM-DDThh:mm (e.g., 2025-05-05T14:30)");
    }
  }

  /**
   * Parses a date string.
   */
  private LocalDateTime parseDate(String dateStr) throws IllegalArgumentException {
    try {
      return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: '" + dateStr +
              "'. Expected format: YYYY-MM-DD (e.g., 2025-05-05)");
    }
  }

  /**
   * Parses weekday characters into DayOfWeek list.
   */
  private ArrayList<DayOfWeek> parseWeekdays(String weekdaysStr) throws IllegalArgumentException {
    ArrayList<DayOfWeek> weekdays = new ArrayList<>();

    for (char c : weekdaysStr.toCharArray()) {
      DayOfWeek day = WEEKDAY_MAP.get(Character.toUpperCase(c));
      if (day == null) {
        throw new IllegalArgumentException("Invalid weekday character: '" + c +
                "'. Valid: M(onday), T(uesday), W(ednesday), R(Thursday), " +
                "F(riday), S(aturday), U(Sunday)");
      }
      weekdays.add(day);
    }

    if (weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }

    return weekdays;
  }

  /**
   * Removes quotes from a string if present.
   */
  private String stripQuotes(String value) {
    if (value != null && value.length() >= 2 &&
            value.startsWith("\"") && value.endsWith("\"")) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }
}