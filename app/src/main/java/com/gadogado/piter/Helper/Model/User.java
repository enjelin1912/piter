package com.gadogado.piter.Helper.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.gadogado.piter.Helper.Database.DBConstant;

@Entity(tableName = DBConstant.TABLE_USERS)
public class User {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = DBConstant.COL_ID)
    public int id;

    @ColumnInfo(name = DBConstant.COL_USERNAME)
    public String username;

    @ColumnInfo(name = DBConstant.COL_PASSWORD)
    public String password;

    @ColumnInfo(name = DBConstant.COL_NAME)
    public String name;

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}
