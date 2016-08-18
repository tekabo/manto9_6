package com.wuxianyingke.property.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.mantoto.property.R;
import com.wuxianying.alipay.sdk.pay.PayResult;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.LifePay;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApiImpl;

/**
 * 生活缴费详情
*/
public class LifePayContentActivity extends BaseActivity implements OnClickListener {
	// 支付宝 初始化信息
	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++++++++++++++
	 */
	// 商户PID
	public static final String PARTNER = "";
	// 商户收款账号
	public static final String SELLER = "";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";
	private static final int SDK_PAY_FLAG = 1;
	/** 左侧导航,查询，支付 */
	private Button topbarLeft, LifePayFind, LifePayCommit;
	/** 标题 */
	private TextView topbarText, LifePayDescRiption;
	/** 客户账号，编码类型，缴费状态，缴费账期，用户姓名，宽带地址，缴费金额 */
	private EditText LifePayNumber, LifePayType, LifePayState, LifePayDate,
			LifePayName, LifePayAddress, LifePayMoney, LifePayMonth,
			LifePayDueTime;
	private ProgressDialog mProgressBar = null;
	/** 返回错误信息 */
	private String mErrorText;
	private String payType, payStatus, payDate, payName, payAddress;
	private double payMoney;
	private LifePay lifePay = null;
	private OrderItem orders = new OrderItem();
	/** 阿里支付订单信息 */
	private String aliOrderStr;
	/** 订单状态 */
	private int billStatusID;
	private int priority;
	private long promotionid;
	private long billId;
	private int promotionNumber;
	/**
	 * 缴费类型的数据
	 */
	private String[] header;
	private EditText editText ;
	private boolean isFlag=true;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch ((msg.what > 200 && msg.what < 999) ? 500 : msg.what) {
			// 查询失败
			case 500:
				Toast.makeText(LifePayContentActivity.this, mErrorText,
						Toast.LENGTH_SHORT).show();
				Log.i("MyLog", "mErrorText=" + mErrorText);
				break;
			// 查询成功
			case 200:
				billStatusID = lifePay.TheBill.Status.BillStatusID;
				Log.i("MyLog", "LifePay-billStatusId=" + billStatusID);
				if  (billStatusID ==3) {
					LifePayType.setEnabled(false);
					LifePayMonth.setEnabled(false);
					findBill();
					LifePayCommit.setVisibility(View.GONE);
				}else if (billStatusID==2) {
					findBill();
					LifePayCommit.setVisibility(View.VISIBLE);
					//选择类型和月份
					lifePayTypeAndMonth();
//					setData();
				}else if (billStatusID==1) {
					//选择类型和月份
					lifePayTypeAndMonth();
					setData();
					// 缴费账期
//					SimpleDateFormat sdf = new SimpleDateFormat(
//							"yyyy-MM-dd");
//					String bTime = sdf
//							.format(new Date(Long.parseLong(payDate)));
//					LifePayDate.setHint(bTime);
//					// 到期时间
//					String dueTime = lifePay.TheBill.ETime;
//					Log.i("MyLog", "Lifepay-dueTime=" + dueTime);
//					Log.i("MyLog", "Lifepay-eTime=" + payDate);
//					String eTime = sdf
//							.format(new Date(Long.parseLong(dueTime)));
//					LifePayDueTime.setHint(eTime);
				}else {
					Toast.makeText(getApplicationContext(), "订单已作废或已取消", Toast.LENGTH_SHORT).show();
				}
				break;

			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
				Intent intent = new Intent();
				intent.setClass(LifePayContentActivity.this,
						LifePayDetails.class);
				startActivity(intent);
				finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(LifePayContentActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(LifePayContentActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;

			case 3:
				final String payInfo = aliOrderStr;
				Runnable payRunnable = new Runnable() {
					@Override
					public void run() {
						// 构造PayTask 对象
						PayTask alipay = new PayTask(
								LifePayContentActivity.this);
						// 调用支付接口，获取支付结果
						String result = alipay.pay(payInfo,true);

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};
				// 必须异步调用
				Thread payThread = new Thread(payRunnable);
				payThread.start();
				break;
			case 4:
				Toast.makeText(LifePayContentActivity.this, "请选择缴费类型", Toast.LENGTH_SHORT).show();
				break;
			// 通讯错误
			default:
				Toast.makeText(LifePayContentActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}

		private void setData() {
			LifePayState.setHint(payStatus);
			LifePayName.setHint(payName);
			LifePayAddress.setHint(payAddress);
			Date currentDate=new Date(System.currentTimeMillis());
			String dueTime = lifePay.TheBill.ETime;
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd");
			String eTime = sdf
					.format(new Date((Long.parseLong(dueTime))));
			if (System.currentTimeMillis()<=Long.parseLong(dueTime)) {
				Log.i("MyLog", "System.currentTimeMillis()="+System.currentTimeMillis()+"="+Long.parseLong(dueTime));
				LifePayDueTime.setHint(eTime);
				Calendar cd=Calendar.getInstance();
				SimpleDateFormat sdf2 = new SimpleDateFormat(
						"yyyy-MM-dd");
				cd.setTime(new Date((Long.parseLong(dueTime))));
				cd.add(Calendar.MONTH, priority);
				LifePayDate.setHint(eTime+"到"+sdf.format(cd.getTime()));
			}else {
				Calendar cd2=Calendar.getInstance();
				cd2.setTime(currentDate);
				
				cd2.add(Calendar.MONTH, priority);
				LifePayDate.setHint(sdf.format(currentDate)+"到"+sdf.format(cd2.getTime()));
				LifePayDueTime.setHint(sdf.format(cd2.getTime()));
			}
			LifePayCommit.setVisibility(View.VISIBLE);
			priority = lifePay.promotionArray.get(0).Priority;
			LifePayMoney.setHint(""
					+ lifePay.promotionArray.get(0).Price
					+ "元");
			LifePayMoney
			.setHintTextColor(getResources()
					.getColor(
							R.color.red));
		}

		private void lifePayTypeAndMonth() {
			LifePayType.setEnabled(true);
			LifePayMonth.setEnabled(true);
			LifePayMonth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (isFlag) {
						Toast.makeText(getApplicationContext(), "请选择缴费类型", Toast.LENGTH_SHORT).show();
					}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LifePayContentActivity.this);
					editText = new EditText(
							LifePayContentActivity.this);
					builder.setTitle("请输入1~12")
							.setView(editText)
							.setPositiveButton(
									"确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0,
												int arg1) {
											if (0 < Long
													.parseLong(""
															+ editText
																	.getText()
																	.toString())
													&& Long.parseLong(""
															+ editText
																	.getText()
																	.toString()) < 12) {
												LifePayMoney.setHint(""
														+ payMoney
														* Long.parseLong(editText
																.getText()
																.toString())
														+ "元");
												LifePayMonth
														.setHint(editText
																.getText()
																.toString()
																+ "个月");
												LifePayMoney
														.setHintTextColor(getResources()
																.getColor(
																		R.color.red));
												promotionNumber=Integer.parseInt(editText
														.getText()
														.toString());
												Log.i("MyLog", "System.currentTimeMillis()=111111111111111111111111=promotionNumber="+promotionNumber);
												Date currentDate=new Date(System.currentTimeMillis());
												String dueTime = lifePay.TheBill.ETime;
												SimpleDateFormat sdf = new SimpleDateFormat(
														"yyyy-MM-dd");
												String eTime = sdf
														.format(new Date((Long.parseLong(dueTime))));
												if (System.currentTimeMillis()<=Long.parseLong(dueTime)) {
													Log.i("MyLog", "System.currentTimeMillis()="+System.currentTimeMillis()+"="+Long.parseLong(dueTime));
													Log.i("MyLog", "System.currentTimeMillis()=111111111111111111111111");
													LifePayDueTime.setHint(eTime);
													Calendar cd=Calendar.getInstance();
													SimpleDateFormat sdf2 = new SimpleDateFormat(
															"yyyy-MM-dd");
													cd.setTime(new Date((Long.parseLong(dueTime))));
													cd.add(Calendar.MONTH,Integer.parseInt(editText
															.getText()
															.toString()));
													LifePayDate.setHint(eTime+"到"+sdf2.format(cd.getTime()));
												}else {
													Calendar cd2=Calendar.getInstance();
													cd2.setTime(currentDate);
													
													cd2.add(Calendar.MONTH, Integer.parseInt(editText
															.getText()
															.toString()));
													LifePayDate.setHint(sdf.format(currentDate)+"到"+sdf.format(cd2.getTime()));
													LifePayDueTime.setHint(sdf.format(cd2.getTime()));
												}	

											} else {
												Toast.makeText(
														getApplicationContext(),
														"请输入1~12的数字", Toast.LENGTH_SHORT)
														.show();
											}

										}
									}).setNegativeButton("取消", null)
							.show();
					LifePayCommit.setVisibility(View.VISIBLE);
				} 
			}
			});
			// 缴费类型
			LifePayType.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LifePayContentActivity.this);
					
					builder.setItems(header,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int witch) {
									if (witch == 0) {
										isFlag=false;
//												LifePayType.setHint("包月");
//												promotionNumber=lifePay.promotionArray
//														.get(witch).Priority;
										payType = lifePay.promotionArray
												.get(witch).header;
										LifePayType.setHint(payType);
										payMoney = lifePay.promotionArray
												.get(witch).Price;
										LifePayMoney.setHint("");
										LifePayMonth
										.setHint("请输入您要使用多少个月");
										Toast.makeText(
												getApplicationContext(),
												"请选择包月周期", Toast.LENGTH_SHORT).show();
										promotionid=lifePay.promotionArray.get(witch).PromotionID;
										Log.i("MyLog","promotionid="+promotionid);
										LifePayMonth.setEnabled(true);
									} else if (witch == 1) {
										
										payType = lifePay.promotionArray
												.get(witch).header;
										LifePayType.setHint(payType);
										LifePayMonth
												.setHint("12个月");
										LifePayMonth.setEnabled(false);
										payType = lifePay.promotionArray
												.get(witch).header;
										payMoney = lifePay.promotionArray
												.get(witch).Price;
										LifePayMoney.setHint(""
												+ payMoney + "元");
										LifePayMoney
												.setHintTextColor(getResources()
														.getColor(
																R.color.red));
										promotionid=lifePay.promotionArray.get(witch).PromotionID;
										Log.i("MyLog","promotionid="+promotionid);
										Date currentDate=new Date(System.currentTimeMillis());
										String dueTime = lifePay.TheBill.ETime;
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd");
										String eTime = sdf
												.format(new Date((Long.parseLong(dueTime))));
										if (System.currentTimeMillis()<=Long.parseLong(dueTime)) {
											Log.i("MyLog", "System.currentTimeMillis()="+System.currentTimeMillis()+"="+Long.parseLong(dueTime));
											Log.i("MyLog", "System.currentTimeMillis()=111111111111111111111111");
											LifePayDueTime.setHint(eTime);
											Calendar cd=Calendar.getInstance();
											SimpleDateFormat sdf2 = new SimpleDateFormat(
													"yyyy-MM-dd");
											cd.setTime(new Date((Long.parseLong(dueTime))));
											cd.add(Calendar.MONTH,12);
											LifePayDate.setHint(eTime+"到"+sdf2.format(cd.getTime()));
										}else {
											Calendar cd2=Calendar.getInstance();
											cd2.setTime(currentDate);
											
											cd2.add(Calendar.MONTH,12);
											LifePayDate.setHint(sdf.format(currentDate)+"到"+sdf.format(cd2.getTime()));
											LifePayDueTime.setHint(eTime);
										}			
										
									} else {
										payType = lifePay.promotionArray
												.get(witch).header;
										LifePayType.setHint(payType);
										LifePayMonth
												.setHint("26个月");
										LifePayMonth.setEnabled(false);
										payType = lifePay.promotionArray
												.get(witch).header;
										payMoney = lifePay.promotionArray
												.get(witch).Price;
										LifePayMoney.setHint(""
												+ payMoney + "元");
										promotionid=lifePay.promotionArray.get(witch).PromotionID;
										Log.i("MyLog","promotionid="+promotionid);
										LifePayMoney
												.setHintTextColor(getResources()
														.getColor(
																R.color.red));
										
										Date currentDate=new Date(System.currentTimeMillis());
										String dueTime = lifePay.TheBill.ETime;
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd");
										String eTime = sdf
												.format(new Date((Long.parseLong(dueTime))));
										if (System.currentTimeMillis()<=Long.parseLong(dueTime)) {
											Log.i("MyLog", "System.currentTimeMillis()="+System.currentTimeMillis()+"="+Long.parseLong(dueTime));
											Log.i("MyLog", "System.currentTimeMillis()=111111111111111111111111");
											LifePayDueTime.setHint(eTime);
											Calendar cd=Calendar.getInstance();
											SimpleDateFormat sdf2 = new SimpleDateFormat(
													"yyyy-MM-dd");
											cd.setTime(new Date((Long.parseLong(dueTime))));
											cd.add(Calendar.MONTH,26);
											LifePayDate.setHint(eTime+"到"+sdf2.format(cd.getTime()));
										}else {
											Calendar cd2=Calendar.getInstance();
											cd2.setTime(currentDate);
											
											cd2.add(Calendar.MONTH, 26);
											LifePayDate.setHint(sdf.format(currentDate)+"到"+sdf.format(cd2.getTime()));
											LifePayDueTime.setHint(eTime);
										}	
									}
								}
							});
					builder.create();
					builder.show();
				}
			});
		}

		private void findBill() {
			priority = lifePay.TheOrder.ThePromotion.Priority;
			int monthNum=lifePay.TheOrder.Number;
			promotionid=lifePay.TheOrder.ThePromotion.PromotionID;
			LifePayType.setHint(lifePay.TheOrder.ThePromotion.header);
			LifePayState.setHint(lifePay.TheBill.Status.BillStatusName);
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd");
			String bTime = sdf
					.format(new Date(Long.parseLong(payDate)));
//			LifePayDate.setHint(bTiString currentTime=
			Date currentDate=new Date(System.currentTimeMillis());
			String dueTime = lifePay.TheBill.ETime;
			String eTime = sdf
					.format(new Date((Long.parseLong(dueTime))));
			if (System.currentTimeMillis()<=Long.parseLong(dueTime)) {
				Log.i("MyLog", "System.currentTimeMillis()="+System.currentTimeMillis()+"="+Long.parseLong(dueTime));
				LifePayDueTime.setHint(eTime);
				Calendar cd=Calendar.getInstance();
				SimpleDateFormat sdf2 = new SimpleDateFormat(
						"yyyy-MM-dd");
				cd.setTime(new Date((Long.parseLong(dueTime))));
				
				if(promotionid==46){
					LifePayMonth.setHint("" + monthNum + "个月");
					cd.add(Calendar.MONTH, monthNum);
				}else{
					LifePayMonth.setHint("" + priority + "个月");
					cd.add(Calendar.MONTH, priority);
				}
				LifePayDate.setHint(eTime+"到"+sdf.format(cd.getTime()));
			}else {
				Calendar cd2=Calendar.getInstance();
				cd2.setTime(currentDate);
//				cd2.add(Calendar.MONTH, monthNum);
				if(promotionid==46){
					LifePayMonth.setHint("" + monthNum + "个月");
					cd2.add(Calendar.MONTH, monthNum);
				}else{
					LifePayMonth.setHint("" + priority + "个月");
					cd2.add(Calendar.MONTH, priority);
				}
				LifePayDate.setHint(sdf.format(currentDate)+"到"+sdf.format(cd2.getTime()));
				LifePayDueTime.setHint(sdf.format(cd2.getTime()));
			}
			LifePayName.setHint(lifePay.TheBill.TrueName);
			LifePayAddress.setHint(lifePay.TheBill.Address);
			LifePayMoney.setHint(""
					+ lifePay.TheOrder.Total + "元");
			Log.i("MyLog", "lifePay.TheOrder.ThePromotion.Price="+lifePay.TheOrder.ThePromotion.Price);
			LifePayMoney.setHintTextColor(getResources().getColor(
					R.color.red));
			LifePayDescRiption.setHint(lifePay.TheBill.Comment);
//			LifePayType.setEnabled(false);
//			LifePayMonth.setEnabled(false);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_pay_content);
		// 初始化布局控件
		initView();
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		topbarLeft.setOnClickListener(this);
		LifePayFind.setOnClickListener(this);
	}

	// 初始化布局控件
	private void initView() {
		topbarLeft = (Button) findViewById(R.id.topbar_left);
		topbarText = (TextView) findViewById(R.id.topbar_txt);
		LifePayFind = (Button) findViewById(R.id.PayContentLIfeFindButton);
		LifePayDescRiption = (TextView) findViewById(R.id.PayContentDescTextView);
		LifePayCommit = (Button) findViewById(R.id.PayContentCommitBuyButton);
		LifePayNumber = (EditText) findViewById(R.id.PayContentNumberEditText);
		LifePayType = (EditText) findViewById(R.id.PayContentTypeEditText);
		LifePayState = (EditText) findViewById(R.id.PayContentStateEditText);
		LifePayDate = (EditText) findViewById(R.id.PayContentDateEditText);
		LifePayName = (EditText) findViewById(R.id.PayContentUserNameEditText);
		LifePayAddress = (EditText) findViewById(R.id.PayContentAddressStateEditText);
		LifePayMoney = (EditText) findViewById(R.id.PayContentMoneyStateEditText);
		LifePayMonth = (EditText) findViewById(R.id.PayContentHowMonthEditText);
		LifePayDueTime = (EditText) findViewById(R.id.PayContentCurrentTimeEditText);
		LifePayNumber.setText(LocalStore.getBroadBand(getApplicationContext()));
		LifePayState.setEnabled(false);
		LifePayDate.setEnabled(false);
		LifePayName.setEnabled(false);
		LifePayAddress.setEnabled(false);
		LifePayMoney.setEnabled(false);
		LifePayDueTime.setEnabled(false);
		topbarLeft.setVisibility(View.VISIBLE);
		topbarText.setVisibility(View.VISIBLE);
		topbarText.setText("银通宽带");
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.topbar_left:// 导航返回
			intent.setClass(LifePayContentActivity.this, LifePayActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		case R.id.PayContentLIfeFindButton:// 查询账户
			//隐藏软键盘
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(LifePayNumber.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			LocalStore.setBroadBand(getApplicationContext(), LifePayNumber
					.getText().toString().trim());
			Thread GetPayThread = new Thread() {
				@Override
				public void run() {
					RemoteApiImpl rai = new RemoteApiImpl();
					lifePay = rai.getLifePay(LifePayContentActivity.this,
							LocalStore.getUserInfo().userId, LifePayNumber
									.getText().toString().trim());
					Log.i("MyLog", "LifePay-code="+lifePay.code);
					int length = 0;
					if (lifePay.code == 200) {
						length = lifePay.promotionArray.size();
						header = new String[length];
						for (int i = 0; i < length; i++) {
							header[i] = lifePay.promotionArray.get(i).header;
							Log.i("MyLog", "header" + header[i]);
						}
						mErrorText = lifePay.desc;
						payStatus = lifePay.TheBill.Status.BillStatusName;
						payDate = lifePay.TheBill.BTime;
						payName = lifePay.TheBill.TrueName;
						payAddress = lifePay.TheBill.Address;
						billId=lifePay.TheBill.BillID;
						Log.i("MyLog", "LifePay--=" + payAddress + payDate
								+ payMoney + payName + payStatus + payType);
						Log.i("MyLog",
								"lifePay="
										+ lifePay.promotionArray.get(0).header
										+ ""
										+ lifePay.TheBill.Status.BillStatusName);
					}
					Message msg = new Message();
					msg.what = lifePay.code;
					mHandler.sendMessage(msg);
				}
			};
			GetPayThread.start();
			break;
		case R.id.PayContentCommitBuyButton:// 提交支付\
			if (LifePayMonth.getHint().toString().trim().equals("请输入您要使用多少个月")) {
				Toast.makeText(getApplicationContext(), "请选择包月周期", Toast.LENGTH_SHORT).show();
			}else{
			Thread CommitBuyThread = new Thread() {
				@Override
				public void run() {
					RemoteApiImpl remote = new RemoteApiImpl();
					int promotionNumber2=promotionNumber;
					Log.i("MyLog", "是否走到这里——————1=="+promotionNumber2);
					if (promotionNumber2>1) {
						promotionNumber2=promotionNumber;
					}else {
						promotionNumber2=1;
					}
					Log.i("MyLog", "是否走到这里——————1=="+lifePay.TheOrder.ThePromotion.Priority);
					OrderItem orderInfo = remote.billPayOrder(
							LifePayContentActivity.this,
							promotionid,
							LocalStore.getUserInfo().userId, billId, promotionNumber2);
					Message msg = new Message();
					Log.i("MyLog", "promotionid"+promotionid);
					Log.i("MyLog", "userid"+LocalStore.getUserInfo().userId);
					Log.i("MyLog", "billId"+billId);
					Log.i("MyLog", "promotionNumber="+promotionNumber2);
					Log.i("MyLog", "是否走到这里——————1");
					if (orderInfo == null) {
						msg.what = 4;
					} else {
						msg.what = 3;
						aliOrderStr = orderInfo.AliOrderStr;

						Log.i("MyLog",
								"***********aliOrderStr支付宝订单信息为=saletypeid="
										+ orderInfo.ThePromotion.SaleTypeID
										+ "aliOrderStr=:" + aliOrderStr);

					}
					mHandler.sendMessage(msg);
				}
			};
			CommitBuyThread.start();
			}
			break;
		}
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

}
