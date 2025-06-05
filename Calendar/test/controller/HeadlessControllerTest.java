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
      return new HeadlessController(model, view, file);
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

  // ----------------------------------------------------------------------------------------------
  // Tests for invalid commands

  @Test
  public void testNoExitCommand() {
    String in = " ";
    convertStringInput(in);
    controller = createController();
    controller.go();
    assertEquals("", logModel.toString());
    assertEquals("Error: No exit command.\n", logView.toString());
  }
}
