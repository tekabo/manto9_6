package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.OrderInfo;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;

/** 
* @ClassName: CommitOrderListActivity 
* @Description:(支付订单列表) 
* @author Liudongdong 
* @date 2015-8-14 下午12:48:18 
*  
*/
public class CommitOrderListActivity2 extends BaseActivity {
	//顶部导航
	private TextView topbar_txt;
	private Button topbar_left;
	private int favorite_flag;
	private Button completedBtn,uncompleteBtn;
	private ListView listView;
	/**上下文参数*/
	private Context mContext;
	/**设置请求页为第一页*/
	private int pageIndex=1;
	private int flags=3;
	/**定义一个集合用于存放获得数据*/
	private ArrayList<OrderItem>orderItems;
	private ProgressDialog mProgressBar = null;
	private GetOrderListAdapter golAdapter; 
	public Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {
			case 1:
				Toast.makeText(getApplicationContext(), "网络访问出错请查看网络是否正常！", 1).show();
				break;
				
			case 2:
				
				break;

			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_myentity);
		//初始化控件
		initViews();
		setImmerseLayout(findViewById(R.id.common_back));
		//初始化事件监听
		initListener();
		//获得未完成的订单
		getUncompletedOrderData();
	}

	//获得未完成的订单
	private void getUncompletedOrderData() {
		Thread getUncompleteOrderDataThread=new Thread(){
			User info=LocalStore.getUserInfo();
			@Override
			public void run() {
			RemoteApiImpl rai = new RemoteApiImpl();	
			OrderInfo orderInfo=rai.getUncompletedOrder(CommitOrderListActivity2.this, info.userId, pageIndex);
			Log.i("MyLog", "从服务端请求的数据为orderInfo"+orderInfo);
			
			Message msg=new Message();
			if (orderInfo==null) {
				msg.what=1;
			}else {
				msg.what=2;
				orderItems=orderInfo.orderInfo;
				Log.i("MyLog", "orderItems-------="+orderInfo.orderInfo);
			}
			}
		};
		
		getUncompleteOrderDataThread.start();
	}

	/** 
	* @Title: initListener 
	* @Description: TODO(初始化事件监听) 
	* @param     设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void initListener() {
		//左侧返回按钮事件监听
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(0!=favorite_flag){
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getApplicationContext(), UserCenterActivity.class);
					startActivity(intent);
				}else{
					finish();
				}
				
			}
		});
		
	//
		completedBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				changeTo(0);
			}
		});
		
		//
		uncompleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				changeTo(1);
				golAdapter=new GetOrderListAdapter(mContext, orderItems, flags);
				listView.setAdapter(golAdapter);
				golAdapter.notifyDataSetChanged();
			}
		});
		
	}
	
	/** 
	* @Title: changeTo 
	* @Description: TODO(页面选择方法) 
	* @param @param i    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void changeTo(int i)
	{
		if(i==0)
		{
			uncompleteBtn.setTextColor(Color.parseColor("#ffffff"));
			completedBtn.setTextColor(Color.parseColor("#00b1ff"));
			uncompleteBtn.setBackgroundResource(R.drawable.switch_button_left_on);
			completedBtn.setBackgroundResource(R.drawable.switch_button_right_default);
		}
		else
		{
			uncompleteBtn.setTextColor(Color.parseColor("#00b1ff"));
			completedBtn.setTextColor(Color.parseColor("#ffffff"));
			uncompleteBtn.setBackgroundResource(R.drawable.switch_button_left_default);
			completedBtn.setBackgroundResource(R.drawable.switch_button_right_on);
		}
	}


	/** 
	* @Title: initViews 
	* @Description: TODO(初始化控件) 
	* @param     设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void initViews() {
		//顶部导航
		topbar_txt= (TextView) findViewById(R.id.topbar_txt) ;
		topbar_txt.setText("我的订单");
		topbar_left=(Button)findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		
		//基础
		uncompleteBtn=(Button) findViewById(R.id.btn_UncompletedId);//未完成
		completedBtn=(Button) findViewById(R.id.btn_CompletedId);//已完成
		uncompleteBtn.setTextColor(Color.parseColor("#00b1ff"));
		completedBtn.setBackgroundResource(R.drawable.switch_button_right_on);//初始化时将完成背景设置成蓝色
		
				
		
	}
}
