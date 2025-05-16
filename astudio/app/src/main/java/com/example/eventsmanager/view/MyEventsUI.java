package com.example.eventsmanager.view;

import androidx.annotation.NonNull;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;

import java.util.List;

public interface MyEventsUI {

    interface Listener{


        void onHomeScreen();


        void onMyEventsReady(@NonNull MyEvents ui);

        void onEventsMenu();

        void onEventItemPage(EventItem lineItem, State state);
    }

    void updateEventDisplay(@NonNull List<EventItem> items);

    void setListener(final Listener listener);
}
