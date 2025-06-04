/*
public class CalendarControllerTest {
    private ICalendarModel model;
    private ICalendarView view;
    private CalendarController controller;
    private StringBuilder output;

    @Before
    public void setUp() {
        model = new CalendarModel();
        output = new StringBuilder();
        view = new MockCalendarView(output);
        controller = new CalendarController(model, view, new StringReader(""));
    }

    @Test
    public void testCreateAndPrintSingleEvent() {
        // Simulate creating a single event
        String input = "create event \"Team Meeting\" from 2024-03-20T10:00 to 2024-03-20T11:00\n" +
                      "print events on 2024-03-20\n" +
                      "exit";
        
        controller = new CalendarController(model, view, new StringReader(input));
        controller.go("interactive", null);

        // Verify the event was created and printed
        List<IEvent> events = model.getEventsOnDate(LocalDate.of(2024, 3, 20));
        assertEquals(1, events.size());
        assertEquals("Team Meeting", events.get(0).getSubject());
        assertEquals(LocalDateTime.of(2024, 3, 20, 10, 0), events.get(0).getStartDateTime());
        assertEquals(LocalDateTime.of(2024, 3, 20, 11, 0), events.get(0).getEndDateTime());
        
        // Verify the output contains the event details
        String outputStr = output.toString();
        assertTrue(outputStr.contains("Team Meeting"));
        assertTrue(outputStr.contains("2024-03-20"));
        
        // Print the output
        System.out.println("\n=== Single Event Test Output ===");
        System.out.println(outputStr);
    }

    @Test
    public void testCreateAndPrintRecurringEvent() {
        // Simulate creating a recurring event
        String input = "create event \"Weekly Standup\" from 2024-03-20T09:00 to 2024-03-20T09:30 repeats MWF for 3\n" +
                      "print events on 2024-03-20\n" +
                      "print events on 2024-03-22\n" +
                      "exit";
        
        controller = new CalendarController(model, view, new StringReader(input));
        controller.go("interactive", null);

        // Verify the events were created
        List<IEvent> eventsOnWednesday = model.getEventsOnDate(LocalDate.of(2024, 3, 20));
        List<IEvent> eventsOnFriday = model.getEventsOnDate(LocalDate.of(2024, 3, 22));
        
        assertEquals(1, eventsOnWednesday.size());
        assertEquals(1, eventsOnFriday.size());
        
        // Verify the event details
        IEvent wednesdayEvent = eventsOnWednesday.get(0);
        IEvent fridayEvent = eventsOnFriday.get(0);
        
        assertEquals("Weekly Standup", wednesdayEvent.getSubject());
        assertEquals(LocalDateTime.of(2024, 3, 20, 9, 0), wednesdayEvent.getStartDateTime());
        assertEquals(LocalDateTime.of(2024, 3, 20, 9, 30), wednesdayEvent.getEndDateTime());
        
        assertEquals("Weekly Standup", fridayEvent.getSubject());
        assertEquals(LocalDateTime.of(2024, 3, 22, 9, 0), fridayEvent.getStartDateTime());
        assertEquals(LocalDateTime.of(2024, 3, 22, 9, 30), fridayEvent.getEndDateTime());
        
        // Verify they're part of the same series
        assertNotNull(wednesdayEvent.getSeriesId());
        assertEquals(wednesdayEvent.getSeriesId(), fridayEvent.getSeriesId());
        
        // Verify the output contains the event details
        String outputStr = output.toString();
        assertTrue(outputStr.contains("Weekly Standup"));
        assertTrue(outputStr.contains("2024-03-20"));
        assertTrue(outputStr.contains("2024-03-22"));
        
        // Print the output
        System.out.println("\n=== Recurring Event Test Output ===");
        System.out.println(outputStr);
    }

    @Test
    public void testCreateAndPrintAllDayEvent() {
        // Simulate creating an all-day event
        String input = "create event \"Holiday\" on 2024-03-21\n" +
                      "print events on 2024-03-21\n" +
                      "exit";
        
        controller = new CalendarController(model, view, new StringReader(input));
        controller.go("interactive", null);

        // Verify the event was created
        List<IEvent> events = model.getEventsOnDate(LocalDate.of(2024, 3, 21));
        assertEquals(1, events.size());
        
        // Verify the event details (all-day events are set to 8:00-17:00)
        IEvent event = events.get(0);
        assertEquals("Holiday", event.getSubject());
        assertEquals(LocalDateTime.of(2024, 3, 21, 8, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2024, 3, 21, 17, 0), event.getEndDateTime());
        
        // Verify the output contains the event details
        String outputStr = output.toString();
        assertTrue(outputStr.contains("Holiday"));
        assertTrue(outputStr.contains("2024-03-21"));
        
        // Print the output
        System.out.println("\n=== All Day Event Test Output ===");
        System.out.println(outputStr);
    }
}
*/