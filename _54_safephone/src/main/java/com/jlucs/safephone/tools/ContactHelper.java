package com.jlucs.safephone.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liuhaoyuan on 2016/7/9.
 */
public class ContactHelper extends SQLiteOpenHelper{
    Context mContext;
    //	String dbname1;
//	String dbname2;
    public ContactHelper(Context context, SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context,"contact", factory, version);
        // TODO Auto-generated constructor stub
        this.mContext = context;
//		this.dbname1 = dbname1;
//		this.dbname2 = dbname2;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table contact (_id integer primary key autoincrement, name_id text, name text, contact_times text, last_contact text)");
        Log.e("test", "...............................");
        db.execSQL("create table contact_data (_id integer primary key autoincrement,  mimetype text,  name_id text, data text)");
        Log.e("lll", "...............................");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
