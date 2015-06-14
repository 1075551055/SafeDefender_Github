package com.water.safedefender.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class PhoneAddressQueryUtils {

	public static String queryAddress(String dbPath,String phoneNumber){
		if(phoneNumber.length()<7){
			return phoneNumber;
		}
		String address = phoneNumber;
		//��assets�µ��ļ�webview����ֱ��ͨ��src·�����ʣ���������openDatabase�򲻿��ԣ����Կ���
		//�ڳ���һ��ʼ���м�splash�����ʱ��assets�µ����ݿ��ļ�copy��data/data/����/file�ļ����£�Ȼ��
		//�Ϳ���ͨ����·�����з�����
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select location from data2 where id = (select outkey from data1 where id = ?)";
		Cursor cursor = db.rawQuery(sql, new String[]{phoneNumber.substring(0, 7)});
		if(cursor!=null && cursor.getCount()>0){
			while(cursor.moveToNext()){
				address = cursor.getString(0);
				
			}
		}
		cursor.close();
		cursor = null;
		return address;
	}
}
