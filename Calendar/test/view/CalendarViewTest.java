package view;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import calendar.model.Event;
import calendar.model.EventLocation;
import calendar.model.IEvent;
import calendar.view.CalendarView;
import calendar.view.ICalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Represents tests and examples for CalendarView.
 * Tests all public methods, formatting logic, error handling, and edge cases.
 */
public class CalendarViewTest {
  private StringBuilder output;
  private ICalendarView view;
  private LocalDateTime baseDateTime;
  private LocalDateTime endDateTime;

  @Before
  public void setUp() {
    output = new StringBuilder();
    view = new CalendarView(output);
    baseDateTime = LocalDateTime.of(2024, 3, 20, 10, 0); // March 20, 2024, 10:00 AM
    endDateTime = LocalDateTime.of(2024, 3, 20, 11, 0);  // March 20, 2024, 11:00 AM
  }

  @Test
  public void testConstructorWithValidAppendable() {
    StringBuilder sb = new StringBuilder();
    ICalendarView testView = new CalendarView(sb);
    assertNotNull("View should be created successfully", testView);
  }

  @Test
  public void testConstructorWithSystemOut() {
    ICalendarView testView = new CalendarView(System.out);
    assertNotNull("View should work with System.out", testView);
  }

  @Test
  public void testDisplayMessage() {
    view.displayMessage("Hello World");
    assertEquals("Hello World\n", output.toString());
  }

  @Test
  public void testDisplayMessageEmpty() {
    view.displayMessage("");
    assertEquals("\n", output.toString());
  }

  @Test
  public void testDisplayMultipleMessages() {
    view.displayMessage("First message");
    view.displayMessage("Second message");
    assertEquals("First message\nSecond message\n", output.toString());
  }

  @Test
  public void testDisplayMessageWithSpecialCharacters() {
    view.displayMessage("Message with special chars: !@#$%^&*()");
    assertEquals("Message with special chars: !@#$%^&*()\n", output.toString());
  }

  @Test
  public void testDisplayMessageWithNewlines() {
    view.displayMessage("Line 1\nLine 2");
    assertEquals("Line 1\nLine 2\n", output.toString());
  }

  @Test
  public void testDisplayError() {
    view.displayError("Something went wrong");
    assertEquals("\nERROR: Something went wrong\n", output.toString());
  }

  @Test
  public void testDisplayErrorEmpty() {
    view.displayError("");
    assertEquals("\nERROR: \n", output.toString());
  }

  @Test
  public void testDisplayMultipleErrors() {
    view.displayError("First error");
    view.displayError("Second error");
    assertEquals("\nERROR: First error\n\nERROR: Second error\n", output.toString());
  }

  @Test
  public void testDisplayErrorWithSpecialCharacters() {
    view.displayError("Error with special chars: !@#$%^&*()");
    assertEquals("\nERROR: Error with special chars: !@#$%^&*()\n", output.toString());
  }

  @Test
  public void testDisplayEventsForDateWithEvents() {
    List<IEvent> events = createSampleEvents();
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    String expected = "\nEvents on 2024-03-20\n" +
            "--------------------\n" +
            "• Meeting (2024-03-20 10:00 - 11:00)\n" +
            "• Lunch (2024-03-20 12:00 - 13:00) : PHYSICAL\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateNoEvents() {
    List<IEvent> events = new ArrayList<>();
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    String expected = "\nEvents on 2024-03-20\n" +
            "--------------------\n" +
            "No events found.\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateSingleEvent() {
    List<IEvent> events = Arrays.asList(createBasicEvent());
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    String expected = "\nEvents on 2024-03-20\n" +
            "--------------------\n" +
            "• Meeting (2024-03-20 10:00 - 11:00)\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateMultiDayEvent() {
    IEvent multiDayEvent = createMultiDayEvent();
    List<IEvent> events = Arrays.asList(multiDayEvent);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    String expected = "\nEvents on 2024-03-20\n" +
            "--------------------\n" +
            "• Conference (2024-03-20 09:00 - 2024-03-22 17:00)\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateAllDayEvent() {
    IEvent allDayEvent = createAllDayEvent();
    List<IEvent> events = Arrays.asList(allDayEvent);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    String expected = "\nEvents on 2024-03-20\n" +
            "--------------------\n" +
            "• Holiday (2024-03-20 08:00 - 17:00)\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateRangeWithEvents() {
    List<IEvent> events = createSampleEvents();
    LocalDateTime startDate = LocalDateTime.of(2024, 3, 20, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 3, 22, 23, 59);

    view.displayEventsForDateRange(startDate, endDate, events);

    String header = "Events from 2024-03-20 to 2024-03-22";
    String expected = "\n" + header + "\n" +
            "-".repeat(header.length()) + "\n" +
            "• Meeting (2024-03-20 10:00 - 11:00)\n" +
            "• Lunch (2024-03-20 12:00 - 13:00) : PHYSICAL\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateRangeNoEvents() {
    List<IEvent> events = new ArrayList<>();
    LocalDateTime startDate = LocalDateTime.of(2024, 3, 20, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 3, 22, 23, 59);

    view.displayEventsForDateRange(startDate, endDate, events);

    String header = "Events from 2024-03-20 to 2024-03-22";
    String expected = "\n" + header + "\n" +
            "-".repeat(header.length()) + "\n" +
            "No events found.\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateRangeSameDay() {
    List<IEvent> events = createSampleEvents();
    LocalDateTime startDate = LocalDateTime.of(2024, 3, 20, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 3, 20, 23, 59);

    view.displayEventsForDateRange(startDate, endDate, events);

    String header = "Events from 2024-03-20 to 2024-03-20";
    String expected = "\n" + header + "\n" +
            "-".repeat(header.length()) + "\n" +
            "• Meeting (2024-03-20 10:00 - 11:00)\n" +
            "• Lunch (2024-03-20 12:00 - 13:00) : PHYSICAL\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayEventsForDateRangeLongRange() {
    List<IEvent> events = createSampleEvents();
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

    view.displayEventsForDateRange(startDate, endDate, events);

    String header = "Events from 2024-01-01 to 2024-12-31";
    String expected = "\n" + header + "\n" +
            "-".repeat(header.length()) + "\n" +
            "• Meeting (2024-03-20 10:00 - 11:00)\n" +
            "• Lunch (2024-03-20 12:00 - 13:00) : PHYSICAL\n" +
            "\n";
    assertEquals(expected, output.toString());
  }

  @Test
  public void testDisplayStatusBusy() {
    view.displayStatus("2024-03-20T10:30", true);
    assertEquals("\nBusy\n\n", output.toString());
  }

  @Test
  public void testDisplayStatusAvailable() {
    view.displayStatus("2024-03-20T10:30", false);
    assertEquals("\nAvailable\n\n", output.toString());
  }

  @Test
  public void testDisplayStatusMultiple() {
    view.displayStatus("2024-03-20T10:30", true);
    view.displayStatus("2024-03-20T15:30", false);
    assertEquals("\nBusy\n\n\nAvailable\n\n", output.toString());
  }

  @Test
  public void testDisplayPrompt() {
    view.displayPrompt();
    assertEquals("> ", output.toString());
  }

  @Test
  public void testDisplayMultiplePrompts() {
    view.displayPrompt();
    view.displayPrompt();
    view.displayPrompt();
    assertEquals("> > > ", output.toString());
  }

  @Test
  public void testEventFormattingBasic() {
    IEvent event = createBasicEvent();
    List<IEvent> events = Arrays.asList(event);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should contain formatted event",
            output.toString().contains("• Meeting (2024-03-20 10:00 - 11:00)"));
  }

  @Test
  public void testEventFormattingWithLocation() {
    IEvent event = Event.getBuilder()
            .subject("Meeting with Location")
            .startDateTime(baseDateTime)
            .endDateTime(endDateTime)
            .location(EventLocation.ONLINE)
            .build();

    List<IEvent> events = Arrays.asList(event);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should contain location",
            output.toString().contains(": ONLINE"));
  }

  @Test
  public void testEventFormattingWithoutLocation() {
    IEvent event = createBasicEvent();
    List<IEvent> events = Arrays.asList(event);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertFalse("Should not contain location marker",
            output.toString().contains(" : "));
  }

  @Test
  public void testEventFormattingMultiDay() {
    IEvent event = createMultiDayEvent();
    List<IEvent> events = Arrays.asList(event);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should format as multi-day event",
            output.toString().contains("2024-03-20 09:00 - 2024-03-22 17:00"));
  }

  @Test
  public void testEventFormattingSpecialCharacters() {
    IEvent event = Event.getBuilder()
            .subject("Meeting with special chars: !@#$%^&*()")
            .startDateTime(baseDateTime)
            .endDateTime(endDateTime)
            .build();

    List<IEvent> events = Arrays.asList(event);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should handle special characters",
            output.toString().contains("Meeting with special chars: !@#$%^&*()"));
  }

  @Test
  public void testEventFormattingLongSubject() throws IOException {
    String longSubject = "This is a very long meeting subject that might wrap around " +
            "multiple lines and should be handled properly by the view formatting system";
    IEvent event = Event.getBuilder()
            .subject(longSubject)
            .startDateTime(baseDateTime)
            .endDateTime(endDateTime)
            .build();

    List<IEvent> events = Arrays.asList(event);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should handle long subject",
            output.toString().contains(longSubject));
  }

  @Test
  public void testHeaderFormattingShortDate() {
    List<IEvent> events = new ArrayList<>();
    LocalDate date = LocalDate.of(2024, 1, 1);

    view.displayEventsForDate(date, events);

    String output = this.output.toString();
    assertTrue("Should contain properly formatted header",
            output.contains("Events on 2024-01-01"));
    assertTrue("Should contain underline",
            output.contains("-------------------"));
  }

  @Test
  public void testHeaderFormattingDateRange() {
    List<IEvent> events = new ArrayList<>();
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

    view.displayEventsForDateRange(startDate, endDate, events);

    String output = this.output.toString();
    String header = "Events from 2024-01-01 to 2024-12-31";
    assertTrue("Should contain properly formatted range header",
            output.contains(header));
    assertTrue("Should contain appropriate underline length",
            output.contains("-".repeat(header.length())));
  }

  @Test(expected = IllegalStateException.class)
  public void testIOExceptionHandlingDisplayMessage() {
    FailingAppendable failingAppendable = new FailingAppendable();
    ICalendarView failingView = new CalendarView(failingAppendable);
    failingView.displayMessage("This should fail");
  }

  @Test(expected = IllegalStateException.class)
  public void testIOExceptionHandlingDisplayError() {
    FailingAppendable failingAppendable = new FailingAppendable();
    ICalendarView failingView = new CalendarView(failingAppendable);
    failingView.displayError("This should fail");
  }

  @Test(expected = IllegalStateException.class)
  public void testIOExceptionHandlingDisplayEvents() {
    FailingAppendable failingAppendable = new FailingAppendable();
    ICalendarView failingView = new CalendarView(failingAppendable);
    List<IEvent> events = createSampleEvents();
    failingView.displayEventsForDate(LocalDate.now(), events);
  }

  @Test(expected = IllegalStateException.class)
  public void testIOExceptionHandlingDisplayPrompt() {
    FailingAppendable failingAppendable = new FailingAppendable();
    ICalendarView failingView = new CalendarView(failingAppendable);
    failingView.displayPrompt();
  }

  @Test(expected = IllegalStateException.class)
  public void testIOExceptionHandlingDisplayStatus() {
    FailingAppendable failingAppendable = new FailingAppendable();
    ICalendarView failingView = new CalendarView(failingAppendable);
    failingView.displayStatus("2024-03-20T10:30", true);
  }

  @Test
  public void testMidnightEvents() {
    IEvent midnightEvent = Event.getBuilder()
            .subject("Midnight Event")
            .startDateTime(LocalDateTime.of(2024, 3, 20, 0, 0))
            .endDateTime(LocalDateTime.of(2024, 3, 20, 1, 0))
            .build();

    List<IEvent> events = Arrays.asList(midnightEvent);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should handle midnight events",
            output.toString().contains("00:00 - 01:00"));
  }

  @Test
  public void testLateNightEvents() {
    IEvent lateEvent = Event.getBuilder()
            .subject("Late Night Event")
            .startDateTime(LocalDateTime.of(2024, 3, 20, 23, 30))
            .endDateTime(LocalDateTime.of(2024, 3, 20, 23, 59))
            .build();

    List<IEvent> events = Arrays.asList(lateEvent);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    assertTrue("Should handle late night events",
            output.toString().contains("23:30 - 23:59"));
  }

  @Test
  public void testBothLocationTypes() {
    IEvent physicalEvent = Event.getBuilder()
            .subject("Physical Meeting")
            .startDateTime(baseDateTime)
            .endDateTime(endDateTime)
            .location(EventLocation.PHYSICAL)
            .build();

    IEvent onlineEvent = Event.getBuilder()
            .subject("Online Meeting")
            .startDateTime(baseDateTime.plusHours(2))
            .endDateTime(endDateTime.plusHours(2))
            .location(EventLocation.ONLINE)
            .build();

    List<IEvent> events = Arrays.asList(physicalEvent, onlineEvent);
    LocalDate date = LocalDate.of(2024, 3, 20);

    view.displayEventsForDate(date, events);

    String output = this.output.toString();
    assertTrue("Should display PHYSICAL location", output.contains(": PHYSICAL"));
    assertTrue("Should display ONLINE location", output.contains(": ONLINE"));
  }

  @Test
  public void testManyEvents() {
    List<IEvent> manyEvents = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      IEvent event = Event.getBuilder()
              .subject("Event " + i)
              .startDateTime(baseDateTime.plusHours(i))
              .endDateTime(baseDateTime.plusHours(i + 1))
              .build();
      manyEvents.add(event);
    }

    LocalDate date = LocalDate.of(2024, 3, 20);
    view.displayEventsForDate(date, manyEvents);

    String output = this.output.toString();
    for (int i = 0; i < 10; i++) {
      assertTrue("Should contain Event " + i, output.contains("Event " + i));
    }
  }

  @Test
  public void testCompleteWorkflow() {
    // Simulate a complete user interaction
    view.displayPrompt();
    view.displayMessage("Creating events...");

    List<IEvent> events = createSampleEvents();
    view.displayEventsForDate(LocalDate.of(2024, 3, 20), events);

    view.displayStatus("2024-03-20T10:30", true);
    view.displayStatus("2024-03-20T15:30", false);

    view.displayError("Sample error occurred");
    view.displayMessage("Workflow completed successfully");

    String output = this.output.toString();
    assertTrue("Should contain all components",
            output.contains("> ") &&
                    output.contains("Creating events...") &&
                    output.contains("Events on 2024-03-20") &&
                    output.contains("Busy") &&
                    output.contains("Available") &&
                    output.contains("ERROR: Sample error occurred") &&
                    output.contains("Workflow completed successfully"));
  }

  private IEvent createBasicEvent() {
    return Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseDateTime)
            .endDateTime(endDateTime)
            .build();
  }

  private IEvent createMultiDayEvent() {
    return Event.getBuilder()
            .subject("Conference")
            .startDateTime(LocalDateTime.of(2024, 3, 20, 9, 0))
            .endDateTime(LocalDateTime.of(2024, 3, 22, 17, 0))
            .build();
  }

  private IEvent createAllDayEvent() {
    return Event.getBuilder()
            .subject("Holiday")
            .startDateTime(LocalDateTime.of(2024, 3, 20, 8, 0))
            .endDateTime(LocalDateTime.of(2024, 3, 20, 17, 0))
            .build();
  }

  private List<IEvent> createSampleEvents() {
    IEvent meeting = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseDateTime)
            .endDateTime(endDateTime)
            .build();

    IEvent lunch = Event.getBuilder()
            .subject("Lunch")
            .startDateTime(LocalDateTime.of(2024, 3, 20, 12, 0))
            .endDateTime(LocalDateTime.of(2024, 3, 20, 13, 0))
            .location(EventLocation.PHYSICAL)
            .build();

    return Arrays.asList(meeting, lunch);
  }

  /**
   * Helper class for testing IOException handling.
   * Always throws IOException when append methods are called.
   */
  private static class FailingAppendable implements Appendable {
    @Override
    public Appendable append(CharSequence csq) throws IOException {
      throw new IOException("Simulated IO failure");
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      throw new IOException("Simulated IO failure");
    }

    @Override
    public Appendable append(char c) throws IOException {
      throw new IOException("Simulated IO failure");
    }
  }
}
