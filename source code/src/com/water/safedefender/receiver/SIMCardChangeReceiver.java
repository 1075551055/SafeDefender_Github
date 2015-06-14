package com.water.safedefender.receiver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

public class SIMCardChangeReceiver extends BroadcastReceiver {

	private SharedPreferences preference;
	private TelephonyManager telManager;
	@Override
	public void onReceive(Context context, Intent intent) {
		preference = context.getSharedPreferences("config", context.MODE_PRIVATE);
		telManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		boolean isOpenProtect = preference.getBoolean("isOpenProtect", false);
		//判断是否开启了防盗保护
		if(isOpenProtect){
			//已经保存的sim序列号
			String simNumber = preference.getString("simNumber", null);
			//如今手机的序列号
			String simSerialNumber = telManager.getSimSerialNumber();
			if(!(simNumber+"hello").equals(simSerialNumber)){
				Toast.makeText(context, "手机卡给换了", Toast.LENGTH_LONG).show();
				
				//发送短信给安全号码
				String securityPhone = preference.getString("securityPhone", "");
				SmsManager sms = SmsManager.getDefault();
				try {
					sms.sendTextMessage(securityPhone, null, URLEncoder.encode("手机给换卡了，有小偷", "utf-8"), null, null);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
