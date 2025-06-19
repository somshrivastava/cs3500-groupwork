package controller;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

import calendar.controller.ControllerGUI;
import calendar.controller.Features;
import calendar.controller.ICalendarController;
import calendar.model.Event;
import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.model.ISmartCalendarModel;
import calendar.view.ICalendarViewGUI;

import static org.junit.Assert.assertEquals;

/**
 * Tester for a ControllerGUI object.
 */
public class ControllerGUITest {
  private Features controller;
  private ICalendarModel model;
  private ICalendarViewGUI view;

  private String setUpLog;
  private String setUpManagerLog;
  private String updateCalManagerLog;

  @Before
  public void setUp() {
    StringBuilder logModel = new StringBuilder();
    StringBuilder logView = new StringBuilder();
    //model = new MockCalendarModel(logModel);
    MockCalendarManager manager = new MockCalendarManager(logModel);
    //view = new MockCalendarView(logView);

    // Set up a mock calendar in the manager so event operations work
    MockSmartCalendarModel mockCalendar = new MockSmartCalendarModel(logModel);
    manager.setCurrentCalendar(mockCalendar);
    manager = new MockCalendarManager(logModel);
    view = new MockViewGUI(logView);
    controller = new ControllerGUI(manager);
    // give controller the mock view
    ((ControllerGUI) controller).setView(view);

    ((MockCalendarManager) manager).setCurrentCalendar(mockCalendar);

    YearMonth now = YearMonth.now();
    LocalDateTime startOfMonth = now.atDay(1).atStartOfDay();
    LocalDateTime endOfMonth = now.atEndOfMonth().atStartOfDay();

    setUpLog = "Features added\n" + "Keyboard shortcut made for pressed LEFT with previousMonth\n"
            + "Keyboard shortcut made for pressed RIGHT with nextMonth\n"
            + "Keyboard shortcut made for pressed S with showSchedule\n"
            + "Current month updated to " + YearMonth.now() + "\n"
            + "Calendar updated to: Default Calendar\n"
            + "Updated event data: \n";
    setUpManagerLog = "Created calendar Default Calendar with timezone " + ZoneId.systemDefault() +
            "Switched to calendar Default Calendar";
    StringBuilder managerLog = new StringBuilder();
    for (LocalDateTime date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
      managerLog.append("Queried for all events that occur on ").append(date);
    }
    setUpManagerLog = setUpManagerLog + managerLog;
    updateCalManagerLog = managerLog.toString();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullManager() {
    controller = new ControllerGUI(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullView() {
    ((ControllerGUI) controller).setView(null);
  }

  @Test
  public void testSetView() {
    String out = "Features added\n" + "Keyboard shortcut made for pressed LEFT with previousMonth\n"
            + "Keyboard shortcut made for pressed RIGHT with nextMonth\n"
            + "Keyboard shortcut made for pressed S with showSchedule\n"
            + "Current month updated to " + YearMonth.now() + "\n"
            + "Calendar updated to: Default Calendar\n"
            + "Updated event data: \n";
    assertEquals(out, logView.toString());
    ((ControllerGUI) controller).setView(view);
    assertEquals(out
            + "Features added\n" + "Keyboard shortcut made for pressed LEFT with previousMonth\n"
            + "Keyboard shortcut made for pressed RIGHT with nextMonth\n"
            + "Keyboard shortcut made for pressed S with showSchedule\n"
            + "Current month updated to " + YearMonth.now() + "\n"
            + "Calendar updated to: DefaultTestCalendar\n"
            + "Updated event data: \n", logView.toString());
  }

  @Test
  public void testExecute() {
    ((ICalendarController) controller).execute();
    assertEquals(setUpLog + "View displayed\n", logView.toString());
  }

  @Test
  public void testChangeMonthNext() {
    YearMonth month = YearMonth.of(2025, 6);
    controller.changeMonth(month, 1);
    assertEquals(setUpLog + "Current month updated to " + YearMonth.of(2025, 7) + "\n"
            + "Updated event data: \n", logView.toString());
  }

  @Test
  public void testChangeMonthPrev() {
    YearMonth month = YearMonth.of(2025, 6);
    controller.changeMonth(month, -1);
    assertEquals(setUpLog + "Current month updated to " + YearMonth.of(2025, 5) + "\n"
            + "Updated event data: \n", logView.toString());
  }

//  @Test
//  public void testChangeCalendar() {
//    //controller.changeCalendar("Calendar2");
//    assertEquals(setUpLog + "Calendar updated to: Calendar2\n", logView.toString());
//    assertEquals(setUpManagerLog + "Switched to calendar Calendar2", logModel.toString());
//  }

  @Test
  public void testCreateEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 20, 11, 30);
    LocalDateTime end = LocalDateTime.of(2025, 6, 20, 13, 30);
    controller.createEvent("Class", start, end);
    assertEquals(setUpLog + "Message displayed: Event 'Class' created successfully!\n"
            + "Updated event data: \n", logView.toString());
    assertEquals(setUpManagerLog + "Created single timed event Class starting at " + start
                    + " until " + end + updateCalManagerLog,
            ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testViewEvents() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.viewEvents(date);
    assertEquals(setUpLog + "Events displayed: No events on " + date + "\n", logView.toString());
    assertEquals(setUpManagerLog + "Queried for all events that occur on " + date.atStartOfDay(),
            ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testShowScheduleView() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.showScheduleView(date);
    assertEquals(setUpLog + "Schedule view displayed: No upcoming events.\n", logView.toString());
    assertEquals(setUpManagerLog + "Got upcoming 10 events starting from " + date.atStartOfDay(),
            ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testGetCurrentMonth() {
    YearMonth month = YearMonth.of(2025, 6);
    controller.changeMonth(month, -1);
    assertEquals(setUpLog + "Current month updated to " + YearMonth.of(2025, 5)
            + "\n" + "Updated event data: \n", logView.toString());
    assertEquals(YearMonth.of(2025, 5), controller.getCurrentMonth());
  }

  @Test
  public void testGetCurrentCalendar() {
    MockSmartCalendarModel mockCalendar = new MockSmartCalendarModel(logModel, "Calendar1",
            ZoneId.of("America/New_York"));
    ((MockCalendarManager) manager).setCurrentCalendar(mockCalendar);
    assertEquals("Calendar1", controller.getCurrentCalendar());
  }

  @Test
  public void testEditEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 20, 11, 30);
    LocalDateTime end = LocalDateTime.of(2025, 6, 20, 13, 30);
    controller.editEvent("Class", start, end, "subject", "Vacation");
    assertEquals(setUpLog + "Message displayed: Event updated successfully!\n"
            + "Updated event data: \n", logView.toString());
    assertEquals(setUpManagerLog + "Edited single event's property: Class starting on " + start
                    + " until " + end + ". Changed subject to Vacation" + updateCalManagerLog,
            logModel.toString());
  }

  @Test
  public void testGetEventsForDate() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.getEventsForDate(date);
    assertEquals(setUpManagerLog + "Queried for all events that occur on " + date.atStartOfDay(),
            logModel.toString());
  }

  @Test
  public void testRequestCreateEvent() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.requestCreateEvent(date);
    assertEquals(setUpLog + "Create event dialogue shown for " + date + "\n", logView.toString());
  }

  @Test
  public void testRequestViewEvents() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.requestViewEvents(date);
    assertEquals(setUpLog + "Events shown on " + date + "\n", logView.toString());
    assertEquals(setUpManagerLog + "Queried for all events that occur on " + date.atStartOfDay(),
            logModel.toString());
  }

  @Test
  public void testRequestEditEvent() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    LocalDateTime start = LocalDateTime.of(2025, 6, 20, 11, 30);
    LocalDateTime end = LocalDateTime.of(2025, 6, 20, 13, 30);
    ISmartCalendarModel cal = manager.getCurrentCalendar();
    cal.createSingleTimedEvent("Class", start, end);
    controller.requestEditEvent(cal.findEventBySubjectAndTime("Class", start));
    assertEquals(setUpLog + "Edit event dialogue shown for Class\n", logView.toString());
  }
}
