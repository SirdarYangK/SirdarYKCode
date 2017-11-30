package com.bjghhnt.app.treatmentdevice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bjghhnt.app.treatmentdevice.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DebugActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		ButterKnife.bind(this);

		//debug - measurement of resolution
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d(TAG, "onCreate: " + metrics.heightPixels + "x" + metrics.widthPixels);
	}

	@OnClick(R.id.btn_sweep_gradient)
	void showSweepGradient() {
		startActivity(new Intent(this, GradientActivity.class));
	}

	@OnClick(R.id.btn_temperature_meter)
	void showTemperatureMeter() {
		startActivity(new Intent(this, TemperatureActivity.class));
	}

	@OnClick(R.id.btn_framing_test)
	void showFramingTest() {
		startActivity(new Intent(this, FramingActivity.class));
	}

	@OnClick(R.id.btn_finish_debug)
	void back() {
		super.finish();
	}
}
