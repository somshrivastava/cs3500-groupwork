package calendar.view;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import java.time.LocalDate;

/**
 * Dialog for creating new events.
 * Extends AbstractEventDialog to eliminate duplicate code.
 */
public class CreateEventDialog extends AbstractEventDialog {
  
  // Constants specific to create dialog
  private static final int CREATE_DIALOG_HEIGHT = 250;
  private static final int DEFAULT_START_HOUR = 9;
  private static final int DEFAULT_END_HOUR = 10;
  
  // Fields specific to create dialog

  private JTextField eventNameField;
  private final LocalDate selectedDate;

  /**
   * Constructs a CreateEventDialog for the specified date.
   * 
   * @param parent the parent frame
   * @param selectedDate the date for the new event
   */
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

    // Event name
    JLabel nameLabel = new JLabel("Event Name: ");
    nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(nameLabel);
    eventNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(eventNameField);
    
    // Start date and time
    JLabel startLabel = new JLabel("Start Date & Time: ");
    startLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(startLabel);
    JPanel startPanel = DialogUtils.createDateTimePanel(startDateSpinner, startTimeSpinner);
    startPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(startPanel);
    
    // End date and time
    JLabel endLabel = new JLabel("End Date & Time: ");
    endLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(endLabel);
    JPanel endPanel = DialogUtils.createDateTimePanel(endDateSpinner, endTimeSpinner);
    endPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(endPanel);
    
    // Buttons
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