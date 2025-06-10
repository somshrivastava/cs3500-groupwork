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
    return new InteractiveController(manager, view, input);
  }

  protected void convertStringInput(String s) {
    input = new StringReader(s);
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for constructor validation

  @Test(expected = IllegalArgumentException.class)
  public void testNullReadable() {
    new InteractiveController(manager, view, null);
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for exit command variations

  @Test
  public void testExitCommandCaseSensitive() {
    convertStringInput("EXIT\n");
    controller = createController();
    controller.execute();

    assertEquals("", logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive" +
            " Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'exit'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n", logView.toString());
  }

  @Test
  public void testExitWithTrailingText() {
    convertStringInput("exit now\n");
    controller = createController();
    controller.execute();

    assertEquals("", logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive" +
            " Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'exit'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n", logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for input edge cases

  @Test
  public void testEmptyInputLines() {
    convertStringInput("\n\n\nexit\n");
    controller = createController();
    controller.execute();

    assertEquals("", logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive " +
            "Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testWhitespaceOnlyLines() {
    convertStringInput("   \n\t\n  \t  \nexit\n");
    controller = createController();
    controller.execute();

    assertEquals("", logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive" +
            " Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testMultipleCommandsWithEmptyLines() {
    convertStringInput("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "\n\n" +
            "show status on 2024-03-20T10:30\n" +
            "   \n" +
            "exit\n");
    controller = createController();
    controller.execute();

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until " +
            "2024-03-20T11:00" +
            "Checked if there is an event during 2024-03-20T10:30";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - Interactive" +
            " Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "2024-03-20T10:30is busy: false\n" +
            "Message displayed: \n> \n" +
            "Error: Command cannot be empty. Please enter a valid command.\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testVeryLongInputLine() {
    StringBuilder longSubject = new StringBuilder("Very Long Event Name ");
    for (int i = 0; i < 100; i++) {
      longSubject.append("Word").append(i).append(" ");
    }

    convertStringInput("create event \"" + longSubject.toString().trim() + "\" from " +
            "2024-03-20T10:00 to 2024-03-20T11:00\nexit\n");
    controller = createController();
    controller.execute();

    String expectedLog = "Created single timed event " + longSubject.toString().trim() +
            " starting at 2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testInputWithoutExit() {
    convertStringInput("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00\n");
    controller = createController();
    controller.execute();

    String expectedLog = "Created single timed event Meeting starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n", logView.toString());
  }

  @Test
  public void testRapidSuccessionOfCommands() {
    convertStringInput("create event Meeting1 from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "create event Meeting2 from 2024-03-20T11:00 to 2024-03-20T12:00\n" +
            "create event Meeting3 from 2024-03-20T12:00 to 2024-03-20T13:00\n" +
            "show status on 2024-03-20T10:30\n" +
            "print events on 2024-03-20\n" +
            "exit\n");
    controller = createController();
    controller.execute();

    String expectedLog = "Created single timed event Meeting1 starting at " +
            "2024-03-20T10:00 until 2024-03-20T11:00" +
            "Created single timed event Meeting2 starting at 2024-03-20T11:00 " +
            "until 2024-03-20T12:00" +
            "Created single timed event Meeting3 starting at 2024-03-20T12:00" +
            " until 2024-03-20T13:00" +
            "Checked if there is an event during 2024-03-20T10:30" +
            "Queried for all events that occur on 2024-03-20T00:00";
    assertEquals(expectedLog, logModel.toString());
  }

  @Test
  public void testMultipleConsecutiveInvalidCommands() {
    convertStringInput("invalid1\n" +
            "invalid2\n" +
            "delete event Meeting\n" +
            "remove event Meeting\n" +
            "exit\n");
    controller = createController();
    controller.execute();

    assertEquals("", logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'invalid1'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'invalid2'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'delete'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'remove'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testMixedValidInvalidWithRecovery() {
    convertStringInput("create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "invalid command\n" +
            "show status on 2024-03-20T10:30\n" +
            "delete event Meeting\n" +
            "print events on 2024-03-20\n" +
            "exit\n");
    controller = createController();
    controller.execute();

    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 " +
            "until 2024-03-20T11:00" +
            "Checked if there is an event during 2024-03-20T10:30" +
            "Queried for all events that occur on 2024-03-20T00:00";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'invalid'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n" +
            "2024-03-20T10:30is busy: false\n" +
            "Message displayed: \n> \n" +
            "Error: Unknown command: 'delete'. Valid commands are: create, edit, print, show\n" +
            "Message displayed: \n> \n" +
            "Events on 2024-03-20\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for invalid commands

  // invalid date/time

  @Override
  public void testCreateSingleTimedEvent() {
    super.testCreateSingleTimedEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application -" +
            " Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    super.testCreateSingleAllDayEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringTimedEvent() {
    super.testCreateRecurringTimedEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application -" +
            " Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    super.testCreateRecurringTimedEventUntil();

    assertEquals("Message displayed: Welcome to the Calendar Application -" +
            " Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringAllDayEvent() {
    super.testCreateRecurringAllDayEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application -" +
            " Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    super.testCreateRecurringAllDayEventUntil();

    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testEditEvent() {
    super.testEditEvent();

    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testEditEvents() {
    super.testEditEvents();

    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testEditSeries() {
    super.testEditSeries();

    assertEquals("Message displayed: Welcome to the Calendar Application -" +
            " Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testPrintEvents() {
    super.testPrintEvents();

    assertEquals("Message displayed: Welcome to the Calendar Application -" +
            " Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \nEvents on 2025-09-01\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  @Test
  public void testShowStatus() {
    super.testShowStatus();

    assertEquals("Message displayed: Welcome to the Calendar Application - " +
            "Interactive Mode\n" +
            "Message displayed: Type 'exit' to quit\n" +
            "Message displayed: \n> \n2025-02-20T10:30is busy: false\n" +
            "Message displayed: \n> \n" +
            "Message displayed: Goodbye\n", logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for sequence of commands interactions

  @Test
  public void testInteractionsView() {
    Interaction[] interactions = new Interaction[] {
      new PrintInteraction("Message displayed: Welcome to the Calendar " +
              "Application - Interactive Mode"),
      new PrintInteraction("Message displayed: Type 'exit' to quit"),
      new PrintInteraction("Message displayed: "),
      new PrintInteraction("> "),
      new InputInteraction("create event \"Team Meeting\" from 2024-03-20T10:00 " +
              "to 2024-03-20T11:00\n"),
      new InputInteraction("not a valid command\n"),
      new PrintInteraction("Message displayed: \n> \nError: Unknown command: " +
              "'not'. Valid commands are: create, edit, print, show"),
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
    controller.execute();
    assertEquals(expectedOutput.toString(), logView.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Integration tests

}