import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import calendar.controller.ICalendarController;
import calendar.controller.InteractiveController;


import static org.junit.Assert.assertEquals;

/**
 * Tester for a InteractiveController object.
 */
public class InteractiveControllerTest extends AbstractControllerTest {

  protected Readable input;

  protected ICalendarController createController() {
    return new InteractiveController(model, view, input);
  }

  @Before
  public void setUp() {
    logModel = new StringBuilder();
    logView = new StringBuilder();
    model = new MockCalendarModel(logModel);
    view = new MockCalendarView(logView);
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for invalid commands

  @Test
  public void testInvalidCommandFormat() throws Exception {
    input = new StringReader(" ");
    controller = createController();
    try {
      controller.go();
    } catch (Exception e) {
      //
    }

    input = new StringReader("create event Team Meeting from 2024-03-20T10::00 to " +
            "2024-03-20T11:00");
    controller = createController();
    controller = createController();
    controller.go();
    // command not executed
    assertEquals("", logModel.toString());
  }

  // invalid date/time


  // ----------------------------------------------------------------------------------------------
  // Tests for valid single commands

  @Test
  public void testCreateSingleTimedEvent() {
    input = new StringReader("create event \"Team Meeting\" from 2024-03-20T10:00 to " +
            "2024-03-20T11:00");
    controller = createController();

    controller.go();

    assertEquals("Created single timed event Team Meeting starting at 2024-03-20T10:00 " +
            "until 2024-03-20T11:00", logModel.toString());
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    input = new StringReader("create event \"Club\" on 2025-05-19");
    controller = createController();

    controller.go();

    assertEquals("Created single all day event Club on 2025-05-19T00:00", logModel.toString());
  }

  @Test
  public void testCreateRecurringTimedEvent() {
    input = new StringReader("create event \"OOD Lecture\" from 2025-05-05T11:40 to " +
            "2025-06-20T01:20 repeats MTWR for 10 times");
    controller = createController();

    controller.go();

    assertEquals("Created recurring timed event OOD Lecture starting at " +
            "2025-05-05T11:40 until 2025-06-20T01:20 for a count of 10", logModel.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    input = new StringReader("create event \"Brunch with Friends\" from 2025-02-20T10:30 to " +
            "2025-02-20T12:45 repeats SU until 2025-06-01");
    controller = createController();

    controller.go();

    assertEquals("Created recurring timed event Brunch with Friends starting at " +
            "2025-02-20T10:30 until 2025-02-20T12:45 to the date 2025-06-01T00:00", logModel.toString());
  }

  @Test
  public void testCreateRecurringAllDayEvent() {
    input = new StringReader("create event \"Tournament\" on 2026-04-15 repeats SU for 2 times");
    controller = createController();

    controller.go();

    assertEquals("Created recurring all day event Tournament starting on the date " +
            "2026-04-15T00:00 for a count of 2", logModel.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    input = new StringReader("create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10");
    controller = createController();

    controller.go();

    assertEquals("Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
            "to the date 2026-05-10T00:00", logModel.toString());
  }

  @Test
  public void testEditEvent() {
    input = new StringReader("edit event subject Club from 2025-02-20T10:30 to 2025-02-20T10:30 with newproperty");
    controller = createController();

    controller.go();

    assertEquals("Edited single event's property: Club starting on 2025-02-20T10:30 until " +
            "2025-02-20T10:30. Changed subject to newproperty", logModel.toString());
  }

  @Test
  public void testEditEvents() {
    input = new StringReader("create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10");
    controller = createController();

    controller.go();

    assertEquals("Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
            "to the date 2026-05-10T00:00", logModel.toString());
  }

  @Test
  public void testEditSeries() {
    input = new StringReader("create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10");
    controller = createController();

    controller.go();

    assertEquals("Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
            "to the date 2026-05-10T00:00", logModel.toString());
  }

  @Test
  public void testPrintEvents() {
    input = new StringReader("print events on 2025-09-01");
    controller = createController();

    controller.go();

    assertEquals("Queried for all events that occur on 2025-09-01T00:00", logModel.toString());
  }

  @Test
  public void testShowStatus() {
    input = new StringReader("show status on 2025-02-20T10:30");
    controller = createController();

    controller.go();

    assertEquals("Checked if there is an event during 2025-02-20T10:30", logModel.toString());
  }


  // ----------------------------------------------------------------------------------------------
  // Tests for sequence of commands interactions

  @Test
  public void testInteractionsModel() {
    Interaction[] interactions = new Interaction[]{
            new InputInteraction("create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n"),
            new PrintInteraction("Created single timed event Team Meeting starting at " +
                    "2024-03-20T10:00 until 2024-03-20T11:00"),
            new InputInteraction("+ 5 7\n"),
            new PrintInteraction("12"),
            new InputInteraction("q\n")
    };

    StringBuilder fakeUserInput = new StringBuilder();
    StringBuilder expectedOutput = new StringBuilder();

    for (Interaction interaction : interactions) {
      interaction.apply(fakeUserInput, expectedOutput);
    }

    input = new StringReader(fakeUserInput.toString());
    //StringBuilder actualOutput = new StringBuilder();

    //Controller controller = new Controller(model, input, actualOutput);
    controller = createController();
    controller.go();

    //assertEquals(expectedOutput.toString(), actualOutput.toString());
    assertEquals(expectedOutput.toString(), logModel.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Integration tests

}
