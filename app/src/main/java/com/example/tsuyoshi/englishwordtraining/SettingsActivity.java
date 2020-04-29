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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Locale;

/**
 * Created by tsuyoshi on 2018/03/29.
 */

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    // private:
    // static:
    // final:
    // protected:
    private ActionBarDrawerToggle toggle;
    private static final String[] foods = {"ホーム","単語リスト","単語登録","単語テスト","カテゴリ追加","カテゴリ編集","使い方","お問い合わせ"};

    protected Switch cs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //タイトルを白字にする
        String title = "設定";
        int titleColor = getResources().getColor(R.color.titleColor);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        String titleHtml = "<font color=\"" + htmlColor +  "\">" + title + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(titleHtml));

        //戻るボタンを設置
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                        intent.setClass(SettingsActivity.this, MainActivity.class);
                        break;
                    case 1:
                        intent.setClass(SettingsActivity.this, TestSample.class);
                        break;
                    case 2:
                        intent.setClass(SettingsActivity.this, WordRegist.class);
                        break;
                    case 3:
                        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                        if (pref.getBoolean("testcate",false)) {
                            intent.setClass(SettingsActivity.this, SelectTest.class);
                            startActivityForResult(intent, 0);
                        }else{
                            intent.setClass(SettingsActivity.this, WordTest.class);
                            intent.putExtra("cate_name", "All");
                            startActivityForResult(intent, 0);
                        }
                        break;
                    case 4:
                        intent.setClass(SettingsActivity.this, AddCategory.class);
                        break;
                    case 5:
                        intent.setClass(SettingsActivity.this, EditCategory.class);
                        break;
                    case 6:
                        intent.setClass(SettingsActivity.this, HowToUse.class);
                        break;
                    case 7:
                        intent.setClass(SettingsActivity.this, Contact.class);
                        break;
                }
                intent.putExtra("SELECTED_DATA", strData);
                startActivity(intent);
            }
        });

        cs = (Switch)findViewById(R.id.switch1);
        cs.setOnCheckedChangeListener(this);

        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        cs.setChecked(pref.getBoolean("testcate",false));

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == true) {
            // テスト時のカテゴリ分類あり
            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean("testcate", true);
            e.commit();
            cs.setChecked(true);
        } else {
            // テスト時のカテゴリ分類なし
            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor e = pref.edit();
            e.putBoolean("testcate", false);
            e.commit();
            cs.setChecked(false);
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
