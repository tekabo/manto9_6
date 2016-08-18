package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetCanyinDetailThread;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShopGoodsActivity extends BaseActivity {
    private Button topbar_left, topbar_right;
    private TextView topbar_txt;
    private ScrollView mAllViewSv = null;
    private ProgressDialog mWaitLoading = null;
    private GetCanyinDetailThread mThread = null;
    private GridView shopGoodsList;
    private GetCanyinOwnListThread mOwnListThread = null;
    private long promotionId;
    private ArrayList<ImageView> productImgList = new ArrayList<ImageView>();
    private RemoteApi.LivingItem mProductDetail;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case Constants.MSG_GET_PRODUCT_DETAIL_FINISH:
                   if (mWaitLoading != null && mWaitLoading.isShowing()) {
                       mWaitLoading.dismiss();
                   }
                   if (mThread != null && mThread.getProductDetail() != null) {
                       mAllViewSv.setVisibility(View.VISIBLE);
                   }

                   break;
               case Constants.MSG_GET_PRODUCT_DETAIL_FAILD:
               case Constants.MSG_ADD_FAVORITE_FINISH:
                   if (mWaitLoading != null && mWaitLoading.isShowing()) {
                       mWaitLoading.dismiss();
                   }
                   RemoteApi.NetInfo mErrorInfo = (RemoteApi.NetInfo) msg.obj;
                   if (!"".equals(mErrorInfo.desc) && mErrorInfo.desc != null) {
                       Toast.makeText(ShopGoodsActivity.this,
                               mErrorInfo.desc, Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(ShopGoodsActivity.this,
                               "读取失败，可能网络问题或服务器无反应", Toast.LENGTH_SHORT).show();
                   }
                   break;
               case Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR:
                   if (mWaitLoading != null && mWaitLoading.isShowing()) {
                       mWaitLoading.dismiss();
                   }
                   mAllViewSv.setVisibility(View.GONE);
                   View view = (View) findViewById(R.id.view_network_error);
                   view.setVisibility(View.VISIBLE);
                   break;


               case Constants.MSG_GET_PRODUCT_FINISH:
                   if (mOwnListThread != null && mOwnListThread.getProductDetail() != null) {
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
                           params.width = display.getWidth() - 20;
                           if(serverImageWidth!=0)
                           params.height = (display.getWidth() - 20)
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
                                       LocalStore.setPromotionId(ShopGoodsActivity.this,promotion.PromotionID);
                                       Log.i("MyLog", "所有的活动商品信息-----PromotionID=" + promotion.PromotionID);
                                       Intent intent = new Intent(
                                               ShopGoodsActivity.this,
                                               CommitOrderActivity.class);
                                       Bundle bundle = new Bundle();
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
                               });

                           }

                           TextView canyin_desc = (TextView) v
                                   .findViewById(R.id.canyin_desc);
                           canyin_desc.setText(promotion.body);

                           if (true) {

                               shopGoodsList.addView(v);
                           }
                           Log.i("MyLog", "所有的活动商品信息-----PromotionID=" + list.promotionList.get(0).PromotionID);
                       }
                   }

                   break;


           }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_goods);
        setImmerseLayout(findViewById(R.id.common_back));

        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_right = (Button) findViewById(R.id.topbar_right);

        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topbar_txt.setVisibility(View.VISIBLE);
        topbar_txt.setText("商家商品");
        topbar_txt.setTextColor(Color.BLACK);

        topbar_right.setVisibility(View.GONE);

        mAllViewSv = (ScrollView) findViewById(R.id.all_view_sv);
        shopGoodsList = (GridView) findViewById(R.id.shop_goods_list);

    }

    private void showDialog() {
        mWaitLoading = new ProgressDialog(ShopGoodsActivity.this);
        String msg = getResources().getString(R.string.txt_loading);
        mWaitLoading.setMessage(msg);
        mWaitLoading.setCancelable(false);
        mWaitLoading.show();
    }
}
