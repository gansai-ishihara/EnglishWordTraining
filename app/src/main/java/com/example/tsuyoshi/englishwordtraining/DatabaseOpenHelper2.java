package com.example.tsuyoshi.englishwordtraining;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper2 extends SQLiteOpenHelper {
    // データベース名
    public static final String DB_NAME  = "database_foods";
    // テーブル名
    public static final String TABLE_NAME = "table_foods";
    public static final String CATEGORY_NAME = "table_category";
    // カラム名
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_NO_MEMORIZED_COUNT = "NoMemoryCount";
    public static final String COLUMN_MEMORIZED_COUNT = "MemoryCount";
    public static final String COLUMN_WORD_SKIP = "WordSkip";
    public static final String COLUMN_TODAY = "Today";
    public static final String COLUMN_DAY = "Day";
    public static final String COLUMN_FIELD = "Field";
    public static final String COLUMN_FIELD_COUNT = "FieldCount";
    // 初期 サンプルデータ
    private String[][] datas = new String[][]{
            {"sample1","sample1_description"},{"sample2","sample1_description"},{"sample3","sample1_description"}
    };
    private String[][] categories = new String[][]{
            {"未分類","未分類"}
    };

    public DatabaseOpenHelper2(Context context) {
        super(context, DB_NAME, null, 12);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // テーブルの生成
            StringBuilder sb = new StringBuilder();
            sb.append("create table " + TABLE_NAME + " (");
            sb.append(COLUMN_ID + " integer primary key,");
            sb.append(COLUMN_NAME + " text,");
            sb.append(COLUMN_PRICE + " text");
            sb.append(")");
            db.execSQL(sb.toString());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        // サンプルデータの投入
        db.beginTransaction();
        try {
            for (String[] data: datas) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, data[0]);
                values.put(COLUMN_PRICE, data[1]);
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // DBバージョンアップ時のデータ移行を実装
        if( oldVersion == 4 && newVersion == 5 ){
            // カラムの追加
            db.execSQL("ALTER TABLE `table_foods` ADD `NoMemoryCount` INTEGER AFTER `price`;");
            db.execSQL("ALTER TABLE `table_foods` ADD `MemoryCount` INTEGER AFTER `NoMemoryCount`;");
            db.execSQL("ALTER TABLE `table_foods` ADD `WordSkip` INTEGER AFTER `MemoryCount`;");

            // 既存データの取得（idとnameとprice）
            Cursor cursor = db.query("table_foods", new String[]{"_id", "name", "price"}, null, null, null, null, "_id ASC");

            // 既存データのNoMemoryCount,MemoryCount,WordSkipカラムのデータを入れる
            String NoMemoryCount = "0";
            String MemoryCount = "0";
            String WordSkip = "false";
            if(cursor.moveToFirst()){
                do{
                    db.execSQL(
                            "UPDATE `table_foods` SET `NoMemoryCount` = ? WHERE `_id` = ?",
                            new String[]{NoMemoryCount, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                    db.execSQL(
                            "UPDATE `table_foods` SET `MemoryCount` = ? WHERE `_id` = ?",
                            new String[]{MemoryCount, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                    db.execSQL(
                            "UPDATE `table_foods` SET `WordSkip` = ? WHERE `_id` = ?",
                            new String[]{WordSkip, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }

        if( oldVersion == 5 && newVersion == 6 ){
            // カラムの追加
            db.execSQL("ALTER TABLE `table_foods` ADD `Today` INTEGER AFTER `WordSkip`;");

            // 既存データの取得（idとnameとprice）
            Cursor cursor = db.query("table_foods", new String[]{"_id", "name", "price", "NoMemoryCount", "MemoryCount", "WordSkip"}, null, null, null, null, "_id ASC");

            // 既存データのNoMemoryCount,MemoryCount,WordSkipカラムのデータを入れる
            String Today = "false";
            if(cursor.moveToFirst()){
                do{
                    db.execSQL(
                            "UPDATE `table_foods` SET `Totday` = ? WHERE `_id` = ?",
                            new String[]{Today, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }

        if( newVersion == 7 ){
            // カラムの追加
            db.execSQL("ALTER TABLE `table_foods` ADD `Today` INTEGER AFTER `WordSkip`;");

            // 既存データの取得（idとnameとprice）
            Cursor cursor = db.query("table_foods", new String[]{"_id", "name", "price", "NoMemoryCount", "MemoryCount", "WordSkip"}, null, null, null, null, "_id ASC");

            // 既存データのTodayカラムのデータを入れる
            String Today = "false";
            if(cursor.moveToFirst()){
                do{
                    db.execSQL(
                            "UPDATE `table_foods` SET `Today` = ? WHERE `_id` = ?",
                            new String[]{Today, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }

        if( newVersion == 8 ){
            // カラムの追加
            db.execSQL("ALTER TABLE `table_foods` ADD `Day` INTEGER AFTER `Today`;");

            // 既存データの取得（idとnameとprice）
            Cursor cursor = db.query("table_foods", new String[]{"_id", "name", "price", "NoMemoryCount", "MemoryCount", "WordSkip", "Today"}, null, null, null, null, "_id ASC");

            // 既存データのDayカラムのデータを入れる
            String Day = "0";
            if(cursor.moveToFirst()){
                do{
                    db.execSQL(
                            "UPDATE `table_foods` SET `" + "Day` = ? WHERE `_id` = ?",
                            new String[]{Day, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }

        if( newVersion == 9 ){
            // カラムの追加
            db.execSQL("ALTER TABLE `table_foods` ADD `Field` INTEGER AFTER `Day`;");

            // 既存データの取得（idとnameとprice）
            Cursor cursor = db.query("table_foods", new String[]{"_id", "name", "price", "NoMemoryCount", "MemoryCount", "WordSkip", "Today", "Day"}, null, null, null, null, "_id ASC");

            // 既存データのFieldカラムのデータを入れる
            String Day = "未分類";
            if(cursor.moveToFirst()){
                do{
                    db.execSQL(
                            "UPDATE `table_foods` SET `" + "Field` = ? WHERE `_id` = ?",
                            new String[]{Day, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }

        if( newVersion == 10 ){
            db.beginTransaction();
            try {
                // テーブルの生成
                StringBuilder sb = new StringBuilder();
                sb.append("create table " + CATEGORY_NAME + " (");
                sb.append(COLUMN_ID + " integer primary key,");
                sb.append(COLUMN_FIELD + " text");
                sb.append(")");
                db.execSQL(sb.toString());
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            // サンプルデータの投入
            db.beginTransaction();
            try {
                for (String[] data: categories) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIELD, data[0]);
                    db.insert(CATEGORY_NAME, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if( newVersion == 11 ){
            // サンプルデータの投入
            db.beginTransaction();
            try {
                for (String[] data: categories) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIELD, data[0]);
                    db.insert(CATEGORY_NAME, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if( newVersion == 12 ){
            // カラムの追加
            db.execSQL("ALTER TABLE `table_category` ADD `FieldCount` INTEGER AFTER `Field`;");

            // 既存データの取得（idとnameとprice）
            Cursor cursor = db.query("table_category", new String[]{"_id", "Field"}, null, null, null, null, "_id ASC");

            // 既存データのFieldCountカラムのデータを入れる
            String FCount = "0";
            if(cursor.moveToFirst()){
                do{
                    db.execSQL(
                            "UPDATE `table_category` SET `" + "FieldCount` = ? WHERE `_id` = ?",
                            new String[]{FCount, String.valueOf(cursor.getLong(cursor.getColumnIndex("_id")))}
                    );
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }
    }
}