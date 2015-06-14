package com.water.safedefender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactsActivity extends Activity {

	private ListView lvContacts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		lvContacts = (ListView) findViewById(R.id.lv_contacts);
		final List<Map<String,String>> contactsMap = getContacts();
		lvContacts.setAdapter(new SimpleAdapter(
				ContactsActivity.this,
				contactsMap, R.layout.contacts, 
				new String[]{"name","phone"}, 
				new int[]{R.id.tv_name,R.id.tv_phone}) );
		
		lvContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = contactsMap.get(position).get("phone");
				//通过意图返回数据
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				//关闭界面
				finish();
			}
		});
	}

	/*
	 * 获取手机联系人，通过内容提供者获取
	 */
	private List<Map<String,String>> getContacts(){
		List<Map<String,String>> contacts = new ArrayList<Map<String,String>>();
		//1.获得内容提供者访问对象
		ContentResolver resolver = getContentResolver();
		//2.查询
		Cursor contactsCursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI,
				new String[]{"_id"}, null, null, null);
		if(contactsCursor!=null){
			
			while(contactsCursor.moveToNext()){
				String contactId = contactsCursor.getString(0);
				if(contactId!=null){
					Map<String,String> contactMap = new HashMap<String, String>();
					Cursor dataCursor = resolver.query(
							ContactsContract.Data.CONTENT_URI, 
							new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{contactId}, null);
					while(dataCursor.moveToNext()){
						String data1 = dataCursor.getString(0);
						String mimeType = dataCursor.getString(1);
						if(mimeType.equals("vnd.android.cursor.item/name")){
							contactMap.put("name", data1);
						}else if(mimeType.equals("vnd.android.cursor.item/phone_v2")){
							contactMap.put("phone", data1);
							
						}
					}
					contacts.add(contactMap);
					dataCursor.close();
				}
			}
		}
		contactsCursor.close();
		return contacts;
	}


}
