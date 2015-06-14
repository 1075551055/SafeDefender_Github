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
		//�ж��Ƿ����˷�������
		if(isOpenProtect){
			//�Ѿ������sim���к�
			String simNumber = preference.getString("simNumber", null);
			//����ֻ������к�
			String simSerialNumber = telManager.getSimSerialNumber();
			if(!(simNumber+"hello").equals(simSerialNumber)){
				Toast.makeText(context, "�ֻ���������", Toast.LENGTH_LONG).show();
				
				//���Ͷ��Ÿ���ȫ����
				String securityPhone = preference.getString("securityPhone", "");
				SmsManager sms = SmsManager.getDefault();
				try {
					sms.sendTextMessage(securityPhone, null, URLEncoder.encode("�ֻ��������ˣ���С͵", "utf-8"), null, null);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
