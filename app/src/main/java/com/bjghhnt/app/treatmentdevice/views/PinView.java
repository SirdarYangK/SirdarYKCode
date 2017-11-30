package com.bjghhnt.app.treatmentdevice.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ToneGenerator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bjghhnt.app.treatmentdevice.TreatmentApplication;
import com.bjghhnt.app.treatmentdevice.activities.PinActivity;

import java.util.ArrayList;

/**
 * This is a sample of custom view.
 * Created by Q on 11/01/2016.
 */
public class PinView extends View {

	private static final ToneGenerator sDTMF_GENERATOR =
			new ToneGenerator(0, ToneGenerator.MAX_VOLUME * 3 / 5);

	private static final int NUM_CLEAR = 10;

	private static final int NUM_ZERO = 11;

	private static final int NUM_BACK_SPACE = 12;

	private static final int STATE_OLD_PIN = 1;

	private static final int STATE_NEW_PIN_FIRST = 2;

	private static final int STATE_NEW_PIN_SECOND = 3;

	private static String mPinString;

	private int mRoutineCode;

	private int mStateCode = STATE_OLD_PIN;

	private Paint mCirclePaint;

	private Paint mRectPaint;

	private Paint mCircledTextPaint;

	private Paint mBoxedTextPaint;

	private float mMinSize;
	private float mRadius;

	private Rect mTextBoundRect;

	private Rect[] mRectArray;

	private static final int PIN_LENGTH = 4;

	private static float[] mCirclePositionX = new float[3];

	private static float[] mCirclePositionY = new float[4];

	private static ArrayList<Integer> sListOfNumbers;

	private static ArrayList<Integer> sNewPinSecondInput;

	private static ArrayList<Integer> sCurrentList;

	private static final String TAG = "PinView";

	private boolean mIsAnyNumberPressed = false;

	private int mNumPressedX = 0;

	private int mNumPressedY = 0;

	public PinView(Context context) {
		super(context, null);
	}

	public PinView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PinView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!isInEditMode()) {
			init();
		}
	}

	private void init() {
		mPinString = ((TreatmentApplication) (getContext().getApplicationContext())).getPIN();
		sListOfNumbers = new ArrayList<>(PIN_LENGTH + 1);
		sCurrentList = sListOfNumbers;

		mCirclePaint = new Paint();
		mCirclePaint.setStyle(Paint.Style.STROKE);
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(0xff64646f);

		mRectPaint = new Paint();
		mRectPaint.setStyle(Paint.Style.STROKE);
		mRectPaint.setAntiAlias(true);
		mRectPaint.setColor(0xffdddddd);

		mCircledTextPaint = new Paint();
		mCircledTextPaint.setAntiAlias(true);
		mCircledTextPaint.setColor(0xff64646f);
		mTextBoundRect = new Rect();

		mBoxedTextPaint = new Paint();
		mRectArray = new Rect[PIN_LENGTH];
	}

	private void initSizeAndPosition() {
		mRadius = 56 * mMinSize;

		float center = 300 * mMinSize;
		float distanceBetweenCircles = 3 * mRadius;
		float numPadStartY = center;
		float numPadStartX = center - distanceBetweenCircles;
		float circledTextSize = mMinSize * 50;
		float boxedTextSize = mMinSize * 75;

		int left = 0, top = 0;
		int edgeLength = (int) (center * 2 / PIN_LENGTH);
		int right = edgeLength;

		// init the rectangle for pin boxes
		for (int i = 0; i < PIN_LENGTH; i++) {
			mRectArray[i] = new Rect(left, top, right, edgeLength);
			left += edgeLength;
			right += edgeLength;
		}

		mBoxedTextPaint.setTextSize(boxedTextSize);
		mBoxedTextPaint.setTextAlign(Paint.Align.CENTER);

		//init the position of center of circles
		for (int i = 0; i < 3; i++) {
			mCirclePositionX[i] = numPadStartX + i * distanceBetweenCircles;
		}

		for (int j = 0; j < 4; j++) {
			mCirclePositionY[j] = numPadStartY + j * distanceBetweenCircles;
		}

		mCircledTextPaint.setTextSize(circledTextSize);
		mCircledTextPaint.setTextAlign(Paint.Align.CENTER);
		mCircledTextPaint.getTextBounds("0", 0, 1, mTextBoundRect);


	}

	@Override
	protected void onDraw(Canvas canvas) {
		mRectPaint.setStrokeWidth(5 * mMinSize);
		mCirclePaint.setStrokeWidth(5 * mMinSize);
		// draw boxes and text in them
		for (int i = 0; i < PIN_LENGTH; i++) {
			canvas.drawRect(mRectArray[i], mRectPaint);
			if (i < sCurrentList.size()) {
				canvas.drawText("*", mRectArray[i].centerX(),
						mRectArray[i].centerY() + mTextBoundRect.height() / 2, mBoxedTextPaint);
			}
		}

		// draw circles and text in them
		float centerX, centerY;
		for (int i = 0; i < 3; i++) {
			centerX = mCirclePositionX[i];
			for (int j = 0; j < 4; j++) {
				centerY = mCirclePositionY[j];
				if (mIsAnyNumberPressed && (mNumPressedX == i) && (mNumPressedY == j)) {
					mCirclePaint.setColor(0xffffffff);
					canvas.drawCircle(centerX, centerY, mRadius, mCirclePaint);
					mCirclePaint.setColor(0xff64646f);
				} else {
					canvas.drawCircle(centerX, centerY, mRadius, mCirclePaint);
				}
				if (j < 3) {
					canvas.drawText(Integer.toString(1 + 3 * j + i), centerX,
							centerY + mTextBoundRect.height() / 2, mCircledTextPaint);
				} else {
					switch (i) {
						case 0:
							canvas.drawText("清除", centerX,
									centerY + mTextBoundRect.height() / 2, mCircledTextPaint);
							break;
						case 1:
							canvas.drawText("0", centerX,
									centerY + mTextBoundRect.height() / 2, mCircledTextPaint);
							break;
						case 2:
							canvas.drawText("退格", centerX,
									centerY + mTextBoundRect.height() / 2, mCircledTextPaint);
							break;
					}
				}
			}
		}
		// finish activity or clear input by verifying the digits
		if (sCurrentList.size() == PIN_LENGTH) {
			if (mRoutineCode == PinActivity.ROUTINE_ENTER_PIN) {
				if (verifyPIN(mPinString)) {
					((PinActivity) getContext()).finish();
				} else {
					Toast.makeText(getContext(), "口令错误，请重新输入。", Toast.LENGTH_SHORT).show();
					sCurrentList.clear();
					invalidate();
				}
			} else if (mRoutineCode == PinActivity.ROUTINE_CHANGE_PIN) {
				if (mStateCode == STATE_OLD_PIN) {
					if (verifyPIN(mPinString)) {
						requestNewPin(false);
					} else {
						Toast.makeText(getContext(), "口令错误，无法修改。", Toast.LENGTH_SHORT).show();
						((PinActivity) getContext()).finishWithPinChange(null);
					}
				} else if (mStateCode == STATE_NEW_PIN_FIRST) {
					requestNewPin(true);

				} else if (mStateCode == STATE_NEW_PIN_SECOND) {
					if (areTwoInputSame()) {
						//change pin in PinActivity then finish it
						Toast.makeText(getContext(), "口令已修改。", Toast.LENGTH_SHORT).show();
						((PinActivity) getContext()).finishWithPinChange(
								TextUtils.join("", sListOfNumbers));
					} else {
						Toast.makeText(getContext(), "修改口令失败。", Toast.LENGTH_SHORT).show();
						((PinActivity) getContext()).finishWithPinChange(null);
					}
				}
				invalidate();
			}
		}
	}

	/**
	 * compare if two inputs of new PIN are the same
	 **/
	private boolean areTwoInputSame() {
		for (int i = 0; i < PIN_LENGTH; i++) {
			if (sListOfNumbers.get(i).intValue() != sNewPinSecondInput.get(i).intValue()) {
				return false;
			}
		}
		return true;
	}

	private void requestNewPin(boolean requestNewInputSecondTime) {
		if (!requestNewInputSecondTime) {
			sListOfNumbers.clear();
			mStateCode = STATE_NEW_PIN_FIRST;
			sNewPinSecondInput = new ArrayList<>(PIN_LENGTH);
			((PinActivity) getContext()).setText(PinActivity.ENTER_NEW_PIN);
		} else {
			mStateCode = STATE_NEW_PIN_SECOND;
			//The second time input of new pin is stored in sNewPinSecondInput
			sCurrentList = sNewPinSecondInput;
			((PinActivity) getContext()).setText(PinActivity.ENTER_NEW_PIN_AGAIN);
		}
	}

	private boolean verifyPIN(String pinString) {
		return TextUtils.join("", sListOfNumbers).equals(pinString);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				reactToNumPad(true, event.getX(), event.getY());
				return true;
			case MotionEvent.ACTION_UP:
				reactToNumPad(false, 0, 0);
				return true;
			default:
				return false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredWidth = Integer.MAX_VALUE;
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		//Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			//Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(desiredWidth, widthSize);
		} else {
			//Be whatever you want
			width = desiredWidth;
		}
		mMinSize = width / 600f;
		int size = width;
		initSizeAndPosition();
		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			//Must be this size
			height = heightSize;
		} else if (heightMode == View.MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			height = Math.min(size, heightSize);
		} else {
			//Be whatever you want
			height = size;
		}

		//MUST CALL THIS
		setMeasuredDimension(width, height);
	}

	private void storeInputAndAlterDisplay(
			int number) {
		if (number < 10 && number > -1) {
			if (sCurrentList.size() == PIN_LENGTH) {
				return;
			}
			sCurrentList.add(number);
			invalidate();
		} else if (!sCurrentList.isEmpty()) {
			if (number == NUM_CLEAR) {
				sCurrentList.clear();
			} else if (number == NUM_BACK_SPACE) {
				sCurrentList.remove(sCurrentList.size() - 1);
			}
			invalidate();
		}
	}

	/**
	 * Plays a D.T.M.F. tone; makes the circle blink
	 *
	 * @param isOnPress true when ACTION_DOWN; false when ACTION_UP
	 **/
	private void reactToNumPad(boolean isOnPress, float x, float y) {
		mIsAnyNumberPressed = isOnPress;
		if (isOnPress) {
			boolean xMatches = false;
			boolean yMatches = false;
			for (int i = 0; i < 3; i++) {
				if (Math.abs(x - mCirclePositionX[i]) <= mRadius) {
					xMatches = true;
					mNumPressedX = i;
					break;
				}
			}
			if (!xMatches) {
				return;
			}

			for (int i = 0; i < 4; i++) {
				if (Math.abs(y - mCirclePositionY[i]) <= mRadius) {
					yMatches = true;
					mNumPressedY = i;
					break;
				}
			}
			if (!yMatches) {
				return;
			}
			int number = 1 + mNumPressedX + 3 * mNumPressedY;
			if (number == NUM_ZERO) {
				number = 0;
			}

			storeInputAndAlterDisplay(number);

			sDTMF_GENERATOR.startTone(ToneGenerator.TONE_DTMF_S, 500);
			Log.d(TAG, "onTouchEvent: " + Integer.toString(1 + mNumPressedX + 3 * mNumPressedY));
		} else {
			sDTMF_GENERATOR.stopTone();
		}
		invalidate();
	}

	public void setRoutineCode(int routineCode) {
		mRoutineCode = routineCode;
	}
}
