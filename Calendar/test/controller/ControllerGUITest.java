package controller;

import org.junit.Before;

import calendar.controller.ICalendarController;
import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarViewGUI;

/**
 * Tester for a ControllerGUI object.
 */
public class ControllerGUITest {
  private ICalendarController controller;
  private ICalendarModel model;
  private ICalendarManager manager;
  private ICalendarViewGUI view;
  private StringBuilder logModel;
  private StringBuilder logView;

  // TODO: will need to create new mocks

  @Before
  public void setUp() {
    logModel = new StringBuilder();
    logView = new StringBuilder();
    //model = new MockCalendarModel(logModel);
    manager = new MockCalendarManager(logModel);
    //view = new MockCalendarView(logView);

    // Set up a mock calendar in the manager so event operations work
    MockSmartCalendarModel mockCalendar = new MockSmartCalendarModel(logModel);
    ((MockCalendarManager) manager).setCurrentCalendar(mockCalendar);
  }
}
