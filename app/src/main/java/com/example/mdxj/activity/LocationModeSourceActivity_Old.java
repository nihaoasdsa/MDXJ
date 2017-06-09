package com.example.mdxj.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.example.mdxj.R;
import com.example.mdxj.jsonbean.ViewInfo;
import java.util.ArrayList;

/**
 * AMapV2地图中介绍使用active deactive进行定位
 */
public class LocationModeSourceActivity_Old extends Activity implements LocationSource,
		AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private TextView mLocationErrText;
	private ArrayList<ViewInfo> viewInfos = new ArrayList<ViewInfo>();
	private Bitmap start_bitmap,end_bitmap;//开始和结束为止的图片
	private ImageView iv_lm_back;
	Bitmap bm1,bm2;
	Canvas canvas = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.locationmodesource_old_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		mLocationErrText = (TextView)findViewById(R.id.location_errInfo_text);
		iv_lm_back= (ImageView) findViewById(R.id.iv_lm_back);
		iv_lm_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		mLocationErrText.setVisibility(View.GONE);
		initViewInfo();
	}
	//载入标记数据
	private void initViewInfo() {
		viewInfos.clear();
		viewInfos.add(new ViewInfo("东华门", (float)39.91356,(float)116.39544));
		viewInfos.add(new ViewInfo("角楼", (float)39.91238602183407,(float)116.39542829017292));
		viewInfos.add(new ViewInfo("午门", (float)39.91188,(float)116.39103));
		viewInfos.add(new ViewInfo("太和门广场", (float)39.91315,(float)116.39095));
		viewInfos.add(new ViewInfo("熙和门", (float)39.91312,(float)116.38954));
		viewInfos.add(new ViewInfo("武英殿", (float)39.91402,(float)116.38815));
		viewInfos.add(new ViewInfo("文华殿", (float)39.91381,(float)116.39320));
		viewInfos.add(new ViewInfo("太和门", (float)39.91368,(float)116.39097));
		viewInfos.add(new ViewInfo("太和殿广场",(float)39.91459,(float)116.39089));
		viewInfos.add(new ViewInfo("弘义阁", (float)39.91478,(float)116.38959));


	}
	private void moveToForbiddenCity() {
		if(viewInfos.size() > 0)
		{
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(viewInfos.get(0).getLat(),viewInfos.get(0).getLng()), 15.0f);
			aMap.moveCamera(cu);
		}
	}

	private void addPolyline(ArrayList<ViewInfo> list) {
		PolylineOptions options = new PolylineOptions().color(Color.RED).width(8);
		for (ViewInfo info : list) {
			options.add(new LatLng(info.getLat(), info.getLng()));

		}
		aMap.addPolyline(options);
	}

	private void addmarker() {
		//设置marker的坐标初始末的图片
		start_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start);
		end_bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.end);
		int i = 0;
		int len = viewInfos.size();

		for (ViewInfo info : viewInfos) {
			MarkerOptions mo = new MarkerOptions();
			mo.position(new LatLng(info.getLat(), info.getLng()));
			mo.draggable(true);
			mo.title(info.getName());
			mo.icon(BitmapDescriptorFactory.defaultMarker());
			bm1 = start_bitmap.copy(Bitmap.Config.ARGB_8888, true);
			bm2=end_bitmap.copy(Bitmap.Config.ARGB_8888, true);
			//在图片上加入自定义字
//			canvas = new Canvas(bm1);
//			Paint paint = new Paint();
//			paint.setColor(Color.WHITE);
//			paint.setTextSize(35);
			if (i == 0) {
			//	canvas.drawText("起", 40, 80, paint);
				mo.icon(BitmapDescriptorFactory.fromBitmap(bm1));
			} else if (i == (len - 1)) {
			//	canvas.drawText("终",40, 80, paint);
				mo.icon(BitmapDescriptorFactory.fromBitmap(bm2));
			} else {
				//给所有的经纬度点设置自定义图片
			//	canvas.drawText("" + i, 18, 42, paint);
				//mo.icon(BitmapDescriptorFactory.fromBitmap(bm1));
			}
			aMap.addMarker(mo);
			i++;
		}
	}
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		moveToForbiddenCity();
		addmarker();
		addPolyline(viewInfos);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		if(null != mlocationClient){
			mlocationClient.onDestroy();
		}
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
				mLocationErrText.setVisibility(View.GONE);
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				Log.e("AmapErr",amapLocation.getAddress()+"--"+amapLocation.getCityCode());
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
				mLocationErrText.setVisibility(View.VISIBLE);
				mLocationErrText.setText(errText);
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mlocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
		//	mlocationClient.startLocation();
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}


	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		render(marker, infoWindow);
		return infoWindow;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}
	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {

			ImageView imageView = (ImageView) view.findViewById(R.id.badge);
			imageView.setImageResource(R.drawable.badge_wa);
		String title = marker.getTitle();
      Log.e("11111","---"+marker.getIcons());
		ArrayList<BitmapDescriptor> img=marker.getIcons();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(15);
			titleUi.setText(titleText);


		} else {
			titleUi.setText("");
		}
	}
}