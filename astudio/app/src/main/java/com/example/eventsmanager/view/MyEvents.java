package com.example.eventsmanager.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.eventsmanager.R;
import com.example.eventsmanager.databinding.EventItemBinding;
import com.example.eventsmanager.databinding.FragmentMyEventsBinding;
import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MyEvents extends Fragment implements MyEventsUI{

    private FragmentMyEventsBinding binding;
    Listener listener;

    private final EventItemsAdapter upcomingItemsAdapter = new EventItemsAdapter();
    private final EventItemsAdapter historyItemsAdapter = new EventItemsAdapter();
    Switch mySwitch;

    TextView historyText, upcomingText;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.binding.historyListRecView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.historyListRecView.setAdapter(this.historyItemsAdapter);
        this.binding.upcomingListRecView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.upcomingListRecView.setAdapter(this.upcomingItemsAdapter);

        mySwitch = this.binding.historyUpcomingSwitch;
        historyText = this.binding.historyText;
        upcomingText = this.binding.upcomingText;
        upcomingText.setVisibility(View.INVISIBLE);

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                historyText.setVisibility(View.INVISIBLE);
                upcomingText.setVisibility(View.VISIBLE);
                binding.historyListRecView.setVisibility(View.GONE);
                binding.upcomingListRecView.setVisibility(View.VISIBLE);
            } else {
                // Switch is OFF
                upcomingText.setVisibility(View.INVISIBLE);
                historyText.setVisibility(View.VISIBLE);
                binding.historyListRecView.setVisibility(View.VISIBLE);
                binding.upcomingListRecView.setVisibility(View.GONE);
            }
        });

        this.binding.homeButton.setOnClickListener((clickedView) -> {
            if (MyEvents.this.listener != null) MyEvents.this.listener.onHomeScreen();
        });

        this.binding.backButton.setOnClickListener((clickedView) -> {
            if (MyEvents.this.listener != null) MyEvents.this.listener.onEventsMenu();
        });

        if (this.listener != null) this.listener.onMyEventsReady(this);

    }


    @Override
    public void updateEventDisplay(@NonNull List<EventItem> items) {
            if (items != null) {
                // Split into upcoming and past events based on current date
                List<EventItem> upcomingEvents = new ArrayList<>();
                List<EventItem> pastEvents = new ArrayList<>();
                LocalDate yesterday = LocalDate.now().minusDays(1);

                for (EventItem item : items) {
                    // Add your date comparison logic here
                     if (item.getDate().isAfter(yesterday)) {
                         upcomingEvents.add(item);
                     } else {
                         pastEvents.add(item);
                     }

                }

                this.historyItemsAdapter.updateData(pastEvents);
                this.upcomingItemsAdapter.updateData(upcomingEvents);

        }
    }
    @Override
    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    private class EventItemsAdapter extends RecyclerView.Adapter<EventItemsAdapter.ViewHolder> {

        List<EventItem> eventItemList = new LinkedList<>();


        @NonNull
        @Override
        public EventItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            EventItemBinding binding = EventItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EventItemsAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull EventItemsAdapter.ViewHolder holder, int position) {
            EventItem eventItem = eventItemList.get(position);
            holder.bind(eventItem);
        }

        @Override
        public int getItemCount() { return eventItemList.size(); }

        public void updateData(@NonNull List<EventItem> eventItems) {
            this.eventItemList = eventItems != null ? eventItems : new ArrayList<>();
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
                        if (MyEvents.this.listener != null ) MyEvents.this.listener.onEventItemPage(lineItem, State.MY);
                    });
                }
            }
        }
    }


}