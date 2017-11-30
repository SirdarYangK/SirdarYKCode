package com.bjghhnt.app.treatmentdevice.activities;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 联系我们界面
 *
 */
public class ContactUsActivity extends MinorActivity {

	private static final String TAG = "ContactUsActivity";
	private final SimpleHandler mSimHandler = new SimpleHandler(this);
	@BindView(R.id.text_view_contact_us)
	TextView mTextViewContactUs;
	@BindView(R.id.tv_time)
	TextView tvTimeContactUs;

	@Override
	protected void upDateTime() {
		tvTimeContactUs.setText(formatTime);
		Logger.d("time_contactUsAct:" + tvTimeContactUs.getText());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
		ButterKnife.bind(this);
		mTextViewContactUs.setText(Html.fromHtml("<P align=center><SUP><FONT color=#ff0000 size=4><STRONG>订购电话：" +
				"400-636-0305 </STRONG></FONT></SUP></P><DIV align=center><FONT size=3 face=arial><FONT size=2>" +
				"北京金华汉新技术有限责任公司</FONT></FONT></DIV><DIV align=center><FONT size=3 face=arial><FONT size=2>" +
				"电子针灸全国统一电话：400-633-6006&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</FONT></FONT></DIV>" +
				"<DIV align=center><FONT size=3 face=arial><FONT size=2>电子针灸医学咨询电话：010-64097850-53</FONT></FONT></DIV>" +
				"<DIV align=center><FONT size=3 face=arial><FONT size=2>招商电话：010-64097840 /64055555&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
				"传真：010-64097803<p><BR>地址：北京市东城区雍和大厦B座7层 邮编：100007<p><BR>&nbsp;备案号 <A href=\"http://www.miibeian.gov.cn\">京ICP备12037469号</A>&nbsp;| " +
				"<A href=\"http://www.bjghhnt.com/news/html/1042.html\" target=_blank>" +
				"国家药监局（京）-非经营性-2013-0066</A>&nbsp;<p>|&nbsp;京公网安备11010102001229号</FONT></FONT></DIV>"));
		Log.d(TAG, "onCreate: created");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume: resumed");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart: started");
	}

	@Override
	protected SerialHandler setHandler() {
		return mSimHandler;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause: paused");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop: stopped");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy: destroyed");
	}

	@OnClick(R.id.btn_back_from_contact_us)
	void goBack() {
		super.finish();
	}
}
