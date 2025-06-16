package calendar.controller;

import java.time.YearMonth;

public interface Features {
  // TODO: Come up with features here:
  void exitProgram();

  void changeMonth(YearMonth currentMonth, int offset);

  void changeCalendar(String cal);

  void createEvent();

  void viewEvents();
}

