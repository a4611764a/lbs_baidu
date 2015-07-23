package com.example.mybaidumap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
//author zb,qq954884146
public class MainActivity extends Activity {
	MapView mMapView = null;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;
	private String address;
	private String city;
	GeoCoder mSearch;
	private EditText conaddress;
	private ListView listView;
	private Button confirm;
	private TextView longitude;
	private TextView latitude;
	private Button search;
	private double latitudeValue, longitudeValue;
	SuggestionSearch mSuggestionSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		 getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		search = (Button) findViewById(R.id.search);
		listView=(ListView) findViewById(R.id.listView);
		/*
		 * address=getIntent().getStringExtra("address");
		 * latitudeValue=Double.parseDouble
		 * (getIntent().getStringExtra("latitude"));
		 * longitudeValue=Double.parseDouble
		 * (getIntent().getStringExtra("longitude"));
		 */
		conaddress = (EditText) findViewById(R.id.conaddress);
		conaddress.setText(address);
		longitude = (TextView) findViewById(R.id.longitude);
		latitude = (TextView) findViewById(R.id.latitude);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mSearch = GeoCoder.newInstance();
		// 设置地图初始Zoom大小
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		// 开启定位图层
		// mBaiduMap.setMyLocationEnabled(true);

		// 定位初始化
		// popWin(new LatLng(latitudeValue, longitudeValue));
		// mSearch.geocode(new GeoCodeOption().city("").address(address));
		LocationClient mLocClient = new LocationClient(this);
		MyLocationListenner myListener = new MyLocationListenner();
		mSuggestionSearch = SuggestionSearch.newInstance();
		mLocClient.registerLocationListener(myListener);
		mSearch.setOnGetGeoCodeResultListener(listener);
		mSuggestionSearch.setOnGetSuggestionResultListener(listener_advice);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		OnMapClickListener listener = new OnMapClickListener() {
			/**
			 * 地图单击事件回调函数
			 * 
			 * @param point
			 *            点击的地理坐标
			 */
			public void onMapClick(LatLng point) {
				LatLng ptCenter = point;
				// 反Geo搜索
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(ptCenter));

				/*
				 * mSearch.geocode(new GeoCodeOption() .city("北京")
				 * .address("海淀区上地十街10号"));
				 */
				/*
				 * GeoPoint ptCenter = new GeoPoint((int) (point.latitude),
				 * (int) (point.longitude)); // 反Geo搜索
				 * mSearch.reverseGeocode(ptCenter);
				 */
				/*
				 * mSearch.geocode(new
				 * GeoCodeOption().city("北京").address("海淀区上地十街10号")); GeoPoint
				 * ptCenter1 = new GeoPoint((int) (point.latitude), (int)
				 * (point.longitude));
				 */
				popWin(point);
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO 自动生成的方法存根
				return false;
			}
		};
		mBaiduMap.setOnMapClickListener(listener);
		/*
		 * conaddress.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence arg0, int arg1, int
		 * arg2, int arg3) { }
		 * 
		 * @Override public void beforeTextChanged(CharSequence arg0, int arg1,
		 * int arg2, int arg3) { }
		 * 
		 * @Override public void afterTextChanged(Editable arg0) {
		 * if(conaddress.getText().toString().trim().length()>3) //
		 * mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(13.0f));
		 * mSearch.geocode(new
		 * GeoCodeOption().city("").address(conaddress.getText().toString())); }
		 * });
		 */
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (conaddress.getText().toString().trim().length() == 0)
					// mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(13.0f));
					Toast.makeText(MainActivity.this, "请输入您要定位的地址",
							Toast.LENGTH_SHORT).show();

				else if (conaddress.getText().toString().trim().length() > 3) {
					mSearch.geocode(new GeoCodeOption().city("").address(
							conaddress.getText().toString()));
					setListView(conaddress.getText().toString()+"");
				} else {
					Toast.makeText(MainActivity.this, "请输入您要定位的详细地址",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mSearch.destroy();
		mSuggestionSearch.destroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			address = location.getAddrStr();
			city=location.getCity();
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				conaddress.setText(location.getAddrStr());
				
				longitude.setText(location.getLongitude() + "");
				latitude.setText(location.getLatitude() + "");
				popWin(ll);
				//建议地址
				setListView(location.getAddrStr());
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	// 地理位置建议监听
	OnGetSuggestionResultListener listener_advice = new OnGetSuggestionResultListener() {  
	    public void onGetSuggestionResult(SuggestionResult res) {  
	        if (res == null || res.getAllSuggestions() == null) {  
	            return;  
	            //未找到相关结果  
	        }  
	List<SuggestionInfo> SuggestionInfos=res.getAllSuggestions();
	String a[]=new String[SuggestionInfos.size()];
	for(int i=0;i<SuggestionInfos.size();i++){
		a[i]=SuggestionInfos.get(i).city+SuggestionInfos.get(i).district+SuggestionInfos.get(i).key;
	}
	listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, 
            android.R.layout.simple_list_item_1, a));
	    }  
	};
	// 地理位置检索监听
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			mBaiduMap.clear();
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有检索到结果
				Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
						Toast.LENGTH_LONG).show();
				mBaiduMap.clear();
			}
			// 获取地理编码结果
			/*
			 * Toast.makeText(MyMap.this, result.getLocation() + "LLL",
			 * Toast.LENGTH_SHORT).show();
			 */
			else {
				LatLng ll = new LatLng(result.getLocation().latitude,
						result.getLocation().longitude);
				popWin(ll);
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
						Toast.LENGTH_LONG).show();
				return;
			}
			mBaiduMap.clear();
			mBaiduMap.addOverlay(new MarkerOptions().position(
					result.getLocation()).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.nav_turn_via_1)));
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
					.getLocation()));
			conaddress.setText(result.getAddress());
			//建议地址
			setListView(result.getAddress());
			Toast.makeText(MainActivity.this, result.getAddress(),
					Toast.LENGTH_LONG).show();

		}
	};

	// 自定义提示图标
	public void popWin(LatLng point) {

		// 定义地图状态 设置中心点，放大倍数
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(15)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.nav_turn_via_1);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.clear();
		mBaiduMap.addOverlay(option);
		longitude.setText(point.longitude + "");
		latitude.setText(point.latitude + "");

		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
	}
	//底部listView显示附件街道信息
	private void setListView(String string) {
		if(string!=null)
		mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())  
			    .keyword(string)  
			    .city(city));
	}
}