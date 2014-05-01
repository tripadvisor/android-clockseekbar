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

import java.util.Locale;

import static com.tripadvisor.seekbar.util.Utils.FontType.BOLD;
import static com.tripadvisor.seekbar.util.Utils.FontType.REGULAR;
import static com.tripadvisor.seekbar.util.Utils.SIMPLE_DATE_FORMAT_AM_PM;
import static com.tripadvisor.seekbar.util.Utils.SIMPLE_DATE_FORMAT_HOURS;
import static com.tripadvisor.seekbar.util.Utils.SIMPLE_DATE_FORMAT_MERIDIAN;
import static com.tripadvisor.seekbar.CircularClockSeekBar.ClockRangeStatus.DIFFERENT_DAY_OF_WEEK;
import static com.tripadvisor.seekbar.CircularClockSeekBar.ClockRangeStatus.INVALID_RANGE;
import static com.tripadvisor.seekbar.CircularClockSeekBar.ClockRangeStatus.VALID_RANGE;

public class ClockView extends LinearLayout {

    public static final float LETTER_SPACING = -3.0f;
    private final LetterSpacingTextView mTimeText;
    private final LetterSpacingTextView mTimeMeridianText;
    private final RobotoTextView mTimeWeekDayText;
    private final CircularClockSeekBar mCircularClockSeekBar;
    private Interval mTimeInterval;
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
            mTimeText.setTypeface(Utils.getRobotoTypeface(context, BOLD));
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
                if (mTimeInterval.contains(mNewCurrentTime)) {
                    // snap to nearest hour.
                    int progressDelta = (int) (mCircularClockSeekBar.getAngle() % 30);
                    if (progressDelta != 0) {
                        // snap either to previous/next hour
                        mCircularClockSeekBar.roundToNearestDegree(30);
                    }
                } else {
                    // slide-animate back or forward
                    mCircularClockSeekBar.animateToDelta(mCircularClockSeekBar.getProgressDelta(), mCurrentValidProgressDelta);
                    setClockText(mCurrentValidTime);
                }
            }

            @Override
            public void onAnimationComplete(CircularClockSeekBar seekBar) {
                if (mTimeInterval != null && mNewCurrentTime != null
                        && mTimeInterval.contains(mNewCurrentTime)) {
                    mClockTimeUpdateListener.onClockTimeUpdate(ClockView.this, mNewCurrentTime);
                }
            }
        });
    }

    public interface ClockTimeUpdateListener{
        public void onClockTimeUpdate(ClockView clockView, DateTime currentTime);
    }

    private void updateProgressWithDelta(int progressDelta) {
        // 1 deg = 2 min
        mNewCurrentTime = mOriginalTime.plusMinutes(progressDelta * 2);
        setClockText(mNewCurrentTime);
        if (mTimeInterval != null && mNewCurrentTime != null
                && mTimeInterval.contains(mNewCurrentTime)) {
            mCurrentValidProgressDelta = progressDelta;
            mCurrentValidTime = mNewCurrentTime.minusMinutes(progressDelta * 2);
        }
    }

    private void setClockText(DateTime newCurrentTime) {
        if (mIs24HourFormat) {
            mTimeText.setText(SIMPLE_DATE_FORMAT_HOURS.format(newCurrentTime.toDate()));
            mTimeMeridianText.setText(R.string.hrs);
        } else {
            mTimeText.setText(SIMPLE_DATE_FORMAT_AM_PM.format(newCurrentTime.toDate()));
            mTimeMeridianText.setText(SIMPLE_DATE_FORMAT_MERIDIAN.format(newCurrentTime.toDate()).toLowerCase(Locale.US));
        }
        setSeekBarStatus(newCurrentTime);
    }

    private void setSeekBarStatus(DateTime newCurrentTime) {
        if (mTimeInterval.contains(newCurrentTime)) {
            if (newCurrentTime.getDayOfWeek() == mTimeInterval.getStart().getDayOfWeek()) {
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
        mTimeInterval = new Interval(minTime, maxTime);
        maxTime = maxTime.minusMillis(1);
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
}
