package com.water.safedefender.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberListDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberListDBOpenHelper(Context context) {
		//数据库名为：BlackNumber.db,版本号为：1
		super(context, "BlackNumber.db", null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table blacknumber(_id integer primary key autoincrement,number varchar(20),mode varchar(2));";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
