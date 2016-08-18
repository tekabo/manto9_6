package com.wuxianyingke.property.adapter;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.LifePayContentActivity;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.decoding.FinishListener;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;

public class LifePayAdapter extends BaseAdapter {

	/**上下文参数的对象*/
	private Context mContext;
	/**数据源集合的对象*/
	private List<LivingItem> mList;
	/**网络操作工具类*/
	private int mCount;
	/**启用线程池下载网络数据*/
	private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
	public LifePayAdapter(Context mContext, List<LivingItem> mList, int mCount) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.mCount = mCount;
	}

	@Override
	public int getCount() {
		return mList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		final LivingItem info = mList.get(position);
		if (convertView == null) { // 创建新的itemView
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.pay_life_list_item, null); // 加载item布局并生成View对象
			
			vHolder = new ViewHolder();
			vHolder.payImage=(ImageView) convertView.findViewById(R.id.LifeLogoImageView);
			vHolder.rightImage=(ImageView) convertView.findViewById(R.id.LifeLeftImageView);
			vHolder.tvHeader=(TextView) convertView.findViewById(R.id.LifeHeaderTextView);
			vHolder.tvContent=(TextView) convertView.findViewById(R.id.LifeContentTextView);
			vHolder.tvPay=(TextView) convertView.findViewById(R.id.LifepayTextView);
			convertView.setTag(vHolder);
		} else { // 复用ListView滚出屏幕的itemView

			vHolder = (ViewHolder) convertView.getTag(); // 获取到可复用的item中的所有控件对象
			vHolder.payImage.setImageResource(R.drawable.code_logo);// 重置图片控件
		}
		vHolder.tvHeader.setText(info.LivingItemName);
		vHolder.tvContent.setText(info.Description);
		
		String url=info.FrontCover.path;
		if(url==null){
			vHolder.payImage.setImageResource(R.drawable.code_logo);
		}else{
			// 从SDCard中读取图片
			Bitmap bitmap = SDCardUtils.readImage(url);
			if (bitmap != null) {
				vHolder.payImage.setImageBitmap(bitmap);
			}else {
//				String url=imageUrl;
				vHolder.payImage.setTag(url); // 将图片的地址作为图片控件的标签

				// 网络下载
				download(url);
			}
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent=new Intent();
				intent.setClass(mContext, LifePayContentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				
			}
		});
		
		return convertView;
	}
	
	
	private void download(final String url) {

		// 将网络请求处理的Runnable增加到线程池中
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {

				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {

						byte[] bytes = EntityUtils.toByteArray(response
								.getEntity());
						//保存图片到本地
							SDCardUtils.saveImage(url, bytes);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
		
	}


	class ViewHolder{
		public ImageView payImage,rightImage;//生活缴费图片,跳转图标
		public TextView tvHeader,tvContent,tvPay;//标题，内容描述，我要缴费
	}


	public void appandAdapter(List<LivingItem> list) {
		for (int i = 0; i < list.size(); i++) {
			mList.add(list.get(i));
			mCount++;
		}
	}

}
