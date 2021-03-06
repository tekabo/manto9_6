package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.GoodsRecycleAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GoodsCauponListActivity extends BaseActivity {
    private Button topbarLeft;
    private TextView topbarTxt;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mlayoutManager;
    private GoodsRecycleAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;

    private GetCanyinOwnListThread mOwnListThread = null;
    private ArrayList<ImageView> productImgList = new ArrayList<ImageView>();
    private int mLivingItemID = 0;
    private long promotionId;
    private int promotionType = 1;//1、商品 2、活动 3、团购 4、商家券

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_GET_PRODUCT_FINISH:
                    if (mOwnListThread != null && mOwnListThread.getProductDetail() != null) {
//                        mAllViewSv.setVisibility(View.VISIBLE);
                        RemoteApi.PromotionList list = mOwnListThread.getProductDetail();

                        for (int i = 0; i < list.promotionList.size(); i++) {
                            final RemoteApi.Promotion promotion = list.promotionList.get(i);
                            promotionId=list.promotionList.get(i).PromotionID;

                            View v = getLayoutInflater().inflate(
                                    R.layout.canyin_detail_own_content, null);

                            Log.i("MyTag",
                                    " promotion = " + promotionId);
                            int serverImageWidth = promotion.Width;
                            int serverImageHeight = promotion.Height;

                            ImageView canyinImg = (ImageView) v
                                    .findViewById(R.id.canyinImg);
                            canyinImg.setScaleType(ImageView.ScaleType.FIT_XY);
                            canyinImg.setAdjustViewBounds(true);

                            Display display = getWindowManager()
                                    .getDefaultDisplay();
                            ViewGroup.LayoutParams params = canyinImg.getLayoutParams();
                            params.width = display.getWidth() -10;
                            params.height = (display.getHeight() - 10)
                                    * serverImageHeight / serverImageWidth;


                            canyinImg.setLayoutParams(params);
                            canyinImg.setVisibility(View.GONE);
                            productImgList.add(canyinImg);

                            TextView canyin_title = (TextView) v
                                    .findViewById(R.id.canyin_title);
                            canyin_title.setText(promotion.header);

                            TextView canyin_price = (TextView) v
                                    .findViewById(R.id.canyin_price);
                            Button goumai = (Button) v.findViewById(R.id.goumaiImg);
                            if (promotion.ForSal) {
                                DecimalFormat df = new DecimalFormat("0.00");
                                Log.i("MyLog", "promotion.Price=" + promotion.Price);
                                canyin_price
                                        .setText("￥ " + df.format(promotion.Price));
                                goumai.setVisibility(View.VISIBLE);
                                goumai.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (LocalStore
                                                .getIsVisitor(getApplicationContext())){
                                            Toast.makeText(getApplicationContext(),
                                                    "游客或者未认证用户无法进行购买,请注册！",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(
                                                    GoodsCauponListActivity.this,
                                                    LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            LocalStore.setPromotionId(GoodsCauponListActivity.this,promotion.PromotionID);
                                            Log.i("MyLog", "所有的活动商品信息-----PromotionID=" + promotion.PromotionID);
                                            Intent intent = new Intent(
                                                    GoodsCauponListActivity.this,
                                                    CommitOrderActivity.class);
                                            Bundle bundle = new Bundle();//向目标页面传递数据
                                            bundle.putDouble("price",
                                                    promotion.Price);
                                            bundle.putString("name",
                                                    promotion.header);
                                            bundle.putLong("promotionid",
                                                    promotion.PromotionID);
                                            bundle.putInt("SaleTypeId", promotion.SaleTypeID);
                                            Log.i("MyLog", "当前的SaleTypeId的标记为：----promotion:"+promotion.SaleTypeID);

                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            Log.i("MyLog",
                                                    "promotionid------"+ promotion.PromotionID);
                                        }
                                    }
                                });

                            }

                            TextView canyin_desc = (TextView) v
                                    .findViewById(R.id.canyin_desc);
                            canyin_desc.setText(promotion.body);

                            if (true) {

//                                shopGoodsList.addView(v);
                            }
                            Log.i("MyLog", "所有的活动商品信息-----PromotionID=" + list.promotionList.get(0).PromotionID);
                        }
                    }
                    break;
                case Constants.MSG_GET_PRODUCT_IMG_FINISH://22-GetCanyinOwnListThread
                {
                    ImageView canyinImg = productImgList.get(msg.arg1);
                    ViewGroup.LayoutParams pr = canyinImg.getLayoutParams();
                    canyinImg
                            .setImageDrawable(mOwnListThread.getDrawable(msg.arg2));
                    canyinImg.setLayoutParams(pr);
                    canyinImg.setVisibility(View.VISIBLE);
                }
                break;

            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_caupon_list);
        initView();
        if(savedInstanceState!=null){
            mLivingItemID = savedInstanceState.getInt("mLivingItemID");
        }else {
            mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);
        }
        mOwnListThread = new GetCanyinOwnListThread(this,
                mHandler,mLivingItemID);
        mOwnListThread.start();


    }

    private void initView(){
       // setImmerseLayout(findViewById(R.id.common_back));
        topbarTxt = (TextView) findViewById(R.id.topbar_txt);
        topbarTxt.setText("商家劵");
        topbarTxt.setVisibility(View.VISIBLE);
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarLeft.setVisibility(View.VISIBLE);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.goodsRecyclerView);
        mlayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this,3);
        mlayoutManager.setOrientation(LinearLayout.VERTICAL);

        if(promotionType == 1){
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else{

            mRecyclerView.setLayoutManager(mlayoutManager);
        }
    }
}
