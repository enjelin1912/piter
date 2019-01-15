package com.gadogado.piter.Module.Moments.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.gadogado.piter.Helper.Database.DatabaseDAO;
import com.gadogado.piter.Helper.Database.RoomDB;
import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Module.DatabaseListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ManageMomentViewModel extends AndroidViewModel {

    private DatabaseDAO dbDao;
    private Application application;
    private List<Integer> selectedTweets;
    private Bitmap bitmap;
    private Uri uri;
    private String filename;

    public ManageMomentViewModel(Application application) {
        super(application);
        this.application = application;

        dbDao = RoomDB.getDatabase(application.getApplicationContext()).dbDao();

        bitmap = null;
        uri = null;
        filename = null;
    }

    public void addMoment(Moment moment, DatabaseListener listener) {
        new AddMomentAsyncTask(dbDao, listener).execute(moment);
    }

    public void setSelectedTweets(List<Integer> selectedTweets) {
        this.selectedTweets = selectedTweets;
    }

    public List<Integer> getSelectedTweets() {
        return selectedTweets;
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

    private static class AddMomentAsyncTask extends AsyncTask<Moment, Void, Void> {
        private DatabaseDAO dbDao;
        private DatabaseListener listener;

        AddMomentAsyncTask(DatabaseDAO dbDao, DatabaseListener listener) {
            this.dbDao = dbDao;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Moment... moments) {
            dbDao.addMoment(moments[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.resultCallback(true);
        }
    }
}
