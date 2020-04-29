package com.example.tsuyoshi.englishwordtraining;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    /** Called when the activity is first created. */

    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"カテゴリ追加","カテゴリ編集","使い方","設定","お問い合わせ"};

    private Button button_regist;
    private Button button_test;
    private Button button_list;

    protected SQLiteDatabase db;
    protected Cursor cursor;
    private String selection;

    protected ContentValues cv;

    protected int petNameIndex;
    protected int word_skip;
    protected int date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //NavigationDrawer
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                // getSupportActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                // getSupportActionBar().setTitle(mDrawerTitle);
            }
        };
        drawer.addDrawerListener(toggle);

        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.drawer_itmelist);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,foods);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String strData = arrayAdapter.getItem(position);

                Intent intent = new Intent();

                switch (position) {
                    case 0:
                        intent.setClass(MainActivity.this, AddCategory.class);
                        break;
                    case 1:
                        intent.setClass(MainActivity.this, EditCategory.class);
                        break;
                    case 2:
                        intent.setClass(MainActivity.this, HowToUse.class);
                        break;
                    case 3:
                        intent.setClass(MainActivity.this, SettingsActivity.class);
                        break;
                    case 4:
                        intent.setClass(MainActivity.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //タイトルを白字にする
        String title = "単トレ";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));

        db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
        cv = new ContentValues();
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.TABLE_NAME);

        //データベースから読み込み
        cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                null, null, null, null, null, null);

        date = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_DAY);
        word_skip = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_WORD_SKIP);
        petNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);

        cursor.moveToFirst();

        //日付が変わってたらtodayを全てfalseにする
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        if(!cursor.getString(date).equals(""+day)) {
            cv.put("Today", "false");
            cv.put("Day", ""+day);
            db.update(DatabaseOpenHelper2.TABLE_NAME, cv, null, null);
            cv.clear();

            for(int i=0;i<recodeCount;i++) {
                cv.put("WordSkip", String.valueOf(Integer.parseInt(cursor.getString(word_skip))-1));
                if(Integer.parseInt(cursor.getString(word_skip))-1 == 0) {
                    cv.put("Today", "true");
                }
                db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{cursor.getString(petNameIndex)});
                cv.clear();
                cursor.moveToNext();
            }

            //今日の問題数を記録
            cv.clear();
            cursor.moveToFirst();
            selection = "MemoryCount<? AND Today=? AND WordSkip<?";
            cursor=db.query(DatabaseOpenHelper2.TABLE_NAME,
                    null, selection, new String[]{"3","false","1"}, null, null, DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT + " asc");
            cursor.moveToFirst();

            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor e = pref.edit();
            e.putInt("ok", cursor.getCount());
            e.putInt("tok", 0);
            e.putInt("tno", 0);
            e.commit();
        }

        cv.clear();
        cursor.moveToFirst();

        selection = "MemoryCount<? AND Today=? AND WordSkip<?";
        cursor=db.query(DatabaseOpenHelper2.TABLE_NAME,
                null, selection, new String[]{"3","false","1"}, null, null, DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT + " asc");
        cursor.moveToFirst();

        TextView varTextView = (TextView) findViewById(R.id.textView5);
        varTextView.setText("今日の問題数："+cursor.getCount()+"問");

        TextView varTextView3 = (TextView) findViewById(R.id.textView7);
        varTextView3.setText("残りの全問題数："+recodeCount+"問");

        TextView varTextView2 = (TextView) findViewById(R.id.textView6);

        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        varTextView2.setText("覚えた総単語数："+pref.getInt("clear",0)+"個");

        button_regist=(Button)findViewById(R.id.button_regist);
        button_regist.setOnClickListener(this);

        button_test=(Button)findViewById(R.id.button_test);
        button_test.setOnClickListener(this);

        button_list=(Button)findViewById(R.id.button_list);
        button_list.setOnClickListener(this);

    }

    //ボタンクリック時の関数
    public void onClick(View v) {

        if(v==button_regist){
            Intent intent = new Intent(this, WordRegist.class);
            startActivityForResult(intent, 0);
        }

        if(v==button_test){
            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            if (pref.getBoolean("testcate",false)) {
                Intent intent = new Intent(this, SelectTest.class);
                startActivityForResult(intent, 0);
            }else{
                Intent intent = new Intent(this, WordTest.class);
                intent.putExtra("cate_name", "All");
                startActivityForResult(intent, 0);
            }
        }

        if(v==button_list){
            Intent intent = new Intent(this, TestSample.class);
            startActivityForResult(intent, 0);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
