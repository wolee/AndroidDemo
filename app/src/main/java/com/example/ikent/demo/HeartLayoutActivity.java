package com.example.ikent.demo;

import android.os.Bundle;

import com.example.ikent.BaseActivity;
import com.example.ikent.R;
import com.kent.widget.heartlayout.HeartLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HeartLayoutActivity extends BaseActivity {
    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_layout);

        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        repeat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeartLayout.aminResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHeartLayout.aminPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    private void repeat() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.showHeartNow(randomResId());
                    }
                });
            }
        }, 50, 100);
    }

    private void oneTime() {
        mHeartLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHeartLayout.showHeartNow(randomResId());
            }
        }, 1000);
    }

    private int randomResId() {
        int index = mRandom.nextInt(HeartLayout.HEART_RES_IDS.length);
        int resId = HeartLayout.HEART_RES_IDS[index];
        return resId;
    }

}
