package com.jlucs.safephone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MessageView extends Activity{

	private TextView messageContent;
	private TextView messageType;
	private TextView messageName;
	private TextView messageDate;
	private String number;
	private String date;
	private String type;
	private String name;
	private String content;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);

		messageContent = (TextView) findViewById(R.id.tv_message_content);
		messageType = (TextView) findViewById(R.id.tv_message_type);
		messageName = (TextView) findViewById(R.id.tv_message_addr);
		messageDate = (TextView) findViewById(R.id.tv_message_date_detail);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		content = bundle.getString("content");
		name = bundle.getString("addr");
		type = bundle.getString("type");
		date = bundle.getString("date");
		number = bundle.getString("number");

		if(type.equals("1")){
			messageType.setText("From");
		}
		else{
			messageType.setText("Send");
		}
		messageName.setText(name);
		messageDate.setText(date);
		messageContent.setText(content);
	}

	public void sendMessage(View view){
		Intent smsIntent=new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+number));
		startActivity(smsIntent);
	}
	public void finishMessageDetail(View view){
		finish();
	}
}
