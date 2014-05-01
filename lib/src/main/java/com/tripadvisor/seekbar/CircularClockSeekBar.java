package com.tripadvisor.seekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import org.jetbrains.annotations.NotNull;

import static android.graphics.Paint.Style.STROKE;
import static android.os.AsyncTask.Status.RUNNING;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Created by ksarmalkar on 4/23/14.
 */
public final class CircularClockSeekBar extends View {

    public static final int MAX_SLEEP_NANOS = 999999;
    public static final int NANO_SECS = 1000000;
    public static final int TOTAL_DEGREES_INT = 360;
    public static final double TOTAL_DEGREES_DOUBLE = 360.0;

    /**
     * The listener to listen for changes
     */
    private OnSeekBarChangeListener mListener;

    /**
     * The angle of progress
     */
    private float mAngle;

    /**
     * The width of the progress ring
     */
    private int mBarWidth;

    /**
     * The maximum progress amount
     */
    private int mMaxProgress = 120;

    /**
     * The current progress
     */
    private int mProgress;

    /**
     * The change in progress
     */
    private int mDeltaProgress;

    /**
     * The progress percent
     */
    private int mProgressPercent;

    /**
     * The X coordinate for the top left corner of the marking drawable
     */
    private float mMarkerPositionX;

    /**
     * The Y coordinate for the top left corner of the marking drawable
     */
    private float mMarkerPositionY;

    /**
     * The X coordinate for the current position of the marker, pre adjustment
     * to center
     */
    private float markPointX;

    /**
     * The Y coordinate for the current position of the marker, pre adjustment
     * to center
     */
    private float markPointY;

    /**
     * The adjustment factor. This adds an adjustment of the specified size to
     * both sides of the progress bar, allowing touch events to be processed
     * more user friendlily (yes, I know that's not a word)
     */
    private float mAdjustmentFactor = 100.0f;

    /**
     * The flag to see if the setProgress() method was called from our own
     * View's setAngle() method, or externally by a user.
     */
    private boolean mFromUser;

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private float mInnerRadius;
    private float mOuterRadius;
    private float mCircleCenterX;
    private float mCircleCenterY;
    private boolean mIsPressed;
    private boolean mChanged;
    private boolean mIsProgressSetViaApi;
    private Bitmap mScrubberNormal;
    private Bitmap mScrubberPressed;
    private Paint mCirclePaint;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private RotateAnimationTask mRotateAnimationTask;
    private DecelerateInterpolator mInterpolator;

    public CircularClockSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initDrawable(context, attrs, defStyle);
    }

    public CircularClockSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularClockSeekBar(Context context) {
        this(context, null, 0);
    }

    public final void initDrawable(Context context, AttributeSet attrs, int defStyle) {

        mListener = new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularClockSeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(CircularClockSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularClockSeekBar seekBar) {
            }

            @Override
            public void onAnimationComplete(CircularClockSeekBar seekBar) {
            }
        };

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(STROKE);
        mCirclePaint.setColor(getResources().getColor(R.color.seekbar_color));// Set default background color to Gray
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(getResources().getDimension(R.dimen.seekbar_width));

        mBarWidth = (int) getResources().getDimension(R.dimen.seekbar_width);

        mScrubberNormal = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.scrubber_control_normal_holo_green);
        mScrubberPressed = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.scrubber_control_pressed_holo_green);

        Resources r = context.getResources();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularClockSeekBar, defStyle, 0);
        try {
            mHourHand = attributes.getDrawable(R.styleable.CircularClockSeekBar_hand_hour);
            if (mHourHand == null) {
                mHourHand = r.getDrawable(R.drawable.time_shorthand);
            }

            mMinuteHand = attributes.getDrawable(R.styleable.CircularClockSeekBar_hand_minute);
            if (mMinuteHand == null) {
                mMinuteHand = r.getDrawable(R.drawable.time_longhand);
            }
        } finally {
            attributes.recycle();
        }

        mWidth = getWidth();
        mHeight = getHeight();
    }

    public void roundToNearestDegree(int degree) {
        int mod = mDeltaProgress % degree;
        int halfOfDegree = degree / 2;
        final int oldDegree = round(mAngle);
        final int newDegree;
        if (mod < 0) {
            if (-mod <= halfOfDegree) {
                // round to previous
                newDegree = round(mAngle - mod);
            } else {
                // round to next
                newDegree = round(mAngle - (degree + mod));
            }
        } else {
            if (mod <= halfOfDegree) {
                // round to previous
                newDegree = round(mAngle - mod);
            } else {
                // round to next
                newDegree = round(mAngle + (degree - mod));
            }
        }
        final int delta = abs(newDegree - oldDegree);
        startAnimation(delta, oldDegree, newDegree);
    }

    public void animateToDelta(int fromDelta, int toDelta) {
        final int delta = abs(toDelta - fromDelta);
        final int oldDegrees = round(mAngle);
        final int newDegrees = round(mAngle + (toDelta - fromDelta));
        startAnimation(delta, oldDegrees, newDegrees);
    }

    private void startAnimation(final int delta, final int oldDegrees, final int newDegrees) {
        mInterpolator = new DecelerateInterpolator();
        if (newDegrees - oldDegrees < 0) {
            // we are going anti-clock wise
            mRotateAnimationTask = new RotateAnimationTask(newDegrees, oldDegrees) {
                @Override
                protected Void doInBackground(Void... params) {
                    for (int i = oldDegrees, j = 0; i >= newDegrees; i--, j++) {
                        try {
                            animationSleep(j, mInterpolator, delta);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        publishProgress(i);
                    }
                    return null;
                }
            };
            mRotateAnimationTask.execute();
        } else {
            // we are going clock wise
            mRotateAnimationTask = new RotateAnimationTask(newDegrees, oldDegrees) {
                @Override
                protected Void doInBackground(Void... params) {
                    for (int i = oldDegrees, j = 0; i <= newDegrees; i++, j++) {
                        try {
                            animationSleep(j, mInterpolator, delta);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        publishProgress(i);
                    }
                    return null;
                }
            };
            mRotateAnimationTask.execute();
        }
    }

    private void animationSleep(int j, Interpolator interpolator, float delta) throws InterruptedException {
        int interpolation = (int) (interpolator.getInterpolation(j / delta) * 10000000);
        int milliseconds = 0;
        if (interpolation >= MAX_SLEEP_NANOS) {
            milliseconds = interpolation / NANO_SECS;
            interpolation = interpolation % NANO_SECS;
        }
        Thread.sleep(milliseconds, interpolation);
    }

    private abstract class RotateAnimationTask extends AsyncTask<Void, Integer, Void> {

        private int mNewDegrees;
        private int mOldDegrees;

        public RotateAnimationTask(int newDegrees, int oldDegrees) {
            mNewDegrees = newDegrees;
            mOldDegrees = oldDegrees;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            setAngle(values[0]);
            mIsProgressSetViaApi = true;
            invalidate();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // adjust and make it positive to maintain consistency of internal state.
            // as our angle never goes negative.
            mNewDegrees = mNewDegrees <= 0 ? TOTAL_DEGREES_INT + mNewDegrees : mNewDegrees;
            setAngle(mNewDegrees);
            mDeltaProgress += getDelta(mOldDegrees, mNewDegrees);
            mListener.onProgressChanged(CircularClockSeekBar.this, mProgress, mFromUser);
            invalidate();
            boolean isRoundingRequired = mDeltaProgress % 30 != 0;
            if (isRoundingRequired) {
                roundToNearestDegree(30);
            } else {
                mListener.onAnimationComplete(CircularClockSeekBar.this);
            }
        }
    }

    public enum ClockRangeStatus {
        VALID_RANGE,
        DIFFERENT_DAY_OF_WEEK,
        INVALID_RANGE
    }

    public void setSeekBarStatus(ClockRangeStatus status) {
        switch (status) {
            case DIFFERENT_DAY_OF_WEEK:
                mCirclePaint.setColor(getResources().getColor(R.color.orange));
                mScrubberNormal = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.scrubber_control_normal_holo_orange);
                mScrubberPressed = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.scrubber_control_pressed_holo_orange);
                break;
            case INVALID_RANGE:
                mCirclePaint.setColor(getResources().getColor(R.color.error_clock));
                mScrubberNormal = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.scrubber_control_normal_holo_red);
                mScrubberPressed = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.scrubber_control_pressed_holo_red);
                break;
            default:
                mCirclePaint.setColor(getResources().getColor(R.color.seekbar_color));
                mScrubberNormal = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.scrubber_control_normal_holo_green);
                mScrubberPressed = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.scrubber_control_pressed_holo_green);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mWidth) {
            hScale = widthSize / (float) mWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mHeight) {
            vScale = heightSize / (float) mHeight;
        }

        float scale = min(hScale, vScale);

        setMeasuredDimension(resolveSize((int) (mWidth * scale), widthMeasureSpec),
                resolveSize((int) (mHeight * scale), heightMeasureSpec));

        int width = getMeasuredWidth();// Get View Width
        int height = getMeasuredHeight();// Get View Height

        int size = (width > height) ? height : width; // Choose the smaller
        // between width and
        // height to make a
        // square

        mCircleCenterX = width / 2; // Center X for circle
        mCircleCenterY = height / 2; // Center Y for circle
        mOuterRadius = size / 2 - mScrubberPressed.getWidth() / 2; // Radius of the outer circle

        mInnerRadius = mOuterRadius - mBarWidth; // Radius of the circle

        float startPointX = mCircleCenterX; //The X coordinate for 12 O'Clock
        float startPointY = mCircleCenterY - mOuterRadius;// 12 O'clock Y coordinate
        if (!mIsProgressSetViaApi) {
            markPointX = startPointX;// Initial location of the marker X coordinate
            markPointY = startPointY;// Initial location of the marker Y coordinate
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        int w = getWidth();
        int h = getHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = min(availableWidth / (float) w,
                    (float) availableHeight / h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }
        canvas.save();

        if (mIsProgressSetViaApi) {
            markPointX = mCircleCenterX - (float) (mOuterRadius * cos(toRadians((mProgress * 360.0d / mMaxProgress) + 90.0d)));
            markPointY = mCircleCenterY - (float) (mOuterRadius * sin(toRadians((mProgress * 360.0d / mMaxProgress) + 90.0d)));
        }
        mMarkerPositionX = getXFromAngle();
        mMarkerPositionY = getYFromAngle();

        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mOuterRadius, mCirclePaint);
        drawMarkerAtProgress(canvas);

        if (mIsProgressSetViaApi) {
            mAngle = mProgress * 360.0f / mMaxProgress;
        }
        canvas.rotate(mProgress * 360.0f / mMaxProgress, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(0, x, y);

        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }

        super.onDraw(canvas);
    }

    /**
     * Draw marker at the current progress point onto the given canvas.
     *
     * @param canvas the canvas
     */
    private void drawMarkerAtProgress(Canvas canvas) {
        if (mIsPressed) {
            canvas.drawBitmap(mScrubberPressed, mMarkerPositionX, mMarkerPositionY, null);
        } else {
            canvas.drawBitmap(mScrubberNormal, mMarkerPositionX, mMarkerPositionY, null);
        }
    }

    /**
     * Gets the X coordinate of the arc's end arm's point of intersection with
     * the circle
     *
     * @return the X coordinate
     */
    private float getXFromAngle() {
        int size1 = mScrubberNormal.getWidth();
        int size2 = mScrubberPressed.getWidth();
        int adjust = (size1 > size2) ? size1 : size2;
        return markPointX - (adjust / 2.0f);
    }

    /**
     * Gets the Y coordinate of the arc's end arm's point of intersection with
     * the circle
     *
     * @return the Y coordinate
     */
    private float getYFromAngle() {
        int size1 = mScrubberNormal.getHeight();
        int size2 = mScrubberPressed.getHeight();
        int adjust = (size1 > size2) ? size1 : size2;
        return markPointY - (adjust / 2.0f);
    }

    public float getAngle() {
        return mAngle;
    }

    private void setAngle(float angle) {
        this.mAngle = angle;
        float donePercent = (this.mAngle / 360.0f) * mMaxProgress;
        float progress = (donePercent / mMaxProgress) * getMaxProgress();
        setProgressPercent(round(donePercent));
        mFromUser = true;
        mIsProgressSetViaApi = false;
        setProgressInternal(round(progress));
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mFromUser = true;
        // since this is set by api
        // we need to calculate markPointX and markPointY
        mIsProgressSetViaApi = true;
        setProgressInternal(progress);
    }

    private void setProgressInternal(int progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            if (!mFromUser) {
                int newPercent = (this.mProgress / this.mMaxProgress) * mMaxProgress;
                int newAngle = (newPercent / mMaxProgress) * TOTAL_DEGREES_INT;
                this.setAngle(newAngle);
                mProgressPercent = newPercent;
            }
            mListener.onProgressChanged(this, mProgress, mFromUser);
            mFromUser = false;
        }
    }

    @Override
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        if (mRotateAnimationTask != null && mRotateAnimationTask.getStatus() == RUNNING) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moved(x, y, false);
                break;
            case MotionEvent.ACTION_MOVE:
                mListener.onStartTrackingTouch(this);
                moved(x, y, false);
                break;
            case MotionEvent.ACTION_UP:
                mListener.onStopTrackingTouch(this);
                moved(x, y, true);
                break;
        }
        return true;
    }

    /**
     * This prevents the touch event from bubbling up if this views parent is scrollable
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent event) {
        if (getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * Moved.
     *
     * @param x  the x
     * @param y  the y
     * @param up the up
     */
    private void moved(float x, float y, boolean up) {
        float distance = (float) sqrt(pow((x - mCircleCenterX), 2) + pow((y - mCircleCenterY), 2));
        if (distance < mOuterRadius + mAdjustmentFactor && distance > mInnerRadius - mAdjustmentFactor && !up) {
            mIsPressed = true;

            markPointX = (float) (mCircleCenterX + mOuterRadius * cos(atan2(x - mCircleCenterX, mCircleCenterY - y) - (PI / 2)));
            markPointY = (float) (mCircleCenterY + mOuterRadius * sin(atan2(x - mCircleCenterX, mCircleCenterY - y) - (PI / 2)));

            float degrees = (float) ((float) ((toDegrees(atan2(x - mCircleCenterX, mCircleCenterY - y)) + TOTAL_DEGREES_DOUBLE)) % TOTAL_DEGREES_DOUBLE);
            // and to make it count 0-360
            if (degrees < 0) {
                degrees += 2 * PI;
            }

            int newDegrees = round(degrees);
            int oldDegrees = (int) mAngle;
            int newDelta;
            if ((newDelta = getDelta(oldDegrees, newDegrees)) == 0) {
                // no need to invalidate or set new values nothing changed
                return;
            }
            mDeltaProgress += newDelta;
            setAngle(newDegrees);
            invalidate();
        } else {
            mIsPressed = false;
            invalidate();
        }
    }

    /**
     * Sets the adjustment factor.
     *
     * @param adjustmentFactor the new adjustment factor
     */
    public void setAdjustmentFactor(float adjustmentFactor) {
        this.mAdjustmentFactor = adjustmentFactor;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getProgressPercent() {
        return mProgressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.mProgressPercent = progressPercent;
    }

    /**
     * Gets the total progress since instantiation.
     *
     * @return the total progress
     */
    public int getProgressDelta() {
        return mDeltaProgress;
    }

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

        void onAnimationComplete(CircularClockSeekBar seekBar);
    }

    public void setSeekBarChangeListener(OnSeekBarChangeListener listener) {
        mListener = listener;
    }


    public static boolean shouldMoveClockwise(int oldValue, int newValue) {
        int dist = abs(newValue - oldValue);
        int direction = oldValue < newValue ? 1 : -1;
        direction = dist < TOTAL_DEGREES_INT / 2 ? direction : -direction;
        return direction == 1;
    }

    public static int getDistanceTo(int oldValue, int newValue) {
        int totalDegrees = TOTAL_DEGREES_INT;
        boolean isClockWise = shouldMoveClockwise(oldValue, newValue);
        int dist = (newValue - oldValue) % totalDegrees;

        if (isClockWise) {
            if (abs(dist) > totalDegrees / 2) {
                dist = (totalDegrees - oldValue) + newValue;
            } else {
                dist = newValue - oldValue;
            }
        } else {
            if (abs(dist) > totalDegrees / 2) {
                dist = totalDegrees - (newValue - oldValue);
                dist = -dist;
            } else {
                dist = newValue - oldValue;
            }
        }
        return dist;
    }

    public static int getDelta(int oldDegrees, int newDegrees) {
        if ((oldDegrees == TOTAL_DEGREES_INT && newDegrees == 0) || (newDegrees == TOTAL_DEGREES_INT && oldDegrees == 0)
                || oldDegrees == 0 || newDegrees == 0) {
            // dont worry about delta for this condition as this basically means they are same.
            // we have this granular values when user touches/scrolls
            return 0;
        }
        return getDistanceTo(oldDegrees, newDegrees);
    }
}
