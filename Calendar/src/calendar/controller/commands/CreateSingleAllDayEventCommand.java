package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

import java.time.LocalDateTime;

/**
 * Command to create a single all-day event.
 */
public class CreateSingleAllDayEventCommand extends CreateEventCommand {
  /**
   * Constructs a new command to create a single all-day event.
   * @param subject the subject of the event
   * @param date the date of the event
   */
  public CreateSingleAllDayEventCommand(String subject, LocalDateTime date) {
    super(subject, date, null, null, null, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel, ICalendarView calendarView) {
    LocalDateTime startOfDay = startDateTime.toLocalDate().atTime(8, 0);
    LocalDateTime endOfDay = startDateTime.toLocalDate().atTime(17, 0);
    
    calendarModel.createEvent(subject, startOfDay, endOfDay, null);
  }
} 