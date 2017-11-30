package com.bjghhnt.app.treatmentdevice.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.activities.FullPanelActivity;
import com.bjghhnt.app.treatmentdevice.activities.MeterSetActivity;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.Waves;
import com.orhanobut.logger.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * this class is the custom view representing the output of a channel
 * Created by Q on 11/01/2016.
 * 自定义治疗表盘进度
 */
public class TreatingMeterView extends View implements View.OnClickListener {

    private int mChanelNo;

    private static final int MAX_MAGNITUDE = 30;

    private static final int DEGREE_SPAN = 270;

    private int mStartingDegree;

    private int mLevel = 0;

    private Paint mArcPaint;

    private Paint mLinePaint;

    private Paint mWavePaint;

    private Paint mTextPaint;

    private Paint mRoundButtonPaint;

    private float mMinSize;

    private float mGapWidth;

    private float mInnerRadius;

    private float mRadius;

    private float mCenter;
    private float mButtonRadius;

    private float mWaveInterval;

    private DecimalFormat mDecimalFormat;

    private RectF mArcRect;

    private RectF mIconRect;

    private SweepGradient mSweepGradient;

//	private RadialGradient mRadialGradient;

    private ArrayList<Integer> mWaveList;

    private int mSessionTime;

    private int mRemainingTime;

    public static final int REMAINING_TIME_UNAVAILABLE = -1;

    private float mProgressPercent = 1f;
    private TimerRedrawThread mTimerRedrawThread;

    private static Bitmap mTouchIconBitmap;

    private static Bitmap mKnobIconBitmap;

    private static Bitmap mBitmapToDraw;
    private static int switchKnobTouchIcon = 0;

    public TreatingMeterView(Context context) {
        super(context, null);
    }

    public TreatingMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreatingMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {//判断窗体是否泄漏
            // get the bitmap for both icons
            if (mKnobIconBitmap == null) {
                mKnobIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_knob_mode);
            }
            if (mTouchIconBitmap == null) {
                mTouchIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_touch_mode);
            }
            mBitmapToDraw = (TreatmentApplication.getSharedPreferences()
                    .getBoolean(Channels.KEY_ALL_USE_KNOB, false)) ? mKnobIconBitmap : mTouchIconBitmap;
            //mBitmapToDraw = getContext().getSharedPreferences()
            // only the views in full panel activity is clickable
            if (context.getClass() == FullPanelActivity.class) {
                this.setOnClickListener(this);
            }
        } else {
            mBitmapToDraw = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_knob_mode);
        }
        init();
    }

    private void init() {
        // round up the mProgressPercent
        //取所有整数部分
        mDecimalFormat = new DecimalFormat("#");
        mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);//四舍五入
        switch (this.getId()) {
            case R.id.custom_view_chn_1:
                mChanelNo = 1;
                break;
            case R.id.custom_view_chn_2:
                mChanelNo = 2;
                break;
            case R.id.custom_view_chn_3:
                mChanelNo = 3;
                break;
            case R.id.custom_view_chn_4:
                mChanelNo = 4;
                break;
            case R.id.custom_view_chn_5:
                mChanelNo = 5;
                break;
            default:
                break;
        }

        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mStartingDegree = -360 + DEGREE_SPAN / 2;

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(0xffdddddd);

        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setColor(0xff00cc00);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(0xff64646f);

        mRoundButtonPaint = new Paint();
        mRoundButtonPaint.setAntiAlias(true);
        mRoundButtonPaint.setStyle(Paint.Style.FILL);
        //mRoundButtonPaint.setColor(Color.BLUE);

        mWaveList = Waves.createWaves();
    }

    private void initSize() {
        mGapWidth = 56 * mMinSize;
        mInnerRadius = 180 * mMinSize;
        mCenter = 300 * mMinSize;
        mRadius = 208 * mMinSize;
        mButtonRadius = 35 * mMinSize;
        // the length of one interval of the wave (16 intervals can be displayed at the same time)
        mWaveInterval = (float) (mInnerRadius *
                Math.sin(Math.toRadians((360 - DEGREE_SPAN) / 2f)) / 8f);
        mArcRect = new RectF(mCenter - mRadius,
                mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);
        //canvas.drawCircle(mCenter, mCenter + mRadius, mButtonRadius, mRoundButtonPaint);
        mIconRect = new RectF(mCenter - mButtonRadius,
                mCenter + mRadius - mButtonRadius,
                mCenter + mButtonRadius, mCenter + mRadius + mButtonRadius);
//		int[] colors = {0xFFE5BD7D, 0xFFFAAA64,
//				0xFFFFFFFF, 0xFF6AE2FD,
//				0xFF8CD0E5, 0xFFA3CBCB,
//				0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D,};
        int[] colors = {0xFF005c99, 0xFF003d66,
                0xFFFFFFFF, 0xFF99d6ff,
                0xFF66c2ff, 0xFF33adff,
                0xFF0099ff, 0xFF007acc, 0xFF005c99,};
        float[] positions = {0, 1f / 8, 2f / 8, 3f / 8, 4f / 8, 5f / 8, 6f / 8, 7f / 8, 1};
        mSweepGradient = new SweepGradient(mCenter, mCenter, colors, positions);
        //TODO fix this
//		mRadialGradient = new RadialGradient(mCenter, mCenter + mRadius, mButtonRadius, 0xFF333333, 0xFFa6a6a6, Shader.TileMode.MIRROR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw the arc with sweep gradient
        mArcPaint.setStrokeWidth(mGapWidth);
        int gapDegree = getDegree();
//        Logger.d("onDraw-gapDegree-"+gapDegree);
        mArcPaint.setShader(mSweepGradient);
        canvas.drawArc(mArcRect, mStartingDegree, gapDegree - mStartingDegree, false, mArcPaint);
        mArcPaint.setShader(null);
        mArcPaint.setColor(Color.WHITE);
        canvas.drawArc(mArcRect, gapDegree, mStartingDegree + DEGREE_SPAN - gapDegree, false, mArcPaint);

        // draw the mode icon
        //canvas.drawCircle(mCenter, mCenter + mRadius, mButtonRadius, mRoundButtonPaint);
        canvas.drawBitmap(mBitmapToDraw, null, mIconRect, mRoundButtonPaint);

        // draw lines
        mLinePaint.setStrokeWidth(mMinSize * 1.5f);
        int divider = DEGREE_SPAN / MAX_MAGNITUDE;
        int numOfDivider = 360 / divider;
        int startingBlackDivider = DEGREE_SPAN / (2 * divider);
        for (int i = 0; i < numOfDivider; i++) {
            if (i <= startingBlackDivider || i >= numOfDivider - startingBlackDivider) {
                float top = mCenter - mInnerRadius - mGapWidth;
                top -= (i % 5 == 0) ? 20 * mMinSize : 0;
                canvas.drawLine(mCenter, mCenter - mInnerRadius, mCenter, top, mLinePaint);
            }
            canvas.rotate(divider, mCenter, mCenter);
        }
        // draw text
        mTextPaint.setTextSize(mMinSize * 30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < 31; i += 5) {
            float r = mInnerRadius + mGapWidth + 40 * mMinSize;
            float x = (float) (mCenter + r * Math.cos((-1 * mStartingDegree / divider - i) * Math.toRadians(divider)));
            float y = (float) (mCenter - r * Math.sin((-1 * mStartingDegree / divider - i) * Math.toRadians(divider)));
            //float x = (float) (mCenter + r * Math.cos((25 - i) / 5 * Math.PI / 4));
            //float y = (float) (mCenter - r * Math.sin((25 - i) / 5 * Math.PI / 4));
            canvas.drawText("" + i, x, y - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
        }

        float yPos;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(30 * mMinSize);
        yPos = mCenter - 70 * mMinSize - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);

        // correct remaining time can only be obtained from the incoming serial frame
        // when submitting the settings, as well as coming back to the full panel view
        // mRemainingTime is temporarily set to REMAINING_TIME_UNAVAILABLE
        // and the display changes to "治疗进度: 获取中" accordingly
        Logger.d("lose----mRemainingTime---- " + mRemainingTime);
        if (mRemainingTime != REMAINING_TIME_UNAVAILABLE) {
            canvas.drawText("治疗进度:" + mDecimalFormat.format(mProgressPercent * 100) + "%", mCenter, yPos, mTextPaint);
            mTextPaint.setColor(0xFF828E98);
            mTextPaint.setTextSize(20 * mMinSize);
            yPos = 260 * mMinSize - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
            canvas.drawText("剩余" + mRemainingTime / 60 + "小时" + mRemainingTime % 60 + "分钟", mCenter, yPos, mTextPaint);

        } else {
            canvas.drawText("治疗进度: 获取中", mCenter, yPos, mTextPaint);

        }
        yPos = mCenter - 120 * mMinSize - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
        mTextPaint.setTextSize(60 * mMinSize);
        canvas.drawText("第" + mChanelNo + "路", mCenter, yPos, mTextPaint);

        // draw the waves
        drawTheWaves(canvas);
        //postInvalidateDelayed(500); // looks weird
    }

    /**
     * The original drawTheWaves without glitches is pasted in README.md
     **/
    private void drawTheWaves(Canvas canvas) {
        /** xPos/yPos is the x/y-coordinate of the end of the current line;
         * yBaseline is the y-coordinate of the line representing 0V **/
        float xPos = (float) (mCenter - mInnerRadius * Math.sin(Math.toRadians((360 - DEGREE_SPAN) / 2f)));
        float yBaseline = (float) (mCenter + mInnerRadius * Math.cos(Math.toRadians((360 - DEGREE_SPAN) / 2f)) / 2f);
        float yPos = yBaseline;
        /** add some glitches to the wave on peaks and baseline,
         * on peaks, the glitches make the wave linearly decrease over one interval;
         * the width of the glitches on baseline is
         * glitchWidthOnBaseline = mWaveInterval - remainderInterval **/
        float glitchPeak = 5 * mMinSize;
        float glitchBase = 5 * mMinSize;
        float glitchWidthOnBaseline = 3 * mMinSize;
        float remainderInterval = mWaveInterval - glitchWidthOnBaseline;
        mWavePaint.setStrokeWidth(mMinSize * 1.5f);
        /** only draw a straight line and return if mLevel is 0**/
        if (mLevel == 0) {
            canvas.drawLine(xPos, yBaseline, 2 * mCenter - xPos, yBaseline, mWavePaint);
            return;
        }
        /** the y-coordinate of upper voltage is mCenter
         * the y-coordinate of lower voltage is posLower **/
        float posLower = 2 * yBaseline - mCenter;
        /** only draw the waves for 16 intervals **/
        int counter = 0;
        for (int i : mWaveList) {
            if (i == 0) {
                canvas.drawLine(xPos, yPos, xPos + glitchWidthOnBaseline, yBaseline, mWavePaint);
                xPos = xPos + glitchWidthOnBaseline;
                canvas.drawLine(xPos, yBaseline, xPos + remainderInterval, yBaseline, mWavePaint);
                xPos = xPos + remainderInterval;
                yPos = yBaseline;
            } else {
                if (i == 1) {
                    canvas.drawLine(xPos, yPos, xPos, mCenter, mWavePaint);
                    yPos = mCenter + glitchPeak;
                    canvas.drawLine(xPos, mCenter, xPos + mWaveInterval, yPos, mWavePaint);
                    xPos = xPos + mWaveInterval;
                    canvas.drawLine(xPos, yPos, xPos, yBaseline + glitchBase, mWavePaint);
                    yPos = yBaseline + glitchBase;
                } else {
                    canvas.drawLine(xPos, yPos, xPos, posLower, mWavePaint);
                    yPos = posLower - glitchPeak;
                    canvas.drawLine(xPos, posLower, xPos + mWaveInterval, yPos, mWavePaint);
                    xPos = xPos + mWaveInterval;
                    canvas.drawLine(xPos, yPos, xPos, yBaseline - glitchBase, mWavePaint);
                    yPos = yBaseline - glitchBase;
                }
            }
            counter++;
            if (counter == 16) {
                Waves.shiftToLeft(mWaveList);
                break;
            }
        }
    }

    private int getDegree() {
        checkLevel(mLevel);
        return -360 + DEGREE_SPAN / 2 + mLevel * DEGREE_SPAN / MAX_MAGNITUDE;
    }

    private void checkLevel(float m) {
        if (m < 0 || m > 30) {
            throw new RuntimeException("Magnitude out of range:" + m);
        }
    }

    public void setChanelNo(int chnNo) {
        this.mChanelNo = chnNo;
    }

    public void setLevel(int m) {
        checkLevel(m);
        mLevel = m;
        Logger.d("onDraw-mLevel-"+mLevel);
        // TODO this is likely to be redundant
        //invalidate();
    }

    public int getMagnitude() {
        return mLevel;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            return this.performClick();
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // if you don't handle the ACTION_DOWN event (return true),
            // the following ACTION_UP event will not be handled
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), MeterSetActivity.class);
        intent.putExtra(Channels.INTENT_KEY_CHANNEL_NUMBER, mChanelNo);
        v.getContext().startActivity(intent);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimerRedrawThread = new TimerRedrawThread();
        mTimerRedrawThread.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimerRedrawThread.halt();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(widthSize, heightSize);
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
        mMinSize = size / 600f;
        initSize();
        //MUST CALL THIS, really?
        setMeasuredDimension(size, size);
    }

    public void setSessionTime(int sessionTime) {
        mSessionTime = sessionTime;
    }

    public void setRemainingTimeAndProgress(int remainingTime) {
        setRemainingTime(remainingTime);
        Logger.d("lose----remainingTime----" + remainingTime);
        //int sessionTime = TreatmentApplication.getSharedPreferences().getInt(keyInSharedPreferences, 0);
        //this.mProgressPercent = (sessionTime == 0) ? 1f : 1 - remainingTime / (float) sessionTime;
        mProgressPercent = (mSessionTime == 0) ? 1f : 1 - remainingTime / (float) mSessionTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.mRemainingTime = remainingTime;
    }

    public static void changeIcon() {
        mBitmapToDraw = (TreatmentApplication.getSharedPreferences()
                .getBoolean(Channels.KEY_ALL_USE_KNOB, false)) ? mKnobIconBitmap : mTouchIconBitmap;
    }

    public static int getSwitchKnobTouchIcon() {
        return switchKnobTouchIcon;
    }

    public static Bitmap getChangeIcon() {
        return mBitmapToDraw;
    }

    private class TimerRedrawThread extends Thread {

        private final long timer = 500; // 500ms

        public void halt() {
            this.runFlag = false;
        }

        private boolean runFlag = true;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                postInvalidate();
                try {
                    Thread.sleep(timer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
