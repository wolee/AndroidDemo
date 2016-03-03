package com.example.ikent.appdemo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ikent.heartlayout.HeartLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DemoActivity extends BaseActivity {

    private static final String TAG = "tag";
    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(randomColor());
                    }
                });
            }
        }, 500, 200);

        String[] args = {"1", "2", "3"};
        String string = getString(R.string.test_string_key, args);
        System.out.println(string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    public void show(View v) {
        String suffix = "点击查看";
        String msg = "Toast消息内容" + "\t";
        int color = 0xff0000ff;
        SpannableString spannableString = new SpannableString(msg + suffix);
        spannableString.setSpan(new ForegroundColorSpan(color),
                msg.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        PopToast.make(this, spannableString, 3000, new PopToast.OnClickListener() {
            @Override
            public void onClick(PopToast toast) {
                Log.d(TAG, "showPushToast onClick");
                Toast.makeText(DemoActivity.this, "点击", Toast.LENGTH_LONG).show();
            }
        }).showRightNow();
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }


}
