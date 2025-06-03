package calendar.controller.commands;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

public abstract class CreateEventCommand implements ICalendarCommand {
  protected final String subject;
  protected final LocalDateTime startDateTime;
  protected final LocalDateTime endDateTime;
  protected final ArrayList<java.time.DayOfWeek> weekdays;
  protected final Integer count;
  protected final LocalDateTime untilDate;

  protected CreateEventCommand(String subject,
                               LocalDateTime startDateTime,
                               LocalDateTime endDateTime,
                               ArrayList<DayOfWeek> weekdays,
                               Integer count,
                               LocalDateTime untilDate) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.weekdays = weekdays;
    this.count = count;
    this.untilDate = untilDate;
  }

  @Override
  public abstract void execute(ICalendarModel calendarModel, ICalendarView calendarView);
}
