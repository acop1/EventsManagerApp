package com.example.eventsmanager.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventsmanager.R;
import com.example.eventsmanager.databinding.FragmentEventItemPageBinding;
import com.example.eventsmanager.model.EventItem;
import com.example.eventsmanager.model.Events;
import com.example.eventsmanager.model.EventsStorage;
import com.example.eventsmanager.model.PrivateEventItem;
import com.example.eventsmanager.view.State;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EventItemPage extends Fragment implements EventItemPageUI {

    private final State origin;
    private FragmentEventItemPageBinding binding;
    Listener listener;
    private EventItem lineItem;
    private EventsStorage savedEvents;
    Switch mySwitch;

    public EventItemPage(EventItem lineItem, EventsStorage saved, State origin) {
        this.lineItem = lineItem;
        this.savedEvents = saved;
        this.origin = origin;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentEventItemPageBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mySwitch = this.binding.save;
        setSwitch();
        setText();

        this.binding.homeButton.setOnClickListener((clickedView) -> {
            if (EventItemPage.this.listener != null) EventItemPage.this.listener.onHomeScreen();
        });

        this.binding.backButton.setOnClickListener((clickedView) -> {
            if (EventItemPage.this.listener != null) {
                if(origin == State.HOME)
                    EventItemPage.this.listener.onHomeScreen();
                if(origin == State.SAVED)
                    EventItemPage.this.listener.onSavedEvents();
                if(origin == State.MY)
                    EventItemPage.this.listener.onMyEvents();
                if(origin == State.INVITES)
                    EventItemPage.this.listener.onManageInvites();
            }
        });

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (EventItemPage.this.listener != null) {
                    EventItemPage.this.listener.onAddSavedEvent(lineItem);
                    Toast.makeText(getContext(), "Event saved!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if (EventItemPage.this.listener != null) {
                    EventItemPage.this.listener.onRemoveSavedEvent(lineItem);
                    Toast.makeText(getContext(), "Event unsaved!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(origin == State.MY) {
            this.binding.update.setVisibility(View.VISIBLE);
            this.binding.delete.setVisibility(View.VISIBLE);
            this.binding.update.setOnClickListener(v -> showUpdateFields());
            this.binding.delete.setOnClickListener((clickedView) -> {
                this.binding.delete.setVisibility(View.GONE);
                this.binding.deleteConfirm.setVisibility(View.VISIBLE);
            });
            this.binding.deleteConfirm.setOnClickListener((clickedView) -> {
                if (EventItemPage.this.listener != null) {
                    EventItemPage.this.listener.onDeleteEvent(lineItem);
                    EventItemPage.this.listener.onMyEvents();
                }
            });
            this.binding.confirm.setOnClickListener(v -> confirmUpdate());
        }
    }

    private void showUpdateFields() {
        // Hide display fields
        this.binding.time.setVisibility(View.INVISIBLE);
        this.binding.title.setVisibility(View.INVISIBLE);
        this.binding.date.setVisibility(View.INVISIBLE);
        this.binding.location.setVisibility(View.INVISIBLE);
        this.binding.description.setVisibility(View.INVISIBLE);
        this.binding.author.setVisibility(View.INVISIBLE);
        this.binding.inviteList.setVisibility(View.INVISIBLE);
        this.binding.saveEventText.setVisibility(View.INVISIBLE);
        this.binding.save.setVisibility(View.INVISIBLE);
        this.binding.update.setVisibility(View.INVISIBLE);
        this.binding.delete.setVisibility(View.GONE);
        this.binding.deleteConfirm.setVisibility(View.GONE);

        // Show edit fields
        this.binding.createEventName.setVisibility(View.VISIBLE);
        this.binding.editTextDate2.setVisibility(View.VISIBLE);
        this.binding.editTextTime2.setVisibility(View.VISIBLE);
        this.binding.createEventLocation.setVisibility(View.VISIBLE);
        this.binding.createEventDescription.setVisibility(View.VISIBLE);
        this.binding.publicPrivateSwitch1.setVisibility(View.VISIBLE);
        this.binding.pickDate.setVisibility(View.VISIBLE);
        this.binding.pickTime.setVisibility(View.VISIBLE);
        this.binding.publicText.setVisibility(View.VISIBLE);
        this.binding.privateText.setVisibility(View.VISIBLE);
        this.binding.confirm.setVisibility(View.VISIBLE);

        // Set current values
        this.binding.createEventName.setText(lineItem.getTitle());
        this.binding.editTextDate2.setText(lineItem.getDateString());
        this.binding.editTextTime2.setText(lineItem.getTimeString());
        this.binding.createEventLocation.setText(lineItem.getLocation());
        this.binding.createEventDescription.setText(lineItem.getDescription());

        // Set switch state based on whether it's private or public
        if (lineItem instanceof PrivateEventItem) {
            this.binding.publicPrivateSwitch1.setChecked(true);
        } else {
            this.binding.publicPrivateSwitch1.setChecked(false);
        }

        // Set up Time Picker button click
        this.binding.pickTime.setOnClickListener(v -> {
            TimePickerFragment newFragment = new TimePickerFragment();

            newFragment.setTimePickerListener((hourOfDay, minute) -> {
                String timeString = String.format("%02d:%02d", hourOfDay, minute);
                this.binding.editTextTime2.setText(timeString);
            });

            newFragment.show(getParentFragmentManager(), "timePicker");
        });

        // Set up Date Picker button click
        this.binding.pickDate.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment();

            newFragment.setDatePickerListener((year, month, day) -> {
                String dateString = String.format("%02d/%02d/%02d", (month + 1), day, year);
                this.binding.editTextDate2.setText(dateString);
            });

            newFragment.show(getParentFragmentManager(), "datePicker");
        });
    }

    private void confirmUpdate() {
        final Editable nameEditable = this.binding.createEventName.getText();
        final String name = nameEditable.toString();

        final String date = this.binding.editTextDate2.getText().toString();
        final String time = this.binding.editTextTime2.getText().toString();

        final Editable locationEditable = this.binding.createEventLocation.getText();
        final String location = locationEditable.toString();

        final Editable descriptionEditable = this.binding.createEventDescription.getText();
        final String description = descriptionEditable.toString();

        boolean isPrivate = this.binding.publicPrivateSwitch1.isChecked();

        // Validate inputs
        if (name.isBlank()) {
            Snackbar.make(getView(), R.string.missing_name_field_error, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (date.isBlank()) {
            Snackbar.make(getView(), R.string.missing_date_field_error, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (time.isBlank()) {
            Snackbar.make(getView(), R.string.missing_time_field_error, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (location.isBlank()) {
            Snackbar.make(getView(), R.string.missing_location_field_error, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (description.isBlank()) {
            Snackbar.make(getView(), R.string.missing_description_field_error, Snackbar.LENGTH_LONG).show();
            return;
        }

        // Prepare updates map
        Map<String, Object> updates = new HashMap<>();
        updates.put(EventItem.TITLE, name);
        updates.put(EventItem.DATE, date);
        updates.put(EventItem.TIME, time);
        updates.put(EventItem.LOCATION, location);
        updates.put(EventItem.DESCRIPTION, description);

        if (listener != null && lineItem.getId() != null) {  // Check if ID exists
            listener.onUpdateEvent(lineItem, updates, isPrivate);
        } else {
            Snackbar.make(getView(), "Error: Event ID missing", Snackbar.LENGTH_LONG).show();
        }

        // Hide edit fields and show display fields
        if (listener != null && lineItem.getId() != null) {
            listener.onUpdateEvent(lineItem, updates, isPrivate);
            // The listener will handle navigation now
        } else {
            Snackbar.make(getView(), "Error: Event ID missing", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void updateEventDisplay(Events eventsModel) {
        // Update the event item if it exists in the new model
        EventItem updatedItem = eventsModel.getPublicEvents().get(lineItem.getTitle());
        if (updatedItem == null) {
            updatedItem = eventsModel.getPrivateEvents().get(lineItem.getTitle());
        }

        if (updatedItem != null) {
            this.lineItem = updatedItem;
            setText();
            setSwitch();
        }
    }

    @Override
    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    public void setText(){
        this.binding.time.setText("Time: " + this.lineItem.getTimeString());
        this.binding.title.setText(this.lineItem.getTitle());
        this.binding.date.setText("Date: " + this.lineItem.getDateString());
        this.binding.location.setText("Location: " + this.lineItem.getLocation());
        this.binding.description.setText("Description: " + this.lineItem.getDescription());
        this.binding.author.setText("Hosted By: " + this.lineItem.getAuthor());
        if(this.lineItem instanceof PrivateEventItem)
            this.binding.inviteList.setText("Current Invitations: " + ((PrivateEventItem)this.lineItem).getInvitations());
    }

    public void setSwitch() {
        if (lineItem != null && savedEvents != null) {
            boolean isSaved;
            if (lineItem instanceof PrivateEventItem) {
                isSaved = savedEvents.getPrivateSavedEvents().stream()
                        .anyMatch(e -> e.getId().equals(lineItem.getId()));
            } else {
                isSaved = savedEvents.getPublicSavedEvents().stream()
                        .anyMatch(e -> e.getId().equals(lineItem.getId()));
            }
            mySwitch.setChecked(isSaved);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public interface TimePickerListener {
            void onTimeSet(int hourOfDay, int minute);
        }

        private CreateEvent.TimePickerFragment.TimePickerListener listener;

        // Method to set the listener
        public void setTimePickerListener(CreateEvent.TimePickerFragment.TimePickerListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (listener != null) {
                listener.onTimeSet(hourOfDay, minute);
            }
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public interface DatePickerListener {
            void onDateSet(int year, int month, int day);
        }

        private CreateEvent.DatePickerFragment.DatePickerListener listener;

        // Method to set the listener
        public void setDatePickerListener(CreateEvent.DatePickerFragment.DatePickerListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (listener != null) {
                listener.onDateSet(year, month, day);
            }
        }
    }
}