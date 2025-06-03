package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to print all events on a specific date.
 */
public class PrintEventsOnDateCommand implements ICalendarCommand {
  private final LocalDate date;

  /**
   * Creates a new PrintEventsOnDateCommand.
   *
   * @param dateStr Date string in format "yyyy-MM-dd"
   * @throws IllegalArgumentException if date string is invalid
   */
  public PrintEventsOnDateCommand(String dateStr) {
    try {
      this.date = LocalDate.parse(dateStr);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
    }
  }

  @Override
  public void execute(ICalendarModel model, ICalendarView view) {
    List<IEvent> eventsOnDate = model.getEventsOnDate(date);

    if (eventsOnDate.isEmpty()) {
      view.displayMessage("No events on " + date.format(DateTimeFormatter.ISO_LOCAL_DATE));
      return;
    }

    StringBuilder output = new StringBuilder();
    output.append("Events on ").append(date.format(DateTimeFormatter.ISO_LOCAL_DATE)).append(":\n");
    
    for (IEvent event : eventsOnDate) {
      output.append(formatEventLine(event));
    }
    
    view.displayMessage(output.toString());
  }

  /**
   * Formats a single event line with its details.
   *
   * @param event The event to format
   * @return Formatted event line
   */
  private String formatEventLine(IEvent event) {
    StringBuilder line = new StringBuilder();
    line.append(event.getSubject());
    
    if (!event.getStartDateTime().toLocalTime().equals(LocalTime.MIDNIGHT) ||
        !event.getEndDateTime().toLocalTime().equals(LocalTime.MAX)) {
      line.append(" ").append(formatTime(event.getStartDateTime()));
      line.append(" - ").append(formatTime(event.getEndDateTime()));
    }
    
    if (event.getLocation() != null) {
      line.append(" [").append(event.getLocation()).append("]");
    }
    
    if (event.getDescription() != null && !event.getDescription().isEmpty()) {
      line.append(" - ").append(event.getDescription());
    }
    
    if (event.getStatus() != null) {
      line.append(" (").append(event.getStatus()).append(")");
    }
    
    line.append("\n");
    return line.toString();
  }

  /**
   * Formats a LocalDateTime to show only the time component.
   *
   * @param dateTime The date/time to format
   * @return Formatted time string
   */
  private String formatTime(LocalDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
  }
} 