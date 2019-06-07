package com.example.administrator.materiatest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.example.administrator.materiatest.DB.BookBaseHelper;
import com.example.administrator.materiatest.DB.BookCursorWrapper;
import  com.example.administrator.materiatest.DB.BookDBSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        mDatabase = new BookBaseHelper(context).getWritableDatabase();
    }



    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs) {

        Cursor cursor = mDatabase.query(
                BookDBSchema.BookTable.NAME,//TableName
                null,//columns, null select all columns
                whereClause,//where
                whereArgs,//whereArgs
                null,//groupBy
                null,//having
                null//limit
        );
        // for log
        if (whereArgs == null) {
            whereArgs = new String[]{"null"};
        }
        if (whereClause == null) {
            whereClause = "null";
        }
        Log.i(TAG, "Query books whereClause = " + whereClause + ", whereArgs = " + Arrays.toString(whereArgs));

        return new BookCursorWrapper(cursor);

    }

    public Book getBook(UUID id) {
        BookCursorWrapper cursor = queryBooks(BookDBSchema.BookTable.Cols.UUID + "= ?",new String[]{id.toString()});

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getBook();

    }

    public List<Book> getBooks() {
        List<Book> mBooks = new ArrayList<>();

        BookCursorWrapper cursor = queryBooks(null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mBooks.add(cursor.getBook());
            cursor.moveToNext();
        }
        return mBooks;

    }

    /**
     * getBooks by bookshelfID and labelID, all the parameters can be null
     *
     * @param bookShelfID
     * @param labelID
     * @return result
     */
    public List<Book> getBooks(@Nullable UUID bookShelfID, @Nullable int labelID) {
        List<Book> mBooks = new ArrayList<>();
        String whereClause;
        String[] whereArgs;
        if (bookShelfID == null && labelID == 0) {
            return getBooks();
        } else if (bookShelfID == null) {
            // bookShelfID == null and labelID != null
            whereClause = BookDBSchema.BookTable.Cols.LABEL_ID + " GLOB ?";
            whereArgs = new String[]{"*" + labelID + "*"};
            // It is WRONG to write ... + "GLOB *?*",new String[](labelID.toString())
        } else if (labelID == 0) {
            // bookShelfID != null and labelID == null
            whereClause = BookDBSchema.BookTable.Cols.BOOKSHELF_ID + "= ?";
            whereArgs = new String[]{bookShelfID.toString()};
        } else {
            // bookShelfID != null and labelID != null
            whereClause = BookDBSchema.BookTable.Cols.BOOKSHELF_ID + "= ? AND "
                    + BookDBSchema.BookTable.Cols.LABEL_ID + " GLOB ?";
            whereArgs = new String[]{bookShelfID.toString(), "*" + labelID + "*"};
        }

        BookCursorWrapper cursor = queryBooks(whereClause, whereArgs);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mBooks.add(cursor.getBook());
            cursor.moveToNext();
        }

        return mBooks;
    }

    /**
     * Search books only supports search on bookshelf currently,
     * keyword will try to match title,authors,translators,publishers,note (case-insensitive)
     *
     * @param keyword     search keyword
     * @param bookshelfID bookshelf id, pass null if on all bookshelves
     * @return search result
     */
    public List<Book> searchBook(String keyword, @Nullable UUID bookshelfID) {
        List<Book> books = new ArrayList<>();
        String whereClause;
        String[] whereArgs;
        if (keyword == null) {
            return getBooks(bookshelfID, 0);
        }
        // in sql, "GLOB" is case-sensitive while "LIKE" is case-insensitive
        if (bookshelfID == null) {
            // on all bookshelves
            whereClause = BookDBSchema.BookTable.Cols.TITLE + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.AUTHORS + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.TRANSLATORS + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.PUBLISHER + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.NOTES + " LIKE ? ";
            whereArgs = new String[]{"%" + keyword + "%",
                    "%" + keyword + "%",
                    "%" + keyword + "%",
                    "%" + keyword + "%",
                    "%" + keyword + "%"};
        } else {
            // on specified bookshelf
            whereClause = BookDBSchema.BookTable.Cols.BOOKSHELF_ID + " = ? AND "
                    + BookDBSchema.BookTable.Cols.TITLE + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.AUTHORS + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.TRANSLATORS + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.PUBLISHER + " LIKE ? OR "
                    + BookDBSchema.BookTable.Cols.NOTES + " LIKE ? ";
            whereArgs = new String[]{bookshelfID.toString(),
                    "%" + keyword + "%",
                    "%" + keyword + "%",
                    "%" + keyword + "%",
                    "%" + keyword + "%",
                    "%" + keyword + "%"};
        }
        BookCursorWrapper cursor = queryBooks(whereClause, whereArgs);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            books.add(cursor.getBook());
            cursor.moveToNext();
        }

        return books;

    }




    public void addBook(Book book) {

    }

    public void addBooks(List<Book> books) {
        if (books != null) {
            for (Book book : books) {
                addBook(book);
            }
        }
    }


    public void deleteBook(Book book) {

    }


    public void updateBook(Book book) {

    }

}
