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

### Multiple Calendar Support
- **Calendar Management**: Create, edit, and manage multiple calendars with unique names
- **Timezone Support**: Each calendar has its own timezone with automatic event conversion
- **Event Copying**: Copy events between calendars with timezone conversion

### Event Operations
- **Create**: Add new single or recurring events
- **Edit**: Modify existing events (single event, events from a date, or entire series)
- **Print**: Display events for a specific date or date range
- **Status**: Check if a specific time slot is busy
- **Copy**: Copy events within or between calendars

### Interface Modes
- **Interactive Mode**: Real-time command-line interface with user prompts
- **Headless Mode**: Batch processing from input files

## Architecture

The application follows an MVC architecture pattern:

```
- Model (calendar.model)
  - ICalendarModel - Core business logic interface
  - CalendarModel - Main model implementation
  - ISmartCalendarModel - Enhanced calendar with timezone and copying
  - SmartCalendarModel - Smart calendar implementation
  - ICalendarManager - Multi-calendar management interface
  - CalendarManager - Multi-calendar management implementation
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
    - SmartCommandParserFactory - Enhanced command routing
    - CommandParserFactory - Event command parsing
    - CreateCommandParser - Handles create event parsing
    - EditCommandParser - Handles edit event parsing
    - PrintCommandParser - Handles print command parsing
    - ShowCommandParser - Handles show command parsing
    - CreateCalCommandParser - Handles create calendar parsing
    - EditCalCommandParser - Handles edit calendar parsing
    - UseCalCommandParser - Handles use calendar parsing
    - CopyCommandParser - Handles copy command parsing
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

### Calendar Management

#### Create Calendar
```
create calendar --name "Work Calendar" --timezone America/New_York
create calendar --name "Personal" --timezone America/Los_Angeles
```

#### Edit Calendar
```
edit calendar --name "Work Calendar" --property name "Professional Calendar"
edit calendar --name "Personal" --property timezone Europe/London
```

#### Use Calendar
```
use calendar --name "Work Calendar"
```

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

### Copying Events

#### Copy Single Event
```
copy event "Team Meeting" on 2024-03-20T10:00 --target "Personal" to 2024-03-25T14:00
```

#### Copy Events on Date
```
copy events on 2024-03-20 --target "Personal" to 2024-03-25
```

#### Copy Events in Range
```
copy events between 2024-03-20 and 2024-03-22 --target "Personal" to 2024-04-01
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
create calendar --name "Work" --timezone America/New_York
use calendar --name "Work"
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
- **Timezone**: IANA format (e.g., `America/New_York`, `Europe/London`)

### Test Coverage
- **Model Tests**: Event creation, editing, querying, recurring event logic, edge cases, calendar management, timezone conversion
- **View Tests**: Display formatting, event rendering, status display, error handling
- **Controller Tests**: Command parsing, user interaction, file processing, error recovery, calendar command routing
- **Integration Tests**: End-to-end workflows, complex scenarios, boundary conditions, multi-calendar operations

## Design Principles

- **MVC Architecture**: Clear separation of concerns
- **Interface-Based Design**: Loose coupling through interfaces
- **Command Parser Pattern**: Extendable command processing
- **Comprehensive Error Handling**: Proper error recovery and user feedback
- **Timezone-Aware Design**: Proper handling of timezone conversions

## Error Handling

The application provides detailed error messages for:
- Invalid command syntax
- Malformed dates and times
- Missing required parameters
- Invalid weekday characters
- File I/O errors (headless mode)
- Event scheduling conflicts
- Invalid timezone specifications
- Calendar management errors
- Event copying conflicts

## Design Changes from Previous Assignment

### 1. **Extended Model Interface**
- **Change**: Created `ISmartCalendarModel` interface extending `ICalendarModel` instead of modifying the original interface
- **Justification**: Maintains backward compatibility while adding new functionality. Follows the Open/Closed Principle.

### 2. **Added Calendar Manager Layer**
- **Change**: Introduced `ICalendarManager` and `CalendarManager` classes
- **Justification**: Separates concerns between individual calendar operations and multi-calendar management. Provides centralized calendar lifecycle management.

### 3. **Enhanced Command Parser**
- **Change**: Created `SmartCommandParserFactory` that delegates to `CommandParserFactory` for event commands
- **Justification**: Maintains existing event command parsing while adding calendar management commands. Follows Single Responsibility Principle.

### 4. **Timezone Conversion**
- **Change**: Added timezone support throughout the model layer with automatic conversion
- **Justification**: Essential for multi-calendar support. Uses Java's built-in timezone handling for accuracy and reliability.

### 5. **Event Copying**
- **Change**: Added comprehensive event copying methods with timezone conversion
- **Justification**: Supports the core requirement of copying events between calendars while handling timezone differences properly.

### 6. **Controller Architecture**
- **Change**: Modified controllers to work with `ICalendarManager` instead of direct `ICalendarModel`
- **Justification**: Enables multi-calendar support while maintaining the same controller interface pattern.

## How to Run the Program

### Using JAR File
```bash
# Interactive mode
java -jar Calendar.jar --mode interactive

# Headless mode
java -jar Calendar.jar --mode headless input_file.txt
```

## Features Status

### Working Features
- **Multiple Calendar Management**: Create, edit, use, and manage multiple calendars
- **Timezone Support**: Full timezone support with automatic event conversion
- **Event Copying**: Copy single events, events on specific dates, and events in date ranges
- **All Original Event Operations**: Create, edit, print, and query events
- **Interactive Mode**: Real-time command-line interface
- **Headless Mode**: Batch processing from files
- **Comprehensive Error Handling**: Detailed error messages for all invalid operations
- **MVC Architecture**: Clean separation of concerns maintained

## Team Contribution

### Som Shrivastava
- **Model Layer**: Implemented `SmartCalendarModel`, `CalendarManager`, and timezone conversion logic
- **Event Copying**: Implemented all event copying functionality with timezone handling
- **Testing**: Model tests, calendar manager tests, and smart calendar model tests

### Elaine Mo
- **Command Parsing**: Developed smart command parser and calendar command parsers
- **Controller Layer**: Enhanced controllers to work with calendar manager
- **Mock Framework**: Created mock implementations for testing

## Additional Notes for Grading

- All timezone conversions use Java's built-in `ZoneId` and `ZonedDateTime` classes for accuracy
- Event copying preserves all event properties including series relationships
- The application gracefully handles edge cases like timezone changes across date boundaries
- Comprehensive test coverage includes both unit tests and integration tests
- All command parsing is case-insensitive for user convenience
- The design supports easy extension for future calendar features

## Start Time Edit Behavior Change

### **Modification to Event Start Time Editing**

#### **Previous Behavior**
When editing an event's start time, the system would:
1. Change the start time to the new value
2. **Automatically adjust the end time** to maintain the original event duration
3. Keep the event duration constant

#### **New Behavior**
When editing an event's start time, the system now:
1. Changes the start time to the new value
2. **Keeps the end time unchanged** (no automatic adjustment)
3. Validates that the new start time is not after the existing end time
4. Throws an error if the new start time would be after the end time

#### **Implementation Details**
The changes were made in the `CalendarModel` class:

1. **Modified `parsePropertyValue` method**: Added validation to check if the new start time is after the existing end time before applying the change.

2. **Updated `applyStartTimeChange` method**: Changed the logic to keep the original end time unchanged instead of maintaining the duration by adjusting the end time.

3. **Enhanced error handling**: The system now throws an appropriate error when attempting to set a start time that would be after the end time.

#### **Test Updates**
The tests were also updated to reflect this new behavior:
- **`testEditEventStartTime`**: Updated to verify that the end time remains unchanged when editing start time
- **`testEditEventStartTimeAfterEndTime`**: New test added to verify proper error handling when start time would be after end time
- Fixed an existing test that was using invalid time ranges (start time equal to end time)
