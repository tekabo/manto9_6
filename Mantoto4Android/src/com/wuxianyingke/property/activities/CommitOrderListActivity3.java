package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.fragment.CompleteOrderFragment;
import com.wuxianyingke.property.fragment.UnCompleteOrderFragment;
import com.wuxianyingke.property.fragment.UnCompleteOrderFragment2;
import com.wuxianyingke.property.fragment.UnCompleteOrderFragment3;


public class CommitOrderListActivity3 extends FragmentActivity {
	// 顶部导航
	private TextView topbar_txt;
	private Button topbar_left;
	private int favorite_flag;
	private Button completedBtn, uncompleteBtn;
	/**viewpager*/
	private ViewPager viewPager;
	/**碎片集合*/
	private List<Fragment> fragments;

	private LinearLayout titleLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_myentity);

		// 初始化控件
		initViews();
		// 初始化fragments
		initFragments();

		// 实例化FragmentPagerAdapter，并设置到ViewPager中
		viewPager
				.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
		

		// 设置每一个模块标题的点击事件
		setTitleEvent();

		// 初始化事件监听
		initListener();
		
		//设置ViewPager的滑动事件监听器
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				select(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}


	private void initListener() {
		// 左侧返回按钮事件监听
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (0 != favorite_flag) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getApplicationContext(),
							UserCenterActivity.class);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});

	}


	private void initViews() {
//		viewPager = (ViewPager) findViewById(R.id.viewPagerId);
		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("我的订单");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		// 基础
		uncompleteBtn = (Button) findViewById(R.id.btn_UncompletedId);// 未完成
		completedBtn = (Button) findViewById(R.id.btn_CompletedId);// 已完成
		uncompleteBtn.setTextColor(Color.parseColor("#00b1ff"));
		completedBtn.setBackgroundResource(R.drawable.switch_button_right_on);// 初始化时将完成背景设置成蓝色

	}

	// 设置每一个模块标题的点击事件
	private void setTitleEvent() {
		View view = null;
		titleLayout = (LinearLayout) findViewById(R.id.titleLayoutId);

		for (int i = 0, len = titleLayout.getChildCount(); i < len; i++) {
			view = titleLayout.getChildAt(i); // 获取容器控件中指定位置的子控件
			view.setTag(i);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					viewPager.setCurrentItem(position);

					select(position);
				}
			});
		}
	}

	// 选择某一标题，设置其字体颜色为红色
	private void select(int position) {
		for (int i = 0, len = titleLayout.getChildCount(); i < len; i++) {
			completedBtn = (Button) titleLayout.getChildAt(i);
			if (i == position) {
				uncompleteBtn.setTextColor(Color.parseColor("#ffffff"));
				completedBtn.setTextColor(Color.parseColor("#00b1ff"));
				uncompleteBtn
						.setBackgroundResource(R.drawable.switch_button_left_on);
				completedBtn
						.setBackgroundResource(R.drawable.switch_button_right_default);
			} else {
				uncompleteBtn.setTextColor(Color.parseColor("#00b1ff"));
				completedBtn.setTextColor(Color.parseColor("#ffffff"));
				uncompleteBtn
						.setBackgroundResource(R.drawable.switch_button_left_default);
				completedBtn
						.setBackgroundResource(R.drawable.switch_button_right_on);
			}
		}
	}

	private void initFragments() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new UnCompleteOrderFragment());
		fragments.add(new CompleteOrderFragment());
	}

	class MyFragmentAdapter extends FragmentStatePagerAdapter {
		public MyFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}
	}
}
