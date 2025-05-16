package com.example.eventsmanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventsmanager.R;
import com.example.eventsmanager.controller.MainActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.sql.Time;

@RunWith(AndroidJUnit4.class)

public class FragmentTest {

    // required - launches the appropriate activity prior to each test
    @org.junit.Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    private ActivityScenario<MainActivity> activityScenario;

    // Test data
    private final String username = "pkabeya";
    private final String password = "abcd";

    private final String eventName = "Team Meeting";
    private final String eventLocation = "Conference Room";
    private final String eventDescription = "Weekly team sync";

    @Before
    public void setup() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testLogin(){
        onView(withId(R.id.usernameEditText))
                .perform(typeText(username));
        onView(withId(R.id. passwordEditText))
                .perform(typeText(password));

        onView(withId(R.id.signinButton)).perform(click());
    }

    @Test
    public void testHomesScreen(){
        testLogin();
        // Navigate to CreateEvent fragment
        onView(withId(R.id.menuButton)).perform(click());
    }

        @Test
        public void testCreateEventFlow() {
            // Log in and navigate to the CreateEvent fragment
            testHomesScreen();

            onView(withId(R.id.createEvent)).perform(click());

            //Fill out the event details
            onView(withId(R.id.createEventName))
                    .perform(ViewActions.typeText(eventName), closeSoftKeyboard());

            // SClick the date picker and handle the dialog
            onView(withId(R.id.pickDate)).perform(click());

            // Handle DatePicker Dialog by selecting a fixed date (e.g., May 14, 2025)
            onView(withId(R.id.pickDate))    // Year picker
                    .perform(ViewActions.scrollTo(), ViewActions.click());
            onView(withText("2025")).perform(ViewActions.click());  // Year selection
            onView(withId(R.id.pickDate))   // Month picker
                    .perform(ViewActions.scrollTo(), ViewActions.click());
            onView(withText("May")).perform(ViewActions.click());   // Month selection
            onView(withId(R.id.pickDate))     // Day picker
                    .perform(ViewActions.scrollTo(), ViewActions.click());
            onView(withText("14")).perform(ViewActions.click());    // Day selection
            onView(withText("OK")).perform(ViewActions.click());    // Confirm date selection

            // Step 4: Click the time picker and handle the dialog
            onView(withId(R.id.pickTime)).perform(click());

            // Handle TimePicker Dialog by selecting a fixed time (e.g., 10:00 AM)
            onView(withId(R.id.pickTime)).perform(ViewActions.scrollTo(), ViewActions.click());
            onView(withText("10")).perform(ViewActions.click());    // Hour selection
            onView(withId(R.id.pickTime)).perform(ViewActions.scrollTo(), ViewActions.click());
            onView(withText("00")).perform(ViewActions.click());    // Minute selection
            onView(withText("OK")).perform(ViewActions.click());    // Confirm time selection

            // Step 5: Fill out the location and description
            onView(withId(R.id.createEventLocation))
                    .perform(ViewActions.typeText(eventLocation), closeSoftKeyboard());

            onView(withId(R.id.createEventDescription))
                    .perform(ViewActions.typeText(eventDescription), closeSoftKeyboard());

            // Step 6: Click the create event button
            onView(withId(R.id.createEventAdd)).perform(click());
        }

    @Test
    public void testSearchEvent(){
        testLogin();

        onView(withId(R.id.searchView)).perform(click());

    }

    @Test
    public void testMyEvents(){
        testHomesScreen();
        onView(withId(R.id.myEvents)).perform(click());
        onView(withId(R.id.historyUpcomingSwitch)).perform(click());

    }

}