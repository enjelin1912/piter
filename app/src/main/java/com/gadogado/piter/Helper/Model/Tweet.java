package com.gadogado.piter.Helper.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.gadogado.piter.Helper.Database.DBConstant;

@Entity(tableName = DBConstant.TABLE_TWEETS)
public class Tweet {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = DBConstant.COL_ID)
    public int id;

    @ColumnInfo(name = DBConstant.COL_MESSAGE)
    public String message;

    @ColumnInfo(name = DBConstant.COL_DATE)
    public String date;

    @ColumnInfo(name = DBConstant.COL_IMAGE)
    public String image;

    @ColumnInfo(name = DBConstant.COL_HASHTAG)
    public String hashtag;

    @ColumnInfo(name = DBConstant.COL_USERNAME)
    public String username;

    public Tweet(String message, String date, String image, String hashtag, String username) {
        this.message = message;
        this.date = date;
        this.image = image;
        this.hashtag = hashtag;
        this.username = username;
    }
}
