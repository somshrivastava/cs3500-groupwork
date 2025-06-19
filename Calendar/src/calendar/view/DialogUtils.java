package calendar.view;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.SpinnerDateModel;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for common dialog operations with simple, clean layouts.
 */
public final class DialogUtils {
  
  // Constants for UI dimensions and styling
  public static final int VERTICAL_STRUT_SIZE = 10;
  public static final int BORDER_SIZE = 15;
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String TIME_FORMAT = "HH:mm";
  public static final int DEFAULT_MINUTE = 0;
  
  // Private constructor to prevent instantiation
  private DialogUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }
  
  /**
   * Creates a main panel for dialogs with simple layout.
   * 
   * @return the main dialog panel
   */
  public static JPanel createDialogMainPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, 
                                                   BORDER_SIZE, BORDER_SIZE));
    return panel;
  }
  
  /**
   * Creates a panel with date and time spinners (without label).
   * 
   * @param dateSpinner the date spinner
   * @param timeSpinner the time spinner
   * @return the date/time panel
   */
  public static JPanel createDateTimePanel(JSpinner dateSpinner, JSpinner timeSpinner) {
    return createDateTimePanel(null, dateSpinner, timeSpinner);
  }
  
  /**
   * Creates a panel with date and time spinners (with optional label).
   * 
   * @param label optional label for the panel
   * @param dateSpinner the date spinner
   * @param timeSpinner the time spinner
   * @return the date/time panel
   */
  public static JPanel createDateTimePanel(String label, JSpinner dateSpinner, 
                                          JSpinner timeSpinner) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
    if (label != null) {
      panel.add(new JLabel(label));
    }
    
    JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    spinnerPanel.add(dateSpinner);
    spinnerPanel.add(timeSpinner);
    panel.add(spinnerPanel);
    
    return panel;
  }
  
  /**
   * Creates a panel containing buttons.
   * 
   * @param buttons the buttons to add
   * @return the button panel
   */
  public static JPanel createButtonPanel(JButton... buttons) {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    for (JButton button : buttons) {
      buttonPanel.add(button);
    }
    return buttonPanel;
  }
  
  /**
   * Creates a date spinner initialized with the given date.
   * 
   * @param date the initial date
   * @return the date spinner
   */
  public static JSpinner createDateSpinner(LocalDate date) {
    SpinnerDateModel dateModel = new SpinnerDateModel();
    dateModel.setValue(java.sql.Date.valueOf(date));
    JSpinner dateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, DATE_FORMAT);
    dateSpinner.setEditor(dateEditor);
    return dateSpinner;
  }
  
  /**
   * Creates a time spinner initialized with the given hour.
   * 
   * @param hour the initial hour
   * @return the time spinner
   */
  public static JSpinner createTimeSpinner(int hour) {
    Calendar timeCal = Calendar.getInstance();
    timeCal.set(Calendar.HOUR_OF_DAY, hour);
    timeCal.set(Calendar.MINUTE, DEFAULT_MINUTE);
    timeCal.set(Calendar.SECOND, 0);
    timeCal.set(Calendar.MILLISECOND, 0);

    SpinnerDateModel timeModel = new SpinnerDateModel();
    timeModel.setValue(timeCal.getTime());
    JSpinner timeSpinner = new JSpinner(timeModel);
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, TIME_FORMAT);
    timeSpinner.setEditor(timeEditor);
    return timeSpinner;
  }
  
  /**
   * Converts a Date to LocalDate without type casting.
   */
  public static LocalDate convertDateToLocalDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return LocalDate.of(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH));
  }
  
  /**
   * Extracts LocalDateTime from date and time spinners.
   * 
   * @param dateSpinner the date spinner
   * @param timeSpinner the time spinner
   * @return the combined LocalDateTime
   */
  public static LocalDateTime getDateTimeFromSpinners(JSpinner dateSpinner, 
                                                     JSpinner timeSpinner) {
    SpinnerDateModel dateModel = (SpinnerDateModel) dateSpinner.getModel();
    SpinnerDateModel timeModel = (SpinnerDateModel) timeSpinner.getModel();
    
    Date dateValue = dateModel.getDate();
    Date timeValue = timeModel.getDate();
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateValue);
    LocalDate date = LocalDate.of(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    
    Calendar timeCal = Calendar.getInstance();
    timeCal.setTime(timeValue);

    return date.atTime(timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE));
  }
} 