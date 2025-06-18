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
  private final ICalendarManager manager; // view only model?
  private ICalendarViewGUI view;

  // current month user is viewing
  private YearMonth currMonth;

  public ControllerGUI(ICalendarManager m) {
    if (m == null) {
      throw new IllegalArgumentException("Calendar manager is null.");
    }
    manager = m;
    // Initialize current month to the current month
    currMonth = YearMonth.now();
  }

  public void setView(ICalendarViewGUI v) {
    if (v == null) {
      throw new IllegalArgumentException("View is null");
    }
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

  @Override
  public void changeMonth(YearMonth currentMonth, int offset) {
    if (currentMonth == null) {
      view.showError("Current month is null.");
      return;
    }
    currMonth = currentMonth.plusMonths(offset);
    view.updateCurrentMonth(currMonth);
  }

  @Override
  public void changeCalendar(String selectedCalendar) {
    if (selectedCalendar == null) {
      view.showError("Selected calendar name is null.");
      return;
    }
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
    if (date == null) {
      view.showError("Date to view event is null");
      return;
    }
    ICalendarModel cal = manager.getCurrentCalendar();
    List<IEvent> dayEvents = cal.printEvents(date.atStartOfDay());

    view.showEvents(eventsListToString(dayEvents, date));
  }

  @Override
  public void showScheduleView(LocalDate startDate) {
    if (startDate == null) {
      view.showError("Date to show schedule view is null");
      return;
    }
    ICalendarModel cal = manager.getCurrentCalendar();
    List<IEvent> events = cal.getUpcomingEvents(startDate.atStartOfDay(), 10);
    view.displayScheduleView(eventsListToString(events, startDate));
  }

  @Override
  public YearMonth getCurrentMonth() {
    return this.currMonth;
  }

  @Override
  public String getCurrentCalendar() {
    return manager.getCurrentCalendar().toString();
  }

  private String eventsListToString(List<IEvent> events, LocalDate date) {
    if (date == null) {
      return "Date to show events is null";
    }
    if (events.isEmpty()) {
      return "No events on " + date.toString();
    }

    StringBuilder eventList = new StringBuilder();
    eventList.append("Events for ").append(date.toString()).append(":\n\n");

    for (IEvent event : events) {
      eventList.append(event.getSubject()).append("\n");
      eventList.append("Start: ").append(event.getStartDateTime()).append("\n");
      eventList.append("End: ").append(event.getEndDateTime()).append("\n\n");
    }

    return eventList.toString();
  }
}
