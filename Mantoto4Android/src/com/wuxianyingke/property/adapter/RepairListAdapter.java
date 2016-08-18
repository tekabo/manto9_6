package com.wuxianyingke.property.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.mantoto.property.R.color;
import com.wuxianyingke.property.activities.RepairLogActivity;
import com.wuxianyingke.property.remote.RemoteApi.Repair;


public class RepairListAdapter extends BaseAdapter {
	private List<Repair> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	
	private Activity mActivity;

	/* 用来标识列表单元被点击 */
    public static final int LIST_ITEM_CLICK_CODE = 1000;
	
	public RepairListAdapter(Context ctx, List<Repair> list)
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public RepairListAdapter(Activity aty, Context ctx, List<Repair> list)
	{
		this.mActivity = aty;
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<Repair> list)
	{
		for(int i=0; i<list.size(); i++)
		{
			mList.add(list.get(i));
			mCount++;
		}
	}
	
	public void cancelRepair(long repairId)
	{
		for (Repair repair : mList) {
			if(repair.repairid == repairId){
				//5	已取消 	报修人终止报修
				repair.status.repairStatusId = 5;
				repair.status.repairStatusName = "已取消 ";
				repair.status.repairStatusDescription = "报修人终止报修";
			}
		}
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
		ActivityItem activityItem;
		final Repair activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.repair_list_item, null);
			activityItem = new ActivityItem();

			
			activityItem.mRepairTypeTextView = (TextView) v.findViewById(R.id.RepairType);
			activityItem.mRepairContentTextView = (TextView) v.findViewById(R.id.RepairContent);
			activityItem.mRepairStatusTextView = (TextView) v.findViewById(R.id.OrderProgress);
			activityItem.mRepairCtimeTextView = (TextView) v.findViewById(R.id.CommitOrderTime);

			activityItem.mMainRadio4ListItemLinearLayout = (LinearLayout) v.findViewById(R.id.MainRadio4ListItemLinearLayout);
			v.setTag(activityItem);
			convertView = v;
		} 
		else 
		{
			activityItem = (ActivityItem)convertView.getTag();
		}


		int statusId = (int)activity.status.repairStatusId;


//
//		switch (statusId){
//			case 1:
//			case 2:
//			case 7:
//				activityItem.mRepairStatusTextView.setTextColor(0xffff0b20);
//				activityItem.isReadImg.setVisibility(View.VISIBLE);
//				activityItem.mRepairStatusTextView.setText("查看进度");
//				break;
//			case 3:
//				activityItem.isReadImg.setVisibility(View.INVISIBLE);
//				activityItem.mRepairStatusTextView.setTextColor(0xff00B1FB);
//				activityItem.mRepairStatusTextView.setText(activity.status.repairStatusName);
//				break;
//			case 4:
//			case 5:
//			case 6:
//				activityItem.isReadImg.setVisibility(View.INVISIBLE);
//				activityItem.mRepairStatusTextView.setTextColor(0xff999999);
//				activityItem.mRepairStatusTextView.setText(activity.status.repairStatusName);
//				break;
//
//
//
//
//		}
		String name="已取消";
		activityItem.mRepairTypeTextView.setText(activity.type.repairTypeName);
		activityItem.mRepairContentTextView.setText(activity.body);

		activityItem.mRepairCtimeTextView.setText(activity.cTime);
		activityItem.mRepairStatusTextView.setText(""+activity.status.repairStatusName);
		if (!activity.status.repairStatusName.equals(name)) {
			activityItem.mRepairStatusTextView.setTextColor(Color.BLUE);
		}else {
			activityItem.mRepairStatusTextView.setTextColor(Color.GRAY);
		}
		
		activityItem.mMainRadio4ListItemLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(mContext, RepairLogActivity.class);
				intent.putExtra("repairLogTitle", activity.type.repairTypeName);
				intent.putExtra("repairLogStatusName", activity.status.repairStatusName);
				intent.putExtra("repairLogStatusId", activity.status.repairStatusId);
				intent.putExtra("repairLogStatusDesc", activity.status.repairStatusDescription);
				intent.putExtra("repairDesc",activity.body);
				intent.putExtra("repairCTime",activity.cTime);
				intent.putExtra("phone", activity.contact);
				Log.i("MyLog", "RepairListAdapter获得当前电话为"+activity.contact);
				intent.putExtra("repairId", activity.repairid);

				//mContext.startActivity(intent);
				mActivity.startActivityForResult(intent, LIST_ITEM_CLICK_CODE);
			}
		});
		
		return convertView;
	}

	class ActivityItem 
	{
		TextView mRepairContentTextView;
		TextView mRepairStatusTextView;
		TextView mRepairTypeTextView;
		TextView mRepairCtimeTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
