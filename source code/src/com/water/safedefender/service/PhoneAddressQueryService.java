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
	 * 广播接收者：接收外拨电话。广播接收者不但可以在清单文件注册还可以在代码注册
	 */
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取到外拨号码
			String phoneNumber = getResultData();//模拟器中号码带110前缀
			//查询数据库获取归属地
			String address = PhoneAddressQueryUtils.queryAddress(context.getFilesDir()+"/address.db", phoneNumber);
			//Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			myToast(address);
		}

	}

	

	
	//窗体管理者
	private WindowManager windowManger;
	//自定义吐司要显示的view
	private View myToastTv;
	//自定义吐司参数
	private final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	//记录双击的数组
	long[] mHits=new long[2];
	
	/**
	 * 
	 * 自定义吐司
	 */
	private void myToast(String text){
		/*myToastTv = new TextView(this);
		myToastTv.setText(text);
		myToastTv.setTextSize(22);*/
		
		configPreference = getSharedPreferences("config", MODE_PRIVATE);
		
		myToastTv =  View.inflate(this, R.layout.mytoast_background, null);
		TextView tv_address = (TextView) myToastTv.findViewById(R.id.tv_address);
		tv_address.setText(text);
		
		//myToastTv点击事件
		myToastTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//从数组index:1开始拷贝数组
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				//给最后一个数组元素赋值：手机开机到现在的时间
				mHits[mHits.length-1]=SystemClock.uptimeMillis();
				//判断数组第一个元素和当前手机开机时间的时间差,因为数组第一个元素保存的是上一次点击的时间，
				//只要这一次点击的时间和上一次的时间差小于或等于50则表示双击了
				if(mHits[0]>=SystemClock.uptimeMillis()-500){
					//让吐司屏幕居中
					params.x = windowManger.getDefaultDisplay().getWidth()/2-myToastTv.getWidth()/2;
					windowManger.updateViewLayout(myToastTv, params);
					//保存位置
					Editor et = configPreference.edit();
					et.putInt("paramsX", params.x);
					et.commit();
				}
			}
		});
		
		//给myToastTv设置触摸监听器
		myToastTv.setOnTouchListener(new OnTouchListener() {
			//定义手指的初始化位置
			int startX;
			int startY;
			//定义手指移动后的位置
			int newX;
			int newY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){//getAction获取手指触摸的动作
					case MotionEvent.ACTION_DOWN://手指按下屏幕
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						Log.i("test", "初始化位置-》startX:"+startX+";startY:"+startY);
						break;
					
					case MotionEvent.ACTION_MOVE://手指在屏幕移动
						newX = (int) event.getRawX();
						newY = (int) event.getRawY();
						Log.i("test", "移动后的位置-》newX:"+newX+";newY:"+newY);
						//计算移动后的x和y轴偏移量
						int dx = newX - startX;
						int dy = newY - startY;
						Log.i("test", "手指偏移量-》dx:"+dx+";dy:"+dy);
						
						//更新吐司位置
						//注意：params.x和params.y都是根据params.gravity进行相对位置的变化的，默认是屏幕居中，
						//已经在下面设置了params.gravity相对屏幕左上角对齐
						params.x+=dx;
						params.y+=dy;
						windowManger.updateViewLayout(myToastTv, params);
						
						//判断x,y的边界值
						if(params.x<0){//左边x轴
							params.x=0;
						}
						if(params.y<0){//上边y轴
							params.y=0;
						}
						//右边x轴
						if(params.x>windowManger.getDefaultDisplay().getWidth()-myToastTv.getWidth()){
							params.x=windowManger.getDefaultDisplay().getWidth()-myToastTv.getWidth();
						}
						//底部y轴
						if(params.y>windowManger.getDefaultDisplay().getHeight()-myToastTv.getHeight()){
							params.y=windowManger.getDefaultDisplay().getHeight()-myToastTv.getHeight();
						}
						
						
						//保存位置
						Editor et = configPreference.edit();
						et.putInt("paramsX", params.x);
						et.putInt("paramsY", params.y);
						et.commit();
						
						break;
						
					case MotionEvent.ACTION_UP://手指离开屏幕
						
						break;
				}
				//true表示事件到我这里处理完毕，不要让父控件或者父布局响应触摸事件
				//假如myToastTv设置了点击事件(点击事件是：按下，停留，抬起三个动作的组合)，如果这里
				//返回了true则表示当执行MotionEvent.ACTION_DOWN或者MotionEvent.ACTION_MOVE或者 MotionEvent.ACTION_UP
				//就处理完毕了，动作并没有连着一起做，所以无法触发点击事件
				return false;
			}
		});
		
		//"半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int [] ids ={R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_green};
		SharedPreferences preference = getSharedPreferences("settings", MODE_PRIVATE);
		int selecteWhich = preference.getInt("which", 0);
		myToastTv.setBackgroundResource(ids[selecteWhich]);
		 
		
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE//想让自定义吐司可以响应触摸事件必须去掉这个
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
       
        //params.type = WindowManager.LayoutParams.TYPE_TOAST;//这种类型是无法响应屏幕触摸事件，所以去掉，设置为电话优先级
        //电话优先级，记得添加权限android.permission.SYSTEM_ALERT_WINDOW
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        
        params.gravity = Gravity.TOP+Gravity.LEFT;//表示左上角
        params.x = configPreference.getInt("paramsX", 0);
        params.y = configPreference.getInt("paramsY", 0);
        
		//windowManger已经在service的onCreate中实例化
		windowManger.addView(myToastTv, params);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//初始化电话管理器
		telephoe = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		//监听来电
		phoneStateListener =new MyPhoneStateListener();
		telephoe.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//注册外拨电话接收者(在service中注册广播接收者的好处是：当服务开启广播接收者跟着注册，关闭则随着取消注册，做到外拨和来电归属地显示统一设置)
		outCallReceiver = new OutCallReceiver();
		IntentFilter filter =new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(outCallReceiver, filter);
		
		//实例化窗体管理者：
		//主要用来管理窗口的一些状态、属性、view增加、删除、更新、窗口顺序、消息收集和处理等。
		//通过Context.getSystemService(Context.WINDOW_SERVICE)的方式可以获得WindowManager的实例。
		//Android中真正展示给用户的是window和view，activity所起的作用主要是处理一些逻辑问题，比如生命周期管理及建立窗口。
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
					//吐司显示来电归属地
					//Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
					myToast(address);
					break;
					
				case TelephonyManager.CALL_STATE_IDLE://空闲状态
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
		//取消监听电话
		telephoe.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		phoneStateListener = null;
		
		//取消注册外拨电话接收者
		unregisterReceiver(outCallReceiver);
		outCallReceiver = null;
	}

}
