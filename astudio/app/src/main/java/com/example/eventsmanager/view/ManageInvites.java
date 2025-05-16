package com.example.eventsmanager.view;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import com.example.eventsmanager.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsmanager.databinding.EventItemBinding;
import com.example.eventsmanager.databinding.FragmentManageInvitesBinding;
import com.example.eventsmanager.databinding.InviteItemBinding;
import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;
import com.example.eventsmanager.model.PrivateEventItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManageInvites extends Fragment implements ManageInvitesUI {

    private FragmentManageInvitesBinding binding;
    Listener listener;

    PrivateEventItem eventItem;
    String[] invitees;
    Switch mySwitch;
    TextView addText, removeText, invitations;
    Button addButton, removeButton;
    Events events;

    private final EventItemsAdapter eventItemsAdapter = new EventItemsAdapter();
    private final ReceivedInvitesAdapter receivedInvitesAdapter = new ReceivedInvitesAdapter();

    public ManageInvites(Events events){
        this.events = events;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentManageInvitesBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.binding.privateEventList.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.privateEventList.setAdapter(this.eventItemsAdapter);


        this.binding.receivedInvitesList.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.receivedInvitesList.setAdapter(receivedInvitesAdapter);


        // Initialize UI elements
        addText = this.binding.addText;
        removeText = this.binding.removeText;
        mySwitch = this.binding.addRemoveSwitch;
        addButton = this.binding.addInvitesButton;
        removeButton = this.binding.removeInvitesButton;
        invitations = this.binding.inviteList;

        // Initially hide the invite management UI
        hideInviteManagementUI();

        this.binding.homeButton.setOnClickListener((clickedView) -> {
            if (ManageInvites.this.listener != null) ManageInvites.this.listener.onHomeScreen();
        });

        this.binding.backButton.setOnClickListener((clickedView) -> {
            if (ManageInvites.this.listener != null) ManageInvites.this.listener.onHomeScreen();
        });

        this.binding.addInvitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get item's name
                final Editable inviteesEditable = ManageInvites.this.binding.editTextInputPeople.getText();
                invitees = inviteesEditable.toString().split(", ");

                // check that we have all required info
                if (inviteesEditable.toString().isBlank()){
                    Snackbar.make(v, R.string.missing_invites_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (ManageInvites.this.listener != null) { // notify listener
                    ManageInvites.this.listener.onAddInvites(eventItem.getTitle(), invitees);

                    // clear fields to make them ready for the next item
                    inviteesEditable.clear();
                    updateInvites(eventItem);
                }
            }
        });

        this.binding.removeInvitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get item's name
                final Editable inviteesEditable = ManageInvites.this.binding.editTextInputPeople.getText();
                invitees = inviteesEditable.toString().split(", ");

                // check that we have all required info
                if (inviteesEditable.toString().isBlank()){
                    Snackbar.make(v, R.string.missing_invites_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (ManageInvites.this.listener != null) { // notify listener
                    ManageInvites.this.listener.onRemoveInvites(eventItem.getTitle(), invitees);

                    // clear fields to make them ready for the next item
                    inviteesEditable.clear();
                    updateInvites(eventItem);
                }
            }
        });


        if (this.listener != null) this.listener.onManageInvitesReady(this);
    }

    private void hideInviteManagementUI() {
        mySwitch.setVisibility(View.INVISIBLE);
        addText.setVisibility(View.INVISIBLE);
        removeText.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.INVISIBLE);
        removeButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    @Override
    public void updatePriavteEventDisplay(@NonNull Events eventsModel, String curUser) {

        List<PrivateEventItem> items = eventsModel.getPrivateEventItems(curUser);
        if (items != null) {
            this.eventItemsAdapter.updateData(items);
        }

    }

    private class EventItemsAdapter extends RecyclerView.Adapter<EventItemsAdapter.ViewHolder> {

        List<PrivateEventItem> eventItemList = new LinkedList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            EventItemBinding binding = EventItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PrivateEventItem eventItem = eventItemList.get(position);
            holder.bind(eventItem);
        }

        @Override
        public int getItemCount() {
            return eventItemList.size();
        }

        public void updateData(@NonNull List<PrivateEventItem> eventItems) {
            this.eventItemList = eventItems != null ? eventItems : new ArrayList<>();
            this.notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final EventItemBinding binding;

            public ViewHolder(@NonNull final EventItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(PrivateEventItem eventItem) {
                if (eventItem != null) {
                    binding.title.setText(eventItem.getTitle());
                    itemView.setOnClickListener(v -> {
                        // Show the invite management UI when an item is clicked
                        mySwitch.setVisibility(View.VISIBLE);
                        addText.setVisibility(View.VISIBLE);
                        removeText.setVisibility(View.VISIBLE);
                        addButton.setVisibility(View.VISIBLE);
                        ManageInvites.this.eventItem = eventItem;
                        updateInvites(eventItem);
                        onMySwitch();
                    });
                }
            }
        }
    }

    private class ReceivedInvitesAdapter extends RecyclerView.Adapter<ReceivedInvitesAdapter.ViewHolder> {

        List<Map<String, Object>> invites = new ArrayList<>();

        public void updateData(List<Map<String, Object>> newInvites) {
            this.invites = newInvites;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // reuse InviteItemBinding or create a new one for invite items
            InviteItemBinding binding = InviteItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(invites.get(position));
        }

        @Override
        public int getItemCount() {
            return invites.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final InviteItemBinding binding;

            public ViewHolder(@NonNull InviteItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(Map<String, Object> invite) {
                String title = (String) invite.get("eventTitle");
                String status = (String) invite.get("status");
                String author = (String) invite.get("author");
                binding.title.setText(title + " (From: " + author + ", Status: " + status + ")");
                itemView.setOnClickListener(v -> {
                    if (ManageInvites.this.listener != null) {
                        ManageInvites.this.listener.onEventItemPage(events.getEvent(title), State.INVITES);
                    }
                });
            }
        }
    }


    public void onMySwitch(){
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                addButton.setVisibility(View.INVISIBLE);
                removeButton.setVisibility(View.VISIBLE);
            } else {
                // Switch is OFF
                removeButton.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateInvites(PrivateEventItem eventItem){
        invitations.setText("Current Invitations: " + eventItem.getInvitations());
    }

    @Override
    public void displayInvitations(List<Map<String, Object>> invitations) {
        receivedInvitesAdapter.updateData(invitations);
    }

}