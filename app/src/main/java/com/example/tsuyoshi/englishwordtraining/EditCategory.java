package com.example.tsuyoshi.englishwordtraining;

import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by tsuyoshi on 2018/03/11.
 */

public class EditCategory extends AppCompatActivity {
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語リスト","単語登録","単語テスト","カテゴリ追加","使い方","設定","お問い合わせ"};

    protected SQLiteDatabase db;
    protected Cursor cursor;
    private final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT;
    protected String[] mListStringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_category);

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
                        intent.setClass(EditCategory.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(EditCategory.this, TestSample.class);
                        break;
                    case 2:
                        intent.setClass(EditCategory.this, WordRegist.class);
                        break;
                    case 3:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(EditCategory.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(EditCategory.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 4:
                        intent.setClass(EditCategory.this, AddCategory.class);
                        break;
                    case 5:
                        intent.setClass(EditCategory.this, HowToUse.class);
                        break;
                    case 6:
                        intent.setClass(EditCategory.this, SettingsActivity.class);
                        break;
                    case 7:
                        intent.setClass(EditCategory.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //データベースのデータをListViewに格納
        db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.CATEGORY_NAME);
        cursor = db.query(DatabaseOpenHelper2.CATEGORY_NAME,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        int fieldNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);

        final ListView myListView = (ListView) findViewById(R.id.category_list);
        mListStringArray = new String[(int) recodeCount];

        ArrayList<String> items = new ArrayList<>();
        for(int i = 0; i < recodeCount; i++) {
            mListStringArray[i]=cursor.getString(fieldNameIndex);
            items.add(cursor.getString(fieldNameIndex));
            if(!cursor.isLast()){
                cursor.moveToNext();
            }
        }

        // Adapter - ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                items
        );

        // ListViewに表示
        myListView.setAdapter(adapter);

        //長押しによるダイアログ表示
        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final EditText editView = new EditText(EditCategory.this);
                editView.setText((String) myListView.getItemAtPosition(position));

                if(((String) myListView.getItemAtPosition(position)).equals("未分類")){
                    new AlertDialog.Builder(EditCategory.this)
                            .setTitle("警告")
                            .setMessage("未分類は編集できません。")
                            .setPositiveButton("OK", null)
                            .show();
                }else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditCategory.this);
                    LinearLayout layout = new LinearLayout(EditCategory.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(editView, new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                    dialog.setView(layout);

                    final ContentValues cv = new ContentValues();

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (editView.getText().toString().equals("")) {
                                new AlertDialog.Builder(EditCategory.this).setTitle("警告").setMessage("未入力あり！").setPositiveButton("OK", null).show();
                            } else {
                                cv.put("Field", editView.getText().toString());
                                db.update(DatabaseOpenHelper2.CATEGORY_NAME, cv, "Field=?", new String[]{mListStringArray[position]});
                                cv.clear();
                                //画面更新
                                Intent intent = new Intent(EditCategory.this, EditCategory.class);
                                startActivityForResult(intent, 0);
                            }
                        }
                    });

                    dialog.setNegativeButton("削除", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(EditCategory.this).setTitle("警告").setMessage("本当に削除しますか？").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK button pressed
                                    db.delete(DatabaseOpenHelper2.CATEGORY_NAME, "Field=?", new String[]{mListStringArray[position]});
                                    //画面更新
                                    Intent intent = new Intent(EditCategory.this, EditCategory.class);
                                    startActivityForResult(intent, 0);
                                }
                            }).setNegativeButton("Cancel", null).show();
                        }
                    });

                    dialog.setNeutralButton("Cancle", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }


                return false;
            }
        });

        //タイトルを白字にする
        String title = "カテゴリ編集";
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
}
