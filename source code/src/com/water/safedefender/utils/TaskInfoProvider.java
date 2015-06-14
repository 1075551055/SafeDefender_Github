package com.water.safedefender.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.water.safedefender.R;
import com.water.safedefender.domian.TaskInfo;

public class TaskInfoProvider {

	/**
	 * 获取手机所有进程信息
	 * @param context 上下文
	 * @return
	 */
	public static List<TaskInfo> getAllTaskInfos(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
	    for (RunningAppProcessInfo runningAppProcessInfo : processInfos) {
	    	TaskInfo taskInfo = new TaskInfo();
			//应用程序的包名
	    	String packageName = runningAppProcessInfo.processName;
	    	taskInfo.setPackagename(packageName);
	    	//注意：这里的MemoryInfo用的是debug下的包，文档有问题
	    	MemoryInfo[] memoryInfos =  am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
	    	long memerySize = memoryInfos[0].getTotalPrivateDirty()*1024;
	    	taskInfo.setMemerySize(memerySize);
	    	
	    	try {
	    		//根据包名获取应用程序的清单文件详细信息
				ApplicationInfo aInfo = pm.getApplicationInfo(packageName, 0);
				//根据应用程序信息获取应用程序的图标，名称
				Drawable icon = aInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = aInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				if((aInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
					//用户程序那么就是用户进程了
					taskInfo.setUserTask(true);
				}else{
					//系统程序那么就是系统进程了
					taskInfo.setUserTask(false);
				}
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.taskinfo_default));
				taskInfo.setName(packageName);
				
			}
	    	taskInfos.add(taskInfo);
	    	
		}
	    return taskInfos;
	}
}
