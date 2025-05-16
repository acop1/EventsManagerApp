package com.example.eventsmanager.model;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventItem {
    String location;
    String author;
    String title;
    LocalTime time;
    String description;
    LocalDate date;

    private String id;

    public static final String LOCATION = "location";
    public static final String AUTHOR = "author";
    public static final String TITLE = "title";
    public static final String TIME = "time";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";

    public EventItem(String location, String author, String title, String timeStr,
                     String description, String dateStr) {
        this.location = location;
        this.author = author;
        this.title = title;
        this.description = description;
        this.time = parseTime(timeStr);
        this.date = parseDate(dateStr);
    }

    public EventItem(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Make sure to exclude id from your toMap() method
// as Firestore will handle the document ID separately
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.equals("null")) {
            return LocalTime.MIDNIGHT; // or another default value
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeStr, timeFormatter);
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.equals("null")) {
            return LocalDate.now(); // or another default value
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(dateStr, dateFormatter);
    }

    // Add string getters for backward compatibility if needed
    public String getTimeString() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getDateString() {
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    // Rest of your existing code...
    public String getLocation() { return location; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "\nPublic Event:\nTitle: " + title + ", Author: " + author +
                ", Date: " + getDateString() + ", Time: " + getTimeString() +
                ", Location: " + location + "\nDescription: " + description;
    }


    /**
     * Converts event into key-value map with string keys.
     *
     * @return a map representation of the event.
     */
    @NonNull
    public Map<String, Object> toMap() {
        Map<String,Object> map = new HashMap<>();

        map.put(LOCATION, this.getLocation());
        map.put(AUTHOR, this.getAuthor());
        map.put(TITLE, this.getTitle());
        map.put(TIME, this.getTimeString());
        map.put(DESCRIPTION, this.getDescription());
        map.put(DATE, this.getDateString());

        return map;
    }

    /**
     * Builds and returns a event from a key-value map with string keys.
     *
     * @return a event containing the same information as the map.
     */
    @NonNull
    public static EventItem fromMap(@NonNull Map<String, Object> map) {
        final EventItem eventItem = new EventItem();

        eventItem.location = map.containsKey(LOCATION) ? String.valueOf(map.get(LOCATION)) : "";
        eventItem.author = map.containsKey(AUTHOR) ? String.valueOf(map.get(AUTHOR)) : "";
        eventItem.title = map.containsKey(TITLE) ? String.valueOf(map.get(TITLE)) : "";
        eventItem.description = map.containsKey(DESCRIPTION) ? String.valueOf(map.get(DESCRIPTION)) : "";

        // Handle time and date with null checks
        String timeStr = map.containsKey(TIME) ? String.valueOf(map.get(TIME)) : "00:00";
        String dateStr = map.containsKey(DATE) ? String.valueOf(map.get(DATE)) : "01/01/2000";

        eventItem.time = parseTime(timeStr);
        eventItem.date = parseDate(dateStr);

        return eventItem;
    }

    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventItem eventItem = (EventItem) o;
        return Objects.equals(id, eventItem.id); // Compare by ID only
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}