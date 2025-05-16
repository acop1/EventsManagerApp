package com.example.eventsmanager.view;

public interface CreateEventUI {
    interface Listener{
        void onCreateEvent(String location, String name, String time, String date, String description, boolean currentState);

        void onHomeScreen();

        void onEventsMenu();
    }

    void setListener(final Listener listener);
}
