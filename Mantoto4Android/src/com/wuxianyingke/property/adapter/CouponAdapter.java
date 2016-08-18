package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;


public class CouponAdapter extends BaseAdapter {
	private List<RemoteApi.UserCashCoupon> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	private int flags;
	private int flag;
	private int selectIndex=-1;
	public CouponAdapter(Context ctx, List<RemoteApi.UserCashCoupon> list,int flags)
	{
		this.mContext = ctx;
		this.mList = list;
		this.flags=flags;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<RemoteApi.UserCashCoupon> list)
	{
		for(int i=0; i<list.size(); i++)
		{
			mList.add(list.get(i));
			mCount++;
		}
	}
	public void changeSelected(int position){
			selectIndex = position;
			notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(mStoped)
			return convertView;
		final ActivityItem activityItem;
		final RemoteApi.UserCashCoupon activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.activity_quan_item, null);
			activityItem = new ActivityItem();
//			activityItem.isReadImg = (ImageView) v.findViewById(R.id.isReadImg);
			activityItem.mBEtime = (TextView) v.findViewById(R.id.mBEtime);
			activityItem.mCouponPrice = (TextView) v.findViewById(R.id.couponPrice);
			activityItem.mCouponTime = (TextView) v.findViewById(R.id.couponTime);
			activityItem.mRechargeUse = (TextView) v.findViewById(R.id.reachargeUse);
			activityItem.mCouponLinearLayout=(LinearLayout)v.findViewById(R.id.couponLinerLayout);
			activityItem.mCouponNormalImage=(ImageView)v.findViewById(R.id.couponNormalImage);
			activityItem.mCouponImage=(ImageView)v.findViewById(R.id.couponImage);
			activityItem.mCouponOverdue=(ImageView)v.findViewById(R.id.couponOverdue);
			activityItem.mCouponCancelImage=(ImageView)v.findViewById(R.id.couponcancel);
			activityItem.mCouponUseedImage=(ImageView)v.findViewById(R.id.couponuseed);
//			activityItem.mMainRadio4ListItemLinearLayout = (LinearLayout) v.findViewById(R.id.MainRadio4ListItemLinearLayout);
			v.setTag(activityItem);
			convertView = v;
		} 
		else 
		{
			activityItem = (ActivityItem)convertView.getTag();
		}
		activityItem.mCouponPrice.setText(""+activity.cashCoupon.ParValue);
		activityItem.mBEtime.setText(activity.BTime+" 至 "+activity.ETime);
		if (activity.cashCouponStatus.CashCouponStatusID==1) {
			activityItem.mRechargeUse.setVisibility(View.VISIBLE);
			activityItem.mRechargeUse.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
			if (selectIndex==position) {
				
				activityItem.mCouponLinearLayout.setBackgroundResource(R.drawable.bg_stroke_item);
				activityItem.mCouponUseedImage.setVisibility(View.GONE);
			}else{
				activityItem.mCouponLinearLayout.setBackgroundResource(R.drawable.white_btn_list);
				activityItem.mCouponUseedImage.setVisibility(View.GONE);
			}
		}else {
			activityItem.mRechargeUse.setVisibility(View.GONE);
			activityItem.mCouponUseedImage.setVisibility(View.VISIBLE);
		}
		
//		activityItem.mCouponTime.setText("有效日期:" + activity.BTime+"～"+activity.ETime);
/*
		if (activity.cashCouponStatus.CashCouponStatusID==1)
		{
			if (selectIndex==position){
				Log.i("MyLog","position="+position);
				activityItem.mCouponImage.setVisibility(View.VISIBLE);
				activityItem.mCouponNormalImage.setVisibility(View.GONE);
//				LocalStore.setCouponTime(mContext, activity.ETime);
			}else{
				activityItem.mCouponImage.setVisibility(View.GONE);
			activityItem.mCouponNormalImage.setVisibility(View.VISIBLE);
			activityItem.mCouponOverdue.setVisibility(View.GONE);
			activityItem.mCouponUseedImage.setVisibility(View.GONE);
			activityItem.mCouponCancelImage.setVisibility(View.GONE);}
		}
		else if(activity.cashCouponStatus.CashCouponStatusID==2)
		{
			activityItem.mCouponImage.setVisibility(View.GONE);
			activityItem.mCouponNormalImage.setVisibility(View.GONE);
			activityItem.mCouponOverdue.setVisibility(View.VISIBLE);
			activityItem.mCouponCancelImage.setVisibility(View.GONE);
			activityItem.mCouponUseedImage.setVisibility(View.GONE);
		}	else if(activity.cashCouponStatus.CashCouponStatusID==3){
			activityItem.mCouponImage.setVisibility(View.GONE);
			activityItem.mCouponCancelImage.setVisibility(View.VISIBLE);
			activityItem.mCouponOverdue.setVisibility(View.GONE);
			activityItem.mCouponUseedImage.setVisibility(View.GONE);
			activityItem.mCouponNormalImage.setVisibility(View.GONE);
		}else{
			activityItem.mCouponImage.setVisibility(View.GONE);
			activityItem.mCouponUseedImage.setVisibility(View.VISIBLE);
			activityItem.mCouponOverdue.setVisibility(View.GONE);
			activityItem.mCouponNormalImage.setVisibility(View.GONE);
			activityItem.mCouponCancelImage.setVisibility(View.GONE);
		}
*/

		/*activityItem.mCouponLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			activityItem.mCouponImage.setVisibility(View.VISIBLE);
			activityItem.mCouponNormalImage.setVisibility(View.GONE);
			LocalStore.setCouponTime(mContext, activity.ETime);
			Log.i("Mylog", "ctime=" + LocalStore.getCouponTime(mContext));
			}
		});*/
/*		activityItem.mMainRadio4ListItemLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(mContext, MessageInfoActivity.class);
				intent.putExtra("productMessageInfoTitle", activity.header);
				intent.putExtra("productMessageInfoTime", activity.cTime);
				intent.putExtra("productMessageInfoContent", activity.body);
				intent.putExtra("productMessageInfoType", activity.type.messageTypeID);
				intent.putExtra("productMessageInfoSignature", activity.isRead);
				intent.putExtra("productMessageInfoRootID", activity.messageID);
				
				mContext.startActivity(intent);
			}
		});*/
		
		return convertView;
	}

	class ActivityItem 
	{
//		ImageView isReadImg;
		TextView mBEtime;
		TextView mCouponPrice;
		TextView mCouponTime;
		TextView mRechargeUse;
		LinearLayout mCouponLinearLayout;
		ImageView mCouponNormalImage,mCouponImage,mCouponOverdue,mCouponCancelImage,mCouponUseedImage;
//		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
