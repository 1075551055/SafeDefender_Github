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
	 * ���ݿ�ı�Ĺ㲥������
	 * @author Administrator
	 *
	 */
	private class DataBaseChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//���ݿ����ݱ仯�����¸����ڴ漯��
			potectPackagenames = dao.findAll();
		}
		
	}
	
	/**
	 * �ֻ������Ĺ㲥������
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
	 * ���յ�LockEnterPwdActivity����������ȷ���͹����Ĺ㲥
	 * @author Administrator
	 *
	 */
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��ȡ��LockEnterPwdActivity���͹����Ĺ㲥��ͼ��stopProtectPackage������Ҫֹͣ������Ӧ�ó���������ȷ�ˣ�
			stopProtectPackage = intent.getStringExtra("packagename");
		}
		
	}
	
	@Override
	public void onCreate() {
		//ע�����ݿ�ı�Ĺ㲥������
		dbChangeReceiver = new DataBaseChangeReceiver();
		registerReceiver(dbChangeReceiver, new IntentFilter("com.water.safedefender.lockchange"));
		
		//ע�������㲥������
		offReceiver = new ScreenOffReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		//
		
		innerReceiver= new InnerReceiver();
		//ע����Ҫֹͣwatchdog���������㲥������
		registerReceiver(innerReceiver, new IntentFilter("com.water.safedefender.stopprotect"));
		
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		flag = true;
		dao = new AppLockDao(this);
		potectPackagenames= dao.findAll();
		
		//�����������activity
		intent = new Intent(WatchDogService.this, LockEnterPwdActivity.class);
		//ע�⣺service��û������ջ��Ϣ�ģ���service�п���activity��Ҫָ��activity���е�����ջ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				while(flag){
					//��ȡ�������е�����ջ(���ʹ�õ�����ջ��ŵ����ϵ���ǰ�����˵�Ѿ��ź����ˣ���ǰʹ�õ�����ջ�ͷŵ��˵�һλ)
					//Return a list of the tasks that are currently running,  with 
					//the most recent being first and older ones after in order.
					List<RunningTaskInfo> taskInfos = am.getRunningTasks(1);
					//��ȡ��ǰ�û�������activity���Ӧ�İ���
					String packagename = taskInfos.get(0).topActivity.getPackageName();
//					System.out.println(packagename);
					if(potectPackagenames.contains(packagename)){//��ѯ���ݿ�Ƚ��������Ըĳɴ��ڴ��в�ѯ
						if(packagename.equals(stopProtectPackage)){
							//����ð����պ�����Ҫֹͣ������Ӧ�ó��������ֹͣ����
						}else{
							
							//���������ݸ�LockEnterPwdActivity
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
