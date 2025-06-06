package controller;

import calendar.controller.ICommandParser;
import calendar.controller.parser.CommandParserFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for CommandParser using manual mock implementations.
 * Tests all command types, valid parsing scenarios, and error conditions.
 */
public class CommandParserTest {
  private StringBuilder modelLog;
  private StringBuilder viewOutput;
  private MockCalendarModel mockModel;
  private MockCalendarView mockView;
  private CommandParserFactory factory;
  private ICommandParser parser;
  private String input;

  @Before
  public void setUp() {
    modelLog = new StringBuilder();
    viewOutput = new StringBuilder();
    mockModel = new MockCalendarModel(modelLog);
    mockView = new MockCalendarView(viewOutput);
    factory = new CommandParserFactory(mockModel, mockView);
  }

  @Test
  public void testCreateSingleTimedEvent() {
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleTimedEventQuoted() {
    input = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Team Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleTimedEventLongSubject() {
    input = "create event \"Very Long Meeting Name With Multiple Words\" from 2024-03-20T10:00 to 2024-03-20T11:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Very Long Meeting Name With Multiple Words starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateSingleTimedEventCaseInsensitive() {
    input = "CREATE EVENT Meeting FROM 2024-03-20T10:00 TO 2024-03-20T11:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00";
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
    input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF for 5 times";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00 for a count of 5";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    input = "create event \"Weekly Standup\" from 2024-03-20T09:00 to 2024-03-20T09:30 repeats MTWRF until 2024-06-20";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Weekly Standup starting at 2024-03-20T09:00 until 2024-03-20T09:30 to the date 2024-06-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventSingleWeekday() {
    input = "create event \"Monday Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeats M for 10 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Monday Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00 for a count of 10";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventAllWeekdays() {
    input = "create event Daily from 2024-03-20T08:00 to 2024-03-20T09:00 repeats MTWRFSU for 7 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Daily starting at 2024-03-20T08:00 until 2024-03-20T09:00 for a count of 7";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringTimedEventWeekends() {
    input = "create event \"Weekend Fun\" from 2024-03-20T14:00 to 2024-03-20T16:00 repeats SU for 4 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Weekend Fun starting at 2024-03-20T14:00 until 2024-03-20T16:00 for a count of 4";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventCount() {
    input = "create event Holiday on 2024-03-20 repeats F for 10 times";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring all day event Holiday starting on the date 2024-03-20T00:00 for a count of 10";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    input = "create event \"Weekly Holiday\" on 2024-03-20 repeats W until 2024-12-31";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Created recurring timed event Weekly Holiday starting on the date 2024-03-20T00:00 to the date 2024-12-31T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventSubject() {
    input = "edit event subject Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with \"New Meeting\"";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on 2024-03-20T10:00 until 2024-03-20T11:00. Changed subject to New Meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventStart() {
    input = "edit event start \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 with 2024-03-20T09:00";

    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Team Meeting starting on 2024-03-20T10:00 until 2024-03-20T11:00. Changed start to 2024-03-20T09:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventEnd() {
    input = "edit event end Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with 2024-03-20T12:00";
    parser = factory.createParser(input);
    parser.parse(input);

    String expectedLog = "Edited single event's property: Meeting starting on 2024-03-20T10:00 until 2024-03-20T11:00. Changed end to 2024-03-20T12:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventDescription() {
    parser.parse("edit event description Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with \"Important meeting\"");

    String expectedLog = "Edited single event's property: Meeting starting on 2024-03-20T10:00 until 2024-03-20T11:00. Changed description to Important meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventLocation() {
    parser.parse("edit event location Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with ONLINE");

    String expectedLog = "Edited single event's property: Meeting starting on 2024-03-20T10:00 until 2024-03-20T11:00. Changed location to ONLINE";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventStatus() {
    parser.parse("edit event status Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with PRIVATE");

    String expectedLog = "Edited single event's property: Meeting starting on 2024-03-20T10:00 until 2024-03-20T11:00. Changed status to PRIVATE";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventsFromDate() {
    parser.parse("edit events subject \"Weekly Meeting\" from 2024-03-20T10:00 with \"Updated Weekly Meeting\"");

    String expectedLog = "Edited series of event's properties: Weekly Meeting starting on or after date 2024-03-20T10:00. Changed subject to Updated Weekly Meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEventsLocation() {
    parser.parse("edit events location Meeting from 2024-03-20T10:00 with PHYSICAL");

    String expectedLog = "Edited series of event's properties: Meeting starting on or after date 2024-03-20T10:00. Changed location to PHYSICAL";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditEntireSeries() {
    parser.parse("edit series subject Meeting from 2024-03-20T10:00 with \"Updated Meeting\"");

    String expectedLog = "Edited all series of event's properties: Meeting with start time 2024-03-20T10:00. Changed subject to Updated Meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEditSeriesDescription() {
    parser.parse("edit series description \"Team Standup\" from 2024-03-20T09:00 with \"Daily team synchronization\"");

    String expectedLog = "Edited all series of event's properties: Team Standup with start time 2024-03-20T09:00. Changed description to Daily team synchronization";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsOnDate() {
    parser.parse("print events on 2024-03-20");

    String expectedLog = "Queried for all events that occur on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsDateRange() {
    parser.parse("print events from 2024-03-20T00:00 to 2024-03-25T23:59");

    String expectedLog = "Queried for all events that occur from 2024-03-20T00:00 to 2024-03-25T23:59";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsLongRange() {
    parser.parse("print events from 2024-01-01T00:00 to 2024-12-31T23:59");

    String expectedLog = "Queried for all events that occur from 2024-01-01T00:00 to 2024-12-31T23:59";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testPrintEventsCaseInsensitive() {
    parser.parse("PRINT EVENTS ON 2024-03-20");

    String expectedLog = "Queried for all events that occur on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testShowStatus() {
    parser.parse("show status on 2024-03-20T10:30");

    String expectedLog = "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testShowStatusDifferentTime() {
    parser.parse("show status on 2024-12-25T00:00");

    String expectedLog = "Checked if there is an event during 2024-12-25T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testShowStatusCaseInsensitive() {
    parser.parse("SHOW STATUS ON 2024-03-20T10:30");

    String expectedLog = "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testExtraWhitespace() {
    parser.parse("  create   event   Meeting   from   2024-03-20T10:00   to   2024-03-20T11:00  ");

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testTabsInCommand() {
    parser.parse("create\tevent\tMeeting\tfrom\t2024-03-20T10:00\tto\t2024-03-20T11:00");

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testLeapYearDate() {
    parser.parse("create event \"Leap Day Event\" on 2024-02-29");

    String expectedLog = "Created single all day event Leap Day Event on 2024-02-29T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testEndOfYearDateTime() {
    parser.parse("show status on 2024-12-31T23:59");

    String expectedLog = "Checked if there is an event during 2024-12-31T23:59";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testMidnightTime() {
    parser.parse("create event \"Midnight Event\" from 2024-03-20T00:00 to 2024-03-20T01:00");

    String expectedLog = "Created single timed event Midnight Event starting at 2024-03-20T00:00 until 2024-03-20T01:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testSingleCharacterSubject() {
    parser.parse("create event A on 2024-03-20");

    String expectedLog = "Created single all day event A on 2024-03-20T00:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testMaxRecurringCount() {
    parser.parse("create event Daily from 2024-03-20T10:00 to 2024-03-20T11:00 repeats M for 999 times");

    String expectedLog = "Created recurring timed event Daily starting at 2024-03-20T10:00 until 2024-03-20T11:00 for a count of 999";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCommand() {
    parser.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCommand() {
    parser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceOnlyCommand() {
    parser.parse("   \t\n  ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownCommand() {
    parser.parse("delete event Meeting");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMissingEventKeyword() {
    parser.parse("create Meeting from 2024-03-20T10:00 to 2024-03-20T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMissingSubject() {
    parser.parse("create event from 2024-03-20T10:00 to 2024-03-20T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidTimedEventKeyword() {
    parser.parse("create event Meeting at 2024-03-20T10:00 to 2024-03-20T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMissingToKeyword() {
    parser.parse("create event Meeting from 2024-03-20T10:00 2024-03-20T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidDateTimeFormat() {
    parser.parse("create event Meeting from 2024/03/20T10:00 to 2024/03/20T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidDateFormat() {
    parser.parse("create event Holiday on 03-20-2024");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringMissingRepeats() {
    parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 MWF for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringInvalidWeekday() {
    parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MXF for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringInvalidCount() {
    parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF for -5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringMissingTimesKeyword() {
    parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF for 5");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidType() {
    parser.parse("edit meeting subject Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidProperty() {
    parser.parse("edit event title Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 with New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingFromKeyword() {
    parser.parse("edit event subject Meeting 2024-03-20T10:00 to 2024-03-20T11:00 with New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingWithKeyword() {
    parser.parse("edit event subject Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 New");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintMissingEventsKeyword() {
    parser.parse("print on 2024-03-20");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintInvalidKeyword() {
    parser.parse("print meetings on 2024-03-20");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintMissingOnKeyword() {
    parser.parse("print events 2024-03-20");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintInvalidDateFormat() {
    parser.parse("print events on 03/20/2024");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowMissingStatusKeyword() {
    parser.parse("show on 2024-03-20T10:30");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowInvalidKeyword() {
    parser.parse("show availability on 2024-03-20T10:30");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowMissingOnKeyword() {
    parser.parse("show status 2024-03-20T10:30");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowInvalidDateTimeFormat() {
    parser.parse("show status on 2024-03-20 10:30");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnclosedQuote() {
    parser.parse("create event \"Team Meeting from 2024-03-20T10:00 to 2024-03-20T11:00");
  }

  @Test
  public void testUnknownCommandErrorMessage() {
    try {
      parser.parse("unknown command");
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
      parser.parse("");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should mention empty command",
              e.getMessage().contains("cannot be empty"));
    }
  }

  @Test
  public void testInvalidWeekdayErrorMessage() {
    try {
      parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MXF for 5 times");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue("Error message should mention invalid weekday",
              e.getMessage().contains("Invalid weekday character"));
      assertTrue("Error message should mention the invalid character",
              e.getMessage().contains("X"));
    }
  }

  @Test
  public void testComplexQuotedSubjects() {
    parser.parse("create event \"Team Meeting: Sprint Planning & Review\" from 2024-03-20T10:00 to 2024-03-20T12:00");

    String expectedLog = "Created single timed event Team Meeting: Sprint Planning & Review starting at 2024-03-20T10:00 until 2024-03-20T12:00";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testComplexEditWithQuotes() {
    parser.parse("edit series description \"Daily Standup\" from 2024-03-20T09:00 with \"Brief daily team sync-up meeting\"");

    String expectedLog = "Edited all series of event's properties: Daily Standup with start time 2024-03-20T09:00. Changed description to Brief daily team sync-up meeting";
    assertEquals(expectedLog, modelLog.toString());
  }

  @Test
  public void testMultipleCommands() {
    parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00");
    parser.parse("print events on 2024-03-20");
    parser.parse("show status on 2024-03-20T10:30");

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until 2024-03-20T11:00" +
            "Queried for all events that occur on 2024-03-20T00:00" +
            "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, modelLog.toString());
  }
} 