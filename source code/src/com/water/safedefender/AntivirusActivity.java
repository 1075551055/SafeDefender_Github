package com.water.safedefender;

import java.util.List;

import com.water.safedefender.dao.AntiVirusDao;
import com.water.safedefender.utils.Md5Utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ���嵥�ļ�������android:configChanges="screenSize|orientation|keyboardHidden"�����Է�ֹ�������л�
 * ���ٵ�ǰactivityȻ�����´���
 * ��ǰactivity����������ɨ�財��
 * @author Administrator
 *
 */
public class AntivirusActivity extends Activity {

	protected static final int SCANNING = 1;
	protected static final int SCAN_FINISH = 2;
	protected static final int SCAN_BENGIN = 0;
	private ImageView iv_scan;
	private ProgressBar progressBar1;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private PackageManager pm;
	private boolean flag; 
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCAN_BENGIN:
				tv_scan_status.setText("���ڳ�ʼ��8��ɱ������...");
				break;
			case SCANNING:
				ScanInfo info = (ScanInfo) msg.obj;
				tv_scan_status.setText("����ɨ��:"+info.appname);
				TextView child = new TextView(getApplicationContext());
				if(info.isVirus){
					child.setTextColor(Color.RED);
				}else{
					child.setTextColor(Color.BLACK);
				}
				child.setText(info.appname+":"+info.desc);
				ll_container.addView(child, 0);
				break;
			case SCAN_FINISH:
				tv_scan_status.setText("ɨ����ϣ�");
				iv_scan.clearAnimation();
				Toast.makeText(getApplicationContext(), "ɨ����ϡ���", 0).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pm = getPackageManager();
		setContentView(R.layout.activity_antivirus);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setRepeatCount(Animation.INFINITE);
		ra.setDuration(2000);
		iv_scan.startAnimation(ra);

		scanVirus();

	}

	private void scanVirus() {
		flag = true;
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				msg.what = SCAN_BENGIN;
				handler.sendMessage(msg);

				// ����ֻ������ÿһ��Ӧ�ó���
				List<PackageInfo> packInfos = pm.getInstalledPackages(0);
				int max = packInfos.size();
				progressBar1.setMax(max);
				int process = 0;
				for (PackageInfo info : packInfos) {
					if(!flag){
						return;
					}
					String apkpath = info.applicationInfo.sourceDir;
					// ����ȡ����ļ��� ������
					String md5info = Md5Utils.getFileMd5(apkpath);
					String result = AntiVirusDao.checkVirus(md5info);
					msg = Message.obtain();
					msg.what = SCANNING;
					ScanInfo scanInfo = new ScanInfo();
					if (result == null) {
						scanInfo.desc = "ɨ�谲ȫ";
						scanInfo.isVirus = false;
					} else {
						scanInfo.desc = result;
						scanInfo.isVirus = true;
					}
					scanInfo.packname = info.packageName;
					scanInfo.appname = info.applicationInfo.loadLabel(pm).toString();
					msg.obj = scanInfo;
					handler.sendMessage(msg);
					process++;
					progressBar1.setProgress(process);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);

			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		flag = false;
		super.onDestroy();
	}
	
	class ScanInfo {
		String appname;
		boolean isVirus;
		String desc;
		String packname;
	}


	

}