package calendar.controller.parser;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Parser for edit commands.
 * Handles editing single events, event series from a point, and entire series.
 */
class EditCommandParser extends AbstractCommandParser {

  public EditCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String[] parts) throws IllegalArgumentException {
    validateMinimumLength(parts, 7, "Incomplete edit command. Format: " +
            "edit event [property] \"subject\" from [start] to [end] with [value]");

    String editType = validateEditType(parts[1]);
    String property = validateProperty(parts[2]);

    // Get the subject
    int subjectEndIndex = extractQuotedText(parts, 3);
    String subject = buildQuotedText(parts, 3, subjectEndIndex);

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
   *
   * This method is called after we've parsed:
   * - "edit event [property] [subject] from [startTime]"
   */
  private void parseEditSingleEvent(String[] parts, int index, String subject,
                                    LocalDateTime startTime, String property) {
    // Validate we have the "to" keyword where expected
    validateKeyword(parts[index], TO, "start time in single event edit");

    // Parse the end time
    LocalDateTime endTime = parseDateTime(parts[index + 1]);

    // Validate the "with" keyword appears before the new value
    validateKeyword(parts[index + 2], WITH, "new value");

    // Extract the new value
    int valueIndex = index + 3;
    int valueEndIndex = extractQuotedText(parts, valueIndex);
    String newValue = buildQuotedText(parts, valueIndex, valueEndIndex);

    // Call model to edit this specific single event
    model.editEvent(subject, startTime, endTime, property, newValue);
  }

  /**
   * Parses series event edit command.
   *
   * This method handles both:
   * 1. "edit events" - edits from a specific occurrence forward in the series
   * 2. "edit series" - edits all occurrences in the series
   */
  private void parseEditSeriesEvent(String[] parts, int index, String subject,
                                    LocalDateTime startTime, String property, String editType) {
    // Validate the "with" keyword appears where expected
    validateKeyword(parts[index], WITH, "new value");

    // Extract the new value
    int valueIndex = index + 1;
    int valueEndIndex = extractQuotedText(parts, valueIndex);
    String newValue = buildQuotedText(parts, valueIndex, valueEndIndex);

    // Route to the appropriate model method based on edit scope
    if (editType.equals(EVENTS)) {
      // "edit events" - changes this occurrence and all future occurrences
      model.editEvents(subject, startTime, property, newValue);
    } else {
      // "edit series" - changes ALL occurrences in the series
      model.editSeries(subject, startTime, property, newValue);
    }
  }
}