package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarView;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to print all events within a specified time interval.
 */
public class PrintEventsInIntervalCommand implements ICalendarCommand {
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  /**
   * Creates a new PrintEventsInIntervalCommand.
   *
   * @param startTimeStr Start time string in format "yyyy-MM-ddTHH:mm"
   * @param endTimeStr   End time string in format "yyyy-MM-ddTHH:mm"
   * @throws IllegalArgumentException if time strings are invalid
   */
  public PrintEventsInIntervalCommand(String startTimeStr, String endTimeStr) {
    try {
      this.startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      this.endTime = LocalDateTime.parse(endTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      
      if (!endTime.isAfter(startTime)) {
        throw new IllegalArgumentException("End time must be after start time");
      }
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid time format. Use yyyy-MM-ddTHH:mm");
    }
  }

  @Override
  public void execute(ICalendarModel model, ICalendarView view) {
    List<IEvent> eventsInInterval = model.getEventsInInterval(startTime, endTime);

    if (eventsInInterval.isEmpty()) {
      view.displayMessage("No events between " + 
          startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " and " +
          endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      return;
    }

    StringBuilder output = new StringBuilder();
    output.append("Events between ")
          .append(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
          .append(" and ")
          .append(endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
          .append(":\n");
    
    for (IEvent event : eventsInInterval) {
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