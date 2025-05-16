package com.example.eventsmanager.view;

import androidx.annotation.NonNull;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.EventsStorage;

public interface SavedEventsUI {

    void updateEventDisplay(EventsStorage savedEvents);

    interface Listener{



        void onEventsMenu();

        void onEventItemPage(EventItem lineItem, State origin);

        void onHomeScreen();
    }

    void setListener(final Listener listener);
}
