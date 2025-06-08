package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.model.ISmartCalendarModel;
import calendar.model.SmartCalendarModel;

/**
 * Tests for the SmartCalendarModel class.
 */
public class SmartCalendarModelTest {
  
  private SmartCalendarModel calendar;
  
  @Before
  public void setUp() {
    calendar = new SmartCalendarModel("Work", ZoneId.of("America/New_York"));
  }
  
  @Test
  public void testConstructorSuccess() {
    SmartCalendarModel cal = new SmartCalendarModel("Test", ZoneId.of("Europe/London"));
    
    assertEquals("Test", cal.getCalendarName());
    assertEquals(ZoneId.of("Europe/London"), cal.getTimezone());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullName() {
    new SmartCalendarModel(null, ZoneId.of("America/New_York"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorEmptyName() {
    new SmartCalendarModel("", ZoneId.of("America/New_York"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWhitespaceName() {
    new SmartCalendarModel("   ", ZoneId.of("America/New_York"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullTimezone() {
    new SmartCalendarModel("Test", null);
  }
  
  @Test
  public void testEditCalendarNameSuccess() {
    calendar.editCalendar("Work", "name", "Personal");
    
    assertEquals("Personal", calendar.getCalendarName());
  }
  
  @Test
  public void testEditCalendarTimezoneSuccess() {
    calendar.editCalendar("Work", "timezone", "Europe/London");
    
    assertEquals(ZoneId.of("Europe/London"), calendar.getTimezone());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarWrongCalendarName() {
    calendar.editCalendar("WrongName", "name", "Personal");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNullProperty() {
    calendar.editCalendar("Work", null, "Personal");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNullValue() {
    calendar.editCalendar("Work", "name", null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarInvalidProperty() {
    calendar.editCalendar("Work", "invalid", "value");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNameEmptyValue() {
    calendar.editCalendar("Work", "name", "");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarNameWhitespaceValue() {
    calendar.editCalendar("Work", "name", "   ");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarTimezoneInvalidValue() {
    calendar.editCalendar("Work", "timezone", "Invalid/Timezone");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarTimezoneEmptyValue() {
    calendar.editCalendar("Work", "timezone", "");
  }
  
  @Test
  public void testEditCalendarCaseInsensitiveProperty() {
    calendar.editCalendar("Work", "NAME", "Personal");
    assertEquals("Personal", calendar.getCalendarName());
    
    calendar.editCalendar("Personal", "TIMEZONE", "Europe/London");
    assertEquals(ZoneId.of("Europe/London"), calendar.getTimezone());
  }
  
  @Test
  public void testInheritedFunctionality() {
    // Test that SmartCalendarModel still has all CalendarModel functionality
    calendar.createSingleTimedEvent("Meeting", 
                                   LocalDateTime.of(2024, 3, 15, 10, 0),
                                   LocalDateTime.of(2024, 3, 15, 11, 0));
    
    assertEquals(1, calendar.printEvents(LocalDateTime.of(2024, 3, 15, 0, 0)).size());
    assertTrue(calendar.showStatus(LocalDateTime.of(2024, 3, 15, 10, 30)));
  }
  
  @Test
  public void testBuilderSuccess() {
    SmartCalendarModel cal = SmartCalendarModel.getBuilder()
        .calendarName("Test Calendar")
        .timezone(ZoneId.of("Asia/Tokyo"))
        .build();
    
    assertEquals("Test Calendar", cal.getCalendarName());
    assertEquals(ZoneId.of("Asia/Tokyo"), cal.getTimezone());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testBuilderMissingName() {
    SmartCalendarModel.getBuilder()
        .timezone(ZoneId.of("Asia/Tokyo"))
        .build();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testBuilderMissingTimezone() {
    SmartCalendarModel.getBuilder()
        .calendarName("Test Calendar")
        .build();
  }
  
  @Test
  public void testMultipleTimezoneFormats() {
    // Test various valid IANA timezone formats
    String[] validTimezones = {
        "America/New_York",
        "Europe/London", 
        "Asia/Tokyo",
        "Australia/Sydney",
        "Africa/Cairo",
        "America/Los_Angeles",
        "Asia/Kolkata",
        "UTC"
    };
    
    for (String tz : validTimezones) {
      calendar.editCalendar("Work", "timezone", tz);
      assertEquals(ZoneId.of(tz), calendar.getTimezone());
    }
  }
  
  @Test
  public void testCalendarNameTrimming() {
    // Test that calendar names are properly trimmed
    calendar.editCalendar("Work", "name", "  Personal  ");
    assertEquals("Personal", calendar.getCalendarName());
  }
  
  @Test
  public void testTimezoneNameTrimming() {
    // Test that timezone names are properly trimmed
    calendar.editCalendar("Work", "timezone", "  Europe/London  ");
    assertEquals(ZoneId.of("Europe/London"), calendar.getTimezone());
  }
  
  @Test
  public void testPropertyCaseInsensitivity() {
    // Test that property names are case insensitive
    calendar.editCalendar("Work", "nAmE", "Personal");
    assertEquals("Personal", calendar.getCalendarName());
    
    calendar.editCalendar("Personal", "tImEzOnE", "Asia/Tokyo");
    assertEquals(ZoneId.of("Asia/Tokyo"), calendar.getTimezone());
  }
  
  @Test
  public void testISmartCalendarModelInterface() {
    // Test that the class implements the ISmartCalendarModel interface
    assertTrue(calendar instanceof ISmartCalendarModel);
    assertTrue(calendar instanceof ICalendarModel);
  }
  
  @Test
  public void testCalendarNameAfterEdit() {
    String originalName = calendar.getCalendarName();
    assertEquals("Work", originalName);
    
    calendar.editCalendar("Work", "name", "NewName");
    assertEquals("NewName", calendar.getCalendarName());
    
    // Should now need to use the new name for further edits
    calendar.editCalendar("NewName", "timezone", "Asia/Tokyo");
    assertEquals(ZoneId.of("Asia/Tokyo"), calendar.getTimezone());
  }
} 