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
	 * ���仯�Ĵ����ȡ�ɽӿ�
	 * @author Administrator
	 *
	 */
	public interface BackupCallback{
		/**
		 * ���ݶ���ǰ���ý������ֵ
		 * @param max �ܽ���
		 */
		public void beforeBackup(int max);
		
		/**
		 * ���ݹ��������ý���
		 * @param progress ���ݽ���
		 */
		public void onSmsBackup(int progress);
	}

	/**
	 * ���ű���
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 * 
	 */
	public static void backupSmsMessage(BackupCallback callBack,Context context) throws IllegalArgumentException, IllegalStateException, FileNotFoundException, IOException{
		ContentResolver resolver = context.getContentResolver();
		//�����λ�ã�sdcard
		File file = new File(Environment.getExternalStorageDirectory(),"SmsMessage_bak.xml");;
		
		//1.��ʼ��xml���л���
		XmlSerializer serializer = Xml.newSerializer();
		//2.���л����ݵ��ĸ�����
		serializer.setOutput(new FileOutputStream(file), "utf-8");
		//3.��ʼ���л�
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smses");
		
		Uri uri = Uri.parse("content://sms/");
		//ͨ�����ݽ�������ȡ����
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, 
				null, 
				null, 
				null);
		if(cursor!=null&&cursor.getCount()>0){
			//���ý��������ֵ
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
