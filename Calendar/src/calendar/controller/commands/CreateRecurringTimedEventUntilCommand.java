package calendar.controller.commands;

import calendar.model.ICalendarModel;
import calendar.model.IEvent;
import calendar.model.RecurringEvent;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CreateRecurringTimedEventUntilCommand extends CreateEventCommand {

    public CreateRecurringTimedEventUntilCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                               ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
        super(subject, startDateTime, endDateTime, weekdays, null, untilDate);
    }

    @Override
    public void execute(ICalendarModel calendarModel) {
        LocalDateTime currentDate = startDateTime;
        long durationMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);
        int seriesId = nextSeriesId++;

        while (!currentDate.isAfter(untilDate)) {
            if (weekdays.contains(currentDate.getDayOfWeek())) {
                LocalDateTime eventStart = currentDate;
                LocalDateTime eventEnd = eventStart.plus(durationMinutes, ChronoUnit.MINUTES);
                
                IEvent event = RecurringEvent.getBuilder()
                        .subject(subject)
                        .startDateTime(eventStart)
                        .endDateTime(eventEnd)
                        .seriesId(seriesId)
                        .build();
                calendarModel.createEvent(event);
            }
            currentDate = currentDate.plusDays(1);
        }
    }
} 