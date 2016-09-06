package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spanned;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.PropertyApplication;
import com.wuxianyingke.property.adapter.CanYinListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.service.LocationService;
import com.wuxianyingke.property.threads.GetCanYinListThread;
import com.wuxianyingke.property.views.StatusBarCompat;

public class CanYinListActivity extends BaseActivity{

	private enum ProductListTab {
		LIST, GRID
	}
	private enum DataSource {
		CATE_SOURCE,
	}

	private ListView mLogsListView;
	private ArrayList<Flea> mDataList = new ArrayList<Flea>();
	private static CanYinListAdapter mListAdapter; // TODO 写重复了一个adapter
	private Button mLeftTopButton,topbar_button_right;
	private TextView topbar_txt;

	private GetCanYinListThread mThread = null;
	private ProgressDialog mProgressDialog;
	private int propertyid;
    private int shoucang_flag=0;
	private ProductListTab mTab = ProductListTab.LIST;

	private DataSource mDataSource = DataSource.CATE_SOURCE;
    private String flag="1";
    private LocationService locationService;
    private TextView mNotification;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				//得到餐饮列表
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if(mThread!=null)
					locationService.stop();
					if(LocalStore.getLatitude(getApplicationContext())==0){
						mNotification = ((TextView) findViewById(R.id.empty_tv));
						mNotification.setVisibility(View.VISIBLE);
						String notification = "未获取到您的位置信息，请重试";
						SpannableString spannableString = new SpannableString(notification);
						int start = notification.indexOf("请");
						int end = start+3;

						spannableString.setSpan(new Clickable(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Toast.makeText(getApplicationContext(), "您好", Toast.LENGTH_SHORT).show();
							}
						}), start, end, Spanned.SPAN_MARK_MARK);

						spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7E00")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						mNotification.setText(spannableString);



					}else{
						((TextView) findViewById(R.id.empty_tv)).setVisibility(View.GONE);

						showLogsListView(mThread.getProductList());
					}
				break;
			//搜索结果为空
			case Constants.MSG_GET_CANYIN_LIST_EMPTY:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				mNotification = ((TextView) findViewById(R.id.empty_tv));
				mNotification.setVisibility(View.VISIBLE);
				mNotification.setEnabled(false);
				break;
			//网络连接错误
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if(mPageNum == 1)
				{
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				}
				else
				{
					LogUtil.d("MyTag","MSG_NETWORK_ERROR");
					mPageNum--;
					mAllowGetLogAgain = false;
					searchLayout.setVisibility(View.GONE);
				}
				break;
			//和搜索有关
			case Constants.MSG_GET_SEARCH_ICON_FINISH:
			//餐饮图片细节
			case Constants.MSG_GET_CANYIN_DETAIL_IMG_FINISH:
				if (mListAdapter != null)
					mListAdapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		StatusBarCompat.compat(this,getResources().getColor(R.color.status_bar_color));
		setContentView(R.layout.canyin_list);
		setImmerseLayout(findViewById(R.id.common_back));
//		SharedPreferences saving = this.getSharedPreferences(
//				LocalStore.USER_INFO, 0);
//		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		propertyid=LocalStore.getUserInfo().PropertyID;
		shoucang_flag= getIntent().getIntExtra(Constants.SHOUCANG_FLAT , 0) ;
		ImageView cartImageview = (ImageView) findViewById(R.id.cart_imageview);
		//修改购物车显示的数量
		Util.modifyCarNumber(this, cartImageview);
		
		//设置标题
		topbar_txt= (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("餐饮");

		LogUtil.d("MyTag", "餐饮");//打印日志
		mNotification = ((TextView) findViewById(R.id.empty_tv));
		mNotification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				locationService.start();
			}
		});
		
		//设置左侧按钮并添加点击事件
		mLeftTopButton = (Button) findViewById(R.id.topbar_left);
		mLeftTopButton.setVisibility(View.VISIBLE);
		mLeftTopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		//设置右侧按钮并添加进入收藏点击事件更改为查询事件
		topbar_button_right = (Button) findViewById(R.id.topbar_button_right) ;
		topbar_button_right.setVisibility(View.VISIBLE);
		topbar_button_right.setBackgroundResource(R.drawable.shop_collection_clicked);
		topbar_button_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				LogUtil.d("MyTag", "topbar_button_right on click listener");

				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				/*intent.setClass(CanYinListActivity.this, CollectionListActivity.class);*/
				intent.setClass(CanYinListActivity.this,CollectionListActivity.class);
				intent.putExtra(Constants.SHOUCANG_FLAT,shoucang_flag);
				startActivity(intent);
			}
		});
		
		//商店列表listview
		mLogsListView = (ListView) findViewById(R.id.product_list_view);
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
		mLogsListView.addFooterView(showLayout());
		mLogsListView.setOnScrollListener(mScrollListner);
		showDialog();
		mThread = new GetCanYinListThread(CanYinListActivity.this,
				mHandler, propertyid,flag, mPageNum,LocalStore.getLatitude(getApplicationContext()),LocalStore.getLongitude(getApplicationContext()));
		Log.i("MyLog", "CanyinList+propertyid="+propertyid);
		Log.i("MyLog", "CanyinList+flag="+flag);
		Log.i("MyLog", "CanyinList+mPageNum="+mPageNum);
		mThread.start();
	}
	protected void onRestart(){
		if(mLogsListView!=null)
//			mLogsListView.invalidateViews();
		super.onRestart();
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			showDialog();
			endChildrenThreads();
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.VISIBLE);
			mThread = new GetCanYinListThread(CanYinListActivity.this,
					mHandler, propertyid,flag, mPageNum,LocalStore.getLatitude(getApplicationContext()),LocalStore.getLongitude(getApplicationContext()));
			mThread.start();
			LogUtil.d("MyTag", "InformDetailActivity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy()
	{
		endChildrenThreads();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//startThread();
		super.onResume();
	}

	private void showDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mLogsListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(CanYinListActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}


	public void freeResource() {
		int count = mDataList.size();
		for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mDataList.get(i).frontCover.imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}
		mThread = null ;
		showDialog();
		endChildrenThreads();
	}

	public void showLogsListView(List<LivingItem> list){
		if (list == null){
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		if(mPageNum == 1){
			 
			mListAdapter = new CanYinListAdapter(this, list,mHandler,shoucang_flag,0);
			LogUtil.d("MyTag","CanYinListActivity253-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mListAdapter.getCount());
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mLogsListView.setAdapter(mListAdapter);
		}
		else{
			mListAdapter.appandAdapter(list);
			mListAdapter.notifyDataSetChanged();
			LogUtil.d("MyTag","CanYinListActivity265- mItemSum = mLogAdapter.getCount()="+mListAdapter.getCount());
		/*	int test12 = mItemSum % 10;
			if(test12 == 0)
				mLogsListView.setSelection(mItemSum - 10);
			else
				mLogsListView.setSelection(mItemSum - test12);*/
		}
		setAllowGetPageAgain();
	}
	


	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;
	//添加足部view
	public LinearLayout showLayout(){
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);
 		//加载圈
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}
	
	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;
	public static void setAllowGetPageAgain(){
		LogUtil.d("MyTag", "mItemSummItemSum="+mItemSum);
		if(mListAdapter!=null)
			mItemSum = mListAdapter.getCount();
		int test12 = mItemSum % 10;
		if((test12 != 0) || (mItemSum >= 96))
		{
			LogUtil.d("MyTag", "mItemSummItemSum"+test12);
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.GONE);
		}
		else
		{
			mAllowGetLogAgain = true;
			LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"+test12);
		}
	}
	
	private OnScrollListener mScrollListner = new OnScrollListener(){
		private int lastItem = 0;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
			lastItem = firstVisibleItem + visibleItemCount - 1;
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState){
			if (mListAdapter != null && lastItem >= (mListAdapter.getCount()-2)){
				LogUtil.d("MyTag", "InformDetailActivity.this mAllowGetLogAgain="+mAllowGetLogAgain);
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "InformDetailActivity.this onScrollStateChanged");
				mThread = new GetCanYinListThread(CanYinListActivity.this,
						mHandler, propertyid,flag, mPageNum,LocalStore.getLatitude(getApplicationContext()),LocalStore.getLongitude(getApplicationContext()));
				mThread.start();
			}
		}
	};

	
	private void endChildrenThreads(){

		if (mThread != null){
			mThread.stopRun();
			mThread = null;
		}

		mLogsListView.setAdapter(null);
		mAllowGetLogAgain = true;
		mPageNum = 1;
		mItemSum = 0;
	}

	void initResource() {
		// TODO Auto-generated method stub
		LogUtil.d("MyTag", "InformDetailActivity.this initResource");
		freeResource() ;
		if (mThread == null) 
		showDialog();
		mThread = new GetCanYinListThread(CanYinListActivity.this,
				mHandler, propertyid,flag, mPageNum,LocalStore.getLatitude(getApplicationContext()),LocalStore.getLongitude(getApplicationContext()));
		mThread.start();
	}
	class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view);
        }
    }
	@Override
	protected void onStart() {
		super.onStart();
		locationService = ((PropertyApplication) getApplication()).locationService; 
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
		locationService.start();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
	}
	
	private BDLocationListener mListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			LocalStore.setLatitude(getApplicationContext(), (float)location.getLatitude());
			LocalStore.setLongitude(getApplicationContext(), (float)location.getLongitude());
			Log.i("MyLog", "百度定位获得的当前的位置为------------》" + location.getCity() + "----"+"latitude = "+LocalStore.getLatitude(getApplicationContext())+" / "+LocalStore.getLongitude(getApplicationContext())
					+ location.getCity());
			LocalStore.cityInfo.city_name = location.getCity();
			LocalStore.setCityInfo(getApplicationContext(), LocalStore.cityInfo);	
			mThread = new GetCanYinListThread(CanYinListActivity.this,
					mHandler, propertyid,flag, mPageNum,LocalStore.getLatitude(getApplicationContext()),LocalStore.getLongitude(getApplicationContext()));
			mThread.start();
			
		}
	};
}
