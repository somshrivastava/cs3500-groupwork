package calendar.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.List;

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

  /**
   * Finds and returns a specific event by subject and start date/time.
   * @param subject the subject of the event to find
   * @param startDateTime the start date/time of the event to find
   * @return the matching event
   * @throws IllegalArgumentException if no event is found with the given criteria
   */
  @Override
  public IEvent findEventBySubjectAndTime(String subject, LocalDateTime startDateTime) {
    // Get all events and search through them
    List<IEvent> allEvents = printEvents(startDateTime.minusYears(100), startDateTime.plusYears(100));
    for (IEvent event : allEvents) {
      if (event.getSubject().equals(subject) && 
          event.getStartDateTime().equals(startDateTime)) {
        return event;
      }
    }
    throw new IllegalArgumentException("Event not found with subject '" + subject + 
        "' and start time '" + startDateTime + "'");
  }

  /**
   * Creates a copy of an existing event with new timing and timezone conversion.
   * @param eventName the name/subject of the event to copy
   * @param sourceDateTime the start date/time of the source event
   * @param targetTimezone the timezone of the target calendar
   * @param targetDateTime the desired start time in the target calendar's timezone
   * @return a new event ready to be added to the target calendar
   * @throws IllegalArgumentException if the source event is not found
   */
  @Override
  public IEvent createCopiedEvent(String eventName, LocalDateTime sourceDateTime, ZoneId targetTimezone, LocalDateTime targetDateTime) {
    // Find the source event
    IEvent sourceEvent = findEventBySubjectAndTime(eventName, sourceDateTime);
    
    // Calculate the duration of the original event
    Duration eventDuration = Duration.between(sourceEvent.getStartDateTime(), sourceEvent.getEndDateTime());
    
    // Calculate the end time for the copied event
    LocalDateTime targetEndDateTime = targetDateTime.plus(eventDuration);
    
    // Create and return the new event (copying all properties except timing and series)
    return Event.getBuilder()
        .subject(sourceEvent.getSubject())
        .description(sourceEvent.getDescription())
        .location(sourceEvent.getLocation())
        .status(sourceEvent.getStatus())
        .startDateTime(targetDateTime)
        .endDateTime(targetEndDateTime)
        .seriesId(null) // Copied events are not part of the original series
        .build();
  }
}
