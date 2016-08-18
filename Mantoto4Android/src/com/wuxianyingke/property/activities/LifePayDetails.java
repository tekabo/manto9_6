package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;

public class LifePayDetails extends BaseActivity {
	
	private TextView topbarText;
	private Button topbarLeft,reBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_life_success);
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		topbarText=(TextView) findViewById(R.id.topbar_txt);
		topbarText.setVisibility(View.VISIBLE);
		topbarText.setText("缴费详情");
		topbarLeft=(Button) findViewById(R.id.topbar_left);
		topbarLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent();
				intent.setClass(LifePayDetails.this, LifePayActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		
		reBack=(Button) findViewById(R.id.LifePayBackButton);
		reBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(LifePayDetails.this, LifePayActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
	}

}
