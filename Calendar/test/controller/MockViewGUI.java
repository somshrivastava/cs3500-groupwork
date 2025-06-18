package controller;

import java.time.YearMonth;
import java.util.Objects;

import calendar.controller.Features;
import calendar.view.ICalendarViewGUI;

public class MockViewGUI implements ICalendarViewGUI {
  private final StringBuilder log;

  public MockViewGUI(StringBuilder log) {
    this.log = Objects.requireNonNull(log);
  }

  @Override
  public String getInputString() {
    return "";
  }

  @Override
  public void clearInputString() {

  }

  @Override
  public void resetFocus() {

  }

  @Override
  public void addFeatures(Features features) {
    log.append("Features added\n");
  }

  @Override
  public void updateCalendar() {
    log.append("Calendar updated\n");
  }

  @Override
  public void displayScheduleView(String scheduleContent) {
    String msg = "Schedule view displayed: " + scheduleContent + "\n";
    log.append(msg);
  }

  @Override
  public void updateCurrentMonth(YearMonth month) {
    String msg = "Current month updated to " + month + "\n";
    log.append(msg);
  }

  @Override
  public void updateCurrentCalendar(String calendarName) {
    String msg = "Calendar updated to: " + calendarName + "\n";
    log.append(msg);
  }

  @Override
  public void showMessage(String message) {
    String msg = "Message displayed: " + message + "\n";
    log.append(msg);
  }

  @Override
  public void showError(String error) {
    String msg = "Error displayed: " + error + "\n";
    log.append(msg);
  }

  @Override
  public void display() {
    log.append("View displayed\n");
  }

  @Override
  public void showEvents(String eventList) {
    String msg = "Events displayed: " + eventList + "\n";
    log.append(msg);
  }
}
