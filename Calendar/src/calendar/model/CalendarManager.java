package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages multiple calendars, each with their own timezone and set of events.
 * Provides functionality to create calendars, switch between calendars, and 
 * copy events between calendars with proper timezone conversions.
 */
public class CalendarManager implements ICalendarManager {
  
  private final Map<String, ISmartCalendarModel> calendars;
  private ISmartCalendarModel currentCalendar;

  /**
   * Constructs a new CalendarManager with no calendars.
   */
  public CalendarManager() {
    this.calendars = new HashMap<>();
    this.currentCalendar = null;
  }

  /**
   * Creates a new calendar with the given name and timezone.
   * 
   * @param calendarName the unique name for the calendar
   * @param timezone the timezone for the calendar
   * @return the created calendar model
   * @throws IllegalArgumentException if calendar name already exists or is null/empty
   */
  @Override
  public ICalendarModel createCalendar(String calendarName, ZoneId timezone) {
    if (calendarName == null || calendarName.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    
    if (calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name '" + calendarName + "' already exists");
    }
    
    if (timezone == null) {
      throw new IllegalArgumentException("Timezone cannot be null");
    }
    
    ISmartCalendarModel newCalendar = new SmartCalendarModel(calendarName, timezone);
    calendars.put(calendarName, newCalendar);
    
    return newCalendar;
  }

  /**
   * Sets the current calendar context to the specified calendar.
   * 
   * @param calendarName the name of the calendar to use
   * @throws IllegalArgumentException if calendar does not exist
   */
  @Override
  public void useCalendar(String calendarName) {
    if (calendarName == null || !calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar '" + calendarName + "' does not exist");
    }
    
    this.currentCalendar = calendars.get(calendarName);
  }

  /**
   * Copies a specific event from the current calendar to the target calendar.
   * 
   * @param eventName the name/subject of the event to copy
   * @param eventDateTime the start date/time of the event to copy
   * @param calendarName the target calendar name
   * @param newEventDateTime the new start date/time in the target calendar's timezone
   * @throws IllegalArgumentException if current calendar is not set, target calendar doesn't exist, or event not found
   */
  @Override
  public void copyEvent(String eventName, LocalDateTime eventDateTime, String calendarName, LocalDateTime newEventDateTime) {
    validateCurrentCalendar();
    
    ISmartCalendarModel targetCalendar = getTargetCalendar(calendarName);
    
    // Find the event in the current calendar
    List<IEvent> eventsAtTime = currentCalendar.printEvents(eventDateTime);
    IEvent eventToCopy = null;
    
    for (IEvent event : eventsAtTime) {
      if (event.getSubject().equals(eventName) && event.getStartDateTime().equals(eventDateTime)) {
        eventToCopy = event;
        break;
      }
    }
    
    if (eventToCopy == null) {
      throw new IllegalArgumentException("Event '" + eventName + "' not found at " + eventDateTime);
    }
    
    // Copy the event to the target calendar
    copyEventToCalendar(eventToCopy, targetCalendar, newEventDateTime, true); // true = already in target timezone
  }

  /**
   * Copies all events on a specific date from the current calendar to the target calendar.
   * 
   * @param dateTime the date to copy events from
   * @param calendarName the target calendar name
   * @param newDateTime the new date in the target calendar's timezone
   * @throws IllegalArgumentException if current calendar is not set or target calendar doesn't exist
   */
  @Override
  public void copyEvents(LocalDateTime dateTime, String calendarName, LocalDateTime newDateTime) {
    validateCurrentCalendar();
    
    ISmartCalendarModel targetCalendar = getTargetCalendar(calendarName);
    
    // Get all events on the specified date
    List<IEvent> eventsToCopy = currentCalendar.printEvents(dateTime);
    
    // Copy each event, converting timezone and placing on the new date
    for (IEvent event : eventsToCopy) {
      LocalDateTime sourceEventStart = event.getStartDateTime();
      // Convert the original time to target timezone, then place on new date
      LocalDateTime convertedTime = convertTimeToTargetTimezone(sourceEventStart, currentCalendar, targetCalendar);
      LocalDateTime newEventStart = newDateTime.toLocalDate().atTime(convertedTime.toLocalTime());
      copyEventToCalendar(event, targetCalendar, newEventStart, false); // false = don't convert again
    }
  }

  /**
   * Copies all events within a date range from the current calendar to the target calendar.
   * 
   * @param startDateTime the start of the date range (inclusive)
   * @param endDateTime the end of the date range (inclusive)
   * @param calendarName the target calendar name
   * @param newDateTime the starting date for the copied events in the target calendar
   * @throws IllegalArgumentException if current calendar is not set or target calendar doesn't exist
   */
  @Override
  public void copyEvents(LocalDateTime startDateTime, LocalDateTime endDateTime, String calendarName, LocalDateTime newDateTime) {
    validateCurrentCalendar();
    
    ISmartCalendarModel targetCalendar = getTargetCalendar(calendarName);
    
    // Get all events in the specified interval
    List<IEvent> eventsToCopy = currentCalendar.printEvents(startDateTime, endDateTime);
    
    // Calculate the offset for copying events
    long dayOffset = java.time.temporal.ChronoUnit.DAYS.between(
        startDateTime.toLocalDate(), 
        newDateTime.toLocalDate()
    );
    
    // Copy each event with the appropriate offset and timezone conversion
    for (IEvent event : eventsToCopy) {
      LocalDateTime originalEventStart = event.getStartDateTime().plusDays(dayOffset);
      // Convert to target timezone
      LocalDateTime convertedEventStart = convertTimeToTargetTimezone(originalEventStart, currentCalendar, targetCalendar);
      copyEventToCalendar(event, targetCalendar, convertedEventStart, false); // false = don't convert again
    }
  }

  /**
   * Gets the current calendar.
   * 
   * @return the current calendar, or null if none is set
   */
  @Override
  public ISmartCalendarModel getCurrentCalendar() {
    return currentCalendar;
  }

  /**
   * Gets a calendar by name.
   * 
   * @param calendarName the name of the calendar
   * @return the calendar, or null if it doesn't exist
   */
  @Override
  public ISmartCalendarModel getCalendar(String calendarName) {
    return calendars.get(calendarName);
  }

  /**
   * Gets all calendar names.
   * 
   * @return a map of all calendars
   */
  @Override
  public Map<String, ISmartCalendarModel> getAllCalendars() {
    return new HashMap<>(calendars);
  }

  /**
   * Validates that a current calendar is set.
   * 
   * @throws IllegalStateException if no current calendar is set
   */
  private void validateCurrentCalendar() {
    if (currentCalendar == null) {
      throw new IllegalStateException("No calendar is currently in use. Use 'use calendar' command first.");
    }
  }

  /**
   * Gets the target calendar by name.
   * 
   * @param calendarName the name of the target calendar
   * @return the target calendar
   * @throws IllegalArgumentException if the calendar doesn't exist
   */
  private ISmartCalendarModel getTargetCalendar(String calendarName) {
    ISmartCalendarModel targetCalendar = calendars.get(calendarName);
    if (targetCalendar == null) {
      throw new IllegalArgumentException("Target calendar '" + calendarName + "' does not exist");
    }
    return targetCalendar;
  }

  /**
   * Copies an event to the target calendar with timezone conversion.
   * 
   * @param event the event to copy
   * @param targetCalendar the target calendar
   * @param newStartDateTime the new start date/time
   * @param alreadyInTargetTimezone true if newStartDateTime is already in target timezone, false if conversion needed
   */
  private void copyEventToCalendar(IEvent event, ISmartCalendarModel targetCalendar, LocalDateTime newStartDateTime, boolean alreadyInTargetTimezone) {
    LocalDateTime adjustedStartTime;
    if (alreadyInTargetTimezone) {
      adjustedStartTime = newStartDateTime;
    } else {
      adjustedStartTime = convertTimeToTargetTimezone(newStartDateTime, currentCalendar, targetCalendar);
    }
    
    if (event.getEndDateTime() != null) {
      // Timed event
      long duration = java.time.temporal.ChronoUnit.MINUTES.between(
          event.getStartDateTime(), 
          event.getEndDateTime()
      );
      LocalDateTime adjustedEndTime = adjustedStartTime.plusMinutes(duration);
      
      targetCalendar.createSingleTimedEvent(event.getSubject(), adjustedStartTime, adjustedEndTime);
    } else {
      // All-day event
      targetCalendar.createSingleAllDayEvent(event.getSubject(), adjustedStartTime);
    }
  }

  /**
   * Converts a time from the current calendar's timezone to the target calendar's timezone.
   * 
   * @param localDateTime the time to convert
   * @param sourceCalendar the source calendar
   * @param targetCalendar the target calendar
   * @return the converted time
   */
  private LocalDateTime convertTimeToTargetTimezone(LocalDateTime localDateTime, 
                                                   ISmartCalendarModel sourceCalendar, 
                                                   ISmartCalendarModel targetCalendar) {
    if (!(sourceCalendar instanceof SmartCalendarModel) || !(targetCalendar instanceof SmartCalendarModel)) {
      return localDateTime; // No conversion if not SmartCalendarModel
    }
    
    SmartCalendarModel sourceCal = (SmartCalendarModel) sourceCalendar;
    SmartCalendarModel targetCal = (SmartCalendarModel) targetCalendar;
    
    // Convert from source timezone to target timezone
    ZonedDateTime sourceZoned = localDateTime.atZone(sourceCal.getTimezone());
    ZonedDateTime targetZoned = sourceZoned.withZoneSameInstant(targetCal.getTimezone());
    
    return targetZoned.toLocalDateTime();
  }
}
