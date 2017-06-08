package com.example.mdxj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mdxj.R;

//2017.6.7 电压选择页面
public class VoltageActivity extends Activity implements View.OnClickListener{
    private LinearLayout low_voltage,medium_voltage,high_voltage;
    private RelativeLayout re_setting;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voltage);
        findId();
        listener();
    }

    private void findId() {
        re_setting = (RelativeLayout)findViewById(R.id.re_setting);//设置
        low_voltage=(LinearLayout)findViewById(R.id.low_voltage);//低压
        medium_voltage=(LinearLayout)findViewById(R.id.medium_voltage);//中压
        high_voltage=(LinearLayout)findViewById(R.id.high_voltage);//高压
    }
    private void listener() {
        re_setting.setOnClickListener(this);
        low_voltage.setOnClickListener(this);
        medium_voltage.setOnClickListener(this);
        high_voltage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.re_setting://设置
                    intent = new Intent(this, SettingActivity.class);
                    this.startActivity(intent);
                    break;
                case R.id.low_voltage://低压
                    intent = new Intent(this, CatagoryOneActivity.class);
                    intent.putExtra("voltage", "低压");
                    intent.putExtra("ID", 1);
                    this.startActivity(intent);
                    break;
                case R.id.medium_voltage://中压
                    intent = new Intent(this, CatagoryOneActivity.class);
                    intent.putExtra("voltage", "中压");
                    intent.putExtra("ID", 2);
                    this.startActivity(intent);
                    break;
                case R.id.high_voltage://高压
                    intent = new Intent(this, CatagoryOneActivity.class);
                    intent.putExtra("voltage","高压");
                    intent.putExtra("ID", 3);
                    this.startActivity(intent);
                    break;
            }
    }
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
