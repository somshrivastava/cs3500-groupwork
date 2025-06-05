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

  public CreateCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String[] commandParts) throws IllegalArgumentException {
    validateCreateCommandFormat(commandParts);

    // Extract subject
    int subjectEndIndex = extractQuotedText(commandParts, 2);
    String subject = buildQuotedText(commandParts, 2, subjectEndIndex);

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
    validateMinimumLength(commandParts, 4, "Invalid create command. " +
            "Format should be: create event \"subject\" from/on [date/time]");
    validateKeyword(commandParts[1], EVENT, "'create'");
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
  private void parseAllDayEvent(String subject, String[] remainingParts) {
    validateMinimumLength(remainingParts, 1, "Missing date for all-day event. " +
            "Format: on YYYY-MM-DD");

    LocalDateTime eventDate = parseDate(remainingParts[0]);

    if (remainingParts.length == 1) {
      model.createSingleAllDayEvent(subject, eventDate);
    } else {
      parseRecurringAllDayEvent(subject, eventDate, remainingParts);
    }
  }

  /**
   * Parses recurring options for a timed event.
   *
   * This method is called when we've already parsed:
   * - "create event [subject] from [startTime] to [endTime]"
   * And now we need to parse the recurring pattern.
   *
   */
  private void parseRecurringTimedEvent(String subject, LocalDateTime startTime,
                                        LocalDateTime endTime, String[] parts) {
    // Confirm the "repeats" keyword is where we expect it
    validateKeyword(parts[3], REPEATS, "end time");

    // Weekdays come right after "repeats", so index 4
    int weekdaysIndex = 4;

    // Need at least 3 more elements after weekdays
    validateMinimumLength(parts, weekdaysIndex + 3, "Incomplete recurring event");

    // Parse which days of the week this event repeats on
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[weekdaysIndex]);

    // Next word determines recurrence type: "for" (count) or "until" (date)
    String recurType = parts[weekdaysIndex + 1].toLowerCase();

    if (recurType.equals(FOR)) {
      // Parse the count number at weekdaysIndex + 2
      int count = parseCount(parts[weekdaysIndex + 2]);

      // Verify the word "times" appears after the count
      validateTimesKeyword(parts, weekdaysIndex + 3);

      // Create the recurring event with specified count
      model.createRecurringTimedEvent(subject, startTime, endTime, weekdays, count);

    } else if (recurType.equals(UNTIL)) {
      // Parse the end date at weekdaysIndex + 2
      LocalDateTime untilDate = parseDate(parts[weekdaysIndex + 2]);

      // Create recurring events until the specified date
      model.createRecurringTimedEventUntil(subject, startTime, endTime, weekdays, untilDate);

    } else {
      // Neither "for" nor "until" - invalid syntax
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[weekdaysIndex + 1] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }

  /**
   * Parses recurring options for an all-day event.
   *
   * This method is called when we've already parsed:
   * - "create event [subject] on [date]"
   * And now we need to parse the recurring pattern.
   */
  private void parseRecurringAllDayEvent(String subject, LocalDateTime eventDate,
                                         String[] parts) {
    // Confirm the "repeats" keyword is where we expect it
    validateKeyword(parts[1], REPEATS, "date");

    // Weekdays come right after "repeats", so index 2
    int weekdaysIndex = 2;

    // Need at least 3 more elements after weekdays
    validateMinimumLength(parts, weekdaysIndex + 3, "Incomplete recurring event");

    // Parse which days of the week this event repeats on
    ArrayList<DayOfWeek> weekdays = parseWeekdays(parts[weekdaysIndex]);

    // Next word determines recurrence type: "for" (count) or "until" (date)
    String recurType = parts[weekdaysIndex + 1].toLowerCase();

    if (recurType.equals(FOR)) {
      // Parse the count number at weekdaysIndex + 2
      int count = parseCount(parts[weekdaysIndex + 2]);

      // Verify the word "times" appears after the count
      validateTimesKeyword(parts, weekdaysIndex + 3);

      // Create the recurring all-day events with specified count
      model.createRecurringAllDayEvent(subject, eventDate, weekdays, count);

    } else if (recurType.equals(UNTIL)) {
      // Parse the end date at weekdaysIndex + 2
      LocalDateTime untilDate = parseDate(parts[weekdaysIndex + 2]);

      // Create recurring all-day events until the specified date
      model.createRecurringAllDayEventUntil(subject, eventDate, weekdays, untilDate);

    } else {
      // Neither "for" nor "until" - invalid syntax
      throw new IllegalArgumentException("Expected 'for' or 'until' after weekdays but found '" +
              parts[weekdaysIndex + 1] + "'. Use 'for [count] times' or 'until [date]'");
    }
  }
}