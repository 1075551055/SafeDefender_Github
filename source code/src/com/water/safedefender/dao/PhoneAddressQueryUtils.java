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
		//在assets下的文件webview可以直接通过src路径访问，但是下面openDatabase则不可以，所以可以
		//在程序一开始运行即splash界面的时候讲assets下的数据库文件copy到data/data/包名/file文件夹下，然后
		//就可以通过该路径进行访问了
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
