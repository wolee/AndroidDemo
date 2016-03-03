package com.example.ikent.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ikent.BaseActivity;
import com.example.ikent.PopToast;
import com.example.ikent.R;

public class PopToastActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_toast);
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
                Toast.makeText(PopToastActivity.this, "点击", Toast.LENGTH_LONG).show();
            }
        }).showRightNow();
    }
}
