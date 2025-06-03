import calendar.model.CalendarModel;
import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class CalendarModelTest {
    private ICalendarModel model;
    private LocalDateTime baseDateTime;
    private LocalDateTime endDateTime;

    @Before
    public void setUp() {
        model = new CalendarModel();
        baseDateTime = LocalDateTime.of(2024, 3, 20, 10, 0); // March 20, 2024, 10:00 AM
        endDateTime = LocalDateTime.of(2024, 3, 20, 11, 0);  // March 20, 2024, 11:00 AM
    }

    // Test creating a single timed event
    @Test
    public void testCreateSingleTimedEvent() {
        model.createEvent("Meeting", baseDateTime, endDateTime, null);
        List<IEvent> events = model.getEvents();
        assertEquals(1, events.size());
        assertEquals("Meeting", events.get(0).getSubject());
        assertEquals(baseDateTime, events.get(0).getStartDateTime());
        assertEquals(endDateTime, events.get(0).getEndDateTime());
    }

    // Test creating an all-day event (8am to 5pm)
    @Test
    public void testCreateAllDayEvent() {
        LocalDateTime startOfDay = LocalDateTime.of(2024, 3, 20, 8, 0);
        LocalDateTime endOfDay = LocalDateTime.of(2024, 3, 20, 17, 0);
        model.createEvent("All Day Meeting", startOfDay, endOfDay, null);
        List<IEvent> events = model.getEvents();
        assertEquals(1, events.size());
        assertEquals("All Day Meeting", events.get(0).getSubject());
        assertEquals(startOfDay, events.get(0).getStartDateTime());
        assertEquals(endOfDay, events.get(0).getEndDateTime());
    }

    // Test creating a multi-day event
    @Test
    public void testCreateMultiDayEvent() {
        LocalDateTime start = LocalDateTime.of(2024, 3, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 22, 10, 0);
        model.createEvent("Multi-day Conference", start, end, null);
        List<IEvent> events = model.getEvents();
        assertEquals(1, events.size());
        assertEquals("Multi-day Conference", events.get(0).getSubject());
        assertEquals(start, events.get(0).getStartDateTime());
        assertEquals(end, events.get(0).getEndDateTime());
    }

    // Test creating a recurring event series
    @Test
    public void testCreateRecurringEventSeries() {
        Integer seriesId = model.getNextSeriesId();
        LocalDateTime start = LocalDateTime.of(2024, 3, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 20, 11, 0);
        
        // Create 3 events in the series
        for (int i = 0; i < 3; i++) {
            model.createEvent("Weekly Meeting", start.plusWeeks(i), end.plusWeeks(i), seriesId);
        }

        List<IEvent> events = model.getEvents();
        assertEquals(3, events.size());
        for (IEvent event : events) {
            assertEquals("Weekly Meeting", event.getSubject());
            assertEquals(seriesId, event.getSeriesId());
        }
    }

    // Test duplicate event prevention
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateEventPrevention() {
        model.createEvent("Meeting", baseDateTime, endDateTime, null);
        model.createEvent("Meeting", baseDateTime, endDateTime, null);
    }

    // Test querying events on a specific date
    @Test
    public void testGetEventsOnDate() {
        LocalDate testDate = LocalDate.of(2024, 3, 20);
        model.createEvent("Morning Meeting", 
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 10, 0), null);
        model.createEvent("Afternoon Meeting",
            LocalDateTime.of(2024, 3, 20, 14, 0),
            LocalDateTime.of(2024, 3, 20, 15, 0), null);
        model.createEvent("Next Day Meeting",
            LocalDateTime.of(2024, 3, 21, 9, 0),
            LocalDateTime.of(2024, 3, 21, 10, 0), null);

        List<IEvent> eventsOnDate = model.getEventsOnDate(testDate);
        assertEquals(2, eventsOnDate.size());
    }

    // Test querying events in a time interval
    @Test
    public void testGetEventsInInterval() {
        LocalDateTime intervalStart = LocalDateTime.of(2024, 3, 20, 8, 0);
        LocalDateTime intervalEnd = LocalDateTime.of(2024, 3, 20, 12, 0);
        
        model.createEvent("Morning Meeting",
            LocalDateTime.of(2024, 3, 20, 9, 0),
            LocalDateTime.of(2024, 3, 20, 10, 0), null);
        model.createEvent("Afternoon Meeting",
            LocalDateTime.of(2024, 3, 20, 14, 0),
            LocalDateTime.of(2024, 3, 20, 15, 0), null);

        List<IEvent> eventsInInterval = model.getEventsInInterval(intervalStart, intervalEnd);
        assertEquals(1, eventsInInterval.size());
        assertEquals("Morning Meeting", eventsInInterval.get(0).getSubject());
    }

    // Test checking if a time is busy
    @Test
    public void testIsTimeBusy() {
        model.createEvent("Meeting", baseDateTime, endDateTime, null);
        
        // Test during the event
        assertTrue(model.isTimeBusy(LocalDateTime.of(2024, 3, 20, 10, 30)));
        
        // Test before the event
        assertFalse(model.isTimeBusy(LocalDateTime.of(2024, 3, 20, 9, 30)));
        
        // Test after the event
        assertFalse(model.isTimeBusy(LocalDateTime.of(2024, 3, 20, 11, 30)));
    }

    // Test editing an event
    @Test
    public void testEditEvent() {
        model.createEvent("Original Meeting", baseDateTime, endDateTime, null);
        model.editEvent("Original Meeting", baseDateTime, endDateTime, "subject", "Updated Meeting");
        
        List<IEvent> events = model.getEvents();
        assertEquals(1, events.size());
        assertEquals("Updated Meeting", events.get(0).getSubject());
    }

    // Test editing a series of events
    @Test
    public void testEditEventSeries() {
        int seriesId = model.getNextSeriesId();
        LocalDateTime start = LocalDateTime.of(2024, 3, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 20, 11, 0);
        
        // Create 3 events in the series
        for (int i = 0; i < 3; i++) {
            model.createEvent("Weekly Meeting", start.plusWeeks(i), end.plusWeeks(i), seriesId);
        }

        // Edit all events in the series
        model.editSeries("Weekly Meeting", start, "subject", "Updated Weekly Meeting");
        
        List<IEvent> events = model.getEvents();
        for (IEvent event : events) {
            assertEquals("Updated Weekly Meeting", event.getSubject());
        }
    }

    // Test invalid event creation
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEventCreation() {
        model.createEvent("", baseDateTime, endDateTime, null);
    }

    // Test invalid time range
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeRange() {
        model.createEvent("Invalid Meeting", endDateTime, baseDateTime, null);
    }
} 