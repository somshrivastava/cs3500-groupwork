package calendar.controller;

import java.io.InputStream;

import calendar.controller.parser.ICommandFactory;
import calendar.controller.parser.SmartCommandParserFactory;
import calendar.view.ICalendarView;

/**
 * This class represents the controller of a calendar application.
 * This controller offers a simple text interface in which it reads off commands from the user and
 * executes the file instructions.
 */
public class SmartController extends AbstractController {
  private final ICalendarManager manager;
  private final ICalendarController controller;

  /**
   * Create a controller to work with the specified controller and view.
   *
   * @param controller the appropriate controller to manage commands for an individual calendar
   * @param view       the calendar view where results are displayed
   */
  public SmartController(ICalendarController controller, ICalendarView view) {
    super(null, view); // leave as null?
    if ((controller == null) || (view == null)) {
      throw new IllegalArgumentException("model, view or readable is null");
    }
    this.controller = controller;
    this.manager = new ICalendarManager();
    //super.calendarView = view;
  }

  @Override
  protected ICommandFactory createFactory() {
    return new SmartCommandParserFactory(this.manager, this.calendarView);
  }

  /**
   * Starts the calendar application in the specified mode.
   * The mode determines how the application will process input (interactive or file-based).
   */
  @Override
  public void execute() {
    this.controller.execute();
  }
}
