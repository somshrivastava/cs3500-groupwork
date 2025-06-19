package controller;

import org.junit.Before;

import calendar.controller.ICalendarController;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarViewGUI;

/**
 * Tester for a ControllerGUI object.
 */
public class ControllerGUITest {
  private ICalendarController controller;
  private ICalendarModel model;
  private ICalendarViewGUI view;

  // TODO: will need to create new mocks

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
  }
}
