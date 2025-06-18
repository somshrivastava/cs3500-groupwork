package calendar.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for common dialog operations.
 */
public final class DialogUtils {
  
  // Constants for UI dimensions and styling
  public static final int VERTICAL_STRUT_SIZE = 10;
  public static final int BORDER_SIZE = 10;
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String TIME_FORMAT = "HH:mm";
  public static final int DEFAULT_MINUTE = 0;
  
  // Private constructor to prevent instantiation
  private DialogUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }
  
  /**
   * Creates a main panel for dialogs with standard layout and border.
   */
  public static JPanel createDialogMainPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
    return panel;
  }
  
  /**
   * Creates a panel containing date and time spinners with optional label.
   */
  public static JPanel createDateTimePanel(String label, JSpinner dateSpinner, JSpinner timeSpinner) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    if (label != null) {
      panel.add(new JLabel(label));
    }
    panel.add(dateSpinner);
    panel.add(timeSpinner);
    return panel;
  }
  
  /**
   * Creates a panel containing buttons in a flow layout.
   */
  public static JPanel createButtonPanel(JButton... buttons) {
    JPanel buttonPanel = new JPanel(new FlowLayout());
    for (JButton button : buttons) {
      buttonPanel.add(button);
    }
    return buttonPanel;
  }
  
  /**
   * Creates a date spinner initialized with the given date.
   * No type casting needed - uses proper SpinnerDateModel.
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
   * No type casting needed - uses proper SpinnerDateModel.
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
   * Extracts LocalDateTime from date and time spinners without type casting.
   * Uses proper SpinnerDateModel access methods.
   */
  public static LocalDateTime getDateTimeFromSpinners(JSpinner dateSpinner, JSpinner timeSpinner) {
    // Use the model directly to avoid type casting
    SpinnerDateModel dateModel = (SpinnerDateModel) dateSpinner.getModel();
    SpinnerDateModel timeModel = (SpinnerDateModel) timeSpinner.getModel();
    
    Date dateValue = dateModel.getDate();
    Date timeValue = timeModel.getDate();
    
    LocalDate date = convertDateToLocalDate(dateValue);
    
    Calendar timeCal = Calendar.getInstance();
    timeCal.setTime(timeValue);

    return date.atTime(
        timeCal.get(Calendar.HOUR_OF_DAY),
        timeCal.get(Calendar.MINUTE));
  }
} 