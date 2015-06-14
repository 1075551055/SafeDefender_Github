package com.water.safedefender.service;

import java.util.List;

import com.water.safedefender.LockEnterPwdActivity;
import com.water.safedefender.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WatchDogService extends Service {

	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private InnerReceiver innerReceiver;
	private String stopProtectPackage;
	private ScreenOffReceiver offReceiver;
	private List<String> potectPackagenames;
	private Intent intent ;
	private DataBaseChangeReceiver dbChangeReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 数据库改变的广播接收者
	 * @author Administrator
	 *
	 */
	private class DataBaseChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//数据库内容变化后重新更新内存集合
			potectPackagenames = dao.findAll();
		}
		
	}
	
	/**
	 * 手机锁屏的广播接收者
	 * @author Administrator
	 *
	 */
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			stopProtectPackage = null;
		}
		
	}
	
	
	
	/**
	 * 接收到LockEnterPwdActivity输入密码正确后发送过来的广播
	 * @author Administrator
	 *
	 */
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取到LockEnterPwdActivity发送过来的广播意图，stopProtectPackage就是需要停止保护的应用程序（密码正确了）
			stopProtectPackage = intent.getStringExtra("packagename");
		}
		
	}
	
	@Override
	public void onCreate() {
		//注册数据库改变的广播接收者
		dbChangeReceiver = new DataBaseChangeReceiver();
		registerReceiver(dbChangeReceiver, new IntentFilter("com.water.safedefender.lockchange"));
		
		//注册锁屏广播接收者
		offReceiver = new ScreenOffReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		//
		
		innerReceiver= new InnerReceiver();
		//注册需要停止watchdog监听保护广播接收者
		registerReceiver(innerReceiver, new IntentFilter("com.water.safedefender.stopprotect"));
		
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		flag = true;
		dao = new AppLockDao(this);
		potectPackagenames= dao.findAll();
		
		//打开输入密码的activity
		intent = new Intent(WatchDogService.this, LockEnterPwdActivity.class);
		//注意：service是没有任务栈信息的，在service中开启activity，要指定activity运行的任务栈
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				while(flag){
					//获取正在运行的任务栈(最近使用的任务栈会放到集合的最前面就是说已经排好序了，当前使用的任务栈就放到了第一位)
					//Return a list of the tasks that are currently running,  with 
					//the most recent being first and older ones after in order.
					List<RunningTaskInfo> taskInfos = am.getRunningTasks(1);
					//获取当前用户操作的activity相对应的包名
					String packagename = taskInfos.get(0).topActivity.getPackageName();
//					System.out.println(packagename);
					if(potectPackagenames.contains(packagename)){//查询数据库比较慢，所以改成从内存中查询
						if(packagename.equals(stopProtectPackage)){
							//如果该包名刚好是需要停止保护的应用程序包名则停止保护
						}else{
							
							//将包名传递给LockEnterPwdActivity
							intent.putExtra("packagename", packagename);
							startActivity(intent);
						}
						
					}
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
		}.start();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		
		unregisterReceiver(offReceiver);
		offReceiver = null;
		
		unregisterReceiver(dbChangeReceiver);
		dbChangeReceiver = null;
		
		flag = false;
		super.onDestroy();
	}

}
