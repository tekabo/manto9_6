package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.ImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.RepairPicture;
import com.wuxianyingke.property.threads.GetRepairInfoThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;


public class RepairInfoActivity extends BaseActivity {

    private MyGallery mMyGallery = null;
    private IndicationDotList mDotList = null;

    private ImageAdapter mAdapter = null ;
    private View mImgView = null;
    private ProgressDialog mWaitLoading = null ;
    private int propertyid,screenWidth,screenHeigh;
    private ScrollView mAllViewSv = null ;
    ImageView cartImageview;
    private Button topbar_left;
    private TextView topbar_txt ,repair_desc ,repair_ctime;
    private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();
    private float latitude,longitude;
    private long repairId,userid;
    private String mRepairBody , mRepairCTime,mRepairPhone,mRepairLogTitle;
    private TextView mRepairBodyTextView, mRepairCTimeTextView,mRepairPhoneTextView,mRepairLogTitleTextView;
    private ArrayList<RepairPicture> repairPicList;

    private GetRepairInfoThread mThread;


    private LinearLayout repairInfo,textNoImage,imageLayout;


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR:
                    if(mWaitLoading != null && mWaitLoading.isShowing()){
                        mWaitLoading.dismiss() ;
                    }
                    mAllViewSv.setVisibility(View.GONE) ;
                    View view = (View) findViewById(R.id.view_network_error);
                    view.setVisibility(View.VISIBLE);
                    break ;
                case Constants.MSG_GET_REPAIR_PIC_LIST_FINSH:

                    if(mThread != null && mThread.getRepairImgItem() != null){
                        repairPicList = mThread.getRepairImgItem();
                        for(int i = 0; i<repairPicList.size();i++)
                        {
                            RepairPicture repairPic = repairPicList.get(i);

                            LogUtil.d("path=",repairPic.path);

//                            View v = getLayoutInflater().inflate(R.layout.repair_image_content, null);
//                            ImageView canyinImg = (ImageView) v.findViewById(R.id.canyinImg);
//                            canyinImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                            canyinImg.setVisibility(View.GONE);
//                            activityImgList.add(canyinImg);
//                            repairInfo.addView(v);
                        }
                    }
                    break;
                case Constants.MSG_GET_REPAIR_PIC_FINSH:
                {
                	if (msg.arg1>=0) {
						imageLayout.setVisibility(View.VISIBLE);
						textNoImage.setVisibility(View.GONE);
					}
                	View v = getLayoutInflater().inflate(R.layout.repair_image_content, null);
                    ImageView canyinImg = (ImageView) v.findViewById(R.id.canyinImg);
                    
                    Bitmap repair_img = mThread.getBitmap(msg.arg2);
                    canyinImg.setImageBitmap(repair_img);
                  
//                    int imgHeight = repair_img.getHeight();
//                    int imgWidth = repair_img.getWidth();
//
//                    float scalew = (float) screenWidth / (float) imgWidth;  
//                    canyinImg.setScaleType(ScaleType.MATRIX);  
//                    Matrix matrix = new Matrix();  
//                    matrix.postScale(scalew, scalew); 
//                    canyinImg.setMaxWidth(screenWidth);  
//                    float ss = screenHeigh > imgHeight ? screenHeigh : imgHeight;  
//                    canyinImg.setMaxWidth((int) ss);  

                    if (repair_img != null && repair_img.isRecycled()) {  
                    	repair_img.recycle();  
                    }  
                    
                    
                    
                    activityImgList.add(canyinImg);
                    repairInfo.addView(v);
                }
                break;
            }
        }
    } ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        PushAgent.getInstance(getApplicationContext()).onAppStart();
        setContentView(R.layout.repair_info) ;
        setImmerseLayout(findViewById(R.id.common_back));
        SharedPreferences saving = this.getSharedPreferences(
                LocalStore.USER_INFO, 0);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
        
//        propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
        propertyid=LocalStore.getUserInfo().PropertyID;
        topbar_txt= (TextView) findViewById(R.id.topbar_txt) ;
        topbar_left=(Button)findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        topbar_txt.setText("报修详情");
        topbar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        repairId =(long) bundle.getLong("repairId");

        mRepairBody = bundle.getString("repairDesc");
        mRepairCTime = bundle.getString("repairCTime");
        mRepairPhone=bundle.getString("phone");
        mRepairLogTitle=bundle.getString("repairLogTitle");
        repair_desc = (TextView)findViewById(R.id.repair_desc);
//        repair_ctime = (TextView)findViewById(R.id.repair_ctime);
        repair_desc.setText(mRepairBody);
//        repair_ctime.setText(mRepairCTime);
        mRepairCTimeTextView=(TextView) findViewById(R.id.RepairTimeTextView);
        mRepairPhoneTextView=(TextView) findViewById(R.id.RepairPhoneTextView);
        mRepairLogTitleTextView=(TextView) findViewById(R.id.RepairTypeTextView);
        mRepairCTimeTextView.setText(mRepairCTime);
        mRepairPhoneTextView.setText(mRepairPhone);
        mRepairLogTitleTextView.setText(mRepairLogTitle);
        repairInfo = (LinearLayout)findViewById(R.id.repair_info);
        textNoImage=(LinearLayout) findViewById(R.id.ll_textNoimage);
        imageLayout=(LinearLayout) findViewById(R.id.ll_imageId);
        textNoImage.setVisibility(View.VISIBLE);
        changeTo(1);
        userid = LocalStore.getUserInfo().userId;
        mThread = new GetRepairInfoThread(this,mHandler,propertyid,repairId,userid,screenWidth,screenHeigh);
        mThread.start() ;

    }
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void changeTo(int i)
    {
        repairInfo.setVisibility(View.VISIBLE);
    }

    private void showDialog(){
        mWaitLoading = new ProgressDialog(RepairInfoActivity.this);
        String msg = getResources().getString(R.string.txt_loading) ;
        mWaitLoading.setMessage(msg);
        mWaitLoading.setCancelable(false);
        mWaitLoading.show();
    }

}
