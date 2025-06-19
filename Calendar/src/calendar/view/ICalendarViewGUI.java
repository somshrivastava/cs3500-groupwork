package calendar.view;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;

import calendar.controller.Features;
import calendar.model.IEvent;

/**
 * The interface for the GUI view class.
 * This interface follows MVC principles by providing methods for the controller
 * to update the view without the view directly accessing the model.
 */
public interface ICalendarViewGUI {


  /**
   * Adds the given features to this view.
   *
   * @param features the features to add to the view
   */
  void addFeatures(Features features);

  /**
   * Updates the calendar display to reflect current state.
   */
  void updateCalendar();

  /**
   * Displays a schedule view with the given content.
   *
   * @param scheduleContent the formatted schedule content to display
   */
  void displayScheduleView(String scheduleContent);

  /**
   * Updates the current month display.
   *
   * @param month the month to display
   */
  void updateCurrentMonth(YearMonth month);

  /**
   * Updates the current calendar name display.
   *
   * @param calendarName the name of the current calendar
   */
  void updateCurrentCalendar(String calendarName);

  /**
   * Shows a message to the user.
   *
   * @param message the message to display
   */
  void showMessage(String message);

  /**
   * Shows an error message to the user.
   *
   * @param error the error message to display
   */
  void showError(String error);

  /**
   * Makes the view visible.
   */
  void display();

  /**
   * Shows events to the user.
   *
   * @param eventList the formatted event list to display
   */
  void showEvents(String eventList);

  // Methods supporting improved MVC design

  /**
   * Updates the event data that the view uses for display.
   *
   * @param eventData map of dates to their events for display purposes
   */
  void updateEventData(Map<LocalDate, List<IEvent>> eventData);

  /**
   * Sets up keyboard shortcuts for the application.
   *
   * @param key         the keystroke to bind
   * @param featureName the name of the feature to trigger
   */
  void setHotKey(KeyStroke key, String featureName);

  /**
   * Shows a dialog to create a new event for the specified date.
   * Called by controller in response to high-level feature requests.
   *
   * @param selectedDate the date for the new event
   */
  void showCreateEventDialog(LocalDate selectedDate);

  /**
   * Shows a dialog listing events for a specific date.
   * Called by controller in response to high-level feature requests.
   *
   * @param date   the date to show events for
   * @param events the list of events for that date
   */
  void showEventsForDate(LocalDate date, List<IEvent> events);

  /**
   * Shows a dialog to edit the specified event.
   * Called by controller in response to high-level feature requests.
   *
   * @param event the event to edit
   */
  void showEditEventDialog(IEvent event);
}
