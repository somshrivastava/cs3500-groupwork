package controller;

import org.junit.Before;
import org.junit.Test;


import calendar.controller.parser.CommandParser;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;


/**
 * Test suite focused on invalid commands and error conditions for CommandParser.
 * Tests all error cases specified in the assignment to ensure proper error handling.
 */
public class CommandParserTest {

  private ICalendarModel mockModel;

  private ICalendarView mockView;

  private CommandParser parser;

  @Before
  public void setUp() {
    parser = new CommandParser(mockModel, mockView);
  }

  // ==================== GENERAL INVALID COMMANDS ====================

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCommand() {
    parser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCommand() {
    parser.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceOnlyCommand() {
    parser.parse("   \t\n  ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownCommand() {
    parser.parse("delete event \"Meeting\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMisspelledCommand() {
    parser.parse("creat event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncompleteCommand() {
    parser.parse("create");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongCommandStructure() {
    parser.parse("event create \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  // ==================== CREATE EVENT ERRORS ====================

  // --- Missing Required Components ---

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingEventKeyword() {
    parser.parse("create \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingSubject() {
    parser.parse("create event from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingFromKeyword() {
    parser.parse("create event \"Meeting\" 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingToKeyword() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingStartTime() {
    parser.parse("create event \"Meeting\" from to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingEndTime() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingOnKeyword() {
    parser.parse("create event \"Meeting\" 2025-05-05");
  }

  // --- Quote Errors ---

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_MissingClosingQuote() {
    parser.parse("create event \"Meeting from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_NoQuotesForMultiWordSubject() {
    parser.parse("create event Team Meeting from 2025-05-05T10:00 to 2025-05-05T11:00");
  }

  // --- Date/Time Format Errors ---

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_InvalidDateFormat_Slashes() {
    parser.parse("create event \"Meeting\" from 05/05/2025T10:00 to 05/05/2025T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_InvalidDateFormat_WrongOrder() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-31-12T11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_InvalidTimeFormat_NoT() {
    parser.parse("create event \"Meeting\" from 2025-05-05 10:00 to 2025-05-05 11:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_InvalidTimeFormat_12Hour() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00AM to 2025-05-05T11:00AM");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEvent_InvalidAllDayDateFormat() {
    parser.parse("create event \"Conference\" on 05-05-2025");
  }

  // --- Recurring Event Errors ---

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_MissingRepeatsKeyword() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 MWF for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_MissingWeekdays() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_InvalidWeekdayCharacter() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MXF for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_EmptyWeekdays() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats \"\" for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_MissingForKeyword() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWF 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_MissingTimesKeyword() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWF for 5");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_InvalidCount() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWF for zero times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_NegativeCount() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWF for -5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_MissingUntilKeyword() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWF 2025-06-30");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_InvalidUntilDate() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWF until June 30");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurring_WrongKeywordOrder() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 for 5 times repeats MWF");
  }

  // ==================== EDIT EVENT ERRORS ====================

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_InvalidEditType() {
    parser.parse("edit meeting subject \"Old\" from 2025-05-05T10:00 to 2025-05-05T11:00 with \"New\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_MissingProperty() {
    parser.parse("edit event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 with \"New\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_InvalidProperty() {
    parser.parse("edit event color \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 with red");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_MissingFromKeyword() {
    parser.parse("edit event subject \"Meeting\" 2025-05-05T10:00 to 2025-05-05T11:00 with \"New\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_MissingToKeyword_SingleEvent() {
    parser.parse("edit event subject \"Meeting\" from 2025-05-05T10:00 2025-05-05T11:00 with \"New\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_MissingWithKeyword() {
    parser.parse("edit event subject \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 \"New\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_MissingNewValue() {
    parser.parse("edit event subject \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 with");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEdit_TooFewArguments() {
    parser.parse("edit event");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEvents_MissingStartTime() {
    parser.parse("edit events subject \"Meeting\" from with \"New\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSeries_InvalidStructure() {
    parser.parse("edit series \"Meeting\" subject from 2025-05-05T10:00 with \"New\"");
  }

  // ==================== PRINT EVENTS ERRORS ====================

  @Test(expected = IllegalArgumentException.class)
  public void testPrint_MissingEventsKeyword() {
    parser.parse("print on 2025-05-05");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrint_InvalidKeyword() {
    parser.parse("print appointments on 2025-05-05");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrint_MissingOnKeyword() {
    parser.parse("print events 2025-05-05");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrint_MissingDate() {
    parser.parse("print events on");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrint_InvalidDateFormat() {
    parser.parse("print events on May 5, 2025");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintRange_MissingFromKeyword() {
    parser.parse("print events 2025-05-05T00:00 to 2025-05-10T23:59");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintRange_MissingToKeyword() {
    parser.parse("print events from 2025-05-05T00:00 2025-05-10T23:59");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintRange_MissingStartDate() {
    parser.parse("print events from to 2025-05-10T23:59");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintRange_MissingEndDate() {
    parser.parse("print events from 2025-05-05T00:00 to");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintRange_WrongArgumentCount() {
    parser.parse("print events from 2025-05-05T00:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrint_InvalidCommandStructure() {
    parser.parse("print events between 2025-05-05 and 2025-05-10");
  }

  // ==================== SHOW STATUS ERRORS ====================

  @Test(expected = IllegalArgumentException.class)
  public void testShow_InvalidCommand() {
    parser.parse("show availability on 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_MissingStatusKeyword() {
    parser.parse("show on 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_MissingOnKeyword() {
    parser.parse("show status 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_MissingDateTime() {
    parser.parse("show status on");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_InvalidDateTimeFormat() {
    parser.parse("show status on 2025-05-05 10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_WrongKeyword() {
    parser.parse("show status at 2025-05-05T10:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_TooManyArguments() {
    parser.parse("show status on 2025-05-05T10:00 extra");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShow_TooFewArguments() {
    parser.parse("show status");
  }

  // ==================== COMPLEX ERROR SCENARIOS ====================

  @Test(expected = IllegalArgumentException.class)
  public void testComplexError_MixedValidInvalidWeekdays() {
    parser.parse("create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00 repeats MWXF for 5 times");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComplexError_QuotesInWrongPlace() {
    parser.parse("create event Meeting from \"2025-05-05T10:00\" to \"2025-05-05T11:00\"");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComplexError_PartialCommand() {
    parser.parse("create event \"Meeting\" from");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComplexError_ExtraWordsInCommand() {
    parser.parse("please create event \"Meeting\" from 2025-05-05T10:00 to 2025-05-05T11:00");
  }
}