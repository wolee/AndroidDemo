package com.example.ikent.demo;

import android.os.Bundle;

import com.example.ikent.BaseActivity;
import com.example.ikent.R;
import com.kent.widget.floatheart.FloatHeartView;
import com.kent.widget.floatheart.opengl.GLFloatHeartView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HeartLayoutActivity extends BaseActivity {
    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private GLFloatHeartView mGLFloatHeart;
    private FloatHeartView mViewFloatHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_layout);

        mGLFloatHeart = (GLFloatHeartView) findViewById(R.id.gl_float_heart);
        mViewFloatHeart = (FloatHeartView) findViewById(R.id.view_float_heart);
        repeat();
        mGLFloatHeart.addHeart(glFloatHeartViewRandomIndex());
        mViewFloatHeart.addHeart(floatHeartViewRandomResId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLFloatHeart.aminResume();
        mViewFloatHeart.aminResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLFloatHeart.aminPause();
        mViewFloatHeart.aminPause();
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
                mViewFloatHeart.post(new Runnable() {
                    @Override
                    public void run() {
                        mGLFloatHeart.addHeart(glFloatHeartViewRandomIndex());
                        mViewFloatHeart.addHeart(floatHeartViewRandomResId());
                    }
                });
            }
        }, 50, 100);
    }

    private void oneTime() {
        mViewFloatHeart.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGLFloatHeart.addHeart(glFloatHeartViewRandomIndex());
                mViewFloatHeart.addHeart(floatHeartViewRandomResId());
            }
        }, 1000);
    }

    private int glFloatHeartViewRandomIndex() {
        int index = mRandom.nextInt(mGLFloatHeart.HEART_RES_IDS.length);
        return index;
    }

    private int floatHeartViewRandomResId() {
        int index = mRandom.nextInt(FloatHeartView.HEART_RES_IDS.length);
        int resId = FloatHeartView.HEART_RES_IDS[index];
        return resId;
    }
}
