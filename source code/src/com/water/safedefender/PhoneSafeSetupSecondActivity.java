package com.water.safedefender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.water.safedefender.ui.SettingItemRelativeLayout;

public class PhoneSafeSetupSecondActivity extends BaseSetupActivity {

	private SettingItemRelativeLayout sirl;
	private TelephonyManager telManager;
	private SharedPreferences preference;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_safe_setup_second);
		
		sirl = (SettingItemRelativeLayout) findViewById(R.id.sirl);
		preference = getSharedPreferences("config", MODE_PRIVATE);
		String simNumber = preference.getString("simNumber", null);
		if(TextUtils.isEmpty(simNumber)){
			//û�а�SIM��
			sirl.cb_promptUpdateSetting.setChecked(false);
		}else{
			//����SIM��
			sirl.cb_promptUpdateSetting.setChecked(true);
		}
		telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//��ȡ��SIM�������кţ�SIM��Ψһ��ʶ��
		final String simSerialNumber = telManager.getSimSerialNumber();
				
		sirl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sirl.cb_promptUpdateSetting.setChecked(!sirl.cb_promptUpdateSetting.isChecked());
				//ѡ����CheckBox���ʾ��SIM������SIM�������кű�������
				if(sirl.cb_promptUpdateSetting.isChecked()){
					Editor et = preference.edit();
					et.putString("simNumber", simSerialNumber);
					et.commit();
				}else{
					//���򱣴�ΪNULL
					Editor et = preference.edit();
					et.putString("simNumber", null);
					et.commit();
				}
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
		// ���ж��Ƿ����SIM��
		String simNumber = preference.getString("simNumber", null);
		if(TextUtils.isEmpty(simNumber)){
			Toast.makeText(PhoneSafeSetupSecondActivity.this, "���Ȱ�SIM��", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(PhoneSafeSetupSecondActivity.this,PhoneSafeSetupThirdActivity.class);
		startActivity(intent);
		finish();
		//�÷���������startActivity����finish��������ִ��
		overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(PhoneSafeSetupSecondActivity.this,PhoneSafeSetupOneActivity.class);
		startActivity(intent);
		finish();
		//�÷���������startActivity����finish��������ִ��
		overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
	}
}
