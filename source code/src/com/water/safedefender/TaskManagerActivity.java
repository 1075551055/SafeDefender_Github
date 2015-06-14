package com.water.safedefender;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.water.safedefender.domian.TaskInfo;
import com.water.safedefender.utils.SystemInfoUtils;
import com.water.safedefender.utils.TaskInfoProvider;

public class TaskManagerActivity extends Activity {

	private TextView tv_process_count;
	private TextView tv_mem_info;
	private LinearLayout ll_pd;
	private ListView lv_task_manager;
	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	private TaskManagerAdapter adapter;
	private TextView tv_taskCount;
	private CheckBox cb_status;
	private LinearLayout ll_buttons;
	private int processCount ;
	private long availableMemery ;
	private long totalMemery;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		
		setTitle();
		
		ll_pd = (LinearLayout) findViewById(R.id.ll_pd);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
		tv_taskCount = (TextView) findViewById(R.id.tv_taskCount);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		ll_buttons = (LinearLayout) findViewById(R.id.ll_buttons);
	
		fillData();
		
		lv_task_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(userTaskInfos!=null&&systemTaskInfos!=null){
					//��Ϊ�����߶���ͨ�����̻߳�ȡ�ģ�������Ҫ��ǰ�ж��Ƿ�Ϊnull
					if(firstVisibleItem>userTaskInfos.size()){
						tv_taskCount.setText("ϵͳ����:"+systemTaskInfos.size()+"��");
					}else{
						tv_taskCount.setText("�û�����:"+userTaskInfos.size()+"��");
						
					}
				}
			}
		});
		
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo;
				if(position==0){
					return;
				}else if(position==(userTaskInfos.size())+1){
					return;
				}else if(position<=userTaskInfos.size()){
					//�û�����
					int newPosition = position-1;
					taskInfo = userTaskInfos.get(newPosition);
				}else{
					//ϵͳ����
					int newPosition = position -2 - userTaskInfos.size();
					taskInfo = systemTaskInfos.get(newPosition);
				}
				if(getPackageName().equals(taskInfo.getPackagename())){
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if(taskInfo.isChecked()){
					taskInfo.setChecked(false);
					holder.cb_status.setChecked(false);
				}else{
					taskInfo.setChecked(true);
					holder.cb_status.setChecked(true);
				}
			}
			
		});
	}



	private void setTitle() {
		int processCount = SystemInfoUtils.getRunningServiceCount(this);
		long availableMemery = SystemInfoUtils.getAvailableMemery(this);
		long totalMemery = SystemInfoUtils.getTotalMemery(this);
		tv_process_count.setText("�����еĽ���:"+processCount+"��");
		tv_mem_info.setText("ʣ��/���ڴ�:"+Formatter.formatFileSize(this, availableMemery)+"/"+Formatter.formatFileSize(this, totalMemery));
	}
	
	
	
	/**
	 * ��ȡ����
	 */
	private void fillData() {
		ll_pd.setVisibility(View.VISIBLE);
		new Thread(new Runnable(){

			@Override
			public void run() {
				allTaskInfos = TaskInfoProvider.getAllTaskInfos(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo info : allTaskInfos) {
					if(info.isUserTask()){
						userTaskInfos.add(info);
					}else{
						systemTaskInfos.add(info);
					}
				}
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						ll_pd.setVisibility(View.INVISIBLE);
						tv_taskCount.setVisibility(View.VISIBLE);
						ll_buttons.setVisibility(View.VISIBLE);
						if(adapter==null){
							adapter = new TaskManagerAdapter();
							lv_task_manager.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						setTitle();
					}
					
				});
			}
			
		}){}.start();
	}

	private class TaskManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allTaskInfos.size()+2;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskInfo;
			if(position==0){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:"+userTaskInfos.size()+"��");
				return tv;
			}else if(position==(userTaskInfos.size())+1){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:"+systemTaskInfos.size()+"��");
				return tv;
			}else if(position<=userTaskInfos.size()){
				//�û�����
				int newPosition = position-1;
				taskInfo = userTaskInfos.get(newPosition);
			}else{
				//ϵͳ����
				int newPosition = position -2 - userTaskInfos.size();
				taskInfo = systemTaskInfos.get(newPosition);
			}
			
			View view;
			ViewHolder holder;
			if(convertView!=null && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(TaskManagerActivity.this, R.layout.list_item_taskinfo, null);
				holder = new ViewHolder();
				holder.tv_task_mem = (TextView) view.findViewById(R.id.tv_task_mem);
				holder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
				holder.tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
				holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				
				view.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_task_mem.setText("�ڴ�ռ��:"+Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemerySize()));
			holder.tv_task_name.setText(taskInfo.getName());
			holder.cb_status.setChecked(taskInfo.isChecked());
			if(getPackageName().equals(taskInfo.getPackagename())){
				holder.cb_status.setVisibility(View.INVISIBLE);
			}else{//������Ҫע�⣺��Ϊ���û���view��ʱ����Ҫ�õ�checkbox,����Ҫ���ÿɼ�
				holder.cb_status.setVisibility(View.VISIBLE);
			}
			return view;
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
	}
	
	static class ViewHolder{
		TextView tv_task_mem;
		TextView tv_task_name;
		ImageView iv_task_icon;
		CheckBox cb_status;
	}
	/**
	 * ȫѡ
	 */
	public void selectAll(View v){
		for (TaskInfo info : allTaskInfos) {
			if(getPackageName().equals(info.getPackagename())){
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * ��ѡ
	 */
	public void selectOpposite(View v){
		for (TaskInfo info : allTaskInfos) {
			if(getPackageName().equals(info.getPackagename())){
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * һ������
	 */
	public void killAll(View v){
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int count=0;
		long freeMemory = 0;
		List<TaskInfo> removeTaskInfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : allTaskInfos) {
			if(info.isChecked()){
				am.killBackgroundProcesses(info.getPackagename());
				if(info.isUserTask()){
					userTaskInfos.remove(info);
				}else{
					systemTaskInfos.remove(info);
				}
				removeTaskInfos.add(info);
				count++;
				freeMemory+=info.getMemerySize();
			}
		}
		allTaskInfos.removeAll(removeTaskInfos);
		adapter.notifyDataSetChanged();
		Toast.makeText(TaskManagerActivity.this, "ɱ����"+count+"������,�ͷ���"+Formatter.formatFileSize(TaskManagerActivity.this, freeMemory)+"���ڴ�", 0).show();
		processCount-=count;
		availableMemery+=freeMemory;
		tv_process_count.setText("�����еĽ���:"+processCount+"��");
		tv_mem_info.setText("ʣ��/���ڴ�:"+Formatter.formatFileSize(this, availableMemery)+"/"+Formatter.formatFileSize(this, totalMemery));
	}
	
	/**
	 * ����
	 */
	public void setting(View v){
		
	}
}
