package com.example.mdxj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdxj.DwpcApplication;
import com.example.mdxj.R;
import com.example.mdxj.adapter.ListDialogAdapter;
import com.example.mdxj.model.SettingData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SettingActivity extends Activity {

    private RelativeLayout re_name;
    private RelativeLayout re_worktype;
    private RelativeLayout re_picsize;
    private RelativeLayout re_deleteall;
    private RelativeLayout re_default;
    private RelativeLayout re_csv;
    private RelativeLayout re_startindex;

    private TextView tv_name;
    private TextView tv_worktype;
    private TextView tv_picsize;
    private TextView tv_deleteall;
    private TextView tv_startindex;

    private static final int UPDATE_NAME = 1;
    
    private SettingData sd = null;
    private SharedPreferences sp;
    private int curWorkTypeSelected = 0;
    private int curPicSizeSelected = 0;
    private int curDeleteAllSelected = 0;

    private List<String> workTypeList = new ArrayList<String>();
    private List<String> picSizeList = new ArrayList<String>();
    private List<String> yesornoList = new ArrayList<String>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (sd == null) {
        	sd = DwpcApplication.getInstance().getSettingData();
        }
        
        workTypeList.add("低压");
        workTypeList.add("高压");
        
        picSizeList.add("540 x 960");
        picSizeList.add("720 x 1280");
        picSizeList.add("1080 x 1920");

        yesornoList.add("是");
        yesornoList.add("否");
        
        initView();
        
        initValue();
    }

    private void initView() {
        re_name = (RelativeLayout) this.findViewById(R.id.re_name);
        re_worktype = (RelativeLayout) this.findViewById(R.id.re_worktype);
        re_picsize = (RelativeLayout) this.findViewById(R.id.re_picsize);
        re_deleteall = (RelativeLayout) this.findViewById(R.id.re_deleteall);
        re_default = (RelativeLayout) this.findViewById(R.id.re_default);
        re_csv = (RelativeLayout) this.findViewById(R.id.re_csv);
        re_startindex = (RelativeLayout) this.findViewById(R.id.re_startindex);
        re_name.setOnClickListener(new MyListener());
        re_worktype.setOnClickListener(new MyListener());
        re_picsize.setOnClickListener(new MyListener());
        re_deleteall.setOnClickListener(new MyListener());
        re_default.setOnClickListener(new MyListener());
        re_csv.setOnClickListener(new MyListener());
        re_startindex.setOnClickListener(new MyListener());
        
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_worktype = (TextView) this.findViewById(R.id.tv_worktype);
        tv_picsize = (TextView) this.findViewById(R.id.tv_picsize);
        tv_deleteall = (TextView) this.findViewById(R.id.tv_deleteall);
        tv_startindex = (TextView) this.findViewById(R.id.tv_startindex);
    }
    
    private void initValue() {
        curWorkTypeSelected = 0;
        curPicSizeSelected = 0;
        curDeleteAllSelected = 0;
        
        for(String str : workTypeList) {
        	if (str.equals(sd.getWorkType())) {
        		break;
        	}
        	curWorkTypeSelected++;
        }
        
        for(String str : picSizeList) {
        	if (str.equals(sd.getPicSize())) {
        		break;
        	}
        	curPicSizeSelected++;
        }
        
        for(String str : yesornoList) {
        	if (str.equals(sd.getAllowAllDelete())) {
        		break;
        	}
        	curDeleteAllSelected++;
        }

//        tv_name.setText(sd.getPersonName());
        tv_name.setText(sp.getString("USER_NAME", ""));
        tv_worktype.setText(sd.getWorkType());
        tv_deleteall.setText(sd.getAllowAllDelete());
        tv_picsize.setText(sd.getPicSize());
        tv_startindex.setText(""+sd.getStartIndex());
    }
    
    private void setToDefault() {
        sd.toDefault();
        
        initValue();
        
		Toast.makeText(SettingActivity.this, "已恢复到默认值",Toast.LENGTH_SHORT).show();
    }
    
    private void createCsvFile() {
    	String result = DwpcApplication.getInstance().createCsvFile();
    	if ("".equals(result)) {
    		Toast.makeText(SettingActivity.this, "CSV文件生成成功",Toast.LENGTH_SHORT).show();
    	} else {
    		if (null == result) {
    			result = "无内容";
    		} 
			showAlert(result);
    		Toast.makeText(SettingActivity.this, "CSV文件生成失败，请重试",Toast.LENGTH_SHORT).show();
    	}
    }

    private void showAlert(String content) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.alert_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("错误");
        
        TextView tv_content = (TextView) window.findViewById(R.id.tv_content);
        tv_content.setText(content);

        TextView ll_doing_ok = (TextView) window.findViewById(R.id.ll_doing_ok);
        ll_doing_ok.setVisibility(View.GONE);

        TextView ll_doing_ng = (TextView) window.findViewById(R.id.ll_doing_ng);
        ll_doing_ng.setText("确认");
        ll_doing_ng.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	dlg.dismiss();
            }
        });
    }
	
    class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.re_name:
                Intent iN = new Intent(SettingActivity.this,
                        UpdateNameActivity.class);
                iN.putExtra("OriName", sd.getPersonName());

                startActivityForResult(iN, UPDATE_NAME);
                break;
            case R.id.re_worktype:
                showWorkTypeDialog();
                break;
            case R.id.re_picsize:
                showPicSizeDialog();
                break;
            case R.id.re_startindex:
            	showCodeDialog();
                break;
            case R.id.re_deleteall:
                showDeleteAllDialog();
                break;
            case R.id.re_default:
            	setToDefault();
                break;
            case R.id.re_csv:
            	createCsvFile();
                break;
            }
        }
    }

    private void showWorkTypeDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.list_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("请选择");
        
        final ListView list = (ListView) window.findViewById(R.id.list);
                
        ListDialogAdapter adapter = new ListDialogAdapter(this, workTypeList, curWorkTypeSelected);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	curWorkTypeSelected = position;
                sd.setWorkType(workTypeList.get(position));
                tv_worktype.setText(sd.getWorkType());
                dlg.cancel();
            }            
        });
    }

    private void showPicSizeDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.list_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("请选择");
        
        final ListView list = (ListView) window.findViewById(R.id.list);
                
        ListDialogAdapter adapter = new ListDialogAdapter(this, picSizeList, curPicSizeSelected);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	curPicSizeSelected = position;
                sd.setPicSize(picSizeList.get(position));
                tv_picsize.setText(sd.getPicSize());
                dlg.cancel();
            }            
        });
    }

    private void showCodeDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.code_dialog, null);
        dlg.setView(layout);
        dlg.show();
        
        Window view = dlg.getWindow();
        view.setContentView(R.layout.code_dialog);
        
        LinearLayout ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("请输入坐标起始序号");
        
        final EditText et_content = (EditText) view.findViewById(R.id.et_content);
        
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {            
            public void run() {  
                InputMethodManager inputManager = (InputMethodManager) 
                		et_content.getContext().getSystemService(CatagoryOneActivity.INPUT_METHOD_SERVICE);  
                inputManager.showSoftInput(et_content, 0);  
            }  
        }, 500);  
        
        LinearLayout ll_doing = (LinearLayout) view.findViewById(R.id.ll_doing);
        ll_doing.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	String code = et_content.getText().toString().trim();
                if(code == null || code.equals("")) {
                    return;
                }  
                
                sd.setStartIndex(Integer.valueOf(code));
                tv_startindex.setText(""+sd.getStartIndex());
        		Toast.makeText(SettingActivity.this, "本设置只针对新建作业的坐标起始序号，已建作业的坐标序号不变。",Toast.LENGTH_SHORT).show();
                
                dlg.cancel();
            }
        });
    }
    private void showDeleteAllDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.list_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("请选择");
        
        final ListView list = (ListView) window.findViewById(R.id.list);
        
        ListDialogAdapter adapter = new ListDialogAdapter(this, yesornoList, curDeleteAllSelected);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	curDeleteAllSelected = position;
                sd.setAllowAllDelete(yesornoList.get(position));
                tv_deleteall.setText(sd.getAllowAllDelete());
                dlg.cancel();
            }            
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case UPDATE_NAME:
                if (data != null && data.hasExtra("NewName")) {
                    String name = (String) data.getSerializableExtra("NewName");
                    sd.setPersonName(name);
//                    tv_name.setText(sd.getPersonName());
                    tv_name.setText(sp.getString("USER_NAME", ""));
                } 
                break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    public void back(View view) {
        finish();
    }
}
