package com.example.administrator.materiatest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BookLab {
    private static final String TAG = "BookLab";

    private Context mContext;
    private static BookLab sBookLab;
    private SQLiteDatabase mDatabase;

    public static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    public BookLab(Context context) {
        mContext = context.getApplicationContext();
        //mDatabase = new BookBaseHelper(context).getWritableDatabase();
    }
}
