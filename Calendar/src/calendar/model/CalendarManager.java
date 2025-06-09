package calendar.model;

import java.time.DateTimeException;
import java.time.ZoneId;
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
  public void createCalendar(String calendarName, ZoneId timezone) {
    if (calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + " already exists");
    }
    ISmartCalendarModel newCalendar = new SmartCalendarModel(calendarName, timezone);
    this.calendars.put(calendarName, newCalendar);
  }

  @Override
  public void useCalendar(String calendarName) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + " does not exist");
    }
    this.currentCalendar = this.calendars.get(calendarName);
  }

  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    if (!calendars.containsKey(calendarName)) {
      throw new IllegalArgumentException("Calendar with name " + calendarName + " does not exist");
    }
    ISmartCalendarModel calendarToEdit = this.calendars.get(calendarName);
    switch (property) {
      case "name":
        if (calendars.containsKey(newValue)) {
          throw new IllegalArgumentException("Calendar with name " + calendarName + " already exists");
        }
        String oldCalendarName = calendarToEdit.getCalendarName();
        calendarToEdit.setCalendarName(newValue);
        calendars.remove(oldCalendarName);
        calendars.put(newValue, calendarToEdit);
        break;
      case "timezone":
        try {
          ZoneId timezone = ZoneId.of(newValue);
          calendarToEdit.setTimezone(timezone);
        } catch (DateTimeException e) {
          throw new IllegalArgumentException("Invalid timezone: " + newValue + ". IANA timezone format is expected.");
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown property " + property);
    }
  }
}
