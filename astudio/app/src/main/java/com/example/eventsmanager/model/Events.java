package com.example.eventsmanager.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.*;

public class Events implements Serializable {
    private Map<String, EventItem> events;

    public Events() {
        this.events = new HashMap<>();
    }

    public void createEvent(EventItem eventItem) {
        this.events.put(eventItem.getTitle(), eventItem);
    }

    public List<EventItem> searchEvents(String searchQuery, String author) {
        List<EventItem> eventList = new ArrayList<>();
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return eventList;
        }

        String query = searchQuery.toLowerCase().trim();
        for (EventItem event : this.getHomeEventItems(author)) {
            if (event.getTitle().toLowerCase().contains(query) ||
                    event.getAuthor().toLowerCase().contains(query) ||
                    event.getLocation().toLowerCase().contains(query) ||
                    event.getDateString().toLowerCase().contains(query) ||
                    event.getTimeString().toLowerCase().contains(query) ||
                    event.getDescription().toLowerCase().contains(query)) {
                eventList.add(event);
            }
        }
        return eventList;
    }


    public HashMap<String, EventItem> getPublicEvents() {
        HashMap<String, EventItem> filteredPublic = new HashMap<>();
        for (Map.Entry<String, EventItem> entry : this.events.entrySet()) {
            if(!(entry.getValue() instanceof PrivateEventItem)) {
                filteredPublic.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredPublic;
    }

    public HashMap<String, PrivateEventItem> getPrivateEvents() {
        HashMap<String, PrivateEventItem> filteredPrivate = new HashMap<>();
        for (Map.Entry<String, EventItem> entry : this.events.entrySet()) {
            if(entry.getValue() instanceof PrivateEventItem) {
                filteredPrivate.put(entry.getKey(), (PrivateEventItem) entry.getValue());
            }
        }
        return filteredPrivate;
    }

    public List<EventItem> getEventItems(){
        return new ArrayList<>(events.values());
    }

    public List<EventItem> getHomeEventItems(String curUser) {
        List<EventItem> combinedList = this.getPublicEventItems();
        combinedList.addAll(this.getEventsByAuthor(curUser));
        List<EventItem> returnList = new ArrayList<>(new HashSet<>(combinedList));
        return returnList;
    }
    public List<EventItem> getPublicEventItems(){
        return new ArrayList<>(this.getPublicEvents().values());
    }

    public List<PrivateEventItem> getPrivateEventItems(String author){
        List<PrivateEventItem> privateEvents =  new ArrayList<>(this.getPrivateEvents().values());
        List<PrivateEventItem> userPrivateEvents = new ArrayList<>();
        for (PrivateEventItem event : privateEvents) {
            if (event.getAuthor().equals(author)) {
                userPrivateEvents.add(event);
            }
        }
        return userPrivateEvents;
    }

    public List<EventItem> getEventsByAuthor(String author) {
        List<EventItem> userEvents = new ArrayList<>();
        for (EventItem event : this.getEventItems()) {
            if (event.getAuthor().equals(author)) {
                userEvents.add(event);
            }
        }
        return userEvents;
    }

    public void removeEvent(String title) {
        this.events.remove(title);
    }

    public EventItem getEvent(String title){
        return events.get(title);
    }


}