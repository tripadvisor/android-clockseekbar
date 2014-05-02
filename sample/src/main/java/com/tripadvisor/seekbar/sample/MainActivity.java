package com.tripadvisor.seekbar.sample;

import android.app.Activity;
import android.app.Fragment;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
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
            DateTime minTime = new DateTime(2014, 4, 25, 7, 0);
            DateTime maxTime = new DateTime(2014, 4, 26, 0, 0);
            minDepartTime.setBounds(minTime, maxTime, false);
            minDepartTime.setNewCurrentTime(new DateTime(2014, 4, 25, 10, 0));

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
//            maxDepartTime.setNewCurrentTime(new DateTime(2014, 4, 26, 0, 0));

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

            return rootView;
        }
    }
}
