package com.example.mdxj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mdxj.R;

//2017.6.7 电压选择页面
public class VoltageActivity extends Activity implements View.OnClickListener{
    private LinearLayout low_tension,medium_voltage,high_voltage;
    private RelativeLayout re_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voltage);
        findId();
        listener();
    }

    private void findId() {
        re_setting = (RelativeLayout)findViewById(R.id.re_setting);//设置
        low_tension=(LinearLayout)findViewById(R.id.low_tension);//低压
        medium_voltage=(LinearLayout)findViewById(R.id.medium_voltage);//中压
        high_voltage=(LinearLayout)findViewById(R.id.medium_voltage);//高压
    }
    private void listener() {
        re_setting.setOnClickListener(this);
        low_tension.setOnClickListener(this);
        medium_voltage.setOnClickListener(this);
        high_voltage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.re_setting://设置
                    Intent intent = new Intent(this, SettingActivity.class);
                    this.startActivity(intent);
                    break;
                case R.id.low_tension://低压
                    break;
                case R.id.medium_voltage://中压
                    break;
                case R.id.high_voltage://高压
                    break;
            }
    }
}
