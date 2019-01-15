package com.gadogado.piter.Helper.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.gadogado.piter.Helper.Database.DBConstant;

@Entity(tableName = DBConstant.TABLE_MOMENTS)
public class Moment {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = DBConstant.COL_ID)
    public int id;

    @ColumnInfo(name = DBConstant.COL_TITLE)
    public String title;

    @ColumnInfo(name = DBConstant.COL_DESCRIPTION)
    public String description;

    @ColumnInfo(name = DBConstant.COL_DATE)
    public String date;

    @ColumnInfo(name = DBConstant.COL_TWEETID)
    public String tweetID;

    @ColumnInfo(name = DBConstant.COL_IMAGE)
    public String image;

    @ColumnInfo(name = DBConstant.COL_COLOR)
    public String color;

    @ColumnInfo(name = DBConstant.COL_USERNAME)
    public String username;

    public Moment(String title, String description, String date, String tweetID, String image, String color, String username) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.tweetID = tweetID;
        this.image = image;
        this.color = color;
        this.username = username;
    }
}
