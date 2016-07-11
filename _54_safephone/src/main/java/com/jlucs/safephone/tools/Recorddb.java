package com.jlucs.safephone.tools;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class Recorddb extends SQLiteOpenHelper{

	Context mContext;
	String db_name;
	public Recorddb(Context context, String name, CursorFactory factory,
					int version) {
		super(context, name, factory, version);
		this.mContext = context;
		this.db_name = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("re","...........................................");
		db.execSQL("create table call_record(id integer primary key autoincrement,name varchar(50),data varchar(50),duration varchar(50),number text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
