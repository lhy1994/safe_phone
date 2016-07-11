package com.jlucs.safephone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.jlucs.safephone.tools.Smsdb;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Telephony.Sms;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.KITKAT) public class MessageAdds extends Activity {

	String sql;
	ListView lv;
	private Listadapter smadd_adapter;
	ArrayList<Simplesms> sms_container;
	ArrayList<MessageInfo> full_info;
	private Smsdb helper;
	private SQLiteDatabase db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_message);
		lv = (ListView) findViewById(R.id.lv_message_add);
		sms_container = getSimplesms();
		smadd_adapter = new Listadapter(sms_container);
		lv.setAdapter(smadd_adapter);
		full_info = getSmsInfos();

		helper = new Smsdb(this, "sms", null, 1);
		db = helper.getWritableDatabase();

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Simplesms info = (Simplesms) lv.getItemAtPosition(position);
				dialog(info, position);
			}
		});

	}

	protected void dialog(final Simplesms info, final int position) {
		Builder builder = new Builder(MessageAdds.this);
		builder.setMessage("确认添加选中短信？");

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MessageInfo tmp = full_info.get(position);
//				sql = "insert into sms (name, date, smsbody, smstype,number)values('"
//						+ tmp.name + "', '" + tmp.date + "', '"
//						+ tmp.smsbody.replace("'", "") + "', '" + tmp.type+",number"
//						+ "')";
//				db.execSQL(sql);

				ContentValues valuse=new ContentValues();
				valuse.put("name",tmp.name);
				valuse.put("date",tmp.date);
				valuse.put("smsbody",tmp.smsbody);
				valuse.put("smstype",tmp.type);
				valuse.put("number",tmp.phoneNumber);
				db.insert("sms",null,valuse);

				String date = full_info.get(position).date;
				sms_container.remove(position);
				full_info.remove(position);
				
				smadd_adapter.notifyDataSetChanged();

				try {
					Delete(date);
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

		public Simplesms(String name, String date, String body) {
			this.name = name;
			this.date = date;
			this.body = body;
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
		String type;
		String phoneNumber;

		public MessageInfo(String name, String data, String smsbody, String type,String phoneNumber) {
			this.name = name;
			this.date = data;
			this.smsbody = smsbody;
			this.type = type;
			this.phoneNumber=phoneNumber;
		}
		// public MessageInfo(int nameColumn,int phoneNumberColumn, int
		// smsbodyColumn ,int dateColumn, int typeColumn,String nameId,String
		// phoneNumber,String smsbody,String date)
		// {
		// this.nameColumn = nameColumn;
		// this.phoneNumberColumn = phoneNumberColumn;
		// this.smsbodyColumn = smsbodyColumn;
		// this.dateColumn = dateColumn;
		// this.typeColumn = typeColumn;
		// this.nameId = nameId;
		// this.phoneNumber = phoneNumber;
		// this.smsbody = smsbody;
		// this.date = date;
		//
		//
		// }
	}

	public ArrayList<Simplesms> getSimplesms() {
		ArrayList<Simplesms> ls = new ArrayList<Simplesms>();
		ArrayList<MessageInfo> mi = getSmsInfos();

		for (MessageInfo i : mi) {
			String name = i.getName();
			int length = i.getSmsbody().length();
			String body;
			if (length<15)
			{
				body = i.getSmsbody();
			}
			else
				body = i.getSmsbody().substring(0, 15)+"...";
			String date = i.getDate();
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = new Date(Long.parseLong(date));
			// ����ʱ��
			String time = sfd.format(date1);
			Simplesms tmp = new Simplesms(name, time, body);
			ls.add(tmp);
		}

		return ls;

	}

	public ArrayList<MessageInfo> getSmsInfos() {
		MessageInfo messageInfo;
		ArrayList<MessageInfo> list = new ArrayList<MessageInfo>();
		final String SMS_URI = "content://sms";// ������
		try {
			ContentResolver cr = MessageAdds.this.getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI);
			Cursor cursor = cr.query(uri, projection, null, null, "date desc");
			while (cursor.moveToNext()) {

				int nameColumn = cursor.getColumnIndex("person");
				int phoneNumberColumn = cursor.getColumnIndex("address");// �ֻ���
				int smsbodyColumn = cursor.getColumnIndex("body");// ��������
				int dateColumn = cursor.getColumnIndex("date");// ����
				int typeColumn = cursor.getColumnIndex("type");// �շ����� 1��ʾ����
																// 2��ʾ����
				String name = cursor.getString(nameColumn);
				String phoneNumber = cursor.getString(phoneNumberColumn);
				String smsbody = cursor.getString(smsbodyColumn);
				String date = cursor.getString(dateColumn);
				String type = cursor.getString(typeColumn);
				String tmp;
				Uri personUri = Uri.withAppendedPath(
						PhoneLookup.CONTENT_FILTER_URI,
						phoneNumber);
				Cursor localCursor = cr.query(personUri, new String[] {
						PhoneLookup.DISPLAY_NAME, PhoneLookup.PHOTO_ID,
						PhoneLookup._ID }, null, null, null);

				if (localCursor.getCount() != 0) {
					localCursor.moveToFirst();
					String name_tmp = localCursor.getString(localCursor
							.getColumnIndex(PhoneLookup.DISPLAY_NAME));
					// messageInfo.setName(name_tmp);
					tmp = name_tmp;
				} else {
					// messageInfo.setName(phoneNumber);
					tmp = phoneNumber;
				}

				localCursor.close();
				messageInfo = new MessageInfo(tmp, date, smsbody, type,phoneNumber);
				list.add(messageInfo);
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void Delete(String name)throws Exception{    
        //����������id  
        //Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");  
        ContentResolver resolver = MessageAdds.this.getContentResolver();  
        resolver.delete(Sms.CONTENT_URI, "date = ?", new String[]{name} );
        
//        Cursor cursor = resolver.query(Calls.CONTENT_URI, new String[]{Data._ID},"date=?", new String[]{name}, null);  
//        if(cursor.moveToFirst()){  
//            int id = cursor.getInt(0);  
//            //����idɾ��data�е���Ӧ����  
//            resolver.delete(Calls.CONTENT_URI, "_id=?", new String[] {id + ""}); 
//        }  
    }
}



