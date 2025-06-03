package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

import java.time.LocalDateTime;

/**
 * Command to create a single timed event.
 */
public class CreateSingleTimedEventCommand extends CreateEventCommand {
  /**
   * Constructs a new command to create a single timed event.
   * @param subject the subject of the event
   * @param startDateTime the start date/time of the event
   * @param endDateTime the end date/time of the event
   */
  public CreateSingleTimedEventCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    super(subject, startDateTime, endDateTime, null, null, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel, ICalendarView calendarView) {
    calendarModel.createEvent(subject, startDateTime, endDateTime, null);
  }
} 