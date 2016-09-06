package com.wuxianyingke.property.threads;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.PropertyNotificationInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;


public class PropertyNotificationThread extends Thread
{
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private int count;
	private int propertyid;
	private List<PropertyNotificationInfo> mActivityList;

	private boolean running = true;

	public PropertyNotificationThread(Context ctx, Handler handler, int propertyid,int count)
	{
		this.handler = handler;
		this.ctx = ctx;
		this.propertyid = propertyid;
		this.count = count;
	}

	public synchronized void stopRun(){
		running = false;
		this.interrupt();

	}
	
	public List<PropertyNotificationInfo> getActivitys()
	{
		return mActivityList;
	}

	public void run() 
	{
		try 
		{
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			mActivityList = remoteApi.getPropertyNotification(ctx, propertyid,count);
			if (mActivityList == null) 
			{
				if (!running) 
					return;
				Message msg = new Message();
				msg.what = Constants.MSG_NETWORK_ERROR;
				handler.sendMessage(msg);
				return;
			}
			
			if (!running) 
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_PROPERTY_NOTIFICATION_FINISH;
			handler.sendMessage(msg);
			
			/*if (!running) 
				return;
			InformDetailActivity.setAllowGetPageAgain();*/
			
		} catch(Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = "+ex.getMessage());
			ex.getMessage();
			if (!running) 
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
		}
	}
}
