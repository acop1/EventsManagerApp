package com.example.eventsmanager.view;

import androidx.annotation.NonNull;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;

import java.util.List;
import java.util.Map;

public interface ManageInvitesUI {
    void updatePriavteEventDisplay(@NonNull Events eventsModel, String curUser);

    void displayInvitations(List<Map<String, Object>> invitations);

    interface Listener{

        void onHomeScreen();

        void onManageInvitesReady(@NonNull ManageInvitesUI ui);

        void onAddInvites(String eventItemName, String[] invitees);

        void onRemoveInvites(String title, String[] invitees);

        void onEventItemPage(EventItem event, State state);
    }
    void setListener(final Listener listener);
}
