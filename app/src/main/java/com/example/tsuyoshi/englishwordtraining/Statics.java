package com.example.tsuyoshi.englishwordtraining;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tsuyoshi on 2018/04/29.
 */

public class Statics extends AppCompatActivity{
    protected SQLiteDatabase db;
    protected Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statics);

        //データベースのデータをListViewに格納
        db = (new DatabaseOpenHelper2(this)).getWritableDatabase();
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseOpenHelper2.CATEGORY_NAME);
        cursor = db.query(DatabaseOpenHelper2.CATEGORY_NAME,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        int fieldNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD);
        int fieldCountNameIndex = cursor.getColumnIndex(DatabaseOpenHelper2.COLUMN_FIELD_COUNT);

    }
}
