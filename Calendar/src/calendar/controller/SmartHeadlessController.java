package calendar.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import calendar.model.ICalendarManager;
import calendar.view.ICalendarView;

/**
 * Smart headless controller for calendar management.
 * This controller handles multiple calendars through a CalendarManager and reads
 * commands from a file instead of user input.
 */
public class SmartHeadlessController extends AbstractSmartController {
  private final File file;

  /**
   * Create a smart headless controller to work with the specified calendar manager,
   * view, and file.
   *
   * @param calendarManager the calendar manager to work with
   * @param view            the calendar view where results are displayed
   * @param file            the file to read commands from
   * @throws FileNotFoundException if the file does not exist or cannot be read
   */
  public SmartHeadlessController(ICalendarManager calendarManager, ICalendarView view, File file)
          throws FileNotFoundException {
    super(calendarManager, view);
    if (file == null) {
      throw new IllegalArgumentException("File cannot be null");
    } else if (!file.exists() || !file.canRead()) {
      throw new FileNotFoundException("File does not exist or cannot be read: " + file.getPath());
    }
    this.file = file;
  }

  @Override
  public void execute() {
    try (Scanner sc = new Scanner(this.file)) {
      while (sc.hasNextLine()) {
        String commandLine = sc.nextLine().trim();
        
        // Skip empty lines
        if (commandLine.isEmpty()) {
          continue;
        }
        
        // Check for exit command
        if (commandLine.equals("exit") || commandLine.equals("q")) {
          this.calendarView.displayMessage("Exit command received. Application terminated.");
          return;
        }
        
        // Execute the command
        try {
          parseCommand(commandLine);
        } catch (Exception e) {
          this.calendarView.displayError("Error executing command '" + commandLine + "': " + e.getMessage());
        }
      }
      
      // If we reach here, no exit command was found
      this.calendarView.displayError("No exit command found in file.");
      
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + e.getMessage(), e);
    }
  }
} 