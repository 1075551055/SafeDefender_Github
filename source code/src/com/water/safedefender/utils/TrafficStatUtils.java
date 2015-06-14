package com.water.safedefender.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;

/**
 * 获取流量统计
 * @author Administrator
 *
 */
public class TrafficStatUtils {
	//获取流量统计有两种方法：
	//1.可以通过读取系统底层的proc/uid_stat/uid/下有两个文件，一个是记录接受/下载的流量，一个是记录发送/上传的流量
	//2.使用TrafficStats
	/**
	 * 获取下载的流量
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static long getDownloadTraffic(Context context,String packageName){
		PackageManager pm = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = pm.getApplicationInfo(packageName, 0);
			int uid = applicationInfo.uid;
			//通过uid获取接受/下载的流量
			return TrafficStats.getUidRxBytes(uid)==-1?0:TrafficStats.getUidRxBytes(uid);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 获取上传的流量
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static long getUploadTraffic(Context context,String packageName){
		PackageManager pm = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = pm.getApplicationInfo(packageName, 0);
			int uid = applicationInfo.uid;
			//通过uid获取发送/上传的流量
			return TrafficStats.getUidTxBytes(uid)==-1?0:TrafficStats.getUidTxBytes(uid);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}
