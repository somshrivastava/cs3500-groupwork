package calendar.controller.parser;

import calendar.controller.parser.ICommandParser;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Main command parser that delegates to specific command parsers.
 */
public class CommandParserFactory {
  private static final String CREATE = "create";
  private static final String EDIT = "edit";
  private static final String PRINT = "print";
  private static final String SHOW = "show";

  private final ICalendarModel model;
  private final ICalendarView view;

  public CommandParserFactory(ICalendarModel model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }

  public ICommandParser createParser(String commandLine) throws IllegalArgumentException {
    validateCommandNotEmpty(commandLine);
    ICommandParser parser;

    String[] commandParts = commandLine.trim().split("\\s+");
    String commandType = commandParts[0].toLowerCase();
    //String commandType = commandLine.trim().substring(0, 0).toLowerCase();

    switch (commandType) {
      case CREATE:
        parser = new CreateCommandParser(model, view);
        break;
      case EDIT:
        parser = new EditCommandParser(model, view);
        break;
      case PRINT:
        parser = new PrintCommandParser(model, view);
        break;
      case SHOW:
        parser = new ShowCommandParser(model, view);
        break;
      default:
        throw new IllegalArgumentException("Unknown command: '" + commandType +
                "'. Valid commands are: create, edit, print, show");
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