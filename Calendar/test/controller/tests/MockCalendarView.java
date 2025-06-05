package controller.tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    String msg = "Message displayed: " + message;
    output.append(msg);
  }

  /**
   * Displays an error message to the user.
   *
   * @param error the error message to display
   */
  @Override
  public void displayError(String error) {
    String msg = "Error: " + error;
    output.append(msg);
  }

  @Override
  public void displayEventsForDate(LocalDate date, List<IEvent> events) {

  }

  @Override
  public void displayEventsForDateRange(LocalDateTime startDate, LocalDateTime endDate, List<IEvent> events) {

  }

  public void displayEvent(String subject, ArrayList<String> details) {
    output.append("Event: ").append(subject).append("\n");
    for (String detail : details) {
      output.append("  ").append(detail).append("\n");
    }
  }

  public void displayEventList(String title, ArrayList<String> eventLines) {
    output.append(title).append("\n");
    for (String line : eventLines) {
      output.append(line).append("\n");
    }
  }

  public void displayStatus(String dateTime, String status) {
    output.append("Status at ").append(dateTime).append(": ").append(status).append("\n");
  }

  @Override
  public void displayPrompt() {
    output.append("> ");
  }

  public void displayBlankLine() {
    output.append("\n");
  }

  @Override
  public void displayStatus(String dateTime, boolean isBusy) {
    String msg = dateTime + "is busy: " + isBusy;
    output.append(msg);
  }
} 