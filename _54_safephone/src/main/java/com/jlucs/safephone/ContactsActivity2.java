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

import com.jlucs.safephone.tools.ContactHelper;
import com.jlucs.safephone.tools.Contactdb;

import java.util.ArrayList;

public class ContactsActivity2 extends Activity {

    ContactHelper helper;
    SQLiteDatabase db;
    String sql;
    ListView lv;
    ArrayList<Contacts> container;
    private Listadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        lv = (ListView)findViewById(R.id.lv_safe_contact);
        container = new ArrayList<Contacts>();
        helper = new ContactHelper(this,null,1);
        db = helper.getWritableDatabase();
        sql = "select * from contact";

        init();
    }

    public void init() {

        Cursor cs = db.rawQuery(sql, null);

        while(cs.moveToNext()){
            String name = cs.getString(cs.getColumnIndex("name"));
            String name_id = cs.getString(cs.getColumnIndex("name_id"));
            String contact_times = cs.getString(cs.getColumnIndex("contact_times"));
            String last_contact = cs.getString(cs.getColumnIndex("last_contact"));
            Contacts info = new Contacts( name_id, name,contact_times, last_contact);
            container.add(info);
        }
        adapter = new Listadapter(container);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {
//                final String name = (String) lv.getItemAtPosition(position);

//				dialog.show();
                String[] items = new String[] { "查看信息", "删除","取消" };
                new AlertDialog.Builder(ContactsActivity2.this).setTitle("关于此联系人")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(
                                                ContactsActivity2.this, ContactDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", container.get(position).name_id);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                        break;
                                    case 1:
                                        sql = "delete from contact where name_id = '"+container.get(position).name_id+"'";
                                        db.execSQL(sql);
                                        sql = "delete from contact_data where name_id = '"+container.get(position).name_id+"'";
                                        db.execSQL(sql);

                                        container.remove(position);
                                        adapter.notifyDataSetChanged();

                                        break;
                                    case 2:
                                        // ���Ͷ���
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
        container.clear();
        Cursor cs = db.rawQuery(sql, null);

        while(cs.moveToNext()){
            String name = cs.getString(cs.getColumnIndex("name"));
            String name_id = cs.getString(cs.getColumnIndex("name_id"));
            String contact_times = cs.getString(cs.getColumnIndex("contact_times"));
            String last_contact = cs.getString(cs.getColumnIndex("last_contact"));
            Contacts info = new Contacts( name_id, name,contact_times, last_contact);
            container.add(info);
        }
        adapter.notifyDataSetChanged();
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
}
