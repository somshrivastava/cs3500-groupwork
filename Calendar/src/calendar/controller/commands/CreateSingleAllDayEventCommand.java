package calendar.controller.commands;

import calendar.model.ICalendarModel;

import java.time.LocalDateTime;

public class CreateSingleAllDayEventCommand extends CreateEventCommand {
  public CreateSingleAllDayEventCommand(String subject, LocalDateTime date) {
    super(subject, date, null, null, null, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    LocalDateTime startOfDay = startDateTime.toLocalDate().atTime(8, 0);
    LocalDateTime endOfDay = startDateTime.toLocalDate().atTime(17, 0);

    IEvent event = SingleEvent.getBuilder()
            .subject(subject)
            .startDateTime(startOfDay)
            .endDateTime(endOfDay)
            .build();
    calendarModel.createEvent(event);
  }
} 