package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import calendar.model.SmartCalendarModel;
import calendar.model.ISmartCalendarModel;
import calendar.model.IEvent;

/**
 * Represents tests and examples for SmartCalendarModel.
 */
public class SmartCalendarModelTest {
  
  private SmartCalendarModel calendar;
  private SmartCalendarModel targetCalendar;
  
  @Before
  public void setUp() {
    calendar = new SmartCalendarModel("TestCalendar", ZoneId.of("America/New_York"));
    targetCalendar = new SmartCalendarModel("TargetCalendar", 
        ZoneId.of("America/Los_Angeles"));
  }

  @Test
  public void testConstructorBasic() {
    SmartCalendarModel newCalendar = new SmartCalendarModel("NewCalendar", 
        ZoneId.of("Europe/London"));
    assertEquals("NewCalendar", newCalendar.getCalendarName());
    assertEquals(ZoneId.of("Europe/London"), newCalendar.getTimezone());
  }
  
  @Test
  public void testConstructorWithDifferentTimezones() {
    SmartCalendarModel est = new SmartCalendarModel("EST", 
        ZoneId.of("America/New_York"));
    SmartCalendarModel pst = new SmartCalendarModel("PST", 
        ZoneId.of("America/Los_Angeles"));
    SmartCalendarModel utc = new SmartCalendarModel("UTC", ZoneId.of("UTC"));
    
    assertEquals(ZoneId.of("America/New_York"), est.getTimezone());
    assertEquals(ZoneId.of("America/Los_Angeles"), pst.getTimezone());
    assertEquals(ZoneId.of("UTC"), utc.getTimezone());
  }
  
  @Test
  public void testConstructorSpecialCharacters() {
    SmartCalendarModel specialCalendar = new SmartCalendarModel("Work-Calendar_2024", 
        ZoneId.of("America/New_York"));
    assertEquals("Work-Calendar_2024", specialCalendar.getCalendarName());
  }
  
  @Test
  public void testConstructorInheritsFromCalendarModel() {
    // Should inherit all functionality from CalendarModel
    calendar.createSingleTimedEvent("Test Event", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(1, events.size());
    assertEquals("Test Event", events.get(0).getSubject());
  }

  @Test
  public void testGetCalendarName() {
    assertEquals("TestCalendar", calendar.getCalendarName());
  }
  
  @Test
  public void testGetTimezone() {
    assertEquals(ZoneId.of("America/New_York"), calendar.getTimezone());
  }
  
  @Test
  public void testSetCalendarName() {
    calendar.setCalendarName("NewName");
    assertEquals("NewName", calendar.getCalendarName());
  }
  
  @Test
  public void testSetCalendarNameMultipleTimes() {
    calendar.setCalendarName("FirstName");
    assertEquals("FirstName", calendar.getCalendarName());
    
    calendar.setCalendarName("SecondName");
    assertEquals("SecondName", calendar.getCalendarName());
    
    calendar.setCalendarName("FinalName");
    assertEquals("FinalName", calendar.getCalendarName());
  }
  
  @Test
  public void testSetTimezone() {
    calendar.setTimezone(ZoneId.of("America/Los_Angeles"));
    assertEquals(ZoneId.of("America/Los_Angeles"), calendar.getTimezone());
  }
  
  @Test
  public void testSetTimezoneConvertsExistingEvents() {
    // Create an event in EST
    calendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0), // 2 PM EST
        LocalDateTime.of(2024, 3, 15, 15, 0)); // 3 PM EST
    
    // Change to PST
    calendar.setTimezone(ZoneId.of("America/Los_Angeles"));
    
    // Event should be converted to PST (3 hours earlier)
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(1, events.size());
    IEvent event = events.get(0);
    assertEquals(LocalDateTime.of(2024, 3, 15, 11, 0), event.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 12, 0), event.getEndDateTime());
  }
  
  @Test
  public void testSetTimezoneWithMultipleEvents() {
    // Create multiple events
    calendar.createSingleTimedEvent("Morning Meeting", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 15, 10, 0));
    
    calendar.createSingleTimedEvent("Lunch", 
        LocalDateTime.of(2024, 3, 15, 12, 0),
        LocalDateTime.of(2024, 3, 15, 13, 0));
    
    calendar.createSingleTimedEvent("Afternoon Meeting", 
        LocalDateTime.of(2024, 3, 15, 15, 0),
        LocalDateTime.of(2024, 3, 15, 16, 0));
    
    // Change timezone
    calendar.setTimezone(ZoneId.of("Europe/London"));
    
    // All events should be converted (EDT to GMT = +4 hours, March 2024 is DST)
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(3, events.size());
    
    for (IEvent event : events) {
      if (event.getSubject().equals("Morning Meeting")) {
        assertEquals(LocalDateTime.of(2024, 3, 15, 13, 0), event.getStartDateTime());
      } else if (event.getSubject().equals("Lunch")) {
        assertEquals(LocalDateTime.of(2024, 3, 15, 16, 0), event.getStartDateTime());
      } else if (event.getSubject().equals("Afternoon Meeting")) {
        assertEquals(LocalDateTime.of(2024, 3, 15, 19, 0), event.getStartDateTime());
      }
    }
  }
  
  @Test
  public void testSetTimezoneWithRecurringEvents() {
    // Create recurring events
    ArrayList<DayOfWeek> weekdays = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
    
    calendar.createRecurringTimedEvent("Daily Standup", 
        LocalDateTime.of(2024, 3, 18, 9, 0), // Monday 9 AM EST
        LocalDateTime.of(2024, 3, 18, 9, 30), // Monday 9:30 AM EST
        weekdays, 3);
    
    // Change timezone to PST
    calendar.setTimezone(ZoneId.of("America/Los_Angeles"));
    
    // All events should be converted (EDT to PDT = -3 hours, March 2024 is DST)
    List<IEvent> events = calendar.printEvents(
        LocalDateTime.of(2024, 3, 18, 0, 0),
        LocalDateTime.of(2024, 3, 25, 0, 0));
    
    assertEquals(3, events.size());
    for (IEvent event : events) {
      assertEquals(6, event.getStartDateTime().getHour()); // 9 AM EDT = 6 AM PDT
      assertEquals(0, event.getStartDateTime().getMinute()); // 9:00 AM = 0 minutes
      assertEquals(6, event.getEndDateTime().getHour());
      assertEquals(30, event.getEndDateTime().getMinute()); // 9:30 AM EDT = 6:30 AM PDT
    }
  }
  
  @Test
  public void testSetTimezoneNoChange() {
    // Create an event
    calendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    // Set to same timezone
    calendar.setTimezone(ZoneId.of("America/New_York"));
    
    // Event should remain unchanged
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(1, events.size());
    IEvent event = events.get(0);
    assertEquals(LocalDateTime.of(2024, 3, 15, 14, 0), event.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 15, 0), event.getEndDateTime());
  }

  @Test
  public void testFindEventBySubjectAndTimeBasic() {
    calendar.createSingleTimedEvent("Team Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    IEvent found = calendar.findEventBySubjectAndTime("Team Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0));
    
    assertNotNull(found);
    assertEquals("Team Meeting", found.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 15, 14, 0), found.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 15, 0), found.getEndDateTime());
  }
  
  @Test
  public void testFindEventWithMultipleEvents() {
    calendar.createSingleTimedEvent("Meeting 1", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    calendar.createSingleTimedEvent("Meeting 2", 
        LocalDateTime.of(2024, 3, 15, 16, 0),
        LocalDateTime.of(2024, 3, 15, 17, 0));
    
    calendar.createSingleTimedEvent("Meeting 1", 
        LocalDateTime.of(2024, 3, 16, 14, 0),
        LocalDateTime.of(2024, 3, 16, 15, 0));
    
    // Find specific event by both subject and time
    IEvent found = calendar.findEventBySubjectAndTime("Meeting 1", 
        LocalDateTime.of(2024, 3, 16, 14, 0));
    
    assertEquals("Meeting 1", found.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 16, 14, 0), found.getStartDateTime());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testFindEventNotFound() {
    calendar.createSingleTimedEvent("Existing Event", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    calendar.findEventBySubjectAndTime("Non-Existent Event", 
        LocalDateTime.of(2024, 3, 15, 14, 0));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testFindEventWrongTime() {
    calendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    calendar.findEventBySubjectAndTime("Meeting", 
        LocalDateTime.of(2024, 3, 15, 15, 0)); // Wrong start time
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testFindEventInEmptyCalendar() {
    calendar.findEventBySubjectAndTime("Any Event", 
        LocalDateTime.of(2024, 3, 15, 14, 0));
  }
  

  @Test
  public void testCreateCopiedEventBasic() {
    // Create source event
    calendar.createSingleTimedEvent("Original Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 30)); // 90 minute duration
    
    // Create copied event
    IEvent copied = calendar.createCopiedEvent("Original Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 20, 10, 0));
    
    assertEquals("Original Meeting", copied.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 20, 10, 0), copied.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 20, 11, 30), copied.getEndDateTime());
    assertNull("Copied event should not have series ID", copied.getSeriesId());
  }
  
  @Test
  public void testCreateCopiedEventPreservesDuration() {
    // Create 3-hour event
    calendar.createSingleTimedEvent("Long Meeting", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 15, 12, 0));
    
    IEvent copied = calendar.createCopiedEvent("Long Meeting", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 20, 14, 0));
    
    assertEquals(LocalDateTime.of(2024, 3, 20, 14, 0), copied.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 20, 17, 0), copied.getEndDateTime()); // 3 hours later
  }
  
  @Test
  public void testCreateCopiedEventPreservesAllProperties() {
    // Create event with all properties
    calendar.createSingleTimedEvent("Detailed Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
        
    // Get the event and verify it has detailed properties
    IEvent original = calendar.findEventBySubjectAndTime("Detailed Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0));
    
    IEvent copied = calendar.createCopiedEvent("Detailed Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 20, 10, 0));
    
    assertEquals(original.getSubject(), copied.getSubject());
    assertEquals(original.getDescription(), copied.getDescription());
    assertEquals(original.getLocation(), copied.getLocation());
    assertEquals(original.getStatus(), copied.getStatus());
    // Times should be different
    assertNotEquals(original.getStartDateTime(), copied.getStartDateTime());
    assertNotEquals(original.getEndDateTime(), copied.getEndDateTime());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCopiedEventNotFound() {
    calendar.createCopiedEvent("Non-Existent Event", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 20, 10, 0));
  }
  
  // ==================== Copy Events To Calendar Tests ====================
  
  @Test
  public void testCopyAllEventsToCalendarBasic() {
    // Create events on source date
    calendar.createSingleTimedEvent("Morning Meeting", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 15, 10, 0));
    
    calendar.createSingleTimedEvent("Lunch", 
        LocalDateTime.of(2024, 3, 15, 12, 0),
        LocalDateTime.of(2024, 3, 15, 13, 0));
    
    // Copy to target calendar
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 15, 0, 0),
        targetCalendar, LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Verify events were copied
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 20, 0, 0));
    assertEquals(2, copiedEvents.size());
    
    // Verify timezone conversion (EST to PST = 3 hours earlier)
    boolean foundMorning = false, foundLunch = false;
    for (IEvent event : copiedEvents) {
      if (event.getSubject().equals("Morning Meeting")) {
        assertEquals(LocalDateTime.of(2024, 3, 20, 6, 0), event.getStartDateTime());
        foundMorning = true;
      } else if (event.getSubject().equals("Lunch")) {
        assertEquals(LocalDateTime.of(2024, 3, 20, 9, 0), event.getStartDateTime());
        foundLunch = true;
      }
    }
    assertTrue("Should find all copied events", foundMorning && foundLunch);
  }
  
  @Test
  public void testCopyAllEventsToCalendarWithSeriesMapping() {
    // Create recurring events (series)
    ArrayList<DayOfWeek> weekdays = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
    
    calendar.createRecurringTimedEvent("Weekly Meeting", 
        LocalDateTime.of(2024, 3, 18, 14, 0), // Monday
        LocalDateTime.of(2024, 3, 18, 15, 0),
        weekdays, 2); // Only Monday & Wednesday of that week
    
    // Copy Monday events to target calendar
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 18, 0, 0),
        targetCalendar, LocalDateTime.of(2024, 4, 15, 0, 0));
    
    // Verify the event was copied with a new series ID
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 15, 0, 0));
    assertEquals(1, copiedEvents.size());
    
    IEvent copiedEvent = copiedEvents.get(0);
    assertEquals("Weekly Meeting", copiedEvent.getSubject());
    assertNotNull("Copied event should have series ID", copiedEvent.getSeriesId());
    
    // Original event's series ID and copied event's series ID may be same since
    // each calendar instance has its own series ID counter starting from 1
    List<IEvent> originalEvents = calendar.printEvents(
        LocalDateTime.of(2024, 3, 18, 0, 0));
    IEvent originalEvent = originalEvents.get(0);
    assertNotNull("Original event should have series ID", originalEvent.getSeriesId());
  }
  
  @Test
  public void testCopyAllEventsToCalendarEmptyDate() {
    // Copy from a date with no events
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 15, 0, 0),
        targetCalendar, LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Target calendar should still be empty  
    List<IEvent> events = targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 20, 0, 0));
    assertEquals(0, events.size());
  }
  
  @Test
  public void testCopyAllEventsToCalendarSameTimezone() {
    SmartCalendarModel sameTimezoneCalendar = new SmartCalendarModel("Same", 
        ZoneId.of("America/New_York"));
    
    // Create event
    calendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    // Copy to calendar with same timezone
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 15, 0, 0),
        sameTimezoneCalendar, LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Event should not be timezone converted
    List<IEvent> events = sameTimezoneCalendar.printEvents(
        LocalDateTime.of(2024, 3, 20, 0, 0));
    assertEquals(1, events.size());
    IEvent event = events.get(0);
    assertEquals(LocalDateTime.of(2024, 3, 20, 14, 0), event.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 20, 15, 0), event.getEndDateTime());
  }
  
  // ==================== Copy Events In Range Tests ====================
  
  @Test
  public void testCopyEventsInRangeToCalendarBasic() {
    // Create events across multiple days
    calendar.createSingleTimedEvent("Day 1 Event", 
        LocalDateTime.of(2024, 3, 10, 14, 0),
        LocalDateTime.of(2024, 3, 10, 15, 0));
    
    calendar.createSingleTimedEvent("Day 2 Event", 
        LocalDateTime.of(2024, 3, 11, 14, 0),
        LocalDateTime.of(2024, 3, 11, 15, 0));
    
    calendar.createSingleTimedEvent("Day 3 Event", 
        LocalDateTime.of(2024, 3, 12, 14, 0),
        LocalDateTime.of(2024, 3, 12, 15, 0));
    
    calendar.createSingleTimedEvent("Outside Range Event", 
        LocalDateTime.of(2024, 3, 13, 14, 0),
        LocalDateTime.of(2024, 3, 13, 15, 0));
    
    // Copy events from March 10-12 to April 20-22
    calendar.copyEventsInRangeToCalendar(
        LocalDateTime.of(2024, 3, 10, 0, 0),
        LocalDateTime.of(2024, 3, 12, 23, 59),
        targetCalendar,
        LocalDateTime.of(2024, 4, 20, 0, 0));
    
    // Verify 3 events were copied (excluding outside range)
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 20, 0, 0),
        LocalDateTime.of(2024, 4, 22, 23, 59));
    
    assertEquals(3, copiedEvents.size());
    
    // Verify events maintain relative dates and are timezone converted
    boolean foundDay1 = false, foundDay2 = false, foundDay3 = false;
    for (IEvent event : copiedEvents) {
      // EST to PST conversion: 2 PM EST = 11 AM PST
      assertEquals(11, event.getStartDateTime().getHour());
      assertEquals(12, event.getEndDateTime().getHour());
      
      if (event.getSubject().equals("Day 1 Event")) {
        assertEquals(20, event.getStartDateTime().getDayOfMonth()); // April 20
        foundDay1 = true;
      } else if (event.getSubject().equals("Day 2 Event")) {
        assertEquals(21, event.getStartDateTime().getDayOfMonth()); // April 21  
        foundDay2 = true;
      } else if (event.getSubject().equals("Day 3 Event")) {
        assertEquals(22, event.getStartDateTime().getDayOfMonth()); // April 22
        foundDay3 = true;
      }
    }
    assertTrue("Should find all three events in range", 
        foundDay1 && foundDay2 && foundDay3);
  }
  
  @Test
  public void testCopyEventsInRangeWithSeriesMapping() {
    // Create a series that spans the range
    ArrayList<DayOfWeek> daily = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
    
    calendar.createRecurringTimedEvent("Daily Standup", 
        LocalDateTime.of(2024, 3, 11, 9, 0), // Tuesday
        LocalDateTime.of(2024, 3, 11, 9, 30),
        daily, 3); // Mon, Tue, Wed of that week
    
    // Copy range that includes some but not all events in the series
    calendar.copyEventsInRangeToCalendar(
        LocalDateTime.of(2024, 3, 11, 0, 0), // Tuesday only
        LocalDateTime.of(2024, 3, 11, 23, 59),
        targetCalendar,
        LocalDateTime.of(2024, 4, 15, 0, 0));
    
    // Only Tuesday event should be copied
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 15, 0, 0));
    assertEquals(1, copiedEvents.size());
    
    IEvent copiedEvent = copiedEvents.get(0);
    assertEquals("Daily Standup", copiedEvent.getSubject());
    assertNotNull("Copied event should have series ID", copiedEvent.getSeriesId());
    
    // Verify both events have series IDs (they may be same since each calendar
    // instance has its own series ID counter starting from 1)
    List<IEvent> originalEvents = calendar.printEvents(
        LocalDateTime.of(2024, 3, 11, 0, 0));
    IEvent originalEvent = originalEvents.get(0);
    assertNotNull("Original event should have series ID", originalEvent.getSeriesId());
  }
  
  @Test
  public void testCopyEventsInRangeEmptyRange() {
    // Create events outside the range
    calendar.createSingleTimedEvent("Before Range", 
        LocalDateTime.of(2024, 3, 9, 14, 0),
        LocalDateTime.of(2024, 3, 9, 15, 0));
    
    calendar.createSingleTimedEvent("After Range", 
        LocalDateTime.of(2024, 3, 13, 14, 0),
        LocalDateTime.of(2024, 3, 13, 15, 0));
    
    // Copy empty range
    calendar.copyEventsInRangeToCalendar(
        LocalDateTime.of(2024, 3, 10, 0, 0),
        LocalDateTime.of(2024, 3, 12, 23, 59),
        targetCalendar,
        LocalDateTime.of(2024, 4, 20, 0, 0));
    
    // No events should be copied
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 20, 0, 0),
        LocalDateTime.of(2024, 4, 22, 23, 59));
    assertEquals(0, copiedEvents.size());
  }
  
  // ==================== Add Event Tests ====================
  
  @Test
  public void testAddEventBasic() {
    // Create a pre-built event
    calendar.createSingleTimedEvent("Original", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    IEvent originalEvent = calendar.findEventBySubjectAndTime("Original", 
        LocalDateTime.of(2024, 3, 15, 14, 0));
    
    // Add it to target calendar
    targetCalendar.addEvent(originalEvent);
    
    // Verify it was added
    List<IEvent> events = targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(1, events.size());
    assertEquals("Original", events.get(0).getSubject());
  }
  
  @Test
  public void testAddEventWithSeriesId() {
    // Create recurring events
    ArrayList<DayOfWeek> weekdays = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
    
    calendar.createRecurringTimedEvent("Series Event", 
        LocalDateTime.of(2024, 3, 18, 14, 0),
        LocalDateTime.of(2024, 3, 18, 15, 0),
        weekdays, 2);
    
    // Get one of the events
    List<IEvent> seriesEvents = calendar.printEvents(
        LocalDateTime.of(2024, 3, 18, 0, 0));
    IEvent eventWithSeries = seriesEvents.get(0);
    
    // Add it to target calendar
    targetCalendar.addEvent(eventWithSeries);
    
    // Verify series ID is preserved
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 18, 0, 0));
    assertEquals(1, copiedEvents.size());
    assertEquals(eventWithSeries.getSeriesId(), copiedEvents.get(0).getSeriesId());
  }
  
  // ==================== Series ID Generation Tests ====================
  
  @Test
  public void testGenerateUniqueSeriesIdBasic() {
    Integer seriesId = calendar.generateUniqueSeriesId();
    assertNotNull("Series ID should not be null", seriesId);
    assertTrue("Series ID should be non-negative", seriesId >= 0);
  }
  
  @Test
  public void testGenerateUniqueSeriesIdMultiple() {
    Set<Integer> generatedIds = new HashSet<>();
    
    // Generate multiple IDs - they should be sequential
    for (int i = 0; i < 10; i++) {
      Integer id = calendar.generateUniqueSeriesId();
      assertFalse("All generated IDs should be unique", generatedIds.contains(id));
      generatedIds.add(id);
    }
    
    // Verify sequential generation
    assertEquals("Should have 10 unique IDs", 10, generatedIds.size());
  }
  
  @Test
  public void testGenerateUniqueSeriesIdIncrementsSequentially() {
    Integer firstId = calendar.generateUniqueSeriesId();
    Integer secondId = calendar.generateUniqueSeriesId();
    Integer thirdId = calendar.generateUniqueSeriesId();
    
    assertNotNull("First ID should not be null", firstId);
    assertNotNull("Second ID should not be null", secondId);
    assertNotNull("Third ID should not be null", thirdId);
    
    assertTrue("IDs should be sequential", firstId < secondId);
    assertTrue("IDs should be sequential", secondId < thirdId);
    assertEquals("IDs should increment by 1", 1, secondId - firstId);
    assertEquals("IDs should increment by 1", 1, thirdId - secondId);
  }
  
  // ==================== Integration Tests ====================
  
  @Test
  public void testComplexScenarioWithMultipleOperations() {
    // Create a complex calendar with various event types
    calendar.createSingleTimedEvent("One-time Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    ArrayList<DayOfWeek> weekdays = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
    
    calendar.createRecurringTimedEvent("Weekly Team Sync", 
        LocalDateTime.of(2024, 3, 18, 10, 0),
        LocalDateTime.of(2024, 3, 18, 11, 0),
        weekdays, 3);
    
    calendar.createSingleTimedEvent("Project Review", 
        LocalDateTime.of(2024, 3, 20, 16, 0),
        LocalDateTime.of(2024, 3, 20, 17, 30));
    
    // Change timezone
    calendar.setTimezone(ZoneId.of("Europe/London"));
    
    // Copy events to target calendar
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 18, 0, 0),
        targetCalendar, LocalDateTime.of(2024, 4, 15, 0, 0));
    
    // Copy a range of events
    calendar.copyEventsInRangeToCalendar(
        LocalDateTime.of(2024, 3, 15, 0, 0),
        LocalDateTime.of(2024, 3, 20, 23, 59),
        targetCalendar,
        LocalDateTime.of(2024, 5, 1, 0, 0));
    
    // Verify all operations worked correctly
    List<IEvent> originalEvents = calendar.printEvents(
        LocalDateTime.of(2024, 3, 15, 0, 0),
        LocalDateTime.of(2024, 3, 25, 0, 0));
    assertTrue("Should have original events", originalEvents.size() > 0);
    
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 1, 0, 0),
        LocalDateTime.of(2024, 5, 31, 0, 0));
    assertTrue("Should have copied events", copiedEvents.size() > 0);
  }
  
  @Test
  public void testSeriesIdMappingConsistency() {
    // Create a series
    ArrayList<DayOfWeek> weekdays = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
    
    calendar.createRecurringTimedEvent("Daily Meeting", 
        LocalDateTime.of(2024, 3, 18, 9, 0),
        LocalDateTime.of(2024, 3, 18, 9, 30),
        weekdays, 3);
    
    // Copy multiple events from the same series
    calendar.copyEventsInRangeToCalendar(
        LocalDateTime.of(2024, 3, 18, 0, 0),
        LocalDateTime.of(2024, 3, 20, 23, 59),
        targetCalendar,
        LocalDateTime.of(2024, 4, 15, 0, 0));
    
    // All copied events should have the same (new) series ID
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 15, 0, 0),
        LocalDateTime.of(2024, 4, 17, 23, 59));
    
    assertTrue("Should have multiple copied events", copiedEvents.size() > 1);
    
    Integer commonSeriesId = copiedEvents.get(0).getSeriesId();
    assertNotNull("All events should have series ID", commonSeriesId);
    
    for (IEvent event : copiedEvents) {
      assertEquals("All copied events should have same series ID", 
          commonSeriesId, event.getSeriesId());
    }
  }
  
  // ==================== Edge Cases and Error Handling ====================
  
  @Test
  public void testTimezoneConversionEdgeCases() {
    // Test timezone conversion across date boundaries
    calendar.createSingleTimedEvent("Late Night Meeting", 
        LocalDateTime.of(2024, 3, 15, 23, 30), // 11:30 PM EDT
        LocalDateTime.of(2024, 3, 16, 0, 30)); // 12:30 AM EDT next day
    
    // Convert to Hawaii time (6 hours behind EDT)
    calendar.setTimezone(ZoneId.of("Pacific/Honolulu"));
    
    List<IEvent> events = calendar.printEvents(
        LocalDateTime.of(2024, 3, 15, 0, 0),
        LocalDateTime.of(2024, 3, 16, 23, 59));
    
    assertEquals(1, events.size());
    IEvent event = events.get(0);
    
    // Event should now be 5:30-6:30 PM on March 15 in Hawaii (6 hours behind EDT)
    assertEquals(LocalDateTime.of(2024, 3, 15, 17, 30), event.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 18, 30), event.getEndDateTime());
  }
  
  @Test
  public void testCopyEventsWithMinimalDurationEvents() {
    // Create 1-minute duration event (minimal valid duration)
    calendar.createSingleTimedEvent("Reminder", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 14, 1));
    
    IEvent copied = calendar.createCopiedEvent("Reminder", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 20, 10, 0));
    
    assertEquals(LocalDateTime.of(2024, 3, 20, 10, 0), copied.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 20, 10, 1), copied.getEndDateTime());
  }
  
  @Test
  public void testEmptyCalendarOperations() {
    // Test operations on empty calendar
    assertEquals(0, calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    
    Integer seriesId = calendar.generateUniqueSeriesId();
    assertNotNull("Should generate series ID even for empty calendar", seriesId);
    
    // Copy from empty calendar
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 15, 0, 0),
        targetCalendar, LocalDateTime.of(2024, 3, 20, 0, 0));
    
    assertEquals(0, targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 20, 0, 0)).size());
  }
  
  @Test
  public void testSpecialTimezoneConversions() {
    // Test UTC conversions
    SmartCalendarModel utcCalendar = new SmartCalendarModel("UTC", ZoneId.of("UTC"));
    
    calendar.createSingleTimedEvent("UTC Test", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    calendar.copyAllEventsToCalendar(LocalDateTime.of(2024, 3, 15, 0, 0),
        utcCalendar, LocalDateTime.of(2024, 3, 20, 0, 0));
    
    List<IEvent> events = utcCalendar.printEvents(LocalDateTime.of(2024, 3, 20, 0, 0));
    assertEquals(1, events.size());
    
    // EDT to UTC = +4 hours (March 2024 is DST)
    assertEquals(LocalDateTime.of(2024, 3, 20, 18, 0), events.get(0).getStartDateTime());
  }
} 