package calendar.controller.commands;

import calendar.model.ICalendarModel;

import java.time.LocalDateTime;

public class CreateSingleTimedEventCommand extends CreateEventCommand {
  public CreateSingleTimedEventCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    super(subject, startDateTime, endDateTime, null, null, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    calendarModel.createEvent(subject, startDateTime, endDateTime, null);
  }
} 