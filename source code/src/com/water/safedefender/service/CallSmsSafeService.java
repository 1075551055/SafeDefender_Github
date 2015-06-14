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
	 * Ϊ�˷�����Ź㲥�����߿�����Ϊ�رտ���������ͨ������ע�ᣬ���������嵥�ļ�ע��
	 * @author Administrator
	 *
	 */
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��ȡ����
			Object[] objs =(Object[]) intent.getExtras().get("pdus");
			for (Object object : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//�õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if("2".equals(mode)||"3".equals(mode)){
					Log.i("test", "���Ÿ�����");
					abortBroadcast();//���ض���
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
		//ע��smsReceiver
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(smsReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ȡ��ע��smsReceiver
		unregisterReceiver(smsReceiver);
		smsReceiver = null;
		
		//ֹͣ�绰����
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
					//ͨ�����ݹ۲��߼���ͨ����¼�Ƿ�ı�,onChange��ʱ���ɾ��
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
							true, new MyContentObserver(incomingNumber, new Handler()));
					
					//�Ҷϵ绰
					endCall();//��һ�������������е�Զ�̷��񷽷����÷��������ú󣬺��м�¼���ܻ�û�в���
					//ɾ��ͨ����¼(��Ȼ�Ҷ��˵绰���ǻ�����ͨ����¼�ĳ���)
					//deleteCallLog(incomingNumber);
					
				}
				break;

			default:
				break;
			}
		}
		/**
		 * ���ݹ۲���
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
				//��������change��Ӧ��ȡ��ע�����ݹ۲���
				getContentResolver().unregisterContentObserver(this);
				deleteCallLog(incomingNumber);
				super.onChange(selfChange);
			}
			
		}

		/**
		 * ɾ��ͨ����¼
		 */
		private void deleteCallLog(String incomingNumber) {
			//ͨ����¼�����data/data/com.android.providers.contacts/contacts2.db��
			//���ڸ����ݿ���˽�еĲ�����ֱ�Ӷ�д�ģ����Ա���ͨ�������ṩ�߽��ж�ȡ����
			//1.��ȡ���ݽ�����
			ContentResolver resolver = getContentResolver();
			//2.ͨ��ͨ����¼��content uri��ѯ��¼
			resolver.delete(CallLog.Calls.CONTENT_URI,"number=?",new String[]{incomingNumber});
					
			
		}

		/**
		 * �Ҷϵ绰
		 */
		private void endCall() {
			
			try {
				//ͨ�������ȡIBinder
				Class serviceManger = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = serviceManger.getMethod("getService", String.class);
				IBinder binder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
				
				//ֹͣ�绰
				ITelephony.Stub.asInterface(binder).endCall();//�ǵ����Ȩ��android.permission.CALL_PHONE
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
