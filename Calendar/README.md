# Calendar Application

A calendar management system built with Java, implementing a clean MVC (Model-View-Controller) 
architecture. This application allows users to create, edit, and manage both single and 
recurring events through either an interactive or headless mode.

## Features

### Event Management
- **Single Events**: Create timed events or all-day events
- **Recurring Events**: Create repeating events
  - Count-based repetition (e.g., "repeat 10 times")
  - Date-based repetition (e.g., "repeat until 2024-12-31")
  - Weekday specification using single characters: M(onday), T(uesday), W(ednesday), R(thuRsday), F(riday), S(aturday), U(sUnday)

### Event Operations
- **Create**: Add new single or recurring events
- **Edit**: Modify existing events (single event, events from a date, or entire series)
- **Print**: Display events for a specific date or date range
- **Status**: Check if a specific time slot is busy

### Interface Modes
- **Interactive Mode**: Real-time command-line interface with user prompts
- **Headless Mode**: Batch processing from input files

## Architecture

The application follows an MVC architecture pattern:

```
- Model (calendar.model)
  - ICalendarModel - Core business logic interface
  - CalendarModel - Main model implementation
  - IEvent - Event interface
  - Event - IEvent implementation
- View (calendar.view)
  - ICalendarView - Display interface
  - CalendarView - Console-based view implementation
- Controller (calendar.controller)
  - ICalendarController - Controller interface
  - InteractiveController - Interactive command-line controller
  - HeadlessController - Headless mode controller
  - Parser (calendar.controller.parser)
    - CommandParser - Root entry into command line parsing
    - CreateCommandParser - Handles create command parsing
    - EditCommandParser - Handles edit command parsing
    - PrintCommandParser - Handles print command parsing
    - ShowCommandParser - Handles show command parsing
```

## Getting Started

### Running the Application

#### Interactive Mode
```bash
java CalendarApp --mode interactive
```

#### Headless Mode
```bash
java CalendarApp --mode headless {file_name}
```

## Command Syntax

### Creating Events

#### Single Events
```
# Timed event
create event "Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00

# All-day event  
create event "Holiday" on 2024-03-20
```

#### Recurring Events
```
# Count-based repetition
create event "Daily Standup" from 2024-03-20T09:00 to 2024-03-20T09:30 repeats MTWRF for 10 times

# Date-based repetition
create event "Weekly Meeting" from 2024-03-20T14:00 to 2024-03-20T15:00 repeats W until 2024-12-31

# All-day recurring
create event "Gym Day" on 2024-03-20 repeats MWF for 20 times
```

### Editing Events

#### Single Event
```
edit event subject "Old Name" from 2024-03-20T10:00 to 2024-03-20T11:00 with "New Name"
edit event start "Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00 with 2024-03-20T09:00
edit event end "Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00 with 2024-03-20T12:00
edit event description "Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00 with "Important discussion"
edit event location "Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00 with "Conference Room A"
edit event status "Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00 with "BUSY"
```

#### Events from Date
```
edit events subject "Meeting" from 2024-03-20T10:00 with "New Meeting Name"
edit events location "Daily Standup" from 2024-03-25T09:00 with "Virtual"
```

#### Entire Series
```
edit series subject "Weekly Review" from 2024-03-20T15:00 with "Team Review"
edit series description "Training" from 2024-03-20T10:00 with "Advanced training session"
```

### Viewing Events

#### Print Events
```
# Single date
print events on 2024-03-20

# Date range
print events from 2024-03-20T00:00 to 2024-03-21T23:59
```

#### Show Status
```
show status on 2024-03-20T10:30
```

### Control Commands
```
exit    # Exit the application
```

## Input File Format (Headless Mode)

Create a text file with one command per line (must have exit command at the end):

```
create event "Team Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00
create event "Lunch" on 2024-03-20
edit event location "Team Meeting" from 2024-03-20T10:00 to 2024-03-20T11:00 with "Conference Room B"
print events on 2024-03-20
show status on 2024-03-20T10:30
exit
```

## Date and Time Formats

- **Date**: `YYYY-MM-DD`
- **Date-Time**: `YYYY-MM-DDTHH:MM`
- **Weekdays**: `M` `T` `W` `R` `F` `S` `U`

### Test Coverage
- **Model Tests**: Event creation, editing, querying, recurring event logic, edge cases
- **View Tests**: Display formatting, event rendering, status display, error handling
- **Controller Tests**: Command parsing, user interaction, file processing, error recovery
- **Integration Tests**: End-to-end workflows, complex scenarios, boundary conditions

## Design Principles

- **MVC Architecture**: Clear separation of concerns
- **Interface-Based Design**: Loose coupling through interfaces
- **Command Parser Pattern**: Extendable command processing
- **Comprehensive Error Handling**: Proper error recovery and user feedback

## Error Handling

The application provides detailed error messages for:
- Invalid command syntax
- Malformed dates and times
- Missing required parameters
- Invalid weekday characters
- File I/O errors (headless mode)
- Event scheduling conflicts

## Division of work

Som and Elaine worked together on this Calendar application. Together, we came up with a solid design
before going into any implementation, and once a design was agreed upon then we divided the work.
Elaine primarily focused on the development of the controller and view and it's corresponding test coverage which involved creating
mocks for controller testing. Som focused on the development of the command parser and model
which involved the business logic behind creating, editing, and querying events.
