package com.water.safedefender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView gv_functions;
	private MyAdapter adapter;
	private static String[] functionNames = {
		"手机防盗","通讯卫士","软件管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"
	};
	
	
	
	private static int[] functionImgId={
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		preferences = getSharedPreferences("config", MODE_PRIVATE);
		gv_functions = (GridView) findViewById(R.id.gv_functions);
		adapter = new MyAdapter();
		gv_functions.setAdapter(adapter);
		
		//设置GridView每一个item的点击事件
		gv_functions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				// TODO Auto-generated method stub
				switch (position) {
				//进入手机防盗界面
				case 0:
					//弹出输入密码的对话框：假如没有设置过密码，则让user先设置密码；如果曾经设置过密码，则让user直接输入密码然后才可以进入防盗界面。
					showPasswordDialog();
					break;
				//进入通讯卫士
				case 1:
					intent = new Intent(HomeActivity.this, CommunicationGuardActivity.class);
					startActivity(intent);
					break;
				//进入软件管理器	
				case 2:
					intent = new Intent(HomeActivity.this, SoftwareManagerActivity.class);
					startActivity(intent);
					break;
					
				//进入任务管理器	
				case 3:
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
					
				//进入流量统计
				case 4:
					intent = new Intent(HomeActivity.this,TrafficAnalysisActivity.class);
					startActivity(intent);
					break;
					
				//进入手机杀毒
				case 5:
					intent = new Intent(HomeActivity.this,AntivirusActivity.class);
					startActivity(intent);
					break;
					
				//进入清理缓存
				case 6:
					intent = new Intent(HomeActivity.this,CleanCacheActivity.class);
					startActivity(intent);
					break;
					
				//进入高级工具界面
				case 7:
					intent = new Intent(HomeActivity.this, AdvancedToolActivity.class);
					startActivity(intent);
					break;
					
				//进入设置中心界面
				case 8:
					intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					break;
				
				
				default:
					break;
				}
			}

			
			
		});
	}

	/*
	 * 显示dialog
	 */
	protected void showPasswordDialog() {
		if(isSetPassword()){
			//设置过了密码，则可以直接输入密码进入防盗界面
			showEnterPassword();
		}else{
			//还没有设置过密码则让user先设置
			showSetPasswordDialog();
		}
	}
	
	private Button btnOk ;
	private Button btnPwdCancel ;
	private EditText etPwd;
	private EditText etConfirmPwd;
	
	/*
	 * 显示只输入密码的dialog
	 */
	private void showEnterPassword() {
		//注意不可以使用getApplicationContext(),因为dialog是挂载在Activity上的，并且
		//HomeActivity是context的之类，之类有的东西父类(Context)不一定有
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		
		//定义一个布局文件，并将其装填到view中
		View view = View.inflate(HomeActivity.this, R.layout.phonesafe_enterpassword_dialog, null);
		
		//自定义对话框，将某个view装填到dialog中
		//builder.setView(view);
		
		//显示
		final AlertDialog dialog = builder.create();
		//让对话框在低版本系统中好看，通过该方法兼容
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		btnOk = (Button) view.findViewById(R.id.btn_pwdOk);
		btnPwdCancel = (Button) view.findViewById(R.id.btn_pwdCancel);
		etPwd = (EditText) view.findViewById(R.id.et_password);
		
		//设置确定按钮对应的事件
				btnOk.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String password = etPwd.getText().toString().trim();
						String savedPassword = preferences.getString("password", null);
						if(TextUtils.isEmpty(password)){
							Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(password.equals(savedPassword)){
							dialog.dismiss();
							enterPhoneSafe();
						}else{
							etPwd.setText("");
							Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
				
				//设置取消按钮的相对应事件
				btnPwdCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						//无法在按钮事件中使用该方法builder.dimiss(),必须通过获取dialog，再调用dismiss方法
						dialog.dismiss();
					}
				});
		
	}

	/*
	 * 判断是否设置了密码
	 */
	private boolean isSetPassword(){
		String password = preferences.getString("password", null);
		return !TextUtils.isEmpty(password);
	}
	
	

	/*
	 * 显示让user设置密码的dialog
	 */
	protected void showSetPasswordDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		
		//定义一个布局文件，并将其装填到view中
		View view = View.inflate(HomeActivity.this, R.layout.phonesafe_setpassword_dialog, null);
		
		//自定义对话框，将某个view装填到dialog中
		//builder.setView(view);
		
		//显示
		final AlertDialog dialog = builder.create();
		//让对话框在低版本系统中好看，通过该方法兼容
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		btnOk = (Button) view.findViewById(R.id.btn_pwdOk);
		btnPwdCancel = (Button) view.findViewById(R.id.btn_pwdCancel);
		etPwd = (EditText) view.findViewById(R.id.et_password);
		etConfirmPwd = (EditText) view.findViewById(R.id.et_passwordConfirm);
		
		//设置确定按钮对应的事件
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//判断两次输入的密码是否相同
				String pwd = etPwd.getText().toString().trim();
				String confirmPwd = etConfirmPwd.getText().toString().trim();
				if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirmPwd)){
					Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(pwd.equals(confirmPwd)){
					//两次密码相同则保存密码,保存到SharedPreferences中
					Editor etPrference = preferences.edit();
					etPrference.putString("password", pwd);
					//别忘了提交
					etPrference.commit();
					
					//消掉dialog进入手机防盗页面
					dialog.dismiss();
					enterPhoneSafe();
					
				}else{
					etPwd.setText("");
					etConfirmPwd.setText("");
					//两次输入的密码不相同则提示user
					Toast.makeText(HomeActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		//设置取消按钮的相对应事件
		btnPwdCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//无法在按钮事件中使用该方法builder.dimiss(),必须通过获取dialog，再调用dismiss方法
				dialog.dismiss();
			}
		});
	}

	/*
	 * 进入手机防盗界面
	 */
	protected void enterPhoneSafe() {
		// TODO Auto-generated method stub
		Intent phoneSafeIntent = new Intent(HomeActivity.this,PhoneSafeActivity.class);
		startActivity(phoneSafeIntent);
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return functionNames.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//通过打气筒将自定义的item布局装填到view中
			View v = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
			ImageView iv_function = (ImageView) v.findViewById(R.id.iv_HomeListItem);
			TextView tv_function = (TextView) v.findViewById(R.id.tv_HomeListItem);
			iv_function.setImageResource(functionImgId[position]);
			tv_function.setText(functionNames[position]);
			return v;
		}
		
	}
}
