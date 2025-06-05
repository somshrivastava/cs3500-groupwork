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
    output.append(message);
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

  /**
   * Displays a success message to the user.
   *
   * @param message the success message to display
   */
  @Override
  public void displaySuccess(String message) {
    String msg = "Success: " + message;
    output.append(msg);
  }

  /**
   * Displays a list of events with a header.
   *
   * @param header the header text for the event list
   * @param events the list of events to display
   */
  @Override
  public void displayEvents(String header, List<IEvent> events) {
    String msg = "Success: ";
    output.append(msg);
  }

  /**
   * Displays the current status of a specific date/time.
   *
   * @param dateTime the date/time to check
   * @param isBusy   true if the time is busy (has an event), false otherwise
   */
  @Override
  public void displayStatus(String dateTime, boolean isBusy) {

  }

  /**
   * Displays the command prompt.
   */
  @Override
  public void displayPrompt() {
    String msg = "Success: ";
    output.append(msg);
  }
} 