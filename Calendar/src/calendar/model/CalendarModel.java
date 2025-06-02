package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CalendarModel implements ICalendarModel {
    private final Set<IEvent> events;
    private static int nextSeriesId = 1;

    public CalendarModel() {
        this.events = new HashSet<>();
    }

    @Override
    public void createEvent(IEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        events.add(event);
    }

    @Override
    public void createSingleTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        validateTimedEvent(subject, startDateTime, endDateTime);
        IEvent event = SingleEvent.getBuilder()
                .subject(subject)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        events.add(event);
    }

    @Override
    public void createSingleAllDayEvent(String subject, LocalDateTime date) {
        validateAllDayEvent(subject, date);
        IEvent event = SingleEvent.getBuilder()
                .subject(subject)
                .startDateTime(date)
                .build();
        events.add(event);
    }

    @Override
    public void createRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                        ArrayList<DayOfWeek> weekdays, int count) {
        validateRecurringTimedEvent(subject, startDateTime, endDateTime, weekdays, count);
        createRecurringTimedEvents(subject, startDateTime, endDateTime, weekdays, count, null);
    }

    @Override
    public void createRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                             ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
        validateRecurringTimedEventUntil(subject, startDateTime, endDateTime, weekdays, untilDate);
        createRecurringTimedEvents(subject, startDateTime, endDateTime, weekdays, null, untilDate);
    }

    @Override
    public void createRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                         ArrayList<DayOfWeek> weekdays, int count) {
        validateRecurringAllDayEvent(subject, startDate, weekdays, count);
        createRecurringAllDayEvents(subject, startDate, weekdays, count, null);
    }

    @Override
    public void createRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                              ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
        validateRecurringAllDayEventUntil(subject, startDate, weekdays, untilDate);
        createRecurringAllDayEvents(subject, startDate, weekdays, null, untilDate);
    }

    private void createRecurringTimedEvents(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                          ArrayList<DayOfWeek> weekdays, Integer count, LocalDateTime untilDate) {
        LocalDateTime currentDate = startDateTime;
        int eventsCreated = 0;
        long durationMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);
        int seriesId = nextSeriesId++;

        while (true) {
            if (weekdays.contains(currentDate.getDayOfWeek())) {
                LocalDateTime eventStart = currentDate;
                LocalDateTime eventEnd = eventStart.plus(durationMinutes, ChronoUnit.MINUTES);
                
                IEvent event = RecurringEvent.getBuilder()
                        .subject(subject)
                        .startDateTime(eventStart)
                        .endDateTime(eventEnd)
                        .seriesId(seriesId)
                        .build();
                events.add(event);
                eventsCreated++;

                if (count != null && eventsCreated >= count) {
                    break;
                }
                if (untilDate != null && !eventStart.toLocalDate().isBefore(untilDate.toLocalDate())) {
                    break;
                }
            }

            currentDate = currentDate.plusDays(1);
            if (untilDate != null && currentDate.isAfter(untilDate)) {
                break;
            }
        }
    }

    private void createRecurringAllDayEvents(String subject, LocalDateTime startDate,
                                           ArrayList<DayOfWeek> weekdays, Integer count, LocalDateTime untilDate) {
        LocalDateTime currentDate = startDate;
        int eventsCreated = 0;
        int seriesId = nextSeriesId++;

        while (true) {
            if (weekdays.contains(currentDate.getDayOfWeek())) {
                IEvent event = RecurringEvent.getBuilder()
                        .subject(subject)
                        .startDateTime(currentDate)
                        .seriesId(seriesId)
                        .build();
                events.add(event);
                eventsCreated++;

                if (count != null && eventsCreated >= count) {
                    break;
                }
                if (untilDate != null && !currentDate.toLocalDate().isBefore(untilDate.toLocalDate())) {
                    break;
                }
            }

            currentDate = currentDate.plusDays(1);
            if (untilDate != null && currentDate.isAfter(untilDate)) {
                break;
            }
        }
    }

    private void validateSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
    }

    private void validateDateTime(LocalDateTime dateTime, String fieldName) {
        if (dateTime == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private void validateTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        validateSubject(subject);
        validateDateTime(startDateTime, "Start time");
        validateDateTime(endDateTime, "End time");
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    private void validateAllDayEvent(String subject, LocalDateTime date) {
        validateSubject(subject);
        validateDateTime(date, "Date");
    }

    private void validateWeekdays(ArrayList<DayOfWeek> weekdays) {
        if (weekdays == null || weekdays.isEmpty()) {
            throw new IllegalArgumentException("At least one weekday must be specified");
        }
    }

    private void validateCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
    }

    private void validateUntilDate(LocalDateTime startDate, LocalDateTime untilDate) {
        validateDateTime(untilDate, "Until date");
        if (!untilDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Until date must be after start date");
        }
    }

    private void validateRecurringTimedEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                           ArrayList<DayOfWeek> weekdays, int count) {
        validateTimedEvent(subject, startDateTime, endDateTime);
        validateWeekdays(weekdays);
        validateCount(count);
    }

    private void validateRecurringTimedEventUntil(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
        validateTimedEvent(subject, startDateTime, endDateTime);
        validateWeekdays(weekdays);
        validateUntilDate(startDateTime, untilDate);
    }

    private void validateRecurringAllDayEvent(String subject, LocalDateTime startDate,
                                            ArrayList<DayOfWeek> weekdays, int count) {
        validateAllDayEvent(subject, startDate);
        validateWeekdays(weekdays);
        validateCount(count);
    }

    private void validateRecurringAllDayEventUntil(String subject, LocalDateTime startDate,
                                                 ArrayList<DayOfWeek> weekdays, LocalDateTime untilDate) {
        validateAllDayEvent(subject, startDate);
        validateWeekdays(weekdays);
        validateUntilDate(startDate, untilDate);
    }
}
