package com.yzq.zxinglibrary.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2019/6/2.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String dbname="BookShelfDataBase.db";
    private static final int version=1;
    private static final String table="BookShelf";
    private static  SQLiteHelper dbHelper;
    //也可以不指定字段的类型、长度，因为int类型也可以保存Char类型的创建学生表
    private final String createTb="CREATE TABLE "+table+" ("+
            "ISBN CHAR(13) not null ,"+
            "title VARCHAR(20) not null,"+
            "author VARCHAR(20),"+
            "translator VARCHAR(20),"+
            "publisher VARCHAR(20),"+
            "time_year VARCHAR(5) ,"+
            "time_month VARCHAR(2) ,"+
            "read_state VARCHAR(10),"+
            "book_shelf VARCHAR(30),"+
            "note VARCHAR(30),"+
            "tag VARCHAR(30),"+
            "website VARCHAR(100),"+
            "img_bitmap BLOB"
            +")";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper(Context context){
        super(context, dbname, null, version);
    }

    public static SQLiteHelper getInstance(Context context) {

        if (dbHelper == null) { //单例模式
            dbHelper = new SQLiteHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建一个数据库表 User ，字段：_id、name、avatar。
        db.execSQL(createTb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}

