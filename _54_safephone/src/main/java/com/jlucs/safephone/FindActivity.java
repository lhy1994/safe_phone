package com.jlucs.safephone;

import com.jlucs.safephone.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class FindActivity extends Activity {

	private SharedPreferences preferences;
	private SettingItemView settingItemView;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find);
		preferences = getSharedPreferences("config", MODE_PRIVATE);
		settingItemView = (SettingItemView) findViewById(R.id.sim);
		settingItemView.setTitile("绑定SIM卡");
		settingItemView.setDesc("绑定SIM卡后，一旦SIM卡发生改变，将自动给安全号码发送短信");
		editText = (EditText) findViewById(R.id.et_safe_number);
		
		init();
		check();
	}

	private void init() {
		String sim = preferences.getString("sim", "");
		String safeNumber = preferences.getString("safe_phone_number", "");
		editText.setText(safeNumber);
		
		if (TextUtils.isEmpty(sim)) {
			settingItemView.setChecked(false);
		} else {
			settingItemView.setChecked(true);
		}
		settingItemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (settingItemView.isChecked()) {
					settingItemView.setChecked(false);
					Toast.makeText(FindActivity.this, "SIM卡已解绑", 0).show();
					preferences.edit().remove("sim").commit();
				} else {
					Toast.makeText(FindActivity.this, "SIM卡已绑定", 0).show();
					settingItemView.setChecked(true);
					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String serialNumber = telephonyManager.getSimSerialNumber();
					System.out.println(serialNumber);
					preferences.edit().putString("sim", serialNumber).commit();
				}
			}
		});
	}

	private void check() {
		String sim = preferences.getString("sim", "");
		if (TextUtils.isEmpty(sim)) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("没有绑定SIM卡");
			builder.setMessage("还没绑定SIM卡，请绑定SIM卡以开启防盗功能");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.show();
		}
	}
	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 1);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK){
			String phoneNumber=data.getStringExtra("phone");
			editText.setText(phoneNumber);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void saveNmber(View view) {
		String safeNumber = editText.getText().toString();
		preferences.edit().putString("safe_phone_number", safeNumber).commit();
		Toast.makeText(this, "保存成功", 0).show();
	}
}
