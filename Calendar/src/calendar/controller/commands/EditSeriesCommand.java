package calendar.controller.commands;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Command to edit all events in a series.
 * If the event is not part of a series, this has the same effect as editing a single event.
 */
public class EditSeriesCommand extends EditEventCommand {
  /**
   * Constructs a new command to edit an entire series.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param property the property to edit
   * @param newValue the new value for the property
   */
  public EditSeriesCommand(String subject, LocalDateTime startDateTime,
                         String property, String newValue) {
    super(subject, startDateTime, null, property, newValue);
  }

  @Override
  public void execute(ICalendarModel calendarModel, ICalendarView calendarView) {
    calendarModel.editSeries(subject, startDateTime, property, newValue);
  }
} 