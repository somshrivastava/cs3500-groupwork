package calendar.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalendarView implements ICalendarView {
  private final Appendable out;

  public CalendarView(Appendable out) {
    this.out = out;
  }

  @Override
  public void displayMessage(String message) {
    try {
      this.out.append(message).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayError(String error) {
    try {
      this.out.append("ERROR: ").append(error).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displaySuccess(String message) {
    try {
      this.out.append("SUCCESS: ").append(message).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayEvent(String subject, ArrayList<String> details) {
    try {
      this.out.append("Event: ").append(subject).append("\n");
      for (String detail : details) {
        this.out.append("  ").append(detail).append("\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayEventList(String header, ArrayList<String> eventLines) {
    try {
      this.out.append(header).append("\n");
      this.out.append("-".repeat(header.length())).append("\n");

      for (String eventLine : eventLines) {
        this.out.append(eventLine).append("\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayStatus(String dateTime, String status) {
    try {
      this.out.append("Status at ").append(dateTime).append(": ").append(status).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayPrompt() {
    try {
      this.out.append("> ");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayBlankLine() {
    try {
      this.out.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }
}
