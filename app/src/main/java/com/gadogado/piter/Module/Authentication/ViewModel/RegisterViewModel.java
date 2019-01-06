package com.gadogado.piter.Module.Authentication.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Module.DatabaseListener;

public class RegisterViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;

    public RegisterViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application).dbDao();
    }

    public void checkUserAvailability(String username, DatabaseListener listener) {
        new CheckUserAvailabilityAsyncTask(dbDao, listener).execute(username);
    }

    public void insert(User user, DatabaseListener listener) {
        new InsertUserAsyncTask(dbDao, listener).execute(user);
    }

    private static class CheckUserAvailabilityAsyncTask extends AsyncTask<String, Void, User> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        CheckUserAvailabilityAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(String... strings) {
            return dbDao.getUser(strings[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.resultCallback(user == null);
        }
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        InsertUserAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(User... users) {
            dbDao.addUser(users[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.resultCallback(true);
        }
    }
}
