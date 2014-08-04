TripAdvisor Android ClockSeekbar
==========================
[![Build Status](https://travis-ci.org/tripadvisor/android-clockseekbar.svg?branch=master)](https://travis-ci.org/tripadvisor/android-clockseekbar.svg)

Tested against API level 14+.

Standalone Android widget for picking a single time or range from a clock view.

![Screenshot](ScreenShot.png)

Usage
-----

Include `ClockView` in your layout XML.

```xml
    <com.tripadvisor.seekbar.ClockView
        android:id="@+id/calendar_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```


or


```xml
    <com.tripadvisor.seekbar.CircularClockSeekBar
        android:id="@+id/clock_seek_bar_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```


This is a fairly large control so it is wise to give it ample space in your layout. On small
devices it is recommended to use a dialog, full-screen fragment, or dedicated activity. On larger
devices like tablets, displaying full-screen is not recommended. A fragment occupying part of the
layout or a dialog is a better choice.

If using `ClockView` , Then in `onCreate` of your activity/dialog or the `onCreateView` of your fragment, initialize the
view with a range of valid dates.

```java
    final ClockView minDepartTime = (ClockView) rootView.findViewById(R.id.min_depart_time_clock_view);
    DateTime minTime = new DateTime(2014, 4, 25, 7, 0);
    DateTime maxTime = new DateTime(2014, 4, 26, 4, 0);
    minDepartTime.setBounds(minTime, maxTime, false);
```


The `ClockView` does not allow the user to exceed the date ranges. It animates the hands back if user scrolls beyond the valid time range.


If using `CircularClockSeekBar`, Then in `onCreate` of your activity/dialog or the `onCreateView` of your fragment, You don't
have to initialize it with anything. The max progress value is defaults to 120 this can be changed as per requirement.

User can add `OnSeekBarChangeListener` that has callbacks for various events.

```java
    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnSeekBarChangeListener {
        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param seekBar  The CircularClockSeekBar whose progress has changed
         * @param progress The current progress level. This will be in the range 0..max where max
         *                 was set by {@link android.widget.ProgressBar#setMax(int)}. (The default value for max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(CircularClockSeekBar seekBar, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param seekBar The CircularClockSeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(CircularClockSeekBar seekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The CircularClockSeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(CircularClockSeekBar seekBar);

        /**
         * Notification that all the animations in this seekbar are finished. Clients may use this to trigger
         * future events within their code.
         *
         * @param seekBar The CircularClockSeekBar for which animations are complete.
         */
        void onAnimationComplete(CircularClockSeekBar seekBar);
    }
```

License
--------

    Copyright 2014 TripAdvisor, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Inspiration
-----------

http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/1.6_r2/android/widget/AnalogClock.java

https://github.com/RaghavSood/AndroidCircularSeekBar
