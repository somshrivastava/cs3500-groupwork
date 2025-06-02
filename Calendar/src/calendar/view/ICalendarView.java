package calendar.view;

import model.IEvent;

import java.util.ArrayList;
import java.util.List;

public interface ICalendarView {

  void displayMessage(String message);

  void displayError(String error);

  void displaySuccess(String message);

  void displayEvent(String subject, ArrayList<String> details);

  void displayEventList(String title, ArrayList<String> eventLines);

  void displayStatus(String dateTime, String status);

  void displayPrompt();

  void displayBlankLine();
}