package calendar.controller;

import calendar.controller.commands.*;
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
 * Parser for calendar commands. Handles parsing user input and creating appropriate command objects.
 */
public class CommandParser {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final Map<Character, DayOfWeek> WEEKDAY_MAP = new HashMap<>();

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
   * Parses a command line and returns the appropriate command object.
   *
   * @param commandLine the command line to parse
   * @return the parsed command
   * @throws IllegalArgumentException if the command line is invalid
   */
  public ICalendarCommand parse(String commandLine) throws IllegalArgumentException {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command line cannot be empty");
    }

    String[] parts = commandLine.trim().split("\\s+");
    if (parts.length < 2) {
      throw new IllegalArgumentException("Invalid command format");
    }

    String commandType = parts[0].toLowerCase();
    if (commandType.equals("create")) {
      return parseCreateCommand(parts);
    } else if (commandType.equals("edit")) {
      return parseEditCommand(parts);
    } else if (commandType.equals("print")) {
      return parsePrintCommand(parts);
    } else if (commandType.equals("show")) {
      return parseShowCommand(parts);
    } else {
      throw new IllegalArgumentException("Unknown command type: " + commandType);
    }
  }

  private ICalendarCommand parseCreateCommand(String[] commandParts) throws IllegalArgumentException {
    validateCreateCommandFormat(commandParts);
    String eventSubject = extractQuotedSubject(commandParts);
    int subjectEndIndex = findSubjectEndIndex(commandParts);
    
    validateEventTypeSpecification(commandParts, subjectEndIndex);
    String eventType = commandParts[subjectEndIndex].toLowerCase();
    
    String[] remainingParts = Arrays.copyOfRange(commandParts, subjectEndIndex + 1, commandParts.length);
    return eventType.equals("from") 
        ? parseTimedEvent(eventSubject, remainingParts)
        : parseAllDayEvent(eventSubject, remainingParts);
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

  private ICalendarCommand parseTimedEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateTimedEventFormat(remainingParts);
    
    final int START_TIME_INDEX = 0;
    final int TO_KEYWORD_INDEX = 1;
    final int END_TIME_INDEX = 2;
    final int REPEATS_KEYWORD_INDEX = 3;
    final int WEEKDAYS_INDEX = 4;
    final int FOR_OR_UNTIL_INDEX = 5;
    final int COUNT_OR_DATE_INDEX = 6;
    
    LocalDateTime startTime = parseDateTime(remainingParts[START_TIME_INDEX]);
    validateToKeyword(remainingParts[TO_KEYWORD_INDEX]);
    LocalDateTime endTime = parseDateTime(remainingParts[END_TIME_INDEX]);

    if (isSingleEvent(remainingParts)) {
      return new CreateSingleTimedEventCommand(subject, startTime, endTime);
    }

    return parseRecurringTimedEvent(subject, startTime, endTime, remainingParts);
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

  private ICalendarCommand parseRecurringTimedEvent(String subject, LocalDateTime startTime, 
      LocalDateTime endTime, String[] parts) throws IllegalArgumentException {
    validateRecurringEventFormat(parts);
    final int WEEKDAYS_INDEX = 4;
    final int FOR_OR_UNTIL_INDEX = 5;
    final int COUNT_OR_DATE_INDEX = 6;
    
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);
    
    return parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("for")
        ? createRecurringTimedEventWithCount(subject, startTime, endTime, weekdays, parts)
        : createRecurringTimedEventUntilDate(subject, startTime, endTime, weekdays, parts);
  }

  private void validateRecurringEventFormat(String[] parts) throws IllegalArgumentException {
    final int REPEATS_KEYWORD_INDEX = parts.length == 7 ? 3 : 1;
    if (parts.length < 4 || !parts[REPEATS_KEYWORD_INDEX].equalsIgnoreCase("repeats")) {
      throw new IllegalArgumentException("Invalid recurring event format");
    }
  }

  private ICalendarCommand createRecurringTimedEventWithCount(String subject, LocalDateTime startTime,
      LocalDateTime endTime, ArrayList<DayOfWeek> weekdays, String[] parts) throws IllegalArgumentException {
    final int COUNT_INDEX = 6;
    if (parts.length != 7) {
      throw new IllegalArgumentException("Invalid count format");
    }
    int count = Integer.parseInt(parts[COUNT_INDEX]);
    return new CreateRecurringTimedEventWithCountCommand(subject, startTime, endTime, weekdays, count);
  }

  private ICalendarCommand createRecurringTimedEventUntilDate(String subject, LocalDateTime startTime,
      LocalDateTime endTime, ArrayList<DayOfWeek> weekdays, String[] parts) throws IllegalArgumentException {
    final int UNTIL_DATE_INDEX = 6;
    if (parts.length != 7) {
      throw new IllegalArgumentException("Invalid until date format");
    }
    LocalDateTime untilDate = parseDate(parts[UNTIL_DATE_INDEX]);
    return new CreateRecurringTimedEventUntilDateCommand(subject, startTime, endTime, weekdays, untilDate);
  }

  private ICalendarCommand parseAllDayEvent(String subject, String[] remainingParts) throws IllegalArgumentException {
    validateAllDayEventFormat(remainingParts);
    final int DATE_INDEX = 0;
    
    LocalDateTime eventDate = parseDate(remainingParts[DATE_INDEX]);

    if (remainingParts.length == 1) {
      return new CreateSingleAllDayEventCommand(subject, eventDate);
    }

    return parseRecurringAllDayEvent(subject, eventDate, remainingParts);
  }

  private void validateAllDayEventFormat(String[] parts) throws IllegalArgumentException {
    if (parts.length < 1) {
      throw new IllegalArgumentException("Missing date for all-day event");
    }
  }

  private ICalendarCommand parseRecurringAllDayEvent(String subject, LocalDateTime eventDate, 
      String[] parts) throws IllegalArgumentException {
    validateRecurringEventFormat(parts);
    final int WEEKDAYS_INDEX = 2;
    final int FOR_OR_UNTIL_INDEX = 3;
    final int COUNT_OR_DATE_INDEX = 4;
    
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);
    
    return parts[FOR_OR_UNTIL_INDEX].equalsIgnoreCase("for")
        ? createRecurringAllDayEventWithCount(subject, eventDate, weekdays, parts)
        : createRecurringAllDayEventUntilDate(subject, eventDate, weekdays, parts);
  }

  private ICalendarCommand createRecurringAllDayEventWithCount(String subject, LocalDateTime eventDate,
      ArrayList<DayOfWeek> weekdays, String[] parts) throws IllegalArgumentException {
    final int COUNT_INDEX = 4;
    if (parts.length != 5) {
      throw new IllegalArgumentException("Invalid count format");
    }
    int count = Integer.parseInt(parts[COUNT_INDEX]);
    return new CreateRecurringAllDayEventWithCountCommand(subject, eventDate, weekdays, count);
  }

  private ICalendarCommand createRecurringAllDayEventUntilDate(String subject, LocalDateTime eventDate,
      ArrayList<DayOfWeek> weekdays, String[] parts) throws IllegalArgumentException {
    final int UNTIL_DATE_INDEX = 4;
    if (parts.length != 5) {
      throw new IllegalArgumentException("Invalid until date format");
    }
    LocalDateTime untilDate = parseDate(parts[UNTIL_DATE_INDEX]);
    return new CreateRecurringAllDayEventUntilDateCommand(subject, eventDate, weekdays, untilDate);
  }

  private ICalendarCommand parseEditCommand(String[] parts) throws IllegalArgumentException {
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
      return new EditSingleEventCommand(subject, startTime, endTime, property, newValue);
    } else {
      if (!parts[i].equals("with")) {
        throw new IllegalArgumentException("Invalid edit format");
      }
      i++;
      String newValue = parts[i];
      if (editType.equals("events")) {
        return new EditEventsFromDateCommand(subject, startTime, property, newValue);
      } else { // series
        return new EditSeriesCommand(subject, startTime, property, newValue);
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

  private ICalendarCommand parsePrintCommand(String[] parts) throws IllegalArgumentException {
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
      return new PrintEventsOnDateCommand(parts[3]);
    } else if (parts[2].equals("from")) {
      if (parts.length != 6 || !parts[4].equals("to")) {
        throw new IllegalArgumentException("Invalid print events from-to format");
      }
      return new PrintEventsInIntervalCommand(parts[3], parts[5]);
    } else {
      throw new IllegalArgumentException("Invalid print command format");
    }
  }

  private ICalendarCommand parseShowCommand(String[] parts) throws IllegalArgumentException {
    if (parts.length != 4 || !parts[1].equals("status") || !parts[2].equals("on")) {
      throw new IllegalArgumentException("Invalid show status format. Must be 'show status on <dateStringTtimeString>'");
    }
    return new ShowStatusCommand(parts[3]);
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