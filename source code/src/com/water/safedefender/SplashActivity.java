package com.water.safedefender;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	private TextView tv_Flash_Version;
	private final int ENTER_HOME = 0;
	private final int NEED_TO_UPLOAD = 1;
	private final int URL_ERROR = 2;
	private final int NET_ERROR = 3;
	private final int JSON_ERROR = 4;
	
	//获取版本
	private String version;
	//获取版本描述
	private String description;
	
	//下载的进度显示control
	TextView tv_DownloadProgress;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_Flash_Version = (TextView) findViewById(R.id.tv_splash_version);
        tv_Flash_Version.setText("版本号:"+getVersionName());
        tv_DownloadProgress = (TextView) findViewById(R.id.tv_downloadProgress);
        
        //copy归属地查询数据库
        copyDB("address.db");
        
        //把病毒数据库拷贝到  系统目录
       copyDB("antivirus.db");

        //设置渐变效果
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(2000);//设置渐变时间长度
        findViewById(R.id.rl_splash).startAnimation(animation);
        
      //获取是否需要提醒升级版本，在设置中心可以进行设置
        SharedPreferences preference = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPromptUpdate = preference.getBoolean("isPromptUpdate",true);
        if(isPromptUpdate){
        	//检查版本更新
            checkUpdate();
        }else{
        	//延时的方式有以下几种：
        	//1.Timer结合TimeTask；2.ClockManager;3.开启线程；4.通过Handler延时
        	//不需要检测更新版本,通过handler延迟2秒进入主页面
        	handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// 进去主页面
					enterHome();
				}
			}, 2000);
        }
    }
    
    /*
     * copy归属地数据库
     */
    private void copyDB(final String dbname) {
		AssetManager aManager = getAssets();
		try {
			//getFilesDir()得到data/data/包名/files目录
			//File file = new File(getFilesDir()+"/address.db");
			File file = new File(getFilesDir()+"/"+dbname);
			//已经存在并且长度大于0则不需要copy了
			if(file.exists() && file.length() > 0){
				return;
			}
			InputStream is = aManager.open(dbname);
			
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while((len = is.read(buffer))!=-1){
				fos.write(buffer, 0, len);
			}
			fos.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Handler handler=new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case ENTER_HOME: //进入主页面
				enterHome();
				break;
			case NEED_TO_UPLOAD: //需要更新版本
				showUpdateDialog();
				break;
			case URL_ERROR:		//url错误
				enterHome();
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
				break;
			case NET_ERROR:		//网络异常
				enterHome();
				Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				break;
			case JSON_ERROR:	//json解析错误
				enterHome();
				Toast.makeText(SplashActivity.this, "Jason解析错误", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
    	};
    };

    private void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		//记得关闭当前页面
		finish();
	}
    
    protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new Builder(this);
		//dialog.setCancelable(false);//强制升级
		dialog.setTitle("版本更新提醒");
		dialog.setMessage(description);
		//当触屏或者按返回键触发该事件
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主页面
				enterHome();
				dialog.dismiss();
				
			}
		});
		dialog.setPositiveButton("立即升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//判断是否有SdCard
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					Toast.makeText(SplashActivity.this, "请安装SdCard再重试", Toast.LENGTH_SHORT).show();
					return;
				}
				// 下载
				downloadAPK();
			}
		});
		
		dialog.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();//取消显示
				enterHome();
			}
		});
		dialog.show();//别忘了显示
	}

    //下载最新版本APK
	protected void downloadAPK() {
		// TODO Auto-generated method stub
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download("http://10.0.2.2/safedefender2.0.apk",
		    "/sdcard/safedefender2.0.apk",
		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
		    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		            tv_DownloadProgress.setText("conn...");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	tv_DownloadProgress.setVisibility(View.VISIBLE);
		        	int progress = (int) (current * 100 / total);
		        	tv_DownloadProgress.setText(progress+"%");
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	tv_DownloadProgress.setText("downloaded:" + responseInfo.result.getPath());
		        	//下载成功后安装apk
		        	installPackage(responseInfo.result);
		        	
		        }


		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	tv_DownloadProgress.setText(msg);
		        	enterHome();
		        }
		});
	}
	
	/*
	 * 安装
	 */
	protected void installPackage(File installFile){
		//安装:通过系统自带的PackageInstallerActivity安装
		/*<intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="content" />
        <data android:scheme="file" />
        <data android:mimeType="application/vnd.android.package-archive" />
      	</intent-filter>*/
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(installFile), "application/vnd.android.package-archive");
		startActivity(intent);
		
	}
	

	/**
     * 联网检查是否新版本,如果有则升级
     */
    private void checkUpdate() {
    	//联网操作要通过子线程，不能在主线程
    	new Thread(){
    		public void run() {
    			Message msg = Message.obtain();//从message池中获取，节省资源
    			long startTime = System.currentTimeMillis();
    			try {
					URL url = new URL(getString(R.string.apkurl));
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);//设置连接超时时间
					int responseCode = connection.getResponseCode();
					if(responseCode == 200){
						InputStream is = connection.getInputStream();
						//转换成字符串
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						int len;
						byte[] buffer = new byte[1024];
						while((len = is.read(buffer))!=-1){
							bos.write(buffer, 0, len);
						}
						String result = bos.toString();
						is.close();
						bos.close();
						
						//将result解析成JSON对象
						JSONObject json = new JSONObject(result);
						//获取版本
					    version = json.getString("version");
						//获取版本描述
						description = json.getString("description");
						//获取APK下载地址
						String apkurl = json.getString("apkurl");
						//判断版本是否是最新
						if(getVersionName().equals(version)){
							msg.what = ENTER_HOME;
						}else{
							msg.what = NEED_TO_UPLOAD;
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = NET_ERROR;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = JSON_ERROR;
				}finally{
					long endTime = System.currentTimeMillis();
					//花了的时间
					long usedTime = endTime - startTime;
					if(usedTime < 2000){
						try {
							Thread.sleep(2000 - usedTime); //为了让logo显示久一点
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//
					}
					handler.sendMessage(msg);
				}
    			
    		};
    	}.start();
	}


	/**
     * 得到版本号
     * @return
     */
    private String getVersionName(){
    	//用来管理手机APK
    	PackageManager packageManager = getPackageManager();
    	try {
    		//得到功能清单文件信息
			PackageInfo info = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
			return info.versionName;
    	} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
}
