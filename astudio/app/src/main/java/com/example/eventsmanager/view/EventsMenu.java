package com.example.eventsmanager.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventsmanager.databinding.FragmentEventsMenuBinding;


public class EventsMenu extends Fragment implements EventsMenuUI {

    private FragmentEventsMenuBinding binding;
    Listener listener;       // observer to be notified of events of interest

    /**
     * OnCreateView() overrides method of the same name from superclass. It's purpose is to
     * inflate the xml layout associated with the fragment.
     * @param inflater object to use to inflate the xml layout (create actual graphical widgets out of the xml declarations)
     * @param container where the graphical widgets will be placed
     * @param savedInstanceState any saved state information to be restored (null if none exists)
     * @return the root of the layout that has just been inflated
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentEventsMenuBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    /**
     * OnViewCreated() overrides method of the same name from superclass. It is called by the
     * android platform after the layout has been inflated, and before the view transitions to the
     * created state.
     *
     * @param view the layout's root view
     * @param savedInstanceState any saved state information to be restored (null if none exists)
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.binding.createEvent.setOnClickListener((clickedView) -> {
            if (EventsMenu.this.listener != null ) EventsMenu.this.listener.onCreateEventMenu();
        });

        this.binding.saveEvents.setOnClickListener((clickedView) -> {
            if (EventsMenu.this.listener != null ) EventsMenu.this.listener.onSavedEvents();
        });

        this.binding.myEvents.setOnClickListener((clickedView) -> {
            if (EventsMenu.this.listener != null ) EventsMenu.this.listener.onMyEvents();
        });

        this.binding.homeButton.setOnClickListener((clickedView) -> {
            if (EventsMenu.this.listener != null ) EventsMenu.this.listener.onHomeScreen();
        });

        this.binding.backButton.setOnClickListener((clickedView) -> {
            if (EventsMenu.this.listener != null ) EventsMenu.this.listener.onHomeScreen();
        });
    }

    /**
     * Sets the listener object to be notified of events of interest originating in the user interface.
     *
     * @param listener the listener object.
     */
    @Override
    public void setListener(final Listener listener) {
        this.listener = listener;
    }
}