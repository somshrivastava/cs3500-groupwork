package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import calendar.model.IEvent;

/**
 * Interface representing the features/callbacks that the view can request from the controller.
 * Provides high-level, application-specific events rather than low-level UI events like button
 * clicks or key presses.
 */
public interface Features {

  /**
   * Exits the program.
   */
  void exitProgram();

  /**
   * Changes the current month being displayed.
   *
   * @param currentMonth the current month being displayed
   * @param offset       the number of months to move (positive for forward, negative for backward)
   */
  void changeMonth(YearMonth currentMonth, int offset);

  /**
   * Creates a new event with the specified details.
   *
   * @param eventName     the name/subject of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime   the end date and time of the event
   */
  void createEvent(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Views events for a specific date.
   *
   * @param date the date to view events for
   */
  void viewEvents(LocalDate date);

  /**
   * Shows a schedule view starting from the specified date
   * (get first 10 events on or after specified date).
   *
   * @param startDate the starting date for the schedule view
   */
  void showScheduleView(LocalDate startDate);

  /**
   * Gets the current month being displayed.
   *
   * @return the current month
   */
  YearMonth getCurrentMonth();

  /**
   * Gets the name of the current calendar.
   *
   * @return the current calendar name
   */
  String getCurrentCalendar();

  /**
   * Edits a single event's property.
   *
   * @param eventSubject the subject of the event to edit
   * @param eventStart   the start time of the event to edit
   * @param eventEnd     the end time of the event to edit
   * @param property     the property to change (subject, start, end, description, location, status)
   * @param newValue     the new value for the property
   */
  void editEvent(String eventSubject, LocalDateTime eventStart, LocalDateTime eventEnd,
                 String property, String newValue);

  /**
   * Gets all events for a specific date with full details.
   *
   * @param date the date to get events for
   * @return list of events for the date
   */
  List<IEvent> getEventsForDate(LocalDate date);

  // High-level event requests following MVC principles

  /**
   * User requests to create a new event for the specified date.
   * This is a high-level request that the controller can handle by showing appropriate dialogs.
   *
   * @param date the date for which to create an event
   */
  void requestCreateEvent(LocalDate date);

  /**
   * User requests to view events for the specified date.
   * This is a high-level request that the controller can handle by retrieving events and showing appropriate dialogs.
   *
   * @param date the date for which to view events
   */
  void requestViewEvents(LocalDate date);

  /**
   * User requests to edit the specified event.
   * This is a high-level request that the controller can handle by showing appropriate dialogs.
   *
   * @param event the event to edit
   */
  void requestEditEvent(IEvent event);
}

