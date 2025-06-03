import calendar.controller.CommandParser;
import calendar.controller.commands.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandParserTest {
    private CommandParser parser;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Before
    public void setUp() {
        parser = new CommandParser();
    }

    @Test
    public void testParseCreateSingleTimedEvent() {
        String command = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof CreateSingleTimedEventCommand);
    }

    @Test
    public void testParseCreateRecurringTimedEventWithCount() {
        String command = "create event \"Weekly Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF for 10";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof CreateRecurringTimedEventWithCountCommand);
    }

    @Test
    public void testParseCreateRecurringTimedEventUntilDate() {
        String command = "create event \"Weekly Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF until 2024-04-20";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof CreateRecurringTimedEventUntilDateCommand);
    }

    @Test
    public void testParseCreateSingleAllDayEvent() {
        String command = "create event \"Holiday\" on 2024-03-20";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof CreateSingleAllDayEventCommand);
    }

    @Test
    public void testParseCreateRecurringAllDayEventWithCount() {
        String command = "create event \"Weekly Holiday\" on 2024-03-20 repeats MWF for 10";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof CreateRecurringAllDayEventWithCountCommand);
    }

    @Test
    public void testParseCreateRecurringAllDayEventUntilDate() {
        String command = "create event \"Weekly Holiday\" on 2024-03-20 repeats MWF until 2024-04-20";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof CreateRecurringAllDayEventUntilDateCommand);
    }

    // Error cases for create commands
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCommandWord() {
        parser.parse("make event \"Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEventWord() {
        parser.parse("create meeting \"Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingQuotes() {
        parser.parse("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEventType() {
        parser.parse("create event \"Meeting\" at 2024-03-20T10:00 to 2024-03-20T11:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimedEventFormat() {
        parser.parse("create event \"Meeting\" from 2024-03-20T10:00 2024-03-20T11:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRecurringFormat() {
        parser.parse("create event \"Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeat MWF for 10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidWeekdays() {
        parser.parse("create event \"Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeats XYZ for 10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCount() {
        parser.parse("create event \"Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF for abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUntilDate() {
        parser.parse("create event \"Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 repeats MWF until invalid-date");
    }

    // Test edit commands
    @Test
    public void testParseEditSingleEvent() {
        String command = "edit event subject \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00 with \"New Meeting\"";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof EditSingleEventCommand);
    }

    @Test
    public void testParseEditEventsFromDate() {
        String command = "edit events subject \"Team Meeting\" from 2024-03-20T10:00 with \"New Meeting\"";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof EditEventsFromDateCommand);
    }

    @Test
    public void testParseEditSeries() {
        String command = "edit series subject \"Team Meeting\" from 2024-03-20T10:00 with \"New Meeting\"";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof EditSeriesCommand);
    }

    // Test print commands
    @Test
    public void testParsePrintEventsOnDate() {
        String command = "print events on 2024-03-20";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof PrintEventsOnDateCommand);
    }

    @Test
    public void testParsePrintEventsInInterval() {
        String command = "print events from 2024-03-20T10:00 to 2024-03-20T11:00";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof PrintEventsInIntervalCommand);
    }

    // Test show command
    @Test
    public void testParseShowStatus() {
        String command = "show status on 2024-03-20T10:00";
        ICalendarCommand result = parser.parse(command);
        assertTrue(result instanceof ShowStatusCommand);
    }

    // Error cases for other commands
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEditFormat() {
        parser.parse("edit invalid subject \"Meeting\" from 2024-03-20T10:00 with \"New Meeting\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPrintFormat() {
        parser.parse("print invalid on 2024-03-20");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidShowFormat() {
        parser.parse("show invalid on 2024-03-20T10:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCommand() {
        parser.parse("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCommand() {
        parser.parse(null);
    }
} 