package com.jlucs.safephone.tools;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class Smsdb extends SQLiteOpenHelper{

	Context mContext;
	String db_name;
	public Smsdb(Context context, String name, CursorFactory factory,
				 int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.db_name = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table '"+db_name+"'(_id integer primary key autoincrement, name text, date text, smsbody text, smstype text,number text)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
