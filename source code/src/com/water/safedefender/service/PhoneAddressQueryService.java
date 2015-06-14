package com.water.safedefender.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.water.safedefender.R;
import com.water.safedefender.dao.PhoneAddressQueryUtils;

public class PhoneAddressQueryService extends Service {

	private TelephonyManager telephoe;
	private MyPhoneStateListener phoneStateListener;
	private OutCallReceiver outCallReceiver;
	private SharedPreferences configPreference;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * �㲥�����ߣ������Ⲧ�绰���㲥�����߲����������嵥�ļ�ע�ỹ�����ڴ���ע��
	 */
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//��ȡ���Ⲧ����
			String phoneNumber = getResultData();//ģ�����к����110ǰ׺
			//��ѯ���ݿ��ȡ������
			String address = PhoneAddressQueryUtils.queryAddress(context.getFilesDir()+"/address.db", phoneNumber);
			//Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			myToast(address);
		}

	}

	

	
	//���������
	private WindowManager windowManger;
	//�Զ�����˾Ҫ��ʾ��view
	private View myToastTv;
	//�Զ�����˾����
	private final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	//��¼˫��������
	long[] mHits=new long[2];
	
	/**
	 * 
	 * �Զ�����˾
	 */
	private void myToast(String text){
		/*myToastTv = new TextView(this);
		myToastTv.setText(text);
		myToastTv.setTextSize(22);*/
		
		configPreference = getSharedPreferences("config", MODE_PRIVATE);
		
		myToastTv =  View.inflate(this, R.layout.mytoast_background, null);
		TextView tv_address = (TextView) myToastTv.findViewById(R.id.tv_address);
		tv_address.setText(text);
		
		//myToastTv����¼�
		myToastTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//������index:1��ʼ��������
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				//�����һ������Ԫ�ظ�ֵ���ֻ����������ڵ�ʱ��
				mHits[mHits.length-1]=SystemClock.uptimeMillis();
				//�ж������һ��Ԫ�غ͵�ǰ�ֻ�����ʱ���ʱ���,��Ϊ�����һ��Ԫ�ر��������һ�ε����ʱ�䣬
				//ֻҪ��һ�ε����ʱ�����һ�ε�ʱ���С�ڻ����50���ʾ˫����
				if(mHits[0]>=SystemClock.uptimeMillis()-500){
					//����˾��Ļ����
					params.x = windowManger.getDefaultDisplay().getWidth()/2-myToastTv.getWidth()/2;
					windowManger.updateViewLayout(myToastTv, params);
					//����λ��
					Editor et = configPreference.edit();
					et.putInt("paramsX", params.x);
					et.commit();
				}
			}
		});
		
		//��myToastTv���ô���������
		myToastTv.setOnTouchListener(new OnTouchListener() {
			//������ָ�ĳ�ʼ��λ��
			int startX;
			int startY;
			//������ָ�ƶ����λ��
			int newX;
			int newY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){//getAction��ȡ��ָ�����Ķ���
					case MotionEvent.ACTION_DOWN://��ָ������Ļ
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						Log.i("test", "��ʼ��λ��-��startX:"+startX+";startY:"+startY);
						break;
					
					case MotionEvent.ACTION_MOVE://��ָ����Ļ�ƶ�
						newX = (int) event.getRawX();
						newY = (int) event.getRawY();
						Log.i("test", "�ƶ����λ��-��newX:"+newX+";newY:"+newY);
						//�����ƶ����x��y��ƫ����
						int dx = newX - startX;
						int dy = newY - startY;
						Log.i("test", "��ָƫ����-��dx:"+dx+";dy:"+dy);
						
						//������˾λ��
						//ע�⣺params.x��params.y���Ǹ���params.gravity�������λ�õı仯�ģ�Ĭ������Ļ���У�
						//�Ѿ�������������params.gravity�����Ļ���ϽǶ���
						params.x+=dx;
						params.y+=dy;
						windowManger.updateViewLayout(myToastTv, params);
						
						//�ж�x,y�ı߽�ֵ
						if(params.x<0){//���x��
							params.x=0;
						}
						if(params.y<0){//�ϱ�y��
							params.y=0;
						}
						//�ұ�x��
						if(params.x>windowManger.getDefaultDisplay().getWidth()-myToastTv.getWidth()){
							params.x=windowManger.getDefaultDisplay().getWidth()-myToastTv.getWidth();
						}
						//�ײ�y��
						if(params.y>windowManger.getDefaultDisplay().getHeight()-myToastTv.getHeight()){
							params.y=windowManger.getDefaultDisplay().getHeight()-myToastTv.getHeight();
						}
						
						
						//����λ��
						Editor et = configPreference.edit();
						et.putInt("paramsX", params.x);
						et.putInt("paramsY", params.y);
						et.commit();
						
						break;
						
					case MotionEvent.ACTION_UP://��ָ�뿪��Ļ
						
						break;
				}
				//true��ʾ�¼��������ﴦ����ϣ���Ҫ�ø��ؼ����߸�������Ӧ�����¼�
				//����myToastTv�����˵���¼�(����¼��ǣ����£�ͣ����̧���������������)���������
				//������true���ʾ��ִ��MotionEvent.ACTION_DOWN����MotionEvent.ACTION_MOVE���� MotionEvent.ACTION_UP
				//�ʹ�������ˣ�������û������һ�����������޷���������¼�
				return false;
			}
		});
		
		//"��͸��","������","��ʿ��","������","ƻ����"
		int [] ids ={R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_green};
		SharedPreferences preference = getSharedPreferences("settings", MODE_PRIVATE);
		int selecteWhich = preference.getInt("which", 0);
		myToastTv.setBackgroundResource(ids[selecteWhich]);
		 
		
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE//�����Զ�����˾������Ӧ�����¼�����ȥ�����
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
       
        //params.type = WindowManager.LayoutParams.TYPE_TOAST;//�����������޷���Ӧ��Ļ�����¼�������ȥ��������Ϊ�绰���ȼ�
        //�绰���ȼ����ǵ����Ȩ��android.permission.SYSTEM_ALERT_WINDOW
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        
        params.gravity = Gravity.TOP+Gravity.LEFT;//��ʾ���Ͻ�
        params.x = configPreference.getInt("paramsX", 0);
        params.y = configPreference.getInt("paramsY", 0);
        
		//windowManger�Ѿ���service��onCreate��ʵ����
		windowManger.addView(myToastTv, params);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//��ʼ���绰������
		telephoe = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		//��������
		phoneStateListener =new MyPhoneStateListener();
		telephoe.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//ע���Ⲧ�绰������(��service��ע��㲥�����ߵĺô��ǣ����������㲥�����߸���ע�ᣬ�ر�������ȡ��ע�ᣬ�����Ⲧ�������������ʾͳһ����)
		outCallReceiver = new OutCallReceiver();
		IntentFilter filter =new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(outCallReceiver, filter);
		
		//ʵ������������ߣ�
		//��Ҫ���������ڵ�һЩ״̬�����ԡ�view���ӡ�ɾ�������¡�����˳����Ϣ�ռ��ʹ���ȡ�
		//ͨ��Context.getSystemService(Context.WINDOW_SERVICE)�ķ�ʽ���Ի��WindowManager��ʵ����
		//Android������չʾ���û�����window��view��activity�����������Ҫ�Ǵ���һЩ�߼����⣬�����������ڹ����������ڡ�
		windowManger = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch(state){
				case TelephonyManager.CALL_STATE_RINGING:
					String address = PhoneAddressQueryUtils.queryAddress(getFilesDir()+"/address.db", incomingNumber);
					//��˾��ʾ���������
					//Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
					myToast(address);
					break;
					
				case TelephonyManager.CALL_STATE_IDLE://����״̬
					if(myToastTv!=null){
						windowManger.removeView(myToastTv);
					}
					
				default:
					break;
					}
			
				
		}
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//ȡ�������绰
		telephoe.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		phoneStateListener = null;
		
		//ȡ��ע���Ⲧ�绰������
		unregisterReceiver(outCallReceiver);
		outCallReceiver = null;
	}

}
