package model;

import calendar.model.CalendarModel;
import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarModelTest {
    private ICalendarModel model;
    private LocalDateTime baseDateTime;
    private LocalDateTime endDateTime;

    // Constants matching the implementation
    private static final LocalTime ALL_DAY_START = LocalTime.of(8, 0);  // 8 AM
    private static final LocalTime ALL_DAY_END = LocalTime.of(17, 0);   // 5 PM

    @Before
    public void setUp() {
        model = new CalendarModel();
        baseDateTime = LocalDateTime.of(2024, 3, 20, 10, 0); // March 20, 2024, 10:00 AM
        endDateTime = LocalDateTime.of(2024, 3, 20, 11, 0);  // March 20, 2024, 11:00 AM
    }

    // Test creating a single timed event
    @Test
    public void testCreateSingleTimedEvent() {
        model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
        List<IEvent> events = model.printEvents(baseDateTime);
        assertEquals(1, events.size());
        assertEquals("Meeting", events.get(0).getSubject());
        assertEquals(baseDateTime, events.get(0).getStartDateTime());
        assertEquals(endDateTime, events.get(0).getEndDateTime());
    }

    // Test creating a single all-day event
    @Test
    public void testCreateSingleAllDayEvent() {
        LocalDateTime date = LocalDateTime.of(2024, 3, 20, 0, 0);
        model.createSingleAllDayEvent("Holiday", date);
        List<IEvent> events = model.printEvents(date);
        assertEquals(1, events.size());
        assertEquals("Holiday", events.get(0).getSubject());
        // Fixed: All-day events should be 8 AM to 5 PM
        assertEquals(date.toLocalDate().atTime(ALL_DAY_START), events.get(0).getStartDateTime());
        assertEquals(date.toLocalDate().atTime(ALL_DAY_END), events.get(0).getEndDateTime());
    }

    // Test creating a recurring timed event with count
    @Test
    public void testCreateRecurringTimedEvent() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.FRIDAY);

        model.createRecurringTimedEvent("Weekly Meeting", baseDateTime, endDateTime, weekdays, 3);
        List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));
        assertEquals(3, events.size());
        for (IEvent event : events) {
            assertEquals("Weekly Meeting", event.getSubject());
            assertNotNull(event.getSeriesId());
        }
    }

    // Test creating a recurring timed event until date
    @Test
    public void testCreateRecurringTimedEventUntil() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.FRIDAY);

        LocalDateTime untilDate = baseDateTime.plusWeeks(2);
        model.createRecurringTimedEventUntil("Weekly Meeting", baseDateTime, endDateTime, weekdays, untilDate);
        List<IEvent> events = model.printEvents(baseDateTime, untilDate);
        assertTrue(events.size() > 0);
        for (IEvent event : events) {
            assertEquals("Weekly Meeting", event.getSubject());
            assertNotNull(event.getSeriesId());
        }
    }

    // Test creating a recurring all-day event with count
    @Test
    public void testCreateRecurringAllDayEvent() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.FRIDAY);

        model.createRecurringAllDayEvent("Weekly Holiday", baseDateTime, weekdays, 3);
        List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));
        assertEquals(3, events.size());
        for (IEvent event : events) {
            assertEquals("Weekly Holiday", event.getSubject());
            assertNotNull(event.getSeriesId());
            // Fixed: All-day events should be 8 AM to 5 PM
            assertEquals(event.getStartDateTime().toLocalDate().atTime(ALL_DAY_START), event.getStartDateTime());
            assertEquals(event.getStartDateTime().toLocalDate().atTime(ALL_DAY_END), event.getEndDateTime());
        }
    }

    // Test creating a recurring all-day event until date
    @Test
    public void testCreateRecurringAllDayEventUntil() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.FRIDAY);

        LocalDateTime untilDate = baseDateTime.plusWeeks(2);
        model.createRecurringAllDayEventUntil("Weekly Holiday", baseDateTime, weekdays, untilDate);
        List<IEvent> events = model.printEvents(baseDateTime, untilDate);
        assertTrue(events.size() > 0);
        for (IEvent event : events) {
            assertEquals("Weekly Holiday", event.getSubject());
            assertNotNull(event.getSeriesId());
            // Fixed: All-day events should be 8 AM to 5 PM
            assertEquals(event.getStartDateTime().toLocalDate().atTime(ALL_DAY_START), event.getStartDateTime());
            assertEquals(event.getStartDateTime().toLocalDate().atTime(ALL_DAY_END), event.getEndDateTime());
        }
    }

    // Test editing a single event
    @Test
    public void testEditEvent() {
        model.createSingleTimedEvent("Original Meeting", baseDateTime, endDateTime);
        model.editEvent("Original Meeting", baseDateTime, endDateTime, "subject", "Updated Meeting");
        List<IEvent> events = model.printEvents(baseDateTime);
        assertEquals(1, events.size());
        assertEquals("Updated Meeting", events.get(0).getSubject());
    }

    // Test editing events in a series from a date
    @Test
    public void testEditEvents() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.FRIDAY);

        // Create 5 events instead of 3 to ensure we have events after the edit date
        model.createRecurringTimedEvent("Weekly Meeting", baseDateTime, endDateTime, weekdays, 5);

        // Print all events to see what was created
        List<IEvent> allEvents = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));

        LocalDateTime editFromDate = baseDateTime.plusWeeks(1);
        model.editEvents("Weekly Meeting", editFromDate, "subject", "Updated Weekly Meeting");

        List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));

        for (IEvent event : events) {
            if (!event.getStartDateTime().isBefore(editFromDate)) {
                assertEquals("Updated Weekly Meeting", event.getSubject());
            } else {
                assertEquals("Weekly Meeting", event.getSubject());
            }
        }
    }

    // Test editing an entire series
    @Test
    public void testEditSeries() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.FRIDAY);

        model.createRecurringTimedEvent("Weekly Meeting", baseDateTime, endDateTime, weekdays, 3);
        model.editSeries("Weekly Meeting", baseDateTime, "subject", "Updated Weekly Meeting");

        List<IEvent> events = model.printEvents(baseDateTime, baseDateTime.plusWeeks(2));
        for (IEvent event : events) {
            assertEquals("Updated Weekly Meeting", event.getSubject());
        }
    }

    // Test printing events on a date
    @Test
    public void testPrintEventsOnDate() {
        model.createSingleTimedEvent("Morning Meeting",
                LocalDateTime.of(2024, 3, 20, 9, 0),
                LocalDateTime.of(2024, 3, 20, 10, 0));
        model.createSingleTimedEvent("Afternoon Meeting",
                LocalDateTime.of(2024, 3, 20, 14, 0),
                LocalDateTime.of(2024, 3, 20, 15, 0));
        model.createSingleTimedEvent("Next Day Meeting",
                LocalDateTime.of(2024, 3, 21, 9, 0),
                LocalDateTime.of(2024, 3, 21, 10, 0));

        List<IEvent> eventsOnDate = model.printEvents(LocalDateTime.of(2024, 3, 20, 0, 0));
        assertEquals(2, eventsOnDate.size());
    }

    // Test printing events in an interval
    @Test
    public void testPrintEventsInInterval() {
        LocalDateTime intervalStart = LocalDateTime.of(2024, 3, 20, 8, 0);
        LocalDateTime intervalEnd = LocalDateTime.of(2024, 3, 20, 12, 0);

        model.createSingleTimedEvent("Morning Meeting",
                LocalDateTime.of(2024, 3, 20, 9, 0),
                LocalDateTime.of(2024, 3, 20, 10, 0));
        model.createSingleTimedEvent("Afternoon Meeting",
                LocalDateTime.of(2024, 3, 20, 14, 0),
                LocalDateTime.of(2024, 3, 20, 15, 0));

        List<IEvent> eventsInInterval = model.printEvents(intervalStart, intervalEnd);
        assertEquals(1, eventsInInterval.size());
        assertEquals("Morning Meeting", eventsInInterval.get(0).getSubject());
    }

    // Test showing status
    @Test
    public void testShowStatus() {
        model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);

        // Test during the event
        assertTrue(model.showStatus(LocalDateTime.of(2024, 3, 20, 10, 30)));

        // Test before the event
        assertFalse(model.showStatus(LocalDateTime.of(2024, 3, 20, 9, 30)));

        // Test after the event
        assertFalse(model.showStatus(LocalDateTime.of(2024, 3, 20, 11, 30)));
    }

    // Test showing status for all-day events
    @Test
    public void testShowStatusAllDay() {
        LocalDateTime date = LocalDateTime.of(2024, 3, 20, 0, 0);
        model.createSingleAllDayEvent("Conference", date);

        // Before 8 AM - should be available
        assertFalse(model.showStatus(LocalDateTime.of(2024, 3, 20, 7, 30)));

        // During business hours (8 AM - 5 PM) - should be busy
        assertTrue(model.showStatus(LocalDateTime.of(2024, 3, 20, 8, 0)));
        assertTrue(model.showStatus(LocalDateTime.of(2024, 3, 20, 12, 0)));
        assertTrue(model.showStatus(LocalDateTime.of(2024, 3, 20, 17, 0)));

        // After 5 PM - should be available
        assertFalse(model.showStatus(LocalDateTime.of(2024, 3, 20, 17, 1)));
    }

    // Error cases
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEventCreation() {
        model.createSingleTimedEvent("", baseDateTime, endDateTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeRange() {
        model.createSingleTimedEvent("Invalid Meeting", endDateTime, baseDateTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidWeekdays() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        model.createRecurringTimedEvent("Meeting", baseDateTime, endDateTime, weekdays, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCount() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        model.createRecurringTimedEvent("Meeting", baseDateTime, endDateTime, weekdays, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUntilDate() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        model.createRecurringTimedEventUntil("Meeting", baseDateTime, endDateTime, weekdays, baseDateTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditNonExistentEvent() {
        model.editEvent("Non-existent", baseDateTime, endDateTime, "subject", "New Subject");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidProperty() {
        model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
        model.editEvent("Meeting", baseDateTime, endDateTime, "invalid", "value");
    }

    // Test duplicate event prevention
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateEventPrevention() {
        model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime);
        model.createSingleTimedEvent("Meeting", baseDateTime, endDateTime); // Should throw
    }

    // Test recurring event single-day constraint
    @Test(expected = IllegalArgumentException.class)
    public void testRecurringEventMustBeWithinSingleDay() {
        ArrayList<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);

        LocalDateTime start = LocalDateTime.of(2024, 3, 20, 22, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 21, 2, 0); // Next day

        model.createRecurringTimedEvent("Night Meeting", start, end, weekdays, 3); // Should throw
    }
}