package com.example.tsuyoshi.englishwordtraining;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by tsuyoshi on 2018/03/04.
 */

public class Contact extends AppCompatActivity {
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語リスト","単語登録","単語テスト","カテゴリ編集","カテゴリ追加","使い方"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

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
                        intent.setClass(Contact.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(Contact.this, TestSample.class);
                        break;
                    case 2:
                        intent.setClass(Contact.this, WordRegist.class);
                        break;
                    case 3:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(Contact.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(Contact.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 4:
                        intent.setClass(Contact.this, EditCategory.class);
                        break;
                    case 5:
                        intent.setClass(Contact.this, AddCategory.class);
                        break;
                    case 6:
                        intent.setClass(Contact.this, HowToUse.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        //タイトルを白字にする
        String title = "お問い合わせ";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));

        TextView descrption = (TextView) findViewById(R.id.textView8);
        descrption.setText(R.string.Contact);

        TextView text_mail = (TextView) findViewById(R.id.textView4);

        text_mail.setText("phoenimaru1@gmail.com");
        Linkify.addLinks(text_mail, Linkify.EMAIL_ADDRESSES);

        TextView descrption2 = (TextView) findViewById(R.id.textView3);
        descrption2.setText(R.string.Contact2);
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
