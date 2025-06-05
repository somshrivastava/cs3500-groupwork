package calendar.controller.parser;

import calendar.controller.ICommandParser;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

/**
 * Main command parser that delegates to specific command parsers.
 */
public class CommandParser implements ICommandParser {
  private static final String CREATE = "create";
  private static final String EDIT = "edit";
  private static final String PRINT = "print";
  private static final String SHOW = "show";

  private final CreateCommandParser createParser;
  private final EditCommandParser editParser;
  private final PrintCommandParser printParser;
  private final ShowCommandParser showParser;

  public CommandParser(ICalendarModel model, ICalendarView view) {
    this.createParser = new CreateCommandParser(model, view);
    this.editParser = new EditCommandParser(model, view);
    this.printParser = new PrintCommandParser(model, view);
    this.showParser = new ShowCommandParser(model, view);
  }

  @Override
  public void parse(String commandLine) throws IllegalArgumentException {
    validateCommandNotEmpty(commandLine);

    String[] commandParts = commandLine.trim().split("\\s+");
    String commandType = commandParts[0].toLowerCase();

    switch (commandType) {
      case CREATE:
        createParser.parse(commandParts);
        break;
      case EDIT:
        editParser.parse(commandParts);
        break;
      case PRINT:
        printParser.parse(commandParts);
        break;
      case SHOW:
        showParser.parse(commandParts);
        break;
      default:
        throw new IllegalArgumentException("Unknown command: '" + commandType +
                "'. Valid commands are: create, edit, print, show");
    }
  }

  // Check if the command line is empty, throw an exception if it is
  private void validateCommandNotEmpty(String commandLine) {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty. Please enter a valid command.");
    }
  }
}