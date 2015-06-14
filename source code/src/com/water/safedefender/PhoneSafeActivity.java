package com.water.safedefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneSafeActivity extends Activity {

	SharedPreferences preference;
	private TextView tv_contactPhone;
	private ImageView iv_isOpenProtect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preference = getSharedPreferences("config", MODE_PRIVATE);
		//��ȡ�Ƿ�����������
		boolean isConfig = preference.getBoolean("isConfig", false);
		if(isConfig){
			//�Ѿ�����������������������
			setContentView(R.layout.activity_phone_safe);
			//��ȡ�Ƿ����˷�������
			boolean isOpenProtect = preference.getBoolean("isOpenProtect", false);
			tv_contactPhone = (TextView) findViewById(R.id.tv_contactPhone);
			iv_isOpenProtect = (ImageView) findViewById(R.id.iv_isOpenProtect);
			tv_contactPhone.setText(preference.getString("securityPhone", ""));
			iv_isOpenProtect.setImageResource(isOpenProtect?R.drawable.lock:R.drawable.unlock);
		}else{
			//û����������������������򵼵�һ������
			Intent intent = new Intent(PhoneSafeActivity.this,PhoneSafeSetupOneActivity.class);
			startActivity(intent);
			finish();
		}
		
	}

	/*
	 * ���½����ֻ�����������ҳ��
	 */
	public void reSetup(View v){
		Intent intent = new Intent(PhoneSafeActivity.this,PhoneSafeSetupOneActivity.class);
		startActivity(intent);
		finish();
	}

}
