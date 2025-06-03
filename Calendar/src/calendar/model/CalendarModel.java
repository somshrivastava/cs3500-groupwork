package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
  private static Integer nextSeriesId = 1;

  /**
   * Constructs a new CalendarModel with an empty set of events.
   */
  public CalendarModel() {
    this.events = new HashSet<IEvent>();
  }

  @Override
  public void createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer seriesId) {
    // Validate inputs
    validateSubject(subject);
    validateDateTime(startDateTime, "Start time");
    validateDateTime(endDateTime, "End time");
    
    // Check for duplicate events
    for (IEvent event : events) {
      if (event.getSubject().equals(subject) && 
          event.getStartDateTime().equals(startDateTime) && 
          event.getEndDateTime().equals(endDateTime)) {
        throw new IllegalArgumentException("An event with the same subject, start time, and end time already exists");
      }
    }

    // Validate time range
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }

    IEvent newEvent = Event.getBuilder()
            .subject(subject)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .seriesId(seriesId)
            .build();
    this.events.add(newEvent);
  }

  @Override
  public Integer getNextSeriesId() {
    return nextSeriesId++;
  }

  @Override
  public void editEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, endDateTime);
    updateEventProperty(eventToEdit, property, newValue);
  }

  @Override
  public void editEvents(String subject, LocalDateTime startDateTime, String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    Integer seriesId = eventToEdit.getSeriesId();
    
    if (seriesId == null) {
      // If not part of a series, just edit the single event
      editEvent(subject, startDateTime, null, property, newValue);
      return;
    }

    // Edit all events in the series that start at or after the given date
    for (IEvent event : events) {
      if (event.getSeriesId() != null && 
          event.getSeriesId().equals(seriesId) && 
          !event.getStartDateTime().isBefore(startDateTime)) {
        updateEventProperty(event, property, newValue);
      }
    }
  }

  @Override
  public void editSeries(String subject, LocalDateTime startDateTime, String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    Integer seriesId = eventToEdit.getSeriesId();
    
    if (seriesId == null) {
      // If not part of a series, just edit the single event
      editEvent(subject, startDateTime, null, property, newValue);
      return;
    }

    // First collect all events in the series
    List<IEvent> eventsToUpdate = new ArrayList<>();
    for (IEvent event : events) {
      if (event.getSeriesId() != null && event.getSeriesId().equals(seriesId)) {
        eventsToUpdate.add(event);
      }
    }

    // Then update each event
    for (IEvent event : eventsToUpdate) {
      updateEventProperty(event, property, newValue);
    }
  }

  /**
   * Finds an event with the given subject and start time.
   * @param subject the subject to search for
   * @param startDateTime the start time to search for
   * @param endDateTime the end time to search for (may be null)
   * @return the found event, or null if not found
   */
  private IEvent findEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    for (IEvent event : events) {
      if (event.getSubject().equals(subject) &&
          event.getStartDateTime().equals(startDateTime) &&
          (endDateTime == null || event.getEndDateTime().equals(endDateTime))) {
        return event;
      }
    }
    throw new IllegalArgumentException("Event not found");
  }

  /**
   * Updates a specific property of an event.
   * @param event the event to update
   * @param property the property to update
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if the property is invalid
   */
  private void updateEventProperty(IEvent event, String property, String newValue) {
    switch (property.toLowerCase()) {
      case "subject":
        // Create a new event with the updated subject
        IEvent updatedEvent = Event.getBuilder()
                .subject(newValue)
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        events.remove(event);
        events.add(updatedEvent);
        break;
      case "start":
        LocalDateTime newStart = LocalDateTime.parse(newValue);
        if (!newStart.isBefore(event.getEndDateTime())) {
          throw new IllegalArgumentException("New start time must be before end time");
        }
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(newStart)
                .endDateTime(event.getEndDateTime())
                .seriesId(event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        events.remove(event);
        events.add(updatedEvent);
        break;
      case "end":
        LocalDateTime newEnd = LocalDateTime.parse(newValue);
        if (!newEnd.isAfter(event.getStartDateTime())) {
          throw new IllegalArgumentException("New end time must be after start time");
        }
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(newEnd)
                .seriesId(event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        events.remove(event);
        events.add(updatedEvent);
        break;
      case "description":
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(event.getSeriesId())
                .description(newValue)
                .location(event.getLocation())
                .status(event.getStatus())
                .build();
        events.remove(event);
        events.add(updatedEvent);
        break;
      case "location":
        EventLocation newLocation = EventLocation.valueOf(newValue.toUpperCase());
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(event.getSeriesId())
                .description(event.getDescription())
                .location(newLocation)
                .status(event.getStatus())
                .build();
        events.remove(event);
        events.add(updatedEvent);
        break;
      case "status":
        EventStatus newStatus = EventStatus.valueOf(newValue.toUpperCase());
        updatedEvent = Event.getBuilder()
                .subject(event.getSubject())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .seriesId(event.getSeriesId())
                .description(event.getDescription())
                .location(event.getLocation())
                .status(newStatus)
                .build();
        events.remove(event);
        events.add(updatedEvent);
        break;
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

  @Override
  public List<IEvent> getEvents() {
    return new ArrayList<>(events);
  }

  @Override
  public List<IEvent> getEventsOnDate(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
    return getEventsInInterval(startOfDay, endOfDay);
  }

  @Override
  public List<IEvent> getEventsInInterval(LocalDateTime startTime, LocalDateTime endTime) {
    List<IEvent> eventsInInterval = new ArrayList<>();
    for (IEvent event : events) {
      if (!event.getStartDateTime().isAfter(endTime) &&
          !event.getEndDateTime().isBefore(startTime)) {
        eventsInInterval.add(event);
      }
    }
    return eventsInInterval;
  }

  @Override
  public boolean isTimeBusy(LocalDateTime time) {
    for (IEvent event : events) {
      if (!time.isBefore(event.getStartDateTime()) && !time.isAfter(event.getEndDateTime())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Validates that the subject is not null or empty.
   * @param subject the subject to validate
   * @throws IllegalArgumentException if the subject is null or empty
   */
  private void validateSubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }
  }

  /**
   * Validates that a date/time is not null.
   * @param dateTime the date/time to validate
   * @param fieldName the name of the field being validated (for error messages)
   * @throws IllegalArgumentException if the date/time is null
   */
  private void validateDateTime(LocalDateTime dateTime, String fieldName) {
    if (dateTime == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }
  }

  /**
   * Validates the parameters for a timed event.
   * @param subject the subject to validate
   * @param startDateTime the start time to validate
   * @param endDateTime the end time to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    validateSubject(subject);
    validateDateTime(startDateTime, "Start time");
    validateDateTime(endDateTime, "End time");
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }
  }

  /**
   * Validates the parameters for an all-day event.
   * @param subject the subject to validate
   * @param date the date to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateAllDayEvent(String subject, LocalDateTime date) {
    validateSubject(subject);
    validateDateTime(date, "Date");
  }

  /**
   * Validates the weekdays list for recurring events.
   * @param weekdays the weekdays list to validate
   * @throws IllegalArgumentException if the list is null or empty
   */
  private void validateWeekdays(ArrayList<DayOfWeek> weekdays) {
    if (weekdays == null || weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }
  }

  /**
   * Validates the count parameter for recurring events.
   * @param count the count to validate
   * @throws IllegalArgumentException if the count is not positive
   */
  private void validateCount(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }
  }

  /**
   * Validates the until date for recurring events.
   * @param startDate the start date to validate against
   * @param untilDate the until date to validate
   * @throws IllegalArgumentException if the until date is null or not after the start date
   */
  private void validateUntilDate(LocalDateTime startDate, LocalDateTime untilDate) {
    validateDateTime(untilDate, "Until date");
    if (!untilDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Until date must be after start date");
    }
  }

  /**
   * Validates the parameters for a recurring timed event with count.
   * @param subject the subject to validate
   * @param startDateTime the start time to validate
   * @param endDateTime the end time to validate
   * @param weekdays the weekdays list to validate
   * @param count the count to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                           ArrayList<DayOfWeek> weekdays, int count) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates the parameters for a recurring timed event with until date.
   * @param subject the subject to validate
   * @param startDateTime the start time to validate
   * @param endDateTime the end time to validate
   * @param weekdays the weekdays list to validate
   * @param untilDate the until date to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateTimedEvent(subject, startDateTime, endDateTime);
    validateWeekdays(weekdays);
    validateUntilDate(startDateTime, untilDate);
  }

  /**
   * Validates the parameters for a recurring all-day event with count.
   * @param subject the subject to validate
   * @param startDate the start date to validate
   * @param weekdays the weekdays list to validate
   * @param count the count to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                            ArrayList<DayOfWeek> weekdays, int count) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateCount(count);
  }

  /**
   * Validates the parameters for a recurring all-day event with until date.
   * @param subject the subject to validate
   * @param startDate the start date to validate
   * @param weekdays the weekdays list to validate
   * @param untilDate the until date to validate
   * @throws IllegalArgumentException if any validation fails
   */
  private void validateRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                                 ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    validateAllDayEvent(subject, startDate);
    validateWeekdays(weekdays);
    validateUntilDate(startDate, untilDate);
  }
}
