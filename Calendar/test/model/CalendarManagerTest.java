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

import calendar.model.CalendarManager;
import calendar.model.ISmartCalendarModel;
import calendar.model.IEvent;

/**
 * Comprehensive test suite for CalendarManager.
 * Tests all public methods, edge cases, error conditions, and integration scenarios.
 */
public class CalendarManagerTest {
  
  private CalendarManager manager;
  
  @Before
  public void setUp() {
    manager = new CalendarManager();
  }
  
  // ==================== Constructor Tests ====================
  
  @Test
  public void testConstructor() {
    CalendarManager newManager = new CalendarManager();
    assertNull("New manager should have no current calendar", 
        newManager.getCurrentCalendar());
  }
  
  // ==================== Calendar Creation Tests ====================
  
  @Test
  public void testCreateCalendarBasic() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    // Verify calendar was created by trying to use it
    manager.useCalendar("TestCalendar");
    ISmartCalendarModel current = manager.getCurrentCalendar();
    assertNotNull("Calendar should be created and usable", current);
    assertEquals("TestCalendar", current.getCalendarName());
    assertEquals(ZoneId.of("America/New_York"), current.getTimezone());
  }
  
  @Test
  public void testCreateMultipleCalendars() {
    manager.createCalendar("Calendar1", ZoneId.of("America/New_York"));
    manager.createCalendar("Calendar2", ZoneId.of("America/Los_Angeles"));
    manager.createCalendar("Calendar3", ZoneId.of("Europe/London"));
    
    // Test switching between them
    manager.useCalendar("Calendar1");
    assertEquals("Calendar1", manager.getCurrentCalendar().getCalendarName());
    
    manager.useCalendar("Calendar2");
    assertEquals("Calendar2", manager.getCurrentCalendar().getCalendarName());
    
    manager.useCalendar("Calendar3");
    assertEquals("Calendar3", manager.getCurrentCalendar().getCalendarName());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarDuplicateName() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.createCalendar("TestCalendar", ZoneId.of("America/Los_Angeles"));
  }
  
  @Test
  public void testCreateCalendarWithDifferentTimezones() {
    manager.createCalendar("EST", ZoneId.of("America/New_York"));
    manager.createCalendar("PST", ZoneId.of("America/Los_Angeles"));
    manager.createCalendar("UTC", ZoneId.of("UTC"));
    manager.createCalendar("Tokyo", ZoneId.of("Asia/Tokyo"));
    
    manager.useCalendar("EST");
    assertEquals(ZoneId.of("America/New_York"), 
        manager.getCurrentCalendar().getTimezone());
    
    manager.useCalendar("Tokyo");
    assertEquals(ZoneId.of("Asia/Tokyo"), 
        manager.getCurrentCalendar().getTimezone());
  }
  
  @Test
  public void testCreateCalendarWithSpecialCharacters() {
    manager.createCalendar("Work-Calendar", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal_Calendar", ZoneId.of("America/New_York"));
    manager.createCalendar("Calendar 2024", ZoneId.of("America/New_York"));
    
    manager.useCalendar("Work-Calendar");
    assertEquals("Work-Calendar", manager.getCurrentCalendar().getCalendarName());
  }
  
  // ==================== Calendar Usage Tests ====================
  
  @Test
  public void testUseCalendarBasic() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    assertNull("Should start with no current calendar", manager.getCurrentCalendar());
    
    manager.useCalendar("TestCalendar");
    assertNotNull("Should have current calendar after use", 
        manager.getCurrentCalendar());
    assertEquals("TestCalendar", manager.getCurrentCalendar().getCalendarName());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testUseNonExistentCalendar() {
    manager.useCalendar("NonExistentCalendar");
  }
  
  @Test
  public void testSwitchBetweenCalendars() {
    manager.createCalendar("Calendar1", ZoneId.of("America/New_York"));
    manager.createCalendar("Calendar2", ZoneId.of("America/Los_Angeles"));
    
    manager.useCalendar("Calendar1");
    assertEquals("Calendar1", manager.getCurrentCalendar().getCalendarName());
    
    manager.useCalendar("Calendar2");
    assertEquals("Calendar2", manager.getCurrentCalendar().getCalendarName());
    
    // Switch back
    manager.useCalendar("Calendar1");
    assertEquals("Calendar1", manager.getCurrentCalendar().getCalendarName());
  }
  
  // ==================== Calendar Editing Tests ====================
  
  @Test
  public void testEditCalendarName() {
    manager.createCalendar("OldName", ZoneId.of("America/New_York"));
    manager.useCalendar("OldName");
    
    manager.editCalendar("OldName", "name", "NewName");
    
    // Old name should no longer work
    try {
      manager.useCalendar("OldName");
      fail("Should not be able to use old calendar name");
    } catch (IllegalArgumentException e) {
      // Expected
    }
    
    // New name should work
    manager.useCalendar("NewName");
    assertEquals("NewName", manager.getCurrentCalendar().getCalendarName());
  }
  
  @Test
  public void testEditCalendarTimezone() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.useCalendar("TestCalendar");
    
    // Create an event to test timezone conversion
    ISmartCalendarModel calendar = manager.getCurrentCalendar();
    calendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0), // 2 PM EST
        LocalDateTime.of(2024, 3, 15, 15, 0)); // 3 PM EST
    
    // Change timezone
    manager.editCalendar("TestCalendar", "timezone", "America/Los_Angeles");
    
    // Verify timezone changed and events were converted
    assertEquals(ZoneId.of("America/Los_Angeles"), 
        manager.getCurrentCalendar().getTimezone());
    
    // Event times should be converted (EST to PST = 3 hours earlier)
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(1, events.size());
    IEvent event = events.get(0);
    assertEquals(LocalDateTime.of(2024, 3, 15, 11, 0), event.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 12, 0), event.getEndDateTime());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditNonExistentCalendar() {
    manager.editCalendar("NonExistent", "name", "NewName");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNameToExistingName() {
    manager.createCalendar("Calendar1", ZoneId.of("America/New_York"));
    manager.createCalendar("Calendar2", ZoneId.of("America/Los_Angeles"));
    
    manager.editCalendar("Calendar1", "name", "Calendar2");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarInvalidProperty() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.editCalendar("TestCalendar", "invalid", "value");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarInvalidTimezone() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.editCalendar("TestCalendar", "timezone", "Invalid/Timezone");
  }
  
  @Test
  public void testEditCalendarPropertyCaseSensitive() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    
    // Test that property names are case sensitive
    try {
      manager.editCalendar("TestCalendar", "NAME", "NewName");
      fail("Should reject uppercase property name");
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }
  
  // ==================== Event Copying Tests ====================
  
  @Test
  public void testCopyEventBasic() {
    // Create source calendar
    manager.createCalendar("SourceCalendar", ZoneId.of("America/New_York"));
    manager.createCalendar("TargetCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Use source calendar and create an event
    manager.useCalendar("SourceCalendar");
    ISmartCalendarModel sourceCalendar = manager.getCurrentCalendar();
    sourceCalendar.createSingleTimedEvent("Team Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0), // 2 PM EST
        LocalDateTime.of(2024, 3, 15, 15, 0)); // 3 PM EST
    
    // Copy the event
    manager.copyEvent("Team Meeting", LocalDateTime.of(2024, 3, 15, 14, 0),
        "TargetCalendar", LocalDateTime.of(2024, 3, 16, 10, 0));
    
    // Verify event was copied to target calendar
    manager.useCalendar("TargetCalendar");
    ISmartCalendarModel targetCalendar = manager.getCurrentCalendar();
    List<IEvent> events = targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 16, 0, 0));
    
    assertEquals(1, events.size());
    IEvent copiedEvent = events.get(0);
    assertEquals("Team Meeting", copiedEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 16, 10, 0), copiedEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 16, 11, 0), copiedEvent.getEndDateTime());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventNoCurrentCalendar() {
    manager.createCalendar("TargetCalendar", ZoneId.of("America/New_York"));
    // Don't set a current calendar
    manager.copyEvent("Event", LocalDateTime.of(2024, 3, 15, 14, 0),
        "TargetCalendar", LocalDateTime.of(2024, 3, 16, 10, 0));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventToNonExistentCalendar() {
    manager.createCalendar("SourceCalendar", ZoneId.of("America/New_York"));
    manager.useCalendar("SourceCalendar");
    
    manager.copyEvent("Event", LocalDateTime.of(2024, 3, 15, 14, 0),
        "NonExistentCalendar", LocalDateTime.of(2024, 3, 16, 10, 0));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCopyNonExistentEvent() {
    manager.createCalendar("SourceCalendar", ZoneId.of("America/New_York"));
    manager.createCalendar("TargetCalendar", ZoneId.of("America/Los_Angeles"));
    manager.useCalendar("SourceCalendar");
    
    manager.copyEvent("NonExistentEvent", LocalDateTime.of(2024, 3, 15, 14, 0),
        "TargetCalendar", LocalDateTime.of(2024, 3, 16, 10, 0));
  }
  
  @Test
  public void testCopyEventsOnDate() {
    // Create calendars
    manager.createCalendar("SourceCalendar", ZoneId.of("America/New_York"));
    manager.createCalendar("TargetCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Create multiple events on source calendar
    manager.useCalendar("SourceCalendar");
    ISmartCalendarModel sourceCalendar = manager.getCurrentCalendar();
    
    sourceCalendar.createSingleTimedEvent("Morning Meeting", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 15, 10, 0));
    
    sourceCalendar.createSingleTimedEvent("Lunch", 
        LocalDateTime.of(2024, 3, 15, 12, 0),
        LocalDateTime.of(2024, 3, 15, 13, 0));
    
    sourceCalendar.createSingleTimedEvent("Afternoon Meeting", 
        LocalDateTime.of(2024, 3, 15, 15, 0),
        LocalDateTime.of(2024, 3, 15, 16, 0));
    
    // Copy all events from March 15 to March 20
    manager.copyEventsOnDate(LocalDateTime.of(2024, 3, 15, 0, 0),
        "TargetCalendar", LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Verify events were copied with timezone conversion
    manager.useCalendar("TargetCalendar");
    ISmartCalendarModel targetCalendar = manager.getCurrentCalendar();
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 3, 20, 0, 0));
    
    assertEquals(3, copiedEvents.size());
    
    // Verify timezone conversion (EST to PST = 3 hours earlier)
    boolean foundMorning = false, foundLunch = false, foundAfternoon = false;
    
    for (IEvent event : copiedEvents) {
      if (event.getSubject().equals("Morning Meeting")) {
        assertEquals(LocalDateTime.of(2024, 3, 20, 6, 0), event.getStartDateTime());
        foundMorning = true;
      } else if (event.getSubject().equals("Lunch")) {
        assertEquals(LocalDateTime.of(2024, 3, 20, 9, 0), event.getStartDateTime());
        foundLunch = true;
      } else if (event.getSubject().equals("Afternoon Meeting")) {
        assertEquals(LocalDateTime.of(2024, 3, 20, 12, 0), event.getStartDateTime());
        foundAfternoon = true;
      }
    }
    
    assertTrue("Should find all copied events", foundMorning && foundLunch && foundAfternoon);
  }
  
  @Test
  public void testCopyEventsBetweenDates() {
    // Create calendars
    manager.createCalendar("SourceCalendar", ZoneId.of("America/New_York"));
    manager.createCalendar("TargetCalendar", ZoneId.of("Europe/London"));
    
    // Create events across multiple days
    manager.useCalendar("SourceCalendar");
    ISmartCalendarModel sourceCalendar = manager.getCurrentCalendar();
    
    // Events on March 10
    sourceCalendar.createSingleTimedEvent("Day 1 Event", 
        LocalDateTime.of(2024, 3, 10, 14, 0),
        LocalDateTime.of(2024, 3, 10, 15, 0));
    
    // Events on March 11
    sourceCalendar.createSingleTimedEvent("Day 2 Event", 
        LocalDateTime.of(2024, 3, 11, 14, 0),
        LocalDateTime.of(2024, 3, 11, 15, 0));
    
    // Events on March 12
    sourceCalendar.createSingleTimedEvent("Day 3 Event", 
        LocalDateTime.of(2024, 3, 12, 14, 0),
        LocalDateTime.of(2024, 3, 12, 15, 0));
    
    // Events on March 13 (outside range we'll copy)
    sourceCalendar.createSingleTimedEvent("Day 4 Event", 
        LocalDateTime.of(2024, 3, 13, 14, 0),
        LocalDateTime.of(2024, 3, 13, 15, 0));
    
    // Copy events from March 10-12 to April 20-22
    manager.copyEventsBetweenDates(
        LocalDateTime.of(2024, 3, 10, 0, 0),
        LocalDateTime.of(2024, 3, 12, 23, 59),
        "TargetCalendar",
        LocalDateTime.of(2024, 4, 20, 0, 0));
    
    // Verify events were copied to target calendar
    manager.useCalendar("TargetCalendar");
    ISmartCalendarModel targetCalendar = manager.getCurrentCalendar();
    
    List<IEvent> copiedEvents = targetCalendar.printEvents(
        LocalDateTime.of(2024, 4, 20, 0, 0),
        LocalDateTime.of(2024, 4, 22, 23, 59));
    
    assertEquals(3, copiedEvents.size()); // Should exclude Day 4 Event
    
    // Verify relative dates are preserved and timezone conversion applied
    // EDT to GMT = 4 hours later (March 2024 is DST)
    for (IEvent event : copiedEvents) {
      assertEquals("Event should be in April", 4, event.getStartDateTime().getMonth().getValue());
      assertEquals("Should be converted to GMT (EDT + 4 hours)", 
          18, event.getStartDateTime().getHour()); // 2 PM + 4 = 6 PM
    }
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateNoCurrentCalendar() {
    manager.createCalendar("TargetCalendar", ZoneId.of("America/New_York"));
    manager.copyEventsOnDate(LocalDateTime.of(2024, 3, 15, 0, 0),
        "TargetCalendar", LocalDateTime.of(2024, 3, 20, 0, 0));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenDatesNoCurrentCalendar() {
    manager.createCalendar("TargetCalendar", ZoneId.of("America/New_York"));
    manager.copyEventsBetweenDates(
        LocalDateTime.of(2024, 3, 10, 0, 0),
        LocalDateTime.of(2024, 3, 12, 23, 59),
        "TargetCalendar",
        LocalDateTime.of(2024, 4, 20, 0, 0));
  }
  
  // ==================== Integration Tests ====================
  
  @Test
  public void testComplexMultiCalendarScenario() {
    // Create multiple calendars in different timezones
    manager.createCalendar("Personal", ZoneId.of("America/New_York"));
    manager.createCalendar("Work", ZoneId.of("America/Los_Angeles"));
    manager.createCalendar("Travel", ZoneId.of("Europe/London"));
    
    // Add events to personal calendar
    manager.useCalendar("Personal");
    ISmartCalendarModel personal = manager.getCurrentCalendar();
    personal.createSingleTimedEvent("Doctor Appointment", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 15, 10, 0));
    
    // Add events to work calendar  
    manager.useCalendar("Work");
    ISmartCalendarModel work = manager.getCurrentCalendar();
    work.createSingleTimedEvent("Team Standup", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        LocalDateTime.of(2024, 3, 15, 9, 30));
    
    // Copy personal event to work calendar
    manager.useCalendar("Personal");
    manager.copyEvent("Doctor Appointment", 
        LocalDateTime.of(2024, 3, 15, 9, 0),
        "Work", LocalDateTime.of(2024, 3, 15, 14, 0));
    
    // Verify both calendars have their expected events
    manager.useCalendar("Work");
    List<IEvent> workEvents = work.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(2, workEvents.size());
    
    // Verify timezone conversion happened
    boolean foundOriginal = false, foundCopied = false;
    for (IEvent event : workEvents) {
      if (event.getSubject().equals("Team Standup")) {
        assertEquals(LocalDateTime.of(2024, 3, 15, 9, 0), event.getStartDateTime());
        foundOriginal = true;
      } else if (event.getSubject().equals("Doctor Appointment")) {
        assertEquals(LocalDateTime.of(2024, 3, 15, 14, 0), event.getStartDateTime());
        foundCopied = true;
      }
    }
    assertTrue("Should find both events", foundOriginal && foundCopied);
  }
  
  @Test
  public void testCalendarRenameWithActiveCalendar() {
    manager.createCalendar("OldName", ZoneId.of("America/New_York"));
    manager.useCalendar("OldName");
    
    // Add an event to the calendar
    ISmartCalendarModel calendar = manager.getCurrentCalendar();
    calendar.createSingleTimedEvent("Test Event", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    // Rename the calendar
    manager.editCalendar("OldName", "name", "NewName");
    
    // Current calendar should still work
    assertNotNull("Current calendar should still be accessible", 
        manager.getCurrentCalendar());
    assertEquals("NewName", manager.getCurrentCalendar().getCalendarName());
    
    // Event should still be there
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(1, events.size());
    assertEquals("Test Event", events.get(0).getSubject());
  }
  
  @Test
  public void testTimezoneChangeWithRecurringEvents() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.useCalendar("TestCalendar");
    
    ISmartCalendarModel calendar = manager.getCurrentCalendar();
    
    // Create recurring events
    ArrayList<DayOfWeek> weekdays = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
    
    calendar.createRecurringTimedEvent("Daily Standup", 
        LocalDateTime.of(2024, 3, 18, 9, 0), // Monday 9 AM EDT
        LocalDateTime.of(2024, 3, 18, 9, 30), // Monday 9:30 AM EDT
        weekdays, 3);
    
    // Change timezone
    manager.editCalendar("TestCalendar", "timezone", "America/Los_Angeles");
    
    // Verify all events were converted
    List<IEvent> events = calendar.printEvents(
        LocalDateTime.of(2024, 3, 18, 0, 0),
        LocalDateTime.of(2024, 3, 25, 0, 0));
    
    assertEquals(3, events.size());
    for (IEvent event : events) {
      // All events should be converted to PDT (3 hours earlier)
      assertEquals(6, event.getStartDateTime().getHour()); // 9 AM EDT = 6 AM PDT
      assertEquals(0, event.getStartDateTime().getMinute());
      assertEquals(6, event.getEndDateTime().getHour());
      assertEquals(30, event.getEndDateTime().getMinute()); // 9:30 AM EDT = 6:30 AM PDT
    }
  }
  
  // ==================== Edge Cases and Error Handling ====================
  
  @Test
  public void testEmptyCalendarOperations() {
    manager.createCalendar("EmptyCalendar", ZoneId.of("America/New_York"));
    manager.useCalendar("EmptyCalendar");
    
    // Operations on empty calendar should work
    ISmartCalendarModel calendar = manager.getCurrentCalendar();
    
    List<IEvent> events = calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0));
    assertEquals(0, events.size());
    
    assertFalse("Empty calendar should show not busy", 
        calendar.showStatus(LocalDateTime.of(2024, 3, 15, 14, 0)));
  }
  
  @Test
  public void testCopyEventsFromEmptyDate() {
    manager.createCalendar("SourceCalendar", ZoneId.of("America/New_York"));
    manager.createCalendar("TargetCalendar", ZoneId.of("America/Los_Angeles"));
    
    manager.useCalendar("SourceCalendar");
    
    // Copy from a date with no events
    manager.copyEventsOnDate(LocalDateTime.of(2024, 3, 15, 0, 0),
        "TargetCalendar", LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Target calendar should still be empty
    manager.useCalendar("TargetCalendar");
    List<IEvent> events = manager.getCurrentCalendar().printEvents(
        LocalDateTime.of(2024, 3, 20, 0, 0));
    assertEquals(0, events.size());
  }
  
  @Test
  public void testCopyToSameCalendar() {
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.useCalendar("TestCalendar");
    
    ISmartCalendarModel calendar = manager.getCurrentCalendar();
    calendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0),
        LocalDateTime.of(2024, 3, 15, 15, 0));
    
    // Copy event to same calendar at different time
    manager.copyEvent("Meeting", LocalDateTime.of(2024, 3, 15, 14, 0),
        "TestCalendar", LocalDateTime.of(2024, 3, 16, 10, 0));
    
    // Should have two events now
    List<IEvent> allEvents = calendar.printEvents(
        LocalDateTime.of(2024, 3, 15, 0, 0),
        LocalDateTime.of(2024, 3, 17, 0, 0));
    assertEquals(2, allEvents.size());
  }
  
  @Test
  public void testVeryLongCalendarNames() {
    StringBuilder longName = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longName.append("A");
    }
    
    manager.createCalendar(longName.toString(), ZoneId.of("America/New_York"));
    manager.useCalendar(longName.toString());
    
    assertEquals(longName.toString(), 
        manager.getCurrentCalendar().getCalendarName());
  }
  
  @Test
  public void testSpecialTimezones() {
    // Test various special timezone cases
    manager.createCalendar("UTC", ZoneId.of("UTC"));
    manager.createCalendar("GMT", ZoneId.of("GMT"));
    manager.createCalendar("SystemDefault", ZoneId.systemDefault());
    
    manager.useCalendar("UTC");
    assertEquals(ZoneId.of("UTC"), manager.getCurrentCalendar().getTimezone());
    
    manager.useCalendar("GMT");
    assertEquals(ZoneId.of("GMT"), manager.getCurrentCalendar().getTimezone());
  }
}
