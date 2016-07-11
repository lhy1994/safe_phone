package com.jlucs.safephone;

import com.jlucs.safephone.utils.MD5Utils;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private GridView gridView;
    private String[] names;
    private int[] ids;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv_main);
        names = new String[]{"隐私联系人", "隐私短信", "通话记录", "密码保险箱", "登录密码修改",
                "手机防盗", "归属地查询"};
        ids = new int[]{R.drawable.user,
                R.drawable.message,
                R.drawable.telepon,
                R.drawable.lock2,
                R.drawable.repair, R.drawable.shield,
                R.drawable.map};
        preferences = getSharedPreferences("password", MODE_PRIVATE);
        init();
    }

    private void init() {
        gridView.setAdapter(new GridAdapter());
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this,
                                ContactsActivity2.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,
                                MessageActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this,
                                RecordActivity.class));
                        break;
                    case 3:
                        AlertDialog.Builder builder = new Builder(MainActivity.this);
                        builder.setTitle("请输入密码");

                        final EditText editText = new EditText(MainActivity.this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        editText.setHint("这里输入密码");
                        builder.setView(editText);
                        builder.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String pass = editText.getText().toString();
                                        if (TextUtils.equals(MD5Utils.encode(pass),
                                                preferences.getString("password",
                                                        "202cb962ac59075b964b07152d234b70"))) {
                                            startActivity(new Intent(
                                                    MainActivity.this,
                                                    PasswordActivity.class));
                                        } else {
                                            Toast.makeText(MainActivity.this,
                                                    "密码错误", 1).show();
                                        }
                                    }
                                });
                        builder.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                        break;
                    case 4:
                        final SharedPreferences sharedPreferences = getSharedPreferences(
                                "login", MODE_PRIVATE);
                        AlertDialog.Builder builder2 = new Builder(
                                MainActivity.this);
                        builder2.setTitle("登录密码修改");
                        final EditText editText2 = new EditText(MainActivity.this);
                        editText2
                                .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        editText2.setHint("这里输入密码");
                        builder2.setView(editText2);
                        builder2.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            final DialogInterface dialog, int which) {
                                        String pass = editText2.getText()
                                                .toString();
                                        if (TextUtils.equals(MD5Utils.encode(pass),
                                                sharedPreferences.getString(
                                                        "password", "202cb962ac59075b964b07152d234b70"))) {
                                            AlertDialog.Builder builder3 = new Builder(
                                                    MainActivity.this);

                                            builder3.setTitle("确定新密码");
                                            View view = View
                                                    .inflate(
                                                            MainActivity.this,
                                                            R.layout.dialog_update_password,
                                                            null);
                                            Button button = (Button) view
                                                    .findViewById(R.id.btn_updatePwd);
                                            final EditText firstEditText = (EditText) view
                                                    .findViewById(R.id.et_firstPwd);
                                            final EditText secondEditText = (EditText) view
                                                    .findViewById(R.id.et_secondPwd);
                                            final TextView textView = (TextView) view
                                                    .findViewById(R.id.tv_updatePwd);
                                            firstEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                            secondEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                            builder3.setView(view);

                                            final AlertDialog alertDialog = builder3
                                                    .create();
                                            button.setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    String first = firstEditText
                                                            .getText().toString();
                                                    String second = secondEditText
                                                            .getText().toString();
                                                    if (TextUtils.isEmpty(first)
                                                            || TextUtils
                                                            .isEmpty(second)) {
                                                        textView.setVisibility(View.VISIBLE);
                                                        textView.setText("密码不能为空");
                                                    } else {
                                                        if (TextUtils.equals(first,
                                                                second)) {
                                                            sharedPreferences
                                                                    .edit()
                                                                    .putString(
                                                                            "password",
                                                                            MD5Utils.encode(first))
                                                                    .commit();
                                                            alertDialog.dismiss();
                                                            Toast.makeText(
                                                                    MainActivity.this,
                                                                    "登录密码修改成功", 0)
                                                                    .show();
                                                        } else {
                                                            textView.setVisibility(View.VISIBLE);
                                                            textView.setText("密码不一致");
                                                        }
                                                    }
                                                }
                                            });

                                            alertDialog.show();
                                        } else {
                                            Toast.makeText(MainActivity.this,
                                                    "密码错误", 0).show();
                                        }
                                    }
                                });
                        builder2.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder2.show();
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this,
                                FindActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(MainActivity.this,
                                SearchActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.gridlist_item,
                    null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_main);
            TextView textView = (TextView) view.findViewById(R.id.tv_main);
            imageView.setImageResource(ids[position]);
            textView.setText(names[position]);
            return view;
        }

    }
}
