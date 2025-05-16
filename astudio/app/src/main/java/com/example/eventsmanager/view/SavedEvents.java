package com.example.eventsmanager.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsmanager.databinding.EventItemBinding;
import com.example.eventsmanager.databinding.FragmentSavedEventsBinding;
import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.EventsStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SavedEvents extends Fragment implements SavedEventsUI {
    private FragmentSavedEventsBinding binding;
    Listener listener;
    private final EventItemsAdapter eventItemsAdapter1 = new EventItemsAdapter();
    private final EventItemsAdapter eventItemsAdapter2 = new EventItemsAdapter();
    HashSet<EventItem> privateEvents;
    HashSet<EventItem> publicEvents;

    public SavedEvents(EventsStorage savedEvents) {
        if (savedEvents != null) {
            privateEvents = savedEvents.getPrivateSavedEvents();
            publicEvents = savedEvents.getPublicSavedEvents();
        } else {
            privateEvents = new HashSet<>();
            publicEvents = new HashSet<>();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentSavedEventsBinding.inflate(inflater);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.binding.publicListRecView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.publicListRecView.setAdapter(this.eventItemsAdapter1);

        this.binding.privateListRecView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.privateListRecView.setAdapter(this.eventItemsAdapter2);

        this.binding.homeButton.setOnClickListener((clickedView) -> {
            if (SavedEvents.this.listener != null) SavedEvents.this.listener.onHomeScreen();
        });

        this.binding.backButton.setOnClickListener((clickedView) -> {
            if (SavedEvents.this.listener != null) SavedEvents.this.listener.onEventsMenu();
        });

        // Convert HashSets to Lists when passing to adapter
        this.eventItemsAdapter1.updateData(new ArrayList<>(publicEvents));
        this.eventItemsAdapter2.updateData(new ArrayList<>(privateEvents));

        //if (this.listener != null) this.listener.onSavedEventsBody(this);
    }

    public void updateEventDisplay(@NonNull EventsStorage savedEvents) {
        if (savedEvents != null) {
            HashSet<EventItem> privateItems = savedEvents.getPrivateSavedEvents();
            HashSet<EventItem> publicItems = savedEvents.getPublicSavedEvents();

            if (publicItems != null) {
                this.publicEvents = publicItems;
                this.eventItemsAdapter1.updateData(new ArrayList<>(publicItems));
            }
            if (privateItems != null) {
                this.privateEvents = privateItems;
                this.eventItemsAdapter2.updateData(new ArrayList<>(privateItems));
            }
        }
    }

    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    class EventItemsAdapter extends RecyclerView.Adapter<EventItemsAdapter.ViewHolder> {
        private List<EventItem> eventItemList = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            EventItemBinding binding = EventItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EventItem eventItem = eventItemList.get(position);
            holder.bind(eventItem);
        }

        @Override
        public int getItemCount() {
            return eventItemList.size();
        }

        public void updateData(@NonNull List<EventItem> eventItems) {
            this.eventItemList = eventItems;
            this.notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final EventItemBinding binding;

            public ViewHolder(@NonNull final EventItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(EventItem lineItem) {
                if (lineItem != null) {
                    binding.title.setText(lineItem.getTitle());
                    itemView.setOnClickListener(v -> {
                        if (SavedEvents.this.listener != null) {
                            SavedEvents.this.listener.onEventItemPage(lineItem, State.SAVED);
                        }
                    });
                }
            }
        }
    }
}