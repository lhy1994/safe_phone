package com.jlucs.safephone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jlucs.safephone.tools.ContactHelper;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/7/9.
 */
public class ContactDetailActivity extends Activity {

    ContactHelper helper;
    SQLiteDatabase db;
    String sql;

    private String name;
    private ArrayList<String> phone;
    private String email;
    private String address;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_detail);

        helper = new ContactHelper(this,  null, 1);
        db = helper.getWritableDatabase();
        phone=new ArrayList<>();

        nameTextView = (TextView) findViewById(R.id.tv_contact_detail_name);
        phoneTextView = (TextView) findViewById(R.id.tv_contact_detail_number);
        emailTextView = (TextView) findViewById(R.id.tv_contact_detail_email);
        addressTextView = (TextView) findViewById(R.id.tv_contact_detail_address);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");

        sql = "select * from contact_data where name_id = '"+id+"'";
        Cursor cur = db.rawQuery(sql, null);
        while(cur.moveToNext())
        {
            String mime = cur.getString(cur.getColumnIndex("mimetype"));
            String data = cur.getString(cur.getColumnIndex("data"));

            if (mime.equals("vnd.android.cursor.item/email_v2"))
                email=data;
            else if(mime.equals("vnd.android.cursor.item/phone_v2"))
                phone.add(data);
            else if(mime.equals("vnd.android.cursor.item/postal-address_v2"))
                address=data;
            else if(mime.equals("vnd.android.cursor.item/name"))
                name=data;

            Log.e("ee","mime "+mime+"  data "+data+"..........................");
        }

        if (!TextUtils.isEmpty(name)){
            nameTextView.setText(name);
        }
        if (!TextUtils.isEmpty(address)){
            addressTextView.setText(address);
        }
        if (!TextUtils.isEmpty(email)){
            emailTextView.setText(email);
        }
        StringBuilder phoneList=new StringBuilder();
        for (String s:phone){
            if (!TextUtils.isEmpty(s)){
                phoneList.append(s+"\n");
            }
        }
        if (!TextUtils.isEmpty(phoneList.toString())){
            phoneTextView.setText(phoneList.toString());
        }
    }

    public void finishContactDetail(View view){
        finish();
    }
    public void call(View view){
        Intent callIntent=new Intent();
        callIntent.setAction(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phone.get(0)));
        startActivity(callIntent);
    }
}