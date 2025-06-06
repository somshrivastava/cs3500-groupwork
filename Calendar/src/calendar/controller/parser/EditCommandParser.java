package calendar.controller.parser;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Parser for edit commands.
 * Handles editing single events, event series from a point, and entire series.
 */
class EditCommandParser extends AbstractCommandParser {
  // Specific indices for edit command structure
  private static final int MIN_EDIT_COMMAND_LENGTH = 7;
  private static final int EDIT_TYPE_INDEX = 1;
  private static final int PROPERTY_INDEX = 2;
  private static final int SUBJECT_INDEX = 3;

  public EditCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    String[] parts = commandLine.trim().split("\\s+");
    validateMinimumLength(parts, MIN_EDIT_COMMAND_LENGTH, "Incomplete edit command. Format: " +
            "edit event [property] \"subject\" from [start] to [end] with [value]");

    String editType = validateEditType(parts[EDIT_TYPE_INDEX]);
    String property = validateProperty(parts[PROPERTY_INDEX]);

    // Get the subject
    int subjectEndIndex = extractQuotedText(parts, SUBJECT_INDEX);
    String subject = buildQuotedText(parts, SUBJECT_INDEX, subjectEndIndex);

    // Ensure "from" keyword follows the subject
    validateKeyword(parts[subjectEndIndex], FROM, "subject");

    // Start time is the word after the subject
    LocalDateTime startTime = parseDateTime(parts[subjectEndIndex + 1]);

    // Route based on edit type
    if (editType.equals(EVENT)) {
      parseEditSingleEvent(parts, subjectEndIndex + 2, subject, startTime, property);
    } else {
      parseEditSeriesEvent(parts, subjectEndIndex + 2, subject, startTime, property, editType);
    }
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
   * Parses single event edit command.
   */
  private void parseEditSingleEvent(String[] parts, int index, String subject,
                                    LocalDateTime startTime, String property) {
    final int TO_OFFSET = 0;
    final int END_TIME_OFFSET = 1;
    final int WITH_OFFSET = 2;
    final int VALUE_OFFSET = 3;

    // Validate we have the "to" keyword where expected
    validateKeyword(parts[index + TO_OFFSET], TO, "start time in single event edit");

    // Parse the end time
    LocalDateTime endTime = parseDateTime(parts[index + END_TIME_OFFSET]);

    // Validate the "with" keyword appears before the new value
    validateKeyword(parts[index + WITH_OFFSET], WITH, "new value");

    // Extract the new value
    String newValue = extractNewValue(parts, index + VALUE_OFFSET);

    // Call model to edit this specific single event
    model.editEvent(subject, startTime, endTime, property, newValue);
  }

  /**
   * Parses series event edit command.
   */
  private void parseEditSeriesEvent(String[] parts, int index, String subject,
                                    LocalDateTime startTime, String property, String editType) {
    final int WITH_OFFSET = 0;
    final int VALUE_OFFSET = 1;

    // Validate the "with" keyword appears where expected
    validateKeyword(parts[index + WITH_OFFSET], WITH, "new value");

    // Extract the new value
    String newValue = extractNewValue(parts, index + VALUE_OFFSET);

    // Route to the appropriate model method based on edit scope
    if (editType.equals(EVENTS)) {
      // "edit events" - changes this occurrence and all future occurrences
      model.editEvents(subject, startTime, property, newValue);
    } else {
      // "edit series" - changes ALL occurrences in the series
      model.editSeries(subject, startTime, property, newValue);
    }
  }

  /**
   * Extracts the new value from the command parts.
   * @param parts the command parts
   * @param valueIndex the starting index of the value
   * @return the extracted value
   */
  private String extractNewValue(String[] parts, int valueIndex) {
    int valueEndIndex = extractQuotedText(parts, valueIndex);
    return buildQuotedText(parts, valueIndex, valueEndIndex);
  }
}