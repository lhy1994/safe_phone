package com.jlucs.safephone;

import com.jlucs.safephone.utils.MD5Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
		editText = (EditText) findViewById(R.id.et_password);
	}
	public void login(View view) {
		String password=editText.getText().toString();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
		}else {
			String pwd=sharedPreferences.getString("password", "202cb962ac59075b964b07152d234b70");
			if (TextUtils.equals(MD5Utils.encode(password), pwd)) {
				startActivity(new Intent(this,MainActivity.class));
				finish();
			}else {
				Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
