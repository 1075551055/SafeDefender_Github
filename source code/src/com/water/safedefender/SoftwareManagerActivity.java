package com.water.safedefender;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.water.safedefender.dao.AppLockDao;
import com.water.safedefender.domain.AppInfo;
import com.water.safedefender.engine.AppInfoProvider;
import com.water.safedefender.utils.DensityUtil;

public class SoftwareManagerActivity extends Activity implements OnClickListener {

	private TextView tv_available_rom;
	private TextView tv_available_sdcard;
	private ListView lv_app_manager;
	private LinearLayout ll_pd;
	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private TextView tv_appCount;
	private PopupWindow popupWindow;
	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private AppInfo appInfo;
	private AppManagerAdapter adapter;
	private AppLockDao dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_software_manager);
		
		dao = new AppLockDao(this);
		tv_available_rom = (TextView) findViewById(R.id.tv_available_rom);
		tv_available_sdcard = (TextView) findViewById(R.id.tv_available_sdcard);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_pd = (LinearLayout) findViewById(R.id.ll_pd);
		tv_appCount = (TextView) findViewById(R.id.tv_appCount);
		//�ڴ���ô�С
		long romAvailableSize = getAvailableSpace(Environment.getDataDirectory().getAbsolutePath());
		//sd�����ô�С
		long sdcardAvailableSize = getAvailableSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		//�����ȡ���Ķ����ֽڴ�С����Ҫ��ʽ����MB
		tv_available_rom.setText("�ڴ���ÿռ�:"+Formatter.formatFileSize(this, romAvailableSize));
		tv_available_sdcard.setText("SD�����ÿռ�:"+Formatter.formatFileSize(this, sdcardAvailableSize));
	
		fillData();
	
		//��listviewע��һ�������ļ�����
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				//ע�⣺�������ж���Ϊ�գ���Ϊ���������϶��������߳��л�ȡ�ģ����ܻ�û��ֵ��ִ�����´���ᱨ�����쳣
				if(userAppInfos!=null && systemAppInfos!=null){
					if(firstVisibleItem>userAppInfos.size()){
						tv_appCount.setText("ϵͳ����:"+systemAppInfos.size()+"��");
					}else{
						tv_appCount.setText("�û�����:"+userAppInfos.size()+"��");
					}
				}
				
			}
		});
		
		
		//����listviewÿ��item�ĵ���¼�
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(position==0){
					return;
				}else if(position==userAppInfos.size()+1){
					return;
				}else if(position<=userAppInfos.size()){
					//�û�����
					int newPosition = position-1;
					appInfo = userAppInfos.get(newPosition);
				}else{
					//ϵͳ����
					int newPosition = position-2-userAppInfos.size();
					appInfo = systemAppInfos.get(newPosition);
				}
				
				dismissPopupWindow();
				
				//TextView contentView = new TextView(SoftwareManagerActivity.this);
				View contentView = View.inflate(SoftwareManagerActivity.this, R.layout.popup_appinfo, null);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
				
				ll_uninstall.setOnClickListener(SoftwareManagerActivity.this);
				ll_start.setOnClickListener(SoftwareManagerActivity.this);
				ll_share.setOnClickListener(SoftwareManagerActivity.this);
				
				//contentView.setText(appInfo.getPackagename());
				//contentView.setTextColor(Color.BLACK);
				//contentView.setBackgroundColor(Color.GRAY);
			    popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				
			    //ע�⣺����Ч���Ĳ��ű���Ҫ�����б�����ɫ��PopupWindowĬ��û�б�����ɫ��
			    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			    
				int[] location = new int[2];
				view.getLocationInWindow(location);
				
				int dip = 58;
				int px = DensityUtil.dip2px(SoftwareManagerActivity.this, dip);
				//һ��API��ʹ�õ����ֶ���pxΪ��λ�ģ�������Ҫdip��px֮���ת��
				popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP, px, location[1]);
				
				
				//���Ŷ���
				ScaleAnimation sa = new ScaleAnimation(0.1f, 1f, 0.1f, 1f);
				sa.setDuration(500);
				//͸���ȶ���
				AlphaAnimation aa = new AlphaAnimation(0.1f, 1f);
				aa.setDuration(500);
				//������
				AnimationSet as = new AnimationSet(false);
				as.addAnimation(sa);
				as.addAnimation(aa);
				//��������
				contentView.startAnimation(as);
				
			}

			
		});
	
		//����listviewÿ��item�ĳ�����¼�
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){
					return true;
				}else if(position==userAppInfos.size()+1){
					return true;
				}else if(position<=userAppInfos.size()){
					//�û�����
					int newPosition = position-1;
					appInfo = userAppInfos.get(newPosition);
				}else{
					//ϵͳ����
					int newPosition = position-2-userAppInfos.size();
					appInfo = systemAppInfos.get(newPosition);
				}
				
				ViewHolder holder = (ViewHolder) view.getTag();
				//�жϵ�ǰӦ���Ƿ�浽�����������ݿ���
				if(dao.find(appInfo.getPackagename())){
					holder.iv_app_lock.setImageResource(R.drawable.unlock);
					dao.delete(appInfo.getPackagename());
				}else{
					holder.iv_app_lock.setImageResource(R.drawable.lock);
					dao.add(appInfo.getPackagename());
				}
				return true;
			}
		});
	}

	/**
	 * ��ȡ����
	 */
	private void fillData() {
		ll_pd.setVisibility(View.VISIBLE);
		new Thread(new Runnable(){

			@Override
			public void run() {
				//���ܰ�װ�ĳ���Ƚ϶࣬���Բ��˷������߳���
				appInfos = AppInfoProvider.getAppInfos(SoftwareManagerActivity.this); 
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo app : appInfos) {
					if(app.isUserApp()){
						userAppInfos.add(app);
					}else{
						systemAppInfos.add(app);
					}
				}
				//����listview������������Ϊ�Ǹ���UI�Ĳ��������Ա���������UI�߳�
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						tv_appCount.setVisibility(View.VISIBLE);
						ll_pd.setVisibility(View.INVISIBLE);
						if(adapter==null){
							adapter = new AppManagerAdapter();
							
							lv_app_manager.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						
					}
					
				});
			}
			
		}){}.start();
	}
	
	private class AppManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userAppInfos.size()+1+systemAppInfos.size()+1;
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
			AppInfo appInfo ;
			if(position==0){
				TextView tv = new TextView(SoftwareManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:"+userAppInfos.size()+"��");
				return tv;
			}else if(position==(userAppInfos.size())+1){
				TextView tv = new TextView(SoftwareManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:"+systemAppInfos.size()+"��");
				return tv;
			}else if(position<=userAppInfos.size()){
				//�û�����
				int newPosition = position-1;
				appInfo = userAppInfos.get(newPosition);
			}else{
				//ϵͳ����
				int newPosition = position -1 - userAppInfos.size()-1;
				appInfo = systemAppInfos.get(newPosition);
			}
			
			View view;
			ViewHolder holder;
			//ע�⣺�����ж��Ƿ�instanceof RelativeLayout,��Ϊ��������ӵ�TextView��û������tag,����������Ŀ������tag,
			//����view.getTag()�е�view������TextView��᷵�ؿ�
			if(convertView!=null && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(SoftwareManagerActivity.this, R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_loacation = (TextView) view.findViewById(R.id.tv_app_location);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.iv_app_lock = (ImageView) view.findViewById(R.id.iv_app_lock);
				view.setTag(holder);
			}
			
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if(appInfo.isInrom()){
				holder.tv_loacation.setText("�ֻ��ڴ�"+";uid="+appInfo.getUid());
			}else{
				holder.tv_loacation.setText("�ⲿ�洢"+";uid="+appInfo.getUid());
			}
			
			if(dao.find(appInfo.getPackagename())){
				holder.iv_app_lock.setImageResource(R.drawable.lock);
			}else{
				holder.iv_app_lock.setImageResource(R.drawable.unlock);
			}
			return view;
		}
		
	}
	/**
	 * �ؼ���ַ�洢��
	 * @author Administrator
	 *
	 */
	static class ViewHolder{
		TextView tv_name;
		TextView tv_loacation;
		ImageView iv_icon;
		ImageView iv_app_lock;
	}
	
	/**
	 * ��ȡ���õ��ڴ�ռ��С
	 * @param path
	 * @return
	 */
	private long getAvailableSpace(String path){
		StatFs statfs = new StatFs(path);
		//�����������
		int availableBlocksCount = statfs.getAvailableBlocks();
		//ÿ������Ĵ�С
		int blockSize = statfs.getBlockSize();
		
		return availableBlocksCount*blockSize;
		
	}
	
	private void dismissPopupWindow() {
		if(popupWindow!=null && popupWindow.isShowing()){
			//����������岻Ϊnull������ʾ�����ˣ����ڵ��ĳ��item��ʱ�򽫵������Ĵ���رյ�
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dismissPopupWindow();//��ΪPopupWindow�ǹ����ڴ����ϵģ�����ǰ��activity������ҲӦ�ð�pupupwindow���ٵ�����ᱨ��
		
	}

	@Override
	public void onClick(View v) {
		//���ȹر�С����
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_uninstall:
			//����listview��item�ĵ���¼���ÿ���һ�ζ���Գ�Ա����appInfo���Ӧ��ֵ�����Կ���ͨ��AppInfo��ȡ
			//�����Ϣ
			if(appInfo.isUserApp()){
				uninstallApplication();
			}else{
				Toast.makeText(this, "ϵͳӦ�ñ����ȡ��rootȨ�޲ſ���ж��", 0).show();
			}
			
			break;
			
		case R.id.ll_share:
			shareApplication();
			break;
			
		case R.id.ll_start:
			startApplication();
			break;

		default:
			break;
		}
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void shareApplication() {
		//����ͨ��һЩ�г��ϵ�app�����ܹ۲�logcat�����ݣ�Ȼ��֪����ͨ��ʲôintent���з����
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,"�Ƽ���ʹ��һ��app:"+appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ�ó���
	 */
	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.getPackagename()));
		startActivityForResult(intent, 0);
	}
	/**
	 * ж��Ӧ�ó��򷵻صĽ��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//���»�ȡ����ˢ�½���
		fillData();
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void startApplication() {
		// ��ѯ��ǰӦ�õ����activity�������������������activity�������嵥�ļ���
		/*<activity
        android:name="com.water.safedefender.SplashActivity"
        android:label="@string/app_name" >
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
        </activity>*/
		PackageManager pm = getPackageManager();
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
//		//��ѯ�����ֻ������о�������������activity
//		List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		
		//��ȡĳ������������������activity
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackagename());
		if(intent!=null){
			startActivity(intent);
		}else{
			Toast.makeText(this, "�޷�������Ӧ��", 0).show();
		}
	}
	
}
