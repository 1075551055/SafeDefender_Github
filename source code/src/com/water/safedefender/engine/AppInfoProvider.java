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
	 * 获取所有的安装的应用程序信息
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			//packageInfo相当于apk包的清单文件
			String packagename = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);//图标，packageInfo.applicationInfo相当于application节点
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();//应用程序的名称
			int uid = packageInfo.applicationInfo.uid;

			AppInfo appInfo = new AppInfo();
			appInfo.setUid(uid);
			int flags = packageInfo.applicationInfo.flags;//应用程序信息标记，相当于用户提交的答卷
			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				//用户程序
				appInfo.setUserApp(true);
			}else{
				//系统程序
				appInfo.setUserApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//手机内存
				appInfo.setInrom(true);
			}else{
				//外部存储
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
