package calendar.model;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

/**
 * Implementation of a smart calendar model that extends the basic calendar functionality
 * with calendar-specific properties like name and timezone.
 * This class follows the same patterns as CalendarModel, maintaining a clean and consistent API.
 * Calendar editing operations are handled by CalendarManager.
 */
public class SmartCalendarModel extends CalendarModel implements ISmartCalendarModel {
  private String calendarName;
  private ZoneId timezone;

  /**
   * Constructs a new SmartCalendarModel with the specified name and timezone.
   * 
   * @param calendarName the name of the calendar (cannot be null or empty)
   * @param timezone the timezone of the calendar (cannot be null)
   * @throws IllegalArgumentException if name is null/empty or timezone is null
   */
  public SmartCalendarModel(String calendarName, ZoneId timezone) {
    super();
    this.calendarName = calendarName;
    this.timezone = timezone;
  }

  /**
   * Gets the name of this calendar.
   * 
   * @return the calendar name
   */
  @Override
  public String getCalendarName() {
    return this.calendarName;
  }

  /**
   * Gets the timezone of this calendar.
   * 
   * @return the calendar timezone
   */
  @Override
  public ZoneId getTimezone() {
    return this.timezone;
  }

  /**
   * Sets the calendar name of this calendar.
   *
   * @param calendarName the new calendar name
   */
  @Override
  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
  }

  /**
   * Sets the timezone of this calendar.
   *
   * @param timezone the new calendar timezone
   */
  @Override
  public void setTimezone(ZoneId timezone) {
    this.timezone = timezone;
  }
}
