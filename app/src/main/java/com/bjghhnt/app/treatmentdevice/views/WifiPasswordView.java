package com.bjghhnt.app.treatmentdevice.views;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bjghhnt.app.treatmentdevice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The compound view for wifi password input dialog.
 * Created by Q on 11/03/2016.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class WifiPasswordView extends LinearLayout {

	@BindView(R.id.edit_text_wifi_password)
	EditText mEditText;

	@BindView(R.id.checkbox_remember_wifi)
	CheckBox mRememberCheckBox;

	@BindView(R.id.checkbox_show_password)
	CheckBox mShowCheckBox;

	public WifiPasswordView(Context context) {
		this(context, null);
	}

	public WifiPasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dialog_wifi_password, this, true);
		ButterKnife.bind(this);
		mShowCheckBox.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mShowCheckBox.setOnCheckedChangeListener(
				// TODO this is faulty
				(buttonView, isChecked) -> {
					if (!isChecked) {
						mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
					} else {
						mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					}
				}
		);
	}

	public CheckBox getRememberCheckBox() {
		return mRememberCheckBox;
	}
}
