package com.jlucs.safephone.receiver;


import java.io.IOException;

import android.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private SharedPreferences sharedPreferences;

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for(Object object:objects){
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			String body = message.getMessageBody();
			String address = message.getOriginatingAddress();
			if("alarm".equals(body)){
				MediaPlayer player = MediaPlayer.create(context, com.jlucs.safephone.R.raw.alarm);
				player.setLooping(true);
				player.start();
				abortBroadcast();
			}
		}
	}

}
