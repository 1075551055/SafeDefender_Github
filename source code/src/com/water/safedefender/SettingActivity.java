package com.water.safedefender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.water.safedefender.service.CallSmsSafeService;
import com.water.safedefender.service.PhoneAddressQueryService;
import com.water.safedefender.service.WatchDogService;
import com.water.safedefender.ui.SettingItemRelativeLayout;
import com.water.safedefender.ui.ShowAddressBgItemRelativeLayout;
import com.water.safedefender.utils.ServiceUtils;

public class SettingActivity extends Activity {

	private SettingItemRelativeLayout sirl;
	private SettingItemRelativeLayout sirl_show_phoneAddress;
	private SharedPreferences preference;
	private Intent showAddressIntent;
	private ShowAddressBgItemRelativeLayout sbir;
	private SettingItemRelativeLayout siv_callsms_safe;
	private Intent callSmsIntent;
	private SettingItemRelativeLayout sirl_watchdog;
	private Intent watchDogIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sbir = (ShowAddressBgItemRelativeLayout) findViewById(R.id.sbir);
		sirl = (SettingItemRelativeLayout) findViewById(R.id.sirl);
		//��ȡ֮ǰ���ù���ֵ��Ȼ��ֵ��CheckBox
		preference = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPromptUpdate = preference.getBoolean("isPromptUpdate",true);
        sirl.setCheckBoxStatus(isPromptUpdate);
        /*if(isPromptUpdate){
        	sirl.tv_promptUpdateDesc.setText("��ʾ�����Ѿ�����");
        }else{
        	sirl.tv_promptUpdateDesc.setText("��ʾ�����Ѿ��ر�");
        }*/
        sirl.setPromptUpdateDescription(isPromptUpdate);
        		        
		sirl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ѡ��checkbox
				boolean statusAfterClick = !sirl.cb_promptUpdateSetting.isChecked();
				/*if(statusAfterClick){
		        	sirl.tv_promptUpdateDesc.setText("��ʾ�����Ѿ�����");
		        }else{
		        	sirl.tv_promptUpdateDesc.setText("��ʾ�����Ѿ��ر�");
		        }*/
				sirl.setPromptUpdateDescription(statusAfterClick);
				sirl.setCheckBoxStatus(statusAfterClick);
				//�����ùر��������ѵ�ֵ���浽sharedpreference�У�û��settings�ļ����Զ�����
				Editor et = preference.edit();
				et.putBoolean("isPromptUpdate", statusAfterClick);
				et.commit();//�������ύ
			}
		});
		
		
		//�����Ƿ���watch dog
		sirl_watchdog = (SettingItemRelativeLayout) findViewById(R.id.sirl_watchdog);
  		watchDogIntent = new Intent(SettingActivity.this, WatchDogService.class);
  		boolean isWatchDogServiceRunning = ServiceUtils.serviceIsRunning(SettingActivity.this,"com.water.safedefender.service.WatchDogService");
  		sirl_watchdog.setCheckBoxStatus(isWatchDogServiceRunning);
  				
  		sirl_watchdog.setOnClickListener(new OnClickListener() {
  					
  				@Override
  				public void onClick(View v) {
  					// TODO Auto-generated method stub
  					boolean statusAfterClick = !sirl_watchdog.cb_promptUpdateSetting.isChecked();
  					sirl_watchdog.setPromptUpdateDescription(statusAfterClick);
  					sirl_watchdog.setCheckBoxStatus(statusAfterClick);
  					if(statusAfterClick){
  						//ѡ��״̬
  						startService(watchDogIntent);
  					}else{
  						//��ѡ��״̬
  						stopService(watchDogIntent);
  					}
  				}
  			});
		
		//�����Ƿ������ŵ绰���ع���
  		siv_callsms_safe = (SettingItemRelativeLayout) findViewById(R.id.siv_callsms_safe);
  		callSmsIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
  		boolean isCallSmsServiceRunning = ServiceUtils.serviceIsRunning(SettingActivity.this,"com.water.safedefender.service.CallSmsSafeService");
  		siv_callsms_safe.setCheckBoxStatus(isCallSmsServiceRunning);
  				
  		siv_callsms_safe.setOnClickListener(new OnClickListener() {
  					
  				@Override
  				public void onClick(View v) {
  					// TODO Auto-generated method stub
  					boolean statusAfterClick = !siv_callsms_safe.cb_promptUpdateSetting.isChecked();
  					siv_callsms_safe.setPromptUpdateDescription(statusAfterClick);
  					siv_callsms_safe.setCheckBoxStatus(statusAfterClick);
  					if(statusAfterClick){
  						//ѡ��״̬
  						startService(callSmsIntent);
  					}else{
  						//��ѡ��״̬
  						stopService(callSmsIntent);
  					}
  				}
  			});
		
		//�����Ƿ��������������ʾ
		sirl_show_phoneAddress = (SettingItemRelativeLayout) findViewById(R.id.sirl_show_phoneAddress);
		showAddressIntent = new Intent(SettingActivity.this, PhoneAddressQueryService.class);
		boolean isRunning = ServiceUtils.serviceIsRunning(SettingActivity.this,"com.water.safedefender.service.PhoneAddressQueryService");
		sirl_show_phoneAddress.setCheckBoxStatus(isRunning);
		
		sirl_show_phoneAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean statusAfterClick = !sirl_show_phoneAddress.cb_promptUpdateSetting.isChecked();
				sirl_show_phoneAddress.setPromptUpdateDescription(statusAfterClick);
				sirl_show_phoneAddress.setCheckBoxStatus(statusAfterClick);
				if(statusAfterClick){
					//ѡ��״̬
					startService(showAddressIntent);
				}else{
					//��ѡ��״̬
					stopService(showAddressIntent);
				}
			}
		});
		
		
		
		final String [] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		final int selectedWhich = preference.getInt("which", 0);
		sbir.setDesc(items[selectedWhich]);
		//��ʾ������
		sbir.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				int which = preference.getInt("which", 0);
				//��ʾ�Ի���
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��ʾ�����ط��");
				builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// �������õĶԻ�����ʽ
						Editor et = preference.edit();
						et.putInt("which", which);
						et.commit();
						
						//
						sbir.setDesc(items[which]);
						//�رնԻ���
						dialog.dismiss();
					}
					
				});
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});
	}
	
	
	
	/*
	 * �����С�����˳���Ȼ���ٽ�����ҳ���ʱ��ȥ�����жϷ����Ƿ�������
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isRunning = ServiceUtils.serviceIsRunning(SettingActivity.this,"com.water.safedefender.service.PhoneAddressQueryService");
		sirl_show_phoneAddress.setCheckBoxStatus(isRunning);
		
		boolean isCallSmsServiceRunning = ServiceUtils.serviceIsRunning(SettingActivity.this,"com.water.safedefender.service.CallSmsSafeService");
		siv_callsms_safe.setCheckBoxStatus(isCallSmsServiceRunning);
	}

}
