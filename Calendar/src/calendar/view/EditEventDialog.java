package calendar.view;

import javax.swing.*;
import java.time.LocalDate;

import calendar.model.IEvent;

/**
 * Dialog for editing existing events.
 * Extends AbstractEventDialog to eliminate duplicate code.
 */
public class EditEventDialog extends AbstractEventDialog {
  
  // Constants specific to edit dialog
  private static final int EDIT_DIALOG_HEIGHT = 300;
  private static final String[] EDIT_PROPERTIES = {"subject", "start", "end"};
  
  // Fields specific to edit dialog
  private JComboBox<String> propertyCombo;
  private JTextField subjectField;
  private JPanel inputPanel;
  private final IEvent event;
  
  public EditEventDialog(JFrame parent, IEvent event) {
    super(parent, "Edit Event", EDIT_DIALOG_HEIGHT);
    this.event = event;
    initializeDialog();
  }
  
  private void initializeDialog() {
    // Create date/time spinners using base class method
    createDateTimeSpinners(
        event.getStartDateTime().toLocalDate(),
        event.getStartDateTime().getHour(),
        event.getEndDateTime().getHour()
    );
    
    propertyCombo = new JComboBox<>(EDIT_PROPERTIES);
    subjectField = new JTextField(event.getSubject());
    inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
    
    // Set up property change listener
    propertyCombo.addActionListener(e -> updateInputPanel());
    
    // Use template method to finalize dialog setup
    finalizeDialog();
    
    // Initialize input panel after dialog is set up
    updateInputPanel();
  }
  
  @Override
  protected JPanel createContentPanel() {
    JPanel mainPanel = DialogUtils.createDialogMainPanel();
    
    // Event info section
    mainPanel.add(new JLabel("Event: " + event.getSubject()));
    mainPanel.add(new JLabel("Time: " + event.getStartDateTime().toLocalTime() + 
                            " - " + event.getEndDateTime().toLocalTime()));
    mainPanel.add(Box.createVerticalStrut(DialogUtils.VERTICAL_STRUT_SIZE));
    
    // Property selection section
    mainPanel.add(new JLabel("Edit:"));
    mainPanel.add(propertyCombo);
    mainPanel.add(Box.createVerticalStrut(DialogUtils.VERTICAL_STRUT_SIZE));
    
    // Dynamic input section
    mainPanel.add(new JLabel("New value:"));
    mainPanel.add(inputPanel);
    mainPanel.add(Box.createVerticalStrut(DialogUtils.VERTICAL_STRUT_SIZE));
    
    // Buttons using base class methods
    JButton saveButton = createConfirmButton("Save");
    JButton cancelButton = createCancelButton();
    mainPanel.add(DialogUtils.createButtonPanel(saveButton, cancelButton));
    
    return mainPanel;
  }
  
  private void updateInputPanel() {
    inputPanel.removeAll();
    String selected = propertyCombo.getSelectedItem().toString();
    
    switch (selected) {
      case "subject":
        inputPanel.add(new JLabel("New subject:"));
        inputPanel.add(subjectField);
        break;
      case "start":
        inputPanel.add(new JLabel("New start date and time:"));
        inputPanel.add(DialogUtils.createDateTimePanel("Date:", startDateSpinner, startTimeSpinner));
        break;
      case "end":
        inputPanel.add(new JLabel("New end date and time:"));
        inputPanel.add(DialogUtils.createDateTimePanel("Date:", endDateSpinner, endTimeSpinner));
        break;
    }
    inputPanel.revalidate();
    inputPanel.repaint();
  }
  
  /**
   * Gets the property being edited.
   * 
   * @return the property name
   */
  public String getProperty() {
    return propertyCombo.getSelectedItem().toString();
  }
  
  /**
   * Gets the new value for the property being edited.
   * 
   * @return the new value as a string
   */
  public String getNewValue() {
    String property = getProperty();
    switch (property) {
      case "subject":
        return subjectField.getText().trim();
      case "start":
        return getStartDateTime().toString();
      case "end":
        return getEndDateTime().toString();
      default:
        return "";
    }
  }
} 