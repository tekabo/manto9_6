package com.wuxianyingke.property.activities;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.wuxianyingke.property.adapter.IndexImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.UpdateManger;
import com.wuxianyingke.property.remote.RemoteApi.HomeMessage;
import com.wuxianyingke.property.remote.RemoteApi.UpdateInfo;
import com.wuxianyingke.property.remote.RemoteApi.WeatherInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.service.SendLocation;
import com.wuxianyingke.property.threads.GetIndexInfoThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;

public class Radio1Activity extends BaseActivity {
	static final String TAG = "Radio1Activity";
	private ProgressDialog mWaitLoading = null;
	private IndicationDotList mDotList = null;
	private GetIndexInfoThread mThread = null;
	private MyGallery mGallery = null;
	private IndexImageAdapter mGalleryAdapter = null;
	/**
	 * 退出判断
	 */
	private long exitTimeSmillis=System.currentTimeMillis();
	private long tempUserId = -1;
	private HomeMessage mHomeMessage;
	private TextView topbar_txt;
	private ImageView mUserCenterLinearLayout, mShouyeLinearLayout,
			mBohaoLinearLayout;
	private ImageView mLuKuangImage;
	private LocationClient mLocClient;
	private static SendLocation info = new SendLocation();
	private TextView DateTextView, Date_day_TextView, Date_weekday_TextView,
			DiZhiTextView, WenduTextView, TianQiTextView;
	private ImageView TianqiImageView, LuKuangImage;
	//定位服务
	public MyLocationListenner myListener = new MyLocationListenner();
	
	public double x,y;
	
	public LocationClient locClient;
	private UpdateInfo updateInfo;
	private String desc;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_INDEX_INFO_FINISH:
				if (mWaitLoading != null && mWaitLoading.isShowing())
					mWaitLoading.dismiss();
				if (mThread != null && mThread.getAllIndexInfo() != null) {
					mHomeMessage = mThread.getAllIndexInfo();
					for (int i = 0; i < mHomeMessage.notes.size(); ++i) {
						mGalleryAdapter.addImg(mHomeMessage.notes.get(i));

					}
					mDotList.setCount(mHomeMessage.notes.size());
					mGalleryAdapter.notifyDataSetChanged();
				}
				LocalStore.setIsVisitor(Radio1Activity.this,
						mHomeMessage.isVisitor);
				break;
			case Constants.MSG_GET_INDEX_INFO_NET_ERROR:
				if (mWaitLoading != null && mWaitLoading.isShowing())
					mWaitLoading.dismiss();

				View view = (View) findViewById(R.id.view_network_error);
				view.setVisibility(View.VISIBLE);
				break;
			case 3:
				initTopBar();
				break;
			case 5:
				Toast.makeText(getApplicationContext(), "请查看网络连接是否正常", Toast.LENGTH_SHORT)
						.show();
				break;
			case 9:
				if (LocalStore.getIsUpload(getApplicationContext())) {
					UpdateManger updateManger = new UpdateManger(Radio1Activity.this, updateInfo.url, updateInfo.updateInfo, updateInfo.versionCode,updateInfo.appversion);
					updateManger.checkUpdate();
				}
				
				break;
			case 10:
				Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT)
				.show();
				break;
			}
		}
	};

	public void initTopBar() {
		DateTextView = (TextView) findViewById(R.id.DateTextView);
		Date_day_TextView = (TextView) findViewById(R.id.Date_day_TextView);
		Date_weekday_TextView = (TextView) findViewById(R.id.Date_weekday_TextView);
		DiZhiTextView = (TextView) findViewById(R.id.DiZhiTextView);
		WenduTextView = (TextView) findViewById(R.id.WenduTextView);
		TianQiTextView = (TextView) findViewById(R.id.TianQiTextView);
		TianqiImageView = (ImageView) findViewById(R.id.TianqiImageView);

		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}

		DateTextView.setText(mYear + "." + mMonth);
		Date_day_TextView.setText(mDay);
		Date_weekday_TextView.setText("星期" + mWay);
		DiZhiTextView.setText(LocalStore.cityInfo.city_name);
		WenduTextView.setText(LocalStore.weatherInfo.temp1);
		TianQiTextView.setText(LocalStore.weatherInfo.fl1);

		int rid = R.drawable.qing;

		if (LocalStore.weatherInfo.img_title_single.equals("特大暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("中雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("小雨")) {
			rid = R.drawable.xiaoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雷阵雨")) {
			rid = R.drawable.leizhenyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阵雨")) {
			rid = R.drawable.zhenyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("冻雨")) {
			rid = R.drawable.yujiaxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雨夹雪")) {
			rid = R.drawable.yujiaxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("暴雪")) {
			rid = R.drawable.baoxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阵雪")) {
			rid = R.drawable.zhenxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大雪")) {
			rid = R.drawable.zhenxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("中雪")) {
			rid = R.drawable.zhongxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("小雪")) {
			rid = R.drawable.xiaoxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("霾")) {
			rid = R.drawable.wumai;
		} else if (LocalStore.weatherInfo.img_title_single.equals("强沙尘暴")) {
			rid = R.drawable.qiangshacenbao;
		} else if (LocalStore.weatherInfo.img_title_single.equals("沙尘暴")) {
			rid = R.drawable.shachenbao;
		} else if (LocalStore.weatherInfo.img_title_single.equals("扬沙")) {
			rid = R.drawable.yangsha;
		} else if (LocalStore.weatherInfo.img_title_single.equals("浮尘")) {
			rid = R.drawable.yangsha;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雾")) {
			rid = R.drawable.zhongwu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阴")) {
			rid = R.drawable.yin;
		} else if (LocalStore.weatherInfo.img_title_single.equals("多云")) {
			rid = R.drawable.yin;
		} else if (LocalStore.weatherInfo.img_title_single.equals("晴")) {
			rid = R.drawable.qing;
		}

		TianqiImageView.setImageResource(rid);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_radio1);
		setImmerseLayout(findViewById(R.id.view));
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initWidget();
	/*	Thread getUpdateThread = new Thread(){
			@Override
			public void run() {
			
				RemoteApiImpl remoteApiImpl = new RemoteApiImpl();
				updateInfo = remoteApiImpl.getUpdateInfo(getApplicationContext(), "mantoto");
				Message msg = new Message();
				if (updateInfo == null) {
					msg.what = 5;
				}else
				{
				if (updateInfo.netInfo.code == 200)
					{
						msg.what = 9;
					}else{
						desc = updateInfo.netInfo.desc;
						msg.what = 10;
					}	
				}
				mHandler.sendMessage(msg);
			}
		};
		getUpdateThread.start();*/
		// topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		// topbar_txt.setText(getResources().getText(R.string.radio_main1));
		tempUserId = LocalStore.getUserInfo().userId;
		mGallery = (MyGallery) findViewById(R.id.my_gallery);
		mDotList = (IndicationDotList) findViewById(R.id.index_indication);
		mDotList.changeType();
		Gallery.LayoutParams params = new Gallery.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mGalleryAdapter = new IndexImageAdapter(Radio1Activity.this, params);
		mGallery.setAdapter(mGalleryAdapter);
		mGallery.setSpacing(1);
		mGallery.setOnItemSelectedListener(gallerySelectListener);
		mGallery.setOnItemClickListener(galleryClickListener);
		initResource();

		// BaiduLocation mBaiduLocation = new BaiduLocation(this, null);
		// mBaiduLocation.getLocation(Constants.MSG_LOCATION_READY);

		// get location
		/**定位服务*/
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			long currentTimeSmillis=System.currentTimeMillis();
			if (currentTimeSmillis-exitTimeSmillis==0||currentTimeSmillis-exitTimeSmillis>1500) {
				exitTimeSmillis=System.currentTimeMillis();
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				return false;
			}else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initWidget() {
		mUserCenterLinearLayout = (ImageView) findViewById(R.id.UserCenterLinearLayout);
		mShouyeLinearLayout = (ImageView) findViewById(R.id.ShouyeLinearLayout);
		mBohaoLinearLayout = (ImageView) findViewById(R.id.BohaoLinearLayout);
		mLuKuangImage = (ImageView) findViewById(R.id.LuKuangImage);
		mUserCenterLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Radio1Activity.this,
						UserCenterActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mShouyeLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Radio1Activity.this,
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mBohaoLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:"
						+ LocalStore.getUserInfo().phone);
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
			}
		});
		initTopBar();
		LuKuangImage = (ImageView) findViewById(R.id.LuKuangImage);
		LuKuangImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(Radio1Activity.this, lukuangActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;

			info.latitude = location.getLatitude();
			info.longitude = location.getLongitude();
			Log.i("MyLog",
					"Radio1Activity" + "info.latitude:"
							+ location.getLatitude() + ":"
							+ location.getLongitude());
			info.city = location.getCity();
			Log.i("MyLog", "Radio1Activity---百度地图获得定位是----" + info.city
					+ "location.getCity" + location.getCity());
			LocalStore.cityInfo.city_name = info.city;
			LocalStore.setCityInfo(Radio1Activity.this, LocalStore.cityInfo);

			Thread registerThread = new Thread() {
				public void run() {
					RemoteApiImpl remote = new RemoteApiImpl();
					WeatherInfo netInfo = remote.getWeather(
							Radio1Activity.this, info.city);

					Message msg = new Message();
					if (netInfo == null) {
						msg.what = 4;
					} else {
						LocalStore.weatherInfo.fl1 = netInfo.fl1;
						LocalStore.weatherInfo.temp1 = netInfo.temp1;
						LocalStore.weatherInfo.img_title_single = netInfo.img_title_single;
						LocalStore.weatherInfo.wind1 = netInfo.wind1;
						LocalStore.saveWeatherInfo(Radio1Activity.this);
						mLocClient.stop();
						msg.what = 3;
					}
					mHandler.sendMessage(msg);
				}
			};
			registerThread.start();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if (needInit) {
			// 重新获取资源
			showDialog();
			endChildrenThreads();
			mThread = new GetIndexInfoThread(this, mHandler);
			mThread.start();
			mDotList.setCount(0);
			LogUtil.d("MyTag", "Radio1Activity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}

	private void endChildrenThreads() {

		if (mThread != null) {
			mThread.stopRun();
			mThread = null;
		}

	}

	private void showDialog() {
		View view = (View) findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mWaitLoading = new ProgressDialog(Radio1Activity.this);
		String msg = getResources().getString(R.string.txt_loading);
		mWaitLoading.setMessage(msg);
		mWaitLoading.setCancelable(true);
		mWaitLoading.show();
	}

	void freeResource() {
		mThread.stopRun();
		mGalleryAdapter.freeDrawable();
		mHomeMessage = null;
	}

	void initResource() {
		showDialog();
		mThread = new GetIndexInfoThread(this, mHandler);
		mThread.start();
		mDotList.setCount(0);

	}

	/*
	 * 当用户滑动了图片回调
	 */
	private final OnItemSelectedListener gallerySelectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mDotList.setIndex(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	/*
	 * 当用户点击了gallery上的图片回调
	 * 
	 * @selectItemIndex
	 */
	private final OnItemClickListener galleryClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(Radio1Activity.this,
					Radio1InfoActivity.class);
			intent.putExtra("radio1info_title",
					mHomeMessage.notes.get(position).header);
			intent.putExtra("radio1info_body",
					mHomeMessage.notes.get(position).body);
			intent.putExtra("radio1info_time",
					mHomeMessage.notes.get(position).time);
			intent.putExtra("radio1info_signature",
					mHomeMessage.notes.get(position).signature);
			startActivity(intent);
		}
	};

}