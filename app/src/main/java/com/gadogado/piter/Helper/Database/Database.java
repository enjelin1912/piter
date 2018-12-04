package com.gadogado.piter.Helper.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_DATE;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_HASHTAG;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_ID;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_IMAGE;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.COL_MESSAGE;
import static com.gadogado.piter.Helper.Database.DatabaseConstant.TABLE_TWEETS;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "piter.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTweetsTableQuery = "CREATE TABLE " + TABLE_TWEETS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MESSAGE + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_IMAGE + " TEXT, " +
                COL_HASHTAG + " TEXT)";

        db.execSQL(createTweetsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEETS);
        onCreate(db);
    }
}
