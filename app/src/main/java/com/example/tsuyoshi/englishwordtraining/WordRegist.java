package com.example.tsuyoshi.englishwordtraining;

import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordRegist extends AppCompatActivity implements OnClickListener {
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語リスト","単語テスト","カテゴリ追加","カテゴリ編集","使い方","設定","お問い合わせ"};

    protected SQLiteDatabase db;
    protected Cursor cursor;

    private Button word_insert;
    private EditText input_word;
    private EditText input_desctiption;
    private Button category_text;
    private int defaultItem = 0; // デフォルトでチェックされているアイテム

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_regist);

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
                        intent.setClass(WordRegist.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(WordRegist.this, TestSample.class);
                        break;
                    case 2:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(WordRegist.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(WordRegist.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 3:
                        intent.setClass(WordRegist.this, AddCategory.class);
                        break;
                    case 4:
                        intent.setClass(WordRegist.this, EditCategory.class);
                        break;
                    case 5:
                        intent.setClass(WordRegist.this, HowToUse.class);
                        break;
                    case 6:
                        intent.setClass(WordRegist.this, SettingsActivity.class);
                        break;
                    case 7:
                        intent.setClass(WordRegist.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //タイトルを白字にする
        String title = "単語登録";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));

        //ボタンのクリックアクションの準備
        word_insert=(Button)findViewById(R.id.button_insert);
        word_insert.setOnClickListener(this);

        category_text=(Button)findViewById(R.id.button_category);
        category_text.setOnClickListener(this);
        category_text.setText("未分類");
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

        //ボタンクリック時の関数
    public void onClick(View v) {
        if(v==category_text){

            //データベース取得
            db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
            cursor = db.query(DatabaseOpenHelper2.CATEGORY_NAME,
                    null, null, null, null, null, null);
            int fieldNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);
            long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.CATEGORY_NAME);
            cursor.moveToFirst();

            //データベースからカテゴリを取り出す
            final String[] items = new String[(int)recodeCount];
            for (int i = 0; i < recodeCount; i++) {
                items[i] = cursor.getString(fieldNameIndex);
                if(!cursor.isLast()){
                    cursor.moveToNext();
                }
            }


            final List<Integer> checkedItems = new ArrayList<>();
            checkedItems.add(defaultItem);
            new AlertDialog.Builder(this)
                    .setTitle("カテゴリ分類")
                    .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                            defaultItem=which;
                            category_text.setText(items[which]);
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!checkedItems.isEmpty()) {
                                Log.d("checkedItem:", "" + checkedItems.get(0));
                            }
                        }
                    })
                    .setNeutralButton("カテゴリ追加",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // カテゴリ追加画面へ遷移
                            Intent intent = new Intent(WordRegist.this, AddCategory.class);
                            startActivityForResult(intent, 0);
                        }
                    })
                    .show();
        }
        if(v==word_insert){

            //データベース取得
            db = (new DatabaseOpenHelper2(this)).getWritableDatabase();

            ContentValues cv = new ContentValues();

            //入力取得
            input_word = (EditText)findViewById(R.id.edit_word);
            input_desctiption = (EditText)findViewById(R.id.edit_description);
            category_text = (Button)findViewById(R.id.button_category);

            String word = input_word.getText().toString();
            String description = input_desctiption.getText().toString();
            String CategoryText = category_text.getText().toString();

            //何も入力されてなかったらダイアログを表示
            if(word.equals("") || description.equals("")){
                new AlertDialog.Builder(this)
                        .setTitle("警告")
                        .setMessage("未入力あり！")
                        .setPositiveButton("OK", null)
                        .show();
            }else{
                //データベースに登録
                cv.put("name", word);
                cv.put("price", description);
                cv.put("NoMemoryCount", "0");
                cv.put("MemoryCount", "0");
                cv.put("WordSkip", "1");
                cv.put("Today", "true");
                cv.put("Day", "0");
                cv.put("Field",CategoryText);


                db.insert(DatabaseOpenHelper2.TABLE_NAME, null, cv);

                //画面遷移
                //Intent intent = new Intent(this, TestSample.class);
                //startActivityForResult(intent, 0);

                //コメント（トースト）表示
                Toast.makeText(this,"「"+word+"」を追加しました。",Toast.LENGTH_LONG).show();

                //リセット
                input_word.setText("");
                input_desctiption.setText("");
                category_text.setText("未分類");
                defaultItem = 0;

            }
        }
    }
}