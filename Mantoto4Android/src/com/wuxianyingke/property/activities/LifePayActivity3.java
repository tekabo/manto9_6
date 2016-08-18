package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.LifePayAdapter;

/**
 * 生活缴费
*/
public class LifePayActivity3 extends BaseActivity implements OnClickListener {
	/**左侧导航*/
	private Button topbarLeft;
	/**标题*/
	private TextView topbarText;
	/**内容列表*/
	private ListView mListView;
	private LifePayAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_pay);
		//初始化布局控件
		initView();
		
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		
	}
//初始化布局控件
	private void initView() {
		topbarLeft=(Button) findViewById(R.id.topbar_left);
		topbarText=(TextView) findViewById(R.id.topbar_txt);
		mListView=(ListView) findViewById(R.id.LifePay_ListView);
		
		topbarLeft.setVisibility(View.VISIBLE);
		topbarText.setVisibility(View.VISIBLE);
		topbarText.setText("生活缴费");
		
		topbarLeft.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		Intent intent=new Intent();
		switch (view.getId()) {
		case R.id.topbar_left:
			intent.setClass(LifePayActivity3.this, LifePayContentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		
	}
	}
