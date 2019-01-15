package com.gadogado.piter.Module.Moments.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;

import java.util.Arrays;
import java.util.List;

public class ViewMomentViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;
    private LiveData<List<Tweet>> tweets;
    private List<Tweet> selectedTweets;

    private Moment moment;

    public ViewMomentViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application.getApplicationContext()).dbDao();
        tweets = dbDao.getAllTweets(Utility.getUserInfo(application.getApplicationContext()).username);

        moment = null;
    }

    public LiveData<List<Tweet>> getTweets() {
        return tweets;
    }

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    public List<Tweet> getSelectedTweets() {
        return selectedTweets;
    }

    public void setSelectedTweets(List<Tweet> selectedTweets) {
        this.selectedTweets = selectedTweets;
    }
}
