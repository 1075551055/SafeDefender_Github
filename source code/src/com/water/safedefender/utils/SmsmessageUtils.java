package com.water.safedefender.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsmessageUtils {
	
	/**
	 * 将变化的代码抽取成接口
	 * @author Administrator
	 *
	 */
	public interface BackupCallback{
		/**
		 * 备份短信前设置进度最大值
		 * @param max 总进度
		 */
		public void beforeBackup(int max);
		
		/**
		 * 备份过程中设置进度
		 * @param progress 备份进度
		 */
		public void onSmsBackup(int progress);
	}

	/**
	 * 短信备份
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 * 
	 */
	public static void backupSmsMessage(BackupCallback callBack,Context context) throws IllegalArgumentException, IllegalStateException, FileNotFoundException, IOException{
		ContentResolver resolver = context.getContentResolver();
		//保存的位置：sdcard
		File file = new File(Environment.getExternalStorageDirectory(),"SmsMessage_bak.xml");;
		
		//1.初始化xml序列化器
		XmlSerializer serializer = Xml.newSerializer();
		//2.序列化数据到哪个流中
		serializer.setOutput(new FileOutputStream(file), "utf-8");
		//3.开始序列化
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smses");
		
		Uri uri = Uri.parse("content://sms/");
		//通过内容解析器获取短信
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, 
				null, 
				null, 
				null);
		if(cursor!=null&&cursor.getCount()>0){
			//设置进度条最大值
			//pd.setMax(cursor.getCount());
			callBack.beforeBackup(cursor.getCount());
			int progress = 0;
			while(cursor.moveToNext()){
				String body = cursor.getString(0);
				String address = cursor.getString(1);
				String type = cursor.getString(2);
				String date = cursor.getString(3);
				serializer.startTag(null, "sms");
				//body
				serializer.startTag(null, "body");
				serializer.text(body);
				serializer.endTag(null, "body");
				
				//address
				serializer.startTag(null, "address");
				serializer.text(address);
				serializer.endTag(null, "address");
				
				//type
				serializer.startTag(null, "type");
				serializer.text(type);
				serializer.endTag(null, "type");
				
				//date
				serializer.startTag(null, "date");
				serializer.text(date);
				serializer.endTag(null, "date");
				
				serializer.endTag(null, "sms");
				progress++;
				//pd.setProgress(progress);
				callBack.onSmsBackup(progress);
			}
		}
		cursor.close();
		serializer.endTag(null, "smses");
		serializer.endDocument();
		
	}
}
