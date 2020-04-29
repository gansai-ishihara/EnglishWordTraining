/*
*
* 1+ → 221 332 → 2を飛ばし、1へ
* 1+ → 222 332 → 今日やってたら終了
* 1+ → 222 332 → 今日やってないやつだったら2へ
* 1+ → 222 232 →
*
* 0+ → 110 221 → 1を飛ばし、0へ
* 0+ → 111 221 → 今日やってたら終了
* 0+ → 111 221 → 今日やってないやつだったら1へ
* 0+ → 111 121 →
*
*
* */

package com.example.tsuyoshi.englishwordtraining;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class WordTest extends AppCompatActivity implements OnClickListener, View.OnClickListener {
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語登録","単語リスト","カテゴリ追加","カテゴリ編集","使い方","設定","お問い合わせ"};

    protected SQLiteDatabase db;
    protected Cursor cursor;
    protected ContentValues cv;

    protected int word_index;
    protected int petNameIndex;
    protected int petTypeIndex;
    protected int noWord;
    protected int yesWord;
    protected int word_skip;
    protected int today;
    protected int date;
    protected int word_category;

    private Button word_present;
    private Button word_no_memorized;
    private Button word_memorized;

    private boolean pre=false;
    private String selection;
    private Intent intent;
    private int qCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_test);

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
                        intent.setClass(WordTest.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(WordTest.this, WordRegist.class);
                        break;
                    case 2:
                        intent.setClass(WordTest.this, TestSample.class);
                        break;
                    case 3:
                        intent.setClass(WordTest.this, AddCategory.class);
                        break;
                    case 4:
                        intent.setClass(WordTest.this, EditCategory.class);
                        break;
                    case 5:
                        intent.setClass(WordTest.this, HowToUse.class);
                        break;
                    case 6:
                        intent.setClass(WordTest.this, SettingsActivity.class);
                        break;
                    case 7:
                        intent.setClass(WordTest.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //タイトルを白字にする
        String title = "単語テスト";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));

        //ボタンのクリックアクションの準備
        word_present = (Button) findViewById(R.id.button6);
        word_present.setOnClickListener(this);

        word_no_memorized = (Button) findViewById(R.id.no_button);
        word_no_memorized.setOnClickListener(this);

        word_memorized = (Button) findViewById(R.id.yes_button);
        word_memorized.setOnClickListener(this);

        //データベース取得
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
        }

        cv.clear();
        cursor.moveToFirst();
        intent=getIntent();

        String test = intent.getStringExtra("cate_name");
        if(intent.getStringExtra("cate_name").equals("All")){
            selection = "MemoryCount<? AND Today=? AND WordSkip<?";
            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                    null, selection, new String[]{"3","false","1"}, null, null, DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT + " asc");

        }else{
            selection = "MemoryCount<? AND Today=? AND WordSkip<? AND Field=?";
            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                    null, selection, new String[]{"3","false","1",intent.getStringExtra("cate_name")}, null, null, DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT + " asc");
        }

        if(cursor.getCount()==0){
            endFlame();
        }else {
            word_index = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_ID);
            petTypeIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_PRICE);
            noWord = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_NO_MEMORIZED_COUNT); //最大数7, 覚えてなければ+1, 覚えても+1
            yesWord = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT); //最大数3, 覚えてれば+1
            today = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_TODAY);
            word_category = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);

            cursor.moveToFirst();
            qCount=cursor.getCount();

            TextView qNumber = (TextView) findViewById(R.id.question_number);
            qNumber.setText(cursor.getString(word_category)+" 残り全"+qCount+"問");

            TextView varTextView = (TextView) findViewById(R.id.wordPresentation);
            varTextView.setText(cursor.getString(petNameIndex));
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

    @Override
    public void onClick(View v) {

        //解答表示
        if(v==word_present && !pre){
            pre=true;
            TextView var2TextView = (TextView)findViewById(R.id.wordDescription);
            var2TextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            var2TextView.setText(cursor.getString(petTypeIndex));
        }else if(v==word_present && pre){
            pre=false;
            TextView var2TextView = (TextView)findViewById(R.id.wordDescription);
            var2TextView.setText("解答隠し中");
        }

        //覚えた？覚えてない？
        if(v==word_no_memorized){
            //覚えてないなら+1, todayにtrue
            cv.put("NoMemoryCount", String.valueOf(Integer.parseInt(cursor.getString(noWord))+1));
            cv.put("Today", "true");
            //７回やってもダメなら覚えたことにする
            if(Integer.parseInt(cursor.getString(noWord))==6){
                if((Integer.parseInt(cursor.getString(yesWord)) + 1)==3){
                    cv.put("NoMemoryCount", "0");
                    cv.put("MemoryCount", "0");
                    cv.put("WordSkip", "30");
                }else {
                    cv.put("NoMemoryCount", "0");
                    cv.put("MemoryCount", String.valueOf(Integer.parseInt(cursor.getString(yesWord)) + 1));
                    cv.put("WordSkip", "7");
                }
            }
            db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{cursor.getString(petNameIndex)});

            //今日どれだけ覚えられなかったかカウントする
            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            Editor e = pref.edit();
            e.putInt("tno", pref.getInt("tno",0)+1);
            e.commit();

            if(cursor.isLast()){
                endFlame();
            }else {
                cupdate();
                presentView();
            }
        }else if(v==word_memorized){
            if((Integer.parseInt(cursor.getString(yesWord)) + 1)==3){
                //３に到達したら削除
                db.delete(DatabaseOpenHelper2.TABLE_NAME,"name=?",new String[]{cursor.getString(petNameIndex)});

                //３回目で覚えられてたら覚えた単語数のカウントを１増やす
                SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                Editor e = pref.edit();
                e.putInt("clear", pref.getInt("clear",0)+1);
                e.commit();
            }else {
                //データベース更新
                cv.put("NoMemoryCount", "0");
                cv.put("MemoryCount", String.valueOf(Integer.parseInt(cursor.getString(yesWord)) + 1));
                cv.put("Today", "true");
                cv.put("WordSkip", "7");
                db.update(DatabaseOpenHelper2.TABLE_NAME, cv, "name=?", new String[]{cursor.getString(petNameIndex)});
            }

            //今日どれだけ覚えたかカウントする
            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            Editor e = pref.edit();
            e.putInt("tok", pref.getInt("tok",0)+1);
            e.commit();

            if(cursor.isLast()){
                endFlame();
            }else {
                cupdate();
                presentView();
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    public void endFlame() {
        // 変更したいレイアウトを取得する
        CoordinatorLayout layout = (CoordinatorLayout)findViewById(R.id.coordinate);

        // レイアウトのビューをすべて削除する
        layout.removeAllViews();

        // レイアウトをR.layout.sampleに変更する
        getLayoutInflater().inflate(R.layout.test_end, layout);

        //今日の問題数
        TextView endtext = (TextView) findViewById(R.id.endText6);
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        endtext.setText(""+pref.getInt("ok",0));

        //今日の覚えた数
        TextView endtext2 = (TextView) findViewById(R.id.endText2);
        endtext2.setText(""+pref.getInt("tok",0));

        //今日も覚えてない数
        TextView endtext3 = (TextView) findViewById(R.id.endText7);
        endtext3.setText(""+pref.getInt("tno",0));
    }

    public void presentView(){
        qCount--;
        TextView qNumber = (TextView) findViewById(R.id.question_number);
        qNumber.setText(cursor.getString(word_category)+" 残り全"+qCount+"問");

        TextView varTextView = (TextView) findViewById(R.id.wordPresentation);
        varTextView.setText(cursor.getString(petNameIndex));

        pre = false;
        TextView var2TextView = (TextView) findViewById(R.id.wordDescription);
        var2TextView.setText("解答隠し中");
    }

    public void cupdate(){
        cv.clear();
        if(intent.getStringExtra("cate_name").equals("All")){
            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                    null, selection, new String[]{"3","false","1"}, null, null, DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT + " asc");
        }else{
            cursor = db.query(DatabaseOpenHelper2.TABLE_NAME,
                    null, selection, new String[]{"3","false","1",intent.getStringExtra("cate_name")}, null, null, DatabaseOpenHelper2.COLUMN_MEMORIZED_COUNT + " asc");
        }
        cursor.moveToFirst();
    }
}