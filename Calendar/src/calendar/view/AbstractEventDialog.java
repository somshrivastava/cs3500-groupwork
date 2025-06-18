package calendar.view;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Abstract base class for event dialogs that provides common functionality
 * and eliminates duplicate code between CreateEventDialog and EditEventDialog.
 */
public abstract class AbstractEventDialog extends JDialog {
  
  // Common constants
  protected static final int DIALOG_WIDTH = 450;
  
  // Common fields
  protected boolean confirmed = false;
  protected JSpinner startDateSpinner;
  protected JSpinner startTimeSpinner;
  protected JSpinner endDateSpinner;
  protected JSpinner endTimeSpinner;
  
  /**
   * Constructor that sets up common dialog properties.
   * 
   * @param parent the parent frame
   * @param title the dialog title
   * @param height the dialog height
   */
  protected AbstractEventDialog(JFrame parent, String title, int height) {
    super(parent, title, true);
    setupDialog(height);
  }
  
  /**
   * Sets up common dialog properties.
   */
  private void setupDialog(int height) {
    setSize(DIALOG_WIDTH, height);
    setLocationRelativeTo(getParent());
  }
  
  /**
   * Creates date and time spinners for the given date and hours.
   * 
   * @param date the initial date
   * @param startHour the initial start hour
   * @param endHour the initial end hour
   */
  protected void createDateTimeSpinners(LocalDate date, int startHour, int endHour) {
    startDateSpinner = DialogUtils.createDateSpinner(date);
    startTimeSpinner = DialogUtils.createTimeSpinner(startHour);
    endDateSpinner = DialogUtils.createDateSpinner(date);
    endTimeSpinner = DialogUtils.createTimeSpinner(endHour);
  }
  
  /**
   * Creates a standard cancel button with common behavior.
   * 
   * @return the cancel button
   */
  protected JButton createCancelButton() {
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    return cancelButton;
  }
  
  /**
   * Creates a confirm button with the specified text and sets confirmed to true when clicked.
   * 
   * @param buttonText the text for the button
   * @return the confirm button
   */
  protected JButton createConfirmButton(String buttonText) {
    JButton confirmButton = new JButton(buttonText);
    confirmButton.addActionListener(e -> {
      confirmed = true;
      dispose();
    });
    return confirmButton;
  }
  
  /**
   * Returns whether the dialog was confirmed (OK/Save clicked) or cancelled.
   * 
   * @return true if confirmed, false if cancelled
   */
  public boolean isConfirmed() {
    return confirmed;
  }
  
  /**
   * Gets the start date and time from the spinners.
   * 
   * @return the start date time
   */
  public LocalDateTime getStartDateTime() {
    return DialogUtils.getDateTimeFromSpinners(startDateSpinner, startTimeSpinner);
  }
  
  /**
   * Gets the end date and time from the spinners.
   * 
   * @return the end date time
   */
  public LocalDateTime getEndDateTime() {
    return DialogUtils.getDateTimeFromSpinners(endDateSpinner, endTimeSpinner);
  }
  
  /**
   * Subclasses must implement this method to create their specific content.
   * 
   * @return the main content panel for the dialog
   */
  protected abstract JPanel createContentPanel();
  
  /**
   * Template method that sets up the dialog with common structure.
   * Subclasses should call this after creating their spinners.
   */
  protected void finalizeDialog() {
    JPanel contentPanel = createContentPanel();
    add(contentPanel);
  }
} 