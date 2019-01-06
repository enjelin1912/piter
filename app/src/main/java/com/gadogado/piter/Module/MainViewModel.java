package com.gadogado.piter.Module;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.v4.app.Fragment;

public class MainViewModel extends AndroidViewModel {

    private int currentID;

    public MainViewModel(Application application) {
        super(application);
        currentID = 0;
    }

    public int getCurrentID() {
        return currentID;
    }

    public void setCurrentID(int currentID) {
        this.currentID = currentID;
    }
}
