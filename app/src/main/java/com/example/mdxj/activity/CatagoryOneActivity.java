package com.example.mdxj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdxj.DwpcApplication;
import com.example.mdxj.R;
import com.example.mdxj.adapter.CatagoryOneAdapter;
import com.example.mdxj.location.GpsLocation;
import com.example.mdxj.model.CatagoryOne;
import com.example.mdxj.model.CatagoryTwo;
import com.example.mdxj.util.DateUtils;
import com.example.mdxj.util.StorageUtil;
import com.example.mdxj.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CatagoryOneActivity extends Activity {
    private RelativeLayout re_operation;
    private TextView tv_title;

    private RelativeLayout re_selectall;
    private RelativeLayout re_selectall_cancel;
    private TextView tv_selectall;
    private ImageView iv_operation;

    private PopupWindow popInfo = null;

    private ListView listView;
    private CatagoryOneAdapter adapter;

    private List<CatagoryOne> cgList = null;
    private List<CatagoryOne> cgList2 = new ArrayList<>();
    private CatagoryOne curCg = null;
    private ImageView iv_back;
    private static final int REQUEST_CATAGORY_TWO = 1;
    private Intent intent;
    private String voltage;
    private int ID;
    private ProgressBar progressBar;
    private CustomProgressDialog dialog;
    private ImageView iv_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cone);
        showmeidialog();
//        progressBar = (ProgressBar) findViewById(R.id.site_pb_load);
        Timer timer = new Timer(true);
        timer.schedule(task, 1000); //第二个参数为延迟时间，1000为1s

    }

    /**
     * 显示美团进度对话框，此对话框
     */
    public void showmeidialog() {
        dialog = new CustomProgressDialog(this, "正在加载中", R.drawable.frame);
        dialog.show();
    }

    //  实现run()方法，发送一条Message给Handler
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendMessage(mHandler.obtainMessage());
        }
    };
    //handleMessage()方法，用于接收Message，刷新UI，加载ListView
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            init();
        }
    };

    private void init() {
        intent = getIntent();
        voltage = intent.getStringExtra("voltage");
        ID=intent.getExtras().getInt("ID");
        if ("2018".equals(DateUtils.getCurrentYear())) {
            finish();
            return;
        }

        GpsLocation gps = new GpsLocation(this);
        if (!gps.isGpsOpen()) {
            showAlert("GPS未开启，是否马上设置？");
        }

        getList();

        initView();
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        iv_map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatagoryOneActivity.this, LocationModeSourceActivity_Old.class);

            startActivity(intent);
            }
        });

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelecting(true);
                adapter.changeSelectItem(position);
                adapter.notifyDataSetChanged();
                if ("是".equals(DwpcApplication.getInstance().getSettingData().getAllowAllDelete())) {
                    re_selectall.setVisibility(View.VISIBLE);
                }
                re_selectall_cancel.setVisibility(View.VISIBLE);
                iv_operation.setBackgroundResource(R.drawable.icon_delete);
                return true;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.isSelecting()) {
                    adapter.changeSelectItem(position);
                    adapter.notifyDataSetChanged();
                } else {
                    CatagoryOne cg = cgList.get(position);
                    setCurCG(cg);
                    Intent intent = new Intent(CatagoryOneActivity.this, CatagoryTwoActivity.class);
                    intent.putExtra("CatagoryOne", cg);
                    startActivityForResult(intent, REQUEST_CATAGORY_TWO);
                }
            }
        });
//        //根据低、中、高压获取数据库数据
//        for (int i = 0; i < cgList.size(); i++) {
//            String m = cgList.get(i).getName();
//            String name = m.substring(0, 2);
//            String code = m.substring(m.lastIndexOf("_") + 1);
//            String time=cgList.get(i).getDate();
//            List<CatagoryTwo> c2List = DwpcApplication.getInstance().getDb().findAllByWhere(CatagoryTwo.class, strWhere);
//            c1.setChildList((ArrayList<CatagoryTwo>)c2List);
//            Log.e("qqqqq", m + "=====");
//            if (voltage.equals(name)) {
//                CatagoryOne co = new CatagoryOne();
//                co.setType(name);
//                co.setCode(code);
//                co.setDate(time);
//                cgList2.add(co);
//
//            }
//        }
        if(cgList!=null)
        adapter = new CatagoryOneAdapter(this, cgList);
        listView.setAdapter(adapter);
        dialog.dismiss();
//        progressBar.setVisibility(View.GONE);
    }

    private void initView() {

        re_operation = (RelativeLayout) this.findViewById(R.id.re_operation);
        re_operation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doOperation();
            }
        });

        re_selectall = (RelativeLayout) this.findViewById(R.id.re_selectall);
        re_selectall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearSelectItem();
                if ("全选".equals(tv_selectall.getText().toString().trim())) {
                    int pos = 0;
                    for (CatagoryOne c : cgList) {
                        adapter.addSelectItem(pos++);
                    }
                    tv_selectall.setText("全不选");
                } else {
                    tv_selectall.setText("全选");
                }
                adapter.notifyDataSetChanged();
            }
        });
        re_selectall_cancel = (RelativeLayout) this.findViewById(R.id.re_selectall_cancel);
        re_selectall_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSelectAll();
            }
        });
        tv_selectall = (TextView) findViewById(R.id.tv_selectall);
        tv_selectall.setText("全选");

        iv_operation = (ImageView) this.findViewById(R.id.iv_operation);
    }


    private void cancelSelectAll() {
        adapter.clearSelectItem();
        adapter.setSelecting(false);
        adapter.notifyDataSetChanged();

        re_selectall.setVisibility(View.GONE);
        re_selectall_cancel.setVisibility(View.GONE);
        iv_operation.setBackgroundResource(R.drawable.operation_add);
    }

    private void getList() {
        DwpcApplication.getInstance().initData(voltage);
        cgList = DwpcApplication.getInstance().getCatagoryOneList();
//        Log.e("数据","---"+cgList.size());
//        Log.e("数据","---"+cgList.get(0).getName());

    }

    public void setCurCG(CatagoryOne cg) {
        curCg = cg;
    }

    private void doOperation() {
        if (adapter.isSelecting()) {
            if (adapter.getSelectCount() == 0) {
                Toast.makeText(CatagoryOneActivity.this, "未选择删除项", Toast.LENGTH_SHORT).show();
                return;
            }

            showPopInfo();
        } else {
            SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//            String name = DwpcApplication.getInstance().getSettingData().getPersonName();
            String name = sp.getString("USER_NAME", "");
            if (null == name || "".equals(name)) {
                Toast.makeText(CatagoryOneActivity.this, "姓名未输入", Toast.LENGTH_SHORT).show();
                return;
            }

            showCodeDialog();
        }
    }

    private void doDelete() {
        List<CatagoryOne> cgListDeleted = new ArrayList<CatagoryOne>();

        for (int i = 0; i < adapter.getSelectCount(); i++) {
            int sel = adapter.getSelectItem(i);
            cgListDeleted.add(cgList.get(sel));
        }

        for (CatagoryOne c : cgListDeleted) {
            c.delete();
            cgList.remove(c);
        }

        cancelSelectAll();
        popInfo.dismiss();
    }

//    @Override
//    protected void onResume() {
////        tv_title.setText("当前作业 (" + DwpcApplication.getInstance().getSettingData().getWorkType() + ")");
//        tv_title.setText("当前作业 (" + voltage + ")");
//        super.onResume();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CATAGORY_TWO:
                    if (data != null && data.hasExtra("CatagoryOne")) {
                        CatagoryOne cg = (CatagoryOne) data.getSerializableExtra("CatagoryOne");
                        curCg.setCurChildCode(cg.getCurChildCode());
                        ArrayList<CatagoryTwo> cldList = cg.getChildList();
                        curCg.getChildList().clear();
                        for (CatagoryTwo c2 : cldList) {
                            curCg.addChild(c2);
                        }

                        adapter.notifyDataSetChanged();
                        curCg.save();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            curCg = null;
        }
    }

    private void showCodeDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.code_dialog, null);
        dlg.setView(layout);
        dlg.show();

        Window view = dlg.getWindow();
        view.setContentView(R.layout.code_dialog);

        LinearLayout ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("请输入编号");

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
                if (code == null || code.equals("")) {
                    return;
                }
//                if(ID==1){//如果是低压
                curCg = new CatagoryOne();
//                curCg.setType(DwpcApplication.getInstance().getSettingData().getWorkType());
                curCg.setType(voltage);
                curCg.setCode(code);
                curCg.setPersonName(DwpcApplication.getInstance().getSettingData().getPersonName());
                curCg.setDate(DateUtils.getCurrentDate());

                int startIndex = DwpcApplication.getInstance().getSettingData().getStartIndex() - 1;
                if (startIndex < 0) {
                    startIndex = 0;
                }
                curCg.setCurChildCode(startIndex);

                if (StorageUtil.isExistName(curCg.getName())) {
                    Toast.makeText(CatagoryOneActivity.this, "编号已存在或图片文件夹已存在！", Toast.LENGTH_SHORT).show();
                    curCg = null;
                } else {
                    int dirRe = StorageUtil.mkdirs(curCg.getFolderPath());
                    if (dirRe == -1) {
                        Toast.makeText(CatagoryOneActivity.this, "创建图片文件夹失败！", Toast.LENGTH_SHORT).show();
                        curCg = null;
                    } else if (dirRe == 0) {
                        Toast.makeText(CatagoryOneActivity.this, "编号已存在或图片文件夹已存在！", Toast.LENGTH_SHORT).show();
                        curCg = null;
                    } else {

                        cgList.add(curCg);
                        adapter.notifyDataSetChanged();
                        curCg.save();
                    }
                }
//                }


                dlg.cancel();
            }
        });
    }

    private void showPopInfo() {
        if (popInfo != null && popInfo.isShowing()) {
            return;
        }

        View popInfoView = getLayoutInflater().inflate(R.layout.popup_info, null);

        popInfo = new PopupWindow(popInfoView, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, true);
        popInfo.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popInfo.setOutsideTouchable(false);
        popInfo.setAnimationStyle(R.style.AnimBottom);

        popInfo.showAtLocation(listView, Gravity.BOTTOM, 0, 0);

        TextView tv_content1 = (TextView) popInfoView.findViewById(R.id.tv_content1);
        tv_content1.setText("删除" + adapter.getSelectCount() + "项");

        LinearLayout ll_content1 = (LinearLayout) popInfoView.findViewById(R.id.ll_content1);
        ll_content1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete();
            }
        });

        LinearLayout ll_content2 = (LinearLayout) popInfoView.findViewById(R.id.ll_content2);
        ll_content2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popInfo.dismiss();
            }

        });
    }

    private void showAlert(String content) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.alert_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("提示");

        TextView tv_content = (TextView) window.findViewById(R.id.tv_content);
        tv_content.setText(content);

        TextView ll_doing_ok = (TextView) window.findViewById(R.id.ll_doing_ok);
        ll_doing_ok.setText("设置");
        ll_doing_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dlg.dismiss();
            }
        });

        TextView ll_doing_ng = (TextView) window.findViewById(R.id.ll_doing_ng);
        ll_doing_ng.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }


}
