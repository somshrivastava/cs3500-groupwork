package calendar.controller;

/**
 * Interface for parsing and executing calendar commands.
 * Implementations of this interface are responsible for interpreting user input
 * and delegating to the appropriate model and view operations.
 */
public interface ICommandParser {

  /**
   * Parses and executes a command line input.
   *
   * @param commandLine the command line string to parse and execute
   * @throws IllegalArgumentException if the command is invalid, malformed, or cannot be executed
   */
  void parse(String commandLine) throws IllegalArgumentException;
}