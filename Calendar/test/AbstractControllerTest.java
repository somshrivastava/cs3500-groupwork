import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import calendar.controller.ICalendarController;
import calendar.model.ICalendarModel;
import calendar.view.ICalendarView;

import static org.junit.Assert.assertEquals;

/**
 * This class encompasses the implementations for testing a CalenderController.
 */
public abstract class AbstractControllerTest {

  protected ICalendarController controller;
  protected ICalendarModel model;
  protected ICalendarView view;

  @Before
  public abstract void setUp();

  protected abstract ICalendarController createController();

  // ----------------------------------------------------------------------------------------------
  // Tests for invalid commands


  // ----------------------------------------------------------------------------------------------
  // Tests for valid single commands


  // ----------------------------------------------------------------------------------------------
  // Integration tests

}