package com.gadogado.piter.Module.Moments.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;

import java.util.ArrayList;
import java.util.List;

public class SelectTweetsViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;
    private LiveData<List<Tweet>> tweets;

    private ArrayList<Integer> selectedTweets;

    public SelectTweetsViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application.getApplicationContext()).dbDao();
        tweets = dbDao.getAllTweets(Utility.getUserInfo(application.getApplicationContext()).username);
        selectedTweets = new ArrayList<>();
    }

    public LiveData<List<Tweet>> getTweets() {
        return tweets;
    }

    public ArrayList<Integer> getSelectedTweets() {
        return selectedTweets;
    }

    public void setSelectedTweets(ArrayList<Integer> selectedTweets) {
        this.selectedTweets = selectedTweets;
    }
}
