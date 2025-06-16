package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import javax.swing.*;

import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarViewGUI;

public class ControllerGUI implements Features, ICalendarController {
  private ICalendarManager manager; // view only model?
  private ICalendarViewGUI view;

  // current month user is viewing
  private YearMonth currMonth;

  public ControllerGUI(ICalendarManager m) {
    manager = m;
  }

  public void setView(ICalendarViewGUI v) {
    view = v;
    // provide view with all the callbacks
    view.addFeatures(this);
  }

  @Override
  public void exitProgram() {
    System.exit(0);
  }

  @Override
  public void execute() {
    // how to execute?
  }

  public void changeMonth(YearMonth currentMonth, int offset) {
    currMonth = currentMonth.plusMonths(offset);
    // should this be how view changes gui?
    view.updateCalendar();
  }

  public void changeCalendar(String selectedCalendar) {
    // change calendar from manager
    manager.useCalendar(selectedCalendar);
    view.updateCalendar();
  }

  @Override
  public void createEvent(String eventName, LocalDateTime date) {
    ICalendarModel cal = manager.getCurrentCalendar();
    cal.createSingleAllDayEvent(eventName, date);
    //cal.createSingleTimedEvent();
  }

  @Override
  public void viewEvents(LocalDate date) {
    // retrieve events from model and have view show it
    ICalendarModel cal = manager.getCurrentCalendar();
    List<IEvent> dayEvents = cal.printEvents(date.atStartOfDay());
    StringBuilder eventList = new StringBuilder();
    for (IEvent e : dayEvents) {
      eventList.append(e.getSubject()).append("\n");
    }
    view.showEvents(eventList.toString());
  }

  @Override
  public void showScheduleView(LocalDate startDate) {

  }

  @Override
  public YearMonth getCurrentMonth() {
    return this.currMonth;
  }

  @Override
  public String getCurrentCalendar() {
    return manager.getCurrentCalendar().toString();
  }
}
