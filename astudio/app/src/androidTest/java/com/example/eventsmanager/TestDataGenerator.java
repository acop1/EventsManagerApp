package com.example.eventsmanager;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;
import com.example.eventsmanager.model.PrivateEventItem;

public class TestDataGenerator {

    public static void setupTestEvents(Events eventsModel) {
        // Clear existing events
        eventsModel.getEventItems().clear();

        // Add public test events
        eventsModel.createEvent(new EventItem(
                "Conference Room B",
                "John Doe",
                "Team Standup",
                "09:00",
                "Daily sync meeting",
                "04/22/2025"
        ));

        eventsModel.createEvent(new EventItem(
                "Zoom Meeting",
                "Jane Smith",
                "Client Demo",
                "14:30",
                "Product demonstration",
                "04/23/2025"
        ));

        // Add private test events
        eventsModel.createEvent(new PrivateEventItem(
                "Executive Lounge",
                "CEO",
                "Board Meeting",
                "16:00",
                "Quarterly financial review",
                "04/24/2025"
        ));
    }

    public static String[] getExpectedEventTitles() {
        return new String[] {
                "Team Standup",
                "Client Demo",
                "Board Meeting"
        };
    }

    public static String[] getExpectedEventLocations() {
        return new String[] {
                "Conference Room B",
                "Zoom Meeting",
                "Executive Lounge"
        };
    }
}
