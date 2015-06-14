package com.water.safedefender.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	public AppLockDBOpenHelper(Context context) {
		//���ݿ���Ϊ��applock.db,�汾��Ϊ��1
		super(context, "applock.db", null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table lock(_id integer primary key autoincrement,packagename varchar(100));";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
