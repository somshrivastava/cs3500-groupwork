package calendar.controller.commands;

import calendar.model.Event;
import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.view.ICalendarView;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CreateRecurringAllDayEventUntilDateCommand extends CreateEventCommand {
  public CreateRecurringAllDayEventUntilDateCommand(String subject, LocalDateTime startDate,
                                                    ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
    super(subject, startDate, null, weekdays, null, untilDate);
  }

  @Override
  public void execute(ICalendarModel calendarModel, ICalendarView calendarView) {
    LocalDateTime currentDate = startDateTime;
    int seriesId = calendarModel.getNextSeriesId();

    while (!currentDate.isAfter(untilDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime startOfDay = currentDate.toLocalDate().atTime(8, 0);
        LocalDateTime endOfDay = currentDate.toLocalDate().atTime(17, 0);

        calendarModel.createEvent(subject, startOfDay, endOfDay, seriesId);
      }
      currentDate = currentDate.plusDays(1);
    }
  }
} 