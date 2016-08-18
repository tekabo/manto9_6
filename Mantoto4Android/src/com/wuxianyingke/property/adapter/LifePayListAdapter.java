package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
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
import com.wuxianyingke.property.activities.CanyinDetailActivity;
import com.wuxianyingke.property.activities.CanyinDetailOwnActivity;
import com.wuxianyingke.property.adapter.LifePayAdapter.ViewHolder;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;

public class LifePayListAdapter extends BaseAdapter {

	private List<LivingItem> mList;
	private Context mContext;
	private Handler mHandler;
	private boolean mStoped;
	private boolean mIsOnEdit;
	private int mCount;
	private int shoucang_flag;
	private int favorite_flat;

	public LifePayListAdapter(Context ctx, List<LivingItem> list, Handler handler,int shoucang_flag , int favorite_flat) {
		this.mContext = ctx;
		this.mList = list;
		this.mHandler = handler;
		this.mStoped = false;
		this.shoucang_flag=shoucang_flag;
		this.mCount = mList.size();
		this.favorite_flat = favorite_flat;
	}

	public void freeDrawable() {
		mStoped = true;
		Log.d("MyTag", "App bitmaps free !!! ");
		for (int i = 0; i < mList.size(); i++) {
			BitmapDrawable a = (BitmapDrawable) mList.get(i).FrontCover.imgDw;
			if (a != null && !a.getBitmap().isRecycled())
				a.getBitmap().recycle();
			mList.get(i).FrontCover.imgDw = null;
		}
		System.gc();
	}

	public void setIsOnEdit(boolean isOnEdit) {
		mIsOnEdit = isOnEdit;
	}

	public final boolean getIsOnEdit() {
		return mIsOnEdit;
	}

	public void appandAdapter(List<LivingItem> list) {
		for (int i = 0; i < list.size(); i++) {
			mList.add(list.get(i));
			mCount++;
		}

	}

	@Override
	public int getCount() {
		return
		/* mCount */
		mList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (mStoped)
			return convertView;
		ProductItem productItem;

		Log.d("MyTag","CanYinListAdapter--position="+position);

		final LivingItem info = mList.get(position);
		if (convertView == null) {

			View v = LayoutInflater.from(mContext).inflate(
					R.layout.pay_life_list_item, null);
			productItem = new ProductItem();
			productItem.payImage = (ImageView) v.findViewById(R.id.LifeLogoImageView);
			productItem.tvHeader = (TextView) v.findViewById(R.id.LifeHeaderTextView);
			productItem.tvContent = (TextView) v.findViewById(R.id.LifeContentTextView);
			productItem.tvPay = (TextView) v.findViewById(R.id.LifepayTextView);
			productItem.rightImage=(ImageView) v.findViewById(R.id.LifeLeftImageView);
		
			v.setTag(productItem);
			convertView = v;
		} else {

			productItem = (ProductItem) convertView.getTag(); // 获取到可复用的item中的所有控件对象
			productItem.payImage.setImageResource(R.drawable.code_logo);// 重置图片控件
		}

			productItem.payImage.setImageResource(R.drawable.code_logo);
		
		return convertView;
	}

	class ProductItem {
//		LinearLayout mItemBackground;
//		ImageView mIcon;
//		TextView mName;
//		TextView mLeixing;
//		TextView mJuli;
//		TextView mRenjun;
//		TextView mDizhi;
//		ImageView mTuan;
//		ImageView mJuan;
//		ImageView mHui;
		public ImageView payImage,rightImage;//生活缴费图片,跳转图标
		public TextView tvHeader,tvContent,tvPay;//标题，内容描述，我要缴费
	}
}
