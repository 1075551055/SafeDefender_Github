package com.water.safedefender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PhoneSafeSetupFourActivity extends BaseSetupActivity {

	private SharedPreferences preference;
	private CheckBox cb_isOpenProtect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_safe_setup_four);
		preference = getSharedPreferences("config",MODE_PRIVATE);
		cb_isOpenProtect = (CheckBox) findViewById(R.id.cb_isOpenProtect);
		
		boolean isOpenProtect = preference.getBoolean("isOpenProtect", false);
		cb_isOpenProtect.setChecked(isOpenProtect);
		cb_isOpenProtect.setText(isOpenProtect?"�����ֻ����������Ѿ�����":"�����ֻ���������δ����");
		
		cb_isOpenProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cb_isOpenProtect.setText(isChecked?"�����ֻ����������Ѿ�����":"�����ֻ���������δ����");
				//�������õ�ֵ
				Editor et = preference.edit();
				et.putBoolean("isOpenProtect", isChecked);
				et.commit();
			}
		});
	}

	public void next(View v){
		showNext();
	}
	
	public void pre(View v){
		showPre();
	}

	@Override
	public void showNext() {
		Editor editor = preference.edit();
		editor.putBoolean("isConfig", true);
		editor.commit();
		Intent intent = new Intent(PhoneSafeSetupFourActivity.this,PhoneSafeActivity.class);
		startActivity(intent);
		finish();
		//�÷���������startActivity����finish��������ִ��
		overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(PhoneSafeSetupFourActivity.this,PhoneSafeSetupThirdActivity.class);
		startActivity(intent);
		finish();
		//�÷���������startActivity����finish��������ִ��
		overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
	}

}
