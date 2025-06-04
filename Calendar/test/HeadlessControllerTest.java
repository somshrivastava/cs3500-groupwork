import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;

import calendar.controller.HeadlessController;
import calendar.controller.ICalendarController;

/**
 * Tester for a HeadlessController object.
 */
public class HeadlessControllerTest extends AbstractControllerTest {
  File file;

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  protected ICalendarController createController() {
    try {
      return new HeadlessController(model, view, file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setUp() {
    model = new MockCalendarModel(new StringBuilder());
    view = new MockCalendarView(new StringBuilder());
    //file = new File();
    //controller = new HeadlessController(model, view, file);
  }
}
