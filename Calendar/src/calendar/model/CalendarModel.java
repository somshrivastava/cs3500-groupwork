package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    
    LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = date.toLocalDate().atTime(LocalTime.MAX);
    
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
    Integer seriesId = nextSeriesId++;
    
    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;
    int occurrences = 0;
    
    while (occurrences < count) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
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
    Integer seriesId = nextSeriesId++;
    
    LocalDateTime currentStart = startDateTime;
    LocalDateTime currentEnd = endDateTime;
    
    while (!currentStart.isAfter(untilDate)) {
      if (weekdays.contains(currentStart.getDayOfWeek())) {
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
        LocalDateTime startOfDay = currentDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(LocalTime.MAX);
        
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
    
    while (!currentDate.isAfter(untilDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime startOfDay = currentDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(LocalTime.MAX);
        
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
    updateEventProperty(eventToEdit, property, newValue);
  }

  @Override
  public void editEvents(String subject, LocalDateTime startDateTime, String property, String newValue) {
    IEvent eventToEdit = findEvent(subject, startDateTime, null);
    Integer seriesId = eventToEdit.getSeriesId();
    
    if (seriesId == null) {
      editEvent(subject, startDateTime, null, property, newValue);
      return;
    }

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<>(events);
    for (IEvent event : eventsCopy) {
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
      editEvent(subject, startDateTime, null, property, newValue);
      return;
    }

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<>(events);
    for (IEvent event : eventsCopy) {
      if (event.getSeriesId() != null && event.getSeriesId().equals(seriesId)) {
        updateEventProperty(event, property, newValue);
      }
    }
  }

  @Override
  public List<IEvent> printEvents(LocalDateTime date) {
    LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = date.toLocalDate().atTime(LocalTime.MAX);
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
      // For series edits, find the first event that matches the subject and is on or after startDateTime
      IEvent nextEvent = null;
      for (IEvent event : events) {
        if (event.getSubject().equals(subject) && 
            (event.getStartDateTime().equals(startDateTime) || 
             event.getStartDateTime().isAfter(startDateTime))) {
          if (nextEvent == null || event.getStartDateTime().isBefore(nextEvent.getStartDateTime())) {
            nextEvent = event;
          }
        }
      }
      if (nextEvent != null) {
        return nextEvent;
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

  private void updateEventProperty(IEvent event, String property, String newValue) {
    switch (property.toLowerCase()) {
      case "subject":
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
