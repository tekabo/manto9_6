package com.wuxianyingke.property.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/*import cn.smssdk.EventHandler;*/
/*import cn.smssdk.SMSSDK;*/

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RSAEncryptor;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.NetInfos;

public class FindPasswordActivity extends BaseActivity {
	private Button mTopbarLeft, Register2_GetPhoneCode, mRegisterButton;
	private TextView mTopbarTxt;
	private EditText Register2_edit1, Register2_editcode;
	private int i = 60;
	private String phoneNumber;
	private RSAEncryptor rsaEncryptor;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(FindPasswordActivity.this,
					LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		// 输入手机号
		try {
			rsaEncryptor = new RSAEncryptor(getAssets().open(
					"rsa_public_key.pem"), getAssets().open(
					"pkcs8_private_key.pem"));
			String encode = rsaEncryptor.encryptWithBase64("15045412899");
			Log.i("MyLog", "encode=" + encode);
			String decode = rsaEncryptor.decryptWithBase64(encode);
			Log.i("MyLog", "decode=" + decode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("MyLog", e.getMessage());
		}

		// 初始化布局控件
		initView();
	//	initSMSSDK();
		mTopbarLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FindPasswordActivity.this,
						UserCenterActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		// 清除内容
		Register2_edit1.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String hint;
				if (arg1) {
					hint = Register2_edit1.getHint().toString();
					Register2_edit1.setTag(hint);
					Register2_edit1.setHint("");
				} else {
					hint = Register2_edit1.getTag().toString();
					Register2_edit1.setHint(hint);
				}

			}
		});
		// 清除内容
		Register2_editcode
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						String hint;
						if (arg1) {
							hint = Register2_editcode.getHint().toString();
							Register2_editcode.setTag(hint);
							Register2_editcode.setHint("");
						} else {
							hint = Register2_editcode.getTag().toString();
							Register2_editcode.setHint(hint);
						}
					}
				});
		// 获取验证码
		Register2_GetPhoneCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Util.isEmpty(Register2_edit1)) {
					Toast.makeText(getApplicationContext(), "请输入手机号。",
							Toast.LENGTH_LONG).show();
				}
				phoneNumber = Register2_edit1.getText().toString();
				// 把按钮变成不可点击，并且显示倒计时（正在获取）
				Register2_GetPhoneCode.setClickable(false);
				Register2_GetPhoneCode.setText("重新发送(" + i + ")");
				Thread getVerificationCodeThread = new Thread() {
					@Override
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						NetInfos netInfo;
						try {
							Log.i("MyLog", "rsaEncryptor=code=");
							Log.i("MyLog",
									"rsaEncryptor=code="
											+ rsaEncryptor
													.encryptWithBase64(phoneNumber));
							netInfo = remote.getPhoneCode(
									FindPasswordActivity.this,
									rsaEncryptor.encryptWithBase64(phoneNumber),
									"mantutu",2);
							Log.i("MyLog",
									"rsaEncryptor=code="
											+ rsaEncryptor
													.encryptWithBase64(phoneNumber));
							if (netInfo == null) {
								handler.sendEmptyMessage(11);
							} else {
								if (netInfo.code == 200) {
									handler.sendEmptyMessage(10);
								} else {
									handler.sendEmptyMessage(11);
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};

				getVerificationCodeThread.start();

				new Thread(new Runnable() {
					@Override
					public void run() {
						for (; i > 0; i--) {
							handler.sendEmptyMessage(-9);
							if (i <= 0) {
								break;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						handler.sendEmptyMessage(-8);
					}
				}).start();
			}
		});

		// MOB 获取验证码
		/*
		 * Register2_GetPhoneCode.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { if
		 * (Util.isEmpty(Register2_edit1)) {
		 * Toast.makeText(getApplicationContext(), "请输入手机号。",
		 * Toast.LENGTH_LONG).show(); } else { //
		 * 下面的代码就是调用sdk的发送短信的方法，其中的“86”是官方中定义的，代表中国的意思 // 第二个参数表示的是需要发送短信的手机号
		 * SMSSDK.getVerificationCode("86", Register2_edit1.getText()
		 * .toString()); Log.i("MyLog", "phone=" +
		 * Register2_edit1.getText().toString());
		 * 
		 * // 把按钮变成不可点击，并且显示倒计时（正在获取）
		 * Register2_GetPhoneCode.setClickable(false);
		 * Register2_GetPhoneCode.setText("重新发送(" + i + ")"); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() { for (; i > 0; i--) {
		 * handler.sendEmptyMessage(-9); if (i <= 0) { break; } try {
		 * Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } } handler.sendEmptyMessage(-8); } }).start();
		 * 
		 * }
		 * 
		 * } });
		 */

		/**
		 * 提交验证码
		 */
		mRegisterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// SMSSDK.submitVerificationCode("86",
				// Register2_edit1.getText().toString(),
				// Register2_editcode.getText().toString());
				// TODO Auto-generated method stub
				/*
				 * Intent intent=new Intent();
				 * intent.setClass(FindPasswordActivity.this,
				 * ModifyPasswordActivity.class); startActivity(intent);
				 * finish();
				 */

				if (!TextUtils.isEmpty(Register2_editcode.getText().toString())) {
					Thread getVerifyCodeThread = new Thread() {
						@Override
						public void run() {
							RemoteApiImpl remote = new RemoteApiImpl();
							NetInfos netInfo = remote.getVarificationCode(
									FindPasswordActivity.this, phoneNumber,
									Register2_editcode.getText().toString());
							if (netInfo.bSuccess) {
								Log.i("MyLog", "验证成功");
								// startRegistrNormal();
								Intent intent = new Intent();
								intent.setClass(FindPasswordActivity.this,
										ModifyPasswordActivity.class);
								startActivity(intent);
								finish();
							} else {
								handler.sendEmptyMessage(12);
							}
						}
					};
					getVerifyCodeThread.start();
				} else {
					Toast.makeText(FindPasswordActivity.this, "验证码不能为空", Toast.LENGTH_SHORT)
							.show();
				}

				Log.i("MyLog", "phonenumber=" + phoneNumber + "---code="
						+ Register2_editcode.getText().toString());
				// startRegistrNormal();

			}
		});
	}



	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				Register2_GetPhoneCode.setText("重新发送(" + i + ")");
			} else if (msg.what == -8) {
				Register2_GetPhoneCode.setText("获取验证码");
				Register2_GetPhoneCode.setClickable(true);
				i = 60;
			}  else if (msg.what == 10) {
				Toast.makeText(getApplicationContext(), "验证码已经发送",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 11) {
				Toast.makeText(getApplicationContext(), "验证码发送失败",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 12) {
				Toast.makeText(getApplicationContext(), "验证码错误",
						Toast.LENGTH_SHORT).show();
			} /*else {
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				Log.e("event", "event=" + event);
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
						Toast.makeText(getApplicationContext(), "提交验证码成功",
								Toast.LENGTH_SHORT).show();

						Intent intent = new Intent(FindPasswordActivity.this,
								ModifyPasswordActivity.class);
						intent.putExtra("phone", Register2_edit1.getText()
								.toString());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(getApplicationContext(), "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					} else {
						((Throwable) data).printStackTrace();
					}
				}
			}*/
		}
	};

	// 初始化布局控件
	private void initView() {
		mTopbarLeft = (Button) findViewById(R.id.topbar_left);
		mTopbarLeft.setVisibility(View.VISIBLE);
		mTopbarTxt = (TextView) findViewById(R.id.topbar_txt);
		mTopbarTxt.setText("找回密码");

		Register2_edit1 = (EditText) findViewById(R.id.Register2_edit1);
		Register2_editcode = (EditText) findViewById(R.id.Register2_editCode);
		Register2_GetPhoneCode = (Button) findViewById(R.id.Register2_Button_GetPhoneCode);

		mRegisterButton = (Button) findViewById(R.id.Register2_btn);
	}

	@Override
	protected void onDestroy() {
		// SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

}