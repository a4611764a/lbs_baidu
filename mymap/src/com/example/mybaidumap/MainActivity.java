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
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
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
		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mSearch = GeoCoder.newInstance();
		// ���õ�ͼ��ʼZoom��С
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		// ������λͼ��
		// mBaiduMap.setMyLocationEnabled(true);

		// ��λ��ʼ��
		// popWin(new LatLng(latitudeValue, longitudeValue));
		// mSearch.geocode(new GeoCodeOption().city("").address(address));
		LocationClient mLocClient = new LocationClient(this);
		MyLocationListenner myListener = new MyLocationListenner();
		mSuggestionSearch = SuggestionSearch.newInstance();
		mLocClient.registerLocationListener(myListener);
		mSearch.setOnGetGeoCodeResultListener(listener);
		mSuggestionSearch.setOnGetSuggestionResultListener(listener_advice);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		OnMapClickListener listener = new OnMapClickListener() {
			/**
			 * ��ͼ�����¼��ص�����
			 * 
			 * @param point
			 *            ����ĵ�������
			 */
			public void onMapClick(LatLng point) {
				LatLng ptCenter = point;
				// ��Geo����
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(ptCenter));

				/*
				 * mSearch.geocode(new GeoCodeOption() .city("����")
				 * .address("�������ϵ�ʮ��10��"));
				 */
				/*
				 * GeoPoint ptCenter = new GeoPoint((int) (point.latitude),
				 * (int) (point.longitude)); // ��Geo����
				 * mSearch.reverseGeocode(ptCenter);
				 */
				/*
				 * mSearch.geocode(new
				 * GeoCodeOption().city("����").address("�������ϵ�ʮ��10��")); GeoPoint
				 * ptCenter1 = new GeoPoint((int) (point.latitude), (int)
				 * (point.longitude));
				 */
				popWin(point);
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO �Զ����ɵķ������
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
					Toast.makeText(MainActivity.this, "��������Ҫ��λ�ĵ�ַ",
							Toast.LENGTH_SHORT).show();

				else if (conaddress.getText().toString().trim().length() > 3) {
					mSearch.geocode(new GeoCodeOption().city("").address(
							conaddress.getText().toString()));
					setListView(conaddress.getText().toString()+"");
				} else {
					Toast.makeText(MainActivity.this, "��������Ҫ��λ����ϸ��ַ",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
		mSearch.destroy();
		mSuggestionSearch.destroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
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
				//�����ַ
				setListView(location.getAddrStr());
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	// ����λ�ý������
	OnGetSuggestionResultListener listener_advice = new OnGetSuggestionResultListener() {  
	    public void onGetSuggestionResult(SuggestionResult res) {  
	        if (res == null || res.getAllSuggestions() == null) {  
	            return;  
	            //δ�ҵ���ؽ��  
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
	// ����λ�ü�������
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			mBaiduMap.clear();
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// û�м��������
				Toast.makeText(MainActivity.this, "��Ǹ��δ���ҵ����",
						Toast.LENGTH_LONG).show();
				mBaiduMap.clear();
			}
			// ��ȡ���������
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
				Toast.makeText(MainActivity.this, "��Ǹ��δ���ҵ����",
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
			//�����ַ
			setListView(result.getAddress());
			Toast.makeText(MainActivity.this, result.getAddress(),
					Toast.LENGTH_LONG).show();

		}
	};

	// �Զ�����ʾͼ��
	public void popWin(LatLng point) {

		// �����ͼ״̬ �������ĵ㣬�Ŵ���
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(15)
				.build();
		// ����MapStatusUpdate�����Ա�������ͼ״̬��Ҫ�����ı仯
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// �ı��ͼ״̬
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.nav_turn_via_1);
		// ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// �ڵ�ͼ�����Marker������ʾ
		mBaiduMap.clear();
		mBaiduMap.addOverlay(option);
		longitude.setText(point.longitude + "");
		latitude.setText(point.latitude + "");

		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
	}
	//�ײ�listView��ʾ�����ֵ���Ϣ
	private void setListView(String string) {
		if(string!=null)
		mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())  
			    .keyword(string)  
			    .city(city));
	}
}