package com.water.safedefender.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	/*
	 * 判断某个服务是否还在运行中
	 * serviceName:服务的名称
	 */
	public static boolean serviceIsRunning(Context context,String serviceName){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取到正在运行的服务
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
