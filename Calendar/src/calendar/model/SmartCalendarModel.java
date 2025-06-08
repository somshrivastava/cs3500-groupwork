package calendar.model;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

/**
 * An enhanced calendar model that supports named calendars with timezones.
 * Extends the base CalendarModel functionality with calendar-specific properties
 * like name and timezone that can be edited.
 */
public class SmartCalendarModel extends CalendarModel implements ISmartCalendarModel {
  private String calendarName;
  private ZoneId timezone;

  /**
   * Constructs a {@code SmartCalendarModel} with the given name and timezone.
   * 
   * @param calendarName the name of the calendar
   * @param timezone the timezone of the calendar
   * @throws IllegalArgumentException if name is null/empty or timezone is null
   */
  public SmartCalendarModel(String calendarName, ZoneId timezone) {
    super();
    if (calendarName == null || calendarName.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    if (timezone == null) {
      throw new IllegalArgumentException("Timezone cannot be null");
    }
    
    this.calendarName = calendarName;
    this.timezone = timezone;
  }

  /**
   * Edits a property of the calendar (name or timezone).
   * 
   * @param calendarName the name of the calendar to edit (must match current name)
   * @param property the property to edit ("name" or "timezone")
   * @param newValue the new value for the property
   * @throws IllegalArgumentException if calendar name doesn't match, property is invalid, or new value is invalid
   */
  @Override
  public void editCalendar(String calendarName, String property, String newValue) {
    // Validate that we're editing the correct calendar
    if (!this.calendarName.equals(calendarName)) {
      throw new IllegalArgumentException("Calendar name '" + calendarName + "' does not match this calendar's name '" + this.calendarName + "'");
    }
    
    if (property == null) {
      throw new IllegalArgumentException("Property cannot be null");
    }
    
    if (newValue == null) {
      throw new IllegalArgumentException("New value cannot be null");
    }
    
    switch (property.toLowerCase()) {
      case "name":
        editCalendarName(newValue);
        break;
      case "timezone":
        editCalendarTimezone(newValue);
        break;
      default:
        throw new IllegalArgumentException("Invalid property '" + property + "'. Valid properties are: name, timezone");
    }
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
   * Edits the calendar name.
   * 
   * @param newName the new name for the calendar
   * @throws IllegalArgumentException if the new name is null or empty
   */
  private void editCalendarName(String newName) {
    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    
    this.calendarName = newName.trim();
  }

  /**
   * Edits the calendar timezone.
   * 
   * @param newTimezone the new timezone string in IANA format (e.g., "America/New_York")
   * @throws IllegalArgumentException if the timezone format is invalid
   */
  private void editCalendarTimezone(String newTimezone) {
    if (newTimezone == null || newTimezone.trim().isEmpty()) {
      throw new IllegalArgumentException("Timezone cannot be null or empty");
    }
    
    try {
      ZoneId newZoneId = ZoneId.of(newTimezone.trim());
      this.timezone = newZoneId;
    } catch (ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid timezone format: " + newTimezone + ". Expected IANA format like 'America/New_York'");
    }
  }

  /**
   * Gets a builder for creating SmartCalendarModel instances.
   * 
   * @return a new SmartCalendarModelBuilder
   */
  public static SmartCalendarModelBuilder getBuilder() {
    return new SmartCalendarModelBuilder();
  }

  /**
   * Builder class for constructing SmartCalendarModel instances.
   */
  public static class SmartCalendarModelBuilder {
    protected String calendarName;
    protected ZoneId timezone;

    /**
     * Sets the calendar name.
     * 
     * @param calendarName the name of the calendar
     * @return this builder instance
     */
    public SmartCalendarModelBuilder calendarName(String calendarName) {
      this.calendarName = calendarName;
      return this;
    }

    /**
     * Sets the calendar timezone.
     * 
     * @param timezone the timezone of the calendar
     * @return this builder instance
     */
    public SmartCalendarModelBuilder timezone(ZoneId timezone) {
      this.timezone = timezone;
      return this;
    }

    /**
     * Builds a SmartCalendarModel instance.
     * 
     * @return a new SmartCalendarModel
     * @throws IllegalArgumentException if required fields are missing
     */
    public SmartCalendarModel build() {
      if (calendarName == null) {
        throw new IllegalArgumentException("Calendar name is required");
      }
      if (timezone == null) {
        throw new IllegalArgumentException("Timezone is required");
      }
      
      return new SmartCalendarModel(calendarName, timezone);
    }
  }
}
