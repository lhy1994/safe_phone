package com.jlucs.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.jlucs.safephone.tools.Recorddb;

import java.util.ArrayList;

public class RecordActivity extends Activity {

    private SQLiteDatabase db;
    private String sql;
    private ListView lv;
    private Listadapter adapter;
    private ArrayList<CallInfo> res;
    private Recorddb helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        lv = (ListView) findViewById(R.id.lv_record);
        res = new ArrayList<CallInfo>();
        helper = new Recorddb(this,"call_record",null,1);
        db=helper.getWritableDatabase();
        sql = "select * from call_record";

        init();

    }

    private void init() {
        Cursor cs = db.rawQuery(sql, null);

        while (cs.moveToNext()) {
            String name1 = cs.getString(cs.getColumnIndex("name"));
            String data = cs.getString(cs.getColumnIndex("data"));
            String duration = cs.getString(cs.getColumnIndex("duration"));
            String number=cs.getString(cs.getColumnIndex("number"));
            CallInfo info = new CallInfo(name1, data, duration,number);
            res.add(info);
        }
        adapter = new Listadapter(res);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {
                final CallInfo info = (CallInfo) lv.getItemAtPosition(position);

//				dialog.show();
                String[] items = new String[]{"拨号","删除", "取消"};
                new AlertDialog.Builder(RecordActivity.this).setTitle("关于此记录")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        Intent callIntent=new Intent();
                                        callIntent.setAction(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:"+info.number));
                                        startActivity(callIntent);
                                        break;
                                    case 1:
                                        sql = "delete from call_record where name = '" + info.name + "'and data = '" + info.date + "'and duration = '" + info.duration + "'";
                                        db.execSQL(sql);
                                        res.remove(position);
                                        adapter.notifyDataSetChanged();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }

    public void refreshRecords(View view){
        refreshUI();
    }
    public void finishRecords(View view){
        finish();
    }
    public void addRecord(View view){
        startActivityForResult(new Intent(this,RecordAdds.class),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            refreshUI();
        }
    }

    private void refreshUI() {
        res.clear();

        Cursor cs = db.rawQuery(sql, null);

        while (cs.moveToNext()) {
            String name1 = cs.getString(cs.getColumnIndex("name"));
            String data = cs.getString(cs.getColumnIndex("data"));
            String duration = cs.getString(cs.getColumnIndex("duration"));
            String number=cs.getString(cs.getColumnIndex("number"));
            CallInfo info = new CallInfo(name1, data, duration,number);
            res.add(info);
        }
        for (CallInfo callInfo: res){
            Log.e("tet",callInfo.toString());
        }
        adapter.notifyDataSetChanged();
    }

    public class CallInfo {
        public String name;
        public String date;
        public String duration;
        public String number;

        @Override
        public String toString() {
            return "CallInfo{" +
                    "name='" + name + '\'' +
                    ", date='" + date + '\'' +
                    ", duration='" + duration + '\'' +
                    ", number='" + number + '\'' +
                    '}';
        }

        public CallInfo(String name, String date, String duration, String number) {
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
}
