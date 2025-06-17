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
 * This class handles the creation, editing, and querying of calendar events,
 * both single and recurring.
 */
public class CalendarModel implements ICalendarModel {
  protected final Set<IEvent> events;
  private final EventValidator validator;
  protected Integer nextSeriesId = 1;

  // Constants for all-day events as per assignment requirements
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);  // 8 AM
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);   // 5 PM

  /**
   * Constructs a new CalendarModel with an empty set of events.
   */
  public CalendarModel() {
    this.events = new HashSet<IEvent>();
    this.validator = new EventValidator();
  }

  /**
   * Creates a single timed event.
   * @param subject the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime the end date and time of the event
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void createSingleTimedEvent(String subject, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime) {
    validator.validateTimedEvent(subject, startDateTime, endDateTime);
    addTimedEvent(subject, startDateTime, endDateTime, null);
  }

  /**
   * Creates a single all-day event.
   * @param subject the subject/title of the event
   * @param date the date of the event
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void createSingleAllDayEvent(String subject, LocalDateTime date) {
    validator.validateAllDayEvent(subject, date);
    addAllDayEvent(subject, date, null);
  }

  /**
   * Creates a recurring timed event with a count.
   * @param subject the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime the end date and time of the event
   * @param weekdays the days of the week when the event occurs
   * @param count the number of occurrences
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void createRecurringTimedEvent(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, ArrayList<DayOfWeek> weekdays,
                                        int count) {
    validator.validateRecurringTimedEvent(subject, startDateTime, endDateTime, weekdays, count);
    validator.validateSingleDayEvent(startDateTime, endDateTime);

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

  /**
   * Creates a recurring timed event until a specific date.
   * @param subject the subject/title of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime the end date and time of the event
   * @param weekdays the days of the week when the event occurs
   * @param untilDate the date until which the event should recur
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime,
                                             LocalDateTime endDateTime, 
                                             ArrayList<DayOfWeek> weekdays,
                                             LocalDateTime untilDate) {
    validator.validateRecurringTimedEventUntil(subject, startDateTime, endDateTime, 
                                               weekdays, untilDate);
    validator.validateSingleDayEvent(startDateTime, endDateTime);

    Integer seriesId = nextSeriesId++;
    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;

    // Compare dates only, not times, for "until" logic
    while (!currentStart.toLocalDate().isAfter(untilDate.toLocalDate())) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
        addTimedEvent(subject, currentStart, currentEnd, seriesId);
      }
      currentStart = currentStart.plusDays(1);
      currentEnd = currentEnd.plusDays(1);
    }
  }

  /**
   * Creates a recurring all-day event with a count.
   * @param subject the subject/title of the event
   * @param startDate the start date of the event
   * @param weekdays the days of the week when the event occurs
   * @param count the number of occurrences
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void createRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                         ArrayList<DayOfWeek> weekdays, int count) {
    validator.validateRecurringAllDayEvent(subject, startDate, weekdays, count);

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

  /**
   * Creates a recurring all-day event until a specific date.
   * @param subject the subject/title of the event
   * @param startDate the start date of the event
   * @param weekdays the days of the week when the event occurs
   * @param untilDate the date until which the event should recur
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void createRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                              ArrayList<DayOfWeek> weekdays, 
                                              LocalDateTime untilDate) {
    validator.validateRecurringAllDayEventUntil(subject, startDate, weekdays, untilDate);

    Integer seriesId = nextSeriesId++;
    LocalDateTime currentDate = startDate;

    // Compare dates only, not times, for "until" logic
    while (!currentDate.toLocalDate().isAfter(untilDate.toLocalDate())) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        addAllDayEvent(subject, currentDate, seriesId);
      }
      currentDate = currentDate.plusDays(1);
    }
  }

  /**
   * Edits a single event's property.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param endDateTime the end date/time of the event to edit
   * @param property the property to edit (subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                        String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, endDateTime);
    updateEventProperty(eventToEdit, property, newValue, null);
  }

  /**
   * Edits all events in a series that start at or after the given date/time.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property the property to edit (subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  @Override
  public void editEvents(String subject, LocalDateTime startDateTime, String property,
                         String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    editSeriesEvents(eventToEdit, property, newValue, true);
  }

  /**
   * Edits all events in a series.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property the property to edit (subject, start, end, description, location, status)
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the event is not found or the property is invalid
   */
  @Override
  public void editSeries(String subject, LocalDateTime startDateTime, String property,
                         String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    editSeriesEvents(eventToEdit, property, newValue, false);
  }

  /**
   * Gets all events that occur on a specific date.
   * @param date the date to filter events for
   * @return a list of events that occur on the given date
   */
  @Override
  public List<IEvent> printEvents(LocalDateTime date) {
    LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
    return getEventsInInterval(startOfDay, endOfDay);
  }

  /**
   * Gets all events that occur within a time interval.
   * @param startDateTime the start of the interval (inclusive)
   * @param endDateTime the end of the interval (inclusive)
   * @return a list of events that overlap with the given interval
   */
  @Override
  public List<IEvent> printEvents(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return getEventsInInterval(startDateTime, endDateTime);
  }

  /**
   * Gets a limited number of events that occur on or after a specific date/time.
   * @param startDateTime the start date/time to search from (inclusive)
   * @param maxEvents the maximum number of events to return
   * @return a list of events that start on or after the given date/time, limited to maxEvents,
   *         sorted by start time
   */
  @Override
  public List<IEvent> getUpcomingEvents(LocalDateTime startDateTime, int maxEvents) {
    List<IEvent> upcomingEvents = new ArrayList<>();
    
    // Filter events that start on or after the given date/time
    for (IEvent event : events) {
      if (!event.getStartDateTime().isBefore(startDateTime)) {
        upcomingEvents.add(event);
      }
    }
    
    // Sort events by start time
    upcomingEvents.sort(Comparator.comparing(IEvent::getStartDateTime));
    
    // Limit to maxEvents
    if (upcomingEvents.size() > maxEvents) {
      upcomingEvents = upcomingEvents.subList(0, maxEvents);
    }
    
    return upcomingEvents;
  }

  /**
   * Checks if the given time is busy (has an event scheduled).
   * @param dateTime the time to check
   * @return true if there is an event at the given time, false otherwise
   */
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
    if (baseEvent.getSeriesId() == null) {
      // Single event, just edit it
      updateEventProperty(baseEvent, property, newValue, null);
      return;
    }

    Integer seriesId = baseEvent.getSeriesId();
    Integer newSeriesId = determineNewSeriesId(baseEvent, property, newValue);

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<>(events);
    LocalDateTime baseDate = baseEvent.getStartDateTime();

    for (IEvent event : eventsCopy) {
      if (shouldEditEvent(event, seriesId, baseDate, fromThisEventForward)) {
        updateEventProperty(event, property, newValue, newSeriesId);
      }
    }
  }

  /**
   * Determines if a new series ID is needed based on the property being changed.
   * @param baseEvent the base event being edited
   * @param property the property being changed
   * @param newValue the new value for the property
   * @return new series ID if needed, null otherwise
   */
  private Integer determineNewSeriesId(IEvent baseEvent, String property, String newValue) {
    if (property.equals("start")) {
      LocalDateTime newStart = parseDateTime(newValue);
      // Only compare the time portion since all events in series have different dates
      if (!newStart.toLocalTime().equals(baseEvent.getStartDateTime().toLocalTime())) {
        return nextSeriesId++;
      }
    }
    return null;
  }

  /**
   * Determines if an event should be edited based on series criteria.
   * @param event the event to check
   * @param seriesId the series ID to match
   * @param baseDate the base date for forward editing
   * @param fromThisEventForward if true, only edit from base date forward
   * @return true if the event should be edited
   */
  private boolean shouldEditEvent(IEvent event, Integer seriesId, LocalDateTime baseDate,
                                  boolean fromThisEventForward) {
    if (event.getSeriesId() == null || !event.getSeriesId().equals(seriesId)) {
      return false;
    }

    return !fromThisEventForward ||
            !event.getStartDateTime().toLocalDate().isBefore(baseDate.toLocalDate());
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
        LocalDateTime newStart = parseDateTime(newValue);
        validator.validateStartBeforeEnd(newStart, event.getEndDateTime());
        return newStart;
      case "end":
        LocalDateTime newEnd = parseDateTime(newValue);
        validator.validateStartBeforeEnd(event.getStartDateTime(), newEnd);
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
    Event.EventBuilder builder = createBuilderFromEvent(original, newSeriesId);
    applyPropertyChange(builder, original, property, newValue);
    return builder.build();
  }

  /**
   * Creates a builder initialized with values from an existing event.
   * @param original the original event
   * @param newSeriesId the new series ID (or null to keep existing)
   * @return a builder with values from the original event
   */
  private Event.EventBuilder createBuilderFromEvent(IEvent original, Integer newSeriesId) {
    return Event.getBuilder()
            .subject(original.getSubject())
            .startDateTime(original.getStartDateTime())
            .endDateTime(original.getEndDateTime())
            .description(original.getDescription())
            .location(original.getLocation())
            .status(original.getStatus())
            .seriesId(newSeriesId != null ? newSeriesId : original.getSeriesId());
  }

  /**
   * Applies a property change to an event builder.
   * @param builder the builder to modify
   * @param original the original event
   * @param property the property to change
   * @param newValue the new value for the property
   */
  private void applyPropertyChange(Event.EventBuilder builder, IEvent original,
                                   String property, Object newValue) {
    switch (property.toLowerCase()) {
      case "subject":
        builder.subject((String) newValue);
        break;
      case "start":
        applyStartTimeChange(builder, original, (LocalDateTime) newValue);
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
      default:
        // No action needed - invalid properties are validated before reaching this method
        break;
    }
  }

  /**
   * Applies a start time change to an event, only changing the start time.
   * @param builder the builder to modify
   * @param original the original event
   * @param newStart the new start time
   */
  private void applyStartTimeChange(Event.EventBuilder builder, IEvent original,
                                    LocalDateTime newStart) {
    // For series events, only change the time, keep the original date
    if (original.getSeriesId() != null) {
      newStart = original.getStartDateTime().toLocalDate()
              .atTime(newStart.toLocalTime());
    }
    builder.startDateTime(newStart);
    // Keep the original end time unchanged
    builder.endDateTime(original.getEndDateTime());
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
    // Sort events by start time
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
}