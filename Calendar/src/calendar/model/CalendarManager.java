package calendar.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * Manages a collection of smart calendars.
 * This class handles the creation, editing, and management of multiple calendars.
 * Provides functionality for calendar switching, event copying with timezone conversion,
 * and calendar management.
 */
public class CalendarManager implements ICalendarManager {
  private final Map<String, ISmartCalendarModel> calendars;
  private ISmartCalendarModel currentCalendar;

  /**
   * Constructs a new CalendarManager with an empty set of calendars.
   */
  public CalendarManager() {
    this.calendars = new HashMap<String, ISmartCalendarModel>();
    this.currentCalendar = null;
  }

  @Override
  public ISmartCalendarModel getCurrentCalendar() {
    return this.currentCalendar;
  }

  @Override
  public void createCalendar(String calendarName, ZoneId timezone) {
    validateCalendarNameAvailable(calendarName);
    ISmartCalendarModel newCalendar = new SmartCalendarModel(calendarName, timezone);
    this.calendars.put(calendarName, newCalendar);
  }

  @Override
  public void useCalendar(String calendarName) {
    validateCalendarExists(calendarName);
    this.currentCalendar = this.calendars.get(calendarName);
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    validateCalendarExists(calendarName);
    ISmartCalendarModel calendarToEdit = this.calendars.get(calendarName);
    switch (property) {
      case "name":
        validateCalendarNameAvailable(newValue);
        String oldCalendarName = calendarToEdit.getCalendarName();
        calendarToEdit.setCalendarName(newValue);
        calendars.remove(oldCalendarName);
        calendars.put(newValue, calendarToEdit);
        break;
      case "timezone":
        try {
          ZoneId newTimezone = ZoneId.of(newValue);
          calendarToEdit.setTimezone(newTimezone);
        } catch (DateTimeException e) {
          throw new IllegalArgumentException("Invalid timezone: " + newValue + 
              ". IANA timezone format is expected.");
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown property " + property);
    }
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime sourceDateTime, 
                       String targetCalendarName, LocalDateTime targetDateTime) {
    validateCurrentCalendarSet();
    ISmartCalendarModel targetCalendar = getValidatedTargetCalendar(targetCalendarName);

    // Let the current calendar create the copied event
    IEvent copiedEvent = currentCalendar.createCopiedEvent(eventName, sourceDateTime, 
        targetDateTime);

    // Add the copied event to the target calendar
    targetCalendar.createSingleTimedEvent(copiedEvent.getSubject(), 
        copiedEvent.getStartDateTime(), copiedEvent.getEndDateTime());
  }

  @Override
  public void copyEventsOnDate(LocalDateTime sourceDate, String targetCalendarName, 
                               LocalDateTime targetDate) {
    validateCurrentCalendarSet();
    ISmartCalendarModel targetCalendar = getValidatedTargetCalendar(targetCalendarName);

    // Copy all events from the source date to the target date with timezone conversion
    currentCalendar.copyAllEventsToCalendar(sourceDate, targetCalendar, targetDate);
  }

  @Override
  public void copyEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, 
                                    String targetCalendarName, LocalDateTime targetStartDate) {
    validateCurrentCalendarSet();
    ISmartCalendarModel targetCalendar = getValidatedTargetCalendar(targetCalendarName);

    // Copy all events in the date range to the target calendar with timezone conversion
    currentCalendar.copyEventsInRangeToCalendar(startDate, endDate, targetCalendar, 
        targetStartDate);
  }

  /**
   * Validates that a calendar with the given name exists.
   * 
   * @param calendarName the name of the calendar to check
   * @throws IllegalArgumentException if the calendar does not exist
   */
  private void validateCalendarExists(String calendarName) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + 
          " does not exist");
    }
  }

  /**
   * Validates that a current calendar is set.
   * 
   * @throws IllegalArgumentException if no current calendar is set
   */
  private void validateCurrentCalendarSet() {
    if (currentCalendar == null) {
      throw new IllegalArgumentException("No calendar is currently in use. " + 
          "Use 'use calendar' command first.");
    }
  }

  /**
   * Validates that the target calendar exists and returns it.
   * 
   * @param targetCalendarName the name of the target calendar
   * @return the target calendar
   * @throws IllegalArgumentException if the target calendar does not exist
   */
  private ISmartCalendarModel getValidatedTargetCalendar(String targetCalendarName) {
    if (!calendars.containsKey(targetCalendarName)) {
      throw new IllegalArgumentException("Target calendar '" + targetCalendarName + 
          "' does not exist");
    }
    return calendars.get(targetCalendarName);
  }

  /**
   * Validates that a calendar name is available (not already in use).
   * 
   * @param calendarName the name to check for availability
   * @throws IllegalArgumentException if the calendar name already exists
   */
  private void validateCalendarNameAvailable(String calendarName) {
    if (calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + 
          " already exists");
    }
  }

  @Override
  public List<String> getCalendarNames() {
    return new ArrayList<>(calendars.keySet());
  }
}
