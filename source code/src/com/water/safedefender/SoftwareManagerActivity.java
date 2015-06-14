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
		//内存可用大小
		long romAvailableSize = getAvailableSpace(Environment.getDataDirectory().getAbsolutePath());
		//sd卡可用大小
		long sdcardAvailableSize = getAvailableSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		//上面获取到的都是字节大小，需要格式化成MB
		tv_available_rom.setText("内存可用空间:"+Formatter.formatFileSize(this, romAvailableSize));
		tv_available_sdcard.setText("SD卡可用空间:"+Formatter.formatFileSize(this, sdcardAvailableSize));
	
		fillData();
	
		//给listview注册一个滚动的监听器
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				//注意：必须先判定不为空，因为这两个集合都是在子线程中获取的，可能还没有值就执行以下代码会报出空异常
				if(userAppInfos!=null && systemAppInfos!=null){
					if(firstVisibleItem>userAppInfos.size()){
						tv_appCount.setText("系统程序:"+systemAppInfos.size()+"个");
					}else{
						tv_appCount.setText("用户程序:"+userAppInfos.size()+"个");
					}
				}
				
			}
		});
		
		
		//设置listview每个item的点击事件
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(position==0){
					return;
				}else if(position==userAppInfos.size()+1){
					return;
				}else if(position<=userAppInfos.size()){
					//用户程序
					int newPosition = position-1;
					appInfo = userAppInfos.get(newPosition);
				}else{
					//系统程序
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
				
			    //注意：动画效果的播放必须要求窗体有背景颜色。PopupWindow默认没有背景颜色。
			    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			    
				int[] location = new int[2];
				view.getLocationInWindow(location);
				
				int dip = 58;
				int px = DensityUtil.dip2px(SoftwareManagerActivity.this, dip);
				//一般API中使用的数字都是px为单位的，所以需要dip和px之间的转换
				popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP, px, location[1]);
				
				
				//缩放动画
				ScaleAnimation sa = new ScaleAnimation(0.1f, 1f, 0.1f, 1f);
				sa.setDuration(500);
				//透明度动画
				AlphaAnimation aa = new AlphaAnimation(0.1f, 1f);
				aa.setDuration(500);
				//动画集
				AnimationSet as = new AnimationSet(false);
				as.addAnimation(sa);
				as.addAnimation(aa);
				//启动动画
				contentView.startAnimation(as);
				
			}

			
		});
	
		//设置listview每个item的长点击事件
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){
					return true;
				}else if(position==userAppInfos.size()+1){
					return true;
				}else if(position<=userAppInfos.size()){
					//用户程序
					int newPosition = position-1;
					appInfo = userAppInfos.get(newPosition);
				}else{
					//系统程序
					int newPosition = position-2-userAppInfos.size();
					appInfo = systemAppInfos.get(newPosition);
				}
				
				ViewHolder holder = (ViewHolder) view.getTag();
				//判断当前应用是否存到程序锁的数据库中
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
	 * 获取数据
	 */
	private void fillData() {
		ll_pd.setVisibility(View.VISIBLE);
		new Thread(new Runnable(){

			@Override
			public void run() {
				//可能安装的程序比较多，所以不宜放在主线程中
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
				//设置listview的适配器，因为是更新UI的操作，所以必须运行在UI线程
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
				tv.setText("用户程序:"+userAppInfos.size()+"个");
				return tv;
			}else if(position==(userAppInfos.size())+1){
				TextView tv = new TextView(SoftwareManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序:"+systemAppInfos.size()+"个");
				return tv;
			}else if(position<=userAppInfos.size()){
				//用户程序
				int newPosition = position-1;
				appInfo = userAppInfos.get(newPosition);
			}else{
				//系统程序
				int newPosition = position -1 - userAppInfos.size()-1;
				appInfo = systemAppInfos.get(newPosition);
			}
			
			View view;
			ViewHolder holder;
			//注意：必须判断是否instanceof RelativeLayout,因为上面新添加的TextView并没有设置tag,而是在子条目设置了tag,
			//所以view.getTag()中的view假如是TextView则会返回空
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
				holder.tv_loacation.setText("手机内存"+";uid="+appInfo.getUid());
			}else{
				holder.tv_loacation.setText("外部存储"+";uid="+appInfo.getUid());
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
	 * 控件地址存储类
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
	 * 获取可用的内存空间大小
	 * @param path
	 * @return
	 */
	private long getAvailableSpace(String path){
		StatFs statfs = new StatFs(path);
		//可用区块个数
		int availableBlocksCount = statfs.getAvailableBlocks();
		//每个区块的大小
		int blockSize = statfs.getBlockSize();
		
		return availableBlocksCount*blockSize;
		
	}
	
	private void dismissPopupWindow() {
		if(popupWindow!=null && popupWindow.isShowing()){
			//如果弹出窗体不为null并且显示出来了，则在点击某个item的时候将弹出来的窗体关闭掉
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dismissPopupWindow();//因为PopupWindow是挂载在窗体上的，当当前的activity销毁了也应该把pupupwindow销毁掉否则会报错
		
	}

	@Override
	public void onClick(View v) {
		//首先关闭小气泡
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_uninstall:
			//上面listview的item的点击事件中每点击一次都会对成员变量appInfo相对应赋值，所以可以通过AppInfo获取
			//相关信息
			if(appInfo.isUserApp()){
				uninstallApplication();
			}else{
				Toast.makeText(this, "系统应用必须获取到root权限才可以卸载", 0).show();
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
	 * 分享应用程序
	 */
	private void shareApplication() {
		//可以通过一些市场上的app分享功能观察logcat的内容，然后知道是通过什么intent进行分享的
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,"推荐您使用一款app:"+appInfo.getName());
		startActivity(intent);
	}

	/**
	 * 卸载应用程序
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
	 * 卸载应用程序返回的结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//重新获取数据刷新界面
		fillData();
	}

	/**
	 * 启动应用程序
	 */
	private void startApplication() {
		// 查询当前应用的入口activity，将它启动起来，入口activity配置在清单文件中
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
//		//查询出了手机上所有具有启动能力的activity
//		List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		
		//获取某个包具有启动能力的activity
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackagename());
		if(intent!=null){
			startActivity(intent);
		}else{
			Toast.makeText(this, "无法启动该应用", 0).show();
		}
	}
	
}
