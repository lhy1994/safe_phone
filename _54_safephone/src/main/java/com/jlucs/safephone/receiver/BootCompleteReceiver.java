package com.jlucs.safephone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"config", Context.MODE_PRIVATE);
		String serialNumber = sharedPreferences.getString("sim", "");

		if (!TextUtils.isEmpty(serialNumber)) {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			String currentSerialNumberString = telephonyManager
					.getSimSerialNumber()+"123";
			if (currentSerialNumberString.equals(serialNumber)) {
				System.out.println("safe");

			} else {
				String safeNumber = sharedPreferences.getString(
						"safe_phone_number", "");
				android.telephony.SmsManager smsManager = android.telephony.SmsManager
						.getDefault();
				smsManager.sendTextMessage(safeNumber, null, "手机可能被盗",
						null, null);
			}
		}

	}

}
