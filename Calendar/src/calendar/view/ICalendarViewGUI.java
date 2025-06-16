package calendar.view;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import calendar.controller.Features;

/**
 * The interface for our GUI view class
 */
public interface ICalendarViewGUI {

  /**
   * Get the string from the text field and return it
   *
   * @return
   */
  String getInputString();

  /**
   * Clear the text field. Note that a more general "setInputString" would work for this
   * purpose but would be incorrect. This is because the text field is not set programmatically
   * in general but input by the user.
   */

  void clearInputString();

  /**
   * Reset the focus on the appropriate part of the view that has the keyboard listener attached
   * to it, so that keyboard events will still flow through.
   */
  void resetFocus();

  /**
   * Adds the given features to this view.
   *
   * @param features the features to add to the view.
   */
  void addFeatures(Features features);

  void updateCalendar();
}
