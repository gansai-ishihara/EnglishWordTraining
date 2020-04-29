package com.example.tsuyoshi.englishwordtraining;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Locale;

/**
 * Created by tsuyoshi on 2018/03/11.
 */

public class AddCategory extends AppCompatActivity implements View.OnClickListener {
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語リスト","単語登録","単語テスト","カテゴリ編集","使い方","設定","お問い合わせ"};

    private EditText input_category;
    private Button button_register;
    protected SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);

        //戻るボタンを設置
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        intent.setClass(AddCategory.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(AddCategory.this, TestSample.class);
                        break;
                    case 2:
                        intent.setClass(AddCategory.this, WordRegist.class);
                        break;
                    case 3:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(AddCategory.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(AddCategory.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 4:
                        intent.setClass(AddCategory.this, EditCategory.class);
                        break;
                    case 5:
                        intent.setClass(AddCategory.this, HowToUse.class);
                        break;
                    case 6:
                        intent.setClass(AddCategory.this, SettingsActivity.class);
                        break;
                    case 7:
                        intent.setClass(AddCategory.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //タイトルを白字にする
        String title = "カテゴリ登録";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));

        button_register=(Button)findViewById(R.id.register_category);
        button_register.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v==button_register){
            //データベース取得
            db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
            ContentValues cv = new ContentValues();

            //入力取得
            input_category = (EditText)findViewById(R.id.category_input);
            String category = input_category.getText().toString();

            //何も入力されてなかったらダイアログを表示
            if(category.equals("")){
                new AlertDialog.Builder(this)
                        .setTitle("警告")
                        .setMessage("カテゴリ名を入力してください。")
                        .setPositiveButton("OK", null)
                        .show();
            }else{
                //データベースに登録
                cv.put("Field",category);
                db.insert(DatabaseOpenHelper2.CATEGORY_NAME, null, cv);
                cv.clear();

                //元の画面に戻る
                finish();
            }
        }
    }
}