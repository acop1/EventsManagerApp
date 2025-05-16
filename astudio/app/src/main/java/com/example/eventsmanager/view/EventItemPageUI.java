package com.example.eventsmanager.view;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;

import java.util.Map;

public interface EventItemPageUI {
    void updateEventDisplay(Events eventsModel);

    interface Listener{
        void onHomeScreen();

        void onAddSavedEvent(EventItem lineItem);

        void onRemoveSavedEvent(EventItem lineItem);

        void onSavedEvents();

        void onMyEvents();

        void onUpdateEvent(EventItem lineItem, Map<String, Object> updates, boolean isPrivate);

        void onDeleteEvent(EventItem lineItem);

        void onManageInvites();
    }
    void setListener(final Listener listener);
}
