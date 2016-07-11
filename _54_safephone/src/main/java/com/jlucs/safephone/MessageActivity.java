package com.jlucs.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jlucs.safephone.tools.Smsdb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends Activity {
    private Smsdb helper;
    private SQLiteDatabase db;
    String sql;
    ListView lv;
    private Listadapter smadd_adapter;
    ArrayList<Simplesms> smsa_container;
    ArrayList<MessageInfo> res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        lv = (ListView) findViewById(R.id.lv_messages);
        res = new ArrayList<MessageInfo>();
        helper = new Smsdb(this, "sms", null, 1);
        db = helper.getWritableDatabase();
        sql = "select * from sms";

        init();
    }

    private void init() {

        Cursor cs = db.rawQuery(sql, null);
        // Cursor cs = db.query("sms", null, null, null, null, null, null);
        while (cs.moveToNext()) {
            String name = cs.getString(cs.getColumnIndex("name"));
            String time = cs.getString(cs.getColumnIndex("date"));
            String body = cs.getString(cs.getColumnIndex("smsbody"));
            String type = cs.getString(cs.getColumnIndex("smstype"));
            String number=cs.getString(cs.getColumnIndex("number"));
            MessageInfo info = new MessageInfo(name, time, body, type,number);
            res.add(info);
        }

        smsa_container = getSimple(res);
        smadd_adapter = new Listadapter(smsa_container);
        lv.setAdapter(smadd_adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {
                final Simplesms info = (Simplesms) lv
                        .getItemAtPosition(position);

                // dialog.show();
                String[] items = new String[]{"查看全文", "删除", "取消"};
                new AlertDialog.Builder(MessageActivity.this).setTitle("关于此记录")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(
                                                MessageActivity.this, MessageView.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("content",
                                                res.get(position).smsbody);
                                        bundle.putString("addr", smsa_container.get(position).name);
                                        bundle.putString("date", smsa_container.get(position).date);
                                        bundle.putString("type", res.get(position).type);
                                        bundle.putString("number",res.get(position).phoneNumber);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        sql = "delete from sms where date = '"
                                                + res.get(position).date + "'";
                                        db.execSQL(sql);
                                        res.remove(position);
                                        smsa_container.remove(position);
                                        smadd_adapter.notifyDataSetChanged();
                                        break;

                                    default:
                                        break;
                                }

                            }
                        }).show();
            }
        });
    }

    public void refreshMessage(View view){
        refreshUI();
    }
    public void finishMessage(View view){
        finish();
    }
    public void addMessage(View view){
        startActivityForResult(new Intent(this,MessageAdds.class),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            refreshUI();
        }
    }

    private void refreshUI() {

        res.clear();
        smsa_container.clear();

//        Cursor cs = db.rawQuery(sql, null);
         Cursor cs = db.query("sms", null, null, null, null, null, null);
        while (cs.moveToNext()) {
            String name = cs.getString(cs.getColumnIndex("name"));
            String time = cs.getString(cs.getColumnIndex("date"));
            String body = cs.getString(cs.getColumnIndex("smsbody"));
            String type = cs.getString(cs.getColumnIndex("smstype"));
            String number=cs.getString(cs.getColumnIndex("number"));
            MessageInfo info = new MessageInfo(name, time, body, type,number);
            Log.e("test",info.toString());
            res.add(info);
        }
        cs.close();
        smsa_container = getSimple(res);
        for (Simplesms s:smsa_container ){
            Log.e("test",s.toString());
        }
        smadd_adapter=new Listadapter(smsa_container);
//        smadd_adapter.notifyDataSetChanged();
        lv.setAdapter(smadd_adapter);
    }

    public class Listadapter extends BaseAdapter {
        ArrayList<Simplesms> item = new ArrayList<Simplesms>();

        Listadapter(ArrayList<Simplesms> item) {
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
                        R.layout.message_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.tv_message_name);
                viewHolder.time = (TextView) convertView
                        .findViewById(R.id.tv_message_date);
                viewHolder.body = (TextView) convertView
                        .findViewById(R.id.tv_message_body);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(item.get(position).name);
            viewHolder.time.setText(item.get(position).date);
            viewHolder.body.setText(item.get(position).body);

            return convertView;
        }

        class ViewHolder {
            public TextView name;
            public TextView time;
            public TextView body;
        }
    }

    public class MessageInfo {

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSmsbody() {
            return smsbody;
        }

        public void setSmsbody(String smsbody) {
            this.smsbody = smsbody;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        String name;
        String smsbody;
        String date;
        String phoneNumber;

        @Override
        public String toString() {
            return "MessageInfo{" +
                    "name='" + name + '\'' +
                    ", smsbody='" + smsbody + '\'' +
                    ", date='" + date + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        String type;

        public MessageInfo(String name, String data, String smsbody, String type,String phoneNumber) {
            this.name = name;
            this.date = data;
            this.smsbody = smsbody;
            this.type = type;
            this.phoneNumber=phoneNumber;
        }
    }

    public ArrayList<Simplesms> getSimple(ArrayList<MessageInfo> in) {
        ArrayList<Simplesms> ls = new ArrayList<Simplesms>();
        ArrayList<MessageInfo> mi = in;

        for (MessageInfo i : mi) {
            String name = i.getName();
            int length = i.getSmsbody().length();
            String body;
            if (length < 15) {
                body = i.getSmsbody();
            } else
                body = i.getSmsbody().substring(0, 15) + "...";

            String date = i.getDate();
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = new Date(Long.parseLong(date));
            // 呼叫时间
            String time = sfd.format(date1);
            System.out.println(time);
            Simplesms tmp = new Simplesms(name, time, body);
            ls.add(tmp);
        }

        return ls;

    }

    public class Simplesms {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        String date;
        String body;

        @Override
        public String toString() {
            return "Simplesms{" +
                    "name='" + name + '\'' +
                    ", date='" + date + '\'' +
                    ", body='" + body + '\'' +
                    '}';
        }

        public Simplesms(String name, String date, String body) {
            this.name = name;
            this.date = date;
            this.body = body;
        }

    }
}
