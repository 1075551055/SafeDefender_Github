package com.water.safedefender;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//在清单文件中设置了启动模式为singleInstance.android:launchMode="singleInstance" ,
//让它只在独立的一个任务栈中，这样就可以避免和手机卫士的activity放在同一个任务栈中
//并且设置了android:excludeFromRecents="true"，让该activity从最近查看过的activity列表中删除（长按小房子就可以查看到最近的activity）
public class LockEnterPwdActivity extends Activity {

	private EditText et_pwd;
	private String packagename;
	private PackageManager pm;
	private TextView tv_name;
	private ImageView iv_icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_enter_pwd);
		
	    tv_name = (TextView) findViewById(R.id.tv_name);
	    iv_icon = (ImageView) findViewById(R.id.iv_icon);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		pm = getPackageManager();
		
		//获取激活当前activity的意图，即是watchdogservice传递过来的intent
		Intent intent = getIntent();
		if(intent!=null){
			packagename = intent.getStringExtra("packagename");
			
			//设置名称和图标
			ApplicationInfo aInfo;
			try {
				aInfo = pm.getApplicationInfo(packagename, 0);
				tv_name.setText(aInfo.loadLabel(pm).toString());
				iv_icon.setImageDrawable(aInfo.loadIcon(pm));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * 按了后退键执行的方法
	 */
	@Override
	public void onBackPressed() {
		//回到桌面
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
	
	@Override
	protected void onStop() {
		//当activity不可见的时候，应该将当前activity销毁掉，否则所以给锁了的应用打开的时候都是显示同一个activity
		//里面的应用程序名称和图标就不正确了
		super.onStop();
		finish();
	}

	public void enter(View v){
		String pwd = et_pwd.getText().toString();
		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(this, "密码不能为空", 0).show();
			return;
		}
		if(pwd.equals("123")){
			//密码正确后，告诉watchdog不用再监视我了。
			//组件间的通讯：1.可以通过绑定service，使用service中的方法；
		    //		    2.组件间可以通过发送广播进行消息传递。（常用）
			//发送广播通知watchdog，密码已经正确了。
			Intent intent = new Intent();
			intent.setAction("com.water.safedefender.stopprotect");
			//同意意图传递数据
			intent.putExtra("packagename", packagename);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(this, "密码错误", 0).show();
		}
	}

}
