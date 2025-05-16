package com.example.eventsmanager.view;

import androidx.annotation.NonNull;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;

import java.util.List;

public interface HomeScreenUI {
    void updateEventDisplay(@NonNull List<EventItem> items);

    interface Listener{
        void onEventsMenu();

        void onHomeScreenReady(@NonNull HomeScreenUI ui);

        void onManageInvites();

        void onEventItemPage(EventItem lineItem, State origin);

        List<EventItem> onSearchRequest(String query);
    }
    void setListener(final Listener listener);


}
