package calendar.controller.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;

import calendar.controller.ICalendarCommand;
import calendar.model.ICalendarModel;

public abstract class CreateEventCommand implements ICalendarCommand {
    protected final String subject;
    protected final LocalDateTime startDateTime;
    protected final LocalDateTime endDateTime;
    protected final ArrayList<java.time.DayOfWeek> weekdays;
    protected final Integer count;
    protected final LocalDateTime untilDate;

    protected CreateEventCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                               ArrayList<java.time.DayOfWeek> weekdays, Integer count, LocalDateTime untilDate) {
        this.subject = subject;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.weekdays = weekdays;
        this.count = count;
        this.untilDate = untilDate;
    }

    @Override
    public void execute(ICalendarModel calendarModel) {
        // calendarModel.createEvent(subject, startDateTime, endDateTime, weekdays, count, untilDate);
    }
}
