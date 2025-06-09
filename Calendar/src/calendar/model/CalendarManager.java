package calendar.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

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
    if (calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + 
          " already exists");
    }
    ISmartCalendarModel newCalendar = new SmartCalendarModel(calendarName, timezone);
    this.calendars.put(calendarName, newCalendar);
  }

  @Override
  public void useCalendar(String calendarName) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + 
          " does not exist");
    }
    this.currentCalendar = this.calendars.get(calendarName);
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + 
          " does not exist");
    }
    ISmartCalendarModel calendarToEdit = this.calendars.get(calendarName);
    switch (property) {
      case "name":
        if (calendars.containsKey(newValue)) {
          throw new IllegalArgumentException("Calendar with name " + calendarName + 
              " already exists");
        }
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
    // Check that a current calendar is set
    if (currentCalendar == null) {
      throw new IllegalArgumentException("No calendar is currently in use. " + 
          "Use 'use calendar' command first.");
    }

    // Check that the target calendar exists
    if (!calendars.containsKey(targetCalendarName)) {
      throw new IllegalArgumentException("Target calendar '" + targetCalendarName + 
          "' does not exist");
    }

    ISmartCalendarModel targetCalendar = calendars.get(targetCalendarName);

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
    // Check that a current calendar is set
    if (currentCalendar == null) {
      throw new IllegalArgumentException("No calendar is currently in use. " + 
          "Use 'use calendar' command first.");
    }

    // Check that the target calendar exists
    if (!calendars.containsKey(targetCalendarName)) {
      throw new IllegalArgumentException("Target calendar '" + targetCalendarName + 
          "' does not exist");
    }

    ISmartCalendarModel targetCalendar = calendars.get(targetCalendarName);

    // Copy all events from the source date to the target date with timezone conversion
    currentCalendar.copyAllEventsToCalendar(sourceDate, targetCalendar, targetDate);
  }

  @Override
  public void copyEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, 
                                    String targetCalendarName, LocalDateTime targetStartDate) {
    // Check that a current calendar is set
    if (currentCalendar == null) {
      throw new IllegalArgumentException("No calendar is currently in use. " + 
          "Use 'use calendar' command first.");
    }

    // Check that the target calendar exists
    if (!calendars.containsKey(targetCalendarName)) {
      throw new IllegalArgumentException("Target calendar '" + targetCalendarName + 
          "' does not exist");
    }

    ISmartCalendarModel targetCalendar = calendars.get(targetCalendarName);

    // Copy all events in the date range to the target calendar with timezone conversion
    currentCalendar.copyEventsInRangeToCalendar(startDate, endDate, targetCalendar, 
        targetStartDate);
  }
}
