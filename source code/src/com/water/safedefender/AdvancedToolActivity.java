package com.water.safedefender;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.water.safedefender.utils.SmsmessageUtils;
import com.water.safedefender.utils.SmsmessageUtils.BackupCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AdvancedToolActivity extends Activity {

	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_tool);
	}

	/*
	 * ��ѯ�绰���������
	 */
	public void queryPhoneNumber(View v){
		Intent intent = new Intent(AdvancedToolActivity.this,PhoneAddressQueryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * ���ű���
	 */
	public void backupSmsMessage(View v){
		pd = new ProgressDialog(this);
		pd.setMessage("���ڱ��ݶ���");
		//ˮƽ��ʽ��ʾ
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		//���ܶ��ŵ�������ܶ࣬���Ա��ݶ��ŵĲ���Ӧ�÷ŵ����߳���
		new Thread(){
			public void run() {
				try {
					SmsmessageUtils.backupSmsMessage(new BackupCallback() {
						
						@Override
						public void onSmsBackup(int progress) {
							// TODO Auto-generated method stub
							pd.setProgress(progress);
						}
						
						@Override
						public void beforeBackup(int max) {
							// TODO Auto-generated method stub
							pd.setMax(max);
						}
					},AdvancedToolActivity.this);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(AdvancedToolActivity.this, "backup successfully������", 0).show();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(AdvancedToolActivity.this, "backup failed������", 0).show();
						}
					});
				}finally{
					//ȡ��������
					pd.dismiss();
				}
			};
		}.start();
		
	}
	
	/**
	 * ���Ż�ԭ
	 */
	public void restoreSmsMessage(View v){
		
	}
}
