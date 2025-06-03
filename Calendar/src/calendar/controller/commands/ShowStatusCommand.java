package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Command to show busy/available status at a specific time.
 */
public class ShowStatusCommand implements ICalendarCommand {
  private final LocalDateTime time;

  /**
   * Creates a new ShowStatusCommand.
   *
   * @param timeStr Time string in format "yyyy-MM-ddTHH:mm"
   * @throws IllegalArgumentException if time string is invalid
   */
  public ShowStatusCommand(String timeStr) {
    try {
      this.time = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid time format. Use yyyy-MM-ddTHH:mm");
    }
  }

  @Override
  public void execute(ICalendarModel model, ICalendarView view) {
    String status = model.isTimeBusy(time) ? "BUSY" : "AVAILABLE";
    view.displayMessage(status);
  }
} 