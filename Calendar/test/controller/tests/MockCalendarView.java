package controller.tests;

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
   * Displays a success message to the user.
   *
   * @param message the success message to display
   */
  @Override
  public void displaySuccess(String message) {
    String msg = "Success: " + message + "\n";
    output.append(msg);
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
    output.append("> \n");
  }

  public void displayBlankLine() {
    output.append("\n");
  }

  /**
   * Displays a list of events with a header.
   *
   * @param header the header text for the event list
   * @param events the list of events to display
   */
  @Override
  public void displayEvents(String header, List<IEvent> events) {
    output.append(header).append("\n");
    for (IEvent e : events) {
      output.append(e).append("\n");
    }
  }

  @Override
  public void displayStatus(String dateTime, boolean isBusy) {
    String msg = dateTime + "is busy: " + isBusy + "\n";
    output.append(msg);
  }
} 