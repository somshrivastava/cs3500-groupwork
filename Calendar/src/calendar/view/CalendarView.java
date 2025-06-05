package calendar.view;

import calendar.model.IEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  public void displayEventsForDate(LocalDate date, List<IEvent> events) {
    String header = "Events on " + date.format(DATE_FORMATTER);
    displayEvents(header, events);
  }

  @Override
  public void displayEventsForDateRange(LocalDateTime startDate, LocalDateTime endDate, List<IEvent> events) {
    String header = "Events from " + startDate.toLocalDate().format(DATE_FORMATTER) +
            " to " + endDate.toLocalDate().format(DATE_FORMATTER);
    displayEvents(header, events);
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

  /**
   * Displays a list of events with the given header.
   * @param header the header text for the event list
   * @param events the list of events to display
   */
  private void displayEvents(String header, List<IEvent> events) {
    try {
      displayHeader(header);

      if (events.isEmpty()) {
        this.out.append("No events found.\n");
        return;
      }

      for (IEvent event : events) {
        String eventLine = formatEvent(event);
        this.out.append("• ").append(eventLine).append("\n");
      }
      this.out.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write output", e);
    }
  }

  /**
   * Displays the header with underline for event lists.
   * @param header the header text to display
   * @throws IOException if output fails
   */
  private void displayHeader(String header) throws IOException {
    this.out.append("\n").append(header).append("\n");
    this.out.append("-".repeat(header.length())).append("\n");
  }

  /**
   * Formats a single event for display.
   * @param event the event to format
   * @return the formatted event string
   */
  private String formatEvent(IEvent event) {
    StringBuilder eventLine = new StringBuilder();

    eventLine.append(event.getSubject()).append(" (");

    if (isMultiDayEvent(event)) {
      formatMultiDayEvent(event, eventLine);
    } else {
      formatSingleDayEvent(event, eventLine);
    }

    eventLine.append(")");

    if (event.getLocation() != null) {
      eventLine.append(" : ").append(event.getLocation());
    }

    return eventLine.toString();
  }

  /**
   * Checks if an event spans multiple days.
   * @param event the event to check
   * @return true if the event spans multiple days, false otherwise
   */
  private boolean isMultiDayEvent(IEvent event) {
    return !event.getStartDateTime().toLocalDate()
            .equals(event.getEndDateTime().toLocalDate());
  }

  /**
   * Formats a multi-day event's date and time information.
   * @param event the multi-day event
   * @param eventLine the StringBuilder to append to
   */
  private void formatMultiDayEvent(IEvent event, StringBuilder eventLine) {
    eventLine.append(event.getStartDateTime().format(DATE_TIME_FORMATTER))
            .append(" - ")
            .append(event.getEndDateTime().format(DATE_TIME_FORMATTER));
  }

  /**
   * Formats a single-day event's date and time information.
   * @param event the single-day event
   * @param eventLine the StringBuilder to append to
   */
  private void formatSingleDayEvent(IEvent event, StringBuilder eventLine) {
    eventLine.append(event.getStartDateTime().toLocalDate().format(DATE_FORMATTER))
            .append(" ")
            .append(event.getStartDateTime().toLocalTime().format(TIME_FORMATTER))
            .append(" - ")
            .append(event.getEndDateTime().toLocalTime().format(TIME_FORMATTER));
  }
}