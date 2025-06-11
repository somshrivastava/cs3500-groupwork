package calendar.controller.parser;

import calendar.model.CalendarModel;
import calendar.model.ICalendarManager;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Factory that creates smart command parser objects.
 */
public class SmartCommandParserFactory implements ICommandFactory {
  private static final String CREATE = "create";
  private static final String EDIT = "edit";
  private static final String USE = "use";
  private static final String COPY = "copy";

  private final ICalendarManager manager;
  private final ICalendarView view;

  public SmartCommandParserFactory(ICalendarManager manager, ICalendarView view) {
    this.manager = manager;
    this.view = view;
  }

  @Override
  public ICommandParser createParser(String commandLine) throws IllegalArgumentException {
    validateCommandNotEmpty(commandLine);
    ICommandParser parser = null;

    String[] commandParts = commandLine.trim().split("\\s+");
    String commandType = commandParts[0].toLowerCase();
    
    // Check if we have at least 2 parts before accessing commandParts[1]
    if (commandParts.length < 2) {
      // For single-word commands, try to delegate to event command factory if calendar is active
      ICalendarModel model = manager.getCurrentCalendar();
      if (model == null) {
        throw new IllegalArgumentException("No calendar is currently in use. " +
                "Use 'use calendar --name [calendar-name]' command first.");
      }
      ICommandFactory defaultFactory = new CommandParserFactory(model, view);
      parser = defaultFactory.createParser(commandLine);
      return parser;
    }
    
    String commandType2 = commandParts[1].toLowerCase();

    if (commandType.equals(USE)) {
      parser = new UseCalCommandParser(manager, view);
    }
    else if (commandType.equals(COPY)) {
      parser = new CopyCommandParser(manager, view);
    }
    else if (commandType.equals(CREATE) && commandType2.equals("calendar")) {
      parser = new CreateCalCommandParser(manager, view);
    }
    else if (commandType.equals(EDIT) && commandType2.equals("calendar")) {
      parser = new EditCalCommandParser(manager, view);
    }
    else {
      // get specific currently in use calendar from manager method
      ICalendarModel model = manager.getCurrentCalendar();
      if (model == null) {
        throw new IllegalArgumentException("No calendar is currently in use. " +
                "Use 'use calendar --name [calendar-name]' command first.");
      }
      ICommandFactory defaultFactory = new CommandParserFactory(model, view);
      parser = defaultFactory.createParser(commandLine);
    }

    return parser;
  }

  // Check if the command line is empty, throw an exception if it is
  private void validateCommandNotEmpty(String commandLine) {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty. Please enter a valid command.");
    }
  }
}