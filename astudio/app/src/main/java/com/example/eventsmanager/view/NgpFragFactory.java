package com.example.eventsmanager.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

public class NgpFragFactory extends FragmentFactory {
    private final UI.Listener iuListener; // the listener to attach to fragments

    /**
     * Constructor method.
     *
     * @param uiListener the listener to attach to fragments.
     */
    public NgpFragFactory(UI.Listener uiListener) {
        this.iuListener = uiListener;
    }

    /**
     * Method used by fragment manager/transaction to instantiate fragments.
     *
     * @param classLoader object to use to load fragment class.
     * @param className name of fragment class to instantiate.
     * @return instantiated fragment.
     */
    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        Fragment frag = super.instantiate(classLoader, className); // delegate construction to superclass
        if (frag instanceof UI) ((UI)frag).setListener(this.iuListener); // attach listener if it's one of our fragments
        return frag;
    }
}
