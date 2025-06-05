package calendar.controller.parser;

import java.time.LocalDateTime;

import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Parser for show status commands.
 * Checks if the user is busy at a specific time.
 */
class ShowCommandParser extends AbstractCommandParser {

  public ShowCommandParser(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void parse(String[] parts) throws IllegalArgumentException {
    if (parts.length != 4) {
      throw new IllegalArgumentException("Show status requires exactly 4 parts. " +
              "Format: show status on YYYY-MM-DDThh:mm");
    }

    // second word should be "status"
    validateKeyword(parts[1], STATUS, "'show'");
    // third word should be "on"
    validateKeyword(parts[2], ON, "'show status'");

    // fourth word is the date and time
    LocalDateTime dateTime = parseDateTime(parts[3]);
    boolean isBusy = model.showStatus(dateTime);
    view.displayStatus(parts[3], isBusy);
  }
}