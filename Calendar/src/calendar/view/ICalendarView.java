package calendar.view;

import calendar.model.IEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
   * Displays events for a specific date.
   * @param date the date of the events
   * @param events the list of events to display
   */
  void displayEventsForDate(LocalDate date, List<IEvent> events);

  /**
   * Displays events for a date range.
   * @param startDate the start date of the range
   * @param endDate the end date of the range
   * @param events the list of events to display
   */
  void displayEventsForDateRange(LocalDateTime startDate, LocalDateTime endDate,
                                 List<IEvent> events);

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