package calendar.controller;

/**
 * Represents the controller interface for the calendar application.
 */
public interface ICalendarController {

  /**
   * Starts the calendar application in the specified mode with the given file.
   * The mode determines how the application will process input (interactive or file-based).
   * 
   * @param mode the mode to run the application in ("interactive" or "file")
   * @param filename the name of the file to read commands from (if applicable)
   * @throws IllegalArgumentException if the mode is invalid or the file cannot be accessed
   */
  void go(String mode, String filename) throws IllegalArgumentException;
}
