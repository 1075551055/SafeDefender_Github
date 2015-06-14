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
	 * ��ȡ�ֻ����н�����Ϣ
	 * @param context ������
	 * @return
	 */
	public static List<TaskInfo> getAllTaskInfos(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
	    for (RunningAppProcessInfo runningAppProcessInfo : processInfos) {
	    	TaskInfo taskInfo = new TaskInfo();
			//Ӧ�ó���İ���
	    	String packageName = runningAppProcessInfo.processName;
	    	taskInfo.setPackagename(packageName);
	    	//ע�⣺�����MemoryInfo�õ���debug�µİ����ĵ�������
	    	MemoryInfo[] memoryInfos =  am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
	    	long memerySize = memoryInfos[0].getTotalPrivateDirty()*1024;
	    	taskInfo.setMemerySize(memerySize);
	    	
	    	try {
	    		//���ݰ�����ȡӦ�ó�����嵥�ļ���ϸ��Ϣ
				ApplicationInfo aInfo = pm.getApplicationInfo(packageName, 0);
				//����Ӧ�ó�����Ϣ��ȡӦ�ó����ͼ�꣬����
				Drawable icon = aInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = aInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				if((aInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
					//�û�������ô�����û�������
					taskInfo.setUserTask(true);
				}else{
					//ϵͳ������ô����ϵͳ������
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
