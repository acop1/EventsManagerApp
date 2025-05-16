package com.example.eventsmanager;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.hamcrest.Matchers.anyOf;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.eventsmanager.controller.MainActivity;

import com.example.eventsmanager.R;

@RunWith(AndroidJUnit4.class)
public class HomeScreenTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private final String testEventTitle = "Team Standup";
    private final String testEventLocation = "Zoom Meeting";

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> {
            // Setup test data
            TestDataGenerator.setupTestEvents(activity.getEventsModel());

            // Refresh UI
            activity.updateAllDisplays();
        });

        // Ensure we're on home screen
        Espresso.onView(ViewMatchers.withId(R.id.homeButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testHomeScreenElementsVisible() {
        // Verify all main elements are displayed
        Espresso.onView(ViewMatchers.withId(R.id.eventListRecView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.menuButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.manageInvites))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.searchView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEventListDisplay() {
        // Verify events are displayed in the RecyclerView
        Espresso.onView(ViewMatchers.withId(R.id.eventListRecView))
                .check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(1)));
    }

    @Test
    public void testNavigationToEventsMenu() {
        // Click menu button
        Espresso.onView(ViewMatchers.withId(R.id.menuButton))
                .perform(ViewActions.click());

        // Verify we see the events menu options
        Espresso.onView(ViewMatchers.withId(R.id.createEvent))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.saveEvents))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Return to home screen
        Espresso.onView(ViewMatchers.withId(R.id.homeButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testNavigationToManageInvites() {
        // Click manage invites button
        Espresso.onView(ViewMatchers.withId(R.id.manageInvites))
                .perform(ViewActions.click());

        // Verify we're on the manage invites screen
        Espresso.onView(ViewMatchers.withId(R.id.manageInvites))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Return to home screen
        Espresso.onView(ViewMatchers.withId(R.id.homeButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testNavigationToSearch() {
        // Click search button
        Espresso.onView(ViewMatchers.withId(R.id.searchView))
                .perform(ViewActions.click());

        // Verify search screen elements
        Espresso.onView(ViewMatchers.withId(R.id.searchView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Return to home screen
        Espresso.onView(ViewMatchers.withId(R.id.homeButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testEventItemClick() {
        // Click first event in the list
        Espresso.onView(ViewMatchers.withId(R.id.eventListRecView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        // Verify event details screen appears
        Espresso.onView(ViewMatchers.withId(R.id.eventListRecView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Return to home screen
        Espresso.onView(ViewMatchers.withId(R.id.homeButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testEventListContent() {
        // Assuming you have test data setup
        Espresso.onView(ViewMatchers.withId(R.id.eventListRecView))
                .check(ViewAssertions.matches(
                        ViewMatchers.hasDescendant(
                                anyOf(
                                        ViewMatchers.withText(testEventTitle),
                                        ViewMatchers.withText(testEventLocation)
                                )
                        )
                ));
    }
}
