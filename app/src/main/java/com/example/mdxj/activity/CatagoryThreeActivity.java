package com.example.mdxj.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.mdxj.adapter.CatagoryThreeAdapter;
import com.example.mdxj.location.GpsLocation;
import com.example.mdxj.model.CatagoryThree;
import com.example.mdxj.model.CatagoryTwo;
import com.example.mdxj.util.DateUtils;
import com.example.mdxj.util.StorageUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CatagoryThreeActivity extends Activity {
	private RelativeLayout re_operation_photo;
	private RelativeLayout re_operation_save;
	private RelativeLayout re_operation;

	private ListView listView;
	private CatagoryThreeAdapter adapter;

	private RelativeLayout re_selectall;
	private RelativeLayout re_selectall_cancel;
	private TextView tv_selectall;
	private ImageView iv_operation;
	private LinearLayout ll_operation;

	private PopupWindow popInfo = null;
	private PopupWindow popAlert = null;

	private TextView tv_title;

	private List<CatagoryThree> cgList = new ArrayList<CatagoryThree>();
	private CatagoryThree curCT = null;
	private CatagoryTwo parent = null;
	private boolean isDeleting = false;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照

	private String latitude = null;
	private String longitude = null;
	private String altitude = null;
	private double accuracy = 0;
	private int satiCount = 0;

	private Timer gpsStatusTimer = null;
	private RelativeLayout re_gpsinfo;
	private TextView tv_gpsinfo;
	private RelativeLayout re_gps;
	private ImageView iv_gps;
	private final Handler mHandler = new GpsHandler();
	private GpsLocation mCGL = new GpsLocation(this);
	private boolean mPositionFlag = false;
	public static final int FlASH_GPSSTATUS = 100;
	public static final int UNFlASH_GPSSTATUS = 101;
	public class GpsHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case GpsLocation.GPS_SUCCESS:
				{
					updateLocation();
					break;
				}
				case GpsLocation.GPS_STOP:
				{
					mPositionFlag = false;
					break;
				}
				case FlASH_GPSSTATUS: {
					iv_gps.setBackgroundResource(R.drawable.bn_gps_blue);
					break;
				}
				case UNFlASH_GPSSTATUS:{
					iv_gps.setBackgroundResource(R.drawable.wn_gps);
					break;
				}
				default:
					break;
			}
			return;
		}
	}

	private void startGps() {
		if (!mPositionFlag) {
			if (!mCGL.isGpsOpen()) {
				Toast.makeText(CatagoryThreeActivity.this,"请开启GPS定位功能",Toast.LENGTH_SHORT).show();
			}

			mPositionFlag = true;
			if (mCGL.startLoaction()) {
				flashGpsStatus();
				tv_gpsinfo.setText("正在定位...");
			} else {
				mPositionFlag = false;
				tv_gpsinfo.setText("启动定位失败");
			}
		}
	}

	public void stopGps() {
		if (mPositionFlag) {
			mCGL.stopLocation();
			mPositionFlag = false;
			unflashGpsStatus();
		}
	}

	private void flashGpsStatus() {
		gpsStatusTimer = new Timer();
		gpsStatusTimer.schedule(new TimerTask() {
			private boolean first = true;
			public void run() {
				if (first) {
					mHandler.sendEmptyMessage(FlASH_GPSSTATUS);
					first = false;
				} else {
					mHandler.sendEmptyMessage(UNFlASH_GPSSTATUS);
					first = true;
				}
			}
		}, 500, 500);
	}


	private void unflashGpsStatus() {
		if (gpsStatusTimer != null) {
			gpsStatusTimer.cancel();
		}
		gpsStatusTimer = null;
	}

	private void updateLocation() {
		if (!mCGL.isAllowedArea()) {
			Toast.makeText(CatagoryThreeActivity.this,"已超出授权的作业范围",Toast.LENGTH_SHORT).show();
			return;
		}

		DecimalFormat df = new DecimalFormat("##0.000000");
		DecimalFormat df1 = new DecimalFormat("##0");
		DecimalFormat df2 = new DecimalFormat("##0.00");

		latitude = df.format(mCGL.getLatitude());
		longitude = df.format(mCGL.getLongitude());
		altitude = df2.format(mCGL.getAltitude());
		accuracy = mCGL.getAccuracy();
		satiCount = mCGL.getSatelliteCount();

		tv_gpsinfo.setText("经度:" + longitude + " 纬度:" + latitude +
				"\n精度:" + df1.format(accuracy) + "米  卫星数:" + satiCount + "颗");

		iv_gps.setBackgroundResource(R.drawable.bn_gps_blue);

		for (CatagoryThree c : cgList) {
			if (c.getLat() == null || "".equals(c.getLat())) {
				c.setLat(latitude);
				c.setLng(longitude);
				c.setAlt(altitude);
			}
		}
		adapter.notifyDataSetChanged();
		unflashGpsStatus();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cthree);

		mCGL.setMh(mHandler);

		Intent intent = getIntent();
		if (intent.hasExtra("CatagoryTwo")) {
			parent = (CatagoryTwo)intent.getSerializableExtra("CatagoryTwo");
		}

		getList();

		initView();
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("(" + parent.getType() + ")");

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setSelecting(true);
				adapter.changeSelectItem(position);
				adapter.notifyDataSetChanged();

				if ("是".equals(DwpcApplication.getInstance().getSettingData().getAllowAllDelete())) {
					re_selectall.setVisibility(View.VISIBLE);
				}
				re_selectall_cancel.setVisibility(View.VISIBLE);
				iv_operation.setVisibility(View.VISIBLE);
				ll_operation.setVisibility(View.GONE);

				return true;
			}});
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (adapter.isSelecting()) {
					adapter.changeSelectItem(position);
					adapter.notifyDataSetChanged();
				} else {
					ArrayList<String> uriList = new ArrayList<String>();
					int cur = 0;

					Intent intent = new Intent(CatagoryThreeActivity.this, ImagePagerActivity.class);

					for (int i = 0; i < cgList.size(); i ++) {
						CatagoryThree af = cgList.get(i);

						if (i == position) {
							cur = uriList.size();
						}

						uriList.add(af.getOriFilePath());
					}
					intent.putStringArrayListExtra("uriList", uriList);
					intent.putExtra("current", cur);

					CatagoryThreeActivity.this.startActivity(intent);
				}
			}});

		adapter = new CatagoryThreeAdapter(this, cgList);
		listView.setAdapter(adapter);

		startGps();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		ll_operation = (LinearLayout) this.findViewById(R.id.ll_operation);

		re_operation = (RelativeLayout) this.findViewById(R.id.re_operation);
		re_operation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doOperation();
			}
		});

		re_operation_photo = (RelativeLayout) this.findViewById(R.id.re_operation_photo);
		re_operation_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//在Android 6.0 以后的系统中授权管理已经，动态加入打开照相权限。
				if (Build.VERSION.SDK_INT >= 23) {
					int checkCallPhonePermission = ContextCompat.checkSelfPermission(CatagoryThreeActivity.this, Manifest.permission.CAMERA);
					if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
						ActivityCompat.requestPermissions(CatagoryThreeActivity.this,new String[]{Manifest.permission.CAMERA},222);
						return;
					}else{

						doPhoto();//调用具体方法
					}
				} else {

					doPhoto();//调用具体方法
				}

			}
		});

		re_operation_save = (RelativeLayout) this.findViewById(R.id.re_operation_save);
		re_operation_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doSave();
			}
		});

		re_selectall = (RelativeLayout) this.findViewById(R.id.re_selectall);
		re_selectall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.clearSelectItem();
				if ("全选".equals(tv_selectall.getText().toString().trim())) {
					int pos = 0;
					for (CatagoryThree c : cgList) {
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

		ll_operation.setVisibility(View.VISIBLE);
		iv_operation = (ImageView) this.findViewById(R.id.iv_operation);

		iv_gps = (ImageView) this.findViewById(R.id.iv_gps);

		re_gpsinfo = (RelativeLayout) this.findViewById(R.id.re_gpsinfo);
		tv_gpsinfo = (TextView) findViewById(R.id.tv_gpsinfo);
	}

	private void cancelSelectAll() {
		adapter.clearSelectItem();
		adapter.setSelecting(false);
		adapter.notifyDataSetChanged();

		re_selectall.setVisibility(View.GONE);
		re_selectall_cancel.setVisibility(View.GONE);
		iv_operation.setVisibility(View.GONE);
		ll_operation.setVisibility(View.VISIBLE);
	}

	private void getList() {
		cgList = parent.getChildList();
	}

	private void doOperation() {
		if (adapter.isSelecting()) {
			if (adapter.getSelectCount() == 0) {
				Toast.makeText(CatagoryThreeActivity.this,"未选择删除项",Toast.LENGTH_SHORT).show();
				return;
			}

			showPopInfo();
		}
	}

	private void doPhoto() {
		try {
			if (!StorageUtil.isExternalMemoryAvailable()) {
				Toast.makeText(this, "没有存储卡", Toast.LENGTH_LONG).show();
				return;
			}

			curCT = new CatagoryThree();
			curCT.setIsSaved(false);
			curCT.setParentId(parent.getId());
			curCT.setCode(parent.nextChildCode());
			curCT.setParentCode(parent.getCode());
			curCT.setPersonName(parent.getPersonName());
			curCT.setType(parent.getType());
			curCT.setDestPath(parent.getDestPath());

			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

			File localFile = StorageUtil.getOutputMediaFile(StorageUtil.MEDIA_TYPE_IMAGE);

			Uri uri = Uri.fromFile(localFile);

			curCT.setOriFilePath(localFile.getPath());

			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			this.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);

		} catch (ActivityNotFoundException e) {
			parent.rollbackChildCode();
			e.printStackTrace();
		}
	}

	private void doSave() {
		stopGps();

		isDeleting = false;

		save();

		Intent intent = new Intent();
		intent.putExtra("CatagoryTwo", parent);
		setResult(RESULT_OK, intent);

		finish();
	}

	private void doDelete() {
		List<CatagoryThree> cgListDeleted = new ArrayList<CatagoryThree>();

		for (int i = 0; i < adapter.getSelectCount(); i++) {
			int sel = adapter.getSelectItem(i);
			cgListDeleted.add(cgList.get(sel));
		}

		for (CatagoryThree c : cgListDeleted) {
			c.delete();
			cgList.remove(c);
		}
		isDeleting = true;

		cancelSelectAll();
		popInfo.dismiss();
	}

	private void save() {
		for(CatagoryThree c3 : cgList) {
			c3.save();
		}
	}
//长按弹出底部删除数据
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
		ll_content1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				doDelete();
			}
		});

		LinearLayout ll_content2 = (LinearLayout) popInfoView.findViewById(R.id.ll_content2);
		ll_content2.setOnClickListener(new OnClickListener(){
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
		ll_doing_ok.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				for (CatagoryThree c : cgList) {
					if (!c.getIsSaved()) {
						c.delete();
					}
				}

				dlg.dismiss();
				stopGps();
				finish();
			}
		});

		TextView ll_doing_ng = (TextView) window.findViewById(R.id.ll_doing_ng);
		ll_doing_ng.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PHOTO_REQUEST_TAKEPHOTO:
					String imagepath = curCT.getOriFilePath();

					int width = DwpcApplication.getInstance().getSettingData().getPicWidth();
					int height = DwpcApplication.getInstance().getSettingData().getPicHeight();
					Bitmap photo = null;
					if (width != -1) {
						photo = StorageUtil.convertToBitmapNew(imagepath, width, height);
						StorageUtil.saveBitmap(imagepath, photo);
					}
					photo = StorageUtil.convertToBitmap(imagepath, 200, 200);
					photo = StorageUtil.centerSquareScaleBitmap(photo, 120);

					String thumbImageName= imagepath.substring(imagepath.lastIndexOf("/") + 1, imagepath.length());

					String thumbnailPath = ((Context)this).getFilesDir().getPath()+"/"+ "thb_"+thumbImageName;
					thumbnailPath = StorageUtil.saveBitmap(thumbnailPath, photo);
					curCT.setThumbnailBigPath(thumbnailPath);
					Bitmap photoS = StorageUtil.convertToBitmap(imagepath, 32, 32);
					photoS = StorageUtil.centerSquareScaleBitmap(photo, 24);

					String thumbnailPathS = ((Context)this).getFilesDir().getPath()+"/"+ "ths_"+thumbImageName;
					thumbnailPathS = StorageUtil.saveBitmap(thumbnailPathS, photoS);

					curCT.setThumbnailSmallPath(thumbnailPathS);

					curCT.setUpdateTime(DateUtils.getCurrentTime());

					curCT.setLat(latitude);
					curCT.setLng(longitude);
					curCT.setAlt(altitude);

					cgList.add(curCT);
					adapter.notifyDataSetChanged();

					break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			parent.rollbackChildCode();
			curCT = null;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			//就像onActivityResult一样这个地方就是判断你是从哪来的。
			case 222:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
				 doPhoto();
				} else {
					// Permission Denied
					Toast.makeText(CatagoryThreeActivity.this, "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}


	public void back(View view) {
		boolean isUnSaved = isDeleting;

		for (CatagoryThree c : cgList) {
			if (!c.getIsSaved()) {
				isUnSaved = true;
				break;
			}
		}

		if (!isUnSaved) {
			stopGps();
			finish();
		} else {
			showAlert("数据还未保存，是否继续退出？");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			back(null);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
