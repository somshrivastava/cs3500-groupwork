package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.model.RecurringEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CreateRecurringAllDayEventCommand extends CreateEventCommand {
  public CreateRecurringAllDayEventCommand(String subject, LocalDateTime startDate,
                                           ArrayList<DayOfWeek> weekdays, int count) {
    super(subject, startDate, null, weekdays, count, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    LocalDateTime currentDate = startDateTime;
    int eventsCreated = 0;
    int seriesId = nextSeriesId++;

    while (eventsCreated < count) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime startOfDay = currentDate.toLocalDate().atTime(8, 0);
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(17, 0);

        IEvent event = RecurringEvent.getBuilder()
                .subject(subject)
                .startDateTime(startOfDay)
                .endDateTime(endOfDay)
                .seriesId(seriesId)
                .build();
        calendarModel.createEvent(event);
        eventsCreated++;
      }
      currentDate = currentDate.plusDays(1);
    }
  }
} 