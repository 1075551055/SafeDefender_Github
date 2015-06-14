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
		//获取短信
		Object [] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objs) {
			//得到短信
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			//发送者
			String sender = message.getOriginatingAddress();
			String body = message.getMessageBody();
			preference = context.getSharedPreferences("config", context.MODE_PRIVATE);
			String securityPhone = preference.getString("securityPhone", "");
			if(sender.contains(securityPhone)){
				if(body.equals("#*location*#")){	
					Log.i("test", "GPS追踪");
					Intent locationIntent = new Intent(context, LocationService.class);
					context.startService(locationIntent);
					String lastlocation = preference.getString("lastlocation", null);
					if(TextUtils.isEmpty(lastlocation)){
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					abortBroadcast();//记得拦截住短信，否则手机还是会接收到短信（要在清单文件中将广播接收者的优先级设置为最高）
					
				}
				if(body.equals("#*alarm*#")){		
					Log.i("test", "播放报警音乐");
					MediaPlayer media = MediaPlayer.create(context, R.raw.guanlangaoshou);
					media.start();
					abortBroadcast();//记得拦截住短信，否则手机还是会接收到短信（要在清单文件中将广播接收者的优先级设置为最高）
				}
				if(body.equals("#*wipedata*#")){
					abortBroadcast();//记得拦截住短信，否则手机还是会接收到短信（要在清单文件中将广播接收者的优先级设置为最高）
					Log.i("test", "远程数据销毁");
				}
				if(body.equals("#*lockscreen*#")){
					abortBroadcast();//记得拦截住短信，否则手机还是会接收到短信（要在清单文件中将广播接收者的优先级设置为最高）
					Log.i("test", "远程锁屏");
				}
			}
			
		}
	}

}
