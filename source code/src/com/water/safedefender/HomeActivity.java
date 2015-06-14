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
		"�ֻ�����","ͨѶ��ʿ","�������",
		"���̹���","����ͳ��","�ֻ�ɱ��",
		"��������","�߼�����","��������"
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
		
		//����GridViewÿһ��item�ĵ���¼�
		gv_functions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				// TODO Auto-generated method stub
				switch (position) {
				//�����ֻ���������
				case 0:
					//������������ĶԻ��򣺼���û�����ù����룬����user���������룻����������ù����룬����userֱ����������Ȼ��ſ��Խ���������档
					showPasswordDialog();
					break;
				//����ͨѶ��ʿ
				case 1:
					intent = new Intent(HomeActivity.this, CommunicationGuardActivity.class);
					startActivity(intent);
					break;
				//�������������	
				case 2:
					intent = new Intent(HomeActivity.this, SoftwareManagerActivity.class);
					startActivity(intent);
					break;
					
				//�������������	
				case 3:
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
					
				//��������ͳ��
				case 4:
					intent = new Intent(HomeActivity.this,TrafficAnalysisActivity.class);
					startActivity(intent);
					break;
					
				//�����ֻ�ɱ��
				case 5:
					intent = new Intent(HomeActivity.this,AntivirusActivity.class);
					startActivity(intent);
					break;
					
				//����������
				case 6:
					intent = new Intent(HomeActivity.this,CleanCacheActivity.class);
					startActivity(intent);
					break;
					
				//����߼����߽���
				case 7:
					intent = new Intent(HomeActivity.this, AdvancedToolActivity.class);
					startActivity(intent);
					break;
					
				//�����������Ľ���
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
	 * ��ʾdialog
	 */
	protected void showPasswordDialog() {
		if(isSetPassword()){
			//���ù������룬�����ֱ��������������������
			showEnterPassword();
		}else{
			//��û�����ù���������user������
			showSetPasswordDialog();
		}
	}
	
	private Button btnOk ;
	private Button btnPwdCancel ;
	private EditText etPwd;
	private EditText etConfirmPwd;
	
	/*
	 * ��ʾֻ���������dialog
	 */
	private void showEnterPassword() {
		//ע�ⲻ����ʹ��getApplicationContext(),��Ϊdialog�ǹ�����Activity�ϵģ�����
		//HomeActivity��context��֮�֮࣬���еĶ�������(Context)��һ����
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		
		//����һ�������ļ���������װ�view��
		View view = View.inflate(HomeActivity.this, R.layout.phonesafe_enterpassword_dialog, null);
		
		//�Զ���Ի��򣬽�ĳ��viewװ�dialog��
		//builder.setView(view);
		
		//��ʾ
		final AlertDialog dialog = builder.create();
		//�öԻ����ڵͰ汾ϵͳ�кÿ���ͨ���÷�������
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		btnOk = (Button) view.findViewById(R.id.btn_pwdOk);
		btnPwdCancel = (Button) view.findViewById(R.id.btn_pwdCancel);
		etPwd = (EditText) view.findViewById(R.id.et_password);
		
		//����ȷ����ť��Ӧ���¼�
				btnOk.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String password = etPwd.getText().toString().trim();
						String savedPassword = preferences.getString("password", null);
						if(TextUtils.isEmpty(password)){
							Toast.makeText(HomeActivity.this, "����Ϊ��", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(password.equals(savedPassword)){
							dialog.dismiss();
							enterPhoneSafe();
						}else{
							etPwd.setText("");
							Toast.makeText(HomeActivity.this, "�������", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
				
				//����ȡ����ť�����Ӧ�¼�
				btnPwdCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						//�޷��ڰ�ť�¼���ʹ�ø÷���builder.dimiss(),����ͨ����ȡdialog���ٵ���dismiss����
						dialog.dismiss();
					}
				});
		
	}

	/*
	 * �ж��Ƿ�����������
	 */
	private boolean isSetPassword(){
		String password = preferences.getString("password", null);
		return !TextUtils.isEmpty(password);
	}
	
	

	/*
	 * ��ʾ��user���������dialog
	 */
	protected void showSetPasswordDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		
		//����һ�������ļ���������װ�view��
		View view = View.inflate(HomeActivity.this, R.layout.phonesafe_setpassword_dialog, null);
		
		//�Զ���Ի��򣬽�ĳ��viewװ�dialog��
		//builder.setView(view);
		
		//��ʾ
		final AlertDialog dialog = builder.create();
		//�öԻ����ڵͰ汾ϵͳ�кÿ���ͨ���÷�������
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		btnOk = (Button) view.findViewById(R.id.btn_pwdOk);
		btnPwdCancel = (Button) view.findViewById(R.id.btn_pwdCancel);
		etPwd = (EditText) view.findViewById(R.id.et_password);
		etConfirmPwd = (EditText) view.findViewById(R.id.et_passwordConfirm);
		
		//����ȷ����ť��Ӧ���¼�
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//�ж���������������Ƿ���ͬ
				String pwd = etPwd.getText().toString().trim();
				String confirmPwd = etConfirmPwd.getText().toString().trim();
				if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirmPwd)){
					Toast.makeText(HomeActivity.this, "����Ϊ��", Toast.LENGTH_SHORT).show();
					return;
				}
				if(pwd.equals(confirmPwd)){
					//����������ͬ�򱣴�����,���浽SharedPreferences��
					Editor etPrference = preferences.edit();
					etPrference.putString("password", pwd);
					//�������ύ
					etPrference.commit();
					
					//����dialog�����ֻ�����ҳ��
					dialog.dismiss();
					enterPhoneSafe();
					
				}else{
					etPwd.setText("");
					etConfirmPwd.setText("");
					//������������벻��ͬ����ʾuser
					Toast.makeText(HomeActivity.this, "������������벻һ��", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		
		//����ȡ����ť�����Ӧ�¼�
		btnPwdCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//�޷��ڰ�ť�¼���ʹ�ø÷���builder.dimiss(),����ͨ����ȡdialog���ٵ���dismiss����
				dialog.dismiss();
			}
		});
	}

	/*
	 * �����ֻ���������
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
			//ͨ������Ͳ���Զ����item����װ�view��
			View v = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
			ImageView iv_function = (ImageView) v.findViewById(R.id.iv_HomeListItem);
			TextView tv_function = (TextView) v.findViewById(R.id.tv_HomeListItem);
			iv_function.setImageResource(functionImgId[position]);
			tv_function.setText(functionNames[position]);
			return v;
		}
		
	}
}
