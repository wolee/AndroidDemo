package com.example.ikent.demo;

import android.os.Bundle;

import com.example.ikent.BaseActivity;
import com.example.ikent.R;
import com.kent.widget.floatheart.FloatHeartSurfaceView;
import com.kent.widget.floatheart.FloatHeartView;
import com.kent.widget.heartlayout.HeartLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HeartLayoutActivity extends BaseActivity {
    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;
    private FloatHeartSurfaceView mFloatHeart;
    private FloatHeartView mFloatHeartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_layout);

        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mFloatHeart = (FloatHeartSurfaceView) findViewById(R.id.float_heart);
        mFloatHeartView = (FloatHeartView) findViewById(R.id.float_heart_view);
        repeat();
        mFloatHeart.addHeart(floatHeartRandomResId());
        mFloatHeartView.addHeart(floatHeartViewRandomResId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeartLayout.aminResume();
        mFloatHeart.startFloatAnim();
        mFloatHeartView.aminResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHeartLayout.aminPause();
        mFloatHeart.stopFloatAnim();
        mFloatHeartView.aminPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mFloatHeart.destroy();
    }

    private void repeat() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.showHeartNow(randomResId());
                        mFloatHeart.addHeart(floatHeartRandomResId());
                        mFloatHeartView.addHeart(floatHeartViewRandomResId());
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

    private int floatHeartRandomResId() {
        int index = mRandom.nextInt(FloatHeartSurfaceView.HEART_RES_IDS.length);
        int resId = FloatHeartSurfaceView.HEART_RES_IDS[index];
        return resId;
    }

    private int floatHeartViewRandomResId() {
        int index = mRandom.nextInt(FloatHeartView.HEART_RES_IDS.length);
        int resId = FloatHeartView.HEART_RES_IDS[index];
        return resId;
    }
}
