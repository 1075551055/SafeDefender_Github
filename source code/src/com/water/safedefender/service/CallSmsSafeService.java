package com.water.safedefender.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Contacts;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.water.safedefender.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {

	private InnerSmsReceiver smsReceiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyPhoneStateListener phoneStateListener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 为了方便短信广播接收者可以人为关闭开启，将其通过代码注册，而不是在清单文件注册
	 * @author Administrator
	 *
	 */
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取短信
			Object[] objs =(Object[]) intent.getExtras().get("pdus");
			for (Object object : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if("2".equals(mode)||"3".equals(mode)){
					Log.i("test", "短信给拦了");
					abortBroadcast();//拦截短信
				}
			}
		}
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneStateListener =new MyPhoneStateListener();
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		dao = new BlackNumberDao(CallSmsSafeService.this);
		smsReceiver = new InnerSmsReceiver();
		//注册smsReceiver
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(smsReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消注册smsReceiver
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		
		//停止电话监听
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if("1".equals(mode)||"3".equals(mode)){
					//通过内容观察者监听通话记录是否改变,onChange的时候就删除
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
							true, new MyContentObserver(incomingNumber, new Handler()));
					
					//挂断电话
					endCall();//另一个进程里面运行的远程服务方法。该方法给调用后，呼叫记录可能还没有产生
					//删除通话记录(虽然挂断了电话但是还会有通话记录的出现)
					//deleteCallLog(incomingNumber);
					
				}
				break;

			default:
				break;
			}
		}
		/**
		 * 内容观察者
		 * @author Administrator
		 *
		 */
		private class MyContentObserver extends ContentObserver{
			private String incomingNumber;
			public MyContentObserver(String incomingNumber,Handler handler) {
				super(handler);
				this.incomingNumber = incomingNumber;
			}

			@Override
			public void onChange(boolean selfChange) {
				//当监听到change后应该取消注册内容观察者
				getContentResolver().unregisterContentObserver(this);
				deleteCallLog(incomingNumber);
				super.onChange(selfChange);
			}
			
		}

		/**
		 * 删除通话记录
		 */
		private void deleteCallLog(String incomingNumber) {
			//通话记录存放在data/data/com.android.providers.contacts/contacts2.db中
			//由于该数据库是私有的不可以直接读写的，所以必须通过内容提供者进行读取数据
			//1.获取内容解析器
			ContentResolver resolver = getContentResolver();
			//2.通过通话记录的content uri查询记录
			resolver.delete(CallLog.Calls.CONTENT_URI,"number=?",new String[]{incomingNumber});
					
			
		}

		/**
		 * 挂断电话
		 */
		private void endCall() {
			
			try {
				//通过反射获取IBinder
				Class serviceManger = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = serviceManger.getMethod("getService", String.class);
				IBinder binder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
				
				//停止电话
				ITelephony.Stub.asInterface(binder).endCall();//记得添加权限android.permission.CALL_PHONE
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
