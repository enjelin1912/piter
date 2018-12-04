package com.gadogado.piter.Module.Home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Constant;
import com.gadogado.piter.Helper.Database.DatabaseHelper;
import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetActivity extends AppCompatActivity {

    public interface TweetListener {
        void refreshList();
    }

    public static TweetListener listener;

    @BindView(R.id.layout_toolbar_twitter) Toolbar toolbar;
    @BindView(R.id.toolbar_tweet_close) ImageView closeButton;
    @BindView(R.id.toolbar_tweet_button) Button tweetButton;

    @BindView(R.id.tweet_charcount) TextView charCountText;
    @BindView(R.id.tweet_charcount_bar) ProgressBar charCountBar;

    @BindView(R.id.tweet_textfield) EditText tweetField;
    @BindView(R.id.tweet_image) ImageView tweetImage;

    @BindView(R.id.tweet_camera) ImageButton cameraButton;
    @BindView(R.id.tweet_gallery) ImageButton galleryButton;
    @BindView(R.id.tweet_clear) ImageButton clearButton;

    private Context context = TweetActivity.this;
    private Gson gson;
    private DatabaseHelper dbHelper;

    private final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private final int REQUEST_CODE_CAMERA = 1;
    private final int REQUEST_CODE_GALLERY = 2;
    private boolean accessCamera;

    private Bitmap bitmap;
    private Uri uri;
    private String filename = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        ButterKnife.bind(this);
        gson = new Gson();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        closeButton.setOnClickListener(new ClickListener());
        tweetButton.setOnClickListener(new ClickListener());
        cameraButton.setOnClickListener(new ClickListener());
        galleryButton.setOnClickListener(new ClickListener());
        clearButton.setOnClickListener(new ClickListener());

        tweetButton.setTextColor(Color.GRAY);
        tweetButton.setEnabled(false);
        clearButton.setEnabled(false);

        tweetField.addTextChangedListener(new TextChangedListener());
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toolbar_tweet_close:
                    finish();
                    break;

                case R.id.toolbar_tweet_button:
                    handleSaveTweet();
                    break;

                case R.id.tweet_camera:
                    accessCamera = true;
                    getStoragePermission();
                    break;

                case R.id.tweet_gallery:
                    accessCamera = false;
                    getStoragePermission();
                    break;

                case R.id.tweet_clear:
                    bitmap = null;
                    uri = null;

                    tweetImage.setVisibility(View.GONE);
                    clearButton.setEnabled(false);
                    clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));
                    break;
            }
        }
    }

    private void getStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (accessCamera) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
            else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStoragePermission();
            }
            else {
                Utility.showAlertDialog(context, R.string.access_denied, R.string.storage_permissionrequired);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            tweetImage.setImageBitmap(bitmap);
            tweetImage.setVisibility(View.VISIBLE);

            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));
        }
        else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            tweetImage.setImageURI(uri);
            tweetImage.setVisibility(View.VISIBLE);

            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));
        }
    }

    private void handleSaveTweet() {
        Utility.showOptionAlertDialog(context, R.string.save_tweet, R.string.areyousure_savetweet, false,
                new Utility.DialogListener() {
                    @Override
                    public void executeYes() {
                        if (bitmap != null || uri != null) {
                            saveImage();
                        }

                        StringBuilder tagString = new StringBuilder();

                        if (Utility.getTags(tweetField.getText().toString()).size() > 0) {
                            List<String> list = Utility.getTags(tweetField.getText().toString());
                            for (int i = 0; i < list.size(); i++) {
                                tagString.append(list.get(i));

                                if (i != list.size() - 1) {
                                    tagString .append(Constant.HASHTAG_DIVIDER);
                                }
                            }
                        }

                        dbHelper = new DatabaseHelper(context);
                        dbHelper.addTweet(tweetField.getText().toString(), filename, tagString.toString());
                        finish();
                        listener.refreshList();
                    }

                    @Override
                    public void executeNo() {

                    }
                });
    }

    private void saveImage() {
        filename = "Piter_" +
                (new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Calendar.getInstance().getTime())) +
                ".jpg";

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            if (bitmap == null) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = tweetField.getText().toString();

            if (text.isEmpty()) {
                tweetButton.setTextColor(Color.GRAY);
                tweetButton.setEnabled(false);
            }
            else if (text.length() > Constant.TWEET_CHARLIMIT) {
                tweetButton.setTextColor(Color.GRAY);
                tweetButton.setEnabled(false);
                charCountText.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
            }
            else {
                tweetButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tweetButton.setEnabled(true);
                charCountText.setTextColor(Color.BLACK);
            }

            charCountText.setText(String.valueOf(text.length()));
            charCountBar.setProgress(text.length() * 100 / Constant.TWEET_CHARLIMIT);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
