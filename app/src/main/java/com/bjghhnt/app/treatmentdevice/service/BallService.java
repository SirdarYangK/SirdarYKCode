package com.bjghhnt.app.treatmentdevice.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.bjghhnt.app.treatmentdevice.R;

/**
 * Created by Development_Android on 2016/12/22.
 */

public class BallService extends Service implements View.OnTouchListener {

    private View view;
    int startX;
    int startY;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        addBallView();
        view.setOnTouchListener(this);
    }

    public void addBallView() {

        if (view == null) {
            view = View.inflate(getApplicationContext(), R.layout.ball, null);
            windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            int screenWidth = windowManager.getDefaultDisplay().getWidth();
            int screenHeight = windowManager.getDefaultDisplay().getHeight();
            params = new WindowManager.LayoutParams();
            params.x = screenWidth;
            params.y = screenHeight / 2;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.format = PixelFormat.RGBA_8888;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            view.setLayoutParams(params);
            windowManager.addView(view, params);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
//                onKeyDown(KeyEvent.KEYCODE_BACK, null);
//                sendKeyCode(KeyEvent.KEYCODE_BACK);
//                IBinder wmbinder = ServiceManager.getService( "window" );
//                IWindowManager m_WndManager = IWindowManager.Stub.asInterface( wmbinder );
                }
            });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下屏幕
            {
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE://手指在屏幕上移动
            {
                int newX = (int) event.getRawX();
                int newY = (int) event.getRawY();
                int dx = newX - startX;
                int dy = newY - startY;
                        /*重新设置窗体的开始位置，结束位置*/
                params.x += dx;
                params.y += dy;
                if (params.x < 0) {
                    params.x = 0;
                } else if (params.y < 0) {
                    params.y = 0;
                } else if (params.x > (windowManager.getDefaultDisplay().getWidth() - view.getWidth())) {
                    params.x = windowManager.getDefaultDisplay().getWidth() - view.getWidth();
                } else if (params.y > (windowManager.getDefaultDisplay().getHeight() - view.getHeight())) {
                    params.y = windowManager.getDefaultDisplay().getHeight() - view.getHeight();
                }
                windowManager.updateViewLayout(view, params);
                // 重新初始化手指的开始结束位置。
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            }
            case MotionEvent.ACTION_UP://手指离开屏幕
            {
//                //记录控件距离屏幕左上角的坐标
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt("lastx", params.x);
//                editor.putInt("lasty", params.y);
//                editor.apply();
                break;
            }

        }
        return false;
    }
}
