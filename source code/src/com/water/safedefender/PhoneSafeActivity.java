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
		//获取是否做过设置向导
		boolean isConfig = preference.getBoolean("isConfig", false);
		if(isConfig){
			//已经做过设置向导则进入防盗界面
			setContentView(R.layout.activity_phone_safe);
			//获取是否开启了防盗保护
			boolean isOpenProtect = preference.getBoolean("isOpenProtect", false);
			tv_contactPhone = (TextView) findViewById(R.id.tv_contactPhone);
			iv_isOpenProtect = (ImageView) findViewById(R.id.iv_isOpenProtect);
			tv_contactPhone.setText(preference.getString("securityPhone", ""));
			iv_isOpenProtect.setImageResource(isOpenProtect?R.drawable.lock:R.drawable.unlock);
		}else{
			//没有做过设置向导则进入设置向导第一个界面
			Intent intent = new Intent(PhoneSafeActivity.this,PhoneSafeSetupOneActivity.class);
			startActivity(intent);
			finish();
		}
		
	}

	/*
	 * 重新进入手机防盗设置向导页面
	 */
	public void reSetup(View v){
		Intent intent = new Intent(PhoneSafeActivity.this,PhoneSafeSetupOneActivity.class);
		startActivity(intent);
		finish();
	}

}
