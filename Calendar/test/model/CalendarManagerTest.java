package calendar.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Tests for the CalendarManager class.
 */
public class CalendarManagerTest {
  
  private CalendarManager manager;
  
  @Before
  public void setUp() {
    manager = new CalendarManager();
  }
  
  @Test
  public void testCreateCalendarSuccess() {
    ICalendarModel calendar = manager.createCalendar("Work", ZoneId.of("America/New_York"));
    
    assertNotNull(calendar);
    assertTrue(calendar instanceof SmartCalendarModel);
    
    SmartCalendarModel smartCal = (SmartCalendarModel) calendar;
    assertEquals("Work", smartCal.getCalendarName());
    assertEquals(ZoneId.of("America/New_York"), smartCal.getTimezone());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarDuplicateName() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Work", ZoneId.of("Europe/London"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarNullName() {
    manager.createCalendar(null, ZoneId.of("America/New_York"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarEmptyName() {
    manager.createCalendar("", ZoneId.of("America/New_York"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarNullTimezone() {
    manager.createCalendar("Work", null);
  }
  
  @Test
  public void testUseCalendarSuccess() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");
    
    assertEquals("Work", manager.getCurrentCalendar().getCalendarName());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testUseCalendarNotExists() {
    manager.useCalendar("NonExistent");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testUseCalendarNull() {
    manager.useCalendar(null);
  }
  
  @Test(expected = IllegalStateException.class)
  public void testCopyEventNoCurrentCalendar() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.copyEvent("Meeting", LocalDateTime.of(2024, 3, 15, 10, 0), 
                     "Work", LocalDateTime.of(2024, 3, 16, 10, 0));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventTargetCalendarNotExists() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.useCalendar("Work");
    manager.copyEvent("Meeting", LocalDateTime.of(2024, 3, 15, 10, 0), 
                     "NonExistent", LocalDateTime.of(2024, 3, 16, 10, 0));
  }
  
  @Test
  public void testCopyEventSuccess() {
    // Create two calendars
    ISmartCalendarModel workCal = (ISmartCalendarModel) manager.createCalendar("Work", ZoneId.of("America/New_York"));
    ISmartCalendarModel personalCal = (ISmartCalendarModel) manager.createCalendar("Personal", ZoneId.of("America/New_York"));
    
    // Set work calendar as current
    manager.useCalendar("Work");
    
    // Add an event to work calendar
    workCal.createSingleTimedEvent("Team Meeting", 
                                  LocalDateTime.of(2024, 3, 15, 10, 0),
                                  LocalDateTime.of(2024, 3, 15, 11, 0));
    
    // Copy the event to personal calendar
    manager.copyEvent("Team Meeting", LocalDateTime.of(2024, 3, 15, 10, 0), 
                     "Personal", LocalDateTime.of(2024, 3, 16, 14, 0));
    
    // Verify the event exists in personal calendar
    assertEquals(1, personalCal.printEvents(LocalDateTime.of(2024, 3, 16, 0, 0)).size());
    IEvent copiedEvent = personalCal.printEvents(LocalDateTime.of(2024, 3, 16, 0, 0)).get(0);
    assertEquals("Team Meeting", copiedEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 16, 14, 0), copiedEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 16, 15, 0), copiedEvent.getEndDateTime());
  }
  
  @Test
  public void testCopyEventsOnDateSuccess() {
    // Create two calendars
    ISmartCalendarModel workCal = (ISmartCalendarModel) manager.createCalendar("Work", ZoneId.of("America/New_York"));
    ISmartCalendarModel personalCal = (ISmartCalendarModel) manager.createCalendar("Personal", ZoneId.of("America/New_York"));
    
    // Set work calendar as current
    manager.useCalendar("Work");
    
    // Add multiple events to work calendar on the same date
    workCal.createSingleTimedEvent("Morning Meeting", 
                                  LocalDateTime.of(2024, 3, 15, 9, 0),
                                  LocalDateTime.of(2024, 3, 15, 10, 0));
    workCal.createSingleTimedEvent("Afternoon Meeting", 
                                  LocalDateTime.of(2024, 3, 15, 14, 0),
                                  LocalDateTime.of(2024, 3, 15, 15, 0));
    
    // Copy all events from March 15 to March 20
    manager.copyEvents(LocalDateTime.of(2024, 3, 15, 0, 0), 
                      "Personal", LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Verify events exist in personal calendar
    assertEquals(2, personalCal.printEvents(LocalDateTime.of(2024, 3, 20, 0, 0)).size());
  }
  
  @Test
  public void testCopyEventsInRangeSuccess() {
    // Create two calendars
    ISmartCalendarModel workCal = (ISmartCalendarModel) manager.createCalendar("Work", ZoneId.of("America/New_York"));
    ISmartCalendarModel personalCal = (ISmartCalendarModel) manager.createCalendar("Personal", ZoneId.of("America/New_York"));
    
    // Set work calendar as current
    manager.useCalendar("Work");
    
    // Add events to work calendar across multiple dates
    workCal.createSingleTimedEvent("Event 1", 
                                  LocalDateTime.of(2024, 3, 15, 10, 0),
                                  LocalDateTime.of(2024, 3, 15, 11, 0));
    workCal.createSingleTimedEvent("Event 2", 
                                  LocalDateTime.of(2024, 3, 16, 10, 0),
                                  LocalDateTime.of(2024, 3, 16, 11, 0));
    workCal.createSingleTimedEvent("Event 3", 
                                  LocalDateTime.of(2024, 3, 17, 10, 0),
                                  LocalDateTime.of(2024, 3, 17, 11, 0));
    
    // Copy events from March 15-16 to start on March 20
    manager.copyEvents(LocalDateTime.of(2024, 3, 15, 0, 0), 
                      LocalDateTime.of(2024, 3, 16, 23, 59),
                      "Personal", LocalDateTime.of(2024, 3, 20, 0, 0));
    
    // Verify correct events were copied with proper offset
    assertEquals(1, personalCal.printEvents(LocalDateTime.of(2024, 3, 20, 0, 0)).size());
    assertEquals(1, personalCal.printEvents(LocalDateTime.of(2024, 3, 21, 0, 0)).size());
    assertEquals(0, personalCal.printEvents(LocalDateTime.of(2024, 3, 22, 0, 0)).size());
    
    IEvent event1 = personalCal.printEvents(LocalDateTime.of(2024, 3, 20, 0, 0)).get(0);
    assertEquals("Event 1", event1.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 20, 10, 0), event1.getStartDateTime());
    
    IEvent event2 = personalCal.printEvents(LocalDateTime.of(2024, 3, 21, 0, 0)).get(0);
    assertEquals("Event 2", event2.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 21, 10, 0), event2.getStartDateTime());
  }
  
  @Test
  public void testGetAllCalendars() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    manager.createCalendar("Personal", ZoneId.of("Europe/London"));
    
    Map<String, ISmartCalendarModel> calendars = manager.getAllCalendars();
    
    assertEquals(2, calendars.size());
    assertTrue(calendars.containsKey("Work"));
    assertTrue(calendars.containsKey("Personal"));
  }
  
  @Test
  public void testGetCalendar() {
    manager.createCalendar("Work", ZoneId.of("America/New_York"));
    
    ISmartCalendarModel calendar = manager.getCalendar("Work");
    assertNotNull(calendar);
    assertEquals("Work", calendar.getCalendarName());
    
    assertNull(manager.getCalendar("NonExistent"));
  }
  
  @Test
  public void testTimezoneConversion() {
    // Create calendars in different timezones
    ISmartCalendarModel eastCal = (ISmartCalendarModel) manager.createCalendar("East", ZoneId.of("America/New_York"));
    ISmartCalendarModel westCal = (ISmartCalendarModel) manager.createCalendar("West", ZoneId.of("America/Los_Angeles"));
    
    // Set east calendar as current
    manager.useCalendar("East");
    
    // Add an event to east calendar
    eastCal.createSingleTimedEvent("Meeting", 
                                  LocalDateTime.of(2024, 3, 15, 14, 0), // 2 PM EST
                                  LocalDateTime.of(2024, 3, 15, 15, 0)); // 3 PM EST
    
    // Copy the event to west calendar (should convert timezone)
    manager.copyEvent("Meeting", LocalDateTime.of(2024, 3, 15, 14, 0), 
                     "West", LocalDateTime.of(2024, 3, 15, 11, 0)); // 11 AM PST
    
    // Verify the event exists in west calendar with converted time
    assertEquals(1, westCal.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    IEvent copiedEvent = westCal.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).get(0);
    assertEquals("Meeting", copiedEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 15, 11, 0), copiedEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 12, 0), copiedEvent.getEndDateTime());
  }
} 