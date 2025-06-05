package calendar.view;

import calendar.model.IEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of the calendar view that displays information to the user.
 * This view formats and displays calendar events and messages using the provided Appendable.
 */
public class CalendarView implements ICalendarView {
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private final Appendable out;

  /**
   * Constructs a new CalendarView with the specified output destination.
   * @param out the Appendable to write output to
   */
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
      this.out.append("\nERROR: ").append(error).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displaySuccess(String message) {
    try {
      this.out.append("\nSUCCESS: ").append(message).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayEvents(String header, List<IEvent> events) {
    try {
      this.out.append("\n").append(header).append("\n");
      this.out.append("-".repeat(header.length())).append("\n");

      if (events.isEmpty()) {
        this.out.append("No events found.\n");
        return;
      }

      for (IEvent event : events) {
        StringBuilder eventLine = new StringBuilder();

        // Check if event spans multiple days
        boolean isMultiDay = !event.getStartDateTime().toLocalDate()
                .equals(event.getEndDateTime().toLocalDate());

        eventLine.append(event.getSubject()).append(" (");

        if (isMultiDay) {
          // For multi-day events, show full date and time
          eventLine.append(event.getStartDateTime().format(DATE_TIME_FORMATTER))
                  .append(" - ")
                  .append(event.getEndDateTime().format(DATE_TIME_FORMATTER));
        } else {
          // For single-day events, show date once and then times
          eventLine.append(event.getStartDateTime().toLocalDate().format(DATE_FORMATTER))
                  .append(" ")
                  .append(event.getStartDateTime().toLocalTime().format(TIME_FORMATTER))
                  .append(" - ")
                  .append(event.getEndDateTime().toLocalTime().format(TIME_FORMATTER));
        }

        eventLine.append(")");

        if (event.getLocation() != null) {
          eventLine.append(" : ").append(event.getLocation());
        }

        this.out.append(eventLine.toString()).append("\n");
      }
      this.out.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  @Override
  public void displayStatus(String dateTime, boolean isBusy) {
    try {
      this.displayMessage("");
      this.out.append(isBusy ? "Busy" : "Available").append("\n");
      this.displayMessage("");
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
}