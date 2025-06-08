package calendar.controller.parser;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Factory that creates command parser objects for smart calendar commands.
 * Handles both calendar management commands and individual event commands.
 */
public class SmartCommandParserFactory implements ICommandFactory {
  private static final String CREATE = "create";
  private static final String EDIT = "edit";
  private static final String USE = "use";
  private static final String COPY = "copy";
  private static final String PRINT = "print";
  private static final String SHOW = "show";
  
  // Keywords to distinguish calendar vs event commands
  private static final String CALENDAR = "calendar";
  private static final String EVENT = "event";
  private static final String EVENTS = "events";

  private final ICalendarManager calendarManager;
  private final ICalendarView view;

  public SmartCommandParserFactory(ICalendarManager calendarManager, ICalendarView view) {
    this.calendarManager = calendarManager;
    this.view = view;
  }

  @Override
  public ICommandParser createParser(String commandLine) throws IllegalArgumentException {
    validateCommandNotEmpty(commandLine);
    
    String[] commandParts = commandLine.trim().split("\\s+");
    String commandType = commandParts[0].toLowerCase();

    switch (commandType) {
      case CREATE:
        return handleCreateCommand(commandParts);
      case EDIT:
        return handleEditCommand(commandParts);
      case USE:
        return new UseCalendarCommandParser(calendarManager, view);
      case COPY:
        return new CopyEventCommandParser(calendarManager, view);
      case PRINT:
      case SHOW:
        // Create event parser with current calendar
        return createEventParser(commandLine);
      default:
        throw new IllegalArgumentException("Unknown command: '" + commandType +
                "'. Valid commands are: create, edit, use, copy, print, show");
    }
  }

  /**
   * Handles create commands - distinguishes between calendar and event creation.
   */
  private ICommandParser handleCreateCommand(String[] commandParts) {
    if (commandParts.length < 2) {
      throw new IllegalArgumentException("Incomplete create command. " +
              "Use 'create calendar' or 'create event'");
    }
    
    String subCommand = commandParts[1].toLowerCase();
    
    if (subCommand.equals(CALENDAR)) {
      return new CreateCalendarCommandParser(calendarManager, view);
    } else if (subCommand.equals(EVENT)) {
      // Create event parser with current calendar
      return createEventParser(String.join(" ", commandParts));
    } else {
      throw new IllegalArgumentException("Invalid create command. " +
              "Use 'create calendar' or 'create event'");
    }
  }

  /**
   * Handles edit commands - distinguishes between calendar and event editing.
   */
  private ICommandParser handleEditCommand(String[] commandParts) {
    if (commandParts.length < 2) {
      throw new IllegalArgumentException("Incomplete edit command. " +
              "Use 'edit calendar' or 'edit event/events/series'");
    }
    
    String subCommand = commandParts[1].toLowerCase();
    
    if (subCommand.equals(CALENDAR)) {
      return new EditCalendarCommandParser(calendarManager, view);
    } else if (subCommand.equals(EVENT) || subCommand.equals(EVENTS) || 
               subCommand.equals("series")) {
      // Create event parser with current calendar
      return createEventParser(String.join(" ", commandParts));
    } else {
      throw new IllegalArgumentException("Invalid edit command. " +
              "Use 'edit calendar' or 'edit event/events/series'");
    }
  }

  /**
   * Creates an event parser using the current calendar from the manager.
   * This ensures we always use the most up-to-date current calendar.
   */
  private ICommandParser createEventParser(String commandLine) {
    // Get the current calendar at the time this is called
    if (calendarManager.getCurrentCalendar() == null) {
      throw new IllegalStateException("No calendar is currently in use. Use 'use calendar --name <name>' first.");
    }
    
    // Create a fresh factory with the current calendar
    CommandParserFactory eventFactory = new CommandParserFactory(
        calendarManager.getCurrentCalendar(), view);
    
    return eventFactory.createParser(commandLine);
  }

  // Check if the command line is empty, throw an exception if it is
  private void validateCommandNotEmpty(String commandLine) {
    if (commandLine == null || commandLine.trim().isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty. Please enter a valid command.");
    }
  }
}
