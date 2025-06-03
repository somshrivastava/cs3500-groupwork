package calendar.controller.commands;

import calendar.model.ICalendarModel;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CreateRecurringTimedEventWithCountCommand extends CreateEventCommand {
  public CreateRecurringTimedEventWithCountCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                   ArrayList<DayOfWeek> weekdays, int count) {
    super(subject, startDateTime, endDateTime, weekdays, count, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel) {
    LocalDateTime currentDate = startDateTime;
    int eventsCreated = 0;
    long durationMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    int seriesId = calendarModel.getNextSeriesId();

    while (eventsCreated < count) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime eventStart = currentDate;
        LocalDateTime eventEnd = eventStart.plus(durationMinutes, ChronoUnit.MINUTES);

        calendarModel.createEvent(subject, eventStart, eventEnd, seriesId);
        eventsCreated++;
      }
      currentDate = currentDate.plusDays(1);
    }
  }
} 