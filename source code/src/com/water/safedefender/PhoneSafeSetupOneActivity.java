package com.water.safedefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class PhoneSafeSetupOneActivity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_safe_setup_one);
		
	
	}

	public void next(View v){
		showNext();
	}



	@Override
	public void showNext() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(PhoneSafeSetupOneActivity.this,PhoneSafeSetupSecondActivity.class);
		startActivity(intent);
		finish();
		//该方法必须在startActivity或者finish方法后面执行
		overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		
	}

}
