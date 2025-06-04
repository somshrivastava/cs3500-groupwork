package calendar.view;

import calendar.model.IEvent;
import java.util.List;

/**
 * Interface for displaying calendar information to the user.
 * This view is responsible for formatting and displaying calendar events and messages.
 */
public interface ICalendarView {
  /**
   * Displays a general message to the user.
   * @param message the message to display
   */
  void displayMessage(String message);

  /**
   * Displays an error message to the user.
   * @param error the error message to display
   */
  void displayError(String error);

  /**
   * Displays a success message to the user.
   * @param message the success message to display
   */
  void displaySuccess(String message);

  /**
   * Displays a list of events with a header.
   * @param header the header text for the event list
   * @param events the list of events to display
   */
  void displayEvents(String header, List<IEvent> events);

  /**
   * Displays the current status of a specific date/time.
   * @param dateTime the date/time to check
   * @param isBusy true if the time is busy (has an event), false otherwise
   */
  void displayStatus(String dateTime, boolean isBusy);

  /**
   * Displays the command prompt.
   */
  void displayPrompt();
}