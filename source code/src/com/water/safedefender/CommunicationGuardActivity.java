package com.water.safedefender;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.water.safedefender.dao.BlackNumberDao;
import com.water.safedefender.domain.BlackNumberInfo;

public class CommunicationGuardActivity extends Activity {

	private List<BlackNumberInfo> blackNumbers;
	private ListView lv_blackList;
	private MyAdapter adapter;
	private BlackNumberDao dao;
	private int offset = 0;
	private int maxNumber = 20;
	private LinearLayout ll_load;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_guard);
		
		ll_load= (LinearLayout) findViewById(R.id.ll_loadData);
		lv_blackList = (ListView) findViewById(R.id.lv_blackList);
		dao= new BlackNumberDao(this);
		fillData();
		
		/**
		 * listview的滚动事件
		 */
		lv_blackList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE://滚动停止即是空闲状态
					//获取listview当前屏幕可以看到的最后一个位置
					int lastPosition = lv_blackList.getLastVisiblePosition();
					//当滑动到最后一个位置的记录的时候继续加载数据
					if(lastPosition==blackNumbers.size()-1){
						offset += maxNumber;
						fillData();
					}
					break;

				case OnScrollListener.SCROLL_STATE_FLING://惯性滑动状态
					
					break;
					
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸滑动状态
					
					break;
				default:
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	/**
	 * 加载data
	 */
	private void fillData() {
		ll_load.setVisibility(View.VISIBLE);
		//通过子线程获取
		new Thread(){
			public void run() {
				SystemClock.sleep(500);//模拟请求网络数据
				if(blackNumbers==null){
					//表示还没有加载过数据
					blackNumbers = dao.findPart(offset, maxNumber);
				}else{
					//已经加载过数据，所以将要加载的数据添加到list集合中
					blackNumbers.addAll(dao.findPart(offset, maxNumber));
				}
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_load.setVisibility(View.INVISIBLE);
						//注意：这是更新UI的操作，所以必须放在主线程就是UI线程
						//adapter = new MyAdapter();//重新new一个适配器的话，页面会跳到第一条记录
						if(adapter==null){
							adapter = new MyAdapter();
							lv_blackList.setAdapter(adapter);
						}else{
							//如果已经加载过数据了，直接通知适配器更新就OK
							adapter.notifyDataSetChanged();
						}
						
					}
				});
				
			};
		}.start();
	}
	
	

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return blackNumbers.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder;
			//复用convertView，性能优化
			if(convertView==null){
				view = View.inflate(CommunicationGuardActivity.this, R.layout.list_item_blacknumber, null);
			
				//因为findViewById方法很耗时间。所以需要对其进行优化
				//减少各个控件的查找次数,将每个控件对象就是地址放到holder里面，并且通过view.setTag打个标签
				holder = new ViewHolder();
				holder.tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_block_mode = (TextView) view.findViewById(R.id.tv_block_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder);
			}else{
				view = convertView;
				//
				holder = (ViewHolder) view.getTag();
			}
			
			BlackNumberInfo info = blackNumbers.get(position);
			holder.tv_black_number.setText(info.getNumber());
			String mode = info.getMode();
			if("1".equals(mode)){
				holder.tv_block_mode.setText("电话拦截");
			}else if("2".equals(mode)){
				holder.tv_block_mode.setText("短信拦截");
			}else{
				holder.tv_block_mode.setText("全部拦截");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CommunicationGuardActivity.this);
					builder.setTitle("警告");
					builder.setMessage("你确定删除吗?");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//删除数据库的信息
							dao.delete(blackNumbers.get(position).getNumber());
							//更新activity
							blackNumbers.remove(position);
							//通知listview适配器数据已经更新了
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			
			return view;
		}
		
	}
	
	/**
	 * view对象容器
	 * 记录view的内存地址
	 * @author Administrator
	 *static：字节码只给加载一次
	 */
	private static class ViewHolder{
		TextView tv_black_number;
		TextView tv_block_mode;
		ImageView iv_delete;
	}
	
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	
	public void addBlackNumber(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		bt_cancel = (Button) contentView.findViewById(R.id.cancel);
		bt_ok = (Button) contentView.findViewById(R.id.ok);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空", 0).show();
					return;
				}
				String mode ;
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//全部拦截
					mode = "3";
				}else if(cb_phone.isChecked()){
					//电话拦截
					mode = "1";
				}else if(cb_sms.isChecked()){
					//短信拦截
					mode = "2";
				}else{
					Toast.makeText(getApplicationContext(), "请选择拦截模式", 0).show();
					return;
				}
				//数据被加到数据库
				dao.add(blacknumber, mode);
				//更新listview集合里面的内容。
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				blackNumbers.add(0, info);//最新添加的记录放到list集合第一位
				//通知listview数据适配器数据更新了。
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}

}
