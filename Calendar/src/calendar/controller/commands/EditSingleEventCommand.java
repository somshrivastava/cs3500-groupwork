package calendar.controller.commands;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Command to edit a single event's property.
 * This command will edit only the specified event, regardless of whether it's part of a series.
 */
public class EditSingleEventCommand extends EditEventCommand {
  /**
   * Constructs a new command to edit a single event.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param endDateTime the end date/time of the event to edit
   * @param property the property to edit
   * @param newValue the new value for the property
   */
  public EditSingleEventCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                              String property, String newValue) {
    super(subject, startDateTime, endDateTime, property, newValue);
  }

  @Override
  public void execute(ICalendarModel calendarModel, ICalendarView calendarView) {
    calendarModel.editEvent(subject, startDateTime, endDateTime, property, newValue);
  }
} 