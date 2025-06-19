package calendar.view;



import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import java.awt.Component;

import calendar.model.IEvent;

/**
 * Dialog for editing existing events.
 * Extends AbstractEventDialog to eliminate duplicate code.
 */
public class EditEventDialog extends AbstractEventDialog {
  
  // Constants specific to edit dialog
  private static final int EDIT_DIALOG_HEIGHT = 250;
  private static final String[] EDIT_PROPERTIES = {"subject", "start", "end"};
  
  // Fields specific to edit dialog
  private JComboBox<String> propertyCombo;
  private JTextField subjectField;
  private JPanel inputPanel;
  private final IEvent event;
  
  /**
   * Constructs an EditEventDialog for the specified event.
   * 
   * @param parent the parent frame
   * @param event the event to edit
   */
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
    inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
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
    
    // Event info
    JLabel eventLabel = new JLabel("Event: " + event.getSubject());
    eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(eventLabel);
    
    JLabel timeLabel = new JLabel("Time: " + event.getStartDateTime().toLocalTime() + 
                            " - " + event.getEndDateTime().toLocalTime());
    timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(timeLabel);
    
    // Property selection
    JLabel editLabel = new JLabel("Edit: ");
    editLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(editLabel);
    propertyCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(propertyCombo);
    
    // Dynamic input
    JLabel valueLabel = new JLabel("New value: ");
    valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(valueLabel);
    mainPanel.add(inputPanel);
    
    // Buttons
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
        subjectField.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(subjectField);
        break;
      case "start":
        JPanel startPanel = DialogUtils.createDateTimePanel(startDateSpinner, startTimeSpinner);
        startPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(startPanel);
        break;
      case "end":
        JPanel endPanel = DialogUtils.createDateTimePanel(endDateSpinner, endTimeSpinner);
        endPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(endPanel);
        break;
      default:
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