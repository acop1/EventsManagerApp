package com.example.eventsmanager.persistence;

import androidx.annotation.NonNull;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;
import com.example.eventsmanager.model.EventsStorage;
import com.example.eventsmanager.model.PrivateEventItem;
import com.example.eventsmanager.model.User;

import java.util.List;
import java.util.Map;


/**
 * Interface that specifies a contract that all persistence solutions must fulfill.
 */
public interface PersistenceFacade {


    /**
     * Issues a ledger save operation.
     *
     *
     */
    interface DataListener<T> {

        /**
         * Called when the requested data is successfully received.
         * @param data the data that was received from the persistence subsystem.
         */
        void onDataReceived(@NonNull T data);



        /**
         * Called when the requested data isn't found in the underlying persistence subsystem.
         */
        void onNoDataFound();
    }

    /**
     * Interface that classes interested in being notified of binary (i.e., true vs false) events
     * from the persistence layer should implement.
     */
    interface BinaryResultListener {
        /**
         * Called when the answer to the issued query is positive.
         */
        void onYesResult();
        /**
         * Called when the answer to the issued query is negative.
         */
        void onNoResult();
    }

    /**
     * Saves new sale to underlying persistence subsystem.
     *
     * @param eventItem the sale object to be saved.
     */
    void saveEventItem(@NonNull final EventItem eventItem);

    void deleteEventItem(@NonNull String eventId);

    void updateEventItem(@NonNull String eventId, @NonNull Map<String, Object> updates);

    void savePrivateEventItem(@NonNull PrivateEventItem eventItem);

    void deletePrivateEventItem(@NonNull String eventId);

    void updatePrivateEventItem(@NonNull String eventId, @NonNull Map<String, Object> updates);

    @NonNull
    void loadEvents(@NonNull DataListener<Events> listener);

    void saveEventForUser(String userId, EventItem event);

    void removeSavedEventForUser(String userId, String eventId);

    void loadSavedEventsForUser(String userId, DataListener<EventsStorage> listener);

    void sendInvitationToUser(String username, String eventId, String eventTitle, String author);

    void removeInvitationFromUser(String username, String eventId);

    void loadInvitationsForUser(String username, @NonNull DataListener<List<Map<String, Object>>> listener);

    /**
     *  Creates an entry for the specified User in the underlying persistence subsystem.
     *
     * @param user the user to be created
     * @param listener the observer to be notified of the query result. OnYesResult() is called if
     *                 a new user was created. Conversely, OnNoResult() is called if a user with
     *                 the specified username already existed.
     */
    void createUserIfNotExists(@NonNull User user, @NonNull BinaryResultListener listener);

    /**
     * Retrieves the User with the specified username from the underlying persistence subsystem.
     *
     * @param username the username of the user to be retrieved.
     * @param listener observer to be notified of query result. onDataReceived() is called if a
     *                 user with the specified username was found. Otherwise, onNoDataFound() is
     *                 called.
     */
    void retrieveUser(@NonNull String username, @NonNull DataListener<User> listener);
}
