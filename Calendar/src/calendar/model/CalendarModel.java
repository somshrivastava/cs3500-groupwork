package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the calendar model that manages a collection of events.
 * This class handles the creation and storage of calendar events, both single and recurring.
 */
public class CalendarModel implements ICalendarModel {
  private final Set<IEvent> events;
  private Integer nextSeriesId = 1; // Fixed: removed static

  // Constants for all-day events as per assignment requirements
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);  // 8 AM
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);   // 5 PM

  /**
   * Constructs a new CalendarModel with an empty set of events.
   */
  public CalendarModel() {
    this.events = new HashSet<IEvent>();
  }

  @Override
  public void createSingleTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    checkForDuplicateEvent(subject, startDateTime, endDateTime);

    IEvent newEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .build();
    this.events.add(newEvent);
  }

  @Override
  public void createSingleAllDayEvent(String subject, LocalDateTime date) {
    validateAllDayEvent(subject, date);

    // Fixed: Use 8am-5pm for all-day events as per assignment
    LocalDateTime startOfDay = date.toLocalDate().atTime(ALL_DAY_START);
    LocalDateTime endOfDay = date.toLocalDate().atTime(ALL_DAY_END);

    // Fixed: Add duplicate check for all-day events
    checkForDuplicateEvent(subject, startOfDay, endOfDay);

    IEvent newEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startOfDay)
            .endDateTime(endOfDay)
            .build();
    this.events.add(newEvent);
  }

  @Override
  public void createRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                        ArrayList<DayOfWeek> weekdays, int count) {
    validateRecurringTimedEvent(subject, startDateTime, endDateTime, weekdays, count);

    // Fixed: Add validation for single-day constraint in series
    if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
      throw new IllegalArgumentException("Events in a series must start and end on the same day");
    }

    Integer seriesId = nextSeriesId++;

    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;
    int occurrences = 0;

    while (occurrences < count) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
        // Fixed: Check for duplicates before creating each event
        checkForDuplicateEvent(subject, currentStart, currentEnd);

        IEvent newEvent = Event.getBuilder()
                .subject(subject)
                .startDateTime(currentStart)
                .endDateTime(currentEnd)
                .seriesId(seriesId)
                .build();
        this.events.add(newEvent);
        occurrences++;
      }
      currentStart = currentStart.plusDays(1);
      currentEnd = currentEnd.plusDays(1);
    }
  }

  @Override
  public void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                             ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateRecurringTimedEventUntil(subject, startDateTime, endDateTime, weekdays, untilDate);

    // Fixed: Add validation for single-day constraint in series
    if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
      throw new IllegalArgumentException("Events in a series must start and end on the same day");
    }

    Integer seriesId = nextSeriesId++;

    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;

    // Fixed: Compare dates only, not times, for "until" logic (inclusive)
    while (!currentStart.toLocalDate().isAfter(untilDate.toLocalDate())) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
        // Fixed: Check for duplicates before creating each event
        checkForDuplicateEvent(subject, currentStart, currentEnd);

        IEvent newEvent = Event.getBuilder()
                .subject(subject)
                .startDateTime(currentStart)
                .endDateTime(currentEnd)
                .seriesId(seriesId)
                .build();
        this.events.add(newEvent);
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
        // Fixed: Use 8am-5pm for all-day events
        LocalDateTime startOfDay = currentDate.toLocalDate().atTime(ALL_DAY_START);
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(ALL_DAY_END);

        // Fixed: Check for duplicates before creating each event
        checkForDuplicateEvent(subject, startOfDay, endOfDay);

        IEvent newEvent = Event.getBuilder()
                .subject(subject)
                .startDateTime(startOfDay)
                .endDateTime(endOfDay)
                .seriesId(seriesId)
                .build();
        this.events.add(newEvent);
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

    // Fixed: Compare dates only, not times, for "until" logic (inclusive)
    while (!currentDate.toLocalDate().isAfter(untilDate.toLocalDate())) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        // Fixed: Use 8am-5pm for all-day events
        LocalDateTime startOfDay = currentDate.toLocalDate().atTime(ALL_DAY_START);
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(ALL_DAY_END);

        // Fixed: Check for duplicates before creating each event
        checkForDuplicateEvent(subject, startOfDay, endOfDay);

        IEvent newEvent = Event.getBuilder()
                .subject(subject)
                .startDateTime(startOfDay)
                .endDateTime(endOfDay)
                .seriesId(seriesId)
                .build();
        this.events.add(newEvent);
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
  public void editEvents(String subject, LocalDateTime startDateTime, String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    Integer seriesId = eventToEdit.getSeriesId();

    if (seriesId == null) {
      // Single event, just edit it
      updateEventProperty(eventToEdit, property, newValue, null);
      return;
    }

    // Fixed: When editing start time, give affected events a new series ID
    Integer newSeriesId = null;
    if (property.equals("start")) {
      newSeriesId = nextSeriesId++;
    }

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<>(events);
    for (IEvent event : eventsCopy) {
      if (event.getSeriesId() != null &&
              event.getSeriesId().equals(seriesId) &&
              !event.getStartDateTime().isBefore(startDateTime)) {
        updateEventProperty(event, property, newValue, newSeriesId);
      }
    }
  }

  @Override
  public void editSeries(String subject, LocalDateTime startDateTime, String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    Integer seriesId = eventToEdit.getSeriesId();

    if (seriesId == null) {
      // Single event, just edit it
      updateEventProperty(eventToEdit, property, newValue, null);
      return;
    }

    // Fixed: When editing start time, give all events a new series ID
    Integer newSeriesId = null;
    if (property.equals("start")) {
      newSeriesId = nextSeriesId++;
    }

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<>(events);
    for (IEvent event : eventsCopy) {
      if (event.getSeriesId() != null && event.getSeriesId().equals(seriesId)) {
        updateEventProperty(event, property, newValue, newSeriesId);
      }
    }
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
      if (!dateTime.isBefore(event.getStartDateTime()) && !dateTime.isAfter(event.getEndDateTime())) {
        return true;
      }
    }
    return false;
  }

  private void checkForDuplicateEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    for (IEvent event : events) {
      if (event.getSubject().equals(subject) &&
              event.getStartDateTime().equals(startDateTime) &&
              event.getEndDateTime().equals(endDateTime)) {
        throw new IllegalArgumentException("An event with the same subject, start time, and end time already exists");
      }
    }
  }

  private IEvent findEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (endDateTime == null) {
      // For series edits, find the event that starts exactly at startDateTime
      for (IEvent event : events) {
        if (event.getSubject().equals(subject) &&
                event.getStartDateTime().equals(startDateTime)) {
          return event;
        }
      }
    } else {
      // For single event edits, find exact match
      for (IEvent event : events) {
        if (event.getSubject().equals(subject) &&
                event.getStartDateTime().equals(startDateTime) &&
                event.getEndDateTime().equals(endDateTime)) {
          return event;
        }
      }
    }
    throw new IllegalArgumentException("Event not found");
  }

  private void updateEventProperty(IEvent event, String property, String newValue, Integer newSeriesId) {
    IEvent updatedEvent;

    switch (property.toLowerCase()) {
      case "subject":
        updatedEvent = Event.getBuilder()
                .subject(newValue)
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(newSeriesId != null ? newSeriesId : event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        break;

      case "start":
        LocalDateTime newStart;
        try {
          newStart = LocalDateTime.parse(newValue);
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("Invalid date-time format: " + newValue);
        }
        if (!newStart.isBefore(event.getEndDateTime())) {
          throw new IllegalArgumentException("New start time must be before end time");
        }
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(newStart)
                .endDateTime(event.getEndDateTime())
                .seriesId(newSeriesId != null ? newSeriesId : event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        break;

      case "end":
        LocalDateTime newEnd;
        try {
          newEnd = LocalDateTime.parse(newValue);
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("Invalid date-time format: " + newValue);
        }
        if (!newEnd.isAfter(event.getStartDateTime())) {
          throw new IllegalArgumentException("New end time must be after start time");
        }
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(newEnd)
                .seriesId(newSeriesId != null ? newSeriesId : event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        break;

      case "description":
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(newSeriesId != null ? newSeriesId : event.getSeriesId())
                .description(newValue)
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        break;

      case "location":
        EventLocation newLocation;
        try {
          newLocation = EventLocation.valueOf(newValue.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid location: " + newValue + ". Valid values are: PHYSICAL, ONLINE");
        }
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(newSeriesId != null ? newSeriesId : event.getSeriesId())
                .description(event.getDescription())
                .location(newLocation)
                .status(event.getStatus())
                .build();
        break;

      case "status":
        EventStatus newStatus;
        try {
          newStatus = EventStatus.valueOf(newValue.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid status: " + newValue + ". Valid values are: PUBLIC, PRIVATE");
        }
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(newSeriesId != null ? newSeriesId : event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(newStatus)
                .build();
        break;

      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }

    events.remove(event);
    events.add(updatedEvent);
  }

  private List<IEvent> getEventsInInterval(LocalDateTime startTime, LocalDateTime endTime) {
    List<IEvent> eventsInInterval = new ArrayList<>();
    for (IEvent event : events) {
      if (!event.getStartDateTime().isAfter(endTime) &&
              !event.getEndDateTime().isBefore(startTime)) {
        eventsInInterval.add(event);
      }
    }
    return eventsInInterval;
  }

  private void validateSubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }
  }

  private void validateDateTime(LocalDateTime dateTime, String fieldName) {
    if (dateTime == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }
  }

  private void validateTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    validateSubject(subject);
    validateDateTime(startDateTime, "Start time");
    validateDateTime(endDateTime, "End time");
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }
  }

  private void validateAllDayEvent(String subject, LocalDateTime date) {
    validateSubject(subject);
    validateDateTime(date, "Date");
  }

  private void validateWeekdays(ArrayList<DayOfWeek> weekdays) {
    if (weekdays == null || weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
  }

  private void validateCount(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }
  }

  private void validateUntilDate(LocalDateTime startDate, LocalDateTime untilDate) {
    validateDateTime(untilDate, "Until date");
    if (!untilDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Until date must be after start date");
    }
  }

  private void validateRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                           ArrayList<DayOfWeek> weekdays, int count) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  private void validateRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateUntilDate(startDateTime, untilDate);
  }

  private void validateRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                            ArrayList<DayOfWeek> weekdays, int count) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  private void validateRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                                 ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateUntilDate(startDate, untilDate);
  }
}