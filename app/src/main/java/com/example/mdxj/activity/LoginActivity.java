package com.example.mdxj.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.example.mdxj.R;
import com.example.mdxj.jsonbean.XmlParam;
import com.example.mdxj.util.InputTextCheck;
import com.example.mdxj.util.MyXmlSerializer;
import com.example.mdxj.util.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//登录页面，jiangpan
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText et_accountname;
    private EditText et_pwd;
    private String accountname = "";
    // --------登录相关------
    private String pwd = "";
    private Button login_land;
    private Button button_clear_account, button_clear_psw;//清除按钮
    private CheckBox rem_pw;//记住密码
    private SharedPreferences sp;
   private ArrayList<XmlParam> data=new ArrayList<>();
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        doLogin();
    }

    private void init() {
        //获得实例对象
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        et_accountname = (EditText) findViewById(R.id.account);
        et_pwd = (EditText) findViewById(R.id.password);
        login_land = (Button) findViewById(R.id.login_land);
        button_clear_account = (Button) findViewById(R.id.button_clear_account);
        button_clear_psw = (Button) findViewById(R.id.button_clear_psw);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        login_land.setOnClickListener(this);
        button_clear_psw.setOnClickListener(this);
        button_clear_account.setOnClickListener(this);


        //et监听事件
        et_accountname.addTextChangedListener(mLoginInputWatcher);
        et_pwd.addTextChangedListener(mPassWordInputWatcher);
        //判断记住多选框的状态
        if (sp.getBoolean("ISCHECK", false)) {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            et_accountname.setText(sp.getString("USER_NAME", ""));
            et_pwd.setText(sp.getString("PASSWORD", ""));

        }
        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rem_pw.isChecked()) {
                    System.out.println("记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();
                } else {
                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }

            }
        });
        initdata();
    }
    private void initdata() {
        mContext = LoginActivity.this;
        if (Tool.isEmpty(data)) {
            try {
                data = MyXmlSerializer.readXml(mContext,getResources().getAssets().open("Class.xml"));
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    //et的监听事件
    private TextWatcher mLoginInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            if (et_accountname.getText().toString() != null && et_accountname.getText().toString().equals("")) {
                button_clear_account.setVisibility(View.INVISIBLE);//显示
            } else {
                button_clear_account.setVisibility(View.VISIBLE);//隐藏
            }
        }
    };
    private TextWatcher mPassWordInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (et_pwd.getText().toString() != null && et_pwd.getText().toString().equals("")) {
                button_clear_psw.setVisibility(View.INVISIBLE);//隐藏
            } else {
                button_clear_psw.setVisibility(View.VISIBLE);//显示
            }
        }
    };
    private void doLogin() {
        accountname = et_accountname.getText().toString().trim();
        pwd = et_pwd.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_land:
                accountname = et_accountname.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                if (!accountname.equals("") && !pwd.equals("")) {
                    //多选框
                    if (rem_pw.isChecked()) {
                        //记住用户名和密码
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("USER_NAME", accountname);
                        editor.putString("PASSWORD", pwd);
                        editor.commit();
                    }
                    for (int i = 0; i < data.size(); i++) {
                        Log.e("数据信息","--"+data);
                        if  (data.get(i).getAccountname().equals(accountname)) {
                            Intent intent = new Intent(LoginActivity.this, VoltageActivity.class);
                            startActivity(intent);
                        }
                    }

                } else {
                    //空状态判断
                    if (InputTextCheck.isEmpty(accountname)) {
                        Toast.makeText(LoginActivity.this, InputTextCheck.Per_NOT_NULL, Toast.LENGTH_LONG).show();

                        //用户名
                    } else if (InputTextCheck.isEmpty(pwd)) {
                        Toast.makeText(LoginActivity.this, InputTextCheck.PASS_WORD, Toast.LENGTH_LONG).show();
                    } else {

                    }

                }

                break;
        }
    }


    }


