package com.tripadvisor.seekbar.sample;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tripadvisor.seekbar.ClockView;

import org.joda.time.DateTime;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            final ClockView minDepartTime = (ClockView) rootView.findViewById(R.id.min_depart_time_clock_view);
            final DateTime minTime = new DateTime(2014, 4, 25, 7, 0);
            final DateTime maxTime = new DateTime(2014, 4, 26, 5, 0);
            minDepartTime.setBounds(minTime, maxTime, false);
            minDepartTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            final ClockView minArriveTime = (ClockView) rootView.findViewById(R.id.min_arrive_time_clock_view);
            minArriveTime.setBounds(minTime, maxTime, false);
            minArriveTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            final ClockView maxArriveTime = (ClockView) rootView.findViewById(R.id.max_arrive_time_clock_view);
            maxArriveTime.setBounds(minTime, maxTime, false);
            maxArriveTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            final ClockView minRandomTime = (ClockView) rootView.findViewById(R.id.min_random_time_clock_view);
            minRandomTime.setBounds(minTime, maxTime, false);
            minRandomTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            final ClockView maxRandomTime = (ClockView) rootView.findViewById(R.id.max_random_time_clock_view);
            maxRandomTime.setBounds(minTime, maxTime, false);
            maxRandomTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

            minDepartTime.setClockTimeUpdateListener(new ClockView.ClockTimeUpdateListener() {
                @Override
                public void onClockTimeUpdate(ClockView clockView, DateTime currentTime) {
                    Log.e("Min -> New Current Time :" , String.valueOf(currentTime));
                }
            });

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            minDepartTime.setNewCurrentTime(new DateTime(2014, 4, 25, 20, 0));
                        }
                    });
                }
            };
            timer.schedule(timerTask, 5000);

            final ClockView maxDepartTime = (ClockView) rootView.findViewById(R.id.max_depart_time_clock_view);
            maxDepartTime.setBounds(minTime, maxTime, true);

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            maxDepartTime.setNewCurrentTime(new DateTime(2014, 4, 25, 16, 0));
                        }
                    });
                }
            };
            timer.schedule(timerTask, 7000);

            maxDepartTime.setClockTimeUpdateListener(new ClockView.ClockTimeUpdateListener() {
                @Override
                public void onClockTimeUpdate(ClockView clockView, DateTime currentTime) {
                    Log.e("Max -> New Current Time :" , String.valueOf(currentTime));
                }
            });

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            minDepartTime.setBounds(minTime, maxTime, false);
                            maxDepartTime.setBounds(minTime, maxTime, true);
                        }
                    });
                }
            };
            timer.schedule(timerTask, 9000);

            return rootView;
        }
    }
}
