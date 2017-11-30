package com.bjghhnt.app.treatmentdevice.activities;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import com.bjghhnt.app.treatmentdevice.service.BallService;
import com.bjghhnt.app.treatmentdevice.service.PropagandaMusicService;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.Channels;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * home界面
 */
public class MainActivity extends MinorActivity {
    @BindView(R.id.tv_time)
    TextView tvTimeMain;
    private BatteryReceiver mBatteryReceiver;
    private final SimpleHandler mSimHandler = new SimpleHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//		startActivity(new Intent(this, PinActivity.class)
//				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//				.putExtra(PinActivity.INTENT_KEY_TO_RECEIVE, PinActivity.ROUTINE_ENTER_PIN));
        Intent propagandaService = new Intent(MainActivity.this, PropagandaMusicService.class);
        startService(propagandaService);
        Intent ballService = new Intent(MainActivity.this, BallService.class);
        startService(ballService);

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryReceiver = new BatteryReceiver();
        registerReceiver(mBatteryReceiver, filter);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone("Asia/Shanghai");
    }

    @Override
    protected SerialHandler setHandler() {
        return mSimHandler;
    }

    //更新时间
    @Override
    protected void upDateTime() {
        tvTimeMain.setText(formatTime);
//        Logger.d("time_mainAct:" + tvTimeMain.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatteryReceiver);
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getExtras().getInt("level");//获得当前电量
            int total = intent.getExtras().getInt("scale");//获得总电量
            int percent = current * 100 / total;
            System.out.println("Battery--" + percent);
        }
    }

    //治疗仪
    @OnClick(R.id.btn_tile_treatment)
    void startFullPanelActivity() {
        startActivity(new Intent(this, FullPanelActivity.class));
    }


    //宣传片
    @OnClick(R.id.btn_tile_propaganda)
    void startVideoActivity() {
//		startActivity(new Intent(this, VideoActivity.class));
        startActivity(new Intent(this, PropagandaActivity.class));
    }

    //偏好设置
    @OnClick(R.id.btn_tile_preferences)
    void startPreferencesActivity() {
//		startActivity(new Intent(this, PrefsActivity.class));
        startActivity(new Intent(this, SettingActivity.class));
    }

    //联系我们
    @OnClick(R.id.btn_tile_contact_us)
    void showContactUsInfo() {
        startActivity(new Intent(this, ContactUsActivity.class));
//        startActivity(new Intent(this, CasesActivity.class));

    }

    //产品理论
    @OnClick(R.id.btn_tile_theory)
    void showWebView() {
        Uri uri = Uri.parse(Channels.WEB_HOME_INDEX);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
//		startActivity(new Intent(this, BrowserActivity.class));
    }
}
