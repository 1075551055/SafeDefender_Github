package com.water.safedefender;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.���Ƽ�����
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// 2.ʵ�������Ƽ�����
		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			// �ֻ�������Ļ�������¼�
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				//�ٶ�̫��Ҳ��������
				if(Math.abs(velocityX) < 200){
					Toast.makeText(BaseSetupActivity.this, "����̫����", Toast.LENGTH_SHORT).show();
					return true;
				}
					
				
				//����б����
				if(Math.abs(e1.getRawY() - e2.getRawY())>100){
					Toast.makeText(BaseSetupActivity.this, "���������ӻ���", Toast.LENGTH_SHORT).show();
					return true;
				}
				
				
				// e1��ʾ��������ʼ�㣬e2��ʾ�������յ�
				if (e1.getRawX() - e2.getRawX() > 150) {
					// ��ʾ�������󻬶�����ʾ��һ��ҳ��
					System.out.println("��ʾ�������󻬶�����ʾ��һ��ҳ��");
					showNext();
					return true;
				}
				if (e2.getRawX() - e1.getRawX() > 150) {
					// ��ʾ�������һ�������ʾ��һ��ҳ��
					System.out.println("��ʾ�������һ�������ʾ��һ��ҳ��");
					showPre();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});
	}
	
	public abstract void showNext();
	public abstract void showPre();
	
	public void next(View v){
		showNext();
	}
	
	public void pre(View v){
		showPre();
	}

	// 3.ʹ�����Ƽ�����
	// ��Ļ�����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ����Ļ�����¼����ݸ����Ƽ�����
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
