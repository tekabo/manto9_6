package com.wuxianyingke.property.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.wuxianyingke.property.remote.RemoteApi.AddressItem;
import com.wuxianyingke.property.remote.RemoteApi.CityInfo;
import com.wuxianyingke.property.remote.RemoteApi.FreeWifi;
import com.wuxianyingke.property.remote.RemoteApi.InvitationCode;
import com.wuxianyingke.property.remote.RemoteApi.LoginInfo;
import com.wuxianyingke.property.remote.RemoteApi.Promotion;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApi.WeatherInfo;

//
public class LocalStore {
	public static String nowTime = "";
	public final static String USER_INFO = "user_info";
	public final static String LOADING_ID = "loading_id";
	public final static String LOADING_URL="loading_url";
	public final static String USER_ID = "userid";
	public final static String USER_NAME = "username";
	public final static String USER_PROPERTYID="userpropertyid";
	public final static String USER_PHONENUMBER="phonenumber";
	public final static String USER_U_ID = "user_U_ID";
	public final static String USER_U_PASS = "user_U_PASS";
	public final static String USER_AUTOLOGIN = "user_autologin";
//	public final static String PROPERTY_ID = "property_id";
	public final static String PROPERTY_NAME = "property_name";
	public final static String WELCOMEID="welcome_id";
	public final static String WIFI_LINKE_ID="wifi_link_id";
	public final static String WIFI_BREAK_ID="wifi_break_id";
	public final static String WIFI_PAY_ID = "wifi_pay_money";
	public final static String PAY_FLAG_ID = "pay_flag";
	public final static String USER_STATUS = "userstatus";// 设置用户是否记住登录状态;
															// true=yes;false=no;
	public final static String PUSH_MESSAGE = "push_message";// 设置是否接受流量（短）彩信;
																// true=yes;false=no;
	public final static String PUSH_MESSAGE_ID = "push_message_id";//
	public final static String PHONE_NUMBER = "phone_number";
	public final static String ISVISITOR = "isVisitor";
	public final static String BROAD_BAND="broadband";
	private static User userInfo;
	private static LoginInfo loginInfo;
	private static InvitationCode invitationCode;
	private static Promotion promotionId;// 优惠id
	private static AddressItem addressId;// 地址id
	// 购物信息
	private final static String PROMOTION_ID = "promotionid";
	//地址信息
	private final static String ADDRESS_ID="addressid";
	/**券码信息*/
	private final static String ORDERSEQUENCENUMBER_ID="OrderSequenceNumberid";
	// 地址信息
	private final static String ADDRESS_INFO = "address_info";
	private final static String CITY_ID = "cityid";
	private final static String CITY_NAME = "cityname";
	public static CityInfo cityInfo;
	public static WeatherInfo weatherInfo;
	public static FreeWifi freeWifi;
	private final static String BIANQIAN = "BIANQIAN";
	private final static String BIANQIAN_TITLE = "bianqian_title";
	private final static String BIANQIAN_CONTENT = "bianqian_content";
	private final static String QUNFAHEAD_CONTENT = "qunfa_id";
	private final static String UPLOADE_FLAG = "upload_flag";
	private final static String WIFIAP = "wifiap";
	private final static String RADIUS_PARAM = "radiusparam";
	private final static String PORTALIP = "portalip";
	// 报修联系方式
	private final static String REPAIR_CONTACT = "repair_contact";
	private final static String LATITUDE = "latitude";
	private final static String LONGITUDE = "longitude";
	private final static String TELNUMBER =	"telnumber";
	//新增个人信息
	private final static String PERSONAL_MESSAGE = "personal_message";

	
	public static void initFreeWifi(Context context){
		SharedPreferences saving=context.getSharedPreferences(ADDRESS_INFO, 0);
		freeWifi=new FreeWifi();
		freeWifi.UserID=saving.getString("UserID", "未知");
		freeWifi.WIFIAccount=saving.getString("WIFIAccount", "未知");
		freeWifi.WIFIPwd=saving.getString("WIFIPwd", "未知");
		freeWifi.WIFIUserID=saving.getString("WIFIUserID", "未知");
	}
	
	public static void saveFreeWifi(Context context){
		SharedPreferences saving = context
				.getSharedPreferences(ADDRESS_INFO, 0);
		saving.edit().putString("UserID", freeWifi.UserID).commit();
		saving.edit().putString("WIFIAccount", freeWifi.WIFIAccount).commit();
		saving.edit().putString("WIFIPwd", freeWifi.WIFIPwd).commit();
		saving.edit().putString("WIFIUserID", freeWifi.WIFIUserID).commit();
		
	}
	public static FreeWifi getFreeWifi(){
		return freeWifi;
	}
	
	public static void initWeatherInfo(Context context) {
		SharedPreferences saving = context
				.getSharedPreferences(ADDRESS_INFO, 0);
		weatherInfo = new WeatherInfo();
		weatherInfo.temp1 = saving.getString("temp1", "未知");
		weatherInfo.img_title_single = saving
				.getString("img_title_single", "晴");
		weatherInfo.wind1 = saving.getString("wind1", "未知");
		weatherInfo.fl1 = saving.getString("fl1", "未知");
	}

	public static void saveWeatherInfo(Context context) {
		SharedPreferences saving = context
				.getSharedPreferences(ADDRESS_INFO, 0);
		saving.edit().putString("temp1", weatherInfo.temp1).commit();
		saving.edit()
				.putString("img_title_single", weatherInfo.img_title_single)
				.commit();
		saving.edit().putString("wind1", weatherInfo.wind1).commit();
		saving.edit().putString("fl1", weatherInfo.fl1).commit();
	}

	public static void initCityInfo(Context context) {
		SharedPreferences saving = context
				.getSharedPreferences(ADDRESS_INFO, 0);
		cityInfo = new CityInfo();
		cityInfo.city_id = saving.getString(CITY_ID, "101101");
		cityInfo.city_name = saving.getString(CITY_NAME, "未知");
	}

	public static void setCityInfo(Context context, CityInfo info) {
		SharedPreferences saving = context
				.getSharedPreferences(ADDRESS_INFO, 0);
		saving.edit().putString(CITY_ID, info.city_id).commit();
		saving.edit().putString(CITY_NAME, info.city_name).commit();
		cityInfo.city_id = info.city_id;
		cityInfo.city_name = info.city_name;
	}

	public static CityInfo getCityInfo() {
		return cityInfo;
	}

	public static void initUserInfo(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		userInfo = new User();
		userInfo.userId = saving.getLong(USER_ID, 0);
		userInfo.userName = saving.getString(USER_NAME, "");
		userInfo.PropertyID=saving.getInt(USER_PROPERTYID, 0);
		userInfo.phone=saving.getString(USER_PHONENUMBER, "");
		userInfo.telnumber = saving.getString(TELNUMBER, "");
	}

	public static void setUserInfo(Context context, User user) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(USER_ID, user.userId).commit();
		saving.edit().putString(USER_NAME, user.userName).commit();
		saving.edit().putInt(USER_PROPERTYID, user.PropertyID).commit();
		saving.edit().putString(USER_PHONENUMBER, user.phone).commit();
		saving.edit().putString(TELNUMBER, user.telnumber).commit();
		userInfo.userId = user.userId;
		userInfo.userName = user.userName;
		userInfo.PropertyID=user.PropertyID;
		userInfo.phone=user.phone;
		userInfo.telnumber = user.telnumber;
	}

	public static LoginInfo initLoginInfo(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		loginInfo = new LoginInfo();
		loginInfo.U_ID = saving.getString(USER_U_ID, "");
		loginInfo.U_PASS = saving.getString(USER_U_PASS, "");
		loginInfo.autoLogin = saving.getBoolean(USER_AUTOLOGIN, false);
		return loginInfo;
	}

	public static void setLoginInfo(Context context, LoginInfo user) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(USER_U_ID, user.U_ID).commit();
		saving.edit().putString(USER_U_PASS, user.U_PASS).commit();
		saving.edit().putBoolean(USER_AUTOLOGIN, user.autoLogin).commit();
		loginInfo.U_ID = user.U_ID;
		loginInfo.U_PASS = user.U_PASS;
		loginInfo.autoLogin = user.autoLogin;
	}
	/**
	 * 存储宽带账号
	 */
	public static void setBroadBand(Context context, String broadband){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(BROAD_BAND, broadband).commit();
	}
	
	public static String getBroadBand(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(BROAD_BAND,"");
	}
	
	/**
	 * 存储地址经纬度
	 */
	public static void setLatitude(Context context,float latitude){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putFloat(LATITUDE, latitude).commit();
	}
	
	public static double getLatitude(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getFloat(LATITUDE,0);
	}
	/**
	 * 存储地址经纬度
	 */
	public static void setLongitude(Context context,float longitude){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putFloat(LONGITUDE, longitude).commit();
	}
	
	public static double getLongitude(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getFloat(LONGITUDE,0);
	}
	
	/**
	 * 存储loadingurl
  */
	public static void setLoadingUrl(Context context, String loadingurl){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(LOADING_URL, loadingurl).commit();
	}
	
	public static String getLoadingUrl(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(LOADING_URL,"");
	}
	
	/**
	 * 存储免费wifiurl
	 * @param context
 */
	public static void setWifiApUrl(Context context, String wifiUrl){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(WIFIAP, wifiUrl).commit();
	}
	
	public static String getWifiApUrl(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(WIFIAP,"");
	}
	/**
	 * 存储 radiusParma
	 */

	public static void setRadiusParam(Context context, String RadiusParam){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(RADIUS_PARAM, RadiusParam).commit();
	}
	
	public static String getRadiusParam(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(RADIUS_PARAM,"");
	}
	
	/**
	 * 存储portalIp
	 */
	public static void setPortalIp(Context context, String portalIp){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(PORTALIP, portalIp).commit();
	}
	
	public static String getPortalIp(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(PORTALIP,"");
	}
	
	/**
	 * 存储welcomeid
		 */
	public static void setWelcomeId(Context context,int welcomeid){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt(WELCOMEID, welcomeid).commit();
	}
	
	public static int getWelcomeId(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(WELCOMEID,0);
	}
	/**
	 * 存储wifiid
	 */
	public static void setWifiId(Context context,int wifiId){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt(WIFI_LINKE_ID, wifiId).commit();
	}
	
	public static int getWifiId(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(WIFI_LINKE_ID,0);
	}
	/**
	 * 存储wifiid
	 */
	public static void setWifiId2(Context context,int wifiId){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt(WIFI_BREAK_ID, wifiId).commit();
	}
	
	public static int getWifiId2(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(WIFI_BREAK_ID,0);
	}
	
	
	/**
	 * 存储wifiPayMoney
	 */
	public static void setWifiPayMoney(Context context,int wifiPayMoneyId){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt(WIFI_PAY_ID, wifiPayMoneyId).commit();
	}
	
	public static int getWifiPayMoney(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(WIFI_PAY_ID,0);
	}
	
	/**
	 * 存储wifiPayMoney
	 */
	public static void setPayFlag(Context context,int payFlagId){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt(PAY_FLAG_ID, payFlagId).commit();
	}
	
	public static int getPayFlag(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(PAY_FLAG_ID,0);
	}
	
	/**
	 * 支付标记
	 *//*
	public static void setPayFlag(Context context,int payFlagId){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO , 0);
		saving.edit().putInt(PAY_FLAG_ID, payFlagId).commit();
	}
	
	public static int getPayFlag(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(PAY_FLAG_ID, 0);
	}*/
	/*public static void initInvitationCode(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		invitationCode = new InvitationCode();
		invitationCode.propertyID = saving.getLong(PROPERTY_ID, 0);
		invitationCode.propertyName = saving.getString(PROPERTY_NAME, "");
		invitationCode.phoneNumber = saving.getString(PHONE_NUMBER, "");
	}

	public static void setInvitationCode(Context context,
			InvitationCode invitationCode) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(PROPERTY_ID, invitationCode.propertyID).commit();
		saving.edit().putString(PROPERTY_NAME, invitationCode.propertyName)
				.commit();
		saving.edit().putString(PHONE_NUMBER, invitationCode.phoneNumber)
				.commit();
		invitationCode.propertyID = invitationCode.propertyID;
		invitationCode.propertyName = invitationCode.propertyName;
		invitationCode.phoneNumber = invitationCode.phoneNumber;
	}

	public static InvitationCode getInvitationCode(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		invitationCode = new InvitationCode();
		invitationCode.propertyID = saving.getLong(PROPERTY_ID, 0);
		invitationCode.propertyName = saving.getString(PROPERTY_NAME, "");
		invitationCode.phoneNumber = saving.getString(PHONE_NUMBER, "");
		return invitationCode;
	}
*/
	public static void logout(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(USER_ID, 0).commit();
		saving.edit().putString(USER_NAME, "").commit();
		userInfo.userId = 0;
		userInfo.userName = "";

		initLoginInfo(context);
		loginInfo.autoLogin = false;
		setLoginInfo(context, loginInfo);
	}

	public static User getUserInfo() {
		return userInfo;
	}

	public static boolean isLogin() {
		if (userInfo.userId == 0)
			return false;
		else
			return true;
	}

	// 获取优惠id
	public static Promotion getPromotion() {
		return promotionId;
	}

	// 获取地址id
	public static AddressItem getAddressId() {
		return addressId;
	}

	/**promotionid 存储优惠id*/
	public static void setPromotionId(Context context, long promotionId) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(PROMOTION_ID, 0);
	}

	/**addressid 获取优惠id*/
	public static long getAddressId(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getLong(ADDRESS_ID, -1);
	}
	/**addressid 存储优惠id*/
	public static void setAddressId(Context context, long promotionId) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(ADDRESS_ID, 0);
	}

	/**promotionid 获取优惠id*/
	public static long getPromotionId(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getLong(PROMOTION_ID, -1);
	}


	/**将登录的状态存储下来（是否是游客）*/
	public static void setIsVisitor(Context context, boolean isVisitor) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putBoolean(ISVISITOR, isVisitor).commit();
	}

	/**获得用户登录的状态）*/
	public static boolean getIsVisitor(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getBoolean(ISVISITOR, true);
	}
	
	/**将登录的状态存储下来（是否是游客）*/
	public static void setIsUpload(Context context, boolean isUpload) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putBoolean(UPLOADE_FLAG, isUpload).commit();
	}

	/**获得用户登录的状态）*/
	public static boolean getIsUpload(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getBoolean(UPLOADE_FLAG, true);
	}

	/**保存用户的登录的状态*/
	public static void setUserStatus(Context context, boolean userStatus) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putBoolean(USER_STATUS, userStatus).commit();
	}

	/**获得用户的状态*/
	public static boolean getUserStatus(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getBoolean(USER_STATUS, true);
	}
	
	//保存订单券码
	public static void setSaveOrderSequenceNumber(Context context,long OrderSequenceNumberId){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(ORDERSEQUENCENUMBER_ID, OrderSequenceNumberId);
	}
	
	//获取订单券码
	public static long getOrderSequenceNumber(Context context){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getLong(ORDERSEQUENCENUMBER_ID, 0);
	}

	public static void setPushMessage(Context context, boolean pushMessge) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putBoolean(PUSH_MESSAGE, pushMessge).commit();
	}

	public static boolean getPushMessge(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getBoolean(PUSH_MESSAGE, true);
	}

	public static void setLoadingId(Context context, int loaindgId) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt(LOADING_ID, loaindgId).commit();
	}

	public static int getLoadingId(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt(LOADING_ID, -1);
	}

	public static void setPushMsgId(Context context, long pushMsgId) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putLong(PUSH_MESSAGE_ID, pushMsgId).commit();
	}

	public static Long getPushMsgId(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getLong(PUSH_MESSAGE_ID, -1);
	}

	public static ArrayList<bianqian> bianqian_List = null;

	public class bianqian {
		public String id;
		public String cTime;
		public String title;
		public String content;
	}

	public void saveBianqian(Context context) {
		if (bianqian_List == null)
			return;
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		try {
			JSONObject json = new JSONObject();
			JSONArray jsonMembers = new JSONArray();
			for (int i = 0; i < bianqian_List.size(); i++) {
				JSONObject member1 = new JSONObject();
				member1.put("id", bianqian_List.get(i).id);
				member1.put("cTime", bianqian_List.get(i).cTime);
				member1.put("title", bianqian_List.get(i).title);
				member1.put("content", bianqian_List.get(i).content);
				jsonMembers.put(member1);
			}
			json.put("bianqian", jsonMembers);
			saving.edit().putString(BIANQIAN, json.toString()).commit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void initBianqian(Context context) {
		bianqian_List = new ArrayList<bianqian>();
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		String jsonString = saving.getString(BIANQIAN, "");
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonArray = json.getJSONArray("bianqian");
			String loginNames = "loginname list:";
			for (int i = 0; i < jsonArray.length(); i++) {
				bianqian tmpbianqian = new LocalStore.bianqian();
				JSONObject jsobj = (JSONObject) jsonArray.get(i);
				tmpbianqian.id = jsobj.getString("id");
				tmpbianqian.cTime = jsobj.getString("cTime");
				tmpbianqian.title = jsobj.getString("title");
				tmpbianqian.content = jsobj.getString("content");
				bianqian_List.add(tmpbianqian);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void deleteBianqianByid(Context context, String id) {
		if (bianqian_List == null)
			return;
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		String jsonString = saving.getString(BIANQIAN, "");
		try {
			for (int i = 0; i < bianqian_List.size(); i++) {
				if (bianqian_List.get(i).id.equals(id)) {
					bianqian_List.remove(i);
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		saveBianqian(context);
	}

	public void addBianqian(Context context, String title, String content) {
		if (bianqian_List == null)
			return;
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		String jsonString = saving.getString(BIANQIAN, "");
		try {
			bianqian tmpbq = new bianqian();
			tmpbq.id = UUID.randomUUID().toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tmpbq.cTime = sdf.format(new Date());
			tmpbq.title = title;
			tmpbq.content = content;
			bianqian_List.add(tmpbq);
		} catch (Exception e) {
			// TODO: handle exception
		}
		saveBianqian(context);
	}

	public void editBianqian(Context context, String title, String content,
			String id) {
		if (bianqian_List == null)
			return;
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		String jsonString = saving.getString(BIANQIAN, "");
		try {
			for (int i = 0; i < bianqian_List.size(); i++) {
				if (bianqian_List.get(i).id.equals(id)) {
					bianqian_List.get(i).title = title;
					bianqian_List.get(i).content = content;
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		saveBianqian(context);
	}

	public static void setBianqianTitle(Context context, String title) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(BIANQIAN_TITLE, title).commit();
	}

	public static String getBianqianTitle(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(BIANQIAN_TITLE, "");
	}

	public static void setBianqianContent(Context context, String content) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(BIANQIAN_CONTENT, content).commit();
	}

	public static String getBianqianContent(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(BIANQIAN_CONTENT, "");
	}

	public static int getQunfaIsRead(Context context, long id) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt("qunfa_id" + id, 0);
	}

	public static void setQunfaIsRead(Context context, long id) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt("qunfa_id" + id, 1).commit();
	}

	public static int getLivingShouCang(Context context, long id) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getInt("LivingShouCang_id" + id, 0);
	}

	public static void setLivingShouCang(Context context, long id, int isture) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putInt("LivingShouCang_id" + id, isture).commit();
	}
	
	public static void setRepairContact(Context context, String contact) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		saving.edit().putString(REPAIR_CONTACT, contact).commit();
	}

	public static String getRepairContact(Context context) {
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		return saving.getString(REPAIR_CONTACT, "");
	}

	//新加
	public  class personal{
		public  String gender;
		public  String age;
		public  String plot;//小区
	}
	public void addPersonalMessage(Context context,String age,String gender){
		SharedPreferences saving = context.getSharedPreferences(USER_INFO, 0);
		personal persal = new personal();
		saving.edit().putString("gender",persal.gender).commit();
		saving.edit().putString("age",persal.age).commit();
		saving.edit().putString("plot",persal.plot).commit();

	}
}
