package com.gadogado.piter.Module.Authentication.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Module.Authentication.LoginListener;

public class LoginViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;

    public LoginViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application).dbDao();
    }

    public void checkUserAvailability(User user, LoginListener listener) {
        new CheckUserAvailabilityAsyncTask(dbDao, listener).execute(user);
    }

    private static class CheckUserAvailabilityAsyncTask extends AsyncTask<User, Void, User> {
        private DatabaseDAO dbDao;
        private LoginListener listener;

        CheckUserAvailabilityAsyncTask(DatabaseDAO dbDao, LoginListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(User... users) {
            return dbDao.getUser(users[0].username, users[0].password);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.getUser(user);
        }
    }
}
