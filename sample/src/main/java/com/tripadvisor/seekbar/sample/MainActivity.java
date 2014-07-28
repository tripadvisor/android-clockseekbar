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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripadvisor.seekbar.ClockView;
import com.tripadvisor.seekbar.util.annotations.VisibleForTesting;

import org.joda.time.DateTime;

import static com.tripadvisor.seekbar.ClockView.ClockTimeUpdateListener;

public class MainActivity extends Activity {

    private static ClockView sMinDepartTimeClockView;
    private static ClockView sMaxDepartTimeClockView;
    private static PlaceholderFragment sPlaceholderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            sPlaceholderFragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, sPlaceholderFragment)
                    .commit();
        }
    }

    public static ClockView getMinDepartTimeClockView() {
        return sMinDepartTimeClockView;
    }

    public static ClockView getMaxDepartTimeClockView() {
        return sMaxDepartTimeClockView;
    }

    public static PlaceholderFragment getPlaceholderFragment() {
        return sPlaceholderFragment;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements ClockTimeUpdateListener {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            final DateTime minTime = new DateTime(2014, 4, 25, 7, 0);
            final DateTime maxTime = new DateTime(2014, 4, 26, 5, 0);

            sMinDepartTimeClockView = (ClockView) rootView.findViewById(R.id.min_depart_time_clock_view);
            sMinDepartTimeClockView.setBounds(minTime, maxTime, false);

            sMaxDepartTimeClockView = (ClockView) rootView.findViewById(R.id.max_depart_time_clock_view);
            sMaxDepartTimeClockView.setBounds(minTime, maxTime, true);

            ClockView mMinArrivalTimeClockView = (ClockView) rootView.findViewById(R.id
                    .min_arrive_time_clock_view);
            mMinArrivalTimeClockView.setBounds(minTime, maxTime, false);
            mMinArrivalTimeClockView.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));
            ClockView mMaxArrivalTimeClockView = (ClockView) rootView.findViewById(R.id
                    .max_arrive_time_clock_view);
            mMaxArrivalTimeClockView.setBounds(minTime, maxTime, true);
            mMaxArrivalTimeClockView.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            final ClockView minRandomTime = (ClockView) rootView.findViewById(R.id.min_random_time_clock_view);
            minRandomTime.setBounds(minTime, maxTime, false);
            minRandomTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            final ClockView maxRandomTime = (ClockView) rootView.findViewById(R.id.max_random_time_clock_view);
            maxRandomTime.setBounds(minTime, maxTime, false);
            maxRandomTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            return rootView;
        }

        @VisibleForTesting
        public void changeClockTimeForTests(DateTime dateTime, boolean isMaxTime) {
            if (isMaxTime) {
                sMaxDepartTimeClockView.setClockTimeUpdateListener(this);
                sMaxDepartTimeClockView.setNewCurrentTime(dateTime);
            } else {
                sMinDepartTimeClockView.setClockTimeUpdateListener(this);
                sMinDepartTimeClockView.setNewCurrentTime(dateTime);
            }
        }

        @Override
        public void onClockTimeUpdate(ClockView clockView, DateTime currentTime) {
            if (clockView.equals(sMinDepartTimeClockView)) {
                if (currentTime.compareTo(sMaxDepartTimeClockView.getNewCurrentTime()) >= 0) {
                    sMinDepartTimeClockView.setNewCurrentTime(sMinDepartTimeClockView.getNewCurrentTime().minusHours(1));
                }
            } else if (clockView.equals(sMaxDepartTimeClockView)) {
                if (currentTime.compareTo(sMinDepartTimeClockView.getNewCurrentTime()) <= 0) {
                    sMaxDepartTimeClockView.setNewCurrentTime(sMaxDepartTimeClockView.getNewCurrentTime().plusHours(1));
                }
            }
        }
    }
}
