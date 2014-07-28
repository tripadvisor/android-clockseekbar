package com.tripadvisor.seekbar.sample;

import android.test.ActivityInstrumentationTestCase2;

import org.joda.time.DateTime;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withContentDescription;
import static com.tripadvisor.seekbar.sample.MainActivity.PlaceholderFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testThatWhenUserTapsTheClocksItChangesTheValuesOnScreen() throws Throwable {
        getActivity();
        onView(withContentDescription("7am"))
                .check(matches(isDisplayed()));
        Thread.sleep(1000L);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 18, 0), false);
            }
        });
        onView(withContentDescription("6pm"))
                .check(matches(isDisplayed()));
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 19, 0), true);
            }
        });
        onView(withContentDescription("7pm"))
                .check(matches(isDisplayed()));
    }

    public void testThatMinClockCannotBeSetBeyondMaxClock() throws Throwable {
        getActivity();
        onView(withContentDescription("7am"))
                .check(matches(isDisplayed()));
        Thread.sleep(1000L);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 18, 0), false);
            }
        });
        onView(withContentDescription("6pm"))
                .check(matches(isDisplayed()));
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 19, 0), true);
            }
        });
        onView(withContentDescription("7pm"))
                .check(matches(isDisplayed()));
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 20, 0), false);
            }
        });
        onView(withContentDescription("6pm"))
                .check(matches(isDisplayed()));
    }

    public void testThatMaxClockCannotBeSetBeyondMinClock() throws Throwable {
        getActivity();
        onView(withContentDescription("7am"))
                .check(matches(isDisplayed()));
        Thread.sleep(1000L);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 18, 0), false);
            }
        });
        onView(withContentDescription("6pm"))
                .check(matches(isDisplayed()));
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 19, 0), true);
            }
        });
        onView(withContentDescription("7pm"))
                .check(matches(isDisplayed()));
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaceholderFragment placeholderFragment = MainActivity.getPlaceholderFragment();
                placeholderFragment.changeClockTimeForTests(
                        new DateTime(2014, 4, 25, 17, 0), true);
            }
        });
        onView(withContentDescription("7pm"))
                .check(matches(isDisplayed()));
    }
}