package com.water.safedefender;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.water.safedefender.dao.PhoneAddressQueryUtils;

public class PhoneAddressQueryActivity extends Activity {

	private EditText et_PhoneNumber;
	private TextView tv_address;
	//振动器，可以使手机振动
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_address_query);
		
		//权限：android.permission.VIBRATE
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_PhoneNumber = (EditText) findViewById(R.id.et_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		
		//et_PhoneNumber长度变化监听
		et_PhoneNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!=null&&s.length()>=11){
					String address = PhoneAddressQueryUtils.queryAddress(getFilesDir()+"/address.db", s.toString());
					tv_address.setText(address);
					
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/*
	 * 查询号码归属地
	 */
	public void queryPhoneAddress(View v){
		String phone = et_PhoneNumber.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			//左右抖动效果
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
	        et_PhoneNumber.startAnimation(shake);
	        
	        //手机振动效果
	        long [] pattern = {200,300,200,300};//振动200毫秒停止300毫秒再振动200毫秒停止300毫秒
	        vibrator.vibrate(pattern, 0);
	        
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
		}else{
			//从数据库查询号码归属地
			String address = PhoneAddressQueryUtils.queryAddress(getFilesDir()+"/address.db", phone);
			tv_address.setText(address);
		}
	}

}
