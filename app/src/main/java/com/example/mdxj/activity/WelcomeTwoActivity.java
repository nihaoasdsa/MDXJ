package com.example.mdxj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mdxj.R;

public class WelcomeTwoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
                new Handler().postDelayed(new Runnable() { //这里相当于Handler的第一个参数
            @Override
            public void run() {
                Intent mainIntent = new Intent(WelcomeTwoActivity.this, LoginActivity.class);
                WelcomeTwoActivity.this.startActivity(mainIntent);
                WelcomeTwoActivity.this.finish();
            }
        }, 2000); //这里的1000指1000毫秒即1秒
    }
}
