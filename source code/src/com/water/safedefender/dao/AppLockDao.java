package com.water.safedefender.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.water.safedefender.db.AppLockDBOpenHelper;

public class AppLockDao {

	private Context context;
	private AppLockDBOpenHelper helper;
	public AppLockDao(Context context){
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	
	public void add(String packagename){
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			values.put("packagename", packagename);
			db.insert("lock", null, values);
			db.close();
		}
		
		//发送广播，数据库内容改变了
		Intent intent = new Intent();
		intent.setAction("com.water.safedefender.lockchange");
		context.sendBroadcast(intent);
	}
	
	public void delete(String packagename){
		SQLiteDatabase db = helper.getReadableDatabase();
		if(db.isOpen()){
			db.delete("lock", "packagename=?", new String[]{packagename});
			db.close();
		}
		
		//发送广播，数据库内容改变了
		Intent intent = new Intent();
		intent.setAction("com.water.safedefender.lockchange");
		context.sendBroadcast(intent);
	}
	
	public boolean find(String packagename){
		SQLiteDatabase db = helper.getReadableDatabase();
		boolean queryResult = false;
		if(db.isOpen()){
			Cursor cursor = db.query("lock", null, "packagename=?", new String[]{packagename}, null, null, null);
			if(cursor.moveToNext()){
				queryResult = true;
			}
		}
		return queryResult;
	}
	
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		List<String> packagenames = new ArrayList<String>();
		if(db.isOpen()){
			Cursor cursor = db.query("lock", new String[]{"packagename"}, null, null, null, null, null);
			while(cursor.moveToNext()){
				packagenames.add(cursor.getString(0));
			}
			cursor.close();
		}
		db.close();
		return packagenames;
	}
	
}
