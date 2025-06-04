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
 * The parser supports the following commands:
 * 1. create event - Creates new events (both timed and all-day)
 * 2. edit event - Modifies existing events
 * 3. print events - Displays events for a specific date
 * 4. show status - Shows availability status for a specific time
 */
public class CommandParser {
  // Formatters for parsing dates and times in ISO format
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  private static final Map<Character, DayOfWeek> WEEKDAY_MAP = new HashMap<>();
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
   * Main entry point for parsing commands. Splits the command line into parts and routes to
   * the appropriate command handler based on the first word.
   *
   * @param commandLine the command line to parse
   * @throws IllegalArgumentException if the command line is invalid or malformed
   */
  public void parse(String commandLine) throws IllegalArgumentException {
    // Check if the command is empty
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty. Please enter a valid command.");
    }

    String[] commandParts = commandLine.trim().split("\\s+");

    // Route to appropriate command handler
    String commandType = commandParts[0].toLowerCase();
    switch (commandType) {
      case "create":
        this.parseCreateCommand(commandParts);
        break;
      case "edit":
        this.parseEditCommand(commandParts);
        break;
      case "print":
        this.parsePrintCommand(commandParts);
        break;
      case "show":
        this.parseShowCommand(commandParts);
        break;
      default:
        throw new IllegalArgumentException("Unknown command: '" + commandType +
                "'. Valid commands are: create, edit, print, show");
    }
  }

  /**
   * Handles the "create event" command. Supports both timed and all-day events.
   * Format: create event "subject" from/to/on [time/date] [recurring options]
   */
  private void parseCreateCommand(String[] commandParts) throws IllegalArgumentException {
    // First two words of command must be "create event"
    this.validateCreateCommandFormat(commandParts);

    // Get the event subject
    String eventSubject = getSubject(commandParts);
    int subjectEndIndex = findSubjectEndIndex(commandParts);

    // Validate and get event type (timed vs all-day)
    validateEventTypeSpecification(commandParts, subjectEndIndex);
    String eventType = commandParts[subjectEndIndex].toLowerCase();

    // Parse remaining parts based on event type
    String[] remainingParts = Arrays.copyOfRange(commandParts, subjectEndIndex + 1, commandParts.length);
    if (eventType.equals("from")) {
      parseTimedEvent(eventSubject, remainingParts);
    } else {
      parseAllDayEvent(eventSubject, remainingParts);
    }
  }

  /**
   * Validates the basic structure of a create command.
   * Must start with "create event" followed by a quoted subject.
   */
  private void validateCreateCommandFormat(String[] commandParts) throws IllegalArgumentException {
    // Create commands must be at least 4 words
    if (commandParts.length < 4) {
      throw new IllegalArgumentException("Invalid create command. Format should be: " +
              "create event \"subject\" from/on [date/time]");
    }
    // First word must be "create"
    if (!commandParts[0].equalsIgnoreCase("create")) {
      throw new IllegalArgumentException("Expected 'create' but found '" + commandParts[0] +
              "'. Commands must start with: create, edit, print, or show");
    }
    // Second word must be "event"
    if (!commandParts[1].equalsIgnoreCase("event")) {
      throw new IllegalArgumentException("Expected 'event' after 'create' but found '" +
              commandParts[1] + "'. Use: create event \"subject\" ...");
    }
  }

  /**
   * Get event subject from quoted text in the command.
   * Handles multi-word subjects by concatenating parts until closing quote.
   */
  private String getSubject(String[] commandParts) throws IllegalArgumentException {
    // Subject must start with a quotation mark in the command
    if (!commandParts[2].startsWith("\"")) {
      throw new IllegalArgumentException("Event subject must be enclosed in quotes. " +
              "Format: create event \"Event Name\" ...");
    }

    StringBuilder subjectBuilder = new StringBuilder();
    int currentIndex = 2;

    // Keep adding words to the subject builder until we find the closing quote
    while (currentIndex < commandParts.length && !commandParts[currentIndex].endsWith("\"")) {
      subjectBuilder.append(commandParts[currentIndex]).append(" ");
      currentIndex++;
    }

    // No closing quote was found for the subject in the command
    if (currentIndex >= commandParts.length) {
      throw new IllegalArgumentException("Unclosed quote in event subject. " +
              "Make sure to close the quotes around the event name.");
    }

    // Add the last word, but get rid of the closing quote
    subjectBuilder.append(commandParts[currentIndex]);
    return subjectBuilder.substring(1, subjectBuilder.length() - 1);
  }

  /**
   * Finds the index where the subject ends (after the closing quote).
   */
  private int findSubjectEndIndex(String[] commandParts) {
    final int SUBJECT_START_INDEX = 2;
    int currentIndex = SUBJECT_START_INDEX;
    while (currentIndex < commandParts.length && !commandParts[currentIndex].endsWith("\"")) {
      currentIndex++;
    }
    return currentIndex + 1;
  }

  /**
   * Validates that the event type specification is present and valid.
   * Must be either 'from' (for timed events) or 'on' (for all-day events).
   */
  private void validateEventTypeSpecification(String[] commandParts, int currentIndex) throws IllegalArgumentException {
    if (currentIndex >= commandParts.length) {
      throw new IllegalArgumentException("Incomplete command. After the event subject, specify either " +
              "'from' for timed events or 'on' for all-day events.");
    }
    String eventType = commandParts[currentIndex].toLowerCase();
    if (!eventType.equals("from") && !eventType.equals("on")) {
      throw new IllegalArgumentException("Invalid keyword '" + commandParts[currentIndex] +
              "'. Use 'from' for timed events (e.g., from 2025-05-05T10:00 to 2025-05-05T11:00) " +
              "or 'on' for all-day events (e.g., on 2025-05-05)");
    }
  }

  /**
   * Parses a timed event command.
   * Format: from [start-time] to [end-time] [recurring options]
   */
  private void parseTimedEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateTimedEventFormat(remainingParts);

    final int START_TIME_INDEX = 0;
    final int TO_KEYWORD_INDEX = 1;
    final int END_TIME_INDEX = 2;

    // Parse start and end times
    LocalDateTime startTime = parseDateTime(remainingParts[START_TIME_INDEX]);
    validateToKeyword(remainingParts[TO_KEYWORD_INDEX]);
    LocalDateTime endTime = parseDateTime(remainingParts[END_TIME_INDEX]);

    // Handle single vs recurring events
    if (isSingleEvent(remainingParts)) {
      model.createSingleTimedEvent(subject, startTime, endTime);
      return;
    }

    parseRecurringTimedEvent(subject, startTime, endTime, remainingParts);
  }

  /**
   * Validates the basic structure of a timed event command.
   */
  private void validateTimedEventFormat(String[] parts) throws IllegalArgumentException {
    if (parts.length < 3) {
      throw new IllegalArgumentException("Incomplete timed event. Format should be: " +
              "from YYYY-MM-DDThh:mm to YYYY-MM-DDThh:mm");
    }
  }

  /**
   * Validates that the 'to' keyword is present between start and end times.
   */
  private void validateToKeyword(String keyword) throws IllegalArgumentException {
    if (!keyword.equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Expected 'to' between start and end times but found '" +
              keyword + "'. Format: from [start-time] to [end-time]");
    }
  }

  /**
   * Checks if the command represents a single (non-recurring) event.
   */
  private boolean isSingleEvent(String[] parts) {
    return parts.length == 3;
  }

  /**
   * Parses a recurring timed event command.
   * Format: from [start-time] to [end-time] repeats [weekdays] for/until [count/date]
   */
  private void parseRecurringTimedEvent(String subject, LocalDateTime startTime,
                                        LocalDateTime endTime, String[] parts) throws IllegalArgumentException {
    validateRecurringEventFormat(parts);
    final int WEEKDAYS_INDEX = 4;
    final int FOR_OR_UNTIL_INDEX = 5;
    final int COUNT_OR_DATE_INDEX = 6;

    // Parse weekdays and recurrence options
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);

    if (parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("for")) {
      // Handle count-based recurrence
      if (COUNT_OR_DATE_INDEX >= parts.length) {
        throw new IllegalArgumentException("Missing count value after 'for'");
      }

      int count;
      try {
        count = Integer.parseInt(parts[COUNT_OR_DATE_INDEX]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid count '" + parts[COUNT_OR_DATE_INDEX] +
                "'. Expected a positive number after 'for'.");
      }

      if (count <= 0) {
        throw new IllegalArgumentException("Count must be positive, but found: " + count);
      }

      if (parts.length < 8 || !parts[7].equalsIgnoreCase("times")) {
        throw new IllegalArgumentException("Expected 'times' after count. Format: repeats [weekdays] for [count] times");
      }

      model.createRecurringTimedEvent(subject, startTime, endTime, weekdays, count);
    } else if (parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("until")) {
      // Handle date-based recurrence
      LocalDateTime untilDate = parseDate(parts[COUNT_OR_DATE_INDEX]);
      model.createRecurringTimedEventUntil(subject, startTime, endTime, weekdays, untilDate);
    } else {
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[FOR_OR_UNTIL_INDEX] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }

  /**
   * Validates the structure of a recurring event command.
   * Must include the 'repeats' keyword and appropriate recurrence options.
   */
  private void validateRecurringEventFormat(String[] parts) throws IllegalArgumentException {
    if (parts.length < 7) {
      throw new IllegalArgumentException("Incomplete recurring event. Format: " +
              "from [start] to [end] repeats [weekdays] for [count] times OR until [date]");
    }

    if (!parts[3].equalsIgnoreCase("repeats")) {
      throw new IllegalArgumentException("Expected 'repeats' after end time but found '" +
              parts[3] + "'. For recurring events use: ... repeats [weekdays] ...");
    }
  }

  /**
   * Parses an all-day event command.
   * Format: on [date] [recurring options]
   */
  private void parseAllDayEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateAllDayEventFormat(remainingParts);
    final int DATE_INDEX = 0;

    LocalDateTime eventDate = parseDate(remainingParts[DATE_INDEX]);

    // Handle single vs recurring events
    if (remainingParts.length == 1) {
      model.createSingleAllDayEvent(subject, eventDate);
      return;
    }

    parseRecurringAllDayEvent(subject, eventDate, remainingParts);
  }

  /**
   * Validates the basic structure of an all-day event command.
   */
  private void validateAllDayEventFormat(String[] parts) throws IllegalArgumentException {
    if (parts.length < 1) {
      throw new IllegalArgumentException("Missing date for all-day event. Format: on YYYY-MM-DD");
    }
  }

  /**
   * Parses a recurring all-day event command.
   * Format: on [date] repeats [weekdays] for/until [count/date]
   */
  private void parseRecurringAllDayEvent(String subject, LocalDateTime eventDate,
                                         String[] parts) throws IllegalArgumentException {
    if (parts.length < 5) {
      throw new IllegalArgumentException("Incomplete recurring all-day event. Format: " +
              "on [date] repeats [weekdays] for [count] times OR until [date]");
    }

    if (!parts[1].equalsIgnoreCase("repeats")) {
      throw new IllegalArgumentException("Expected 'repeats' after date but found '" +
              parts[1] + "'");
    }

    final int WEEKDAYS_INDEX = 2;
    final int FOR_OR_UNTIL_INDEX = 3;
    final int COUNT_OR_DATE_INDEX = 4;

    // Parse weekdays and recurrence options
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);

    if (parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("for")) {
      // Handle count-based recurrence
      if (COUNT_OR_DATE_INDEX >= parts.length) {
        throw new IllegalArgumentException("Missing count value after 'for'");
      }

      int count;
      try {
        count = Integer.parseInt(parts[COUNT_OR_DATE_INDEX]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid count '" + parts[COUNT_OR_DATE_INDEX] +
                "'. Expected a positive number after 'for'.");
      }

      if (count <= 0) {
        throw new IllegalArgumentException("Count must be positive, but found: " + count);
      }

      if (parts.length < 6 || !parts[5].equalsIgnoreCase("times")) {
        throw new IllegalArgumentException("Expected 'times' after count. Format: repeats [weekdays] for [count] times");
      }

      model.createRecurringAllDayEvent(subject, eventDate, weekdays, count);
    } else if (parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("until")) {
      // Handle date-based recurrence
      LocalDateTime untilDate = parseDate(parts[COUNT_OR_DATE_INDEX]);
      model.createRecurringAllDayEventUntil(subject, eventDate, weekdays, untilDate);
    } else {
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[FOR_OR_UNTIL_INDEX] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }

  /**
   * Parses an edit command.
   * Format: edit event/events/series [property] [subject] from [time] [to [time]] with [value]
   */
  private void parseEditCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length < 7) {
      throw new IllegalArgumentException("Incomplete edit command. Format: " +
              "edit event [property] \"subject\" from [start] to [end] with [value]");
    }

    String editType = parts[1].toLowerCase();
    if (!editType.equals("event") && !editType.equals("events") && !editType.equals("series")) {
      throw new IllegalArgumentException("Invalid edit type '" + parts[1] +
              "'. Must be 'event' (single), 'events' (from point), or 'series' (all)");
    }

    String property = parts[2].toLowerCase();
    if (!isValidProperty(property)) {
      throw new IllegalArgumentException("Invalid property '" + property +
              "'. Valid properties are: subject, start, end, description, location, status");
    }

    // Extract subject (which may be quoted and multi-word)
    int i = 3;
    String subject;
    if (parts[i].startsWith("\"")) {
      StringBuilder subjectBuilder = new StringBuilder();
      while (i < parts.length && !parts[i].endsWith("\"")) {
        subjectBuilder.append(parts[i]).append(" ");
        i++;
      }
      if (i < parts.length) {
        subjectBuilder.append(parts[i]);
      } else {
        throw new IllegalArgumentException("Unclosed quote in event subject");
      }
      subject = subjectBuilder.toString().trim();
      subject = stripQuotesIfPresent(subject);
      i++;
    } else {
      subject = parts[i];
      i++;
    }

    if (i >= parts.length || !parts[i].equals("from")) {
      throw new IllegalArgumentException("Expected 'from' after subject but found " +
              (i < parts.length ? "'" + parts[i] + "'" : "end of command"));
    }
    i++;

    if (i >= parts.length) {
      throw new IllegalArgumentException("Missing start time after 'from'");
    }
    LocalDateTime startTime = parseDateTime(parts[i]);
    i++;

    if (editType.equals("event")) {
      if (i >= parts.length || !parts[i].equals("to")) {
        throw new IllegalArgumentException("For single event edit, expected 'to' after start time");
      }
      i++;
      if (i >= parts.length) {
        throw new IllegalArgumentException("Missing end time after 'to'");
      }
      LocalDateTime endTime = parseDateTime(parts[i]);
      i++;
      if (i >= parts.length || !parts[i].equals("with")) {
        throw new IllegalArgumentException("Expected 'with' before new value");
      }
      i++;
      if (i >= parts.length) {
        throw new IllegalArgumentException("Missing new value after 'with'");
      }
      // Extract newValue (which may be quoted and multi-word)
      String newValue;
      if (parts[i].startsWith("\"")) {
        StringBuilder valueBuilder = new StringBuilder();
        int startIdx = i;
        while (i < parts.length && !parts[i].endsWith("\"")) {
          if (i > startIdx) {
            valueBuilder.append(" ");
          }
          valueBuilder.append(parts[i]);
          i++;
        }
        if (i < parts.length) {
          if (i > startIdx) {
            valueBuilder.append(" ");
          }
          valueBuilder.append(parts[i]);
        } else {
          throw new IllegalArgumentException("Unclosed quote in new value");
        }
        newValue = stripQuotesIfPresent(valueBuilder.toString());
      } else {
        newValue = parts[i];
      }
      model.editEvent(subject, startTime, endTime, property, newValue);
    } else {
      if (i >= parts.length || !parts[i].equals("with")) {
        throw new IllegalArgumentException("Expected 'with' before new value");
      }
      i++;
      if (i >= parts.length) {
        throw new IllegalArgumentException("Missing new value after 'with'");
      }
      // Extract newValue (which may be quoted and multi-word)
      String newValue;
      if (parts[i].startsWith("\"")) {
        StringBuilder valueBuilder = new StringBuilder();
        int startIdx = i;
        while (i < parts.length && !parts[i].endsWith("\"")) {
          if (i > startIdx) {
            valueBuilder.append(" ");
          }
          valueBuilder.append(parts[i]);
          i++;
        }
        if (i < parts.length) {
          if (i > startIdx) {
            valueBuilder.append(" ");
          }
          valueBuilder.append(parts[i]);
        } else {
          throw new IllegalArgumentException("Unclosed quote in new value");
        }
        newValue = stripQuotesIfPresent(valueBuilder.toString());
      } else {
        newValue = parts[i];
      }
      if (editType.equals("events")) {
        model.editEvents(subject, startTime, property, newValue);
      } else { // series
        model.editSeries(subject, startTime, property, newValue);
      }
    }
  }

  private boolean isValidProperty(String property) {
    return property.equals("subject") ||
            property.equals("start") ||
            property.equals("end") ||
            property.equals("description") ||
            property.equals("location") ||
            property.equals("status");
  }

  private void parsePrintCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length < 4) {
      throw new IllegalArgumentException("Incomplete print command. Use: " +
              "print events on YYYY-MM-DD OR print events from [start] to [end]");
    }

    if (!parts[1].equals("events")) {
      throw new IllegalArgumentException("Expected 'events' after 'print' but found '" +
              parts[1] + "'. Use: print events ...");
    }

    List<IEvent> events;
    String header;

    if (parts[2].equals("on")) {
      if (parts.length != 4) {
        throw new IllegalArgumentException("For 'print events on', provide exactly one date. " +
                "Format: print events on YYYY-MM-DD");
      }
      LocalDateTime date = parseDate(parts[3]);
      events = model.printEvents(date);
      header = "Events on " + date.toLocalDate().toString();
    } else if (parts[2].equals("from")) {
      if (parts.length != 6) {
        throw new IllegalArgumentException("For date range, use: print events from [start] to [end]");
      }
      if (!parts[4].equals("to")) {
        throw new IllegalArgumentException("Expected 'to' between dates but found '" +
                parts[4] + "'");
      }
      LocalDateTime startDate = parseDateTime(parts[3]);
      LocalDateTime endDate = parseDateTime(parts[5]);
      events = model.printEvents(startDate, endDate);
      header = "Events from " + startDate.toLocalDate().toString() + " to " + endDate.toLocalDate().toString();
    } else {
      throw new IllegalArgumentException("After 'print events', use either 'on' for single date " +
              "or 'from' for date range. Found: '" + parts[2] + "'");
    }

    view.displayEvents(header, events);
  }

  private void parseShowCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length != 4) {
      throw new IllegalArgumentException("Show status requires exactly 4 parts. " +
              "Format: show status on YYYY-MM-DDThh:mm");
    }

    if (!parts[1].equals("status")) {
      throw new IllegalArgumentException("Expected 'status' after 'show' but found '" +
              parts[1] + "'. Use: show status on [date-time]");
    }

    if (!parts[2].equals("on")) {
      throw new IllegalArgumentException("Expected 'on' after 'show status' but found '" +
              parts[2] + "'. Format: show status on YYYY-MM-DDThh:mm");
    }

    LocalDateTime dateTime = parseDateTime(parts[3]);
    boolean isBusy = model.showStatus(dateTime);
    view.displayStatus(parts[3], isBusy);
  }

  private LocalDateTime parseDateTime(String dateTimeStr) throws IllegalArgumentException {
    try {
      return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date-time format: '" + dateTimeStr +
              "'. Expected format: YYYY-MM-DDThh:mm (e.g., 2025-05-05T14:30)");
    }
  }

  private LocalDateTime parseDate(String dateStr) throws IllegalArgumentException {
    try {
      return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: '" + dateStr +
              "'. Expected format: YYYY-MM-DD (e.g., 2025-05-05)");
    }
  }

  private ArrayList<DayOfWeek> parseWeekdays(String weekdaysStr) throws IllegalArgumentException {
    ArrayList<DayOfWeek> weekdays = new ArrayList<>();
    for (char c : weekdaysStr.toCharArray()) {
      DayOfWeek day = WEEKDAY_MAP.get(Character.toUpperCase(c));
      if (day == null) {
        throw new IllegalArgumentException("Invalid weekday character: '" + c +
                "'. Valid weekdays are: M(onday), T(uesday), W(ednesday), R(Thursday), " +
                "F(riday), S(aturday), U(Sunday)");
      }
      weekdays.add(day);
    }
    if (weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
    return weekdays;
  }

  private String stripQuotesIfPresent(String value) {
    if (value != null && value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }
}