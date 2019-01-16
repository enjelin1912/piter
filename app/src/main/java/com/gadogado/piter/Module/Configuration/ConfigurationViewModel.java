package com.gadogado.piter.Module.Configuration;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;

public class ConfigurationViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;

    public ConfigurationViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application).dbDao();
    }

    public void update(User user, DatabaseListener listener) {
        new UpdateUserAsyncTask(dbDao, listener).execute(user);
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        UpdateUserAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(User... users) {
            dbDao.updateUser(users[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.resultCallback(true);
        }
    }
}
