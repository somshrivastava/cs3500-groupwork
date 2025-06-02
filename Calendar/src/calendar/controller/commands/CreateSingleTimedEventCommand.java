package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;

import java.time.LocalDateTime;

public class CreateSingleTimedEventCommand extends CreateEventCommand {
  public CreateSingleTimedEventCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    super(subject, startDateTime, endDateTime, null, null, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    IEvent event = SingleEvent.getBuilder()
            .subject(subject)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .build();
    calendarModel.createEvent(event);
  }
} 