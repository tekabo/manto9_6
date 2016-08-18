package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.common.message.UmengMessageDeviceConfig;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;

public class SetActivity extends Activity {
    protected static final String TAG = SetActivity.class.getSimpleName();
    private LinearLayout set_modifypassword,set_btn_exit;
    private  TextView  set_check_update_version;
    private ImageView set_notify_btn_enable;
    private PushAgent mPushAgent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initWidget();
        mPushAgent = PushAgent.getInstance(getApplicationContext());
        mPushAgent.enable();
        //mPushAgent.onAppStart();
        String device_token = UmengRegistrar
                .getRegistrationId(getApplicationContext());
        Log.i("MyLog", "device_token=" + device_token);
        //开启推送并设置注册的回调处理
        mPushAgent.enable(mRegisterCallback);


    }

    public void initWidget(){
        //标题
        TextView topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_txt.setText("设置");
        Button backbutton = (Button) findViewById(R.id.topbar_left);
        backbutton.setVisibility(View.VISIBLE);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //修改密码
        set_modifypassword = (LinearLayout) findViewById(R.id.set_modifypassword);
        set_modifypassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(SetActivity.this,
                        FindPasswordActivity.class);
                startActivity(intent);

            }
        });
        //通知提醒
        set_notify_btn_enable = (ImageView) findViewById(R.id.set_notify_btn_enable);
        set_notify_btn_enable.setOnClickListener(clickListener);
        //检查更新
         String version = Util.getPackageVersion(getApplicationContext(),
                Constants.GET_PACKAGENAME(getApplicationContext()));
        if (version != null) {
            set_check_update_version =  (TextView)findViewById(R.id.set_check_update_version);
			/*if (Constants.URL.equals("http://dev.mantoto.com/")) {
				textVersion.setText("v"+version + "(测试版)");
			}else{*/
            set_check_update_version.setText("v"+version);
            //}
        }

        //退出按钮
        set_btn_exit = (LinearLayout) findViewById(R.id.set_btn_exit);
        set_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogouDialog();
            }
        });


    }

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==set_notify_btn_enable){
                switchPush();
            }
        }
    };
    private void switchPush(){
        if(set_notify_btn_enable.isClickable()){
            set_notify_btn_enable.setClickable(false);
            String info = String.format("enabled:%s  isRegistered:%s",
                    mPushAgent.isEnabled(), mPushAgent.isRegistered());
            Log.i(TAG, "switch Push:" + info);
            if (mPushAgent.isEnabled() || UmengRegistrar.isRegistered(SetActivity.this)) {
                //开启推送并设置注册的回调处理
                mPushAgent.disable(mUnregisterCallback);
            } else {
                //关闭推送并设置注销的回调处理
                mPushAgent.enable(mRegisterCallback);
            }
        }
    }

    protected void confirmLogouDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String strTitle = getResources().getString(R.string.txt_tips);
        String strOk = getResources().getString(R.string.txt_ok);
        String strCancel = getResources().getString(R.string.txt_cancel);

        builder.setTitle(strTitle);
        builder.setMessage("确认退出登录吗？");

        builder.setPositiveButton(strOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LocalStore.setIsVisitor(getApplicationContext(), true);
                Log.i("MyLog","UserCenterActivity visitor="+LocalStore.getIsVisitor(getApplicationContext()));
                LocalStore.logout(SetActivity.this);

                Intent intent = new Intent();
                intent.setClass(SetActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });

        builder.setNegativeButton(strCancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    public Handler handler = new Handler();

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

        @Override
        public void onRegistered(String registrationId) {
            // TODO Auto-generated method stub
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    updateStatus();
                }
            });
        }
    };

    private void updateStatus() {
        String pkgName = getApplicationContext().getPackageName();
        String info = String.format("enabled:%s\nisRegistered:%s\nDeviceToken:%s\n" +
                        "SdkVersion:%s\nAppVersionCode:%s\nAppVersionName:%s",
                mPushAgent.isEnabled(), mPushAgent.isRegistered(),
                mPushAgent.getRegistrationId(), MsgConstant.SDK_VERSION,
                UmengMessageDeviceConfig.getAppVersionCode(this), UmengMessageDeviceConfig.getAppVersionName(this));
        //tvStatus.setText("应用包名：" + pkgName + "\n" + info);

        set_notify_btn_enable.setImageResource(mPushAgent.isEnabled() ? R.drawable.open_button : R.drawable.close_button);
       // copyToClipBoard();

        Log.i(TAG, "updateStatus:" + String.format("enabled:%s  isRegistered:%s",
                mPushAgent.isEnabled(), mPushAgent.isRegistered()));
        Log.i(TAG, "=============================");
        set_notify_btn_enable.setClickable(true);
    }

    //此处是注销的回调处理
    //参考集成文档的1.7.10
    //http://dev.umeng.com/push/android/integration#1_7_10
    public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

        @Override
        public void onUnregistered(String registrationId) {
            // TODO Auto-generated method stub
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    updateStatus();
                }
            }, 2000);
        }
    };


}
