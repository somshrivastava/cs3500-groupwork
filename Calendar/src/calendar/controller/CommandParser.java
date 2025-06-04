package calendar.controller;

import calendar.model.ICalendarModel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for calendar commands. Handles parsing user input and calling appropriate model methods.
 */
public class CommandParser {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final Map<Character, DayOfWeek> WEEKDAY_MAP = new HashMap<>();
  private final ICalendarModel model;

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
   * Constructs a new CommandParser with the given calendar model.
   * @param model the calendar model to use
   */
  public CommandParser(ICalendarModel model) {
    this.model = model;
  }

  /**
   * Parses a command line and executes the appropriate model method.
   *
   * @param commandLine the command line to parse
   * @throws IllegalArgumentException if the command line is invalid
   */
  public void parse(String commandLine) throws IllegalArgumentException {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command line cannot be empty");
    }

    String[] parts = commandLine.trim().split("\\s+");
    if (parts.length < 2) {
      throw new IllegalArgumentException("Invalid command format");
    }

    String commandType = parts[0].toLowerCase();
    if (commandType.equals("create")) {
      parseCreateCommand(parts);
    } else if (commandType.equals("edit")) {
      parseEditCommand(parts);
    } else if (commandType.equals("print")) {
      parsePrintCommand(parts);
    } else if (commandType.equals("show")) {
      parseShowCommand(parts);
    } else {
      throw new IllegalArgumentException("Unknown command type: " + commandType);
    }
  }

  private void parseCreateCommand(String[] commandParts) throws IllegalArgumentException {
    validateCreateCommandFormat(commandParts);
    String eventSubject = extractQuotedSubject(commandParts);
    int subjectEndIndex = findSubjectEndIndex(commandParts);
    
    validateEventTypeSpecification(commandParts, subjectEndIndex);
    String eventType = commandParts[subjectEndIndex].toLowerCase();
    
    String[] remainingParts = Arrays.copyOfRange(commandParts, subjectEndIndex + 1, commandParts.length);
    if (eventType.equals("from")) {
      parseTimedEvent(eventSubject, remainingParts);
    } else {
      parseAllDayEvent(eventSubject, remainingParts);
    }
  }

  private void validateCreateCommandFormat(String[] commandParts) throws IllegalArgumentException {
    if (commandParts.length < 4) {
      throw new IllegalArgumentException("Invalid create command format");
    }
    final int COMMAND_WORD_INDEX = 0;
    final int EVENT_WORD_INDEX = 1;
    
    if (!commandParts[COMMAND_WORD_INDEX].equalsIgnoreCase("create")) {
      throw new IllegalArgumentException("Command must start with 'create'");
    }
    if (!commandParts[EVENT_WORD_INDEX].equalsIgnoreCase("event")) {
      throw new IllegalArgumentException("Command must be 'create event'");
    }
  }

  private String extractQuotedSubject(String[] commandParts) throws IllegalArgumentException {
    final int SUBJECT_START_INDEX = 2;
    if (!commandParts[SUBJECT_START_INDEX].startsWith("\"")) {
      throw new IllegalArgumentException("Event subject must be enclosed in quotes");
    }

    StringBuilder subjectBuilder = new StringBuilder();
    int currentIndex = SUBJECT_START_INDEX;
    
    while (currentIndex < commandParts.length && !commandParts[currentIndex].endsWith("\"")) {
      subjectBuilder.append(commandParts[currentIndex]).append(" ");
      currentIndex++;
    }
    
    if (currentIndex >= commandParts.length) {
      throw new IllegalArgumentException("Event subject must be enclosed in quotes");
    }
    
    subjectBuilder.append(commandParts[currentIndex]);
    return subjectBuilder.substring(1, subjectBuilder.length() - 1);
  }

  private int findSubjectEndIndex(String[] commandParts) {
    final int SUBJECT_START_INDEX = 2;
    int currentIndex = SUBJECT_START_INDEX;
    while (currentIndex < commandParts.length && !commandParts[currentIndex].endsWith("\"")) {
      currentIndex++;
    }
    return currentIndex + 1;
  }

  private void validateEventTypeSpecification(String[] commandParts, int currentIndex) throws IllegalArgumentException {
    if (currentIndex >= commandParts.length) {
      throw new IllegalArgumentException("Missing event type specification (from/on)");
    }
    String eventType = commandParts[currentIndex].toLowerCase();
    if (!eventType.equals("from") && !eventType.equals("on")) {
      throw new IllegalArgumentException("Event must specify 'from' for timed events or 'on' for all-day events");
    }
  }

  private void parseTimedEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateTimedEventFormat(remainingParts);
    
    final int START_TIME_INDEX = 0;
    final int TO_KEYWORD_INDEX = 1;
    final int END_TIME_INDEX = 2;
    
    LocalDateTime startTime = parseDateTime(remainingParts[START_TIME_INDEX]);
    validateToKeyword(remainingParts[TO_KEYWORD_INDEX]);
    LocalDateTime endTime = parseDateTime(remainingParts[END_TIME_INDEX]);

    if (isSingleEvent(remainingParts)) {
      model.createSingleTimedEvent(subject, startTime, endTime);
      return;
    }

    parseRecurringTimedEvent(subject, startTime, endTime, remainingParts);
  }

  private void validateTimedEventFormat(String[] parts) throws IllegalArgumentException {
    if (parts.length < 3) {
      throw new IllegalArgumentException("Invalid timed event format");
    }
  }

  private void validateToKeyword(String keyword) throws IllegalArgumentException {
    if (!keyword.equalsIgnoreCase("to")) {
      throw new IllegalArgumentException("Timed event must specify 'to' after start time");
    }
  }

  private boolean isSingleEvent(String[] parts) {
    return parts.length == 3;
  }

  private void parseRecurringTimedEvent(String subject, LocalDateTime startTime, 
      LocalDateTime endTime, String[] parts) throws IllegalArgumentException {
    validateRecurringEventFormat(parts);
    final int WEEKDAYS_INDEX = 4;
    final int FOR_OR_UNTIL_INDEX = 5;
    final int COUNT_OR_DATE_INDEX = 6;
    
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);
    
    if (parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("for")) {
      int count = Integer.parseInt(parts[COUNT_OR_DATE_INDEX]);
      model.createRecurringTimedEvent(subject, startTime, endTime, weekdays, count);
    } else {
      LocalDateTime untilDate = parseDate(parts[COUNT_OR_DATE_INDEX]);
      model.createRecurringTimedEventUntil(subject, startTime, endTime, weekdays, untilDate);
    }
  }

  private void validateRecurringEventFormat(String[] parts) throws IllegalArgumentException {
    final int REPEATS_KEYWORD_INDEX = parts.length == 7 ? 3 : 1;
    if (parts.length < 4 || !parts[REPEATS_KEYWORD_INDEX].equalsIgnoreCase("repeats")) {
      throw new IllegalArgumentException("Invalid recurring event format");
    }
  }

  private void parseAllDayEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateAllDayEventFormat(remainingParts);
    final int DATE_INDEX = 0;
    
    LocalDateTime eventDate = parseDate(remainingParts[DATE_INDEX]);

    if (remainingParts.length == 1) {
      model.createSingleAllDayEvent(subject, eventDate);
      return;
    }

    parseRecurringAllDayEvent(subject, eventDate, remainingParts);
  }

  private void validateAllDayEventFormat(String[] parts) throws IllegalArgumentException {
    if (parts.length < 1) {
      throw new IllegalArgumentException("Missing date for all-day event");
    }
  }

  private void parseRecurringAllDayEvent(String subject, LocalDateTime eventDate, 
      String[] parts) throws IllegalArgumentException {
    validateRecurringEventFormat(parts);
    final int WEEKDAYS_INDEX = 2;
    final int FOR_OR_UNTIL_INDEX = 3;
    final int COUNT_OR_DATE_INDEX = 4;
    
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);
    
    if (parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("for")) {
      int count = Integer.parseInt(parts[COUNT_OR_DATE_INDEX]);
      model.createRecurringAllDayEvent(subject, eventDate, weekdays, count);
    } else {
      LocalDateTime untilDate = parseDate(parts[COUNT_OR_DATE_INDEX]);
      model.createRecurringAllDayEventUntil(subject, eventDate, weekdays, untilDate);
    }
  }

  private void parseEditCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length < 7) {
      throw new IllegalArgumentException("Invalid edit command format");
    }

    String editType = parts[1].toLowerCase();
    if (!editType.equals("event") && !editType.equals("events") && !editType.equals("series")) {
      throw new IllegalArgumentException("Invalid edit type. Must be 'event', 'events', or 'series'");
    }

    String property = parts[2].toLowerCase();
    if (!isValidProperty(property)) {
      throw new IllegalArgumentException("Invalid property: " + property);
    }

    // Extract subject (which may be quoted)
    StringBuilder subjectBuilder = new StringBuilder();
    int i = 3;
    if (parts[i].startsWith("\"")) {
      while (i < parts.length && !parts[i].endsWith("\"")) {
        subjectBuilder.append(parts[i]).append(" ");
        i++;
      }
      if (i < parts.length) {
        subjectBuilder.append(parts[i]);
      }
      subjectBuilder.deleteCharAt(0).deleteCharAt(subjectBuilder.length() - 1);
      i++;
    } else {
      subjectBuilder.append(parts[i]);
      i++;
    }
    String subject = subjectBuilder.toString();

    if (!parts[i].equals("from")) {
      throw new IllegalArgumentException("Invalid edit command format");
    }
    i++;

    LocalDateTime startTime = parseDateTime(parts[i]);
    i++;

    if (editType.equals("event")) {
      if (!parts[i].equals("to")) {
        throw new IllegalArgumentException("Invalid edit event format");
      }
      i++;
      LocalDateTime endTime = parseDateTime(parts[i]);
      i++;
      if (!parts[i].equals("with")) {
        throw new IllegalArgumentException("Invalid edit event format");
      }
      i++;
      String newValue = parts[i];
      model.editEvent(subject, startTime, endTime, property, newValue);
    } else {
      if (!parts[i].equals("with")) {
        throw new IllegalArgumentException("Invalid edit format");
      }
      i++;
      String newValue = parts[i];
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
      throw new IllegalArgumentException("Invalid print command format");
    }

    if (!parts[1].equals("events")) {
      throw new IllegalArgumentException("Invalid print command. Must start with 'print events'");
    }

    if (parts[2].equals("on")) {
      if (parts.length != 4) {
        throw new IllegalArgumentException("Invalid print events on date format");
      }
      LocalDateTime date = parseDate(parts[3]);
      model.printEvents(date);
    } else if (parts[2].equals("from")) {
      if (parts.length != 6 || !parts[4].equals("to")) {
        throw new IllegalArgumentException("Invalid print events from-to format");
      }
      LocalDateTime startDate = parseDate(parts[3]);
      LocalDateTime endDate = parseDate(parts[5]);
      model.printEvents(startDate, endDate);
    } else {
      throw new IllegalArgumentException("Invalid print command format");
    }
  }

  private void parseShowCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length != 4 || !parts[1].equals("status") || !parts[2].equals("on")) {
      throw new IllegalArgumentException("Invalid show status format. Must be 'show status on <dateStringTtimeString>'");
    }
    LocalDateTime dateTime = parseDateTime(parts[3]);
    model.showStatus(dateTime);
  }

  private LocalDateTime parseDateTime(String dateTimeStr) throws IllegalArgumentException {
    try {
      return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date-time format: " + dateTimeStr);
    }
  }

  private LocalDateTime parseDate(String dateStr) throws IllegalArgumentException {
    try {
      return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }
  }

  private ArrayList<DayOfWeek> parseWeekdays(String weekdaysStr) throws IllegalArgumentException {
    ArrayList<DayOfWeek> weekdays = new ArrayList<>();
    for (char c : weekdaysStr.toCharArray()) {
      DayOfWeek day = WEEKDAY_MAP.get(Character.toUpperCase(c));
      if (day == null) {
        throw new IllegalArgumentException("Invalid weekday: " + c);
      }
      weekdays.add(day);
    }
    if (weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
    return weekdays;
  }
}