package com.jlucs.safephone;

import com.jlucs.safephone.utils.AddressUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchActivity extends Activity {

	private EditText editText;
	private TextView result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		editText = (EditText) findViewById(R.id.et_search);
		result = (TextView) findViewById(R.id.tv_search_result);
	}
	public void query(View view) {
		String numberString=editText.getText().toString();
		String address = AddressUtils.getAddress(numberString);
		result.setText(address);
	}
}
