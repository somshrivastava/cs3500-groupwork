package calendar.controller;

import java.util.Scanner;

/**
 * This class represents implementations of a controller of a calendar application.
 */
public abstract class AbstractController implements ICalendarController {

  /**
   * Takes a command line and parses it, creating the corresponding command or throwing an exception.
   *
   * @param commandLine the line to parse
   * @return a calendar command corresponding the command
   */
  protected void parseCommand(String commandLine) {
    Scanner scanner = new Scanner(commandLine);

    String action = scanner.next();

    switch (action) {
      case "create":
        break;
      case "edit":
        break;
      case "print":
        break;
      case "show":
        break;
      default:
        break;
    }
  }
}
