package com.gadogado.piter.Module.Moments.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;

import java.util.List;

public class MomentsViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;
    private LiveData<List<Moment>> moments;

    public MomentsViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application.getApplicationContext()).dbDao();
        moments = dbDao.getAllMoments(Utility.getUserInfo(application.getApplicationContext()).username);
    }

    public LiveData<List<Moment>> getMoments() {
        return moments;
    }

    public void deleteMoment(Moment moment, DatabaseListener listener) {
        new DeleteMomentAsyncTask(dbDao, listener).execute(moment);
    }

    private static class DeleteMomentAsyncTask extends AsyncTask<Moment, Void, Void> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        DeleteMomentAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Moment... moments) {
            dbDao.deleteMoment(moments[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.resultCallback(true);
        }
    }
}
