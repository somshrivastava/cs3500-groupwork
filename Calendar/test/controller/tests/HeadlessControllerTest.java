package controller.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import calendar.controller.HeadlessController;
import calendar.controller.ICalendarController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

  @Override
  public void testInvalidCommandFormat() {
    String in = " ";
    convertStringInput(in);
    controller = createController();
    try {
      controller.go();
      assertEquals("", logModel.toString());
      assertEquals("Error: No exit command.", logView.toString());
    } catch (Exception e) {
      //
    }

    // invalid date/time
    in = "create event Team Meeting from 2024-03-20T10::00 to 2024-03-20T11:00" +
            "\nexit";
    convertStringInput(in);
    controller = createController();
    controller.go();
    // command not executed
    assertEquals("", logModel.toString());
  }

  // ----------------------------------------------------------------------------------------------
  // Tests for sequence of commands interactions
}
