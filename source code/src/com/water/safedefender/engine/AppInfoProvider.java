package com.water.safedefender.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.water.safedefender.domain.AppInfo;

public class AppInfoProvider {

	/**
	 * ��ȡ���еİ�װ��Ӧ�ó�����Ϣ
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			//packageInfo�൱��apk�����嵥�ļ�
			String packagename = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);//ͼ�꣬packageInfo.applicationInfo�൱��application�ڵ�
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();//Ӧ�ó��������
			int uid = packageInfo.applicationInfo.uid;

			AppInfo appInfo = new AppInfo();
			appInfo.setUid(uid);
			int flags = packageInfo.applicationInfo.flags;//Ӧ�ó�����Ϣ��ǣ��൱���û��ύ�Ĵ��
			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				//�û�����
				appInfo.setUserApp(true);
			}else{
				//ϵͳ����
				appInfo.setUserApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//�ֻ��ڴ�
				appInfo.setInrom(true);
			}else{
				//�ⲿ�洢
				appInfo.setInrom(false);
			}
			
			
			appInfo.setPackagename(packagename);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
			
		}
		return appInfos;
	}
}
