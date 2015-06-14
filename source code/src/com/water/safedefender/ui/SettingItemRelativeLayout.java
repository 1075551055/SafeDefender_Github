package com.water.safedefender.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.water.safedefender.R;

/*
 * �Զ�����Ͽؼ�����̳л��߼�Ӽ̳�ViewGroup���Ĵ󲼾ֶ��Ǽ̳���ViewGroup��
 * ֻ��ViewGroup���Ͳſ���������һ�������ļ�����View.inflate�еĲ����йأ�����������������ҪViewGroup�����������װ�ɿؼ�
 */
public class SettingItemRelativeLayout extends RelativeLayout {

	public TextView tv_promptUpdate;
	public TextView tv_promptUpdateDesc;
	public CheckBox cb_promptUpdateSetting;
	private String textOpen;
	private String textOff;
	private String textIsPromptUpdate;
	
	/*
	 * ��������ʽ��ʱ����ø÷���
	 */
	public SettingItemRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/*
	 * �ڲ����ļ������øÿؼ���ʱ��ִֻ�����������Ĺ��췽��
	 */
	public SettingItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		//ע�⣺����װ��Զ�����Ͽؼ��У�Ȼ����ȥʹ��findViewById,����tv_promptUpdate��tv_promptUpdateDesc��cb_promptUpdateSetting��ΪNULL
		init(context);
		tv_promptUpdate = (TextView) findViewById(R.id.tv_promptUpdate);
		tv_promptUpdateDesc = (TextView) findViewById(R.id.tv_promptUpdateDesc);
		cb_promptUpdateSetting = (CheckBox) findViewById(R.id.cb_promptUpdateSetting);
		
		//�����Զ������ԵĲ��裺1.�ڲ����ļ������������ռ��Ѿ�����ǰ׺�����磺 xmlns:water="http://schemas.android.com/����"��
		//				2.����һ����������xml:attrs.xml,���Բ���sdk�µ�\sdk\platforms\android-18\data\res\values\attrs.xml
		//				3.ͨ�������ռ��ȡ���Ӧ�����Ե�ֵ��
		//				4.��3�л�ȡ����ֵ�����Ӧ�Ŀؼ���ϵ����
		//ͨ�������ռ��ȡ���Ӧ�����Ե�ֵ
		textIsPromptUpdate = attrs.getAttributeValue("http://schemas.android.com/com.water.safedefender","textIsPromptUpdate");
		textOpen = attrs.getAttributeValue("http://schemas.android.com/com.water.safedefender","textOpen");
		textOff = attrs.getAttributeValue("http://schemas.android.com/com.water.safedefender","textOff");
		
		//����ȡ�������Ե�ֵ��ؼ���ϵ����
		tv_promptUpdate.setText(textIsPromptUpdate);
		tv_promptUpdateDesc.setText(textOff);
		
	}

	/*
	 * ͨ��new��ʽ��ʱ����ø÷���
	 */
	public SettingItemRelativeLayout(Context context) {
		super(context);
		init(context);
		
	}
	
	/*
	 * ��ʼ���ؼ����������ļ�װ�SettingItemRelativeLayout����
	 */
	private void init(Context context){
		View.inflate(context, R.layout.setting_item, this);
	}
	
	/*
	 * ����CheckBox�Ƿ񱻹�ѡ
	 */
	public void setCheckBoxStatus(boolean isCheck){
		cb_promptUpdateSetting.setChecked(isCheck);
	}
	
	/*
	 * ������ʾ����������Ϣ��������ֵ�Ϳؼ���ϵ����
	 */
	public void setPromptUpdateDescription(boolean isCheck){
		tv_promptUpdateDesc.setText(isCheck?textOpen:textOff);
	}
	
}
