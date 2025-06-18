package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.swing.KeyStroke;

import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.model.ISmartCalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarViewGUI;

/**
 * GUI Controller that handles high-level feature requests and provides data to the view rather
 * than allowing direct model access.
 */
public class ControllerGUI implements Features, ICalendarController {
  private final ICalendarManager manager;
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
    
    // Set up default keyboard shortcuts
    setupKeyboardShortcuts();
    
    // Initialize the view with the current month and calendar
    view.updateCurrentMonth(currMonth);

    // Initialize calendar name if there's a current calendar
    ISmartCalendarModel currentCal = manager.getCurrentCalendar();
    if (currentCal != null) {
      view.updateCurrentCalendar(currentCal.getCalendarName());
    } else {
      view.updateCurrentCalendar("Default Calendar");
    }
    
    // Provide initial event data to view
    updateViewEventData();
  }
  
  /**
   * Sets up customizable keyboard shortcuts
   */
  private void setupKeyboardShortcuts() {
    // These could be read from a configuration file for customization
    view.setHotKey(KeyStroke.getKeyStroke("LEFT"), "previousMonth");
    view.setHotKey(KeyStroke.getKeyStroke("RIGHT"), "nextMonth");
    view.setHotKey(KeyStroke.getKeyStroke("S"), "showSchedule");
  }
  
  /**
   * Updates the view with current event data.
   * This prevents the view from directly querying the model.
   */
  private void updateViewEventData() {
    try {
      ICalendarModel cal = getCurrentCalendarModel();
      Map<LocalDate, List<IEvent>> eventData = new HashMap<>();
      
      // Get events for the current month being displayed
      LocalDate startOfMonth = currMonth.atDay(1);
      LocalDate endOfMonth = currMonth.atEndOfMonth();
      
      for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
        List<IEvent> events = cal.printEvents(date.atStartOfDay());
        if (!events.isEmpty()) {
          eventData.put(date, events);
        }
      }
      
      view.updateEventData(eventData);
    } catch (Exception e) {
      view.showError("Failed to load event data: " + e.getMessage());
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
    if (validateNonNull(currentMonth, "Current month is null.")) {
      return;
    }
    currMonth = currentMonth.plusMonths(offset);
    view.updateCurrentMonth(currMonth);
    // Update event data for the new month
    updateViewEventData();
  }

  @Override
  public void createEvent(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    String validationError = validateEventCreation(eventName, startDateTime, endDateTime);
    if (validationError != null) {
      view.showError(validationError);
      return;
    }

    try {
      ICalendarModel cal = getCurrentCalendarModel();
      cal.createSingleTimedEvent(eventName, startDateTime, endDateTime);
      view.showMessage("Event '" + eventName + "' created successfully!");
      updateViewEventData();
    } catch (Exception e) {
      view.showError("Failed to create event: " + e.getMessage());
    }
  }

  @Override
  public void viewEvents(LocalDate date) {
    if (validateNonNull(date, "Date to view event is null")) {
      return;
    }
    ICalendarModel cal = getCurrentCalendarModel();
    List<IEvent> dayEvents = cal.printEvents(date.atStartOfDay());

    view.showEvents(eventsListToString(dayEvents, date));
  }

  @Override
  public void showScheduleView(LocalDate startDate) {
    if (validateNonNull(startDate, "Date to show schedule view is null")) {
      return;
    }
    ICalendarModel cal = getCurrentCalendarModel();
    List<IEvent> events = cal.getUpcomingEvents(startDate.atStartOfDay(), 10);
    view.displayScheduleView(scheduleEventsToString(events));
  }

  @Override
  public YearMonth getCurrentMonth() {
    return this.currMonth;
  }

  @Override
  public String getCurrentCalendar() {
    return manager.getCurrentCalendar().getCalendarName();
  }

  @Override
  public void editEvent(String eventSubject, LocalDateTime eventStart, LocalDateTime eventEnd, 
                        String property, String newValue) {
    String validationError = validateEventEdit(eventSubject, eventStart, eventEnd, property, newValue);
    if (validationError != null) {
      view.showError(validationError);
      return;
    }
    
    try {
      ICalendarModel cal = getCurrentCalendarModel();
      cal.editEvent(eventSubject, eventStart, eventEnd, property, newValue);
      view.showMessage("Event updated successfully!");
      updateViewEventData();
    } catch (Exception e) {
      view.showError("Failed to edit event: " + e.getMessage());
    }
  }

  @Override
  public List<IEvent> getEventsForDate(LocalDate date) {
    if (validateNonNull(date, "Date to get events is null")) {
      return List.of();
    }
    
    try {
      ICalendarModel cal = getCurrentCalendarModel();
      return cal.printEvents(date.atStartOfDay());
    } catch (Exception e) {
      view.showError("Failed to get events: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public void requestCreateEvent(LocalDate date) {
    if (validateNonNull(date, "Cannot create event for null date.")) {
      return;
    }
    
    // Controller handles the request by showing the appropriate view dialog
    view.showCreateEventDialog(date);
  }
  
  @Override
  public void requestViewEvents(LocalDate date) {
    if (validateNonNull(date, "Cannot view events for null date.")) {
      return;
    }
    
    try {
      // Controller retrieves the data and provides it to the view
      List<IEvent> events = getEventsForDate(date);
      view.showEventsForDate(date, events);
    } catch (Exception e) {
      view.showError("Failed to retrieve events: " + e.getMessage());
    }
  }
  
  @Override
  public void requestEditEvent(IEvent event) {
    if (validateNonNull(event, "Cannot edit null event.")) {
      return;
    }
    
    // Controller handles the request by showing the appropriate view dialog
    view.showEditEventDialog(event);
  }

  // Helper methods to eliminate duplicate code

  /**
   * Gets the current calendar model with consistent error handling.
   */
  private ICalendarModel getCurrentCalendarModel() {
    return manager.getCurrentCalendar();
  }

  /**
   * Validates that an object is not null and shows error if it is.
   */
  private boolean validateNonNull(Object obj, String errorMessage) {
    if (obj == null) {
      view.showError(errorMessage);
      return true;
    }
    return false;
  }


  /**
   * Formats a single event's details consistently.
   */
  private String formatEventDetails(IEvent event) {
    StringBuilder details = new StringBuilder();
    details.append(event.getSubject()).append("\n");
    details.append("Start: ").append(event.getStartDateTime()).append("\n");
    details.append("End: ").append(event.getEndDateTime()).append("\n");
    
    if (event.getDescription() != null && !event.getDescription().trim().isEmpty()) {
      details.append("Description: ").append(event.getDescription()).append("\n");
    }
    if (event.getLocation() != null) {
      details.append("Location: ").append(event.getLocation()).append("\n");
    }
    
    return details.toString();
  }

  private String scheduleEventsToString(List<IEvent> events) {
    if (events.isEmpty()) {
      return "No upcoming events.";
    }

    StringBuilder eventList = new StringBuilder();
    for (IEvent event : events) {
      eventList.append(formatEventDetails(event)).append("\n");
    }

    return eventList.toString();
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
      eventList.append(formatEventDetails(event)).append("\n");
    }

    return eventList.toString();
  }

  /**
   * Validates event creation parameters.
   *
   * @param eventName the event name to validate
   * @param startDateTime the start date/time to validate
   * @param endDateTime the end date/time to validate
   * @return null if valid, error message if invalid
   */
  private String validateEventCreation(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (eventName == null || eventName.trim().isEmpty()) {
      return "Event name cannot be empty.";
    }

    if (startDateTime == null || endDateTime == null) {
      return "Event must have both start and end times.";
    }

    if (startDateTime.isAfter(endDateTime) || startDateTime.equals(endDateTime)) {
      return "Event start time must be before end time.";
    }

    return null;
  }
  
  /**
   * Validates event editing parameters.
   * This centralizes validation logic in the controller following MVC principles.
   * 
   * @param eventSubject the original event subject
   * @param eventStart the original event start time
   * @param eventEnd the original event end time
   * @param property the property being edited
   * @param newValue the new value for the property
   * @return null if valid, error message if invalid
   */
  private String validateEventEdit(String eventSubject, LocalDateTime eventStart, LocalDateTime eventEnd, 
                                   String property, String newValue) {
    if (eventSubject == null || property == null || newValue == null) {
      return "Invalid edit parameters.";
    }
    
    // Validate based on the property being edited
    switch (property) {
      case "subject":
        if (newValue.trim().isEmpty()) {
          return "Event subject cannot be empty.";
        }
        break;
      case "start":
      case "end":
        try {
          LocalDateTime newDateTime = LocalDateTime.parse(newValue);
        } catch (Exception e) {
          return "Invalid date/time format.";
        }
        break;
      default:
        return "Unknown property: " + property;
    }
    
    return null;
  }
}
