package com.example.eventsmanager.controller;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;
import com.example.eventsmanager.model.EventsStorage;
import com.example.eventsmanager.model.PrivateEventItem;
import com.example.eventsmanager.model.User;
import com.example.eventsmanager.persistence.FirestoreFacade;
import com.example.eventsmanager.persistence.PersistenceFacade;
import com.example.eventsmanager.view.CreateEvent;
import com.example.eventsmanager.view.CreateEventUI;
import com.example.eventsmanager.view.EventItemPage;
import com.example.eventsmanager.view.EventItemPageUI;
import com.example.eventsmanager.view.EventsMenu;
import com.example.eventsmanager.view.EventsMenuUI;
import com.example.eventsmanager.view.HomeScreen;
import com.example.eventsmanager.view.HomeScreenUI;
import com.example.eventsmanager.view.Login;
import com.example.eventsmanager.view.LoginUI;
import com.example.eventsmanager.view.MainUI;
import com.example.eventsmanager.view.ManageInvites;
import com.example.eventsmanager.view.ManageInvitesUI;
import com.example.eventsmanager.view.MyEvents;
import com.example.eventsmanager.view.MyEventsUI;
import com.example.eventsmanager.view.NgpFragFactory;
import com.example.eventsmanager.view.SavedEvents;
import com.example.eventsmanager.view.SavedEventsUI;
import com.example.eventsmanager.view.State;
import com.example.eventsmanager.view.UI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements EventsMenuUI.Listener, HomeScreenUI.Listener, CreateEventUI.Listener, ManageInvitesUI.Listener, EventItemPageUI.Listener, SavedEventsUI.Listener, LoginUI.Listener, UI.Listener, MyEventsUI.Listener {

    private static final String CUR_EVENT = "curEvent";
    private static final String CUR_STATE = "curState";
    private static final String CUR_USER = "curUser";
    private MainUI mainUI;
    private Events eventsModel;
    private EventsStorage savedEvents;

    private PersistenceFacade persistenceFacade; // data persistence delegate

    private State curState; // keeps track of what screen we're on

    private User curUser = new User(); // keeps track of who the currently logged-on user is

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseApp.initializeApp(this);
        this.getSupportFragmentManager().setFragmentFactory(new NgpFragFactory((UI.Listener) this));
        super.onCreate(savedInstanceState);
        this.mainUI = new MainUI(this);


        setContentView(this.mainUI.getRootView());

        this.persistenceFacade = new FirestoreFacade(); // init persistence facade

        // load events with deferred processing
        this.persistenceFacade.loadEvents(new PersistenceFacade.DataListener<Events>() {
            @Override
            public void onDataReceived(@NonNull Events eventsModel) {
                Log.i("Events Manager", "Events received by controller");
                MainActivity.this.eventsModel = eventsModel;

                if (MainActivity.this.curState == State.HOME) { // are we in the HOME state?
                    HomeScreen lfrag = (HomeScreen) mainUI.getCurrentFragment();
                    lfrag.updateEventDisplay(MainActivity.this.eventsModel.getHomeEventItems(curUser.getUsername()));
                }
            }

            @Override
            public void onNoDataFound() {}
        });

        if (savedInstanceState == null) { // indicates first activity launch
            this.onLogin();
        }
        else { // indicates reconstruction, should reload state
            this.curState = State.valueOf(savedInstanceState.getString(CUR_STATE));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.eventsModel = savedInstanceState.getSerializable(CUR_EVENT, Events.class);
                this.curUser = savedInstanceState.getSerializable(CUR_USER, User.class);
            }
        }
        loadSavedEvents();

    }

    //Registering code
    /* AuthUI.Listener implementation start */
    /**
     * Called to indicate the user's desire to register an account.
     *
     * @param username the user-provided username.
     * @param password the user-provided password.
     * @param ui the generator of the event.
     */
    @Override
    public void onRegister(String username, String password, LoginUI ui) {
        User user = new User(username, password);
        this.persistenceFacade.createUserIfNotExists(user, new PersistenceFacade.BinaryResultListener() {
            @Override
            public void onYesResult() { // successfully registered
                ui.onRegisterSuccess();
            }

            @Override
            public void onNoResult() { // user with specified username already exists
                ui.onUserAlreadyExists();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CUR_STATE, this.curState.name());
        outState.putSerializable(CUR_EVENT, this.eventsModel);
        outState.putSerializable(CUR_USER, this.curUser);
    }

    /**
     * Called to indicate the user's desire to sign onto the system.
     *
     * @param username the user-provided username.
     * @param password the user-provided password.
     * @param ui the generator of the event.
     */
    @Override
    public void onSigninAttempt(String username, String password, LoginUI ui) {
        this.persistenceFacade.retrieveUser(username, new PersistenceFacade.DataListener<User>() {
            @Override
            public void onDataReceived(@NonNull User user) { // user exists
                if (user.validatePassword(password)){ // is the password correct?
                    MainActivity.this.curUser = user;
                    loadSavedEvents();
                    MainActivity.this.onHomeScreen();
                } else ui.onInvalidCredentials(); // incorrect password
            }

            @Override
            public void onNoDataFound() { // user doesn't exist
                ui.onInvalidCredentials();
            }
        });
    }

    private void loadSavedEvents() {
        if (curUser != null) {
            this.persistenceFacade.loadSavedEventsForUser(curUser.getUsername(),
                    new PersistenceFacade.DataListener<EventsStorage>() {
                        @Override
                        public void onDataReceived(@NonNull EventsStorage eventsStorage) {
                            MainActivity.this.savedEvents = eventsStorage;
                            Log.i("Events Manager", "Saved Events loaded: " +
                                    eventsStorage.getPublicSavedEvents().size() + " public, " +
                                    eventsStorage.getPrivateSavedEvents().size() + " private");

                            // Update UI if we're on the SavedEvents screen
                            if (curState == State.SAVED) {
                                Fragment frag = mainUI.getCurrentFragment();
                                if (frag instanceof SavedEventsUI) {
                                    ((SavedEventsUI) frag).updateEventDisplay(savedEvents);
                                }
                            }
                        }

                        @Override
                        public void onNoDataFound() {
                            MainActivity.this.savedEvents = new EventsStorage(curUser.getUsername());
                        }
                    });
        }
    }
    //Done with registration code


    private void onLogin() {
        this.curState = State.LOGIN;
        Login loginfrag = new Login();
        loginfrag.setListener(this);
        this.mainUI.displayFragment(loginfrag);
    }

    public void onHomeScreen() {
        this.curState = State.HOME;
        HomeScreen screen = new HomeScreen();
        screen.setUser(curUser.getUsername());
        screen.setListener(this);
        this.mainUI.displayFragment(screen);
    }

    @Override
    public void onManageInvitesReady(@NonNull ManageInvitesUI ui) {
        ui.updatePriavteEventDisplay(eventsModel, curUser.getUsername());

        this.persistenceFacade.loadInvitationsForUser(curUser.getUsername(), new PersistenceFacade.DataListener<>() {
            @Override
            public void onDataReceived(@NonNull List<Map<String, Object>> invitations) {
                ui.displayInvitations(invitations);
            }

            @Override
            public void onNoDataFound() {
                ui.displayInvitations(new ArrayList<>());
            }
        });
    }


    @Override
    public void onHomeScreenReady(@NonNull HomeScreenUI ui) {
        ui.updateEventDisplay(eventsModel.getHomeEventItems(curUser.getUsername()));
    }

    @Override
    public void onMyEventsReady(@NonNull MyEvents ui) {
        ui.updateEventDisplay((eventsModel.getEventsByAuthor(curUser.getUsername())));
    }

    //IDK what to do with these
    @Override
    public void onCreateEvent(String location, String title, String time,
                              String date, String description, boolean eventPrivate) {
        EventItem eventItem;
        String author = curUser.getUsername();
        if (eventPrivate) {
            eventItem = new PrivateEventItem(location, author, title, time, description, date);
            this.persistenceFacade.savePrivateEventItem((PrivateEventItem) eventItem); // issue private event save
        } else {
            eventItem = new EventItem(location, author, title, time, description, date);
            this.persistenceFacade.saveEventItem(eventItem); // issue event save
        }
        eventsModel.createEvent(eventItem);
        updateAllDisplays();

    }


    @Override
    public void onRemoveInvites(String selectedEvent, String[] invitees) {
        // Second step - actually remove the invites
        PrivateEventItem event = eventsModel.getPrivateEvents().get(selectedEvent);
        String eventId = event.getId();
        if (event != null) {
            event.removeInvitations(invitees);
            Map<String, Object> invites = new HashMap<>();
            invites.put("invitations", new ArrayList<>(event.getInvitations()));
            this.persistenceFacade.updatePrivateEventItem(eventId, invites);
            updateAllDisplays();
        }

    }

    public void onEventsMenu() {
        this.curState = State.MENU;
        EventsMenu eventsMenu = new EventsMenu();
        eventsMenu.setListener(this);
        this.mainUI.displayFragment(eventsMenu);
    }

    public void onEventItemPage(EventItem lineItem, State origin) {
        EventItemPage eventItemPage = new EventItemPage(lineItem, this.savedEvents, origin);
        this.curState = State.ITEM;
        eventItemPage.setListener(this);
        this.mainUI.displayFragment(eventItemPage);
    }

    public void onCreateEventMenu() {
        this.curState = State.CREATE;
        CreateEvent createEvent = new CreateEvent();
        createEvent.setListener(this);
        this.mainUI.displayFragment(createEvent);
    }

    public void onManageInvites() {
        this.curState = State.INVITES;
        ManageInvites manageInvites = new ManageInvites(this.eventsModel);
        manageInvites.setListener(this);
        this.mainUI.displayFragment(manageInvites);
    }


    public void onSavedEvents() {
        this.curState = State.SAVED;
        SavedEvents savedEvents = new SavedEvents(this.savedEvents);
        savedEvents.setListener(this);
        this.mainUI.displayFragment(savedEvents);

    }

    public void onMyEvents() {
        this.curState = State.MY;
        MyEvents myEvents = new MyEvents();
        myEvents.setListener(this);
        this.mainUI.displayFragment(myEvents);
    }

    public void updateAllDisplays() {
        // Reload events from Firestore to ensure we have the latest data
        this.persistenceFacade.loadEvents(new PersistenceFacade.DataListener<Events>() {
            @Override
            public void onDataReceived(@NonNull Events eventsModel) {
                MainActivity.this.eventsModel = eventsModel;

                // Update current fragment
                Fragment currentFragment = mainUI.getCurrentFragment();
                if (currentFragment instanceof HomeScreenUI) {
                    ((HomeScreenUI) currentFragment).updateEventDisplay(
                            eventsModel.getHomeEventItems(curUser.getUsername()));
                } else if (currentFragment instanceof ManageInvitesUI) {
                    ((ManageInvitesUI) currentFragment).updatePriavteEventDisplay(
                            eventsModel, curUser.getUsername());
                } else if (currentFragment instanceof SavedEventsUI) {
                    ((SavedEventsUI) currentFragment).updateEventDisplay(savedEvents);
                } else if (currentFragment instanceof EventItemPageUI) {
                    ((EventItemPageUI) currentFragment).updateEventDisplay(eventsModel);
                } else if (currentFragment instanceof MyEventsUI) {
                    ((MyEventsUI) currentFragment).updateEventDisplay(
                            eventsModel.getEventsByAuthor(curUser.getUsername()));
                }
            }

            @Override
            public void onNoDataFound() {
                // Handle no data case if needed
            }
        });
    }

    @Override
    public void onAddSavedEvent(EventItem eventItem) {
        this.savedEvents.saveEvent(eventItem);
        this.persistenceFacade.saveEventForUser(curUser.getUsername(), eventItem);
    }

    @Override
    public void onRemoveSavedEvent(EventItem eventItem) {
        this.savedEvents.removeSavedEvent(eventItem);
        this.persistenceFacade.removeSavedEventForUser(curUser.getUsername(), eventItem.getId());
    }

    @Override
    public List<EventItem> onSearchRequest(String searchQuery) {
        List<EventItem> results = eventsModel.searchEvents(searchQuery, curUser.getUsername());
        return results;
    }
    public Events getEventsModel(){
        return this.eventsModel;
    }

    @Override
    public void onUpdateEvent(EventItem originalEvent, Map<String, Object> updates, boolean isPrivate) {
        String eventId = originalEvent.getId();

        if (eventId == null || eventId.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Error: Event ID missing", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Remove the old event from the model first
        eventsModel.removeEvent(originalEvent.getTitle());

        if (isPrivate) {
            // If changing to private or already private
            if (originalEvent instanceof PrivateEventItem) {
                this.persistenceFacade.updatePrivateEventItem(eventId, updates);
            } else {
                // Convert public to private - need to delete public and create private
                this.persistenceFacade.deleteEventItem(eventId);

                // Create new private event with updated values
                PrivateEventItem newEvent = new PrivateEventItem(
                        (String) updates.get(EventItem.LOCATION),
                        originalEvent.getAuthor(),
                        (String) updates.get(EventItem.TITLE),
                        (String) updates.get(EventItem.TIME),
                        (String) updates.get(EventItem.DESCRIPTION),
                        (String) updates.get(EventItem.DATE)
                );
                newEvent.setId(eventId); // Keep the same ID

                this.persistenceFacade.savePrivateEventItem(newEvent);
                eventsModel.createEvent(newEvent);
            }
        } else {
            // If changing to public or already public
            if (originalEvent instanceof PrivateEventItem) {
                // Convert private to public - need to delete private and create public
                this.persistenceFacade.deletePrivateEventItem(eventId);

                // Create new public event with updated values
                EventItem newEvent = new EventItem(
                        (String) updates.get(EventItem.LOCATION),
                        originalEvent.getAuthor(),
                        (String) updates.get(EventItem.TITLE),
                        (String) updates.get(EventItem.TIME),
                        (String) updates.get(EventItem.DESCRIPTION),
                        (String) updates.get(EventItem.DATE)
                );
                newEvent.setId(eventId); // Keep the same ID

                this.persistenceFacade.saveEventItem(newEvent);
                eventsModel.createEvent(newEvent);
            } else {
                this.persistenceFacade.updateEventItem(eventId, updates);
            }
        }

        // Refresh the display
        updateAllDisplays();
        onMyEvents();
    }

    @Override
    public void onDeleteEvent(EventItem eventItem) {
        if (eventItem instanceof PrivateEventItem) {
            this.persistenceFacade.deletePrivateEventItem(eventItem.getId());
        }
        else{
            this.persistenceFacade.deleteEventItem(eventItem.getId());
        }
        this.eventsModel.removeEvent(eventItem.getTitle());
        this.savedEvents.removeSavedEvent(eventItem);
        this.persistenceFacade.removeSavedEventForUser(curUser.getUsername(),eventItem.getId());
    }

    @Override
    public void onAddInvites(String selectedEvent, String[] invitees) {
        PrivateEventItem event = eventsModel.getPrivateEvents().get(selectedEvent);

        if (event != null) {
            String eventId = event.getId();
            String author = event.getAuthor();
            String title = event.getTitle();

            // Update event's own invitation list
            event.addInvitations(invitees);
            Map<String, Object> invitesMap = new HashMap<>();
            invitesMap.put("invitations", new ArrayList<>(event.getInvitations()));
            this.persistenceFacade.updatePrivateEventItem(eventId, invitesMap);

            // Send invitations to each user
            for (String invitee : invitees) {
                this.persistenceFacade.sendInvitationToUser(invitee, eventId, title, author);
            }

            updateAllDisplays();
        }
    }

}

/*
-> create a notifications screen
-> this will have all invites, whether they were accepted or declined
-> Create an accept/decline field for invites
-> an accepted invite should display the event in myEvents
-> it should send the creator a notification that the event was accepted
-> a declined invite should no longer appear on the invitees notification screen, manage invites will show declined
-> it should send the creator a notification that the event was declined

-> Invites should only be sent to users who exist in the database
-> failed invites shouldn't go anywhere and should give an error
 */




