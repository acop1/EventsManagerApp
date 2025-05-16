package com.example.eventsmanager.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventsmanager.R;
import com.example.eventsmanager.databinding.MainBinding;


public class MainUI {
    private final MainBinding binding;
    private final FragmentManager fmanager;

    /**
     * Constructor method.
     * @param factivity The android activity the screen is associated with.
     */
    public MainUI (@NonNull FragmentActivity factivity) {
        this.binding = MainBinding.inflate(LayoutInflater.from(factivity));
        this.fmanager = factivity.getSupportFragmentManager();

        // eliminates colored bar at top of screen
        EdgeToEdge.enable(factivity);
        ViewCompat.setOnApplyWindowInsetsListener(this.binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Replaces the contents of the screen's fragment container with the one passed in as an argument.
     *
     * @param frag The fragment to be displayed.
     */
    public void displayFragment(@NonNull Fragment frag) {
        FragmentTransaction ftrans = this.fmanager.beginTransaction();
        ftrans.replace(this.binding.fragmentContainerView.getId(), frag);
        ftrans.commit();
    }

    public Fragment getCurrentFragment() {
        return fmanager.findFragmentById(binding.fragmentContainerView.getId());
    }

    /**
     * Retrieve the graphical widget (android view) at the root of the screen hierarchy.
     *
     * @return the screen's root android view (widget)
     */
    @NonNull
    public View getRootView() { return this.binding.getRoot(); }
}