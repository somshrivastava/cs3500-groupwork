package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Comparator;

/**
 * Implementation of the calendar model that manages a collection of events.
 * This class handles the creation, editing, and querying of all calendar events.
 */
public class CalendarModel implements ICalendarModel {
  private final Set<IEvent> events;
  private Integer nextSeriesId = 1;

  // Constants for all-day events
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);  // 8 AM
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);   // 5 PM

  /**
   * Constructs a new CalendarModel with an empty set of events.
   */
  public CalendarModel() {
    this.events = new HashSet<IEvent>();
  }

  @Override
  public void createSingleTimedEvent(String subject, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    addTimedEvent(subject, startDateTime, endDateTime, null);
  }

  @Override
  public void createSingleAllDayEvent(String subject, LocalDateTime date) {
    validateAllDayEvent(subject, date);
    addAllDayEvent(subject, date, null);
  }

  @Override
  public void createRecurringTimedEvent(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                        int count) {
    validateRecurringTimedEvent(subject, startDateTime, endDateTime, weekdays, count);
    ensureSingleDayEvent(startDateTime, endDateTime);

    Integer seriesId = nextSeriesId++;
    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;
    int occurrences = 0;

    while (occurrences < count) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
        addTimedEvent(subject, currentStart, currentEnd, seriesId);
        occurrences++;
      }
      currentStart = currentStart.plusDays(1);
      currentEnd = currentEnd.plusDays(1);
    }
  }

  @Override
  public void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime,
                                             LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                             LocalDateTime untilDate) {
    validateRecurringTimedEventUntil(subject, startDateTime, endDateTime, weekdays, untilDate);
    ensureSingleDayEvent(startDateTime, endDateTime);

    Integer seriesId = nextSeriesId++;
    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;

    while (!currentStart.toLocalDate().isAfter(untilDate.toLocalDate())) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
        addTimedEvent(subject, currentStart, currentEnd, seriesId);
      }
      currentStart = currentStart.plusDays(1);
      currentEnd = currentEnd.plusDays(1);
    }
  }

  @Override
  public void createRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                         ArrayList<DayOfWeek> weekdays, int count) {
    validateRecurringAllDayEvent(subject, startDate, weekdays, count);

    Integer seriesId = nextSeriesId++;
    LocalDateTime currentDate = startDate;
    int occurrences = 0;

    while (occurrences < count) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        addAllDayEvent(subject, currentDate, seriesId);
        occurrences++;
      }
      currentDate = currentDate.plusDays(1);
    }
  }

  @Override
  public void createRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                              ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateRecurringAllDayEventUntil(subject, startDate, weekdays, untilDate);

    Integer seriesId = nextSeriesId++;
    LocalDateTime currentDate = startDate;

    while (!currentDate.toLocalDate().isAfter(untilDate.toLocalDate())) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        addAllDayEvent(subject, currentDate, seriesId);
      }
      currentDate = currentDate.plusDays(1);
    }
  }

  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                        String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, endDateTime);
    updateEventProperty(eventToEdit, property, newValue, null);
  }

  @Override
  public void editEvents(String subject, LocalDateTime startDateTime, String property,
                         String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    editSeriesEvents(eventToEdit, property, newValue, true);
  }

  @Override
  public void editSeries(String subject, LocalDateTime startDateTime, String property,
                         String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    editSeriesEvents(eventToEdit, property, newValue, false);
  }

  @Override
  public List<IEvent> printEvents(LocalDateTime date) {
    LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
    return getEventsInInterval(startOfDay, endOfDay);
  }

  @Override
  public List<IEvent> printEvents(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return getEventsInInterval(startDateTime, endDateTime);
  }

  @Override
  public boolean showStatus(LocalDateTime dateTime) {
    for (IEvent event : events) {
      if (isTimeWithinEvent(dateTime, event)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates and adds a timed event without validation.
   * Handles duplicate checking and event creation.
   */
  private void addTimedEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime, Integer seriesId) {
    checkForDuplicateEvent(subject, startDateTime, endDateTime);

    IEvent newEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .seriesId(seriesId)
            .build();
    this.events.add(newEvent);
  }

  /**
   * Creates and adds an all-day event without validation.
   * Handles time conversion, duplicate checking, and event creation.
   */
  private void addAllDayEvent(String subject, LocalDateTime date, Integer seriesId) {
    LocalDateTime startOfDay = getAllDayStart(date);
    LocalDateTime endOfDay = getAllDayEnd(date);

    checkForDuplicateEvent(subject, startOfDay, endOfDay);

    IEvent newEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startOfDay)
            .endDateTime(endOfDay)
            .seriesId(seriesId)
            .build();
    this.events.add(newEvent);
  }

  /**
   * Gets the start time for an all-day event on the given date.
   */
  private LocalDateTime getAllDayStart(LocalDateTime date) {
    return date.toLocalDate().atTime(ALL_DAY_START);
  }

  /**
   * Gets the end time for an all-day event on the given date.
   */
  private LocalDateTime getAllDayEnd(LocalDateTime date) {
    return date.toLocalDate().atTime(ALL_DAY_END);
  }

  /**
   * Checks if a duplicate event exists with the same subject, start, and end times.
   * @throws IllegalArgumentException if a duplicate is found
   */
  private void checkForDuplicateEvent(String subject, LocalDateTime startDateTime,
                                      LocalDateTime endDateTime) {
    IEvent tempEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .build();

    if (events.contains(tempEvent)) {
      throw new IllegalArgumentException(
              "An event with the same subject, start time, and end time already exists");
    }
  }

  /**
   * Finds an event by subject and start time, optionally matching end time.
   * @return the matching event
   * @throws IllegalArgumentException if no event is found
   */
  private IEvent findEvent(String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime) {
    for (IEvent event : events) {
      if (event.getSubject().equals(subject) &&
              event.getStartDateTime().equals(startDateTime)) {
        if (endDateTime == null || event.getEndDateTime().equals(endDateTime)) {
          return event;
        }
      }
    }
    throw new IllegalArgumentException("Event not found");
  }

  /**
   * Edits events in a series based on the given criteria.
   * @param baseEvent the event used as reference for the edit
   * @param property the property to edit
   * @param newValue the new value for the property
   * @param fromThisEventForward if true, only edits events from the base event forward
   */
  private void editSeriesEvents(IEvent baseEvent, String property, String newValue,
                                boolean fromThisEventForward) {
    // if event is not part of series, just edit that single event
    if (baseEvent.getSeriesId() == null) {
      updateEventProperty(baseEvent, property, newValue, null);
      return;
    }

    Integer seriesId = baseEvent.getSeriesId();
    Integer newSeriesId = null;

    // Only create new series ID if start time is actually changing
    if (property.equals("start")) {
      LocalDateTime newStart = parseDateTime(newValue);
      if (!newStart.equals(baseEvent.getStartDateTime())) {
        newSeriesId = nextSeriesId++;
      }
    }

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<>(events);
    LocalDateTime baseDate = baseEvent.getStartDateTime();

    for (IEvent event : eventsCopy) {
      if (event.getSeriesId() != null && event.getSeriesId().equals(seriesId)) {
        boolean shouldEdit = !fromThisEventForward ||
                !event.getStartDateTime().toLocalDate().isBefore(baseDate.toLocalDate());

        if (shouldEdit) {
          updateEventProperty(event, property, newValue, newSeriesId);
        }
      }
    }
  }

  /**
   * Updates a single property of an event, creating a new event instance.
   * @param event the event to update
   * @param property the property name to update
   * @param newValue the new value as a string
   * @param newSeriesId the new series ID (if changing series membership)
   */
  private void updateEventProperty(IEvent event, String property, String newValue,
                                   Integer newSeriesId) {
    // Parse and validate the new value based on property type
    Object parsedValue = parsePropertyValue(property, newValue, event);

    // Create a copy of the event with the property changed
    IEvent updatedEvent = copyEventWithChange(event, property, parsedValue, newSeriesId);

    // Replace the old event with the updated one
    events.remove(event);
    events.add(updatedEvent);
  }

  /**
   * Parses a property value from string format to the appropriate type.
   * @param property the property name
   * @param newValue the string value to parse
   * @param event the original event (for validation context)
   * @return the parsed value as the appropriate type
   * @throws IllegalArgumentException if the value is invalid
   */
  private Object parsePropertyValue(String property, String newValue, IEvent event) {
    switch (property.toLowerCase()) {
      case "subject":
      case "description":
        return newValue;
      case "start":
        return parseDateTime(newValue);
      case "end":
        LocalDateTime newEnd = parseDateTime(newValue);
        validateStartBeforeEnd(event.getStartDateTime(), newEnd);
        return newEnd;
      case "location":
        return parseLocation(newValue);
      case "status":
        return parseStatus(newValue);
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

  /**
   * Creates a copy of an event with one property changed.
   * @param original the original event
   * @param property the property to change
   * @param newValue the new value for the property
   * @param newSeriesId the new series ID (or null to keep existing)
   * @return a new event instance with the property changed
   */
  private IEvent copyEventWithChange(IEvent original, String property,
                                     Object newValue, Integer newSeriesId) {
    Event.EventBuilder builder = Event.getBuilder()
            .subject(original.getSubject())
            .startDateTime(original.getStartDateTime())
            .endDateTime(original.getEndDateTime())
            .description(original.getDescription())
            .location(original.getLocation())
            .status(original.getStatus())
            .seriesId(newSeriesId != null ? newSeriesId : original.getSeriesId());

    switch (property.toLowerCase()) {
      case "subject":
        builder.subject((String) newValue);
        break;
      case "start":
        LocalDateTime newStart = (LocalDateTime) newValue;
        if (original.getSeriesId() != null) {
          newStart = original.getStartDateTime().toLocalDate()
                  .atTime(newStart.toLocalTime());
        }
        builder.startDateTime(newStart);
        long duration = java.time.Duration.between(
                original.getStartDateTime(),
                original.getEndDateTime()
        ).toMinutes();
        LocalDateTime newEnd = newStart.plusMinutes(duration);
        builder.endDateTime(newEnd);
        break;
      case "end":
        builder.endDateTime((LocalDateTime) newValue);
        break;
      case "description":
        builder.description((String) newValue);
        break;
      case "location":
        builder.location((EventLocation) newValue);
        break;
      case "status":
        builder.status((EventStatus) newValue);
        break;
    }
    return builder.build();
  }

  /**
   * Gets all events that overlap with the given time interval, sorted by start time.
   * @param startTime the start of the interval
   * @param endTime the end of the interval
   * @return a sorted list of events that overlap with the interval
   */
  private List<IEvent> getEventsInInterval(LocalDateTime startTime, LocalDateTime endTime) {
    List<IEvent> eventsInInterval = new ArrayList<>();
    for (IEvent event : events) {
      if (eventsOverlap(event, startTime, endTime)) {
        eventsInInterval.add(event);
      }
    }
    // Sort events by start time, then by subject if start times are equal
    eventsInInterval.sort(
            Comparator.comparing(IEvent::getStartDateTime)
    );
    return eventsInInterval;
  }

  /**
   * Checks if an event overlaps with a time interval.
   * @param event the event to check
   * @param intervalStart the start of the interval
   * @param intervalEnd the end of the interval
   * @return true if the event overlaps with the interval
   */
  private boolean eventsOverlap(IEvent event, LocalDateTime intervalStart,
                                LocalDateTime intervalEnd) {
    return !event.getStartDateTime().isAfter(intervalEnd) &&
            !event.getEndDateTime().isBefore(intervalStart);
  }

  /**
   * Checks if a specific time falls within an event's duration.
   * @param time the time to check
   * @param event the event to check against
   * @return true if the time is within the event's duration
   */
  private boolean isTimeWithinEvent(LocalDateTime time, IEvent event) {
    return !time.isBefore(event.getStartDateTime()) &&
            !time.isAfter(event.getEndDateTime());
  }

  /**
   * Ensures that an event starts and ends on the same day (for series events).
   * @param start the start time
   * @param end the end time
   * @throws IllegalArgumentException if not on the same day
   */
  private void ensureSingleDayEvent(LocalDateTime start, LocalDateTime end) {
    if (!start.toLocalDate().equals(end.toLocalDate())) {
      throw new IllegalArgumentException(
              "Events in a series must start and end on the same day");
    }
  }

  /**
   * Parses a string into a LocalDateTime.
   * @param dateTimeStr the string to parse
   * @return the parsed LocalDateTime
   * @throws IllegalArgumentException if the format is invalid
   */
  private LocalDateTime parseDateTime(String dateTimeStr) {
    try {
      return LocalDateTime.parse(dateTimeStr);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date-time format: " + dateTimeStr);
    }
  }

  /**
   * Parses a string into an EventLocation.
   * @param locationStr the string to parse
   * @return the parsed EventLocation
   * @throws IllegalArgumentException if the value is invalid
   */
  private EventLocation parseLocation(String locationStr) {
    try {
      return EventLocation.valueOf(locationStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
              "Invalid location: " + locationStr + ". Valid values are: PHYSICAL, ONLINE");
    }
  }

  /**
   * Parses a string into an EventStatus.
   * @param statusStr the string to parse
   * @return the parsed EventStatus
   * @throws IllegalArgumentException if the value is invalid
   */
  private EventStatus parseStatus(String statusStr) {
    try {
      return EventStatus.valueOf(statusStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
              "Invalid status: " + statusStr + ". Valid values are: PUBLIC, PRIVATE");
    }
  }

  /**
   * Validates that a subject is not null or empty.
   * @param subject the subject to validate
   * @throws IllegalArgumentException if invalid
   */
  private void validateSubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }
  }

  /**
   * Validates that a date/time is not null.
   * @param dateTime the date/time to validate
   * @param fieldName the name of the field (for error messages)
   * @throws IllegalArgumentException if null
   */
  private void validateDateTime(LocalDateTime dateTime, String fieldName) {
    if (dateTime == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }
  }

  /**
   * Validates that start time is before end time.
   * @param start the start time
   * @param end the end time
   * @throws IllegalArgumentException if end is not after start
   */
  private void validateStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
    if (!end.isAfter(start)) {
      throw new IllegalArgumentException("End time must be after start time");
    }
  }

  /**
   * Validates parameters for a timed event.
   * @param subject the event subject
   * @param startDateTime the start time
   * @param endDateTime the end time
   * @throws IllegalArgumentException if any parameter is invalid
   */
  private void validateTimedEvent(String subject, LocalDateTime startDateTime,
                                  LocalDateTime endDateTime) {
    validateSubject(subject);
    validateDateTime(startDateTime, "Start time");
    validateDateTime(endDateTime, "End time");
    validateStartBeforeEnd(startDateTime, endDateTime);
  }

  /**
   * Validates parameters for an all-day event.
   * @param subject the event subject
   * @param date the event date
   * @throws IllegalArgumentException if any parameter is invalid
   */
  private void validateAllDayEvent(String subject, LocalDateTime date) {
    validateSubject(subject);
    validateDateTime(date, "Date");
  }

  /**
   * Validates that weekdays list is not null or empty.
   * @param weekdays the list to validate
   * @throws IllegalArgumentException if null or empty
   */
  private void validateWeekdays(ArrayList<DayOfWeek> weekdays) {
    if (weekdays == null || weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
  }

  /**
   * Validates that count is positive.
   * @param count the count to validate
   * @throws IllegalArgumentException if not positive
   */
  private void validateCount(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }
  }

  /**
   * Validates that until date is after start date.
   * @param startDate the start date
   * @param untilDate the until date
   * @throws IllegalArgumentException if until date is not after start date
   */
  private void validateUntilDate(LocalDateTime startDate, LocalDateTime untilDate) {
    validateDateTime(untilDate, "Until date");
    if (!untilDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Until date must be after start date");
    }
  }

  /**
   * Validates parameters for a recurring timed event with count.
   */
  private void validateRecurringTimedEvent(String subject, LocalDateTime startDateTime,
                                           LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                           int count) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates parameters for a recurring timed event with until date.
   */
  private void validateRecurringTimedEventUntil(String subject, LocalDateTime startDateTime,
                                                LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                                LocalDateTime untilDate) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateUntilDate(startDateTime, untilDate);
  }

  /**
   * Validates parameters for a recurring all-day event with count.
   */
  private void validateRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                            ArrayList<DayOfWeek> weekdays, int count) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates parameters for a recurring all-day event with until date.
   */
  private void validateRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                                 ArrayList<DayOfWeek> weekdays,
                                                 LocalDateTime untilDate) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateUntilDate(startDate, untilDate);
  }
}