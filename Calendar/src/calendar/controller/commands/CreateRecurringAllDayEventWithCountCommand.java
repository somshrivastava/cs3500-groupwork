package calendar.controller.commands;

import calendar.model.Event;
import calendar.model.ICalendarModel;
import calendar.model.IEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CreateRecurringAllDayEventWithCountCommand extends CreateEventCommand {
  public CreateRecurringAllDayEventWithCountCommand(String subject, LocalDateTime startDate,
                                                    ArrayList<DayOfWeek> weekdays, int count) {
    super(subject, startDate, null, weekdays, count, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    LocalDateTime currentDate = startDateTime;
    int eventsCreated = 0;
    int seriesId = calendarModel.getNextSeriesId();

    while (eventsCreated < count) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime startOfDay = currentDate.toLocalDate().atTime(8, 0);
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(17, 0);

        calendarModel.createEvent(subject, startOfDay, endOfDay, seriesId);
        eventsCreated++;
      }
      currentDate = currentDate.plusDays(1);
    }
  }
} 