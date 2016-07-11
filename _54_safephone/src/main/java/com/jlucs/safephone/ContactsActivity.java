package com.jlucs.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jlucs.safephone.tools.Contactdb;

import java.util.ArrayList;

public class ContactsActivity extends Activity {

    private ListView listView;
    private ArrayList<Info> res;
    private Contactdb helper;
    private SQLiteDatabase db;
    private String sql;
    private Listadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listView = (ListView) findViewById(R.id.lv_safe_contact);
        res = new ArrayList<Info>();
        helper = new Contactdb(ContactsActivity.this, "contacts", null, 1);
        db = helper.getWritableDatabase();
        sql = "select * from contacts";
        init();
    }

    public void init() {
        Cursor cs = db.rawQuery(sql, null);

        while (cs.moveToNext()) {
            String name1 = cs.getString(cs.getColumnIndex("name"));
            String phone = cs.getString(cs.getColumnIndex("phone"));
            Info info = new Info(name1, phone);
            res.add(info);
        }
        adapter = new Listadapter(res);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {
                final Info info = (Info) listView.getItemAtPosition(position);

//				dialog.show();
                String[] items = new String[]{"查看信息", "删除", "取消"};
                new AlertDialog.Builder(ContactsActivity.this).setTitle("关于此联系人")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        // 复制号码到拨号盘
                                        break;
                                    case 1:
                                        sql = "delete from contacts where name = '" + info.name + "'and phone = '" + info.phone + "'";
                                        db.execSQL(sql);
                                        res.remove(position);
                                        adapter.notifyDataSetChanged();

                                        break;
                                    case 2:
                                        // 发送短信
                                        break;

                                    default:
                                        break;
                                }

                            }
                        }).show();
            }
        });
    }

    public void refreshContacts(View view){
        refreshUI();
    }
    public void finishContacts(View view){
        finish();
    }
    public void addContact(View view){
        startActivityForResult(new Intent(this,ContactsAdds.class),1);
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
            String phone = cs.getString(cs.getColumnIndex("phone"));
            Info info = new Info(name1, phone);
            res.add(info);
        }
        adapter.notifyDataSetChanged();
    }

    public class Info {
        String name;
        String phone;

        public Info(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    public class Listadapter extends BaseAdapter {
        ArrayList<Info> item = new ArrayList<Info>();

        Listadapter(ArrayList<Info> item) {
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
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.contact_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_contact_people);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.tv_contact_number);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(item.get(position).name);
            viewHolder.phone.setText(item.get(position).phone);

            return convertView;
        }


        class ViewHolder {
            public TextView name;
            public TextView phone;
        }

    }
}
