package com.example.eventsmanager.persistence;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;
import com.example.eventsmanager.model.EventsStorage;
import com.example.eventsmanager.model.PrivateEventItem;
import com.example.eventsmanager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that implements the persistence facade by saving/loading data to/from a Google Cloud
 * Firestore database.
 */
public class FirestoreFacade implements PersistenceFacade {

    private static final String EVENTS_COLLECTION = "events";
    private final CollectionReference eventsCref = FirebaseFirestore.getInstance().collection(EVENTS_COLLECTION);

    private static final String PRIVATE_EVENTS_COLLECTION = "private_events";
    private final CollectionReference privateEventsCref = FirebaseFirestore.getInstance().collection(PRIVATE_EVENTS_COLLECTION);

    private static final String USERS_COLLECTION = "users";
    private final CollectionReference usersCref = FirebaseFirestore.getInstance().collection(USERS_COLLECTION);

    private static final String SAVED_EVENTS_COLLECTION = "saved_events";
    private final CollectionReference savedEventsCref = FirebaseFirestore.getInstance().collection(SAVED_EVENTS_COLLECTION);

    /**
     * Saves new sale to underlying persistence subsystem.
     *
     * @param eventItem the sale object to be saved.
     */
    @Override
    public void saveEventItem(@NonNull EventItem eventItem) {
        this.eventsCref.add(eventItem.toMap());
    }

    /**
     * Deletes an event from the events collection
     * @param eventId The ID of the event to delete
     */
    @Override
    public void deleteEventItem(@NonNull String eventId) {
        eventsCref.document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.i("Events Manager", "Event successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error deleting event", e);
                });
    }

    /**
     * Updates an event in the events collection
     * @param eventId The ID of the event to update
     * @param updates Map of fields to update
     */
    @Override
    public void updateEventItem(@NonNull String eventId, @NonNull Map<String, Object> updates) {
        eventsCref.document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.i("Events Manager", "Event successfully updated!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error updating event", e);
                });
    }

    @Override
    public void savePrivateEventItem(@NonNull PrivateEventItem eventItem) {
        this.privateEventsCref.add(eventItem.toMap());
    }

    /**
     * Deletes a private event from the private_events collection
     * @param eventId The ID of the private event to delete
     */
    @Override
    public void deletePrivateEventItem(@NonNull String eventId) {
        privateEventsCref.document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.i("Events Manager", "Private event successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error deleting private event", e);
                });
    }

    /**
     * Updates a private event in the private_events collection
     * @param eventId The ID of the private event to update
     * @param updates Map of fields to update
     */
    @Override
    public void updatePrivateEventItem(@NonNull String eventId, @NonNull Map<String, Object> updates) {
        privateEventsCref.document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.i("Events Manager", "Private event successfully updated!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error updating private event", e);
                });
    }


    /**
     * Issues a ledger retrieval operation.
     *
     * @param listener the observer to be notified of query result.
     */
    @Override
    public void loadEvents(@NonNull DataListener<Events> listener) {
        final Events events = new Events();
        this.eventsCref
                .get()
                .addOnSuccessListener(qsnap -> {
                    for (DocumentSnapshot dsnap : qsnap) {
                        final EventItem eventItem = EventItem.fromMap(dsnap.getData());
                        eventItem.setId(dsnap.getId());  // Set the document ID
                        events.createEvent(eventItem);
                    }
                    Log.i("Events Manager", "events data read");
                    listener.onDataReceived(events);
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error loading events", e);
                    listener.onNoDataFound();
                });

        this.privateEventsCref
                .get()
                .addOnSuccessListener(qsnap -> {
                    for (DocumentSnapshot dsnap : qsnap) {
                        final PrivateEventItem eventItems = PrivateEventItem.fromMap(dsnap.getData());
                        eventItems.setId(dsnap.getId());  // Set the document ID
                        events.createEvent(eventItems);
                    }
                    Log.i("Events Manager", "private events data read");
                    listener.onDataReceived(events);
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error loading private events", e);
                    listener.onNoDataFound();
                });
    }

    @Override
    public void saveEventForUser(String userId, EventItem event) {
        Map<String, Object> savedEventData = new HashMap<>();
        savedEventData.put("userId", userId);
        savedEventData.put("eventId", event.getId());
        savedEventData.put("eventData", event.toMap());
        savedEventData.put("isPrivate", event instanceof PrivateEventItem);

        savedEventsCref.document(userId + "_" + event.getId())
                .set(savedEventData)
                .addOnSuccessListener(aVoid -> Log.i("Events Manager", "Event saved for user"));
    }

    @Override
    public void removeSavedEventForUser(String userId, String eventId) {
        savedEventsCref.document(userId + "_" + eventId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.i("Events Manager", "Saved event removed"));
    }

    @Override
    public void loadSavedEventsForUser(String userId, DataListener<EventsStorage> listener) {
        savedEventsCref.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    EventsStorage storage = new EventsStorage(userId);
                    for (DocumentSnapshot document : querySnapshot) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            boolean isPrivate = (boolean) data.getOrDefault("isPrivate", false);
                            Map<String, Object> eventData = (Map<String, Object>) data.get("eventData");

                            EventItem event;
                            if (isPrivate) {
                                event = PrivateEventItem.fromMap(eventData);
                            } else {
                                event = EventItem.fromMap(eventData);
                            }
                            event.setId((String) data.get("eventId"));
                            storage.saveEvent(event);
                        }
                    }
                    listener.onDataReceived(storage);
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Error loading saved events", e);
                    listener.onNoDataFound();
                });
    }


    @Override
    public void sendInvitationToUser(String username, String eventId, String eventTitle, String author) {
        Map<String, Object> invitationData = new HashMap<>();
        invitationData.put("eventId", eventId);
        invitationData.put("eventTitle", eventTitle);
        invitationData.put("status", "pending");
        invitationData.put("author", author);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(username)
                .collection("invitations")
                .document(eventId)
                .set(invitationData)
                .addOnSuccessListener(aVoid -> Log.i("Events Manager", "Invitation sent to " + username))
                .addOnFailureListener(e -> Log.e("Events Manager", "Failed to send invitation to " + username, e));
    }

    @Override
    public void loadInvitationsForUser(String username, @NonNull DataListener<List<Map<String, Object>>> listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(username)
                .collection("invitations")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> invitations = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        invitations.add(doc.getData());
                    }
                    listener.onDataReceived(invitations);
                })
                .addOnFailureListener(e -> {
                    Log.e("Events Manager", "Failed to load invitations", e);
                    listener.onNoDataFound();
                });
    }


    /**
     *  Creates an entry for the specified User in the underlying persistence subsystem.
     *
     * @param user the user to be created
     * @param listener the observer to be notified of the query result. OnYesResult() is called if
     *                 a new user was created. Conversely, OnNoResult() is called if a user with
     *                 the specified username already existed.
     */
    @Override
    public void createUserIfNotExists(@NonNull User user, @NonNull BinaryResultListener listener) {
        this.retrieveUser(user.getUsername(),
                new DataListener<>() {
                    @Override
                    public void onDataReceived(@NonNull User user) { // username already exists
                        listener.onNoResult();
                    }

                    @Override
                    public void onNoDataFound() { // username doesn't yet exist, so create new user
                        usersCref.document(user.getUsername())
                                .set(user.toMap())
                                .addOnSuccessListener(unused -> listener.onYesResult());
                    }
                });
    }

    /**
     * Retrieves the User with the specified username from the underlying persistence subsystem.
     *
     * @param username the username of the user to be retrieved.
     * @param listener observer to be notified of query result. onDataReceived() is called if a
     *                 user with the specified username was found. Otherwise, onNoDataFound() is
     *                 called.
     */
    @Override
    public void retrieveUser(@NonNull String username, @NonNull DataListener<User> listener) {
        this.usersCref.document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dsnap) {
                        if (dsnap.exists()) { // returns true iff the snapshot contains data
                            User user = User.fromMap(dsnap.getData());
                            listener.onDataReceived(user);
                        } else listener.onNoDataFound();
                    }
                });
    }
}
