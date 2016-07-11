package com.jlucs.safephone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.jlucs.safephone.tools.Recorddb;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordAdds extends Activity {

    private Recorddb mHelper;
    private SQLiteDatabase database;
    private String sql;
    private ListView lv;
    private Listadapter readd_adapter;
    ArrayList<CallInfo> call_container;
    ArrayList<String> call_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        call_date = new ArrayList<String>();
        call_container = GetCall();

        readd_adapter = new Listadapter(call_container);
        lv = (ListView) findViewById(R.id.lv_record_add);
        lv.setAdapter(readd_adapter);

        mHelper = new Recorddb(this,"call_record",null,1);
        database = mHelper.getWritableDatabase();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                CallInfo info = (CallInfo) lv.getItemAtPosition(position);
                dialog(info, position);
            }
        });

    }

    protected void dialog(final CallInfo info, final int position) {
        Builder builder = new Builder(RecordAdds.this);
        builder.setMessage("确认添加选择记录？");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                sql = "insert into call_record (name,data,duration)values('" + info.name
//                        + "', '" + info.date + "', '" + info.duration + "')";
//                database.execSQL(sql);

                ContentValues values=new ContentValues();
                values.put("name",info.name);
                values.put("data",info.date);
                values.put("duration",info.duration);
                values.put("number",info.number);
                database.insert("call_record",null,values);

                call_container.remove(position);
                String tmp = call_date.get(position);
                call_date.remove(position);
                readd_adapter.notifyDataSetChanged();

                try {
                    System.out.println("HELLO" + tmp);
                    Delete(tmp);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
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

    public class CallInfo {
        public String name;
        public String date;
        public String duration;
        public String number;

        public CallInfo(String name, String date, String duration,String number) {
            super();
            this.name = name;
            this.date = date;
            this.duration = duration;
            this.number=number;
        }


    }

    public class Listadapter extends BaseAdapter {
        ArrayList<CallInfo> item = new ArrayList<CallInfo>();

        Listadapter(ArrayList<CallInfo> item) {
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
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.record_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_record_name);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_record_time);
                viewHolder.duration = (TextView) convertView.findViewById(R.id.tv_record_duration);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(item.get(position).name);
            viewHolder.time.setText(item.get(position).date);
            viewHolder.duration.setText(item.get(position).duration);

            return convertView;
        }


        class ViewHolder {
            public TextView name;
            public TextView time;
            public TextView duration;
        }

    }

    public ArrayList<CallInfo> GetCall() {
        ArrayList<CallInfo> ls = new ArrayList<CallInfo>();
        Cursor cursor = RecordAdds.this.getContentResolver().query(Calls.CONTENT_URI, null, null, null, null);
        String name;
        String time;
        String duration;
        String number;

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));//�绰����
            if (name == null)
                name = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
            call_date.add(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE)));

            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE))));
            //����ʱ��                                                                                           
            time = sfd.format(date);
            // time = cursor.getString(cursor.getColumnIndex(Calls.DATE));//����
            duration = cursor.getString(cursor.getColumnIndex(Calls.DURATION)) + "s";
            number=cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
            CallInfo info = new CallInfo(name, time, duration,number);
            ls.add(info);
        }

        return ls;
    }

    public void Delete(String name) throws Exception {
        //����������id  
        //Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");  
        ContentResolver resolver = RecordAdds.this.getContentResolver();
        resolver.delete(Calls.CONTENT_URI, "date = ?", new String[]{name});
        System.out.println("jack");

//        Cursor cursor = resolver.query(Calls.CONTENT_URI, new String[]{Data._ID},"date=?", new String[]{name}, null);  
//        if(cursor.moveToFirst()){  
//            int id = cursor.getInt(0);  
//            //����idɾ��data�е���Ӧ����  
//            resolver.delete(Calls.CONTENT_URI, "_id=?", new String[] {id + ""}); 
//        }  
    }
}
