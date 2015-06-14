package com.water.safedefender.receiver;

import com.water.safedefender.dao.PhoneAddressQueryUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		/*//获取到外拨号码
		String phoneNumber = getResultData();//模拟器中号码带110前缀
		//查询数据库获取归属地
		String address = PhoneAddressQueryUtils.queryAddress(context.getFilesDir()+"/address.db", phoneNumber);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();*/
	}

}
