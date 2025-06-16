package controller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import calendar.controller.HeadlessController;
import calendar.controller.ICalendarController;

import static org.junit.Assert.assertEquals;

/**
 * Tester for a HeadlessController object.
 */
public class HeadlessControllerTest extends AbstractControllerTest {
  File file;
  FileWriter writer;

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  protected ICalendarController createController() {
    try {
      return new HeadlessController(manager, view, file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  protected void convertStringInput(String s) {
    // populate file
    try {
      file = folder.newFile("someTestFile.txt");
      writer = new FileWriter(file, true);
      writer.write(s);
      writer.close();
    } catch (Exception e) {
      System.out.println("File error");
    }
  }

  // Tests for constructor validation

  @Test(expected = IllegalArgumentException.class)
  public void testNullFile() throws FileNotFoundException {
    new HeadlessController(manager, view, null);
  }

  @Test(expected = FileNotFoundException.class)
  public void testNonExistentFile() throws FileNotFoundException {
    File nonExistentFile = new File("nonexistent.txt");
    new HeadlessController(manager, view, nonExistentFile);
  }

  // Tests for file content edge cases

  @Test
  public void testEmptyFile() {
    convertStringInput("");
    controller = createController();
    controller.execute();
    assertEquals("", logModel.toString());
    assertEquals("Error: No exit command.\n", logView.toString());
  }

  @Test
  public void testFileWithOnlyWhitespace() {
    convertStringInput("   \n\t\n  \n");
    controller = createController();
    controller.execute();
    assertEquals("", logModel.toString());
    assertEquals("Error: No exit command.\n", logView.toString());
  }

  @Test
  public void testFileWithOnlyExit() {
    convertStringInput("exit");
    controller = createController();
    controller.execute();
    assertEquals("", logModel.toString());
    assertEquals("", logView.toString());
  }

  @Test
  public void testFileWithOnlyQ() {
    convertStringInput("q");
    controller = createController();
    controller.execute();
    assertEquals("", logModel.toString());
    assertEquals("", logView.toString());
  }

  @Test
  public void testExitInMiddleOfFile() {
    String input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
                   "exit\n" +
                   "create event Another from 2024-03-21T10:00 to 2024-03-21T11:00";
    convertStringInput(input);
    controller = createController();
    controller.execute();
    
    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until " +
            "2024-03-20T11:00";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("", logView.toString());
  }

  @Test
  public void testMultipleExitCommands() {
    String input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
                   "exit\n" +
                   "exit";
    convertStringInput(input);
    controller = createController();
    controller.execute();
    
    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until " +
            "2024-03-20T11:00";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("", logView.toString());
  }

  @Test
  public void testExitWithWhitespace() {
    String input = "create event Meeting from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
                   "  exit  ";
    convertStringInput(input);
    controller = createController();
    controller.execute();
    
    String expectedLog = "Created single timed event Meeting starting at 2024-03-20T10:00 until " +
            "2024-03-20T11:00";
    assertEquals(expectedLog, logModel.toString());
    assertEquals("", logView.toString());
  }

  @Test
  public void testFileWithOnlyInvalidCommands() {
    String input = "invalid command\n" +
                   "another invalid\n" +
                   "delete event Meeting\n" +
                   "exit";
    convertStringInput(input);
    controller = createController();
    controller.execute();
    
    assertEquals("", logModel.toString());
    assertEquals("Error: Unknown command: 'invalid'. Valid commands are: create, edit, " +
                    "print, show\n" +
                "Error: Unknown command: 'another'. Valid commands are: create, edit, print, " +
                    "show\n" +
                "Error: Unknown command: 'delete'. Valid commands are: create, edit, " +
                    "print, show\n", 
                logView.toString());
  }

  // Tests for invalid commands

  @Test
  public void testNoExitCommand() {
    String in = " ";
    convertStringInput(in);
    controller = createController();
    controller.execute();
    assertEquals("", logModel.toString());
    assertEquals("Error: No exit command.\n", logView.toString());
  }
}
