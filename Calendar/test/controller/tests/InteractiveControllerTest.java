package controller.tests;

import org.junit.Before;
import org.junit.Test;

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

  protected void convertStringInput(String s) {
    input = new StringReader(s);
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for invalid commands

  @Test
  public void testInvalidCommandFormat() {
    super.testInvalidCommandFormat();
  }

  // invalid date/time

  @Override
  public void testCreateSingleTimedEvent() {
    super.testCreateSingleTimedEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    super.testCreateSingleAllDayEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testCreateRecurringTimedEvent() {
    super.testCreateRecurringTimedEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    super.testCreateRecurringTimedEventUntil();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testCreateRecurringAllDayEvent() {
    super.testCreateRecurringAllDayEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    super.testCreateRecurringAllDayEventUntil();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testEditEvent() {
    super.testEditEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testEditEvents() {
    super.testEditEvents();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testEditSeries() {
    super.testEditSeries();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > " +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testPrintEvents() {
    super.testPrintEvents();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > Events on 2025-09-01\n" +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  @Test
  public void testShowStatus() {
    super.testShowStatus();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode" +
            "Message displayed: Type 'exit' to quit" +
            "Message displayed: > 2025-02-20T10:30is busy: false" +
            "Message displayed: > " +
            "Message displayed: Goodbye", logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for sequence of commands interactions

  @Test
  public void testInteractionsModel() {
    Interaction[] interactions = new Interaction[]{
            new InputInteraction("create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n"),
            new PrintInteraction("Created single timed event Team Meeting starting at " +
                    "2024-03-20T10:00 until 2024-03-20T11:00"),
            new InputInteraction("not a valid command\n"),
            //new PrintInteraction(""),
            new InputInteraction("q")
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
