/*
 * Copyright (C) 2014 TripAdvisor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author ksarmalkar 7/28/2014
 */

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