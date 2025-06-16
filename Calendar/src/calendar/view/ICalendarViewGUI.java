package calendar.view;

import java.time.YearMonth;

import calendar.controller.Features;

/**
 * The interface for the GUI view class.
 */
public interface ICalendarViewGUI {

  /**
   * Get the string from the text field and return it.
   *
   * @return the input string from text field
   */
  String getInputString();

  /**
   * Clear the text field.
   */
  void clearInputString();

  /**
   * Reset the focus on the appropriate part of the view that has the keyboard listener attached
   * to it, so that keyboard events will work.
   */
  void resetFocus();

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
}
