package view;

import calendar.model.Event;
import calendar.model.EventLocation;
import calendar.model.EventStatus;
import calendar.model.IEvent;
import calendar.view.CalendarView;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CalendarViewTest {
    private CalendarView view;
    private StringWriter output;

    @Before
    public void setUp() {
        output = new StringWriter();
        view = new CalendarView(output);
    }

    @Test
    public void testDisplayMessage() {
        view.displayMessage("Test message");
        assertEquals("Test message\n", output.toString());
    }

    @Test
    public void testDisplayError() {
        view.displayError("Test error");
        assertEquals("\nERROR: Test error\n", output.toString());
    }

    @Test
    public void testDisplaySuccess() {
        view.displaySuccess("Test success");
        assertEquals("\nSUCCESS: Test success\n", output.toString());
    }

    @Test
    public void testDisplayEventsEmpty() {
        String header = "Test Header";
        view.displayEvents(header, new ArrayList<>());
        assertEquals("\n" + header + "\n" +
                "-".repeat(header.length()) + "\n" +
                "No events found.\n", output.toString());
    }

    @Test
    public void testDisplayEventsWithSingleEvent() {
        String header = "Test Header";
        List<IEvent> events = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2024, 3, 20, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 20, 10, 0);
        
        IEvent event = Event.getBuilder()
                .subject("Team Meeting")
                .startDateTime(start)
                .endDateTime(end)
                .build();
        events.add(event);

        view.displayEvents(header, events);
        assertEquals("\n" + header + "\n" +
                "-".repeat(header.length()) + "\n" +
                "Team Meeting (09:00 - 10:00)\n\n", output.toString());
    }

    @Test
    public void testDisplayEventsWithFullDetails() {
        String header = "Test Header";
        List<IEvent> events = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2024, 3, 20, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 20, 10, 0);
        
        IEvent event = Event.getBuilder()
                .subject("Team Meeting")
                .startDateTime(start)
                .endDateTime(end)
                .description("Weekly team sync")
                .location(EventLocation.ONLINE)
                .status(EventStatus.PRIVATE)
                .build();
        events.add(event);

        view.displayEvents(header, events);
        assertEquals("\n" + header + "\n" +
                "-".repeat(header.length()) + "\n" +
                "Team Meeting (09:00 - 10:00) at ONLINE\n" +
                "  Weekly team sync\n\n", output.toString());
    }

    @Test
    public void testDisplayStatus() {
        view.displayStatus("2024-03-20T14:30", true);
        assertEquals("\nBusy\n\n", output.toString());
    }

    @Test
    public void testDisplayPrompt() {
        view.displayPrompt();
        assertEquals("> ", output.toString());
    }

    @Test
    public void testDisplayMultipleEvents() {
        String header = "Test Header";
        List<IEvent> events = new ArrayList<>();
        LocalDateTime start1 = LocalDateTime.of(2024, 3, 20, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 3, 20, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 3, 20, 14, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 3, 20, 15, 0);
        
        IEvent event1 = Event.getBuilder()
                .subject("Morning Meeting")
                .startDateTime(start1)
                .endDateTime(end1)
                .build();
        
        IEvent event2 = Event.getBuilder()
                .subject("Afternoon Meeting")
                .startDateTime(start2)
                .endDateTime(end2)
                .build();
        
        events.add(event1);
        events.add(event2);

        view.displayEvents(header, events);
        assertEquals("\n" + header + "\n" +
                "-".repeat(header.length()) + "\n" +
                "Morning Meeting (09:00 - 10:00)\n" +
                "Afternoon Meeting (14:00 - 15:00)\n\n", output.toString());
    }
} 