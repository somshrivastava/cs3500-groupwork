package calendar.controller.parser;

/**
 * Main command parser that creates different command parsers based on the given command.
 */
public interface ICommandFactory {

  /**
   * Creates a parser based on the action that the user is trying to take.
   *
   * @param commandLine the command line prompt from the user
   * @return an instance of the command parser based on what action the user is trying to take
   * @throws IllegalArgumentException if the command is unknown
   */
  public ICommandParser createParser(String commandLine);

}
