package controller.parser;

import calendar.controller.parser.*;
import controller.MockCalendarManager;
import controller.MockCalendarView;
import controller.MockSmartCalendarModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for SmartCommandParserFactory that tests command routing logic.
 */
public class SmartCommandParserFactoryTest {
  private MockCalendarManager mockManager;
  private MockCalendarView mockView;
  private SmartCommandParserFactory factory;
  private StringBuilder managerLog;
  private StringBuilder viewOutput;

  @Before
  public void setUp() {
    managerLog = new StringBuilder();
    viewOutput = new StringBuilder();
    mockManager = new MockCalendarManager(managerLog);
    mockView = new MockCalendarView(viewOutput);
    factory = new SmartCommandParserFactory(mockManager, mockView);
  }

  // Helper method to clear logs between tests
  private void clearLogs() {
    managerLog.setLength(0);
    viewOutput.setLength(0);
  }

  // Test Calendar Command Behavior
  @Test
  public void testCreateCalendarCommand() {
    String command = "create calendar --name Work --timezone America/New_York";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Created calendar Work with timezone America/New_York", 
        mockManager.getLog());
  }

  @Test
  public void testEditCalendarCommand() {
    String command = "edit calendar --name Work --property timezone Europe/London";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Edited calendar Work property timezone to Europe/London", 
        mockManager.getLog());
  }

  @Test
  public void testUseCalendarCommand() {
    String command = "use calendar --name Work";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Switched to calendar Work", mockManager.getLog());
  }

  @Test
  public void testCopyEventCommand() {
    String command = "copy event Meeting on 2024-03-20T10:00 --target Personal to 2024-03-25T10:00";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Copied event Meeting from 2024-03-20T10:00 to calendar Personal at 2024-03-25T10:00", 
        mockManager.getLog());
  }

  @Test
  public void testCopyEventsOnDateCommand() {
    String command = "copy events on 2024-03-20 --target Personal to 2024-04-15";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Copied events on 2024-03-20T00:00 to calendar Personal starting at 2024-04-15T00:00", 
        mockManager.getLog());
  }

  @Test
  public void testCopyEventsBetweenDatesCommand() {
    String command = "copy events between 2024-03-18 and 2024-03-22 --target Personal to 2024-05-01";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Copied events between 2024-03-18T00:00 and 2024-03-22T00:00 to calendar Personal starting at 2024-05-01T00:00", 
        mockManager.getLog());
  }

  // Test Event Command Routing with Active Calendar
  @Test
  public void testEventCommandWithActiveCalendar() {
    // Set up mock to return an active calendar
    mockManager.setCurrentCalendar(new MockSmartCalendarModel(new StringBuilder()));
    
    String command = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    ICommandParser parser = factory.createParser(command);
    
    // Should delegate to CommandParserFactory and return appropriate parser
    assertNotNull("Should create parser when calendar is active", parser);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventCommandWithoutActiveCalendar() {
    // Ensure no calendar is active
    mockManager.setCurrentCalendar(null);
    
    String command = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    factory.createParser(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintCommandWithoutActiveCalendar() {
    mockManager.setCurrentCalendar(null);
    
    String command = "print events on 2024-03-20";
    factory.createParser(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowStatusWithoutActiveCalendar() {
    mockManager.setCurrentCalendar(null);
    
    String command = "show status on 2024-03-20T10:00";
    factory.createParser(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventWithoutActiveCalendar() {
    mockManager.setCurrentCalendar(null);
    
    String command = "edit event subject Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with NewMeeting";
    factory.createParser(command);
  }

  // Test Case Sensitivity
  @Test
  public void testCreateCalendarCaseInsensitive() {
    String command = "CREATE CALENDAR --name Work --timezone America/New_York";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Created calendar Work with timezone America/New_York", 
        mockManager.getLog());
  }

  @Test
  public void testUseCaseInsensitive() {
    String command = "USE calendar --name Work";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Switched to calendar Work", mockManager.getLog());
  }

  @Test
  public void testCopyCaseInsensitive() {
    String command = "COPY event Meeting on 2024-03-20T10:00 --target Personal to 2024-03-25T10:00";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Copied event Meeting from 2024-03-20T10:00 to calendar Personal at 2024-03-25T10:00", 
        mockManager.getLog());
  }

  // Test Calendar Command Validation
  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarMissingName() {
    String command = "create calendar --timezone America/New_York";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarMissingTimezone() {
    String command = "create calendar --name Work";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarMissingProperty() {
    String command = "edit calendar --name Work";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUseCalendarMissingName() {
    String command = "use calendar";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventMissingTarget() {
    String command = "copy event Meeting on 2024-03-20T10:00 to 2024-03-25T10:00";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  // Test Error Conditions
  @Test(expected = IllegalArgumentException.class)
  public void testNullCommand() {
    factory.createParser(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCommand() {
    factory.createParser("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceOnlyCommand() {
    factory.createParser("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownCommand() {
    factory.createParser("unknown command");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncompleteCommand() {
    factory.createParser("create");
  }

  // Test Command Structure Recognition
  @Test
  public void testCommandStructureWithMultipleWords() {
    mockManager.setCurrentCalendar(new MockSmartCalendarModel(new StringBuilder()));
    
    String command = "create event \"Multi Word Event\" from 2024-03-20T10:00 to 2024-03-20T11:00";
    ICommandParser parser = factory.createParser(command);
    
    assertNotNull("Should handle multi-word subjects in event commands", parser);
  }

  @Test
  public void testCalendarVsEventCommandDistinction() {
    // Calendar command - should not require active calendar
    String calCommand = "create calendar --name Test --timezone UTC";
    ICommandParser calParser = factory.createParser(calCommand);
    calParser.parse(calCommand);
    assertEquals("Calendar commands should work without active calendar", 
        "Created calendar Test with timezone UTC", mockManager.getLog());

    // Event command - should require active calendar
    clearLogs();
    mockManager.setCurrentCalendar(new MockSmartCalendarModel(new StringBuilder()));
    String eventCommand = "create event Test from 2024-03-20T10:00 to 2024-03-20T11:00";
    ICommandParser eventParser = factory.createParser(eventCommand);
    assertNotNull("Event commands should work with active calendar", eventParser);
  }

  // Test error messages
  @Test
  public void testNoActiveCalendarErrorMessage() {
    mockManager.setCurrentCalendar(null);
    
    try {
      factory.createParser("create event Test from 2024-03-20T10:00 to 2024-03-20T11:00");
      fail("Should throw exception for event command without active calendar");
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should mention calendar requirement", 
          e.getMessage().contains("No calendar is currently in use"));
      assertTrue("Error message should suggest use command", 
          e.getMessage().contains("use calendar --name"));
    }
  }

  // Additional Edge Case Tests
  @Test
  public void testEditCalendarNameProperty() {
    String command = "edit calendar --name Work --property name \"New Work Name\"";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Edited calendar Work property name to New Work Name", 
        mockManager.getLog());
  }

  @Test
  public void testCreateCalendarWithQuotedNames() {
    String command = "create calendar --name \"My Work Calendar\" --timezone \"America/New_York\"";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Created calendar My Work Calendar with timezone America/New_York", 
        mockManager.getLog());
  }

  @Test
  public void testCopyEventWithQuotedNames() {
    String command = "copy event \"Team Meeting\" on 2024-03-20T10:00 --target \"Personal Calendar\" to 2024-03-25T10:00";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Copied event Team Meeting from 2024-03-20T10:00 to calendar Personal Calendar at 2024-03-25T10:00", 
        mockManager.getLog());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimezone() {
    String command = "create calendar --name Work --timezone Invalid/Timezone";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarInvalidProperty() {
    String command = "edit calendar --name Work --property invalidprop value";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsInvalidFormat() {
    String command = "copy events invalid format";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsMissingBetweenDate() {
    String command = "copy events between 2024-03-20 --target Personal to 2024-04-15";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test
  public void testCommandWithExtraWhitespace() {
    String command = "   create   calendar   --name   Work   --timezone   UTC   ";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Created calendar Work with timezone UTC", 
        mockManager.getLog());
  }

  @Test
  public void testMixedCaseKeywords() {
    String command = "create CALENDAR --NAME Work --TIMEZONE UTC";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
    
    assertEquals("Created calendar Work with timezone UTC", 
        mockManager.getLog());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithoutCalendarKeyword() {
    String command = "create --name Work --timezone UTC";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditWithoutCalendarKeyword() {
    String command = "edit --name Work --property timezone UTC";
    ICommandParser parser = factory.createParser(command);
    parser.parse(command);
  }
} 