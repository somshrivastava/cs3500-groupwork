package calendar.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
   * @param timezone     the timezone of the calendar (cannot be null)
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
   * When the timezone changes, all existing events are converted to the new timezone.
   *
   * @param timezone the new calendar timezone
   */
  @Override
  public void setTimezone(ZoneId timezone) {
    ZoneId oldTimezone = this.timezone;
    this.convertAllEventsToNewTimezone(oldTimezone, timezone);
    this.timezone = timezone;
  }

  /**
   * Finds and returns a specific event by subject and start date/time.
   *
   * @param subject       the subject of the event to find
   * @param startDateTime the start date/time of the event to find
   * @return the matching event
   * @throws IllegalArgumentException if no event is found with the given criteria
   */
  @Override
  public IEvent findEventBySubjectAndTime(String subject, LocalDateTime startDateTime) {
    List<IEvent> allEvents = printEvents(startDateTime.minusYears(100),
            startDateTime.plusYears(100));
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
   * Creates a copy of an existing event with new timing.
   *
   * @param eventName      the name/subject of the event to copy
   * @param sourceDateTime the start date/time of the source event
   * @param targetDateTime the desired start time for the copied event
   * @return a new event ready to be added to the target calendar
   * @throws IllegalArgumentException if the source event is not found
   */
  @Override
  public IEvent createCopiedEvent(String eventName, LocalDateTime sourceDateTime,
                                  LocalDateTime targetDateTime) {
    // Find the source event
    IEvent sourceEvent = findEventBySubjectAndTime(eventName, sourceDateTime);

    // Calculate the duration of the original event directly (duration is timezone-independent)
    Duration eventDuration = Duration.between(sourceEvent.getStartDateTime(),
            sourceEvent.getEndDateTime());

    // Use the specified target time and preserve the duration
    LocalDateTime targetEndTime = targetDateTime.plus(eventDuration);

    // Create and return the new event (copying all properties except timing and series)
    return buildEventFromTemplate(sourceEvent, targetDateTime, targetEndTime, null);
  }

  /**
   * Copies all events from a source date to a target calendar on a target date with timezone
   * conversion.
   * Events are converted from this calendar's timezone to the target calendar's timezone.
   * If an event is part of a series, its series status is retained in the destination calendar.
   *
   * @param sourceDate     the date to copy events from
   * @param targetCalendar the calendar to copy events to
   * @param targetDate     the date to place the copied events on
   */
  @Override
  public void copyAllEventsToCalendar(LocalDateTime sourceDate,
                                      ISmartCalendarModel targetCalendar,
                                      LocalDateTime targetDate) {
    // Get all events on the source date
    List<IEvent> eventsOnDate = printEvents(sourceDate);

    // Build series ID mapping for events being copied
    Map<Integer, Integer> seriesIdMapping = buildSeriesIdMapping(eventsOnDate, targetCalendar);

    // Copy each event with mapped series IDs and timezone conversion
    for (IEvent event : eventsOnDate) {
      LocalDateTime[] convertedTimes = convertEventTimesToTargetTimezone(event, targetCalendar);
      LocalDateTime convertedStartTime = convertedTimes[0];
      LocalDateTime convertedEndTime = convertedTimes[1];

      // Adjust the converted times to the target date while preserving time
      LocalDateTime finalStartTime = adjustTimeToTargetDate(targetDate, convertedStartTime);
      LocalDateTime finalEndTime = adjustTimeToTargetDate(targetDate, convertedEndTime);

      // Create and add the copied event
      Integer mappedSeriesId = mapSeriesId(event.getSeriesId(), seriesIdMapping);
      IEvent copiedEvent = buildEventFromTemplate(event, finalStartTime, finalEndTime,
              mappedSeriesId);
      targetCalendar.addEvent(copiedEvent);
    }
  }

  /**
   * Copies all events in a date range from this calendar to a target calendar with timezone
   * conversion.
   * Events are converted from this calendar's timezone to the target calendar's timezone.
   * If an event series partly overlaps with the range, only overlapping events are copied
   * but retain series status.
   *
   * @param startDate       the start date of the range (inclusive)
   * @param endDate         the end date of the range (inclusive)
   * @param targetCalendar  the calendar to copy events to
   * @param targetStartDate the start date to place the copied events on
   */
  @Override
  public void copyEventsInRangeToCalendar(LocalDateTime startDate, LocalDateTime endDate,
                                          ISmartCalendarModel targetCalendar,
                                          LocalDateTime targetStartDate) {
    // Get all events in the date range
    List<IEvent> eventsInRange = printEvents(startDate, endDate);

    // Calculate the time offset from source start date to target start date
    long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(),
            targetStartDate.toLocalDate());

    // Build series ID mapping for events being copied
    Map<Integer, Integer> seriesIdMapping = buildSeriesIdMapping(eventsInRange, targetCalendar);

    // Copy each event with mapped series IDs and timezone conversion
    for (IEvent event : eventsInRange) {
      LocalDateTime[] convertedTimes = convertEventTimesToTargetTimezone(event, targetCalendar);
      LocalDateTime convertedStartTime = convertedTimes[0];
      LocalDateTime convertedEndTime = convertedTimes[1];

      // Adjust the converted times by the day offset
      LocalDateTime finalStartTime = convertedStartTime.plusDays(daysBetween);
      LocalDateTime finalEndTime = convertedEndTime.plusDays(daysBetween);

      // Create and add the copied event
      Integer mappedSeriesId = mapSeriesId(event.getSeriesId(), seriesIdMapping);
      IEvent copiedEvent = buildEventFromTemplate(event, finalStartTime, finalEndTime,
              mappedSeriesId);
      targetCalendar.addEvent(copiedEvent);
    }
  }

  /**
   * Adds a pre-built event to this calendar.
   * This allows adding events with specific properties like series ID.
   *
   * @param event the event to add to the calendar
   */
  @Override
  public void addEvent(IEvent event) {
    events.add(event);
  }

  /**
   * Converts all events in this calendar from the old timezone to a new timezone.
   * This is called when the calendar's timezone is changed.
   *
   * @param oldTimezone the original timezone
   * @param newTimezone the new timezone to convert to
   */
  public void convertAllEventsToNewTimezone(ZoneId oldTimezone, ZoneId newTimezone) {
    if (oldTimezone.equals(newTimezone)) {
      return; // No conversion needed
    }

    // Create a copy of events to avoid concurrent modification
    Set<IEvent> eventsCopy = new HashSet<IEvent>(events);
    events.clear(); // Clear all existing events

    // Convert each event and add it back
    for (IEvent event : eventsCopy) {
      LocalDateTime convertedStart = convertTimeBetweenTimezones(event.getStartDateTime(),
              oldTimezone, newTimezone);
      LocalDateTime convertedEnd = convertTimeBetweenTimezones(event.getEndDateTime(),
              oldTimezone, newTimezone);

      // Create new event with converted times
      IEvent convertedEvent = buildEventFromTemplate(event, convertedStart, convertedEnd,
              event.getSeriesId());
      events.add(convertedEvent);
    }
  }

  /**
   * Generates a unique series ID using the inherited nextSeriesId counter.
   *
   * @return a new unique series ID
   */
  @Override
  public Integer generateUniqueSeriesId() {
    return nextSeriesId++;
  }

  /**
   * Builds a series ID mapping for a list of events to avoid conflicts in the target calendar.
   *
   * @param events         the events to build mapping for
   * @param targetCalendar the target calendar that will receive new series IDs
   * @return a mapping from original series IDs to new series IDs
   */
  private Map<Integer, Integer> buildSeriesIdMapping(List<IEvent> events,
                                                     ISmartCalendarModel targetCalendar) {
    Map<Integer, Integer> seriesIdMapping = new HashMap<>();

    for (IEvent event : events) {
      if (event.getSeriesId() != null &&
              !seriesIdMapping.containsKey(event.getSeriesId())) {
        // Generate a new unique series ID for the target calendar
        Integer newSeriesId = targetCalendar.generateUniqueSeriesId();
        seriesIdMapping.put(event.getSeriesId(), newSeriesId);
      }
    }

    return seriesIdMapping;
  }

  /**
   * Converts event times from this calendar's timezone to target calendar's timezone.
   *
   * @param event          the event whose times need conversion
   * @param targetCalendar the target calendar with the destination timezone
   * @return array with [convertedStartTime, convertedEndTime]
   */
  private LocalDateTime[] convertEventTimesToTargetTimezone(IEvent event,
                                                            ISmartCalendarModel targetCalendar) {
    LocalDateTime convertedStartTime = convertTimeBetweenTimezones(
            event.getStartDateTime(), this.timezone, targetCalendar.getTimezone());
    LocalDateTime convertedEndTime = convertTimeBetweenTimezones(
            event.getEndDateTime(), this.timezone, targetCalendar.getTimezone());

    return new LocalDateTime[]{convertedStartTime, convertedEndTime};
  }

  /**
   * Adjusts a converted time to a target date while preserving the time components.
   *
   * @param targetDate    the target date
   * @param convertedTime the converted time to adjust
   * @return the time adjusted to the target date
   */
  private LocalDateTime adjustTimeToTargetDate(LocalDateTime targetDate, LocalDateTime
          convertedTime) {
    return targetDate.withHour(convertedTime.getHour())
            .withMinute(convertedTime.getMinute())
            .withSecond(convertedTime.getSecond())
            .withNano(convertedTime.getNano());
  }

  /**
   * Maps a series ID using the provided mapping, returning null if the original ID is null.
   *
   * @param originalSeriesId the original series ID
   * @param seriesIdMapping  the mapping to use
   * @return the mapped series ID or null
   */
  private Integer mapSeriesId(Integer originalSeriesId, Map<Integer, Integer> seriesIdMapping) {
    return originalSeriesId != null ? seriesIdMapping.get(originalSeriesId) : null;
  }

  /**
   * Builds an event from a template event with new timing and series ID.
   *
   * @param templateEvent the template event to copy properties from
   * @param startDateTime the new start time
   * @param endDateTime   the new end time
   * @param seriesId      the new series ID (or null)
   * @return a new event with the specified properties
   */
  private IEvent buildEventFromTemplate(IEvent templateEvent, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, Integer seriesId) {
    return Event.getBuilder()
            .subject(templateEvent.getSubject())
            .description(templateEvent.getDescription())
            .location(templateEvent.getLocation())
            .status(templateEvent.getStatus())
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .seriesId(seriesId)
            .build();
  }

  /**
   * Converts a LocalDateTime from one timezone to another.
   *
   * @param dateTime     the time to convert
   * @param fromTimezone the source timezone
   * @param toTimezone   the target timezone
   * @return the converted time
   */
  private LocalDateTime convertTimeBetweenTimezones(LocalDateTime dateTime,
                                                    ZoneId fromTimezone, ZoneId toTimezone) {
    ZonedDateTime fromZoned = dateTime.atZone(fromTimezone);
    ZonedDateTime toZoned = fromZoned.withZoneSameInstant(toTimezone);
    return toZoned.toLocalDateTime();
  }
}
