package com.example.tsuyoshi.englishwordtraining;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Locale;

/**
 * Created by tsuyoshi on 2018/04/22.
 */

public class SelectTest extends AppCompatActivity implements View.OnClickListener {

    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語リスト","単語登録","単語テスト","カテゴリ追加","カテゴリ編集","使い方","設定","お問い合わせ"};

    protected SQLiteDatabase db;
    protected Cursor cursor;

    protected long recodeCount;
    protected Button[] btns;
    protected Button mbt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_test);

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
                        intent.setClass(SelectTest.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(SelectTest.this, TestSample.class);
                        break;
                    case 2:
                        intent.setClass(SelectTest.this, WordRegist.class);
                        break;
                    case 3:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(SelectTest.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(SelectTest.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 4:
                        intent.setClass(SelectTest.this, AddCategory.class);
                        break;
                    case 5:
                        intent.setClass(SelectTest.this, EditCategory.class);
                        break;
                    case 6:
                        intent.setClass(SelectTest.this, HowToUse.class);
                        break;
                    case 7:
                        intent.setClass(SelectTest.this, SettingsActivity.class);
                        break;
                    case 8:
                        intent.setClass(SelectTest.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //データベース取得
        db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
        recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.CATEGORY_NAME);

        //データベースから読み込み
        cursor = db.query(DatabaseOpenHelper2.CATEGORY_NAME,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        final int petNameIndex8 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);

        //ボタン生成
        ConstraintLayout cst = (ConstraintLayout) findViewById(R.id.layout);
        btns = new Button[(int)recodeCount];
        for (int i=0;i<(int)recodeCount;i++){
            btns[i] = new Button(this);
            btns[i].setLayoutParams(new ConstraintLayout.LayoutParams(450, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            btns[i].setId(i);
            if (i%2==1){
                btns[i].setTranslationX(540); //奇数なら横にずらす
                btns[i].setTranslationY(20+150+((i-1)/2)*150); //ex.0,150,300
            }else{
                btns[i].setTranslationX(40);
                btns[i].setTranslationY(20+150+(i/2)*150); //ex.0,150,300
            }
            btns[i].setText(cursor.getString(petNameIndex8));
            btns[i].setOnClickListener(this);
            cst.addView(btns[i]);
            if(!cursor.isLast()){
                cursor.moveToNext();
            }
        }

        //Allボタンの生成
        mbt = new Button(this);
        mbt.setLayoutParams(new ConstraintLayout.LayoutParams(950, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        mbt.setId((int)recodeCount+1);
        mbt.setTranslationX(40);
        mbt.setTranslationY(20);
        mbt.setText("All");
        mbt.setOnClickListener(this);
        cst.addView(mbt);


        //タイトルを白字にする
        String title = "テスト選択";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor + "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));
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

    @Override
    public void onClick(View v) {
        for (int i=0;i<(int)recodeCount;i++) {
            if (v==btns[i]) {
                Intent intent = new Intent(getApplicationContext(), WordTest.class);
                intent.putExtra("cate_name", btns[i].getText());
                startActivity(intent);
            }
        }

        if (v==mbt) {
            Intent intent = new Intent(getApplicationContext(), WordTest.class);
            intent.putExtra("cate_name", "All");
            startActivity(intent);
        }
    }
}
