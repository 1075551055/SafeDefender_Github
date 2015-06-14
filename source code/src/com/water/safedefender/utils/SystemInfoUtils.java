package com.water.safedefender.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfoUtils {

	/**
	 * 获取正在运行进程的数量
	 * @return
	 */
	public static int getRunningServiceCount(Context context){
//		PackageManager//这个相当于window下的程序管理器
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//相当于window下的任务管理器
		List<RunningAppProcessInfo> runningProcess = am.getRunningAppProcesses();
		return runningProcess.size();
	}
	
	/**
	 * 获取手机可用内存
	 * @param context 上下文
	 * @return
	 */
	public static long getAvailableMemery(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//相当于window下的任务管理器
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * 获取手机总共内存
	 * @param context 上下文
	 * @return
	 */
	public static long getTotalMemery(Context context){
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//相当于window下的任务管理器
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		return outInfo.totalMem;//该api不适用于android2.3系统
		
		//可以参考系统setting的源码，写出兼容低版本的代码
		File file = new File("/proc/meminfo");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			for (char c : line.toCharArray()) {
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		 
	}
}
