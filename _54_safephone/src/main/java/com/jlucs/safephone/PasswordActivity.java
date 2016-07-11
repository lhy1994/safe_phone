package com.jlucs.safephone;

import java.util.ArrayList;

import com.jlucs.safephone.bean.PasswordInfo;
import com.jlucs.safephone.utils.DatabaseUtils;
import com.jlucs.safephone.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class PasswordActivity extends Activity {

	private ListView listView;
	private DatabaseUtils databaseUtils;
	private ArrayList<PasswordInfo> list;
	private ListAdapter adapter;
	private SharedPreferences preferences;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_password);
		listView = (ListView) findViewById(R.id.lv_password);
		databaseUtils = new DatabaseUtils(this);
		preferences = getSharedPreferences("password", MODE_PRIVATE);
		searchView = (SearchView) findViewById(R.id.sv_password);
		init();
	}

	private void init() {
		list = databaseUtils.query();
		if (list!=null) {
			adapter = new ListAdapter();
			listView.setAdapter(adapter);
		}
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				list=databaseUtils.search(query);
				adapter.notifyDataSetChanged();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		searchView.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public boolean onClose() {
				list=databaseUtils.query();
				adapter.notifyDataSetChanged();
				return false;
			}
		});
		searchView.setSubmitButtonEnabled(true);
	}
	public void add(View view) {
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("添加新密码");
		View dialogView=View.inflate(this, R.layout.dialog_password, null);
		final EditText user=(EditText) dialogView.findViewById(R.id.et_user);
		final EditText password=(EditText) dialogView.findViewById(R.id.et_password);
		final EditText desc=(EditText) dialogView.findViewById(R.id.et_desc);
		builder.setView(dialogView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String userString=user.getText().toString();
				String pass=password.getText().toString();
				String descsString=desc.getText().toString();
				if (TextUtils.isEmpty(userString)||TextUtils.isEmpty(pass)) {
					Toast.makeText(PasswordActivity.this, "用户名或密码不能为空", 1).show();
				}else {
					PasswordInfo info=new PasswordInfo();
					info.setUser(userString);
					info.setPassword(pass);
					info.setDescription(descsString);
					databaseUtils.add(info);
					list=databaseUtils.query();
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	public void changePwd(View view) {
		final String oldPass=preferences.getString("password", "202cb962ac59075b964b07152d234b70");
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("请输入原密码");
		final EditText editText=new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
		builder.setView(editText);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String inputPass = editText.getText().toString();
				if (TextUtils.equals(MD5Utils.encode(inputPass), oldPass)) {
					AlertDialog.Builder builder3 = new Builder(
							PasswordActivity.this);
					
					builder3.setTitle("确定新密码");
					View view=View.inflate(PasswordActivity.this, R.layout.dialog_update_password, null);
					Button button=(Button) view.findViewById(R.id.btn_updatePwd);
					final EditText firstEditText=(EditText) view.findViewById(R.id.et_firstPwd);
					final EditText secondEditText=(EditText) view.findViewById(R.id.et_secondPwd);
					final TextView textView=(TextView) view.findViewById(R.id.tv_updatePwd);
					firstEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
					secondEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
					builder3.setView(view);
					
					final AlertDialog alertDialog=builder3.create();
					button.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							String first=firstEditText.getText().toString();
							String second=secondEditText.getText().toString();
							if (TextUtils.isEmpty(first)||TextUtils.isEmpty(second)) {
								textView.setVisibility(View.VISIBLE);
								textView.setText("密码不能为空");
							}else {
								if (TextUtils.equals(first, second)) {
									preferences.edit().putString("password", MD5Utils.encode(first)).commit();
									alertDialog.dismiss();
									Toast.makeText(PasswordActivity.this, "保险箱密码修改成功", 0).show();
								}else {
									textView.setVisibility(View.VISIBLE);
									textView.setText("密码不一致");
								}
							}
						}
					});
					
					alertDialog.show();
				}else {
					Toast.makeText(PasswordActivity.this, "密码错误", 0).show();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView==null) {
				convertView=View.inflate(PasswordActivity.this, R.layout.password_list_item, null);
				holder=new ViewHolder();
				holder.user=(TextView) convertView.findViewById(R.id.tv_user);
				holder.password=(TextView) convertView.findViewById(R.id.tv_password);
				holder.description=(TextView) convertView.findViewById(R.id.tv_desciption);
				holder.delete=(Button) convertView.findViewById(R.id.btn_delete);
				holder.edit=(Button) convertView.findViewById(R.id.btn_edit);
				convertView.setTag(holder);
			}else {
				holder=(ViewHolder) convertView.getTag();
			}
			final PasswordInfo info = list.get(position);
			holder.user.setText(info.getUser());
			holder.password.setText(info.getPassword());
			holder.description.setText(info.getDescription());
			
			holder.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					databaseUtils.delete(info.getUser());
					list=databaseUtils.query();
					adapter.notifyDataSetChanged();
				}
			});
			holder.edit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder=new Builder(PasswordActivity.this);
					builder.setTitle("修改密码");
					View dialogView=View.inflate(PasswordActivity.this, R.layout.dialog_password, null);
					final EditText user=(EditText) dialogView.findViewById(R.id.et_user);
					final EditText password=(EditText) dialogView.findViewById(R.id.et_password);
					final EditText desc=(EditText) dialogView.findViewById(R.id.et_desc);
					user.setText(info.getUser());
					builder.setView(dialogView);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String userString=user.getText().toString();
							String pass=password.getText().toString();
							String descsString=desc.getText().toString();
							
							if (TextUtils.isEmpty(userString)||TextUtils.isEmpty(pass)) {
								Toast.makeText(PasswordActivity.this, "用户名或密码不能为空", 1).show();
							}else {
								PasswordInfo info=new PasswordInfo();
								info.setUser(userString);
								info.setPassword(pass);
								info.setDescription(descsString);
								databaseUtils.update(info);
								list=databaseUtils.query();
								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
				}
			});
			return convertView;
		}
		
	}
	class ViewHolder{
		TextView user;
		TextView password;
		TextView description;
		Button delete;
		Button edit;
	}
}
