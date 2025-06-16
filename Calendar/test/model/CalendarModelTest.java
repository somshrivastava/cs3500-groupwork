package model;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import calendar.model.CalendarModel;
import calendar.model.EventLocation;
import calendar.model.EventStatus;
import calendar.model.ICalendarModel;
import calendar.model.IEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Represents tests and examples for CalendarModel.
 * Tests all public methods, edge cases, error conditions, and integration scenarios.
 */
public class CalendarModelTest {
  private ICalendarModel model;
  private LocalDateTime baseDateTime;
  private LocalDateTime endDateTime;
  private LocalDateTime nextDay;
  private ArrayList<DayOfWeek> mondayWednesdayFriday;
  private ArrayList<DayOfWeek> weekends;

  // Constants matching the implementation
  private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);  // 8 AM
  private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);   // 5 PM

  @Before
  public void setUp() {
    model = new CalendarModel();
    baseDateTime = LocalDateTime.of(2024, 3, 18, 10, 0);
    // Monday, March 18, 2024, 10:00 AM
    endDateTime = LocalDateTime.of(2024, 3, 18, 11, 0);
    // Monday, March 18, 2024, 11:00 AM
    nextDay = LocalDateTime.of(2024, 3, 19, 10, 0);
    // Tuesday

    mondayWednesdayFriday = new ArrayList<>(Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
    weekends = new ArrayList<>(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
  }

  @Test
  public void testConstructor() {
    ICalendarModel newModel = new CalendarModel();
    List<IEvent> events = newModel.printEvents(baseDateTime);
    assertEquals("New model should have no events", 0, events.size());
    assertFalse("New model should show available status",
            newModel.showStatus(baseDateTime));
  }

  @Test
  public void testCreateSingleTimedEvent() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    List<IEvent> events = model.printEvents(baseDateTime);

    assertEquals(1, events.size());
    IEvent event = events.get(0);
    assertEquals("Meeting", event.getSubject());
    assertEquals(baseDateTime, event.getStartDateTime());
    assertEquals(endDateTime, event.getEndDateTime());
    assertNull("Single event should not have series ID", event.getSeriesId());
  }

  @Test
  public void testCreateMultipleSingleTimedEvents() {
    model.createSingleTimedEvent("Meeting 1", baseDateTime, endDateTime);
    model.createSingleTimedEvent("Meeting 2", nextDay, nextDay.plusHours(1));

    List<IEvent> allEvents = model.printEvents(baseDateTime, nextDay.plusDays(1));
    assertEquals(2, allEvents.size());

    // Events should be sorted by start time
    assertEquals("Meeting 1", allEvents.get(0).getSubject());
    assertEquals("Meeting 2", allEvents.get(1).getSubject());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleTimedEventNullSubject() {
    model.createSingleTimedEvent(null, baseDateTime, endDateTime);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleTimedEventEmptySubject() {
    model.createSingleTimedEvent("", baseDateTime, endDateTime);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleTimedEventNullStartTime() {
    model.createSingleTimedEvent("Meeting", null, endDateTime);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleTimedEventNullEndTime() {
    model.createSingleTimedEvent("Meeting", baseDateTime, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleTimedEventEndBeforeStart() {
    model.createSingleTimedEvent("Meeting", endDateTime, baseDateTime);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleTimedEventSameStartEnd() {
    model.createSingleTimedEvent("Meeting", baseDateTime, baseDateTime);
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    LocalDateTime date = LocalDateTime.of(2024, 3, 20, 12, 0);
    model.createSingleAllDayEvent("Holiday", date);

    List<IEvent> events = model.printEvents(date);
    assertEquals(1, events.size());

    IEvent event = events.get(0);
    assertEquals("Holiday", event.getSubject());
    assertEquals(date.toLocalDate().atTime(ALL_DAY_START), event.getStartDateTime());
    assertEquals(date.toLocalDate().atTime(ALL_DAY_END), event.getEndDateTime());
    assertNull("Single all-day event should not have series ID", event.getSeriesId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleAllDayEventNullSubject() {
    model.createSingleAllDayEvent(null, baseDateTime);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSingleAllDayEventNullDate() {
    model.createSingleAllDayEvent("Holiday", null);
  }

  @Test
  public void testCreateRecurringTimedEventWithCount() {
    model.createRecurringTimedEvent("Weekly Meeting", baseDateTime, endDateTime,
            mondayWednesdayFriday, 5);

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(3));
    assertEquals(5, events.size());

    Integer seriesId = events.get(0).getSeriesId();
    assertNotNull("Recurring events should have series ID", seriesId);

    for (IEvent event : events) {
      assertEquals("Weekly Meeting", event.getSubject());
      assertEquals(seriesId, event.getSeriesId());
      assertTrue("Event should be on specified weekday",
              mondayWednesdayFriday.contains(event.getStartDateTime().getDayOfWeek()));
    }
  }

  @Test
  public void testCreateRecurringTimedEventSkipsNonMatchingDays() {
    // Create event that only occurs on weekends, starting on Monday
    model.createRecurringTimedEvent("Weekend Meeting", baseDateTime, endDateTime,
            weekends, 2);

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));
    assertEquals(2, events.size());

    for (IEvent event : events) {
      assertTrue("Event should only occur on weekends",
              weekends.contains(event.getStartDateTime().getDayOfWeek()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringTimedEventMultipleDays() {
    LocalDateTime multiDayEnd = baseDateTime.plusDays(1);
    model.createRecurringTimedEvent("Invalid", baseDateTime, multiDayEnd,
            mondayWednesdayFriday, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringTimedEventZeroCount() {
    model.createRecurringTimedEvent("Meeting", baseDateTime, endDateTime,
            mondayWednesdayFriday, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringTimedEventNegativeCount() {
    model.createRecurringTimedEvent("Meeting", baseDateTime, endDateTime,
            mondayWednesdayFriday, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringTimedEventNullWeekdays() {
    model.createRecurringTimedEvent("Meeting", baseDateTime, endDateTime,
            null, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringTimedEventEmptyWeekdays() {
    model.createRecurringTimedEvent("Meeting", baseDateTime, endDateTime,
            new ArrayList<>(), 3);
  }

  @Test
  public void testCreateRecurringTimedEventUntilDate() {
    LocalDateTime untilDate = baseDateTime.plusWeeks(2);
    model.createRecurringTimedEventUntil("Weekly Meeting", baseDateTime, endDateTime,
            mondayWednesdayFriday, untilDate);

    List<IEvent> events = model.printEvents(baseDateTime, untilDate.plusDays(1));
    assertTrue("Should have created events", events.size() > 0);

    for (IEvent event : events) {
      assertEquals("Weekly Meeting", event.getSubject());
      assertNotNull("Recurring events should have series ID", event.getSeriesId());
      assertFalse("Event should not be after until date",
              event.getStartDateTime().toLocalDate().isAfter(untilDate.toLocalDate()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringTimedEventUntilBeforeStart() {
    LocalDateTime pastDate = baseDateTime.minusDays(1);
    model.createRecurringTimedEventUntil("Meeting", baseDateTime, endDateTime,
            mondayWednesdayFriday, pastDate);
  }

  @Test
  public void testCreateRecurringAllDayEvent() {
    model.createRecurringAllDayEvent("Daily Standup", baseDateTime,
            mondayWednesdayFriday, 3);

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));
    assertEquals(3, events.size());

    for (IEvent event : events) {
      assertEquals("Daily Standup", event.getSubject());
      assertNotNull("Recurring events should have series ID", event.getSeriesId());
      assertEquals(event.getStartDateTime().toLocalDate().atTime(ALL_DAY_START),
              event.getStartDateTime());
      assertEquals(event.getStartDateTime().toLocalDate().atTime(ALL_DAY_END),
              event.getEndDateTime());
    }
  }

  @Test
  public void testCreateRecurringAllDayEventUntilDate() {
    LocalDateTime untilDate = baseDateTime.plusWeeks(2);
    model.createRecurringAllDayEventUntil("Holiday", baseDateTime,
            mondayWednesdayFriday, untilDate);

    List<IEvent> events = model.printEvents(baseDateTime, untilDate.plusDays(1));
    assertTrue("Should have created events", events.size() > 0);

    for (IEvent event : events) {
      assertEquals("Holiday", event.getSubject());
      assertFalse("Event should not be after until date",
              event.getStartDateTime().toLocalDate().isAfter(untilDate.toLocalDate()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDuplicateSingleEventPrevention() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
  }

  @Test
  public void testDifferentSubjectSameTimeAllowed() {
    model.createSingleTimedEvent("Meeting 1", baseDateTime, endDateTime);
    model.createSingleTimedEvent("Meeting 2", baseDateTime, endDateTime);

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(2, events.size());
  }

  @Test
  public void testSameSubjectDifferentTimeAllowed() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.createSingleTimedEvent("Meeting", nextDay, nextDay.plusHours(1));

    List<IEvent> events = model.printEvents(baseDateTime, nextDay.plusDays(1));
    assertEquals(2, events.size());
  }

  @Test
  public void testEditEventSubject() {
    model.createSingleTimedEvent("Original", baseDateTime, endDateTime);
    model.editEvent("Original", baseDateTime, endDateTime, "subject",
            "Updated");

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals("Updated", events.get(0).getSubject());
  }

  @Test
  public void testEditEventStartTime() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    LocalDateTime newStart = baseDateTime.plusMinutes(30); // 10:30 AM, still before 11:00 AM end
    model.editEvent("Meeting", baseDateTime, endDateTime, "start",
            newStart.toString());

    List<IEvent> events = model.printEvents(baseDateTime.plusMinutes(30));
    assertEquals(1, events.size());
    assertEquals(newStart, events.get(0).getStartDateTime());
    // End time should remain unchanged
    assertEquals(endDateTime, events.get(0).getEndDateTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventStartTimeAfterEndTime() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    LocalDateTime newStart = endDateTime.plusHours(1); // Start after end
    model.editEvent("Meeting", baseDateTime, endDateTime, "start",
            newStart.toString());
  }

  @Test
  public void testEditEventEndTime() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    LocalDateTime newEnd = endDateTime.plusHours(1);
    model.editEvent("Meeting", baseDateTime, endDateTime, "end", newEnd.toString());

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals(newEnd, events.get(0).getEndDateTime());
  }

  @Test
  public void testEditEventDescription() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "description",
            "Important meeting");

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals("Important meeting", events.get(0).getDescription());
  }

  @Test
  public void testEditEventLocation() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "location",
            "ONLINE");

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals(EventLocation.ONLINE, events.get(0).getLocation());
  }

  @Test
  public void testEditEventStatus() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "status",
            "PRIVATE");

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals(EventStatus.PRIVATE, events.get(0).getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditNonExistentEvent() {
    model.editEvent("NonExistent", baseDateTime, endDateTime, "subject",
            "New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidProperty() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "invalid",
            "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidEndTime() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "end",
            baseDateTime.minusHours(1).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidLocation() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "location",
            "INVALID");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidStatus() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "status",
            "INVALID");
  }

  @Test
  public void testEditEventsFromDate() {
    model.createRecurringTimedEvent("Weekly", baseDateTime, endDateTime,
            mondayWednesdayFriday, 6);

    LocalDateTime editFromDate = baseDateTime.plusWeeks(1);
    model.editEvents("Weekly", editFromDate, "subject", "Updated Weekly");

    List<IEvent> allEvents = model.printEvents(baseDateTime, baseDateTime.plusWeeks(3));

    for (IEvent event : allEvents) {
      if (!event.getStartDateTime().toLocalDate().isBefore(editFromDate.toLocalDate())) {
        assertEquals("Updated Weekly", event.getSubject());
      } else {
        assertEquals("Weekly", event.getSubject());
      }
    }
  }

  @Test
  public void testEditEntireSeries() {
    model.createRecurringTimedEvent("Weekly", baseDateTime, endDateTime,
            mondayWednesdayFriday, 5);
    model.editSeries("Weekly", baseDateTime, "subject", "Updated Weekly");

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(3));
    for (IEvent event : events) {
      assertEquals("Updated Weekly", event.getSubject());
    }
  }

  @Test
  public void testEditSeriesStartTimeCreatesSeparateSeries() {
    model.createRecurringTimedEvent("Weekly", baseDateTime, endDateTime,
            mondayWednesdayFriday, 6);

    LocalDateTime editFromDate = baseDateTime.plusWeeks(1);
    LocalDateTime newStartTime = baseDateTime.plusHours(1);
    model.editEvents("Weekly", editFromDate, "start", newStartTime.toString());

    List<IEvent> allEvents = model.printEvents(baseDateTime, baseDateTime.plusWeeks(3));

    // Check that events have different series IDs
    Integer originalSeriesId = null;
    Integer newSeriesId = null;

    for (IEvent event : allEvents) {
      if (event.getStartDateTime().toLocalDate().isBefore(editFromDate.toLocalDate())) {
        if (originalSeriesId == null) {
          originalSeriesId = event.getSeriesId();
        }
        assertEquals(originalSeriesId, event.getSeriesId());
      } else {
        if (newSeriesId == null) {
          newSeriesId = event.getSeriesId();
        }
        assertEquals(newSeriesId, event.getSeriesId());
        assertNotEquals("Should have different series ID", originalSeriesId, newSeriesId);
      }
    }
  }

  @Test
  public void testEditSingleEventInSeries() {
    model.createRecurringTimedEvent("Weekly", baseDateTime, endDateTime,
            mondayWednesdayFriday, 3);
    model.editEvent("Weekly", baseDateTime, endDateTime, "subject",
            "Special Meeting");

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));

    boolean foundSpecial = false;
    int weeklyCount = 0;

    for (IEvent event : events) {
      if (event.getSubject().equals("Special Meeting")) {
        foundSpecial = true;
        assertEquals(baseDateTime, event.getStartDateTime());
      } else if (event.getSubject().equals("Weekly")) {
        weeklyCount++;
      }
    }

    assertTrue("Should find the specially edited event", foundSpecial);
    assertEquals("Should still have other Weekly events", 2, weeklyCount);
  }

  @Test
  public void testPrintEventsOnDate() {
    model.createSingleTimedEvent("Morning",
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 10, 0));
    model.createSingleTimedEvent("Afternoon",
            LocalDateTime.of(2024, 3, 20, 14, 0),
            LocalDateTime.of(2024, 3, 20, 15, 0));
    model.createSingleTimedEvent("Next Day",
            LocalDateTime.of(2024, 3, 21, 9, 0),
            LocalDateTime.of(2024, 3, 21, 10, 0));

    List<IEvent> eventsOnDate = model.printEvents(LocalDateTime.of(2024, 3,
            20, 0, 0));
    assertEquals(2, eventsOnDate.size());
    assertEquals("Morning", eventsOnDate.get(0).getSubject());
    assertEquals("Afternoon", eventsOnDate.get(1).getSubject());
  }

  @Test
  public void testPrintEventsInInterval() {
    model.createSingleTimedEvent("Event1",
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 10, 0));
    model.createSingleTimedEvent("Event2",
            LocalDateTime.of(2024, 3, 22, 9, 0),
            LocalDateTime.of(2024, 3, 22, 10, 0));
    model.createSingleTimedEvent("Event3",
            LocalDateTime.of(2024, 3, 25, 9, 0),
            LocalDateTime.of(2024, 3, 25, 10, 0));

    List<IEvent> events = model.printEvents(
            LocalDateTime.of(2024, 3, 21, 0, 0),
            LocalDateTime.of(2024, 3, 24, 23, 59));

    assertEquals(1, events.size());
    assertEquals("Event2", events.get(0).getSubject());
  }

  @Test
  public void testPrintEventsEmptyResult() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    List<IEvent> events = model.printEvents(nextDay);
    assertEquals(0, events.size());
  }

  @Test
  public void testPrintEventsSortedByStartTime() {
    model.createSingleTimedEvent("Late",
            LocalDateTime.of(2024, 3, 20, 15, 0),
            LocalDateTime.of(2024, 3, 20, 16, 0));
    model.createSingleTimedEvent("Early",
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 10, 0));
    model.createSingleTimedEvent("Middle",
            LocalDateTime.of(2024, 3, 20, 12, 0),
            LocalDateTime.of(2024, 3, 20, 13, 0));

    List<IEvent> events = model.printEvents(LocalDateTime.of(2024, 3, 20,
            0, 0));
    assertEquals(3, events.size());
    assertEquals("Early", events.get(0).getSubject());
    assertEquals("Middle", events.get(1).getSubject());
    assertEquals("Late", events.get(2).getSubject());
  }

  @Test
  public void testShowStatusBusy() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    assertTrue("Should be busy during event",
            model.showStatus(baseDateTime.plusMinutes(30)));
  }

  @Test
  public void testShowStatusAvailable() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    assertFalse("Should be available before event",
            model.showStatus(baseDateTime.minusMinutes(30)));
    assertFalse("Should be available after event",
            model.showStatus(endDateTime.plusMinutes(30)));
  }

  @Test
  public void testShowStatusAtEventBoundaries() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    assertTrue("Should be busy at start time", model.showStatus(baseDateTime));
    assertTrue("Should be busy at end time", model.showStatus(endDateTime));
  }

  @Test
  public void testShowStatusAllDayEvent() {
    LocalDateTime date = LocalDateTime.of(2024, 3, 20, 0, 0);
    model.createSingleAllDayEvent("Holiday", date);

    assertTrue("Should be busy during all-day event hours",
            model.showStatus(LocalDateTime.of(2024, 3,
                    20, 12, 0)));
    assertFalse("Should be available outside all-day event hours",
            model.showStatus(LocalDateTime.of(2024, 3,
                    20, 6, 0)));
  }

  @Test
  public void testShowStatusMultipleEvents() {
    model.createSingleTimedEvent("Morning",
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 10, 0));
    model.createSingleTimedEvent("Afternoon",
            LocalDateTime.of(2024, 3, 20, 14, 0),
            LocalDateTime.of(2024, 3, 20, 15, 0));

    assertTrue("Should be busy during morning event",
            model.showStatus(LocalDateTime.of(2024, 3,
                    20, 9, 30)));
    assertFalse("Should be available between events",
            model.showStatus(LocalDateTime.of(2024, 3,
                    20, 12, 0)));
    assertTrue("Should be busy during afternoon event",
            model.showStatus(LocalDateTime.of(2024, 3,
                    20, 14, 30)));
  }

  @Test
  public void testComplexSeriesEditingScenario() {
    // Recreate the scenario from assignment description
    model.createRecurringTimedEvent("First",
            LocalDateTime.of(2025, 5, 5, 10, 0),
            LocalDateTime.of(2025, 5, 5, 11, 0),
            new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)), 6);

    // Edit subject starting from May 12
    model.editEvents("First", LocalDateTime.of(2025, 5,
            12, 10, 0), "subject", "Second");

    // Edit subject for all events in series starting from May 5
    model.editSeries("First", LocalDateTime.of(2025, 5,
            5, 10, 0), "subject", "Third");

    List<IEvent> events = model.printEvents(
            LocalDateTime.of(2025, 5, 1, 0, 0),
            LocalDateTime.of(2025, 6, 1, 0, 0));

    // All events should now be "Third" since editSeries was called last
    for (IEvent event : events) {
      assertEquals("Third", event.getSubject());
    }
  }

  @Test
  public void testOverlappingEventsAllowed() {
    model.createSingleTimedEvent("Meeting 1", baseDateTime, endDateTime);
    model.createSingleTimedEvent("Meeting 2", baseDateTime.plusMinutes(30),
            endDateTime.plusMinutes(30));

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(2, events.size());
    assertTrue("Should be busy during overlap",
            model.showStatus(baseDateTime.plusMinutes(45)));
  }

  @Test
  public void testRecurringEventSpansWeeks() {
    model.createRecurringTimedEvent("Weekly", baseDateTime, endDateTime,
            new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY)), 4);

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(4));
    assertEquals(4, events.size());

    // Check that events are exactly one week apart
    for (int i = 1; i < events.size(); i++) {
      long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
              events.get(i - 1).getStartDateTime(), events.get(i).getStartDateTime());
      assertEquals(7, daysBetween);
    }
  }

  @Test
  public void testEmptyCalendarQueries() {
    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(0, events.size());

    assertFalse("Empty calendar should show available", model.showStatus(baseDateTime));

    List<IEvent> intervalEvents = model.printEvents(baseDateTime, endDateTime);
    assertEquals(0, intervalEvents.size());
  }

  @Test
  public void testLargeNumberOfRecurringEvents() {
    model.createRecurringTimedEvent("Daily", baseDateTime, endDateTime,
            new ArrayList<>(Arrays.asList(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)), 50);

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(12));
    assertEquals(50, events.size());

    // All should have the same series ID
    Integer seriesId = events.get(0).getSeriesId();
    for (IEvent event : events) {
      assertEquals(seriesId, event.getSeriesId());
    }
  }


  // Additional Edge Case and Boundary Tests

  @Test
  public void testLeapYearHandling() {
    LocalDateTime leapDay = LocalDateTime.of(2024, 2, 29, 10, 0);
    model.createSingleTimedEvent("Leap Day Meeting", leapDay, leapDay.plusHours(1));

    List<IEvent> events = model.printEvents(leapDay);
    assertEquals(1, events.size());
    assertEquals("Leap Day Meeting", events.get(0).getSubject());
  }

  @Test
  public void testYearBoundaryRecurring() {
    LocalDateTime yearEnd = LocalDateTime.of(2024, 12, 30, 10, 0);
    model.createRecurringTimedEvent("New Year Meeting", yearEnd, yearEnd.plusHours(1),
            new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)), 5);

    List<IEvent> events = model.printEvents(yearEnd, LocalDateTime.of(2025, 1, 15, 0, 0));
    assertTrue("Should create events across year boundary", events.size() > 0);
  }

  @Test
  public void testMidnightBoundaryEvents() {
    LocalDateTime almostMidnight = LocalDateTime.of(2024, 3, 20, 23, 59);
    LocalDateTime pastMidnight = LocalDateTime.of(2024, 3, 21, 0, 1);
    model.createSingleTimedEvent("Midnight Spanning", almostMidnight, pastMidnight);

    List<IEvent> events = model.printEvents(LocalDateTime.of(2024, 3, 20, 0, 0));
    assertEquals(1, events.size());
  }

  @Test
  public void testAllDayEventTimeBoundariesPrecise() {
    LocalDateTime date = LocalDateTime.of(2024, 3, 20, 12, 0);
    model.createSingleAllDayEvent("Holiday", date);

    // Test exact boundary times
    assertTrue("Should be busy at 8:00 AM start",
            model.showStatus(date.toLocalDate().atTime(8, 0)));
    assertTrue("Should be busy at 5:00 PM end",
            model.showStatus(date.toLocalDate().atTime(17, 0)));
    assertFalse("Should be free at 7:59 AM",
            model.showStatus(date.toLocalDate().atTime(7, 59)));
    assertFalse("Should be free at 5:01 PM",
            model.showStatus(date.toLocalDate().atTime(17, 1)));
  }

  @Test
  public void testSeriesIdIncrementing() {
    model.createRecurringTimedEvent("Series1", baseDateTime, endDateTime,
            mondayWednesdayFriday, 2);
    model.createRecurringTimedEvent("Series2", nextDay, nextDay.plusHours(1),
            weekends, 2);

    List<IEvent> allEvents = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));
    Set<Integer> seriesIds = new HashSet<>();
    for (IEvent event : allEvents) {
      if (event.getSeriesId() != null) {
        seriesIds.add(event.getSeriesId());
      }
    }
    assertEquals("Should have exactly 2 distinct series IDs", 2, seriesIds.size());
  }

  @Test
  public void testEditEventWithIdenticalTimes() {
    model.createSingleTimedEvent("Meeting A", baseDateTime, endDateTime);
    model.createSingleTimedEvent("Meeting B", baseDateTime, endDateTime);

    model.editEvent("Meeting A", baseDateTime, endDateTime, "subject", "Updated A");

    List<IEvent> events = model.printEvents(baseDateTime);
    boolean foundUpdatedA = false;
    boolean foundMeetingB = false;

    for (IEvent event : events) {
      if (event.getSubject().equals("Updated A")) {
        foundUpdatedA = true;
      } else if (event.getSubject().equals("Meeting B")) {
        foundMeetingB = true;
      }
    }

    assertTrue("Should find Updated A", foundUpdatedA);
    assertTrue("Should still find Meeting B unchanged", foundMeetingB);
  }

  @Test
  public void testSubjectWithSpecialCharacters() {
    String specialSubject = "Meeting with \"quotes\" & symbols!";
    model.createSingleTimedEvent(specialSubject, baseDateTime, endDateTime);

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals(specialSubject, events.get(0).getSubject());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSubjectWithWhitespaceOnly() {
    model.createSingleTimedEvent("   ", baseDateTime, endDateTime);
  }

  @Test
  public void testEditPropertyCaseSensitivity() {
    model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
    model.editEvent("Meeting", baseDateTime, endDateTime, "SUBJECT", "New Name");

    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals("New Name", events.get(0).getSubject());
  }

  @Test
  public void testVeryLongTimeRanges() {
    LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);

    model.createSingleTimedEvent("Long Range Test", start, start.plusHours(1));
    List<IEvent> events = model.printEvents(start, end);
    assertEquals(1, events.size());
  }

  @Test
  public void testMaxStringLengths() {
    StringBuilder longSubject = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longSubject.append("A");
    }

    model.createSingleTimedEvent(longSubject.toString(), baseDateTime, endDateTime);
    List<IEvent> events = model.printEvents(baseDateTime);
    assertEquals(1, events.size());
    assertEquals(longSubject.toString(), events.get(0).getSubject());
  }

  @Test
  public void testRecurringEventUntilExactBoundary() {
    LocalDateTime until = baseDateTime.plusWeeks(2);
    model.createRecurringTimedEventUntil("Weekly", baseDateTime, endDateTime,
            new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY)), until);

    List<IEvent> events = model.printEvents(baseDateTime, until.plusDays(1));
    for (IEvent event : events) {
      assertFalse("Event should not be after until date",
              event.getStartDateTime().toLocalDate().isAfter(until.toLocalDate()));
    }
  }

  @Test
  public void testEventAtExactMidnight() {
    LocalDateTime midnight = LocalDateTime.of(2024, 3, 21, 0, 0);
    model.createSingleTimedEvent("Midnight Event", midnight, midnight.plusHours(1));

    List<IEvent> events = model.printEvents(midnight);
    assertEquals(1, events.size());
    assertTrue("Should be busy at midnight", model.showStatus(midnight));
  }

  @Test
  public void testMultipleSeriesEditing() {
    // Create multiple series
    model.createRecurringTimedEvent("Daily", baseDateTime, endDateTime,
            new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)), 4);
    model.createRecurringTimedEvent("Weekly", baseDateTime.plusHours(2),
            baseDateTime.plusHours(3),
            new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY)), 3);

    // Edit one series
    model.editSeries("Daily", baseDateTime, "subject", "Updated Daily");

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(3));

    boolean foundUpdatedDaily = false;
    boolean foundWeekly = false;
    for (IEvent event : events) {
      if (event.getSubject().equals("Updated Daily")) {
        foundUpdatedDaily = true;
      } else if (event.getSubject().equals("Weekly")) {
        foundWeekly = true;
      }
    }

    assertTrue("Should find updated daily events", foundUpdatedDaily);
    assertTrue("Should find unchanged weekly events", foundWeekly);
  }

  @Test
  public void testPrintEventsWithOverlappingRanges() {
    model.createSingleTimedEvent("Event1",
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 11, 0));
    model.createSingleTimedEvent("Event2",
            LocalDateTime.of(2024, 3, 20, 10, 0),
            LocalDateTime.of(2024, 3, 20, 12, 0));

    List<IEvent> events = model.printEvents(
            LocalDateTime.of(2024, 3, 20, 9, 30),
            LocalDateTime.of(2024, 3, 20, 10, 30));

    assertEquals("Both events should overlap with query range", 2, events.size());
  }

  @Test
  public void testShowStatusWithZeroDurationEvent() {
    // Edge case: what happens with very short events
    LocalDateTime start = baseDateTime;
    LocalDateTime end = baseDateTime.plusMinutes(1);
    model.createSingleTimedEvent("Short Event", start, end);

    assertTrue("Should be busy during short event", model.showStatus(start));
    assertTrue("Should be busy at end of short event", model.showStatus(end));
  }

  @Test
  public void testRecurringAllDayEventMultipleWeekdays() {
    model.createRecurringAllDayEvent("Multi-day Holiday", baseDateTime,
            new ArrayList<>(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)), 4);

    List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(3));
    assertEquals(4, events.size());

    for (IEvent event : events) {
      assertTrue("Should only occur on weekends",
              event.getStartDateTime().getDayOfWeek() == DayOfWeek.SATURDAY ||
                      event.getStartDateTime().getDayOfWeek() == DayOfWeek.SUNDAY);
    }
  }
}