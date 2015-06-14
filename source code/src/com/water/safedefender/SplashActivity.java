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
	
	//��ȡ�汾
	private String version;
	//��ȡ�汾����
	private String description;
	
	//���صĽ�����ʾcontrol
	TextView tv_DownloadProgress;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_Flash_Version = (TextView) findViewById(R.id.tv_splash_version);
        tv_Flash_Version.setText("�汾��:"+getVersionName());
        tv_DownloadProgress = (TextView) findViewById(R.id.tv_downloadProgress);
        
        //copy�����ز�ѯ���ݿ�
        copyDB("address.db");
        
        //�Ѳ������ݿ⿽����  ϵͳĿ¼
       copyDB("antivirus.db");

        //���ý���Ч��
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(2000);//���ý���ʱ�䳤��
        findViewById(R.id.rl_splash).startAnimation(animation);
        
      //��ȡ�Ƿ���Ҫ���������汾�����������Ŀ��Խ�������
        SharedPreferences preference = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isPromptUpdate = preference.getBoolean("isPromptUpdate",true);
        if(isPromptUpdate){
        	//���汾����
            checkUpdate();
        }else{
        	//��ʱ�ķ�ʽ�����¼��֣�
        	//1.Timer���TimeTask��2.ClockManager;3.�����̣߳�4.ͨ��Handler��ʱ
        	//����Ҫ�����°汾,ͨ��handler�ӳ�2�������ҳ��
        	handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// ��ȥ��ҳ��
					enterHome();
				}
			}, 2000);
        }
    }
    
    /*
     * copy���������ݿ�
     */
    private void copyDB(final String dbname) {
		AssetManager aManager = getAssets();
		try {
			//getFilesDir()�õ�data/data/����/filesĿ¼
			//File file = new File(getFilesDir()+"/address.db");
			File file = new File(getFilesDir()+"/"+dbname);
			//�Ѿ����ڲ��ҳ��ȴ���0����Ҫcopy��
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
			case ENTER_HOME: //������ҳ��
				enterHome();
				break;
			case NEED_TO_UPLOAD: //��Ҫ���°汾
				showUpdateDialog();
				break;
			case URL_ERROR:		//url����
				enterHome();
				Toast.makeText(SplashActivity.this, "URL����", Toast.LENGTH_SHORT).show();
				break;
			case NET_ERROR:		//�����쳣
				enterHome();
				Toast.makeText(SplashActivity.this, "�����쳣", Toast.LENGTH_SHORT).show();
				break;
			case JSON_ERROR:	//json��������
				enterHome();
				Toast.makeText(SplashActivity.this, "Jason��������", Toast.LENGTH_SHORT).show();
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
		//�ǵùرյ�ǰҳ��
		finish();
	}
    
    protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new Builder(this);
		//dialog.setCancelable(false);//ǿ������
		dialog.setTitle("�汾��������");
		dialog.setMessage(description);
		//���������߰����ؼ��������¼�
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//������ҳ��
				enterHome();
				dialog.dismiss();
				
			}
		});
		dialog.setPositiveButton("��������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//�ж��Ƿ���SdCard
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					Toast.makeText(SplashActivity.this, "�밲װSdCard������", Toast.LENGTH_SHORT).show();
					return;
				}
				// ����
				downloadAPK();
			}
		});
		
		dialog.setNegativeButton("ȡ��", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();//ȡ����ʾ
				enterHome();
			}
		});
		dialog.show();//��������ʾ
	}

    //�������°汾APK
	protected void downloadAPK() {
		// TODO Auto-generated method stub
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download("http://10.0.2.2/safedefender2.0.apk",
		    "/sdcard/safedefender2.0.apk",
		    true, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
		    true, // ��������󷵻���Ϣ�л�ȡ���ļ�����������ɺ��Զ���������
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
		        	//���سɹ���װapk
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
	 * ��װ
	 */
	protected void installPackage(File installFile){
		//��װ:ͨ��ϵͳ�Դ���PackageInstallerActivity��װ
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
     * ��������Ƿ��°汾,�����������
     */
    private void checkUpdate() {
    	//��������Ҫͨ�����̣߳����������߳�
    	new Thread(){
    		public void run() {
    			Message msg = Message.obtain();//��message���л�ȡ����ʡ��Դ
    			long startTime = System.currentTimeMillis();
    			try {
					URL url = new URL(getString(R.string.apkurl));
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);//�������ӳ�ʱʱ��
					int responseCode = connection.getResponseCode();
					if(responseCode == 200){
						InputStream is = connection.getInputStream();
						//ת�����ַ���
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						int len;
						byte[] buffer = new byte[1024];
						while((len = is.read(buffer))!=-1){
							bos.write(buffer, 0, len);
						}
						String result = bos.toString();
						is.close();
						bos.close();
						
						//��result������JSON����
						JSONObject json = new JSONObject(result);
						//��ȡ�汾
					    version = json.getString("version");
						//��ȡ�汾����
						description = json.getString("description");
						//��ȡAPK���ص�ַ
						String apkurl = json.getString("apkurl");
						//�жϰ汾�Ƿ�������
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
					//���˵�ʱ��
					long usedTime = endTime - startTime;
					if(usedTime < 2000){
						try {
							Thread.sleep(2000 - usedTime); //Ϊ����logo��ʾ��һ��
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
     * �õ��汾��
     * @return
     */
    private String getVersionName(){
    	//���������ֻ�APK
    	PackageManager packageManager = getPackageManager();
    	try {
    		//�õ������嵥�ļ���Ϣ
			PackageInfo info = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
			return info.versionName;
    	} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
}
