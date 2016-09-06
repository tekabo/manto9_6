package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.TestAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

import java.util.ArrayList;

public class TestActivity extends Activity {
    private Button topLeft;
    private GridView girdView;
    private TestAdapter testAdapter;
    private int mLivingItemID = 0;
    private TextView topTxt;

    private GetCanyinOwnListThread mOwnListThread = null;
    private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();
    private ArrayList<ImageView> productImgList = new ArrayList<ImageView>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_GET_PRODUCT_FINISH:
                    if (mOwnListThread != null && mOwnListThread.getProductDetail() != null) {
                        testAdapter = new TestAdapter(getApplicationContext(),
                                mOwnListThread.getProductDetail().promotionList);
                        girdView.setAdapter(testAdapter);
                        testAdapter.notifyDataSetChanged();
                    }
                        break;

            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initWidget();

        if(savedInstanceState != null){
            mLivingItemID=savedInstanceState.getInt("mLivingItemID");

        }else{
            mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);

        }

        mOwnListThread = new GetCanyinOwnListThread(this, mHandler,
                mLivingItemID);
        mOwnListThread.start();

    }

    private void initWidget() {
        topLeft = (Button) findViewById(R.id.topbar_left);
        topTxt= (TextView) findViewById(R.id.topbar_txt);
        topTxt.setVisibility(View.VISIBLE);
        topTxt.setText("商家商品");
        topLeft.setVisibility(View.VISIBLE);
        topLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        girdView = (GridView) findViewById(R.id.testlist);
        girdView.setNumColumns(2);
    }
}
