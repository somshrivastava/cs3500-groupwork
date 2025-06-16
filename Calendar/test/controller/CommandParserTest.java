package controller;

import calendar.controller.parser.ICommandParser;
import calendar.controller.parser.SmartCommandParserFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Comprehensive test suite for CommandParser using manual mock implementations.
 * Tests all command types, valid parsing scenarios, and error conditions.
 * Updated for Assignment 5 to work with SmartCommandParserFactory and calendar context.
 */
public class CommandParserTest {
  private StringBuilder modelLog;
  private StringBuilder managerLog;
  private SmartCommandParserFactory factory;
  private MockCalendarManager mockManager;
  private ICommandParser parser;
  private String input;

  @Before
  public void setUp() {
    modelLog = new StringBuilder();
    managerLog = new StringBuilder();
    StringBuilder viewOutput = new StringBuilder();
    
    MockSmartCalendarModel mockModel = new MockSmartCalendarModel(modelLog);
    mockManager = new MockCalendarManager(managerLog);
    MockCalendarView mockView = new MockCalendarView(viewOutput);
    
    // Set up an active calendar for event commands to work
    mockManager.setCurrentCalendar(mockModel);
    
    factory = new SmartCommandParserFactory(mockManager, mockView);
  }

  @Test
  public void testCreateSingleTimedEvent() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleTimedEventQuoted() {
    input = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Team Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleTimedEventLongSubject() {
    input = "create event \"Very Long Meeting Name With Multiple Words\" " +
            "from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Very Long Meeting Name With " +
            "Multiple Words starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleTimedEventCaseInsensitive() {
    input = "CREATE EVENT Meeting FROM 2024-03-20T10:00 TO 2024-03-20T11:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    input = "create event Holiday on 2024-03-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single all day event Holiday on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleAllDayEventQuoted() {
    input = "create event \"Company Holiday\" on 2024-03-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single all day event Company Holiday on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleAllDayEventCaseInsensitive() {
    input = "create event Holiday ON 2024-03-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single all day event Holiday on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventCount() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 " +
            "repeats MWF for 5 times";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00 for a count of 5";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    input = "create event \"Weekly Standup\" from 2024-03-20T09:00 to " +
            "2024-03-20T09:30 repeats MTWRF until 2024-06-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Weekly Standup starting at " +
            "2024-03-20T09:00 until 2024-03-20T09:30 to the date 2024-06-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventSingleWeekday() {
    input = "create event \"Monday Meeting\" from 2024-03-20T10:00 to " +
            "2024-03-20T11:00 repeats M for 10 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Monday Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00 for a count of 10";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventAllWeekdays() {
    input = "create event Daily from 2024-03-20T08:00 to 2024-03-20T09:00 " +
            "repeats MTWRFSU for 7 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Daily starting at " +
            "2024-03-20T08:00 until 2024-03-20T09:00 for a count of 7";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventWeekends() {
    input = "create event \"Weekend Fun\" from 2024-03-20T14:00 to " +
            "2024-03-20T16:00 repeats SU for 4 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Weekend Fun starting at " +
            "2024-03-20T14:00 until 2024-03-20T16:00 for a count of 4";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventCount() {
    input = "create event Holiday on 2024-03-20 repeats F for 10 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring all day event Holiday starting on " +
            "the date 2024-03-20T00:00 for a count of 10";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    input = "create event \"Weekly Holiday\" on 2024-03-20 repeats W until 2024-12-31";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Weekly Holiday starting on " +
            "the date 2024-03-20T00:00 to the date 2024-12-31T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventSubject() {
    input = "edit event subject Meeting from 2024-03-20T10:00 to " +
            "2024-03-20T11:00 with \"New Meeting\"";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on " +
            "2024-03-20T10:00 until 2024-03-20T11:00. Changed subject to New Meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventStart() {
    input = "edit event start \"Team Meeting\" from 2024-03-20T10:00 to " +
            "2024-03-20T11:00 with 2024-03-20T09:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Team Meeting starting on " +
            "2024-03-20T10:00 until 2024-03-20T11:00. Changed start to 2024-03-20T09:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventEnd() {
    input = "edit event end Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 " +
            "with 2024-03-20T12:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on " +
            "2024-03-20T10:00 until 2024-03-20T11:00. Changed end to 2024-03-20T12:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventDescription() {
    input = "edit event description Meeting from 2024-03-20T10:00 to " +
            "2024-03-20T11:00 with \"Important meeting\"";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on " +
            "2024-03-20T10:00 until 2024-03-20T11:00. Changed description to Important meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventLocation() {
    input = "edit event location Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with ONLINE";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on " +
            "2024-03-20T10:00 until 2024-03-20T11:00. Changed location to ONLINE";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventStatus() {
    input = "edit event status Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with PRIVATE";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on " +
            "2024-03-20T10:00 until 2024-03-20T11:00. Changed status to PRIVATE";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventsFromDate() {
    input = "edit events subject \"Weekly Meeting\" from 2024-03-20T10:00 " +
            "with \"Updated Weekly Meeting\"";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited series of event's properties: Weekly Meeting starting on " +
            "or after date 2024-03-20T10:00. Changed subject to Updated Weekly Meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventsLocation() {
    input = "edit events location Meeting from 2024-03-20T10:00 with PHYSICAL";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited series of event's properties: Meeting starting on " +
            "or after date 2024-03-20T10:00. Changed location to PHYSICAL";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEntireSeries() {
    input = "edit series subject Meeting from 2024-03-20T10:00 with \"Updated Meeting\"";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited all series of event's properties: Meeting with " +
            "start time 2024-03-20T10:00. Changed subject to Updated Meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditSeriesDescription() {
    input = "edit series description \"Team Standup\" from 2024-03-20T09:00 " +
            "with \"Daily team synchronization\"";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited all series of event's properties: Team Standup with " +
            "start time 2024-03-20T09:00. Changed description to Daily team synchronization";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsOnDate() {
    input = "print events on 2024-03-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Queried for all events that occur on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsDateRange() {
    input = "print events from 2024-03-20T00:00 to 2024-03-25T23:59";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Queried for all events that occur from " +
            "2024-03-20T00:00 to 2024-03-25T23:59";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsLongRange() {
    input = "print events from 2024-01-01T00:00 to 2024-12-31T23:59";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Queried for all events that occur from " +
            "2024-01-01T00:00 to 2024-12-31T23:59";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsCaseInsensitive() {
    input = "PRINT EVENTS ON 2024-03-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Queried for all events that occur on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testShowStatus() {
    input = "show status on 2024-03-20T10:30";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testShowStatusDifferentTime() {
    input = "show status on 2024-12-25T00:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Checked if there is an event during 2024-12-25T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testShowStatusCaseInsensitive() {
    input = "SHOW STATUS ON 2024-03-20T10:30";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testExtraWhitespace() {
    input = "  create   event   Meeting   from   2024-03-20T10:00   to   2024-03-20T11:00  ";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testTabsInCommand() {
    input = "\tcreate\tevent\tMeeting\tfrom\t2024-03-20T10:00\tto\t2024-03-20T11:00\t";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testLeapYearDate() {
    input = "create event \"Leap Day Event\" on 2024-02-29";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single all day event Leap Day Event on 2024-02-29T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEndOfYearDateTime() {
    input = "show status on 2024-12-31T23:59";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Checked if there is an event during 2024-12-31T23:59";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testMidnightTime() {
    input = "create event \"Midnight Event\" from 2024-03-20T00:00 to 2024-03-20T01:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Midnight Event starting at " +
            "2024-03-20T00:00 until 2024-03-20T01:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testSingleCharacterSubject() {
    input = "create event A on 2024-03-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single all day event A on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testMaxRecurringCount() {
    input = "create event Daily from 2024-03-20T10:00 to 2024-03-20T11:00 repeats M for 999 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Daily starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00 for a count of 999";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCommand() {
    input = null;
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCommand() {
    input = "";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceOnlyCommand() {
    input = "   \t\n  ";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownCommand() {
    input = "delete event Meeting";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMissingEventKeyword() {
    input = "create Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMissingSubject() {
    input = "create event from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidTimedEventKeyword() {
    input = "create event Meeting at 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMissingToKeyword() {
    input = "create event Meeting from 2024-03-20T10:00 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidDateTimeFormat() {
    input = "create event Meeting from 2024/03/20T10:00 to 2024/03/20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidDateFormat() {
    input = "create event Holiday on 03-20-2024";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringMissingRepeats() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 MWF for 5 times";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringInvalidWeekday() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 " +
            "repeats MXF for 5 times";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringInvalidCount() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 " +
            "repeats MWF for -5 times";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringMissingTimesKeyword() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF for 5";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidType() {
    input = "edit meeting subject Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with New";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidProperty() {
    input = "edit event title Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with New";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingFromKeyword() {
    input = "edit event subject Meeting 2024-03-20T10:00 to 2024-03-20T11:00 with New";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingWithKeyword() {
    input = "edit event subject Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 New";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintMissingEventsKeyword() {
    input = "print on 2024-03-20";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintInvalidKeyword() {
    input = "print meetings on 2024-03-20";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintMissingOnKeyword() {
    input = "print events 2024-03-20";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintInvalidDateFormat() {
    input = "print events on 03/20/2024";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowMissingStatusKeyword() {
    input = "show on 2024-03-20T10:30";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowInvalidKeyword() {
    input = "show availability on 2024-03-20T10:30";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowMissingOnKeyword() {
    input = "show status 2024-03-20T10:30";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowInvalidDateTimeFormat() {
    input = "show status on 2024-03-20 10:30";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnclosedQuote() {
    input = "create event \"Team Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
  }

  @Test
  public void testUnknownCommandErrorMessage() {
    try {
      input = "unknown command";
      parser = factory.createParser(input);
      parser.parse(input);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should mention valid commands",
              e.getMessage().contains("Valid commands are: create, edit, print, show"));
      assertTrue("Error message should mention the unknown command",
              e.getMessage().contains("unknown"));
    }
  }

  @Test
  public void testEmptyCommandErrorMessage() {
    try {
      input = "";
      parser = factory.createParser(input);
      parser.parse(input);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should mention empty command",
              e.getMessage().contains("cannot be empty"));
    }
  }

  @Test
  public void testInvalidWeekdayErrorMessage() {
    try {
      input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 " +
              "repeats MXF for 5 times";
      parser = factory.createParser(input);
      parser.parse(input);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should mention invalid weekday",
              e.getMessage().contains("Invalid weekday character: 'X'"));
    }
  }

  @Test
  public void testComplexQuotedSubjects() {
    input = "create event \"Team Meeting: Sprint Planning & Review\" " +
            "from 2024-03-20T10:00 to 2024-03-20T12:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Team Meeting: Sprint Planning & " +
            "Review starting at 2024-03-20T10:00 until 2024-03-20T12:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testComplexEditWithQuotes() {
    input = "edit series description \"Daily Standup\" from 2024-03-20T09:00 " +
            "with \"Brief daily team sync-up meeting\"";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited all series of event's properties: Daily Standup with " +
            "start time 2024-03-20T09:00. Changed description to Brief daily team sync-up meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testMultipleCommands() {
    // First command
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    input = "print events on 2024-03-20";
    parser = factory.createParser(input);
    parser.parse(input);

    input = "show status on 2024-03-20T10:30";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00" +
            "Queried for all events that occur on 2024-03-20T00:00" +
            "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, modelLog.toString());
  }

  // Integration Tests for Assignment 5 - Calendar Management + Event Operations
  @Test
  public void testCalendarManagementIntegration() {
    // Test creating a calendar
    input = "create calendar --name Work --timezone America/New_York";
    parser = factory.createParser(input);
    parser.parse(input);
    
    assertTrue("Should handle calendar commands", 
        managerLog.toString().contains("Created calendar Work with timezone America/New_York"));
    
    // Test using the calendar
    managerLog.setLength(0); // Clear log
    input = "use calendar --name Work";
    parser = factory.createParser(input);
    parser.parse(input);
    
    assertTrue("Should handle use calendar commands", 
        managerLog.toString().contains("Switched to calendar Work"));
  }

  @Test
  public void testEventCommandRequiresActiveCalendar() {
    // Remove active calendar
    mockManager.setCurrentCalendar(null);
    
    try {
      input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
      parser = factory.createParser(input);
      fail("Should throw exception when no calendar is active");
    } catch (IllegalArgumentException e) {
      assertTrue("Error should mention no active calendar", 
          e.getMessage().contains("No calendar is currently in use"));
    }
  }

  @Test
  public void testCalendarContextSwitching() {
    // Set up first calendar
    input = "create calendar --name Work --timezone America/New_York";
    parser = factory.createParser(input);
    parser.parse(input);
    
    input = "use calendar --name Work";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Create event in Work calendar
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
    
    String workEventLog = modelLog.toString();
    assertTrue("Should create event in active calendar", 
        workEventLog.contains("Created single timed event Meeting"));
    
    // Switch to different calendar
    modelLog.setLength(0); // Clear model log
    input = "use calendar --name Personal";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Create event in Personal calendar (would need different mock setup)
    input = "print events on 2024-03-20";
    parser = factory.createParser(input);
    parser.parse(input);
    
    assertTrue("Should query events in new active calendar", 
        modelLog.toString().contains("Queried for all events"));
  }

  @Test
  public void testCopyCommandIntegration() {
    input = "copy event Meeting on 2024-03-20T10:00 --target Personal to 2024-03-25T10:00";
    parser = factory.createParser(input);
    parser.parse(input);
    
    String expected = "Copied event Meeting from 2024-03-20T10:00 to calendar Personal at" +
            " 2024-03-25T10:00";
    assertTrue("Should handle copy event commands", 
        managerLog.toString().contains(expected));
  }

  @Test
  public void testCopyEventsOnDateIntegration() {
    input = "copy events on 2024-03-20 --target Personal to 2024-04-15";
    parser = factory.createParser(input);
    parser.parse(input);
    
    String expected = "Copied events on 2024-03-20T00:00 to calendar Personal starting at " +
            "2024-04-15T00:00";
    assertTrue("Should handle copy events on date commands", 
        managerLog.toString().contains(expected));
  }

  @Test
  public void testCopyEventsBetweenDatesIntegration() {
    input = "copy events between 2024-03-18 and 2024-03-22 --target Personal to 2024-05-01";
    parser = factory.createParser(input);
    parser.parse(input);
    
    String expected = "Copied events between 2024-03-18T00:00 and 2024-03-22T00:00 to " +
            "calendar Personal starting at 2024-05-01T00:00";
    assertTrue("Should handle copy events between dates commands", 
        managerLog.toString().contains(expected));
  }

  @Test
  public void testEditCalendarIntegration() {
    input = "edit calendar --name Work --property timezone Europe/London";
    parser = factory.createParser(input);
    parser.parse(input);
    
    String expected = "Edited calendar Work property timezone to Europe/London";
    assertTrue("Should handle edit calendar commands", 
        managerLog.toString().contains(expected));
  }

  @Test
  public void testMixedCalendarAndEventCommands() {
    // Create and use calendar
    input = "create calendar --name TestCal --timezone UTC";
    parser = factory.createParser(input);
    parser.parse(input);
    
    input = "use calendar --name TestCal";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Create event
    input = "create event TestEvent from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Query event
    input = "print events on 2024-03-20";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Edit calendar
    input = "edit calendar --name TestCal --property timezone America/New_York";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Copy event
    input = "copy event TestEvent on 2024-03-20T10:00 --target OtherCal to 2024-03-25T10:00";
    parser = factory.createParser(input);
    parser.parse(input);
    
    // Verify all operations were logged
    assertTrue("Should create calendar", managerLog.toString()
            .contains("Created calendar TestCal"));
    assertTrue("Should use calendar", managerLog.toString()
            .contains("Switched to calendar TestCal"));
    assertTrue("Should create event", modelLog.toString()
            .contains("Created single timed event TestEvent"));
    assertTrue("Should query events", modelLog.toString()
            .contains("Queried for all events"));
    assertTrue("Should edit calendar", managerLog.toString()
            .contains("Edited calendar TestCal"));
    assertTrue("Should copy event", managerLog.toString()
            .contains("Copied event TestEvent"));
  }
}