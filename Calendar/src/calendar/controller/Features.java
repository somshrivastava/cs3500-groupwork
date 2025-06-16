package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * Interface representing the features/callbacks that the view can request from the controller.
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
   * Changes the current calendar being used.
   *
   * @param calendarName the name of the calendar to switch to
   */
  void changeCalendar(String calendarName);

  /**
   * Creates a new event on the specified date.
   *
   * @param date the date on which to create the event
   */
  void createEvent(String eventName, LocalDateTime date);

  /**
   * Views events for a specific date.
   *
   * @param date the date to view events for
   */
  void viewEvents(LocalDate date);

  /**
   * Shows a schedule view starting from the specified date.
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
}

