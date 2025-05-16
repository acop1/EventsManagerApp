package com.example.eventsmanager.view;

public interface EventsMenuUI {
    interface Listener{
        void onCreateEventMenu();

        void onHomeScreen();

        void onSavedEvents();

        void onMyEvents();
    }
    void setListener(final Listener listener);
}
