package com.wuxianyingke.property.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;

public class BalenceActivity extends BaseActivity {
	private Button topbar_left,cutwifi;
	private WifiManager wifiManager;
	private TextView topbar_text;
	private String[] results;
	private TextView presentWifiTime;//赠送时长
	private TextView rechargeWifiTime;//上网时长
	private TextView rechargeMoney;//账号金额
	private ProgressDialog mDialog;
	private LinearLayout balaenceLinearLayout;
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
				mDialog.dismiss();
				String leftTime;
				String leftMoney;
				String result=msg.getData().getString("value");
				try {
					JSONObject object = new JSONObject(result);
					JSONArray array = object.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						leftTime = obj.getString("lefttime");
						leftMoney = obj.getString("leftmoney");
						Log.i("MyLog", "Money = "+Double.valueOf(leftMoney));
						rechargeWifiTime.setText(""+(int)(Double.valueOf(leftMoney)*100+Double.valueOf(leftTime))/60+"小时"+String.format("%.0f", (Double.valueOf(leftMoney)*100+Double.valueOf(leftTime))%60)+"分");
						rechargeMoney.setText(leftMoney+"元");
						presentWifiTime.setText(leftTime+"分");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				/*String result=msg.getData().getString("value");
				results = result.split("\t");//根据"/t"区分
				rechargeMoney.setText(results[4]+"元");
				String times = results[4];
				rechargeWifiTime.setText(""+Long.parseLong(times.replace(".", ""))/60+"小时"+Long.parseLong(times.replace(".", ""))%60+"分钟");
				Log.i("MyLog", "results=="+results[0]);
				Log.i("MyLog", "results="+results[0]+results[1]+results[2]+results[3]+results[4]+results[5]+results[6]+results[7]+results[8]);*/
		
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balence);
		mDialog = new ProgressDialog(BalenceActivity.this);
		mDialog.setMessage("加载中......");
		mDialog.setCanceledOnTouchOutside(true);
		 /* mDialog = ProgressDialog.show(BalenceActivity.this, null, "加载中......",
                  true);*/
		mDialog.show();
		
		
		//添加点
		cutwifi = (Button) findViewById(R.id.cutwifi);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);//获取WifiManager
		cutwifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 
					wifiManager.setWifiEnabled(false);  
					/*开启、关闭wifiif (wifiManager.isWifiEnabled()) {  
						wifiManager.setWifiEnabled(false);  
						} else {  
						wifiManager.setWifiEnabled(true);  
						}*/
				
			}
			
		});
		
		
		
		rechargeWifiTime = (TextView) findViewById(R.id.rechargeWifiTime);
		presentWifiTime = (TextView) findViewById(R.id.presentWifiTime);
		rechargeMoney = (TextView) findViewById(R.id.rechargeMoneyTextView);
		balaenceLinearLayout = (LinearLayout) findViewById(R.id.balaenceLinearLayout);
		balaenceLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(BalenceActivity.this, RechargeActivity.class);
				startActivity(intent);
			}
		});
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_text = (TextView) findViewById(R.id.topbar_txt);
		topbar_text.setText("WIFI");
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		setImmerseLayout(findViewById(R.id.common_back));
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				String datsNew = "S07"+LocalStore.getFreeWifi().WIFIAccount;
				Log.i("MyLog", "datasNew = "+datsNew);
				String dataBase = Base64.encodeToString(datsNew.getBytes(), Base64.NO_WRAP);
				Log.i("MyLog", "dataBase = "+dataBase);
				
				String result = "";
				String str_data = "";
/*
				String data = "091" + LocalStore.getFreeWifi().WIFIAccount;

				String datas = Base64.encodeToString(data.getBytes(),
						Base64.NO_WRAP);
				Log.i("MyLog", "datas2==" + datas+"data="+ data);

				String url = LocalStore.getWifiApUrl(BalenceActivity.this)
						+ datas;*/
				String url = LocalStore.getWifiApUrl(getApplicationContext())+dataBase;
				Log.i("MyLog", "url = "+url);
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse response = new DefaultHttpClient().execute(httpGet);
					result = getResponseResult(response);
					
					str_data = str_data + "第二次请求返回结果: " + result;
					Log.i("MyLog", "断开返回result===" + result);
					Message msg = new Message();
					Bundle data2 = new Bundle();
//					data2.putCharArray("value", results);
					data2.putString("value", result);
					msg.setData(data2);
					handler2.sendMessage(msg);
				} catch (Exception e) {

					/*String err = e.toString();
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("value", err);
					msg.setData(data);*/
//					handler.sendMessage(msg);

					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}
	

	/**
	 * 显示响应结果到命令行和TextView
	 * 
	 * @param response
	 */
	private String getResponseResult(HttpResponse response) {
		if (null == response) {
			return "";
		}
		String result = "";
		HttpEntity httpEntity = response.getEntity();
		try {
			InputStream inputStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

			String line = "";
			while (null != (line = reader.readLine())) {
				result += line;

			}

			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
