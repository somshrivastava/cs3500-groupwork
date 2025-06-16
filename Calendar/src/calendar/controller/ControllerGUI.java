package calendar.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarViewGUI;

public class ControllerGUI implements Features, ICalendarController {
  private ICalendarManager manager; // view only model?
  private ICalendarViewGUI view;

  // current month user is viewing
  private YearMonth currMonth;
  // current date user is selecting
  private LocalDate date;

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
  public void createEvent() {
    // model.createEvent()
  }

  @Override
  public void viewEvents() {
    // retrieve events from model and have view show it
    //List<String> dayEvents = model.get;
    String eventList = dayEvents.isEmpty() ? "No events" : String.join("\n", dayEvents);
    view.showEvents(eventList);
  }
}
