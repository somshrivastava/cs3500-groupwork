package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import calendar.model.IEvent;
import calendar.view.ICalendarView;

/**
 * This class is a mock CalendarView used for testing the CalendarController.
 */
public class MockCalendarView implements ICalendarView {
  private final StringBuilder output;

  public MockCalendarView(StringBuilder output) {
    this.output = output;
  }

  /**
   * Displays a general message to the user.
   *
   * @param message the message to display
   */
  @Override
  public void displayMessage(String message) {
    String msg = "Message displayed: " + message + "\n";
    output.append(msg);
  }

  /**
   * Displays an error message to the user.
   *
   * @param error the error message to display
   */
  @Override
  public void displayError(String error) {
    String msg = "Error: " + error + "\n";
    output.append(msg);
  }

  /**
   * Display the events for a single date.
   *
   * @param date the date of the events
   * @param events the list of events to display
   */
  @Override
  public void displayEventsForDate(LocalDate date, List<IEvent> events) {
    output.append("Events on ").append(date.toString()).append("\n");
  }

  /**
   * Display the events between a date range.
   *
   * @param startDate the start date of the range
   * @param endDate the end date of the range
   * @param events the list of events to display
   */
  @Override
  public void displayEventsForDateRange(LocalDateTime startDate, LocalDateTime endDate,
                                        List<IEvent> events) {
    output.append("Events from ").append(startDate.toString()).append(" to ")
            .append(endDate.toString()).append("\n");
  }

  /**
   * Displays the prompt in the command line.
   */
  @Override
  public void displayPrompt() {
    output.append("> \n");
  }

  /**
   * Displays the status of the event.
   *
   * @param dateTime the date/time to check
   * @param isBusy true if the time is busy (has an event), false otherwise
   */
  @Override
  public void displayStatus(String dateTime, boolean isBusy) {
    String msg = dateTime + "is busy: " + isBusy + "\n";
    output.append(msg);
  }
} 