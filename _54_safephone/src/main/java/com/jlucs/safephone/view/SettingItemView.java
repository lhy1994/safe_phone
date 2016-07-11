package com.jlucs.safephone.view;


import com.jlucs.safephone.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private TextView title;
	private TextView description;
	private CheckBox checkBox;

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	@SuppressLint("NewApi") public SettingItemView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		View view=View.inflate(getContext(), R.layout.setting_item, this);
		title = (TextView) view.findViewById(R.id.tv_setting_title);
		description = (TextView) view.findViewById(R.id.tv_setting_desc);
		checkBox = (CheckBox) view.findViewById(R.id.chb_setting_check);
	}
	public void setTitile(String title) {
		this.title.setText(title);
	}
	public void setDesc(String desc) {
		description.setText(desc);
	}
	public boolean isChecked() {
		return checkBox.isChecked();
	}
	public void setChecked(boolean b) {
		checkBox.setChecked(b);
	}
}
