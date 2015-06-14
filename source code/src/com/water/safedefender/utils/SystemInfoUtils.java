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
	 * ��ȡ�������н��̵�����
	 * @return
	 */
	public static int getRunningServiceCount(Context context){
//		PackageManager//����൱��window�µĳ��������
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//�൱��window�µ����������
		List<RunningAppProcessInfo> runningProcess = am.getRunningAppProcesses();
		return runningProcess.size();
	}
	
	/**
	 * ��ȡ�ֻ������ڴ�
	 * @param context ������
	 * @return
	 */
	public static long getAvailableMemery(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//�൱��window�µ����������
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * ��ȡ�ֻ��ܹ��ڴ�
	 * @param context ������
	 * @return
	 */
	public static long getTotalMemery(Context context){
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//�൱��window�µ����������
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		return outInfo.totalMem;//��api��������android2.3ϵͳ
		
		//���Բο�ϵͳsetting��Դ�룬д�����ݵͰ汾�Ĵ���
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
