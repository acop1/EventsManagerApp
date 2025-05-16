package com.example.eventsmanager.view;

/**
 * General base interface for UI components.
 */
public interface UI {

    /**
     * General base interface for UI components.
     */
    interface Listener {}

    /**
     * Sets the listener object to be notified of events of interest originating in the user interface.
     *
     * @param listener the listener object.
     */
    <L extends Listener> void setListener(final L listener);
}
