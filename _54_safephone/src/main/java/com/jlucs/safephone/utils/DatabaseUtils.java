package com.jlucs.safephone.utils;

import java.util.ArrayList;

import com.jlucs.safephone.bean.PasswordInfo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseUtils {
	private Context context;
	private MyOpenHelper helper;
	private SQLiteDatabase db;
	public DatabaseUtils(Context context) {
		this.context=context;
		helper = new MyOpenHelper(context, "password_manage", null, 1);
		db = helper.getWritableDatabase();
	}
	@SuppressLint("NewApi") public ArrayList<PasswordInfo> query(){
		ArrayList<PasswordInfo> list=new ArrayList<PasswordInfo>();
		Cursor cursor = db.query(true, "password", null, null, null, null, null, null, null, null);
		while(cursor.moveToNext()){
			PasswordInfo passwordInfo=new PasswordInfo();
			passwordInfo.setUser(cursor.getString(cursor.getColumnIndex("user")));
			passwordInfo.setPassword(cursor.getString(cursor.getColumnIndex("password")));
			passwordInfo.setDescription(cursor.getString(cursor.getColumnIndex("description")));
			list.add(passwordInfo);
		}
		return list;
	}
	public void add(PasswordInfo info) {
		ContentValues values=new ContentValues();
		values.put("user", info.getUser());
		values.put("password", info.getPassword());
		values.put("description", info.getDescription());
		db.insert("password", null, values);
	}
	public void update(PasswordInfo info) {
		ContentValues values=new ContentValues();
		values.put("user", info.getUser());
		values.put("password", info.getPassword());
		values.put("description", info.getDescription());
		int nums = db.update("password", values, "user=?", new String[]{info.getUser()});
		if (nums<1) {
			db.insert("password", null, values);
		}
	}
	public void delete(String user) {
		db.delete("password", "user=?", new String[]{user});
	}
	@SuppressLint("NewApi") public ArrayList<PasswordInfo> search(String user) {
		ArrayList<PasswordInfo> list=new ArrayList<PasswordInfo>();
		Cursor cursor=db.query(true, "password", null, "user like ?", new String[]{"%"+user+"%"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			PasswordInfo info=new PasswordInfo();
			info.setUser(cursor.getString(cursor.getColumnIndex("user")));
			info.setPassword(cursor.getString(cursor.getColumnIndex("password")));
			info.setDescription(cursor.getString(cursor.getColumnIndex("description")));
			list.add(info);
		}
		return list;
	}
}
