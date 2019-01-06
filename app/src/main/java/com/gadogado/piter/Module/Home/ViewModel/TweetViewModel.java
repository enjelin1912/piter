package com.gadogado.piter.Module.Home.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Module.DatabaseListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TweetViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;
    private Application application;

    private Bitmap bitmap;
    private Uri uri;
    private String filename;

    public TweetViewModel(Application application) {
        super(application);
        dbDao = RoomDB.getDatabase(application.getApplicationContext()).dbDao();

        this.application = application;

        bitmap = null;
        uri = null;
        filename = null;
    }

    public void addTweet(Tweet tweet, DatabaseListener listener) {
        new AddTweetAsyncTask(dbDao, listener).execute(tweet);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getFilename() {
        return filename;
    }

    public void saveImage() {
        filename = "Piter_" +
                (new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Calendar.getInstance().getTime())) +
                ".jpg";

        File file = new File(application.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            if (bitmap == null) {
                bitmap = MediaStore.Images.Media.getBitmap(application.getApplicationContext().getContentResolver(), uri);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class AddTweetAsyncTask extends AsyncTask<Tweet, Void, Void> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        AddTweetAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Tweet... tweets) {
            dbDao.addTweet(tweets[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.resultCallback(true);
        }
    }
}
