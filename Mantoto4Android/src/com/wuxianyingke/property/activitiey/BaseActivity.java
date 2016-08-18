package com.wuxianyingke.property.activitiey;

import android.app.Activity;
import android.os.Bundle;

import com.wuxianyingke.property.remote.RemoteApi;

public abstract class BaseActivity extends Activity {
	protected Activity mContext;
@Override
protected void onCreate(Bundle savedInstanceState) {
	mContext=this;
	super.onCreate(savedInstanceState);
//	PushAgent.getInstance(getApplicationContext()).onAppStart();
	if (getContentViewResId() != 0) {
		setContentView(getContentViewResId());
	}
	
	initViews();
	initEvents();
	loadDatas();
}


protected final RemoteApi getRemoteApi() {
	return getRemoteApi();
}


private void onLoadDatas() {
}


protected void loadDatas() {
	
}


protected void initEvents() {
}


protected void initViews() {
	
}


protected abstract int getContentViewResId();

}
