package com.example.mdxj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdxj.DwpcApplication;
import com.example.mdxj.R;
import com.example.mdxj.adapter.CatagoryTwoAdapter;
import com.example.mdxj.adapter.ListDialogAdapter;
import com.example.mdxj.location.GpsLocation;
import com.example.mdxj.model.CatagoryOne;
import com.example.mdxj.model.CatagoryThree;
import com.example.mdxj.model.CatagoryTwo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CatagoryTwoActivity extends Activity {
    private RelativeLayout re_operation;
    private TextView tv_title;
    private ListView listView;
    private CatagoryTwoAdapter adapter;
    private RelativeLayout re_selectall;
    private RelativeLayout re_selectall_cancel;
    private TextView tv_selectall;
    private ImageView iv_operation;
    private PopupWindow popInfo = null;
    private List<CatagoryTwo> cgList = new ArrayList<CatagoryTwo>();
    private CatagoryTwo curCg = null;
    private CatagoryOne parent = null;

    private static final int REQUEST_CATAGORY_THREE = 1;
    private static final int REQUEST_CATAGORY_THREE_EDIT = 2;
    private ImageView iv_map;
    private Timer gpsStatusTimer = null;
    private GpsLocation mCGL = new GpsLocation(this);
    private boolean mPositionFlag = false;
    public static final int FlASH_GPSSTATUS = 100;
    public static final int UNFlASH_GPSSTATUS = 101;
    private String voltage;
    private void startGps() {
        if (!mPositionFlag) {
            if (!mCGL.isGpsOpen()) {
                Toast.makeText(CatagoryTwoActivity.this, "请开启GPS定位功能", Toast.LENGTH_SHORT).show();
            }

            mPositionFlag = true;

        }
    }
    public void updateLocation() {
        if (!mCGL.isAllowedArea()) {
            Toast.makeText(CatagoryTwoActivity.this, "已超出授权的作业范围", Toast.LENGTH_SHORT).show();
            return;
        }

        DecimalFormat df = new DecimalFormat("##0.000000");
        DecimalFormat df1 = new DecimalFormat("##0.00");

        for (CatagoryTwo ct : cgList) {
            if (null == ct.getLat() || "".equals(ct.getLat())) {
                ct.setLat(df.format(mCGL.getLatitude()));
                ct.setLng(df.format(mCGL.getLongitude()));
                ct.setAlt(df1.format(mCGL.getAltitude()));
                ct.save();
            }

            ArrayList<CatagoryThree> childList = ct.getChildList();
            for (CatagoryThree c : childList) {
                if (null == c.getLat() || "".equals(c.getLat())) {
                    c.setLat(df.format(mCGL.getLatitude()));
                    c.setLng(df.format(mCGL.getLongitude()));
                    c.setAlt(df1.format(mCGL.getAltitude()));
                    c.save();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void unflashGpsStatus() {
        if (gpsStatusTimer != null) {
            gpsStatusTimer.cancel();
        }
        gpsStatusTimer = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctwo);
        Intent intent = getIntent();
        if (intent.hasExtra("CatagoryOne")) {
            parent = (CatagoryOne) intent.getSerializableExtra("CatagoryOne");
        }
        voltage = intent.getStringExtra("voltage");
        getList();

        initView();
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("线路 (" + parent.getType() + ")");

        listView = (ListView) findViewById(R.id.list);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        iv_map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatagoryTwoActivity.this, LocationModeSourceActivity_Old.class);

                startActivity(intent);
            }
        });
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
                    CatagoryTwo cg = cgList.get(position);
                    setCurCG(cg);

                    Intent intent = new Intent(CatagoryTwoActivity.this, CatagoryThreeActivity.class);
                    intent.putExtra("CatagoryTwo", cg);
                    startActivityForResult(intent, REQUEST_CATAGORY_THREE_EDIT);
                }
            }
        });

        adapter = new CatagoryTwoAdapter(this, cgList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (CatagoryTwo ct : cgList) {
            if (null == ct.getLat() || "".equals(ct.getLat())) {
                startGps();
                return;
            }

            ArrayList<CatagoryThree> childList = ct.getChildList();
            for (CatagoryThree c : childList) {
                if (null == c.getLat() || "".equals(c.getLat())) {
                    startGps();
                    return;
                }
            }
        }
    }

    private void initView() {
        re_operation = (RelativeLayout) this.findViewById(R.id.re_operation);
        re_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOperation();
            }
        });

        re_selectall = (RelativeLayout) this.findViewById(R.id.re_selectall);
        re_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearSelectItem();
                if ("全选".equals(tv_selectall.getText().toString().trim())) {
                    int pos = 0;
                    for (CatagoryTwo c : cgList) {
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
        re_selectall_cancel.setOnClickListener(new View.OnClickListener() {
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
        cgList = parent.getChildList();
    }

    public void setCurCG(CatagoryTwo cg) {
        curCg = cg;
    }

    private void doOperation() {
        if (adapter.isSelecting()) {
            if (adapter.getSelectCount() == 0) {
                Toast.makeText(CatagoryTwoActivity.this, "未选择删除项", Toast.LENGTH_SHORT).show();
                return;
            }

            showPopInfo();
        } else {
//            if ("低压".equals(voltage)) {
            showDiyaDialog();
//            } else {
//                showGaoyaDialog();
//            }
        }
    }

    private void doDelete() {
        List<CatagoryTwo> cgListDeleted = new ArrayList<CatagoryTwo>();

        for (int i = 0; i < adapter.getSelectCount(); i++) {
            int sel = adapter.getSelectItem(i);
            cgListDeleted.add(cgList.get(sel));
        }

        for (CatagoryTwo c : cgListDeleted) {
            c.delete();
            cgList.remove(c);
        }

        cancelSelectAll();
        popInfo.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CATAGORY_THREE:
                    if (data != null && data.hasExtra("CatagoryTwo")) {
                        CatagoryTwo cg = (CatagoryTwo) data.getSerializableExtra("CatagoryTwo");

                        ArrayList<CatagoryThree> cldList = cg.getChildList();
                        curCg.setCurChildCode(cg.getCurChildCode());

                        curCg.getChildList().clear();
                        for (CatagoryThree c3 : cldList) {
                            curCg.addChild(c3);
                        }

                        if (cldList.size() > 0) {
                            String date = cldList.get(cldList.size() - 1).getUpdateTime();
                            curCg.setUpdateTime(date);
                            curCg.setLat(cldList.get(0).getLat());
                            curCg.setLng(cldList.get(0).getLng());
                        } else {
                            curCg.setUpdateTime("");
                            curCg.setLat("");
                            curCg.setLng("");
                        }

                        cgList.add(curCg);
                        adapter.notifyDataSetChanged();

                        curCg.save();
                        parent.save();
                    }
                    break;
                case REQUEST_CATAGORY_THREE_EDIT:
                    if (data != null && data.hasExtra("CatagoryTwo")) {
                        CatagoryTwo cg = (CatagoryTwo) data.getSerializableExtra("CatagoryTwo");

                        ArrayList<CatagoryThree> cldList = cg.getChildList();
                        curCg.setCurChildCode(cg.getCurChildCode());

                        curCg.getChildList().clear();
                        for (CatagoryThree c3 : cldList) {
                            curCg.addChild(c3);
                        }

                        if (cldList.size() > 0) {
                            String date = cldList.get(cldList.size() - 1).getUpdateTime();
                            curCg.setUpdateTime(date);
                            curCg.setLat(cldList.get(0).getLat());
                            curCg.setLng(cldList.get(0).getLng());
                        } else {
                            curCg.setUpdateTime("");
                            curCg.setLat("");
                            curCg.setLng("");
                        }

                        adapter.notifyDataSetChanged();

                        curCg.save();
                        parent.save();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            switch (requestCode) {
                case REQUEST_CATAGORY_THREE:
                    parent.rollbackChildCode();
                    break;
                case REQUEST_CATAGORY_THREE_EDIT:
                    break;
            }
            curCg = null;
        }
    }

    private void showDiyaDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.list_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("请选择");

        final ListView list = (ListView) window.findViewById(R.id.list);

        final List<String> contentList = new ArrayList<String>();
        contentList.add("杆塔全景");
        contentList.add("标识牌");
        contentList.add("杆塔基础");
        contentList.add("塔头");
        contentList.add("大号通道");
        contentList.add("小号通道");
        contentList.add("其他");
        contentList.add("变压器");
        contentList.add("计量箱");
        contentList.add("电表");
        final List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        Resources res = getResources();
        Bitmap bmp1 = BitmapFactory.decodeResource(res, R.drawable.gtqj1);
        Bitmap bmp2 = BitmapFactory.decodeResource(res, R.drawable.bsp);
        Bitmap bmp3 = BitmapFactory.decodeResource(res, R.drawable.gtjc);
        Bitmap bmp4 = BitmapFactory.decodeResource(res, R.drawable.tt);
        Bitmap bmp5 = BitmapFactory.decodeResource(res, R.drawable.dhtd);
        Bitmap bmp6 = BitmapFactory.decodeResource(res, R.drawable.xhtd);
        Bitmap bmp7 = BitmapFactory.decodeResource(res, R.drawable.qt);
        Bitmap bmp8 = BitmapFactory.decodeResource(res, R.drawable.byq);
        Bitmap bmp9 = BitmapFactory.decodeResource(res, R.drawable.jlx);
        Bitmap bmp10 = BitmapFactory.decodeResource(res, R.drawable.db);
        bitmapList.add(bmp1);
        bitmapList.add(bmp2);
        bitmapList.add(bmp3);
        bitmapList.add(bmp4);
        bitmapList.add(bmp5);
        bitmapList.add(bmp6);
        bitmapList.add(bmp7);
        bitmapList.add(bmp8);
        bitmapList.add(bmp9);
        bitmapList.add(bmp10);
        ListDialogAdapter adapter = new ListDialogAdapter(this, contentList, bitmapList, -1);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View view,
                                    int position, long id) {
                curCg = new CatagoryTwo();
                curCg.setParentId(parent.getId());
                curCg.setCode(parent.nextChildCode());
                curCg.setPersonName(parent.getPersonName());
                curCg.setType(contentList.get(position));
                curCg.setDestPath(parent.getFolderPath());

                Intent intent = new Intent(CatagoryTwoActivity.this, CatagoryThreeActivity.class);
                intent.putExtra("CatagoryTwo", curCg);
                CatagoryTwoActivity.this.startActivityForResult(intent, REQUEST_CATAGORY_THREE);

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

    public void back(View view) {
//		String tost = "正在等待定位，请稍后";
//
//		for (CatagoryTwo ct : cgList) {
//			if (null == ct.getLat() || "".equals(ct.getLat())) {
//				Toast.makeText(CatagoryTwoActivity.this, tost,Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			ArrayList<CatagoryThree> childList = ct.getChildList();
//			for (CatagoryThree c : childList) {
//				if (null == c.getLat() || "".equals(c.getLat())) {
//					Toast.makeText(CatagoryTwoActivity.this, tost,Toast.LENGTH_SHORT).show();
//					return;
//				}
//			}
//		}
//
//
        Intent intent = new Intent();
        intent.putExtra("CatagoryOne", parent);
        setResult(RESULT_OK, intent);

        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.putExtra("CatagoryOne", parent);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
