package com.example.eventsmanager.view;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.eventsmanager.R;
import com.example.eventsmanager.databinding.FragmentCreateEventBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;


public class CreateEvent extends Fragment implements CreateEventUI {
    private FragmentCreateEventBinding binding;
    Listener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView timeEdit = view.findViewById(R.id.editTextTime2);
        TextView dateEdit = view.findViewById(R.id.editTextDate2);

        TextView publicText = view.findViewById(R.id.publicText);
        TextView privateText = view.findViewById(R.id.privateText);
        publicText.setVisibility(View.INVISIBLE);


        Switch mySwitch = view.findViewById(R.id.publicPrivateSwitch1);

        // Listen for changes
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                privateText.setVisibility(View.INVISIBLE);
                publicText.setVisibility(View.VISIBLE);
            } else {
                // Switch is OFF
                publicText.setVisibility(View.INVISIBLE);
                privateText.setVisibility(View.VISIBLE);

            }
        });


        // Set up Time Picker button click
        view.findViewById(R.id.pickDate).setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment();

            // Set the listener to receive the time
            newFragment.setDatePickerListener((year, month, day) -> {
                // Format the time as you prefer
                String dateString = String.format("%02d/%02d/%02d", (month + 1), day,year);
                dateEdit.setText(dateString);
            });

            newFragment.show(getParentFragmentManager(), "datePicker");
        });

        // Set up Time Picker button click
        view.findViewById(R.id.pickTime).setOnClickListener(v -> {
            TimePickerFragment newFragment = new TimePickerFragment();

            // Set the listener to receive the time
            newFragment.setTimePickerListener((hourOfDay, minute) -> {
                // Format the time as you prefer
                String timeString = String.format("%02d:%02d", hourOfDay, minute);
                timeEdit.setText(timeString);
            });

            newFragment.show(getParentFragmentManager(), "timePicker");
        });


        this.binding.createEventAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                final Editable nameEditable = CreateEvent.this.binding.createEventName.getText();
                final String name = nameEditable.toString();

                final String date = dateEdit.getText().toString();

                final String time = timeEdit.getText().toString();

                final Editable locationEditable = CreateEvent.this.binding.createEventLocation.getText();
                final String location = locationEditable.toString();

                final Editable descriptionEditable = CreateEvent.this.binding.createEventDescription.getText();
                final String description = descriptionEditable.toString();

                //final Editable authorEditable = CreateEvent.this.binding.createEventAuthor.getText();


                // check that we have all required info
                if (name.isBlank()){
                    Snackbar.make(v, R.string.missing_name_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (date.isBlank()){
                    Snackbar.make(v, R.string.missing_date_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (time.isBlank()){
                    Snackbar.make(v, R.string.missing_time_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (location.isBlank()){
                    Snackbar.make(v, R.string.missing_location_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (description.isBlank()){
                    Snackbar.make(v, R.string.missing_description_field_error, Snackbar.LENGTH_LONG).show();
                    return;
                }



                if (CreateEvent.this.listener != null) {
                    boolean isPrivate = mySwitch.isChecked(); // Get current switch state
                    CreateEvent.this.listener.onCreateEvent(location, name, time, date, description, isPrivate);

                    // clear fields to make them ready for the next item
                    nameEditable.clear();
                    timeEdit.setText("click button to set time");
                    dateEdit.setText("click button to set date");
                    locationEditable.clear();
                    //authorEditable.clear();
                    descriptionEditable.clear();
                    mySwitch.setChecked(false);

                }
            }
        });

        this.binding.homeButton.setOnClickListener((clickedView) -> {
            if (CreateEvent.this.listener != null ) CreateEvent.this.listener.onHomeScreen();
        });

        this.binding.backButton.setOnClickListener((clickedView) -> {
            if (CreateEvent.this.listener != null ) CreateEvent.this.listener.onEventsMenu();
        });


    }

    public void setListener(final Listener listener) {
        this.listener = listener;

    }



    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public interface TimePickerListener {
            void onTimeSet(int hourOfDay, int minute);
        }

        private TimePickerListener listener;

        // Method to set the listener
        public void setTimePickerListener(TimePickerListener listener) {
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

        private DatePickerListener listener;

        // Method to set the listener
        public void setDatePickerListener(DatePickerListener listener) {
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