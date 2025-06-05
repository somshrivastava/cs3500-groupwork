package controller;

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

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    super.testCreateSingleAllDayEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringTimedEvent() {
    super.testCreateRecurringTimedEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    super.testCreateRecurringTimedEventUntil();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringAllDayEvent() {
    super.testCreateRecurringAllDayEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    super.testCreateRecurringAllDayEventUntil();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testEditEvent() {
    super.testEditEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testEditEvents() {
    super.testEditEvents();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testEditSeries() {
    super.testEditSeries();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testPrintEvents() {
    super.testPrintEvents();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \nEvents on 2025-09-01\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testShowStatus() {
    super.testShowStatus();

    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n2025-02-20T10:30is busy: false\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for sequence of commands interactions

  @Test
  public void testInteractionsView() {
    Interaction[] interactions = new Interaction[]{
            new PrintInteraction("Message displayed: Welcome to the Calendar Application - Interactive Mode"),
            new PrintInteraction("Message displayed: Type 'exit' to quit"),
            new PrintInteraction("Message displayed: "),
            new PrintInteraction("> "),
            new InputInteraction("create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n"),
            new InputInteraction("not a valid command\n"),
            new PrintInteraction("Message displayed: \n> \nError: Unknown command: 'not'. Valid commands are: create, edit, print, show"),
            new InputInteraction("q"),
            new PrintInteraction("Message displayed: \n> "),
            new PrintInteraction("Message displayed: Goodbye"),
    };

    StringBuilder fakeUserInput = new StringBuilder();
    StringBuilder expectedOutput = new StringBuilder();

    for (Interaction interaction : interactions) {
      interaction.apply(fakeUserInput, expectedOutput);
    }

    input = new StringReader(fakeUserInput.toString());
    controller = createController();
    controller.go();
    assertEquals(expectedOutput.toString(), logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Integration tests

}
