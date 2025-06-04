import java.util.ArrayList;
import java.util.List;

import calendar.model.IEvent;
import calendar.view.ICalendarView;

/**
 * This class is a mock CalendarView used for testing the CalendarController.
 */
public class MockCalendarView implements ICalendarView {
    private final StringBuilder output;

    public MockCalendarView(StringBuilder output) {
        this.output = output;
    }

    @Override
    public void displayMessage(String message) {
        output.append(message).append("\n");
    }

    @Override
    public void displayError(String error) {
        output.append("Error: ").append(error).append("\n");
    }

    @Override
    public void displaySuccess(String message) {
        output.append("Success: ").append(message).append("\n");
    }

    @Override
    public void displayEvent(String subject, ArrayList<String> details) {
        output.append("Event: ").append(subject).append("\n");
        for (String detail : details) {
            output.append("  ").append(detail).append("\n");
        }
    }

    @Override
    public void displayEventList(String title, ArrayList<String> eventLines) {
        output.append(title).append("\n");
        for (String line : eventLines) {
            output.append(line).append("\n");
        }
    }

    @Override
    public void displayStatus(String dateTime, String status) {
        output.append("Status at ").append(dateTime).append(": ").append(status).append("\n");
    }

    @Override
    public void displayPrompt() {
        output.append("> ");
    }

    @Override
    public void displayBlankLine() {
        output.append("\n");
    }

    /**
     * Displays a list of events with a header.
     *
     * @param header the header text for the event list
     * @param events the list of events to display
     */
    @Override
    public void displayEvents(String header, List<IEvent> events) {

    }
} 