package calendar.controller.parser;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Parser for create event commands.
 * Handles both single and recurring events, timed and all-day.
 */
class CreateCommandParser extends AbstractCommandParser {
  // Specific indices for create command structure
  private static final int MIN_CREATE_COMMAND_LENGTH = 4;
  private static final int REPEATS_OFFSET = 3;  // Offset from subject end for "repeats" keyword
  private static final int WEEKDAYS_OFFSET = 4; // Offset from subject end for weekdays
  private static final int RECUR_TYPE_OFFSET = 5; // Offset for "for" or "until"
  private static final int RECUR_VALUE_OFFSET = 6; // Offset for count or until date
  private static final int TIMES_KEYWORD_OFFSET = 7; // Offset for "times" keyword

  public CreateCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] commandParts = commandLine.trim().split("\\s+");

    validateCreateCommandFormat(commandParts);

    // Extract subject
    int subjectEndIndex = extractQuotedText(commandParts, SUBJECT_START_INDEX);
    String subject = buildQuotedText(commandParts, SUBJECT_START_INDEX, subjectEndIndex);

    // Determine event type
    String eventType = getEventTypeKeyword(commandParts, subjectEndIndex);

    // Get remaining parts
    String[] remainingParts = Arrays.copyOfRange(commandParts, subjectEndIndex + 1,
            commandParts.length);

    if (eventType.equals(FROM)) {
      parseTimedEvent(subject, remainingParts);
    } else {
      parseAllDayEvent(subject, remainingParts);
    }
  }

  /**
   * Validates create command format.
   */
  private void validateCreateCommandFormat(String[] commandParts) {
    validateMinimumLength(commandParts, MIN_CREATE_COMMAND_LENGTH, "Invalid create command. " +
            "Format should be: create event \"subject\" from/on [date/time]");
    validateKeyword(commandParts[COMMAND_SUBTYPE_INDEX], EVENT, "'create'");
  }

  /**
   * Gets the event type keyword.
   */
  private String getEventTypeKeyword(String[] parts, int index) {
    if (index >= parts.length) {
      throw new IllegalArgumentException("Incomplete command. After the event subject, " +
              "specify either 'from' for timed events or 'on' for all-day events.");
    }

    String eventType = parts[index].toLowerCase();
    if (!eventType.equals(FROM) && !eventType.equals(ON)) {
      throw new IllegalArgumentException("Invalid keyword '" + parts[index] +
              "'. Use 'from' for timed events or 'on' for all-day events");
    }

    return eventType;
  }

  /**
   * Parses a timed event.
   */
  private void parseTimedEvent(String subject, String[] remainingParts) {
    final int START_TIME_INDEX = 0;
    final int TO_KEYWORD_INDEX = 1;
    final int END_TIME_INDEX = 2;
    final int MIN_TIMED_EVENT_LENGTH = 3;

    validateMinimumLength(remainingParts, MIN_TIMED_EVENT_LENGTH, "Incomplete timed event. Format: " +
            "from YYYY-MM-DDThh:mm to YYYY-MM-DDThh:mm");

    LocalDateTime startTime = parseDateTime(remainingParts[START_TIME_INDEX]);
    validateKeyword(remainingParts[TO_KEYWORD_INDEX], TO, "start and end times");
    LocalDateTime endTime = parseDateTime(remainingParts[END_TIME_INDEX]);

    if (remainingParts.length == MIN_TIMED_EVENT_LENGTH) {
      model.createSingleTimedEvent(subject, startTime, endTime);
    } else {
      parseRecurringTimedEvent(subject, startTime, endTime, remainingParts);
    }
  }

  /**
   * Parses an all-day event.
   */
  private void parseAllDayEvent(String subject, String[] remainingParts) {
    final int DATE_INDEX = 0;
    final int MIN_ALL_DAY_LENGTH = 1;

    validateMinimumLength(remainingParts, MIN_ALL_DAY_LENGTH, "Missing date for all-day event. " +
            "Format: on YYYY-MM-DD");

    LocalDateTime eventDate = parseDate(remainingParts[DATE_INDEX]);

    if (remainingParts.length == MIN_ALL_DAY_LENGTH) {
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
    // Confirm the "repeats" keyword is where we expect it
    validateKeyword(parts[REPEATS_OFFSET], REPEATS, "end time");

    // Need at least 3 more elements after weekdays
    validateMinimumLength(parts, WEEKDAYS_OFFSET + 3, "Incomplete recurring event");

    // Parse which days of the week this event repeats on
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_OFFSET]);

    // Next word determines recurrence type: "for" (count) or "until" (date)
    String recurType = parts[RECUR_TYPE_OFFSET].toLowerCase();

    if (recurType.equals(FOR)) {
      // Parse the count number
      int count = parseCount(parts[RECUR_VALUE_OFFSET]);

      // Verify the word "times" appears after the count
      validateTimesKeyword(parts, TIMES_KEYWORD_OFFSET);

      // Create the recurring event with specified count
      model.createRecurringTimedEvent(subject, startTime, endTime, weekdays, count);

    } else if (recurType.equals(UNTIL)) {
      // Parse the end date
      LocalDateTime untilDate = parseDate(parts[RECUR_VALUE_OFFSET]);

      // Create recurring events until the specified date
      model.createRecurringTimedEventUntil(subject, startTime, endTime, weekdays, untilDate);

    } else {
      // Neither "for" nor "until" - invalid syntax
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[RECUR_TYPE_OFFSET] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }

  /**
   * Parses recurring options for an all-day event.
   */
  private void parseRecurringAllDayEvent(String subject, LocalDateTime eventDate,
                                         String[] parts) {
    final int REPEATS_INDEX = 1;
    final int WEEKDAYS_INDEX = 2;
    final int RECUR_TYPE_INDEX = 3;
    final int RECUR_VALUE_INDEX = 4;
    final int TIMES_INDEX = 5;

    // Confirm the "repeats" keyword is where we expect it
    validateKeyword(parts[REPEATS_INDEX], REPEATS, "date");

    // Need at least 3 more elements after weekdays
    validateMinimumLength(parts, WEEKDAYS_INDEX + 3, "Incomplete recurring event");

    // Parse which days of the week this event repeats on
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[WEEKDAYS_INDEX]);

    // Next word determines recurrence type: "for" (count) or "until" (date)
    String recurType = parts[RECUR_TYPE_INDEX].toLowerCase();

    if (recurType.equals(FOR)) {
      // Parse the count number
      int count = parseCount(parts[RECUR_VALUE_INDEX]);

      // Verify the word "times" appears after the count
      validateTimesKeyword(parts, TIMES_INDEX);

      // Create the recurring all-day events with specified count
      model.createRecurringAllDayEvent(subject, eventDate, weekdays, count);

    } else if (recurType.equals(UNTIL)) {
      // Parse the end date
      LocalDateTime untilDate = parseDate(parts[RECUR_VALUE_INDEX]);

      // Create recurring all-day events until the specified date
      model.createRecurringAllDayEventUntil(subject, eventDate, weekdays, untilDate);

    } else {
      // Neither "for" nor "until" - invalid syntax
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[RECUR_TYPE_INDEX] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }
}