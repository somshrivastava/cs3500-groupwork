package controller;

import org.junit.Before;
import org.junit.Test;

import calendar.controller.ICalendarController;
import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

import static org.junit.Assert.assertEquals;

/**
 * This class encompasses the implementations for testing a CalenderController.
 */
public abstract class AbstractControllerTest {

  protected ICalendarController controller;
  protected ICalendarManager manager;
  protected ICalendarView view;
  protected StringBuilder logModel;
  protected StringBuilder logView;
  protected StringBuilder logManager;

  @Before
  public void setUp() {
    logModel = new StringBuilder();
    logView = new StringBuilder();
    logManager = new StringBuilder();

    manager = new MockCalendarManager(logManager);
    view = new MockCalendarView(logView);
  }

  protected abstract ICalendarController createController();

  protected abstract void convertStringInput(String s);

  @Test(expected = IllegalArgumentException.class)
  public void testNullManagerInput() {
    manager = null;
    createController();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullViewInput() {
    view = null;
    createController();
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for invalid commands

  @Test
  public void testInvalidCommandFormat() {
    String in = " \nexit";
    convertStringInput(in);
    controller = createController();
    controller.execute();
    assertEquals("", logManager.toString());

    in = "create event Team Meeting from 2024-03-20T10::00 to 2024-03-20T11:00" +
            "\nexit";
    convertStringInput(in);
    controller = createController();
    controller.execute();
    // command not executed
    assertEquals("", logManager.toString());

    // no calendar to use
  }


  // ----------------------------------------------------------------------------------------------
  // Tests for valid single commands

  @Test
  public void testCreateSingleTimedEvent() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created single timed event Team Meeting starting at 2024-03-20T10:00 " +
            "until 2024-03-20T11:00", logModel.toString());
  }

  @Test
  public void testCreateSingleAllDayEvent() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "create event \"Club\" on 2025-05-19" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created single all day event Club on 2025-05-19T00:00",
            logModel.toString());
  }

  @Test
  public void testCreateRecurringTimedEvent() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "create event \"OOD Lecture\" from 2025-05-05T11:40 to 2025-06-20T01:20 repeats " +
                    "MTWR for 10 times" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created recurring timed event OOD Lecture starting at " +
            "2025-05-05T11:40 until 2025-06-20T01:20 for a count of 10", logModel.toString());
  }

  @Test
  public void testCreateRecurringTimedEventUntil() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "create event \"Brunch with Friends\" from 2025-02-20T10:30 to 2025-02-20T12:45 " +
                    "repeats SU until 2025-06-01" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created recurring timed event Brunch with Friends starting at " +
                    "2025-02-20T10:30 until 2025-02-20T12:45 to the date 2025-06-01T00:00",
            logModel.toString());
  }

  @Test
  public void testCreateRecurringAllDayEvent() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "create event \"Tournament\" on 2026-04-15 repeats SU for 2 times" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created recurring all day event Tournament starting on the date " +
            "2026-04-15T00:00 for a count of 2", logModel.toString());
  }

  @Test
  public void testCreateRecurringAllDayEventUntil() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created recurring timed event Class starting on the date " +
            "2025-09-01T00:00 to the date 2026-05-10T00:00", logModel.toString());
  }

  @Test
  public void testEditEvent() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "edit event subject Club from 2025-02-20T10:30 to 2025-02-20T10:30 with newproperty"
                    + "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Edited single event's property: Club starting on 2025-02-20T10:30 until "
            + "2025-02-20T10:30. Changed subject to newproperty", logModel.toString());
  }

  @Test
  public void testEditEvents() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "edit events subject Club from 2025-02-20T10:30 with Class" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Edited series of event's properties: Club starting on or after date " +
            "2025-02-20T10:30. Changed subject to Class", logModel.toString());
  }

  @Test
  public void testEditSeries() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "edit series subject Club from 2025-02-20T10:30 with Class" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Edited all series of event's properties: Club with start time " +
            "2025-02-20T10:30. Changed subject to Class", logModel.toString());
  }

  @Test
  public void testPrintEvents() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "print events on 2025-09-01" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Queried for all events that occur on 2025-09-01T00:00",
            logModel.toString());
  }

  @Test
  public void testShowStatus() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in =
            "show status on 2025-02-20T10:30" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Checked if there is an event during 2025-02-20T10:30",
            logModel.toString());
  }

  @Test
  public void testCreateCalendar() {
    String in =
            "create calendar --name Calendar1 --timezone America/New_York" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Created calendar Calendar1 with timezone America/New_York",
            logManager.toString());
  }

  @Test
  public void testUseCalendar() {
    String in =
            "use calendar --name Calendar1" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Switched to calendar Calendar1",
            logManager.toString());
  }

  @Test
  public void testEditCalendar() {
    String in =
            "edit calendar --name Calendar1 --property name New_Calendar" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Edited calendar Calendar1 property name to New_Calendar",
            logManager.toString());
  }

  @Test
  public void testCopyEvent() {
    String in =
            "copy event Club on 2024-03-20T10:00 --target Calendar1 to 2024-03-20T11:00" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Copied event Club from 2024-03-20T10:00 to calendar Calendar1 at " +
                    "2024-03-20T11:00",
            logManager.toString());
  }

  @Test
  public void testCopyEventsOnDate() {
    String in =
            "copy events on 2024-03-20 --target Calendar1 to 2024-08-20" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Copied events on 2024-03-20T00:00 to calendar Calendar1 starting at " +
                    "2024-08-20T00:00",
            logManager.toString());
  }

  @Test
  public void testCopyEventsBetweenDates() {
    String in =
            "copy events between 2024-03-20 and 2024-08-20 --target Calendar1 to 2024-03-29" +
                    "\nexit";
    convertStringInput(in);
    controller = createController();

    controller.execute();

    assertEquals("Copied events between 2024-03-20T00:00 and 2024-08-20T00:00 to calendar " +
                    "Calendar1 starting at 2024-03-29T00:00",
            logManager.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for sequence commands

  @Test
  public void testSequenceCommandsValid() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "create event \"Club\" on 2025-05-19\n" +
            "create event \"OOD Lecture\" from 2025-05-05T11:40 to 2025-06-20T01:20 repeats MTWR " +
            "for 10 times\n" +
            "create event \"Brunch with Friends\" from 2025-02-20T10:30 to 2025-02-20T12:45 " +
            "repeats SU until 2025-06-01\n" +
            "create event \"Tournament\" on 2026-04-15 repeats SU for 2 times\n" +
            "create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10\n" +
            "edit event subject Club from 2025-02-20T10:30 to 2025-02-20T12:30 with Free\n" +
            "edit events subject Classes from 2025-02-20T10:30 with Free\n" +
            "edit series location Club from 2025-02-20T10:30 with Bar\n" +
            "print events on 2025-02-20\n" +
            "print events from 2025-02-20T10:30 to 2025-02-20T12:30\n" +
            "show status on 2025-02-20T12:30\n" +
            "exit";
    convertStringInput(in);
    controller = createController();

    String expectedOut =
            "Created single timed event Team Meeting starting at 2024-03-20T10:00 until " +
                    "2024-03-20T11:00" +
                    "Created single all day event Club on 2025-05-19T00:00" +
                    "Created recurring timed event OOD Lecture starting at 2025-05-05T11:40 " +
                    "until " +
                    "2025-06-20T01:20 for a count of 10" +
                    "Created recurring timed event Brunch with Friends starting at " +
                    "2025-02-20T10:30 until 2025-02-20T12:45 to the date 2025-06-01T00:00" +
                    "Created recurring all day event Tournament starting on the date " +
                    "2026-04-15T00:00 for a count of 2" +
                    "Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
                    "to the date 2026-05-10T00:00" +
                    "Edited single event's property: Club starting on 2025-02-20T10:30 until " +
                    "2025-02-20T12:30. Changed subject to Free" +
                    "Edited series of event's properties: Classes starting on or after date " +
                    "2025-02-20T10:30. Changed subject to Free" +
                    "Edited all series of event's properties: Club with start time " +
                    "2025-02-20T10:30. Changed location to Bar" +
                    "Queried for all events that occur on 2025-02-20T00:00" +
                    "Queried for all events that occur from 2025-02-20T10:30 to 2025-02-20T12:30" +
                    "Checked if there is an event during 2025-02-20T12:30";

    controller.execute();

    assertEquals(expectedOut, logModel.toString());
  }

  @Test
  public void testSequenceCommandsMix() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "create event \"Club\" on 2025-05-19\n" +
            "create event \"OOD Lecture\" from 2025-05-05T11:40 to 2025-06-20T01:20 repeats " +
            "MTWR for 10 times\n" +
            "create event \"Brunch with Friends\" from 2025-02-20T10::30 to 2025-02-20T12:45 " +
            "repeats SU until 2025-06-01\n" +
            "create event \"Tournament\" on 2026-04-15 repeats SU for 2 times\n" +
            "create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10\n" +
            "edit event subject Club from 2025-02-20T10:30 to 2025-02-20T12:30 with Free\n" +
            "edit events subject Classes from 2025-02-20T10:30 with Free\n" +
            "edit series location Club from 2025-02-20T10:30 with Bar\n" +
            "print events on 2025-02-20::00\n" +
            "print events from 2025-02-20T10:30 to 2025-02-20T12:30\n" +
            "show status on 2025-02-20T12:30\n" +
            "exit";
    convertStringInput(in);
    controller = createController();

    String expectedOut =
            "Created single timed event Team Meeting starting at 2024-03-20T10:00 until " +
                    "2024-03-20T11:00" +
                    "Created single all day event Club on 2025-05-19T00:00" +
                    "Created recurring timed event OOD Lecture starting at 2025-05-05T11:40 " +
                    "until " +
                    "2025-06-20T01:20 for a count of 10" +
                    "Created recurring all day event Tournament starting on the date " +
                    "2026-04-15T00:00 for a count of 2" +
                    "Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
                    "to the date 2026-05-10T00:00" +
                    "Edited single event's property: Club starting on 2025-02-20T10:30 until " +
                    "2025-02-20T12:30. Changed subject to Free" +
                    "Edited series of event's properties: Classes starting on or after date " +
                    "2025-02-20T10:30. Changed subject to Free" +
                    "Edited all series of event's properties: Club with start time " +
                    "2025-02-20T10:30. Changed location to Bar" +
                    "Queried for all events that occur from 2025-02-20T10:30 to 2025-02-20T12:30" +
                    "Checked if there is an event during 2025-02-20T12:30";

    controller.execute();

    assertEquals(expectedOut, logModel.toString());
  }

  @Test
  public void testExitNotAtEnd() {
    ((MockCalendarManager) manager).setCurrentCalendar(new MockSmartCalendarModel(logModel));
    String in = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "create event \"Club\" on 2025-05-19\n" +
            "create event \"OOD Lecture\" from 2025-05-05T11:40 to 2025-06-20T01:20 repeats MTWR " +
            "for 10 times\n" +
            "create event \"Brunch with Friends\" from 2025-02-20T10::30 to 2025-02-20T12:45 " +
            "repeats SU until 2025-06-01\n" +
            "create event \"Tournament\" on 2026-04-15 repeats SU for 2 times\n" +
            "create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10\n" +
            "edit event subject Club from 2025-02-20T10:30 to 2025-02-20T12:30 with Free\n" +
            "exit\n" +
            "edit events subject Classes from 2025-02-20T10:30 with Free\n" +
            "edit series location Club from 2025-02-20T10:30 with Bar\n" +
            "print events on 2025-02-20::00\n" +
            "print events from 2025-02-20T10:30 to 2025-02-20T12:30\n" +
            "show status on 2025-02-20T12:30\n";
    convertStringInput(in);
    controller = createController();

    String expectedOut =
            "Created single timed event Team Meeting starting at 2024-03-20T10:00 until " +
                    "2024-03-20T11:00" +
                    "Created single all day event Club on 2025-05-19T00:00" +
                    "Created recurring timed event OOD Lecture starting at 2025-05-05T11:40 " +
                    "until 2025-06-20T01:20 for a count of 10" +
                    "Created recurring all day event Tournament starting on the date " +
                    "2026-04-15T00:00 for a count of 2" +
                    "Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
                    "to the date 2026-05-10T00:00" +
                    "Edited single event's property: Club starting on 2025-02-20T10:30 until " +
                    "2025-02-20T12:30. Changed subject to Free";

    controller.execute();

    assertEquals(expectedOut, logModel.toString());
  }

  @Test
  public void testValidSmartCommands() {
    String in = "create calendar --name Calendar1 --timezone Asia/Kolkata\n" +
            "use calendar --name Calendar1\n" +
            "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "edit calendar --name Calendar1 --property name Calendar2\n" +
            "edit calendar --name Calendar2 --property timezone Europe/Paris\n" +
            "create event \"Club\" on 2025-05-19\n" +
            "create event \"OOD Lecture\" from 2025-05-05T11:40 to 2025-06-20T01:20 repeats MTWR " +
            "for 10 times\n" +
            "create event \"Brunch with Friends\" from 2025-02-20T10:30 to 2025-02-20T12:45 " +
            "repeats SU until 2025-06-01\n" +
            "create event \"Tournament\" on 2026-04-15 repeats SU for 2 times\n" +
            "create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10\n" +
            "edit event subject Club from 2025-02-20T10:30 to 2025-02-20T12:30 with Free\n" +
            "edit events subject Classes from 2025-02-20T10:30 with Free\n" +
            "edit series location Club from 2025-02-20T10:30 with Bar\n" +
            "print events on 2025-02-20\n" +
            "print events from 2025-02-20T10:30 to 2025-02-20T12:30\n" +
            "show status on 2025-02-20T12:30\n" +
            "exit";
    convertStringInput(in);
    controller = createController();

    String expectedManagerOut = "Created calendar Calendar1 with timezone Asia/Kolkata"
            + "Switched to calendar Calendar1"
            + "Edited calendar Calendar1 property name to Calendar2"
            + "Edited calendar Calendar2 property timezone to Europe/Paris";
    String expectedOut =
            "Created single timed event Team Meeting starting at 2024-03-20T10:00 until " +
                    "2024-03-20T11:00" +
                    "Created single all day event Club on 2025-05-19T00:00" +
                    "Created recurring timed event OOD Lecture starting at 2025-05-05T11:40 " +
                    "until " +
                    "2025-06-20T01:20 for a count of 10" +
                    "Created recurring timed event Brunch with Friends starting at " +
                    "2025-02-20T10:30 until 2025-02-20T12:45 to the date 2025-06-01T00:00" +
                    "Created recurring all day event Tournament starting on the date " +
                    "2026-04-15T00:00 for a count of 2" +
                    "Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
                    "to the date 2026-05-10T00:00" +
                    "Edited single event's property: Club starting on 2025-02-20T10:30 until " +
                    "2025-02-20T12:30. Changed subject to Free" +
                    "Edited series of event's properties: Classes starting on or after date " +
                    "2025-02-20T10:30. Changed subject to Free" +
                    "Edited all series of event's properties: Club with start time " +
                    "2025-02-20T10:30. Changed location to Bar" +
                    "Queried for all events that occur on 2025-02-20T00:00" +
                    "Queried for all events that occur from 2025-02-20T10:30 to 2025-02-20T12:30" +
                    "Checked if there is an event during 2025-02-20T12:30";

    controller.execute();

    assertEquals(expectedManagerOut, logManager.toString());
    assertEquals(expectedOut, ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testCopyCommands() {
    String in = "create calendar --name Calendar1 --timezone Asia/Kolkata\n" +
            "create calendar --name Calendar2 --timezone Europe/Paris\n" +
            "use calendar --name Calendar1\n" +
            "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
            "copy event \"Team Meeting\" on 2024-03-20T10:00 --target Calendar2 to " +
            "2024-03-20T10:00\n" +
            "create event \"Club\" on 2025-05-19\n" +
            "create event \"OOD Lecture\" from 2025-05-05T11:40 to 2025-06-20T01:20 repeats MTWR " +
            "for 10 times\n" +
            "copy events on 2025-05-05 --target Calendar2 to 2025-05-05\n" +
            "create event \"Brunch with Friends\" from 2025-02-20T10:30 to 2025-02-20T12:45 " +
            "repeats SU until 2025-06-01\n" +
            "create event \"Tournament\" on 2026-04-15 repeats SU for 2 times\n" +
            "create event \"Class\" on 2025-09-01 repeats MWRF until 2026-05-10\n" +
            "copy events between 2025-09-01 and 2026-04-20 --target Calendar2 to 2025-09-01\n" +
            "exit";
    convertStringInput(in);
    controller = createController();

    String expectedManagerOut = "Created calendar Calendar1 with timezone Asia/Kolkata"
            + "Created calendar Calendar2 with timezone Europe/Paris"
            + "Switched to calendar Calendar1"
            + "Copied event Team Meeting from 2024-03-20T10:00 to calendar Calendar2 at " +
            "2024-03-20T10:00"
            + "Copied events on 2025-05-05T00:00 to calendar Calendar2 starting at " +
            "2025-05-05T00:00"
            + "Copied events between 2025-09-01T00:00 and 2026-04-20T00:00 to calendar " +
            "Calendar2 starting at 2025-09-01T00:00";
    String expectedOut =
            "Created single timed event Team Meeting starting at 2024-03-20T10:00 until " +
                    "2024-03-20T11:00" +
                    "Created single all day event Club on 2025-05-19T00:00" +
                    "Created recurring timed event OOD Lecture starting at 2025-05-05T11:40 " +
                    "until " +
                    "2025-06-20T01:20 for a count of 10" +
                    "Created recurring timed event Brunch with Friends starting at " +
                    "2025-02-20T10:30 until 2025-02-20T12:45 to the date 2025-06-01T00:00" +
                    "Created recurring all day event Tournament starting on the date " +
                    "2026-04-15T00:00 for a count of 2" +
                    "Created recurring timed event Class starting on the date 2025-09-01T00:00 " +
                    "to the date 2026-05-10T00:00";

    controller.execute();

    assertEquals(expectedManagerOut, logManager.toString());
    assertEquals(expectedOut, ((MockCalendarManager) manager).getModelLog());
  }
}