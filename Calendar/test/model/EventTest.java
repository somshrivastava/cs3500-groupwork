package model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import calendar.model.Event;
import calendar.model.EventLocation;
import calendar.model.EventStatus;
import calendar.model.IEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for the Event class.
 * Tests the builder pattern, equality, hash codes, and all getter methods.
 */
public class EventTest {
  private LocalDateTime baseStart;
  private LocalDateTime baseEnd;
  private Event.EventBuilder builder;

  @Before
  public void setUp() {
    baseStart = LocalDateTime.of(2024, 3, 20, 10, 0);
    baseEnd = LocalDateTime.of(2024, 3, 20, 11, 0);
    builder = Event.getBuilder();
  }

  @Test
  public void testBasicEventCreation() {
    IEvent event = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    assertEquals("Meeting", event.getSubject());
    assertEquals(baseStart, event.getStartDateTime());
    assertEquals(baseEnd, event.getEndDateTime());
    assertNull(event.getDescription());
    assertNull(event.getLocation());
    assertNull(event.getStatus());
    assertNull(event.getSeriesId());
  }

  @Test
  public void testFullEventCreation() {
    IEvent event = builder
            .subject("Important Meeting")
            .description("Quarterly review")
            .location(EventLocation.ONLINE)
            .status(EventStatus.PRIVATE)
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .seriesId(123)
            .build();

    assertEquals("Important Meeting", event.getSubject());
    assertEquals("Quarterly review", event.getDescription());
    assertEquals(EventLocation.ONLINE, event.getLocation());
    assertEquals(EventStatus.PRIVATE, event.getStatus());
    assertEquals(baseStart, event.getStartDateTime());
    assertEquals(baseEnd, event.getEndDateTime());
    assertEquals(Integer.valueOf(123), event.getSeriesId());
  }

  @Test
  public void testEventBuilderChaining() {
    IEvent event = Event.getBuilder()
            .subject("Chain Test")
            .description("Testing chaining")
            .location(EventLocation.PHYSICAL)
            .status(EventStatus.PUBLIC)
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .seriesId(456)
            .build();

    assertEquals("Chain Test", event.getSubject());
    assertEquals("Testing chaining", event.getDescription());
    assertEquals(EventLocation.PHYSICAL, event.getLocation());
    assertEquals(EventStatus.PUBLIC, event.getStatus());
    assertEquals(Integer.valueOf(456), event.getSeriesId());
  }

  @Test
  public void testEventEquality() {
    IEvent event1 = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .description("First description")
            .location(EventLocation.ONLINE)
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .description("Different description")
            .location(EventLocation.PHYSICAL)
            .build();

    assertEquals("Events with same subject, start, and end should be equal", event1,
            event2);
  }

  @Test
  public void testEventInequalityDifferentSubject() {
    IEvent event1 = builder
            .subject("Meeting A")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting B")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    assertNotEquals("Events with different subjects should not be equal", event1, event2);
  }

  @Test
  public void testEventInequalityDifferentStartTime() {
    IEvent event1 = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseStart.plusHours(1))
            .endDateTime(baseEnd)
            .build();

    assertNotEquals("Events with different start times should not be equal", event1,
            event2);
  }

  @Test
  public void testEventInequalityDifferentEndTime() {
    IEvent event1 = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd.plusHours(1))
            .build();

    assertNotEquals("Events with different end times should not be equal", event1, event2);
  }

  @Test
  public void testEventEqualityWithNulls() {
    IEvent event1 = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .description(null)
            .location(null)
            .status(null)
            .seriesId(null)
            .build();

    assertEquals("Events should be equal regardless of null optional fields", event1,
            event2);
  }

  @Test
  public void testEventHashCode() {
    IEvent event1 = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .description("Description")
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .location(EventLocation.ONLINE)
            .build();

    assertEquals("Equal events should have same hash code",
            event1.hashCode(), event2.hashCode());
  }

  @Test
  public void testEventHashCodeDifferent() {
    IEvent event1 = builder
            .subject("Meeting A")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent event2 = Event.getBuilder()
            .subject("Meeting B")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    assertNotEquals("Different events should likely have different hash codes",
            event1.hashCode(), event2.hashCode());
  }

  @Test
  public void testEventNotEqualToNull() {
    IEvent event = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    assertNotEquals("Event should not equal null", null, event);
  }

  @Test
  public void testEventNotEqualToDifferentType() {
    IEvent event = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    assertNotEquals("Event should not equal string", "Meeting", event);
    assertNotEquals("Event should not equal integer", 123, event);
  }

  @Test
  public void testEventSelfEquality() {
    IEvent event = builder
            .subject("Meeting")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    assertEquals("Event should equal itself", event, event);
  }

  @Test
  public void testBuilderReuse() {
    Event.EventBuilder sharedBuilder = Event.getBuilder();

    IEvent event1 = sharedBuilder
            .subject("First Event")
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent event2 = sharedBuilder
            .subject("Second Event")
            .startDateTime(baseStart.plusDays(1))
            .endDateTime(baseEnd.plusDays(1))
            .build();

    assertNotEquals("Builder reuse should create different events", event1, event2);
    assertEquals("Second Event", event2.getSubject());
    assertEquals(baseStart.plusDays(1), event2.getStartDateTime());
  }

  @Test
  public void testAllLocationValues() {
    IEvent physicalEvent = builder
            .subject("Physical Meeting")
            .location(EventLocation.PHYSICAL)
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent onlineEvent = Event.getBuilder()
            .subject("Online Meeting")
            .location(EventLocation.ONLINE)
            .startDateTime(baseStart.plusHours(2))
            .endDateTime(baseEnd.plusHours(2))
            .build();

    assertEquals(EventLocation.PHYSICAL, physicalEvent.getLocation());
    assertEquals(EventLocation.ONLINE, onlineEvent.getLocation());
  }

  @Test
  public void testAllStatusValues() {
    IEvent publicEvent = builder
            .subject("Public Meeting")
            .status(EventStatus.PUBLIC)
            .startDateTime(baseStart)
            .endDateTime(baseEnd)
            .build();

    IEvent privateEvent = Event.getBuilder()
            .subject("Private Meeting")
            .status(EventStatus.PRIVATE)
            .startDateTime(baseStart.plusHours(2))
            .endDateTime(baseEnd.plusHours(2))
            .build();

    assertEquals(EventStatus.PUBLIC, publicEvent.getStatus());
    assertEquals(EventStatus.PRIVATE, privateEvent.getStatus());
  }
} 