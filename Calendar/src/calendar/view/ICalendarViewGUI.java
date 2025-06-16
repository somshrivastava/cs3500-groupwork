package calendar.view;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import calendar.controller.Features;
import calendar.model.IEvent;

/**
 * The interface for our GUI view class
 */
public interface ICalendarViewGUI {

  /**
   * Get the string from the text field and return it
   *
   * @return the input string from text field
   */
  String getInputString();

  /**
   * Clear the text field. Note that a more general "setInputString" would work for this
   * purpose but would be incorrect. This is because the text field is not set programmatically
   * in general but input by the user.
   */
  void clearInputString();

  /**
   * Reset the focus on the appropriate part of the view that has the keyboard listener attached
   * to it, so that keyboard events will still flow through.
   */
  void resetFocus();

  /**
   * Adds the given features to this view.
   *
   * @param features the features to add to the view.
   */
  void addFeatures(Features features);

  /**
   * Updates the calendar display to reflect current state.
   */
  void updateCalendar();

  /**
   * Displays a schedule view showing events from the given start date.
   *
   * @param startDate the starting date for the schedule
   * @param events the list of events to display (up to 10)
   */
  void displayScheduleView(LocalDate startDate, List<IEvent> events);

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
}
