package com.example.tsuyoshi.englishwordtraining;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ExpandableListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by tsuyoshi on 2017/12/17.
 */

public class TestSample extends ExpandableListActivity {
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語登録","単語テスト","カテゴリ追加","カテゴリ編集","使い方","設定","お問い合わせ"};

    protected SQLiteDatabase db;
    protected Cursor cursor;
    protected String[] mListStringArray;
    private final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT;
    protected int petNameIndexc;
    protected int petTypeIndexc;

    ContentValues cv = new ContentValues();

    private Button category_button;
    private int defaultItem = 0; // デフォルトでチェックされているアイテム

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

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
                        intent.setClass(TestSample.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(TestSample.this, WordRegist.class);
                        break;
                    case 2:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(TestSample.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(TestSample.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 3:
                        intent.setClass(TestSample.this, AddCategory.class);
                        break;
                    case 4:
                        intent.setClass(TestSample.this, EditCategory.class);
                        break;
                    case 5:
                        intent.setClass(TestSample.this, HowToUse.class);
                        break;
                    case 6:
                        intent.setClass(TestSample.this, SettingsActivity.class);
                        break;
                    case 7:
                        intent.setClass(TestSample.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });


        //アクションバー
        String title = "単語リスト";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getActionBar().setTitle(Html.fromHtml(titleHtml));
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xff3F51B5));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ExpandableListView elist = (ExpandableListView) findViewById(android.R.id.list);
        registerForContextMenu(elist);

        List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childList = new ArrayList<List<Map<String, String>>>();

        db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.TABLE_NAME);
        mListStringArray = new String[(int) recodeCount];

        cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                null, null, null, null, null, null);
        int petNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NO_MEMORIZED_COUNT);
        int petNameIndex2 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT);
        int petNameIndex3 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_TODAY);
        int petNameIndex4 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_DAY);
        int petNameIndex5 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_WORD_SKIP);
        int petNameIndex6 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);
        int petNameIndex7 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_ID);
        final int petNameIndex8 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);
        int petTypeIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
        cursor.moveToFirst();

        for (int i = 0; i < recodeCount; i++) {
            // Group（親）のリスト
            Map<String, String> groupElement = new HashMap<String, String>();
            groupElement.put("GROUP_TITLE", cursor.getString(petNameIndex)+" "+cursor.getString(petNameIndex2)+" "+cursor.getString(petNameIndex5)+" "+cursor.getString(petNameIndex6)+" "+cursor.getString(petNameIndex8));
            //groupElement.put("GROUP_TITLE", cursor.getString(petNameIndex6));
            mListStringArray[i]=cursor.getString(petNameIndex6);
            groupList.add(groupElement);
            // Childのリスト
            List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();

            Map<String, String> child = new HashMap<String, String>();
            child.put("CHILD_TITLE", cursor.getString(petTypeIndex));
            childElements.add(child);

            childList.add(childElements);

            cursor.moveToNext();
        }
        cursor.close();

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                // Group(親)のリスト
                groupList,
                // Group(親)のレイアウト
                android.R.layout.simple_expandable_list_item_1,
                // Group(親)のリストで表示するMapのキー
                new String[]{"GROUP_TITLE"},
                // Group(親)のレイアウト内での文字を表示するTextViewのID
                new int[]{android.R.id.text1},
                // Child(子)のリスト
                childList,
                // Child(子)のレイアウト
                R.layout.child_list_view,
                // Child(子)のリストで表示するMapのキー
                new String[]{"CHILD_TITLE"},
                // Child(子)のレイアウト内での文字を表示するTextViewのID
                new int[]{R.id.listtextView1}
        );
        setListAdapter(adapter);

        elist.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ExpandableListView listView = (ExpandableListView) parent;
                long packed = listView.getExpandableListPosition(position);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(packed);
                int childPosition = ExpandableListView.getPackedPositionChild(packed);
                if(ExpandableListView.getPackedPositionType(packed) == 1) {

                    final EditText editView = new EditText(TestSample.this);
                    category_button = new Button(TestSample.this);
                    final EditText editView2 = new EditText(TestSample.this);
                    cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                            null, "name=?", new String[]{mListStringArray[groupPosition]}, null, null, null);
                    cursor.moveToFirst();
                    petNameIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);
                    petTypeIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
                    editView.setText(cursor.getString(petNameIndexc));
                    category_button.setText(cursor.getString(petNameIndex8));
                    category_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //データベース取得
                            categoryButtonClickEvent(cursor.getString(petNameIndex8));
                        }
                    });

                    editView2.setText(cursor.getString(petTypeIndexc));
                    editView2.setHeight(450);
                    editView2.setGravity(Gravity.LEFT | Gravity.TOP);

                    Builder dialog = new AlertDialog.Builder(TestSample.this);
                    LinearLayout layout = new LinearLayout(TestSample.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(editView,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                    layout.addView(category_button,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                    layout.addView(editView2,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                    dialog.setView(layout);


                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(editView.getText().toString()=="" || editView2.getText().toString()==""){
                                new AlertDialog.Builder(TestSample.this)
                                        .setTitle("警告")
                                        .setMessage("未入力あり！")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }else {
                                cv.put("name", editView.getText().toString());
                                cv.put("field",category_button.getText().toString());
                                cv.put("price", editView2.getText().toString());
                                db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{mListStringArray[groupPosition]});
                                cv.clear();
                                //画面更新
                                Intent intent = new Intent(TestSample.this, TestSample.class);
                                startActivityForResult(intent, 0);
                            }
                        }
                    });

                    dialog.setNegativeButton("削除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(TestSample.this)
                                            .setTitle("警告")
                                            .setMessage("本当に削除しますか？")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // OK button pressed
                                                    db.delete(DatabaseOpenHelper2.TABLE_NAME,"name=?",new String[]{mListStringArray[groupPosition]});
                                                    //画面更新
                                                    Intent intent = new Intent(TestSample.this, TestSample.class);
                                                    startActivityForResult(intent, 0);
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            });

                    dialog.setNeutralButton("Cancle",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialog.show();


                    return true;
                } else {
                    // 親要素が長押しされた時のアクションを記述
                    return true;
                }
            }
        });


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
    public boolean onCreateOptionsMenu(Menu menu) {
        //main.xmlの内容を読み込む
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    public void categoryButtonClickEvent(String category) {
        //初期化
        defaultItem=0;

        //データベース取得
        db = (new DatabaseOpenHelper2(TestSample.this)).getWritableDatabase();
        cursor = db.query(DatabaseOpenHelper2.CATEGORY_NAME, null, null, null, null, null, null);
        int fieldNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.CATEGORY_NAME);
        cursor.moveToFirst();

        //データベースからカテゴリを取り出す
        final String[] items = new String[(int) recodeCount];
        for (int i = 0; i < recodeCount; i++) {
            items[i] = cursor.getString(fieldNameIndex);
            if (!cursor.isLast()) {
                cursor.moveToNext();
            }
        }

        //カテゴリの初期値の設定
        cursor.moveToFirst();
        String category2 = cursor.getString(fieldNameIndex);
        for (int i=0; i < recodeCount; i++) {
            if(category.equals(cursor.getString(fieldNameIndex))){
                break;
            }
            defaultItem++;
            cursor.moveToNext();
        }

        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);

        Builder dialog = new AlertDialog.Builder(TestSample.this);
        LinearLayout layout = new LinearLayout(TestSample.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        dialog.setView(layout);
        dialog.setTitle("カテゴリ分類");
        dialog.setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItems.clear();
                checkedItems.add(which);
                defaultItem = which;
                category_button.setText(items[which]);
            }});
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!checkedItems.isEmpty()) {
                    Log.d("checkedItem:", "" + checkedItems.get(0));
                }
            }});
        dialog.setNeutralButton("カテゴリ追加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // カテゴリ追加画面へ遷移
                Intent intent = new Intent(TestSample.this, AddCategory.class);
                startActivityForResult(intent, 0);

            }});
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }


        ExpandableListView elist = (ExpandableListView) findViewById(android.R.id.list);
        registerForContextMenu(elist);

        List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childList = new ArrayList<List<Map<String, String>>>();

        db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.TABLE_NAME);
        mListStringArray = new String[(int) recodeCount];

        cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                null, null, null, null, null, null);
        int petNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NO_MEMORIZED_COUNT);
        int petNameIndex2 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT);
        int petNameIndex3 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_TODAY);
        int petNameIndex4 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_DAY);
        int petNameIndex5 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_WORD_SKIP);
        int petNameIndex6 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);
        int petTypeIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
        final int petNameIndex8 = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);

        SimpleExpandableListAdapter adapter;

        switch (item.getItemId()) {
            case R.id.item1:

                cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                        null, null, null, null, null, DatabaseOpenHelper2.COLUMN_ID + " desc");
                cursor.moveToFirst();

                for (int i = 0; i < recodeCount; i++) {
                    // Group（親）のリスト
                    Map<String, String> groupElement = new HashMap<String, String>();
                    groupElement.put("GROUP_TITLE", cursor.getString(petNameIndex)+" "+cursor.getString(petNameIndex2)+" "+cursor.getString(petNameIndex5)+" "+cursor.getString(petNameIndex6));
                    mListStringArray[i]=cursor.getString(petNameIndex6);
                    groupList.add(groupElement);
                    // Childのリスト
                    List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();

                    Map<String, String> child = new HashMap<String, String>();
                    child.put("CHILD_TITLE", cursor.getString(petTypeIndex)+"\n\n編集 削除");
                    childElements.add(child);

                    childList.add(childElements);

                    cursor.moveToNext();
                }
                cursor.close();

                adapter = new SimpleExpandableListAdapter(
                        this,
                        // Group(親)のリスト
                        groupList,
                        // Group(親)のレイアウト
                        android.R.layout.simple_expandable_list_item_1,
                        // Group(親)のリストで表示するMapのキー
                        new String[]{"GROUP_TITLE"},
                        // Group(親)のレイアウト内での文字を表示するTextViewのID
                        new int[]{android.R.id.text1},
                        // Child(子)のリスト
                        childList,
                        // Child(子)のレイアウト
                        R.layout.child_list_view,
                        // Child(子)のリストで表示するMapのキー
                        new String[]{"CHILD_TITLE"},
                        // Child(子)のレイアウト内での文字を表示するTextViewのID
                        new int[]{R.id.listtextView1}
                );
                setListAdapter(adapter);
                elist.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ExpandableListView listView = (ExpandableListView) parent;
                        long packed = listView.getExpandableListPosition(position);
                        final int groupPosition = ExpandableListView.getPackedPositionGroup(packed);
                        int childPosition = ExpandableListView.getPackedPositionChild(packed);
                        if(ExpandableListView.getPackedPositionType(packed) == 1) {

                            final EditText editView = new EditText(TestSample.this);
                            category_button = new Button(TestSample.this);
                            final EditText editView2 = new EditText(TestSample.this);
                            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                                    null, "name=?", new String[]{mListStringArray[groupPosition]}, null, null, null);
                            cursor.moveToFirst();
                            petNameIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);
                            petTypeIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
                            editView.setText(cursor.getString(petNameIndexc));
                            category_button.setText(cursor.getString(petNameIndex8));
                            category_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //データベース取得
                                    categoryButtonClickEvent(cursor.getString(petNameIndex8));
                                }
                            });

                            editView2.setText(cursor.getString(petTypeIndexc));
                            editView2.setHeight(450);
                            editView2.setGravity(Gravity.LEFT | Gravity.TOP);

                            Builder dialog = new AlertDialog.Builder(TestSample.this);
                            LinearLayout layout = new LinearLayout(TestSample.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(editView,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            layout.addView(category_button,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            layout.addView(editView2,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            dialog.setView(layout);


                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(editView.getText().toString()=="" || editView2.getText().toString()==""){
                                        new AlertDialog.Builder(TestSample.this)
                                                .setTitle("警告")
                                                .setMessage("未入力あり！")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }else {
                                        cv.put("name", editView.getText().toString());
                                        cv.put("field",category_button.getText().toString());
                                        cv.put("price", editView2.getText().toString());
                                        db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{mListStringArray[groupPosition]});
                                        cv.clear();
                                        //画面更新
                                        Intent intent = new Intent(TestSample.this, TestSample.class);
                                        startActivityForResult(intent, 0);
                                    }
                                }
                            });

                            dialog.setNegativeButton("削除",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new AlertDialog.Builder(TestSample.this)
                                                    .setTitle("警告")
                                                    .setMessage("本当に削除しますか？")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // OK button pressed
                                                            db.delete(DatabaseOpenHelper2.TABLE_NAME,"name=?",new String[]{mListStringArray[groupPosition]});
                                                            //画面更新
                                                            Intent intent = new Intent(TestSample.this, TestSample.class);
                                                            startActivityForResult(intent, 0);
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                    });

                            dialog.setNeutralButton("Cancle",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            dialog.show();


                            return true;
                        } else {
                            // 親要素が長押しされた時のアクションを記述
                            return true;
                        }
                    }
                });

                return true;
            case R.id.item2:
                cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                        null, null, null, null, null, DatabaseOpenHelper2.COLUMN_NAME + " asc");
                cursor.moveToFirst();

                for (int i = 0; i < recodeCount; i++) {
                    // Group（親）のリスト
                    Map<String, String> groupElement = new HashMap<String, String>();
                    groupElement.put("GROUP_TITLE", cursor.getString(petNameIndex)+" "+cursor.getString(petNameIndex2)+" "+cursor.getString(petNameIndex5)+" "+cursor.getString(petNameIndex6));
                    mListStringArray[i]=cursor.getString(petNameIndex6);
                    groupList.add(groupElement);
                    // Childのリスト
                    List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();

                    Map<String, String> child = new HashMap<String, String>();
                    child.put("CHILD_TITLE", cursor.getString(petTypeIndex)+"\n\n編集 削除");
                    childElements.add(child);

                    childList.add(childElements);

                    cursor.moveToNext();
                }
                cursor.close();

                adapter = new SimpleExpandableListAdapter(
                        this,
                        // Group(親)のリスト
                        groupList,
                        // Group(親)のレイアウト
                        android.R.layout.simple_expandable_list_item_1,
                        // Group(親)のリストで表示するMapのキー
                        new String[]{"GROUP_TITLE"},
                        // Group(親)のレイアウト内での文字を表示するTextViewのID
                        new int[]{android.R.id.text1},
                        // Child(子)のリスト
                        childList,
                        // Child(子)のレイアウト
                        R.layout.child_list_view,
                        // Child(子)のリストで表示するMapのキー
                        new String[]{"CHILD_TITLE"},
                        // Child(子)のレイアウト内での文字を表示するTextViewのID
                        new int[]{R.id.listtextView1}
                );
                setListAdapter(adapter);
                elist.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ExpandableListView listView = (ExpandableListView) parent;
                        long packed = listView.getExpandableListPosition(position);
                        final int groupPosition = ExpandableListView.getPackedPositionGroup(packed);
                        int childPosition = ExpandableListView.getPackedPositionChild(packed);
                        if(ExpandableListView.getPackedPositionType(packed) == 1) {

                            final EditText editView = new EditText(TestSample.this);
                            category_button = new Button(TestSample.this);
                            final EditText editView2 = new EditText(TestSample.this);
                            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                                    null, "name=?", new String[]{mListStringArray[groupPosition]}, null, null, null);
                            cursor.moveToFirst();
                            petNameIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);
                            petTypeIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
                            editView.setText(cursor.getString(petNameIndexc));
                            category_button.setText(cursor.getString(petNameIndex8));
                            category_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //データベース取得
                                    categoryButtonClickEvent(cursor.getString(petNameIndex8));
                                }
                            });

                            editView2.setText(cursor.getString(petTypeIndexc));
                            editView2.setHeight(450);
                            editView2.setGravity(Gravity.LEFT | Gravity.TOP);

                            Builder dialog = new AlertDialog.Builder(TestSample.this);
                            LinearLayout layout = new LinearLayout(TestSample.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(editView,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            layout.addView(category_button,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            layout.addView(editView2,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            dialog.setView(layout);


                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(editView.getText().toString()=="" || editView2.getText().toString()==""){
                                        new AlertDialog.Builder(TestSample.this)
                                                .setTitle("警告")
                                                .setMessage("未入力あり！")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }else {
                                        cv.put("name", editView.getText().toString());
                                        cv.put("field",category_button.getText().toString());
                                        cv.put("price", editView2.getText().toString());
                                        db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{mListStringArray[groupPosition]});
                                        cv.clear();
                                        //画面更新
                                        Intent intent = new Intent(TestSample.this, TestSample.class);
                                        startActivityForResult(intent, 0);
                                    }
                                }
                            });

                            dialog.setNegativeButton("削除",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new AlertDialog.Builder(TestSample.this)
                                                    .setTitle("警告")
                                                    .setMessage("本当に削除しますか？")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // OK button pressed
                                                            db.delete(DatabaseOpenHelper2.TABLE_NAME,"name=?",new String[]{mListStringArray[groupPosition]});
                                                            //画面更新
                                                            Intent intent = new Intent(TestSample.this, TestSample.class);
                                                            startActivityForResult(intent, 0);
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                    });

                            dialog.setNeutralButton("Cancle",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            dialog.show();


                            return true;
                        } else {
                            // 親要素が長押しされた時のアクションを記述
                            return true;
                        }
                    }
                });

                return true;
            case R.id.item3:

                cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                        null, null, null, null, null, DatabaseOpenHelper2.COLUMN_ID + " asc");
                cursor.moveToFirst();

                for (int i = 0; i < recodeCount; i++) {
                    // Group（親）のリスト
                    Map<String, String> groupElement = new HashMap<String, String>();
                    groupElement.put("GROUP_TITLE", cursor.getString(petNameIndex)+" "+cursor.getString(petNameIndex2)+" "+cursor.getString(petNameIndex5)+" "+cursor.getString(petNameIndex6));
                    mListStringArray[i]=cursor.getString(petNameIndex6);
                    groupList.add(groupElement);
                    // Childのリスト
                    List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();

                    Map<String, String> child = new HashMap<String, String>();
                    child.put("CHILD_TITLE", cursor.getString(petTypeIndex)+"\n\n編集 削除");
                    childElements.add(child);

                    childList.add(childElements);

                    cursor.moveToNext();
                }
                cursor.close();

                adapter = new SimpleExpandableListAdapter(
                        this,
                        // Group(親)のリスト
                        groupList,
                        // Group(親)のレイアウト
                        android.R.layout.simple_expandable_list_item_1,
                        // Group(親)のリストで表示するMapのキー
                        new String[]{"GROUP_TITLE"},
                        // Group(親)のレイアウト内での文字を表示するTextViewのID
                        new int[]{android.R.id.text1},
                        // Child(子)のリスト
                        childList,
                        // Child(子)のレイアウト
                        R.layout.child_list_view,
                        // Child(子)のリストで表示するMapのキー
                        new String[]{"CHILD_TITLE"},
                        // Child(子)のレイアウト内での文字を表示するTextViewのID
                        new int[]{R.id.listtextView1}
                );
                setListAdapter(adapter);
                elist.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ExpandableListView listView = (ExpandableListView) parent;
                        long packed = listView.getExpandableListPosition(position);
                        final int groupPosition = ExpandableListView.getPackedPositionGroup(packed);
                        int childPosition = ExpandableListView.getPackedPositionChild(packed);
                        if(ExpandableListView.getPackedPositionType(packed) == 1) {

                            final EditText editView = new EditText(TestSample.this);
                            category_button = new Button(TestSample.this);
                            final EditText editView2 = new EditText(TestSample.this);
                            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                                    null, "name=?", new String[]{mListStringArray[groupPosition]}, null, null, null);
                            cursor.moveToFirst();
                            petNameIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NAME);
                            petTypeIndexc = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
                            editView.setText(cursor.getString(petNameIndexc));
                            category_button.setText(cursor.getString(petNameIndex8));
                            category_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //データベース取得
                                    categoryButtonClickEvent(cursor.getString(petNameIndex8));
                                }
                            });

                            editView2.setText(cursor.getString(petTypeIndexc));
                            editView2.setHeight(450);
                            editView2.setGravity(Gravity.LEFT | Gravity.TOP);

                            Builder dialog = new AlertDialog.Builder(TestSample.this);
                            LinearLayout layout = new LinearLayout(TestSample.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(editView,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            layout.addView(category_button,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            layout.addView(editView2,new LinearLayout.LayoutParams(FILL_PARENT, WRAP_CONTENT));
                            dialog.setView(layout);


                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(editView.getText().toString()=="" || editView2.getText().toString()==""){
                                        new AlertDialog.Builder(TestSample.this)
                                                .setTitle("警告")
                                                .setMessage("未入力あり！")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }else {
                                        cv.put("name", editView.getText().toString());
                                        cv.put("field",category_button.getText().toString());
                                        cv.put("price", editView2.getText().toString());
                                        db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{mListStringArray[groupPosition]});
                                        cv.clear();
                                        //画面更新
                                        Intent intent = new Intent(TestSample.this, TestSample.class);
                                        startActivityForResult(intent, 0);
                                    }
                                }
                            });

                            dialog.setNegativeButton("削除",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new AlertDialog.Builder(TestSample.this)
                                                    .setTitle("警告")
                                                    .setMessage("本当に削除しますか？")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // OK button pressed
                                                            db.delete(DatabaseOpenHelper2.TABLE_NAME,"name=?",new String[]{mListStringArray[groupPosition]});
                                                            //画面更新
                                                            Intent intent = new Intent(TestSample.this, TestSample.class);
                                                            startActivityForResult(intent, 0);
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", null)
                                                    .show();
                                        }
                                    });

                            dialog.setNeutralButton("Cancle",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            dialog.show();


                            return true;
                        } else {
                            // 親要素が長押しされた時のアクションを記述
                            return true;
                        }
                    }
                });

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
