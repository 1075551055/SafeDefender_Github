package com.water.safedefender.receiver;

import com.water.safedefender.dao.PhoneAddressQueryUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		/*//��ȡ���Ⲧ����
		String phoneNumber = getResultData();//ģ�����к����110ǰ׺
		//��ѯ���ݿ��ȡ������
		String address = PhoneAddressQueryUtils.queryAddress(context.getFilesDir()+"/address.db", phoneNumber);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();*/
	}

}
