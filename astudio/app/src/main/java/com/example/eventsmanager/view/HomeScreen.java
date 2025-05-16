package com.example.eventsmanager.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.eventsmanager.databinding.EventItemBinding;
import com.example.eventsmanager.databinding.FragmentHomeScreenBinding;
import com.example.eventsmanager.model.EventItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class HomeScreen extends Fragment implements HomeScreenUI {
    private FragmentHomeScreenBinding binding;
    private Listener listener;

    private String user = "@";
    private final EventItemsAdapter eventItemsAdapter = new EventItemsAdapter();
    private final EventItemsAdapter searchItemsAdapter = new EventItemsAdapter();


    @Override
    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentHomeScreenBinding.inflate(inflater);
        return this.binding.getRoot();
    }

    public void setUser(String username) {
        this.user += username;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        this.binding.eventListRecView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.eventListRecView.setAdapter(this.eventItemsAdapter);
        this.binding.searchResultsRecView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        this.binding.searchResultsRecView.setAdapter(this.searchItemsAdapter);

        this.binding.idDisplay.setText(this.user);

        this.binding.menuButton.setOnClickListener((clickedView) -> {
            if (HomeScreen.this.listener != null ) HomeScreen.this.listener.onEventsMenu();
        });

        this.binding.manageInvites.setOnClickListener((clickedView) -> {
            if (HomeScreen.this.listener != null ) HomeScreen.this.listener.onManageInvites();
        });


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.eventListRecView.setVisibility(View.GONE);
                binding.searchResultsRecView.setVisibility(View.VISIBLE);
                if (query.isEmpty()) {
                    clearSearchResults();
                    return true;
                }

                if (listener != null) {
                    List<EventItem> results = listener.onSearchRequest(query);
                    searchItemsAdapter.updateData(results);


                    if (results.isEmpty()) {
                        binding.noResultsText.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    clearSearchResults();
                }
                return true;
            }
        });

        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                clearSearchResults();
                binding.searchResultsRecView.setVisibility(View.GONE);
                binding.eventListRecView.setVisibility(View.VISIBLE);
                return true;
            }
        });

        if (this.listener != null) this.listener.onHomeScreenReady(this);
    }

    private void clearSearchResults() {
        searchItemsAdapter.updateData(new ArrayList<>());
        binding.noResultsText.setVisibility(View.GONE);
    }
    @Override
    public void updateEventDisplay(@NonNull List<EventItem> items) {
        if (items != null) {
            this.eventItemsAdapter.updateData(items);
        }
    }


    private class EventItemsAdapter extends RecyclerView.Adapter<EventItemsAdapter.ViewHolder> {

        List<EventItem> eventItemList = new LinkedList<>();


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
        public int getItemCount() { return eventItemList.size(); }

        public void updateData(@NonNull List<EventItem> eventItems) {
            this.eventItemList = eventItems != null ? eventItems : new ArrayList<>();
            this.notifyDataSetChanged();

            // Show/hide no results message
            if (eventItems.isEmpty()) {
                binding.noResultsText.setVisibility(View.VISIBLE);
            } else {
                binding.noResultsText.setVisibility(View.GONE);
            }
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
                        if (HomeScreen.this.listener != null ) HomeScreen.this.listener.onEventItemPage(lineItem, State.HOME);
                    });
                }
            }
        }
    }


}