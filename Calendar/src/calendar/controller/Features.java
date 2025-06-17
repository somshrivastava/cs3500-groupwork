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
   * Creates a new event with the specified details.
   *
   * @param eventName the name/subject of the event
   * @param startDateTime the start date and time of the event
   * @param endDateTime the end date and time of the event
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
}

