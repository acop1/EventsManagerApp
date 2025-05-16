package com.example.eventsmanager.model;

import java.util.HashSet;
import java.util.Set;

public class EventsStorage {
    private Set<EventItem> savedEvents;
    private HashSet<EventItem> privateEvents;
    private HashSet<EventItem> publicEvents;
    private String currentUserId;

    public EventsStorage(String userId) {
        this.currentUserId = userId;
        this.savedEvents = new HashSet<>();
        this.privateEvents = new HashSet<>();
        this.publicEvents = new HashSet<>();
    }

    public void saveEvent(EventItem event) {
        savedEvents.add(event);
        if (event instanceof PrivateEventItem) {
            privateEvents.add(event);
        } else {
            publicEvents.add(event);
        }
    }

    public void removeSavedEvent(EventItem event) {
        savedEvents.remove(event);
        if (event instanceof PrivateEventItem) {
            privateEvents.remove(event);
        } else {
            publicEvents.remove(event);
        }
    }

    public HashSet<EventItem> getPrivateSavedEvents() {
        return new HashSet<>(privateEvents);
    }

    public HashSet<EventItem> getPublicSavedEvents() {
        return new HashSet<>(publicEvents);
    }

    public boolean isSavedPublic(EventItem event) {
        return publicEvents.stream()
                .anyMatch(e -> e.getId().equals(event.getId()));
    }

    public boolean isSavedPrivate(PrivateEventItem event) {
        return privateEvents.stream()
                .anyMatch(e -> e.getId().equals(event.getId()));
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}