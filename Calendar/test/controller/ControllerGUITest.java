package controller;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;

import calendar.controller.ControllerGUI;
import calendar.controller.Features;
import calendar.controller.ICalendarController;
import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarViewGUI;

import static org.junit.Assert.assertEquals;

/**
 * Tester for a ControllerGUI object.
 */
public class ControllerGUITest {
  private Features controller;
  private ICalendarModel model;
  private ICalendarManager manager;
  private ICalendarViewGUI view;
  private StringBuilder logModel;
  private StringBuilder logView;

  @Before
  public void setUp() {
    logModel = new StringBuilder();
    logView = new StringBuilder();
    //model = new MockCalendarModel(logModel);
    manager = new MockCalendarManager(logModel);
    view = new MockViewGUI(logView);
    controller = new ControllerGUI(manager);
    // give controller the mock view
    ((ControllerGUI) controller).setView(view);

    // Set up a mock calendar in the manager so event operations work
    MockSmartCalendarModel mockCalendar = new MockSmartCalendarModel(logModel);
    ((MockCalendarManager) manager).setCurrentCalendar(mockCalendar);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullManager() {
    controller = new ControllerGUI(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullView() {
    ((ControllerGUI) controller).setView(null);
  }

  @Test
  public void testSetView() {
    ((ControllerGUI) controller).setView(view);
    assertEquals("Features added\n" + "Current month updated to " + YearMonth.now() + "\n",
            logView.toString());
  }

  @Test
  public void testExecute() {
    ((ICalendarController) controller).execute();
    assertEquals("View displayed\n", logView.toString());
  }

  @Test
  public void testChangeMonthNext() {
    YearMonth month = YearMonth.of(2025, 6);
    controller.changeMonth(month, 1);
    assertEquals("Current month updated to " + Month.of(7) + "\n", logView.toString());
  }

  @Test
  public void testChangeMonthPrev() {
    YearMonth month = YearMonth.of(2025, 6);
    controller.changeMonth(month, -1);
    assertEquals("Current month updated to " + Month.of(5) + "\n", logView.toString());
  }

  @Test
  public void testChangeCalendar() {
    controller.changeCalendar("Calendar2");
    assertEquals("Calendar updated to: Calendar2\n", logView.toString());
    assertEquals("Switched to calendar Calendar2", logModel.toString());
  }

  @Test
  public void testCreateEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 20, 11, 30);
    LocalDateTime end = LocalDateTime.of(2025, 6, 20, 13, 30);
    controller.createEvent("Class", start, end);
    assertEquals("Message displayed: Event 'Class' created successfully!\n"
            + "Calendar updated\n", logView.toString());
    assertEquals("Created single timed event Class starting at " + start + " until " + end,
            ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testViewEvents() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.viewEvents(date);
    assertEquals("Events displayed: No events on" + date, logView.toString());
    assertEquals("Queried for all events that occur on " + date,
            ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testShowScheduleView() {
    LocalDate date = LocalDate.of(2025, 6, 20);
    controller.showScheduleView(date);
    assertEquals("Schedule view displayed: No events on " + date, logView.toString());
    assertEquals("Got upcoming 10 events starting from " + date,
            ((MockCalendarManager) manager).getModelLog());
  }

  @Test
  public void testGetCurrentMonth() {
    YearMonth month = YearMonth.of(2025, 6);
    controller.changeMonth(month, -1);
    assertEquals("Current month updated to " + Month.of(5) + "\n", logView.toString());
    assertEquals(YearMonth.of(2025, 5), controller.getCurrentMonth());
  }

  @Test
  public void testGetCurrentCalendar() {
    MockSmartCalendarModel mockCalendar = new MockSmartCalendarModel(logModel, "Calendar1",
            ZoneId.of("America/New_York"));
    ((MockCalendarManager) manager).setCurrentCalendar(mockCalendar);
    assertEquals("Calendar1", controller.getCurrentCalendar());
  }
}
