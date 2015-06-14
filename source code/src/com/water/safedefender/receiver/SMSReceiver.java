package com.water.safedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.water.safedefender.R;
import com.water.safedefender.service.LocationService;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences preference;
	@Override
	public void onReceive(Context context, Intent intent) {
		//��ȡ����
		Object [] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objs) {
			//�õ�����
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			//������
			String sender = message.getOriginatingAddress();
			String body = message.getMessageBody();
			preference = context.getSharedPreferences("config", context.MODE_PRIVATE);
			String securityPhone = preference.getString("securityPhone", "");
			if(sender.contains(securityPhone)){
				if(body.equals("#*location*#")){	
					Log.i("test", "GPS׷��");
					Intent locationIntent = new Intent(context, LocationService.class);
					context.startService(locationIntent);
					String lastlocation = preference.getString("lastlocation", null);
					if(TextUtils.isEmpty(lastlocation)){
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					abortBroadcast();//�ǵ�����ס���ţ������ֻ����ǻ���յ����ţ�Ҫ���嵥�ļ��н��㲥�����ߵ����ȼ�����Ϊ��ߣ�
					
				}
				if(body.equals("#*alarm*#")){		
					Log.i("test", "���ű�������");
					MediaPlayer media = MediaPlayer.create(context, R.raw.guanlangaoshou);
					media.start();
					abortBroadcast();//�ǵ�����ס���ţ������ֻ����ǻ���յ����ţ�Ҫ���嵥�ļ��н��㲥�����ߵ����ȼ�����Ϊ��ߣ�
				}
				if(body.equals("#*wipedata*#")){
					abortBroadcast();//�ǵ�����ס���ţ������ֻ����ǻ���յ����ţ�Ҫ���嵥�ļ��н��㲥�����ߵ����ȼ�����Ϊ��ߣ�
					Log.i("test", "Զ����������");
				}
				if(body.equals("#*lockscreen*#")){
					abortBroadcast();//�ǵ�����ס���ţ������ֻ����ǻ���յ����ţ�Ҫ���嵥�ļ��н��㲥�����ߵ����ȼ�����Ϊ��ߣ�
					Log.i("test", "Զ������");
				}
			}
			
		}
	}

}
