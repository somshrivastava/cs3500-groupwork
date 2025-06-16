package calendar.controller;

import java.time.YearMonth;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarViewGUI;

public class ControllerGUI implements Features, ICalendarController {
  private ICalendarManager manager; // view only model?
  private ICalendarViewGUI view;

  public ControllerGUI(ICalendarManager m) {
    manager = m;
  }

  public void setView(ICalendarViewGUI v) {
    view = v;
    //provide view with all the callbacks
    view.addFeatures(this);
  }

  @Override
  public void exitProgram() {
    System.exit(0);
  }

  @Override
  public void execute() {
    //
  }

  // where should current month / calendar being displayed be stored?

  public void changeMonth(YearMonth currentMonth, int offset) {
    currentMonth = currentMonth.plusMonths(offset);
    view.updateCalendar();
  }

  public void changeCalendar(String selectedCalendar) {
    view.updateCalendar();
  }
}
