package com.gadogado.piter.Helper.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Model.User;

import java.util.List;

@Dao
public interface DatabaseDAO {

    @Insert
    void addUser(User user);

    @Delete
    void deleteUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM " + DBConstant.TABLE_USERS + " WHERE " + DBConstant.COL_USERNAME + " = :username")
    User getUser(String username);

    @Query("SELECT * FROM " + DBConstant.TABLE_USERS +
            " WHERE " + DBConstant.COL_USERNAME + " = :username AND " + DBConstant.COL_PASSWORD + " = :password")
    User getUser(String username, String password);

    @Insert
    void addTweet(Tweet tweet);

    @Delete
    void deleteTweet(Tweet tweet);

    @Query("SELECT * FROM " + DBConstant.TABLE_TWEETS +
            " WHERE " + DBConstant.COL_USERNAME + " = :username" +
            " ORDER BY " + DBConstant.COL_ID + " DESC")
    LiveData<List<Tweet>> getAllTweets(String username);

    @Query("SELECT * FROM " + DBConstant.TABLE_TWEETS +
            " WHERE " + DBConstant.COL_USERNAME + " = :username AND " + DBConstant.COL_HASHTAG + " LIKE :tag" +
            " ORDER BY " + DBConstant.COL_ID + " DESC")
    LiveData<List<Tweet>> getTweetsByTag(String username, String tag);

    @Insert
    void addMoment(Moment moment);

    @Delete
    void deleteMoment(Moment moment);

    @Query("SELECT * FROM " + DBConstant.TABLE_MOMENTS +
            " WHERE " + DBConstant.COL_USERNAME + " = :username" +
            " ORDER BY " + DBConstant.COL_ID + " DESC")
    LiveData<List<Moment>> getAllMoments(String username);


}
