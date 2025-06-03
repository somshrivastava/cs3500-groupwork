package calendar.controller;

import calendar.controller.commands.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Parser for calendar commands. Handles parsing user input and creating appropriate command objects.
 */
public class CommandParser {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    String subject = parts[1];

    switch (commandType) {
      case "create":
        return parseCreateCommand(subject, Arrays.copyOfRange(parts, 2, parts.length));
      case "edit":
        // TODO: Implement edit command parsing
        throw new UnsupportedOperationException("Edit command not yet implemented");
      case "delete":
        // TODO: Implement delete command parsing
        throw new UnsupportedOperationException("Delete command not yet implemented");
      case "query":
        // TODO: Implement query command parsing
        throw new UnsupportedOperationException("Query command not yet implemented");
      default:
        throw new IllegalArgumentException("Unknown command type: " + commandType);
    }
  }

  private ICalendarCommand parseCreateCommand(String subject, String[] args) throws IllegalArgumentException {
    if (args.length < 1) {
      throw new IllegalArgumentException("Missing event type");
    }

    String eventType = args[0].toLowerCase();
    String[] eventArgs = Arrays.copyOfRange(args, 1, args.length);

    switch (eventType) {
      case "single":
        return parseSingleEventCommand(subject, eventArgs);
      case "recurring":
        return parseRecurringEventCommand(subject, eventArgs);
      default:
        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }
  }

  private ICalendarCommand parseSingleEventCommand(String subject, String[] args) throws IllegalArgumentException {
    if (args.length < 1) {
      throw new IllegalArgumentException("Missing event subtype");
    }

    String subtype = args[0].toLowerCase();
    String[] eventArgs = Arrays.copyOfRange(args, 1, args.length);

    switch (subtype) {
      case "timed":
        if (eventArgs.length != 2) {
          throw new IllegalArgumentException("Timed event requires start and end times");
        }
        LocalDateTime startTime = parseDateTime(eventArgs[0]);
        LocalDateTime endTime = parseDateTime(eventArgs[1]);
        return new CreateSingleTimedEventCommand(subject, startTime, endTime);

      case "allday":
        if (eventArgs.length != 1) {
          throw new IllegalArgumentException("All-day event requires a date");
        }
        LocalDateTime date = parseDate(eventArgs[0]);
        return new CreateSingleAllDayEventCommand(subject, date);

      default:
        throw new IllegalArgumentException("Unknown single event subtype: " + subtype);
    }
  }

  private ICalendarCommand parseRecurringEventCommand(String subject, String[] args) throws IllegalArgumentException {
    if (args.length < 3) {
      throw new IllegalArgumentException("Recurring event requires event subtype, weekdays, and count/until date");
    }

    String subtype = args[0].toLowerCase();
    ArrayList<DayOfWeek> weekdays = parseWeekdays(args[1]);
    String lastArg = args[2];

    switch (subtype) {
      case "timed":
        if (args.length != 5) {
          throw new IllegalArgumentException("Recurring timed event requires start time, end time, weekdays, and count/until date");
        }
        LocalDateTime startTime = parseDateTime(args[2]);
        LocalDateTime endTime = parseDateTime(args[3]);
        String countOrUntil = args[4];

        if (countOrUntil.matches("\\d+")) {
          int count = Integer.parseInt(countOrUntil);
          return new CreateRecurringTimedEventWithCountCommand(subject, startTime, endTime, weekdays, count);
        } else {
          LocalDateTime untilDate = parseDate(countOrUntil);
          return new CreateRecurringTimedEventUntilDateCommand(subject, startTime, endTime, weekdays, untilDate);
        }

      case "allday":
        if (args.length != 4) {
          throw new IllegalArgumentException("Recurring all-day event requires start date, weekdays, and count/until date");
        }
        LocalDateTime startDate = parseDate(args[2]);
        String countOrUntilDate = args[3];

        if (countOrUntilDate.matches("\\d+")) {
          int count = Integer.parseInt(countOrUntilDate);
          return new CreateRecurringAllDayEventWithCountCommand(subject, startDate, weekdays, count);
        } else {
          LocalDateTime untilDate = parseDate(countOrUntilDate);
          return new CreateRecurringAllDayEventUntilDateCommand(subject, startDate, weekdays, untilDate);
        }

      default:
        throw new IllegalArgumentException("Unknown recurring event subtype: " + subtype);
    }
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
      return LocalDateTime.parse(dateStr + " 00:00", DATE_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }
  }

  private ArrayList<DayOfWeek> parseWeekdays(String weekdaysStr) throws IllegalArgumentException {
    ArrayList<DayOfWeek> weekdays = new ArrayList<>();
    String[] days = weekdaysStr.split(",");

    for (String day : days) {
      try {
        weekdays.add(DayOfWeek.valueOf(day.trim().toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid weekday: " + day);
      }
    }

    if (weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }

    return weekdays;
  }
}