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
		 * listview�Ĺ����¼�
		 */
		lv_blackList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE://����ֹͣ���ǿ���״̬
					//��ȡlistview��ǰ��Ļ���Կ��������һ��λ��
					int lastPosition = lv_blackList.getLastVisiblePosition();
					//�����������һ��λ�õļ�¼��ʱ�������������
					if(lastPosition==blackNumbers.size()-1){
						offset += maxNumber;
						fillData();
					}
					break;

				case OnScrollListener.SCROLL_STATE_FLING://���Ի���״̬
					
					break;
					
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://��������״̬
					
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
	 * ����data
	 */
	private void fillData() {
		ll_load.setVisibility(View.VISIBLE);
		//ͨ�����̻߳�ȡ
		new Thread(){
			public void run() {
				SystemClock.sleep(500);//ģ��������������
				if(blackNumbers==null){
					//��ʾ��û�м��ع�����
					blackNumbers = dao.findPart(offset, maxNumber);
				}else{
					//�Ѿ����ع����ݣ����Խ�Ҫ���ص�������ӵ�list������
					blackNumbers.addAll(dao.findPart(offset, maxNumber));
				}
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_load.setVisibility(View.INVISIBLE);
						//ע�⣺���Ǹ���UI�Ĳ��������Ա���������߳̾���UI�߳�
						//adapter = new MyAdapter();//����newһ���������Ļ���ҳ���������һ����¼
						if(adapter==null){
							adapter = new MyAdapter();
							lv_blackList.setAdapter(adapter);
						}else{
							//����Ѿ����ع������ˣ�ֱ��֪ͨ���������¾�OK
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
			//����convertView�������Ż�
			if(convertView==null){
				view = View.inflate(CommunicationGuardActivity.this, R.layout.list_item_blacknumber, null);
			
				//��ΪfindViewById�����ܺ�ʱ�䡣������Ҫ��������Ż�
				//���ٸ����ؼ��Ĳ��Ҵ���,��ÿ���ؼ�������ǵ�ַ�ŵ�holder���棬����ͨ��view.setTag�����ǩ
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
				holder.tv_block_mode.setText("�绰����");
			}else if("2".equals(mode)){
				holder.tv_block_mode.setText("��������");
			}else{
				holder.tv_block_mode.setText("ȫ������");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CommunicationGuardActivity.this);
					builder.setTitle("����");
					builder.setMessage("��ȷ��ɾ����?");
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//ɾ�����ݿ����Ϣ
							dao.delete(blackNumbers.get(position).getNumber());
							//����activity
							blackNumbers.remove(position);
							//֪ͨlistview�����������Ѿ�������
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.show();
				}
			});
			
			return view;
		}
		
	}
	
	/**
	 * view��������
	 * ��¼view���ڴ��ַ
	 * @author Administrator
	 *static���ֽ���ֻ������һ��
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
					Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", 0).show();
					return;
				}
				String mode ;
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//ȫ������
					mode = "3";
				}else if(cb_phone.isChecked()){
					//�绰����
					mode = "1";
				}else if(cb_sms.isChecked()){
					//��������
					mode = "2";
				}else{
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 0).show();
					return;
				}
				//���ݱ��ӵ����ݿ�
				dao.add(blacknumber, mode);
				//����listview������������ݡ�
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				blackNumbers.add(0, info);//������ӵļ�¼�ŵ�list���ϵ�һλ
				//֪ͨlistview�������������ݸ����ˡ�
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}

}
