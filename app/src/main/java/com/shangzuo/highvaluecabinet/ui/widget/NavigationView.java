package com.shangzuo.highvaluecabinet.ui.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.shangzuo.highvaluecabinet.R;

import static com.blankj.utilcode.util.ViewUtils.runOnUiThread;

public class NavigationView extends RelativeLayout {
    private    CountDownTimer countdownTimer;
    public NavigationView(Context context) {
        super(context);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.navigationview, this, true);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Navigation);

            String title = a.getString(R.styleable.Navigation_title);
            boolean isEnabled = a.getBoolean(R.styleable.Navigation_enableCountDown,false);

            TextView tv_title = findViewById(R.id.tv_navigation_title);
            TextView tv_count_down=findViewById(R.id.tv_count_down);
            MaterialButton iv_back =findViewById(R.id.iv_back);
            iv_back.setOnClickListener(view -> {
                Activity activity = (Activity) context;
                // 关闭当前 Activity
                activity.finish();
            });
            tv_title.setText(title);
            if (isEnabled){
                // 创建并启动倒计时器
                 countdownTimer = new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long secondsRemaining = millisUntilFinished / 1000;
                        runOnUiThread(() -> tv_count_down.setText(secondsRemaining+"s"));
                    }
                    @Override
                    public void onFinish() {
                        ((Activity) context).finish();
                    }
                };
                 countdownTimer.start();
            }
        }
    }
    // 在Activity的onDestroy方法中停止倒计时器
    public void stopCountdownTimer() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }
}
