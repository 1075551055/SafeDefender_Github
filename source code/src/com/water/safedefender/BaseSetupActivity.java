package com.water.safedefender;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.手势监听器
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// 2.实例化手势监听器
		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			// 手机滑动屏幕触发该事件
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				
				//速度太慢也不允许滑动
				if(Math.abs(velocityX) < 200){
					Toast.makeText(BaseSetupActivity.this, "划得太慢了", Toast.LENGTH_SHORT).show();
					return true;
				}
					
				
				//屏蔽斜滑动
				if(Math.abs(e1.getRawY() - e2.getRawY())>100){
					Toast.makeText(BaseSetupActivity.this, "不能这样子滑动", Toast.LENGTH_SHORT).show();
					return true;
				}
				
				
				// e1表示滑动的起始点，e2表示滑动的终点
				if (e1.getRawX() - e2.getRawX() > 150) {
					// 表示从右往左滑动，显示下一个页面
					System.out.println("表示从右往左滑动，显示下一个页面");
					showNext();
					return true;
				}
				if (e2.getRawX() - e1.getRawX() > 150) {
					// 表示从左往右滑动，显示上一个页面
					System.out.println("表示从左往右滑动，显示上一个页面");
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

	// 3.使用手势监听器
	// 屏幕触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 将屏幕触摸事件传递给手势监听器
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
