package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Command to create a recurring all-day event with a specified number of occurrences.
 */
public class CreateRecurringAllDayEventWithCountCommand extends CreateEventCommand {
  /**
   * Constructs a new command to create a recurring all-day event.
   * @param subject the subject of the event
   * @param startDate the start date of the event
   * @param weekdays the weekdays on which the event should occur
   * @param count the number of occurrences
   */
  public CreateRecurringAllDayEventWithCountCommand(String subject, LocalDateTime startDate,
                                                  ArrayList<DayOfWeek> weekdays, int count) {
    super(subject, startDate, null, weekdays, count, null);
  }

  @Override
  public void execute(ICalendarModel calendarModel, ICalendarView calendarView) {
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