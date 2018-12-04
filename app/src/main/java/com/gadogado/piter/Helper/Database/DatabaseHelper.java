package com.gadogado.piter.Helper.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_DATE;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_HASHTAG;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_ID;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_IMAGE;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_MESSAGE;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.TABLE_TWEETS;

public class DatabaseHelper extends Database {

    public DatabaseHelper(Context context) {
        super(context);
    }

    public Cursor getAllTweets() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TWEETS + " ORDER BY " + COL_ID + " DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getTweetsByTag(String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_TWEETS, new String[] {COL_ID, COL_MESSAGE, COL_DATE, COL_IMAGE, COL_HASHTAG},
                COL_HASHTAG + " LIKE ?", new String[] { "%"+tag+"%" },
                null, null, COL_ID + " DESC", null);
    }

    public void addTweet(String message, String image, String hashtag) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_MESSAGE, message);
        cv.put(COL_DATE, date);
        cv.put(COL_IMAGE, image);
        cv.put(COL_HASHTAG, hashtag);
        db.insert(TABLE_TWEETS, null, cv);
    }

    public void deleteTweet(int tweetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = TABLE_TWEETS;
        String whereClause =  COL_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(tweetID)};
        db.delete(table, whereClause, whereArgs);
    }
}
