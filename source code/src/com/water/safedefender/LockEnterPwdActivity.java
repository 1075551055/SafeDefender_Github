package com.water.safedefender;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//���嵥�ļ�������������ģʽΪsingleInstance.android:launchMode="singleInstance" ,
//����ֻ�ڶ�����һ������ջ�У������Ϳ��Ա�����ֻ���ʿ��activity����ͬһ������ջ��
//����������android:excludeFromRecents="true"���ø�activity������鿴����activity�б���ɾ��������С���ӾͿ��Բ鿴�������activity��
public class LockEnterPwdActivity extends Activity {

	private EditText et_pwd;
	private String packagename;
	private PackageManager pm;
	private TextView tv_name;
	private ImageView iv_icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_enter_pwd);
		
	    tv_name = (TextView) findViewById(R.id.tv_name);
	    iv_icon = (ImageView) findViewById(R.id.iv_icon);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		pm = getPackageManager();
		
		//��ȡ���ǰactivity����ͼ������watchdogservice���ݹ�����intent
		Intent intent = getIntent();
		if(intent!=null){
			packagename = intent.getStringExtra("packagename");
			
			//�������ƺ�ͼ��
			ApplicationInfo aInfo;
			try {
				aInfo = pm.getApplicationInfo(packagename, 0);
				tv_name.setText(aInfo.loadLabel(pm).toString());
				iv_icon.setImageDrawable(aInfo.loadIcon(pm));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * ���˺��˼�ִ�еķ���
	 */
	@Override
	public void onBackPressed() {
		//�ص�����
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
	
	@Override
	protected void onStop() {
		//��activity���ɼ���ʱ��Ӧ�ý���ǰactivity���ٵ����������Ը����˵�Ӧ�ô򿪵�ʱ������ʾͬһ��activity
		//�����Ӧ�ó������ƺ�ͼ��Ͳ���ȷ��
		super.onStop();
		finish();
	}

	public void enter(View v){
		String pwd = et_pwd.getText().toString();
		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(this, "���벻��Ϊ��", 0).show();
			return;
		}
		if(pwd.equals("123")){
			//������ȷ�󣬸���watchdog�����ټ������ˡ�
			//������ͨѶ��1.����ͨ����service��ʹ��service�еķ�����
		    //		    2.��������ͨ�����͹㲥������Ϣ���ݡ������ã�
			//���͹㲥֪ͨwatchdog�������Ѿ���ȷ�ˡ�
			Intent intent = new Intent();
			intent.setAction("com.water.safedefender.stopprotect");
			//ͬ����ͼ��������
			intent.putExtra("packagename", packagename);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(this, "�������", 0).show();
		}
	}

}
