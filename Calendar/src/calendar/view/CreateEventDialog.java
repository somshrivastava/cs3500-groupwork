package calendar.view;

import javax.swing.*;
import java.time.LocalDate;

/**
 * Dialog for creating new events.
 * Extends AbstractEventDialog to eliminate duplicate code.
 */
public class CreateEventDialog extends AbstractEventDialog {
  
  // Constants specific to create dialog
  private static final int CREATE_DIALOG_HEIGHT = 400;
  private static final int DEFAULT_START_HOUR = 9;
  private static final int DEFAULT_END_HOUR = 10;
  
  // Fields specific to create dialog
  private JTextField eventNameField;
  private final LocalDate selectedDate;
  
  public CreateEventDialog(JFrame parent, LocalDate selectedDate) {
    super(parent, "Create New Event", CREATE_DIALOG_HEIGHT);
    this.selectedDate = selectedDate;
    initializeDialog();
  }
  
  private void initializeDialog() {
    // Create date/time spinners using base class method
    createDateTimeSpinners(selectedDate, DEFAULT_START_HOUR, DEFAULT_END_HOUR);
    
    // Initialize event name field
    eventNameField = new JTextField(20);
    
    // Use template method to finalize dialog setup
    finalizeDialog();
  }
  
  @Override
  protected JPanel createContentPanel() {
    JPanel panel = DialogUtils.createDialogMainPanel();

    // Event name section
    panel.add(new JLabel("Event Name:"));
    panel.add(eventNameField);
    panel.add(Box.createVerticalStrut(DialogUtils.VERTICAL_STRUT_SIZE));

    // Start date and time section
    panel.add(new JLabel("Start Date & Time:"));
    panel.add(DialogUtils.createDateTimePanel(null, startDateSpinner, startTimeSpinner));
    panel.add(Box.createVerticalStrut(DialogUtils.VERTICAL_STRUT_SIZE));

    // End date and time section
    panel.add(new JLabel("End Date & Time:"));
    panel.add(DialogUtils.createDateTimePanel(null, endDateSpinner, endTimeSpinner));
    panel.add(Box.createVerticalStrut(DialogUtils.VERTICAL_STRUT_SIZE));

    // Buttons using base class methods
    JButton createButton = createConfirmButton("Create");
    JButton cancelButton = createCancelButton();
    panel.add(DialogUtils.createButtonPanel(createButton, cancelButton));

    return panel;
  }
  
  /**
   * Gets the event name entered by the user.
   * 
   * @return the event name
   */
  public String getEventName() {
    return eventNameField.getText().trim();
  }
} 