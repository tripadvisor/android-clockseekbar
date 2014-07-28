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

package com.tripadvisor.seekbar;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tripadvisor.seekbar.util.Utils;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.util.Locale;

import static com.tripadvisor.seekbar.CircularClockSeekBar.ClockRangeStatus.DIFFERENT_DAY_OF_WEEK;
import static com.tripadvisor.seekbar.CircularClockSeekBar.ClockRangeStatus.INVALID_RANGE;
import static com.tripadvisor.seekbar.CircularClockSeekBar.ClockRangeStatus.VALID_RANGE;
import static com.tripadvisor.seekbar.util.Utils.FontType.REGULAR;
import static com.tripadvisor.seekbar.util.Utils.SIMPLE_DATE_FORMAT_AM_PM;
import static com.tripadvisor.seekbar.util.Utils.SIMPLE_DATE_FORMAT_HOURS;
import static com.tripadvisor.seekbar.util.Utils.SIMPLE_DATE_FORMAT_MERIDIAN;

public class ClockView extends LinearLayout {

    public static final float LETTER_SPACING = -3.0f;
    private final LetterSpacingTextView mTimeText;
    private final LetterSpacingTextView mTimeMeridianText;
    private final RobotoTextView mTimeWeekDayText;
    private final CircularClockSeekBar mCircularClockSeekBar;
    private Interval mValidTimeInterval;
    private DateTime mOriginalTime;
    private final boolean mIs24HourFormat;
    private DateTime mNewCurrentTime;
    private int mCurrentValidProgressDelta;
    private DateTime mCurrentValidTime;
    private ClockTimeUpdateListener mClockTimeUpdateListener;

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mClockTimeUpdateListener = new ClockTimeUpdateListener() {
            @Override
            public void onClockTimeUpdate(ClockView clockView, DateTime currentTime) {
            }
        };
        mIs24HourFormat = DateFormat.is24HourFormat(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.clock_view, this);
        mTimeText = (LetterSpacingTextView) view.findViewById(R.id.time_text_view);
        mTimeMeridianText = (LetterSpacingTextView) view.findViewById(R.id.time_meredian_text_view);
        mTimeWeekDayText = (RobotoTextView) view.findViewById(R.id.time_week_day_text);
        if (!isInEditMode()) {
            mTimeText.setLetterSpacing(LETTER_SPACING);
            mTimeMeridianText.setTypeface(Utils.getRobotoTypeface(context, REGULAR));
            mTimeMeridianText.setLetterSpacing(LETTER_SPACING);
        }
        mCircularClockSeekBar = (CircularClockSeekBar) view.findViewById(R.id.clock_seek_bar);
        mCircularClockSeekBar.setSeekBarChangeListener(new CircularClockSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularClockSeekBar seekBar, int progress, boolean fromUser) {
                updateProgressWithDelta(seekBar.getProgressDelta());
            }

            @Override
            public void onStartTrackingTouch(CircularClockSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularClockSeekBar seekBar) {
                // snap to correct position
                if (mValidTimeInterval.contains(mNewCurrentTime)) {
                    // snap to nearest hour.
                    int progressDelta = (int) (mCircularClockSeekBar.getAngle() % 30);
                    if (progressDelta != 0) {
                        // snap either to previous/next hour
                        mCircularClockSeekBar.roundToNearestDegree(30);
                    } else {
                        // we are trigering onClockTimeUpdate because the user was perfect and moved the
                        // clock by exact multiple of 30 degrees so there is no animation and the time is still
                        // within valid time interval
                        mClockTimeUpdateListener.onClockTimeUpdate(ClockView.this, mNewCurrentTime);
                    }
                } else {
                    // slide-animate back or forward
                    mCircularClockSeekBar.animateToDelta(mCircularClockSeekBar.getProgressDelta(), mCurrentValidProgressDelta);
                    setClockText(mCurrentValidTime);
                }
            }

            @Override
            public void onAnimationComplete(CircularClockSeekBar seekBar) {
                if (mValidTimeInterval != null && mNewCurrentTime != null
                        && mValidTimeInterval.contains(mNewCurrentTime)) {
                    mClockTimeUpdateListener.onClockTimeUpdate(ClockView.this, mNewCurrentTime);
                }
            }
        });
    }

    public interface ClockTimeUpdateListener {
        public void onClockTimeUpdate(ClockView clockView, DateTime currentTime);
    }

    public void setNewCurrentTime(DateTime newCurrentTime) {
        if (mValidTimeInterval != null && newCurrentTime != null && mNewCurrentTime != null
                && mValidTimeInterval.contains(newCurrentTime)) {
            int diffInMinutes = Minutes.minutesBetween(mNewCurrentTime, newCurrentTime).getMinutes();
            mCircularClockSeekBar.moveToDelta(mCurrentValidProgressDelta, diffInMinutes / 2);
            setClockText(mCurrentValidTime);
        }
    }

    private void updateProgressWithDelta(int progressDelta) {
        // 1 deg = 2 min
        mNewCurrentTime = mOriginalTime.plusMinutes(progressDelta * 2);
        setClockText(mNewCurrentTime);
        if (mValidTimeInterval != null && mNewCurrentTime != null
                && mValidTimeInterval.contains(mNewCurrentTime)) {
            mCurrentValidProgressDelta = progressDelta;
            mCurrentValidTime = mNewCurrentTime.minusMinutes(progressDelta * 2);
        }
    }

    private void setClockText(DateTime newCurrentTime) {
        if (mIs24HourFormat) {
            mTimeText.setText(SIMPLE_DATE_FORMAT_HOURS.print(newCurrentTime));
            mTimeMeridianText.setText(R.string.flights_app_short_hrs_cbd);
        } else {
            mTimeText.setText(SIMPLE_DATE_FORMAT_AM_PM.print(newCurrentTime));
            mTimeMeridianText.setText(SIMPLE_DATE_FORMAT_MERIDIAN.print(newCurrentTime).toLowerCase(Locale.US));
        }
        setSeekBarStatus(newCurrentTime);
    }

    private void setSeekBarStatus(DateTime newCurrentTime) {
        if (mValidTimeInterval.contains(newCurrentTime)) {
            if (newCurrentTime.getDayOfWeek() == mValidTimeInterval.getStart().getDayOfWeek()) {
                mCircularClockSeekBar.setSeekBarStatus(VALID_RANGE);
                mTimeWeekDayText.setVisibility(GONE);
            } else {
                mCircularClockSeekBar.setSeekBarStatus(DIFFERENT_DAY_OF_WEEK);
                mTimeWeekDayText.setVisibility(VISIBLE);
                mTimeWeekDayText.setText(newCurrentTime.toString("EEE"));
            }
        } else {
            mCircularClockSeekBar.setSeekBarStatus(INVALID_RANGE);
            mTimeWeekDayText.setVisibility(GONE);
        }
    }

    public void setBounds(DateTime minTime, DateTime maxTime, boolean isMaxClock) {
        // NOTE: To show correct end time on clock, since the Interval.contains() checks for
        // millisInstant >= thisStart && millisInstant < thisEnd
        // however we want
        // millisInstant >= thisStart && millisInstant <= thisEnd
        maxTime = maxTime.plusMillis(1);
        mValidTimeInterval = new Interval(minTime, maxTime);
        maxTime = maxTime.minusMillis(1);
        mCircularClockSeekBar.reset();
        if (isMaxClock) {
            mOriginalTime = maxTime;
            mCurrentValidTime = maxTime;
            int hourOfDay = maxTime.get(DateTimeFieldType.clockhourOfDay()) % 12;
            mCircularClockSeekBar.setProgress(hourOfDay * 10);
            setClockText(mOriginalTime);
        } else {
            mOriginalTime = minTime;
            mCurrentValidTime = minTime;
            int hourOfDay = minTime.get(DateTimeFieldType.clockhourOfDay()) % 12;
            mCircularClockSeekBar.setProgress(hourOfDay * 10);
            setClockText(mOriginalTime);
        }
    }

    @Nullable
    @Override
    public CharSequence getContentDescription() {
        return String.format("%s%s", mTimeText.getText(), mTimeMeridianText.getText());
    }

    public void setClockTimeUpdateListener(ClockTimeUpdateListener clockTimeUpdateListener) {
        mClockTimeUpdateListener = clockTimeUpdateListener;
    }

    public void removeClockTimeUpdateListener(ClockTimeUpdateListener clockTimeUpdateListener) {
        if (mClockTimeUpdateListener.equals(clockTimeUpdateListener)) {
            mClockTimeUpdateListener = new ClockTimeUpdateListener() {
                @Override
                public void onClockTimeUpdate(ClockView clockView, DateTime currentTime) {
                }
            };
        }
    }

    public DateTime getCurrentValidTime() {
        return mCurrentValidTime;
    }

    public DateTime getNewCurrentTime() {
        return mNewCurrentTime;
    }
}
