package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import javax.swing.*;

import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.model.ISmartCalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarViewGUI;

public class ControllerGUI implements Features, ICalendarController {
  private ICalendarManager manager; // view only model?
  private ICalendarViewGUI view;

  // current month user is viewing
  private YearMonth currMonth;

  public ControllerGUI(ICalendarManager m) {
    manager = m;
    // Initialize current month to the current month
    currMonth = YearMonth.now();
  }

  public void setView(ICalendarViewGUI v) {
    view = v;
    // provide view with all the callbacks
    view.addFeatures(this);
    // Initialize the view with the current month and calendar
    view.updateCurrentMonth(currMonth);
    
    // Initialize calendar name if there's a current calendar
    ISmartCalendarModel currentCal = manager.getCurrentCalendar();
    if (currentCal != null) {
      view.updateCurrentCalendar(currentCal.getCalendarName());
    } else {
      view.updateCurrentCalendar("No Calendar");
    }
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
  public void createEvent(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (eventName == null || eventName.trim().isEmpty()) {
      view.showError("Cannot create event without a name.");
      return;
    }
    
    if (startDateTime == null || endDateTime == null) {
      view.showError("Cannot create event without start and end times.");
      return;
    }
    
    if (startDateTime.isAfter(endDateTime) || startDateTime.equals(endDateTime)) {
      view.showError("Event start time must be before end time.");
      return;
    }
    
    try {
      ICalendarModel cal = manager.getCurrentCalendar();
      cal.createSingleTimedEvent(eventName, startDateTime, endDateTime);
      view.showMessage("Event '" + eventName + "' created successfully!");
      // Refresh the calendar display
      view.updateCalendar();
    } catch (Exception e) {
      view.showError("Failed to create event: " + e.getMessage());
    }
  }

  @Override
  public void viewEvents(LocalDate date) {
    ICalendarModel cal = manager.getCurrentCalendar();
    List<IEvent> dayEvents = cal.printEvents(date.atStartOfDay());
    
    if (dayEvents.isEmpty()) {
      view.showEvents("No events on " + date.toString());
      return;
    }
    
    StringBuilder eventList = new StringBuilder();
    eventList.append("Events for ").append(date.toString()).append(":\n\n");
    
    for (IEvent event : dayEvents) {
      eventList.append(event.getSubject()).append("\n");
      eventList.append("Start: ").append(event.getStartDateTime()).append("\n");
      eventList.append("End: ").append(event.getEndDateTime()).append("\n\n");
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
