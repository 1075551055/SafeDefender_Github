package com.water.safedefender.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	/*
	 * �ж�ĳ�������Ƿ���������
	 * serviceName:���������
	 */
	public static boolean serviceIsRunning(Context context,String serviceName){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//��ȡ���������еķ���
		List<RunningServiceInfo> services =  activityManager.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : services) {
			String name = runningServiceInfo.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}
}
