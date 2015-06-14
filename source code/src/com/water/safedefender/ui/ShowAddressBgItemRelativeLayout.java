package com.water.safedefender.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.water.safedefender.R;

/*
 * 自定义组合控件必须继承或者间接继承ViewGroup，四大布局都是继承于ViewGroup，
 * 只有ViewGroup类型才可以容纳下一个布局文件（与View.inflate中的参数有关，第三个参数就是需要ViewGroup），并将其封装成控件
 */
public class ShowAddressBgItemRelativeLayout extends RelativeLayout {
	
	public TextView tv_promptUpdate;
	public TextView tv_promptUpdateDesc;
	public ImageView iv_jiantou;
	private String textOpen;
	private String textOff;
	private String textIsPromptUpdate;
	
	/*
	 * 设置了样式的时候调用该方法
	 */
	public ShowAddressBgItemRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/*
	 * 在布局文件中引用该控件的时候，只执行两个参数的构造方法
	 */
	public ShowAddressBgItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		//注意：得先装填到自定义组合控件中，然后再去使用findViewById,否则tv_promptUpdate，tv_promptUpdateDesc，cb_promptUpdateSetting都为NULL
		init(context);
		tv_promptUpdate = (TextView) findViewById(R.id.tv_promptUpdate);
		tv_promptUpdateDesc = (TextView) findViewById(R.id.tv_promptUpdateDesc);
		iv_jiantou = (ImageView) findViewById(R.id.iv_jiantou);
		
		//设置自定义属性的步骤：1.在布局文件中引入命名空间已经属性前缀，例如： xmlns:water="http://schemas.android.com/包名"；
		//				2.定义一个属性声明xml:attrs.xml,可以参照sdk下的\sdk\platforms\android-18\data\res\values\attrs.xml
		//				3.通过命名空间获取相对应的属性的值；
		//				4.将3中获取到的值与相对应的控件联系起来
		//通过命名空间获取相对应的属性的值
		textIsPromptUpdate = attrs.getAttributeValue("http://schemas.android.com/com.water.safedefender","textIsPromptUpdate");
		textOpen = attrs.getAttributeValue("http://schemas.android.com/com.water.safedefender","textOpen");
		textOff = attrs.getAttributeValue("http://schemas.android.com/com.water.safedefender","textOff");
		
		//将获取到的属性的值与控件联系起来
		tv_promptUpdate.setText(textIsPromptUpdate);
		tv_promptUpdateDesc.setText(textOff);
		
	}

	/*
	 * 通过new方式的时候调用该方法
	 */
	public ShowAddressBgItemRelativeLayout(Context context) {
		super(context);
		init(context);
		
	}
	
	/*
	 * 初始化控件，将布局文件装填到SettingItemRelativeLayout类中
	 */
	private void init(Context context){
		View.inflate(context, R.layout.show_addressbg_item, this);
	}
	
	/*
	 * 设置描述信息
	 */
	public void setDesc(String description){
		tv_promptUpdateDesc.setText(description);
	}
}
