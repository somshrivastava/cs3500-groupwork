package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CreateRecurringAllDayEventUntilCommand extends CreateEventCommand {
  public CreateRecurringAllDayEventUntilCommand(String subject, LocalDateTime startDate,
                                                ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    super(subject, startDate, null, weekdays, null, untilDate);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    LocalDateTime currentDate = startDateTime;
    int seriesId = nextSeriesId++;

    while (!currentDate.isAfter(untilDate)) {
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
      }
      currentDate = currentDate.plusDays(1);
    }
  }
} 