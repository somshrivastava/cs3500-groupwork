package calendar.controller.commands;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Abstract base class for all event editing commands.
 * Provides common functionality and fields for editing events.
 */
public abstract class EditEventCommand implements ICalendarCommand {
  protected final String subject;
  protected final LocalDateTime startDateTime;
  protected final LocalDateTime endDateTime;
  protected final String property;
  protected final String newValue;

  /**
   * Constructs a new edit event command.
   * @param subject the subject of the event to edit
   * @param startDateTime the start date/time of the event to edit
   * @param endDateTime the end date/time of the event to edit (may be null)
   * @param property the property to edit
   * @param newValue the new value for the property
   */
  protected EditEventCommand(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                           String property, String newValue) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.property = property;
    this.newValue = newValue;
  }

  @Override
  public abstract void execute(ICalendarModel calendarModel, ICalendarView calendarView);
} 