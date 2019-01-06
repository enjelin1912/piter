package com.gadogado.piter.Module.Home.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;
    private Application application;
    private LiveData<List<Tweet>> tweets;
    private String tag;

    public SearchViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application.getApplicationContext()).dbDao();
        this.application = application;
    }

    public LiveData<List<Tweet>> getTweets() {
        return tweets;
    }

    public void deleteTweet(Tweet tweet, DatabaseListener listener) {
        new DeleteTweetAsyncTask(dbDao, listener).execute(tweet);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        tweets = dbDao.getTweetsByTag(Utility.getUserInfo(application.getApplicationContext()).username, "%"+tag+"%");
    }

    private static class DeleteTweetAsyncTask extends AsyncTask<Tweet, Void, Void> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        DeleteTweetAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Tweet... tweets) {
            dbDao.deleteTweet(tweets[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.resultCallback(true);
        }
    }
}
