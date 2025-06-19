package controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;

import calendar.controller.Features;
import calendar.model.IEvent;
import calendar.view.ICalendarViewGUI;

public class MockViewGUI implements ICalendarViewGUI {
  private final StringBuilder log;

  public MockViewGUI(StringBuilder log) {
    this.log = Objects.requireNonNull(log);
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

  /**
   * Updates the event data that the view uses for display.
   *
   * @param eventData map of dates to their events for display purposes
   */
  @Override
  public void updateEventData(Map<LocalDate, List<IEvent>> eventData) {
    String msg = "Updated event data: \n";
    log.append(msg);
  }

  /**
   * Sets up keyboard shortcuts for the application.
   *
   * @param key         the keystroke to bind
   * @param featureName the name of the feature to trigger
   */
  @Override
  public void setHotKey(KeyStroke key, String featureName) {
    String msg = "Keyboard shortcut made for " + key + " with " + featureName + "\n";
    log.append(msg);
  }

  /**
   * Shows a dialog to create a new event for the specified date.
   * Called by controller in response to high-level feature requests.
   *
   * @param selectedDate the date for the new event
   */
  @Override
  public void showCreateEventDialog(LocalDate selectedDate) {
    String msg = "Create event dialogue shown for " + selectedDate + "\n";
    log.append(msg);
  }

  /**
   * Shows a dialog listing events for a specific date.
   * Called by controller in response to high-level feature requests.
   *
   * @param date   the date to show events for
   * @param events the list of events for that date
   */
  @Override
  public void showEventsForDate(LocalDate date, List<IEvent> events) {
    String msg = "Events shown on " + date + "\n";
    log.append(msg);
  }

  /**
   * Shows a dialog to edit the specified event.
   * Called by controller in response to high-level feature requests.
   *
   * @param event the event to edit
   */
  @Override
  public void showEditEventDialog(IEvent event) {
    String msg = "Edit event dialogue shown for " + event.getSubject() + "\n";
    log.append(msg);
  }
}
