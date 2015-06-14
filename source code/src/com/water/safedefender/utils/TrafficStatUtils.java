package com.water.safedefender.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;

/**
 * ��ȡ����ͳ��
 * @author Administrator
 *
 */
public class TrafficStatUtils {
	//��ȡ����ͳ�������ַ�����
	//1.����ͨ����ȡϵͳ�ײ��proc/uid_stat/uid/���������ļ���һ���Ǽ�¼����/���ص�������һ���Ǽ�¼����/�ϴ�������
	//2.ʹ��TrafficStats
	/**
	 * ��ȡ���ص�����
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
			//ͨ��uid��ȡ����/���ص�����
			return TrafficStats.getUidRxBytes(uid)==-1?0:TrafficStats.getUidRxBytes(uid);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * ��ȡ�ϴ�������
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
			//ͨ��uid��ȡ����/�ϴ�������
			return TrafficStats.getUidTxBytes(uid)==-1?0:TrafficStats.getUidTxBytes(uid);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}
