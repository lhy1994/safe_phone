package com.jlucs.safephone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jlucs.safephone.tools.ContactHelper;
import com.jlucs.safephone.tools.Contactdb;
import com.jlucs.safephone.tools.Recorddb;
import com.jlucs.safephone.tools.Smsdb;

public class ContactsAdds extends Activity {

    ContactHelper helper;
    SQLiteDatabase db;
    Recorddb rehelper;
    Smsdb smshelper;

    String sql;
    ListView lv;
    ArrayList<Contacts> container;
    ArrayList<Contact_data> contact_data;
    private Listadapter conadd_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        lv = (ListView) findViewById(R.id.lv_contact);


        helper = new ContactHelper(this, null, 1);
        db = helper.getWritableDatabase();



        container = GetContacts();
        conadd_adapter = new Listadapter(container);
        lv.setAdapter(conadd_adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Contacts select = (Contacts) lv.getItemAtPosition(position);
                String name = select.name;
                dialog(name, position);
            }
        });

    }

    protected void dialog(final String name, final int position) {
        AlertDialog.Builder builder = new Builder(ContactsAdds.this);
        builder.setMessage("确认添加联系人");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                helper = new ContactHelper(ContactsAdds.this,  null, 1);
                db = helper.getWritableDatabase();
                System.out.println("here");
                sql = "insert into contact (name_id,  name, contact_times, last_contact)values('"
                        + container.get(position).name_id
                        + "',  '"
                        + container.get(position).name
                        + "', '"
                        + container.get(position).contact_times
                        + "', '"
                        + container.get(position).last_contact + "')";
                db.execSQL(sql);
//                ContentValues values=new ContentValues();
//                values.put("name", "test");
//                db.insert("contact", null, values);
                System.out.println("here123");
                contact_data = GetData(container.get(position).name_id);
                for (Contact_data i : contact_data) {
                    sql = "insert into contact_data(name_id, mimetype, data)values('"
                            + i.name_id
                            + "', '"
                            + i.mimetype
                            + "', '"
                            + i.data
                            + "')";
                    db.execSQL(sql);
                    System.out.println("h321");
                }

                InsertRecord(container.get(position).name);
                InsertSms(container.get(position).name);

                container.remove(position);
                conadd_adapter.notifyDataSetChanged();

                try {
                    Delete(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public class Contacts {
        String name_id;
        String name;
        String contact_times;
        String last_contact;

        public Contacts(String name_id, String name, String contact_times,
                        String last_contact) {
            this.name_id = name_id;
            this.name = name;
            this.contact_times = contact_times;
            this.last_contact = last_contact;

        }
    }

    public class Contact_data {
        String name_id;
        String mimetype;
        String data;

        public Contact_data(String name_id, String mimetype, String data) {
            this.name_id = name_id;
            this.mimetype = mimetype;
            this.data = data;

        }
    }

    public class Listadapter extends BaseAdapter {
        ArrayList<Contacts> item = new ArrayList<Contacts>();

        Listadapter(ArrayList<Contacts> item) {
            this.item = item;
        }

        public int getCount() {
            return item.size();
        }

        @Override
        public Object getItem(int position) {
            return item.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplication()).inflate(
                        R.layout.safe_contact_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.tv_contact_name);
                viewHolder.contact_time = (TextView)convertView.findViewById(R.id.tv_contact_times);
                viewHolder.last_contact = (TextView)convertView.findViewById(R.id.tv_contact_last);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(item.get(position).name);
            viewHolder.contact_time.setText(item.get(position).contact_times);
            viewHolder.last_contact.setText(item.get(position).last_contact);

            return convertView;
        }

        class ViewHolder {
            public TextView name;
            public TextView contact_time;
            public TextView last_contact;
        }

    }

    public void Delete(String name) throws Exception {
        // 根据姓名求id
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = ContactsAdds.this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[] { Data._ID },
                "display_name=?", new String[] { name }, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            // 根据id删除data中的相应数据
            int i = resolver.delete(uri, "display_name=?", new String[]{name});
            Log.e("asda","............"+i);
            uri = Uri.parse("content://com.android.contacts/data");
            resolver.delete(uri, "raw_contact_id=?", new String[] { id + "" });
        }
    }

    public ArrayList<Contact_data> GetData(String id) {
        ArrayList<Contact_data> contact_list = new ArrayList<Contact_data>();
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentResolver resolver = ContactsAdds.this.getContentResolver();
        String name_id;
        String data;
        String mimetype;
        Cursor cursor = resolver.query(uri, null,
                "raw_contact_id=?", new String[] { id }, null);
        while (cursor.moveToNext()) {
            name_id = id;
            System.out.println(id);
            data = cursor.getString(cursor.getColumnIndex("data1"));
            mimetype = cursor.getString(cursor.getColumnIndex("mimetype"));
            Contact_data info = new Contact_data(name_id, mimetype,data );

            contact_list.add(info);
        }
        cursor.close();
        return contact_list;

    }

    public ArrayList<Contacts> GetContacts() {
        ArrayList<Contacts> ls = new ArrayList<Contacts>();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        // Uri dataUri=Uri.parse("content://com.android.contacts/data");
        ContentResolver resolver = ContactsAdds.this.getContentResolver();
        String name = "";
        String name_id = "";
        String last_contact = "";
        String contact_times = "";
        // phoneinfo = new ArrayList<PhoneInfo>();
        ArrayList<Contacts> contacts = new ArrayList<Contacts>();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        while (cursor.moveToNext()) {
            name_id = cursor.getString(cursor.getColumnIndex("contact_id"));
            name = cursor.getString(cursor.getColumnIndex("display_name"));
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String tmp = cursor.getString(cursor
                    .getColumnIndex("last_time_contacted"));
            if(tmp == null)
                last_contact = "never";
            else
            {
                Date date = new Date(Long.parseLong(tmp));
                last_contact = sfd.format(date);
            }

            contact_times = cursor.getString(cursor
                    .getColumnIndex("times_contacted"));
            Contacts info = new Contacts(name_id, name, contact_times,
                    last_contact);
            ls.add(info);
        }

        return ls;
    }

    public class MessageInfo {

        String name;
        String smsbody;
        String date;
        String type;
        String number;
        public MessageInfo(String name, String data, String smsbody, String type,String number) {
            this.name = name;
            this.date = data;
            this.smsbody = smsbody;
            this.type = type;
            this.number = number;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void InsertSms(String name) {
        ArrayList<MessageInfo> select;
        ArrayList<String>date = new ArrayList<String>();
        select = getSmsInfos();
        for (MessageInfo i : select) {
            if (i.name.equals(name)) {
                smshelper=new Smsdb(this,"sms",null,1);
                db = smshelper.getWritableDatabase();
                date.add(i.date);
                sql = "insert into sms (name, date, smsbody, smstype,number)values('"
                        + i.name + "', '" + i.date + "', '"
                        + i.smsbody.replace("'", "") + "', '" + i.type + "', '"+i.number+"')";
                db.execSQL(sql);
            }
        }
        ContentResolver resolver = ContactsAdds.this.getContentResolver();
        for(String tmp:date)
        {
            resolver.delete(Telephony.Sms.CONTENT_URI, "date = ?", new String[]{tmp} );
        }
    }

    public ArrayList<MessageInfo> getSmsInfos() {
        MessageInfo messageInfo;
        ArrayList<MessageInfo> list = new ArrayList<MessageInfo>();
        final String SMS_URI = "content://sms";// 收信箱
        try {
            ContentResolver cr = ContactsAdds.this.getContentResolver();
            String[] projection = new String[] { "_id", "address", "person",
                    "body", "date", "type" };
            Uri uri = Uri.parse(SMS_URI);
            Cursor cursor = cr.query(uri, projection, null, null, "date desc");
            while (cursor.moveToNext()) {

                int phoneNumberColumn = cursor.getColumnIndex("address");// 手机号
                int smsbodyColumn = cursor.getColumnIndex("body");// 短信内容
                int dateColumn = cursor.getColumnIndex("date");// 日期
                int typeColumn = cursor.getColumnIndex("type");// 收发类型 1表示接受
                // 2表示发送
                String phoneNumber = cursor.getString(phoneNumberColumn);
                String smsbody = cursor.getString(smsbodyColumn);
                String date = cursor.getString(dateColumn);
                String type = cursor.getString(typeColumn);
                String tmp;
                Uri personUri = Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        phoneNumber);
                Cursor localCursor = cr.query(personUri, new String[] {
                        ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_ID,
                        ContactsContract.PhoneLookup._ID }, null, null, null);

                if (localCursor.getCount() != 0) {
                    localCursor.moveToFirst();
                    String name_tmp = localCursor.getString(localCursor
                            .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    // messageInfo.setName(name_tmp);
                    tmp = name_tmp;
                } else {
                    // messageInfo.setName(phoneNumber);
                    tmp = phoneNumber;
                }
                String number = phoneNumber;
                localCursor.close();
                messageInfo = new MessageInfo(tmp, date, smsbody, type, number);
                list.add(messageInfo);
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return list;
    }




    public class CallInfo {
        public String name;
        public String date;
        public String duration;
        public String number;
        public CallInfo(String name, String date, String duration, String number) {
            super();
            this.name = name;
            this.date = date;
            this.duration = duration;
            this.number = number;
        }


    }

    public void InsertRecord(String name)
    {
        ArrayList<CallInfo> recordinfo = new ArrayList<CallInfo>();
        Cursor cursor = ContactsAdds.this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, "name = ?", new String[]{name}, null);
        String name1;
        String time;
        String duration;
        String number;
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));//电话号码
            if(name == null)
                name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))));
            time = sfd.format(date);

            duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))+"s";
            CallInfo  info = new CallInfo(name, time, duration,number);
            recordinfo.add(info);
        }
        for(CallInfo i:recordinfo)
        {
            rehelper = new Recorddb(ContactsAdds.this,"call_record",null,1);
            db = rehelper.getWritableDatabase();
            sql = "insert into call_record (name, data, duration,number)values('" + i.name
                    + "', '" + i.date + "', '"+i.duration+"', '"+i.number+"')";
            db.execSQL(sql);
        }

        ContentResolver resolver = ContactsAdds.this.getContentResolver();
        resolver.delete(CallLog.Calls.CONTENT_URI, "name = ?", new String[]{name} );
    }

}
