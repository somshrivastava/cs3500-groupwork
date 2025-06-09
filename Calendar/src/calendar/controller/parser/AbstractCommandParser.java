package calendar.controller.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Abstract base class for command parsers.
 * Contains common parsing logic shared by all command types.
 */
public abstract class AbstractCommandParser implements ICommandParser {
  // Common array indices used across parsers
  protected static final int COMMAND_TYPE_INDEX = 0;
  protected static final int COMMAND_SUBTYPE_INDEX = 1;
  protected static final int SUBJECT_START_INDEX = 2;

  // Formatters for parsing dates and times
  protected static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  // Maps single characters to days of week
  protected static final Map<Character, DayOfWeek> WEEKDAY_MAP = new HashMap<>();

  // Common keywords used across different commands
  protected static final String EVENT = "event";
  protected static final String EVENTS = "events";
  protected static final String SERIES = "series";
  protected static final String STATUS = "status";
  protected static final String FROM = "from";
  protected static final String TO = "to";
  protected static final String ON = "on";
  protected static final String WITH = "with";
  protected static final String REPEATS = "repeats";
  protected static final String FOR = "for";
  protected static final String UNTIL = "until";
  protected static final String TIMES = "times";

  protected final ICalendarModel model;
  protected final ICalendarView view;
  protected final ICalendarManager manager;

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
   * Constructs an abstract command parser.
   */
  protected AbstractCommandParser(ICalendarModel model, ICalendarView view) {
    this.model = model;
    this.view = view;
    this.manager = null;
  }

  /**
   * Constructs an abstract command parser with the given calendar manager and view.
   *
   * @param manager the calendar manager
   * @param view    the view
   */
  protected AbstractCommandParser(ICalendarManager manager, ICalendarView view) {
    this.manager = manager;
    this.view = view;
    this.model = null;
  }

  /**
   * Parses the command with the given parts.
   * Each subclass implements this to handle its specific command type.
   *
   * @param commandParts the split command parts
   * @throws IllegalArgumentException if parsing fails
   */
  public abstract void parse(String commandParts) throws IllegalArgumentException;

  /**
   * Validates that an array has at least the specified length.
   * Used to ensure commands have all required parameters before accessing them.
   */
  protected void validateMinimumLength(String[] parts, int minLength, String errorMessage) {
    if (parts.length < minLength) {
      throw new IllegalArgumentException(errorMessage);
    }
  }

  /**
   * Validates that a keyword matches the expected value (case-insensitive).
   * Used to verify command structure.
   */
  protected void validateKeyword(String actual, String expected, String context) {
    if (!actual.equalsIgnoreCase(expected)) {
      throw new IllegalArgumentException("Expected '" + expected + "' " +
              (context.isEmpty() ? "" : "after " + context + " ") +
              "but found '" + actual + "'");
    }
  }

  /**
   * Parses a date-time string in ISO format.
   * Format: YYYY-MM-DDThh:mm
   * The 'T' separates date from time.
   */
  protected LocalDateTime parseDateTime(String dateTimeStr) {
    try {
      return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date-time format: '" + dateTimeStr +
              "'. Expected format: YYYY-MM-DDThh:mm (e.g., 2025-05-05T14:30)");
    }
  }

  /**
   * Parses a date string in ISO format.
   * Format: YYYY-MM-DD
   * Returns the date at start of day (00:00).
   */
  protected LocalDateTime parseDate(String dateStr) {
    try {
      return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: '" + dateStr +
              "'. Expected format: YYYY-MM-DD (e.g., 2025-05-05)");
    }
  }

  /**
   * Parses weekday characters into DayOfWeek list.
   * Example: "MWF" -> [MONDAY, WEDNESDAY, FRIDAY]
   */
  protected ArrayList<DayOfWeek> parseWeekdays(String weekdaysStr) {
    ArrayList<DayOfWeek> weekdays = new ArrayList<>();

    // Process each character in the string
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
   * Parses a count value for recurring events.
   * Must be a positive integer.
   */
  protected int parseCount(String countStr) {
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
   * Finds the end index of quoted text starting at the given index.
   * Handles both quoted and unquoted text:
   *
   * @param parts      the command parts array
   * @param startIndex where to start looking
   * @return index after the text
   */
  protected int extractQuotedText(String[] parts, int startIndex) {
    if (startIndex >= parts.length) {
      throw new IllegalArgumentException("Missing text at expected position");
    }

    // Check if text starts with a quote
    if (!parts[startIndex].startsWith("\"")) {
      // Single word, not quoted, return next index
      return startIndex + 1;
    }

    // Multi-word quoted text, find closing quote
    int currentIndex = startIndex;
    while (currentIndex < parts.length && !parts[currentIndex].endsWith("\"")) {
      currentIndex++;
    }

    if (currentIndex >= parts.length) {
      throw new IllegalArgumentException("Unclosed quote in text");
    }

    // Return index after the element with closing quote
    return currentIndex + 1;
  }

  /**
   * Builds text content from parts array, handling quotes.
   * Reconstructs multi-word strings that were split by spaces.
   */
  protected String buildQuotedText(String[] parts, int startIndex, int endIndex) {
    if (startIndex >= parts.length) {
      throw new IllegalArgumentException("Invalid text position");
    }

    // Single element, just strip quotes if present
    if (endIndex == startIndex + 1) {
      return stripQuotes(parts[startIndex]);
    }

    // Multiple elements, reconstruct with spaces
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
   * Removes quotes from a string if present.
   * "Team Meeting" -> Team Meeting
   */
  protected String stripQuotes(String value) {
    if (value != null && value.length() >= 2 &&
            value.startsWith("\"") && value.endsWith("\"")) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }

  /**
   * Validates the 'times' keyword appears after a count.
   * Required syntax: "for [count] times"
   */
  protected void validateTimesKeyword(String[] parts, int index) {
    if (index >= parts.length || !parts[index].equalsIgnoreCase(TIMES)) {
      throw new IllegalArgumentException("Expected 'times' after count. " +
              "Format: repeats [weekdays] for [count] times");
    }
  }
}