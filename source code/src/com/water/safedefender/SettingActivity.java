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
		//获取之前设置过的值，然后赋值给CheckBox
		preference = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPromptUpdate = preference.getBoolean("isPromptUpdate",true);
        sirl.setCheckBoxStatus(isPromptUpdate);
        /*if(isPromptUpdate){
        	sirl.tv_promptUpdateDesc.setText("提示升级已经开启");
        }else{
        	sirl.tv_promptUpdateDesc.setText("提示升级已经关闭");
        }*/
        sirl.setPromptUpdateDescription(isPromptUpdate);
        		        
		sirl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//选择checkbox
				boolean statusAfterClick = !sirl.cb_promptUpdateSetting.isChecked();
				/*if(statusAfterClick){
		        	sirl.tv_promptUpdateDesc.setText("提示升级已经开启");
		        }else{
		        	sirl.tv_promptUpdateDesc.setText("提示升级已经关闭");
		        }*/
				sirl.setPromptUpdateDescription(statusAfterClick);
				sirl.setCheckBoxStatus(statusAfterClick);
				//把设置关闭升级提醒的值保存到sharedpreference中，没有settings文件会自动创建
				Editor et = preference.edit();
				et.putBoolean("isPromptUpdate", statusAfterClick);
				et.commit();//别忘了提交
			}
		});
		
		
		//设置是否开启watch dog
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
  						//选中状态
  						startService(watchDogIntent);
  					}else{
  						//非选中状态
  						stopService(watchDogIntent);
  					}
  				}
  			});
		
		//设置是否开启短信电话拦截功能
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
  						//选中状态
  						startService(callSmsIntent);
  					}else{
  						//非选中状态
  						stopService(callSmsIntent);
  					}
  				}
  			});
		
		//设置是否开启号码归属地显示
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
					//选中状态
					startService(showAddressIntent);
				}else{
					//非选中状态
					stopService(showAddressIntent);
				}
			}
		});
		
		
		
		final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		final int selectedWhich = preference.getInt("which", 0);
		sbir.setDesc(items[selectedWhich]);
		//显示归属地
		sbir.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				int which = preference.getInt("which", 0);
				//显示对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("显示归属地风格");
				builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 保存设置的对话框样式
						Editor et = preference.edit();
						et.putInt("which", which);
						et.commit();
						
						//
						sbir.setDesc(items[which]);
						//关闭对话框
						dialog.dismiss();
					}
					
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
	}
	
	
	
	/*
	 * 当点击小房子退出，然后再进到该页面的时候去重新判断服务是否运行中
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
