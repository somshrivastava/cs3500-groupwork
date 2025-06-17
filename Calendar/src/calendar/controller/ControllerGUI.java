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
    view.display();
  }

  public void changeMonth(YearMonth currentMonth, int offset) {
    currMonth = currentMonth.plusMonths(offset);
    // should this be how view changes gui?
    view.updateCurrentMonth(currMonth);
  }

  public void changeCalendar(String selectedCalendar) {
    // change calendar from manager
    manager.useCalendar(selectedCalendar); // manager throws exception if calendar does not exist
    view.updateCurrentCalendar(selectedCalendar);
  }

  @Override
  public void createEvent(String eventName, LocalDateTime date) {
    // have two createEvent methods?
    if (eventName == null || eventName.trim().isEmpty()) {
      view.showError("Cannot make event without a name.");
    }
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
    ICalendarModel cal = manager.getCurrentCalendar();
    // somehow get first 10 events on or after specified date
    // TODO: need a method in model to get all events ON or AFTER a specified date
    //cal.printEvents(startDate);
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
