package com.water.safedefender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneSafeSetupThirdActivity extends BaseSetupActivity {

	private EditText etPhone;
	private SharedPreferences preference;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_safe_setup_third);
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		preference = getSharedPreferences("config", MODE_PRIVATE);
		etPhone.setText(preference.getString("securityPhone", null));
	}
	
	
	/*
	 * 选择联系人
	 */
	public void selectContact(View v){
		Intent intent = new Intent(PhoneSafeSetupThirdActivity.this,ContactsActivity.class);
		startActivityForResult(intent,0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data==null){
			return;
		}else{
			etPhone.setText(data.getStringExtra("phone"));
		}
	}
	
	public void next(View v){
		showNext();
	}
	
	public void pre(View v){
		showPre();
	}

	@Override
	public void showNext() {
		String phone = etPhone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "安全号码还没有设置", 0).show();
			return;
		}
		Editor et = preference.edit();
		et.putString("securityPhone", phone);
		et.commit();
		
		
		Intent intent = new Intent(PhoneSafeSetupThirdActivity.this,PhoneSafeSetupFourActivity.class);
		startActivity(intent);
		finish();
		//该方法必须在startActivity或者finish方法后面执行
		overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(PhoneSafeSetupThirdActivity.this,PhoneSafeSetupSecondActivity.class);
		startActivity(intent);
		finish();
		//该方法必须在startActivity或者finish方法后面执行
		overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
	}
}
