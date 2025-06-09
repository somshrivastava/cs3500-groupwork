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

public class CalendarManagerTest {
  
  private CalendarManager manager;
  
  @Before
  public void setUp() {
    manager = new CalendarManager();
  }
  
  @Test
  public void testTimezoneConversionFromPSTToEST() {
    // Create a calendar in PST
    manager.createCalendar("TestCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Use the calendar
    manager.useCalendar("TestCalendar");
    
    // Get the current calendar and create an event
    ISmartCalendarModel currentCalendar = manager.getCurrentCalendar();
    
    // Create an event at 2 PM PST
    currentCalendar.createSingleTimedEvent("Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0), // 2 PM PST
        LocalDateTime.of(2024, 3, 15, 15, 0)); // 3 PM PST
    
    // Verify the event was created with original PST times
    assertEquals(1, currentCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    IEvent originalEvent = currentCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).get(0);
    assertEquals("Meeting", originalEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 15, 14, 0), originalEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 15, 0), originalEvent.getEndDateTime());
    
    // Change calendar timezone from PST to EST
    manager.editCalendar("TestCalendar", "timezone", "America/New_York");
    
    // Verify the event time was converted (2 PM PST/PDT = 5 PM EST/EDT)
    // In March 2024, we're in Daylight Saving Time: PDT (UTC-7) and EDT (UTC-4), so 3-hour difference
    assertEquals(1, currentCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    IEvent convertedEvent = currentCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).get(0);
    assertEquals("Meeting", convertedEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 15, 17, 0), convertedEvent.getStartDateTime()); // 5 PM EDT
    assertEquals(LocalDateTime.of(2024, 3, 15, 18, 0), convertedEvent.getEndDateTime());   // 6 PM EDT
  }
  
  @Test
  public void testCopyEventWithTimezoneConversion() {
    // Create source calendar in PST
    manager.createCalendar("WestCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Create target calendar in EST
    manager.createCalendar("EastCalendar", ZoneId.of("America/New_York"));
    
    // Use the west calendar and create an event
    manager.useCalendar("WestCalendar");
    ISmartCalendarModel westCalendar = manager.getCurrentCalendar();
    
    // Create an event at 10 AM PST to 11 AM PST
    westCalendar.createSingleTimedEvent("Team Standup", 
        LocalDateTime.of(2024, 3, 15, 10, 0), // 10 AM PST
        LocalDateTime.of(2024, 3, 15, 11, 0)); // 11 AM PST
    
    // Verify the event was created in the west calendar
    assertEquals(1, westCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    IEvent originalEvent = westCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).get(0);
    assertEquals("Team Standup", originalEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 15, 10, 0), originalEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 3, 15, 11, 0), originalEvent.getEndDateTime());
    
    // Copy the event to the east calendar at 2 PM EST
    manager.copyEvent("Team Standup", LocalDateTime.of(2024, 3, 15, 10, 0), 
                     "EastCalendar", LocalDateTime.of(2024, 3, 15, 14, 0));
    
    // Verify the event was copied to the east calendar
    manager.useCalendar("EastCalendar");
    ISmartCalendarModel eastCalendar = manager.getCurrentCalendar();
    
    assertEquals(1, eastCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    IEvent copiedEvent = eastCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).get(0);
    
    // Verify the copied event details
    assertEquals("Team Standup", copiedEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 3, 15, 14, 0), copiedEvent.getStartDateTime()); // 2 PM EST as specified
    assertEquals(LocalDateTime.of(2024, 3, 15, 15, 0), copiedEvent.getEndDateTime());   // 3 PM EST (1 hour duration preserved)
    
    // Verify original event is still in west calendar unchanged
    assertEquals(1, westCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    IEvent stillOriginalEvent = westCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).get(0);
    assertEquals(LocalDateTime.of(2024, 3, 15, 10, 0), stillOriginalEvent.getStartDateTime()); // Still 10 AM PST
    assertEquals(LocalDateTime.of(2024, 3, 15, 11, 0), stillOriginalEvent.getEndDateTime());   // Still 11 AM PST
  }
  
  @Test
  public void testCopyEventsOnDateWithTimezoneConversion() {
    // Create source calendar in EST
    manager.createCalendar("EastCalendar", ZoneId.of("America/New_York"));
    
    // Create target calendar in PST
    manager.createCalendar("WestCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Use the east calendar and create multiple events
    manager.useCalendar("EastCalendar");
    ISmartCalendarModel eastCalendar = manager.getCurrentCalendar();
    
    // Create events on March 15, 2024 in EST
    eastCalendar.createSingleTimedEvent("Morning Meeting", 
        LocalDateTime.of(2024, 3, 15, 9, 0), // 9 AM EST
        LocalDateTime.of(2024, 3, 15, 10, 0)); // 10 AM EST
    
    eastCalendar.createSingleTimedEvent("Lunch Break", 
        LocalDateTime.of(2024, 3, 15, 12, 0), // 12 PM EST
        LocalDateTime.of(2024, 3, 15, 13, 0)); // 1 PM EST
    
    eastCalendar.createSingleTimedEvent("Afternoon Call", 
        LocalDateTime.of(2024, 3, 15, 15, 0), // 3 PM EST
        LocalDateTime.of(2024, 3, 15, 16, 0)); // 4 PM EST
    
    // Verify events were created in east calendar
    assertEquals(3, eastCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    
    // Copy all events from March 15 in east calendar to April 16 in west calendar
    manager.copyEventsOnDate(LocalDateTime.of(2024, 3, 15, 0, 0), 
                            "WestCalendar", LocalDateTime.of(2024, 4, 16, 0, 0));
    
    // Verify events were copied to west calendar with timezone conversion
    manager.useCalendar("WestCalendar");
    ISmartCalendarModel westCalendar = manager.getCurrentCalendar();
    
    assertEquals(3, westCalendar.printEvents(LocalDateTime.of(2024, 4, 16, 0, 0)).size());
    List<IEvent> copiedEvents = westCalendar.printEvents(LocalDateTime.of(2024, 4, 16, 0, 0));
    
    // Verify timezone conversion: EST to PST is 3 hours earlier
    // 9 AM EST = 6 AM PST, 12 PM EST = 9 AM PST, 3 PM EST = 12 PM PST
    IEvent morningMeeting = findEventBySubject(copiedEvents, "Morning Meeting");
    assertEquals(LocalDateTime.of(2024, 4, 16, 6, 0), morningMeeting.getStartDateTime()); // 6 AM PST
    assertEquals(LocalDateTime.of(2024, 4, 16, 7, 0), morningMeeting.getEndDateTime());   // 7 AM PST
    
    IEvent lunchBreak = findEventBySubject(copiedEvents, "Lunch Break");
    assertEquals(LocalDateTime.of(2024, 4, 16, 9, 0), lunchBreak.getStartDateTime()); // 9 AM PST
    assertEquals(LocalDateTime.of(2024, 4, 16, 10, 0), lunchBreak.getEndDateTime());  // 10 AM PST
    
    IEvent afternoonCall = findEventBySubject(copiedEvents, "Afternoon Call");
    assertEquals(LocalDateTime.of(2024, 4, 16, 12, 0), afternoonCall.getStartDateTime()); // 12 PM PST
    assertEquals(LocalDateTime.of(2024, 4, 16, 13, 0), afternoonCall.getEndDateTime());   // 1 PM PST
    
    // Verify original events are still in east calendar unchanged
    assertEquals(3, eastCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
  }
  
  // Helper method to find an event by subject
  private IEvent findEventBySubject(List<IEvent> events, String subject) {
    for (IEvent event : events) {
      if (event.getSubject().equals(subject)) {
        return event;
      }
    }
    throw new AssertionError("Event with subject '" + subject + "' not found");
  }
  
  @Test
  public void testCopyEventsBetweenDatesWithTimezoneConversion() {
    // Create source calendar in PST
    manager.createCalendar("WestCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Create target calendar in EST
    manager.createCalendar("EastCalendar", ZoneId.of("America/New_York"));
    
    // Use the west calendar and create multiple events across several days
    manager.useCalendar("WestCalendar");
    ISmartCalendarModel westCalendar = manager.getCurrentCalendar();
    
    // Create events on March 10, 2024 in PST
    westCalendar.createSingleTimedEvent("Day 1 Meeting", 
        LocalDateTime.of(2024, 3, 10, 14, 0), // 2 PM PST
        LocalDateTime.of(2024, 3, 10, 15, 0)); // 3 PM PST
    
    // Create events on March 11, 2024 in PST
    westCalendar.createSingleTimedEvent("Day 2 Conference", 
        LocalDateTime.of(2024, 3, 11, 10, 0), // 10 AM PST
        LocalDateTime.of(2024, 3, 11, 12, 0)); // 12 PM PST
    
    // Create events on March 12, 2024 in PST
    westCalendar.createSingleTimedEvent("Day 3 Workshop", 
        LocalDateTime.of(2024, 3, 12, 9, 0), // 9 AM PST
        LocalDateTime.of(2024, 3, 12, 11, 0)); // 11 AM PST
    
    // Create events on March 13, 2024 in PST (outside the range we'll copy)
    westCalendar.createSingleTimedEvent("Day 4 Review", 
        LocalDateTime.of(2024, 3, 13, 16, 0), // 4 PM PST
        LocalDateTime.of(2024, 3, 13, 17, 0)); // 5 PM PST
    
    // Verify 4 total events were created in west calendar
    assertEquals(4, westCalendar.printEvents(LocalDateTime.of(2024, 3, 10, 0, 0), 
                                            LocalDateTime.of(2024, 3, 13, 23, 59)).size());
    
    // Copy events from March 10-12 in west calendar to May 20-22 in east calendar
    manager.copyEventsBetweenDates(LocalDateTime.of(2024, 3, 10, 0, 0), 
                                   LocalDateTime.of(2024, 3, 12, 23, 59),
                                   "EastCalendar", 
                                   LocalDateTime.of(2024, 5, 20, 0, 0));
    
    // Verify events were copied to east calendar with timezone conversion
    manager.useCalendar("EastCalendar");
    ISmartCalendarModel eastCalendar = manager.getCurrentCalendar();
    
    // Should have 3 events (March 10-12 copied, but not March 13)
    assertEquals(3, eastCalendar.printEvents(LocalDateTime.of(2024, 5, 20, 0, 0), 
                                            LocalDateTime.of(2024, 5, 22, 23, 59)).size());
    
    // Check events on May 20th (copied from March 10th)
    List<IEvent> may20Events = eastCalendar.printEvents(LocalDateTime.of(2024, 5, 20, 0, 0));
    assertEquals(1, may20Events.size());
    IEvent day1Meeting = may20Events.get(0);
    assertEquals("Day 1 Meeting", day1Meeting.getSubject());
    // 2 PM PST = 5 PM EST
    assertEquals(LocalDateTime.of(2024, 5, 20, 17, 0), day1Meeting.getStartDateTime()); // 5 PM EST
    assertEquals(LocalDateTime.of(2024, 5, 20, 18, 0), day1Meeting.getEndDateTime());   // 6 PM EST
    
    // Check events on May 21st (copied from March 11th)
    List<IEvent> may21Events = eastCalendar.printEvents(LocalDateTime.of(2024, 5, 21, 0, 0));
    assertEquals(1, may21Events.size());
    IEvent day2Conference = may21Events.get(0);
    assertEquals("Day 2 Conference", day2Conference.getSubject());
    // 10 AM PST = 1 PM EST
    assertEquals(LocalDateTime.of(2024, 5, 21, 13, 0), day2Conference.getStartDateTime()); // 1 PM EST
    assertEquals(LocalDateTime.of(2024, 5, 21, 15, 0), day2Conference.getEndDateTime());   // 3 PM EST
    
    // Check events on May 22nd (copied from March 12th)
    List<IEvent> may22Events = eastCalendar.printEvents(LocalDateTime.of(2024, 5, 22, 0, 0));
    assertEquals(1, may22Events.size());
    IEvent day3Workshop = may22Events.get(0);
    assertEquals("Day 3 Workshop", day3Workshop.getSubject());
    // 9 AM PST = 12 PM EST
    assertEquals(LocalDateTime.of(2024, 5, 22, 12, 0), day3Workshop.getStartDateTime()); // 12 PM EST
    assertEquals(LocalDateTime.of(2024, 5, 22, 14, 0), day3Workshop.getEndDateTime());   // 2 PM EST
    
    // Verify that the March 13th event was NOT copied
    assertEquals(0, eastCalendar.printEvents(LocalDateTime.of(2024, 5, 23, 0, 0)).size());
    
    // Verify original events are still in west calendar unchanged
    assertEquals(4, westCalendar.printEvents(LocalDateTime.of(2024, 3, 10, 0, 0), 
                                            LocalDateTime.of(2024, 3, 13, 23, 59)).size());
  }
  
  @Test
  public void testCopyEventWithIdenticalEventAlreadyExists() {
    // Create source calendar in PST
    manager.createCalendar("SourceCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Create target calendar in PST (same timezone to avoid confusion)
    manager.createCalendar("TargetCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Use the source calendar and create an event
    manager.useCalendar("SourceCalendar");
    ISmartCalendarModel sourceCalendar = manager.getCurrentCalendar();
    
    sourceCalendar.createSingleTimedEvent("Team Meeting", 
        LocalDateTime.of(2024, 3, 15, 14, 0), // 2 PM PST
        LocalDateTime.of(2024, 3, 15, 15, 0)); // 3 PM PST
    
    // Switch to target calendar and create an identical event on the same date we'll copy to
    manager.useCalendar("TargetCalendar");
    ISmartCalendarModel targetCalendar = manager.getCurrentCalendar();
    
    targetCalendar.createSingleTimedEvent("Team Meeting", 
        LocalDateTime.of(2024, 4, 16, 14, 0), // 2 PM PST (same time, different date)
        LocalDateTime.of(2024, 4, 16, 15, 0)); // 3 PM PST
    
    // Verify target calendar has 1 event before copying
    assertEquals(1, targetCalendar.printEvents(LocalDateTime.of(2024, 4, 16, 0, 0)).size());
    
    // Switch back to source calendar and copy the event to target calendar
    manager.useCalendar("SourceCalendar");
    manager.copyEventsOnDate(LocalDateTime.of(2024, 3, 15, 0, 0), 
                            "TargetCalendar", LocalDateTime.of(2024, 4, 16, 0, 0));
    
    // Check what happened - how many events are now in the target calendar?
    manager.useCalendar("TargetCalendar");
    List<IEvent> eventsOnTargetDate = targetCalendar.printEvents(LocalDateTime.of(2024, 4, 16, 0, 0));
    
    // This test reveals the system's duplicate handling behavior
    // The system correctly rejects duplicates - only 1 event should remain
    System.out.println("Number of events after copying to existing identical event: " + eventsOnTargetDate.size());
    
    // System has duplicate prevention - identical events are rejected
    assertEquals(1, eventsOnTargetDate.size());
    
    // Verify the single remaining event has the correct content
    IEvent remainingEvent = eventsOnTargetDate.get(0);
    assertEquals("Team Meeting", remainingEvent.getSubject());
    assertEquals(LocalDateTime.of(2024, 4, 16, 14, 0), remainingEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 4, 16, 15, 0), remainingEvent.getEndDateTime());
    
    // Verify original event still exists in source calendar
    manager.useCalendar("SourceCalendar");
    assertEquals(1, sourceCalendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
  }

  @Test
  public void testSeriesIdMappingDuringCopy() {
    // Create source calendar in PST
    manager.createCalendar("SourceCalendar", ZoneId.of("America/Los_Angeles"));
    
    // Create target calendar in EST
    manager.createCalendar("TargetCalendar", ZoneId.of("America/New_York"));
    
    // Use the source calendar and create recurring events with series IDs
    manager.useCalendar("SourceCalendar");
    ISmartCalendarModel sourceCalendar = manager.getCurrentCalendar();
    
    // Create weekday arrays for recurring events
    ArrayList<DayOfWeek> everyDay = new ArrayList<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
    
    // Create a recurring event series (3 events with same series ID)
    sourceCalendar.createRecurringTimedEvent("Daily Standup", 
        LocalDateTime.of(2024, 3, 10, 9, 0), // 9 AM PST
        LocalDateTime.of(2024, 3, 10, 9, 30), // 9:30 AM PST
        everyDay, // Every day of the week
        3); // 3 events total
    
    // Create another recurring event series (2 events with different series ID)
    sourceCalendar.createRecurringTimedEvent("Team Sync", 
        LocalDateTime.of(2024, 3, 11, 14, 0), // 2 PM PST
        LocalDateTime.of(2024, 3, 11, 15, 0), // 3 PM PST  
        everyDay, // Every day of the week
        2); // 2 events total
    
    // Verify we have 5 total events in source (3 + 2)
    List<IEvent> sourceEvents = sourceCalendar.printEvents(LocalDateTime.of(2024, 3, 10, 0, 0), 
                                                           LocalDateTime.of(2024, 3, 12, 23, 59));
    assertEquals(5, sourceEvents.size());
    
    // Collect original series IDs
    Integer originalStandupSeriesId = null;
    Integer originalSyncSeriesId = null;
    
    for (IEvent event : sourceEvents) {
      if (event.getSubject().equals("Daily Standup") && originalStandupSeriesId == null) {
        originalStandupSeriesId = event.getSeriesId();
      } else if (event.getSubject().equals("Team Sync") && originalSyncSeriesId == null) {
        originalSyncSeriesId = event.getSeriesId();
      }
    }
    
    // Verify series IDs are not null and different
    assertNotNull("Daily Standup should have a series ID", originalStandupSeriesId);
    assertNotNull("Team Sync should have a series ID", originalSyncSeriesId);
    assertNotEquals("Series IDs should be different", originalStandupSeriesId, originalSyncSeriesId);
    
    // Verify all Daily Standup events have the same series ID
    for (IEvent event : sourceEvents) {
      if (event.getSubject().equals("Daily Standup")) {
        assertEquals("All Daily Standup events should have same series ID", 
                    originalStandupSeriesId, event.getSeriesId());
      }
    }
    
    // Verify all Team Sync events have the same series ID
    for (IEvent event : sourceEvents) {
      if (event.getSubject().equals("Team Sync")) {
        assertEquals("All Team Sync events should have same series ID", 
                    originalSyncSeriesId, event.getSeriesId());
      }
    }
    
    // Copy events from March 10-12 to target calendar (April 20-22)
    manager.copyEventsBetweenDates(LocalDateTime.of(2024, 3, 10, 0, 0), 
                                   LocalDateTime.of(2024, 3, 12, 23, 59),
                                   "TargetCalendar", 
                                   LocalDateTime.of(2024, 4, 20, 0, 0));
    
    // Switch to target calendar and verify events were copied
    manager.useCalendar("TargetCalendar");
    ISmartCalendarModel targetCalendar = manager.getCurrentCalendar();
    
    List<IEvent> copiedEvents = targetCalendar.printEvents(LocalDateTime.of(2024, 4, 20, 0, 0), 
                                                          LocalDateTime.of(2024, 4, 22, 23, 59));
    assertEquals(5, copiedEvents.size());
    
    // Collect new series IDs from copied events
    Integer newStandupSeriesId = null;
    Integer newSyncSeriesId = null;
    
    for (IEvent event : copiedEvents) {
      if (event.getSubject().equals("Daily Standup") && newStandupSeriesId == null) {
        newStandupSeriesId = event.getSeriesId();
      } else if (event.getSubject().equals("Team Sync") && newSyncSeriesId == null) {
        newSyncSeriesId = event.getSeriesId();
      }
    }
    
    // Verify new series IDs are not null and different from originals
    assertNotNull("Copied Daily Standup should have a series ID", newStandupSeriesId);
    assertNotNull("Copied Team Sync should have a series ID", newSyncSeriesId);
    assertNotEquals("New Daily Standup series ID should differ from original", 
                    originalStandupSeriesId, newStandupSeriesId);
    assertNotEquals("New Team Sync series ID should differ from original", 
                    originalSyncSeriesId, newSyncSeriesId);
    assertNotEquals("New series IDs should be different from each other", 
                    newStandupSeriesId, newSyncSeriesId);
    
    // Verify all copied Daily Standup events have the same NEW series ID
    for (IEvent event : copiedEvents) {
      if (event.getSubject().equals("Daily Standup")) {
        assertEquals("All copied Daily Standup events should have same NEW series ID", 
                    newStandupSeriesId, event.getSeriesId());
      }
    }
    
    // Verify all copied Team Sync events have the same NEW series ID
    for (IEvent event : copiedEvents) {
      if (event.getSubject().equals("Team Sync")) {
        assertEquals("All copied Team Sync events should have same NEW series ID", 
                    newSyncSeriesId, event.getSeriesId());
      }
    }
    
    // Verify timezone conversion worked correctly
    // 9 AM PST = 12 PM EST, 2 PM PST = 5 PM EST
    IEvent copiedStandup = findEventBySubject(copiedEvents, "Daily Standup");
    assertEquals(LocalDateTime.of(2024, 4, 20, 12, 0), copiedStandup.getStartDateTime()); // 12 PM EST
    assertEquals(LocalDateTime.of(2024, 4, 20, 12, 30), copiedStandup.getEndDateTime());  // 12:30 PM EST
    
    IEvent copiedSync = findEventBySubject(copiedEvents, "Team Sync");
    assertEquals(LocalDateTime.of(2024, 4, 21, 17, 0), copiedSync.getStartDateTime()); // 5 PM EST
    assertEquals(LocalDateTime.of(2024, 4, 21, 18, 0), copiedSync.getEndDateTime());   // 6 PM EST
    
    // Verify original events and series IDs are unchanged in source calendar
    manager.useCalendar("SourceCalendar");
    List<IEvent> originalEventsAfterCopy = sourceCalendar.printEvents(LocalDateTime.of(2024, 3, 10, 0, 0), 
                                                                     LocalDateTime.of(2024, 3, 12, 23, 59));
    assertEquals(5, originalEventsAfterCopy.size());
    
    for (IEvent event : originalEventsAfterCopy) {
      if (event.getSubject().equals("Daily Standup")) {
        assertEquals("Original Daily Standup series ID should be unchanged", 
                    originalStandupSeriesId, event.getSeriesId());
      } else if (event.getSubject().equals("Team Sync")) {
        assertEquals("Original Team Sync series ID should be unchanged", 
                    originalSyncSeriesId, event.getSeriesId());
      }
    }
  }
} 