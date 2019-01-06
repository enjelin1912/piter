package com.gadogado.piter.Module.Home;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Constant;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.Module.Home.ViewModel.TweetViewModel;
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

    @BindView(R.id.layout_toolbar_twitter) Toolbar toolbar;
    @BindView(R.id.toolbar_tweet_button) Button tweetButton;

    @BindView(R.id.tweet_charcount) TextView charCountText;
    @BindView(R.id.tweet_charcount_bar) ProgressBar charCountBar;

    @BindView(R.id.tweet_textfield) EditText tweetField;
    @BindView(R.id.tweet_image) ImageView tweetImage;

    @BindView(R.id.tweet_camera) ImageButton cameraButton;
    @BindView(R.id.tweet_gallery) ImageButton galleryButton;
    @BindView(R.id.tweet_clear) ImageButton clearButton;

    private Context context = TweetActivity.this;
    private TweetViewModel tweetViewModel;

    private final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private final int REQUEST_CODE_CAMERA = 1;
    private final int REQUEST_CODE_GALLERY = 2;
    private boolean accessCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        ButterKnife.bind(this);

        tweetViewModel = ViewModelProviders.of(this).get(TweetViewModel.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);
        getSupportActionBar().setTitle(null);

        tweetButton.setOnClickListener(new ClickListener());
        cameraButton.setOnClickListener(new ClickListener());
        galleryButton.setOnClickListener(new ClickListener());
        clearButton.setOnClickListener(new ClickListener());

        tweetButton.setTextColor(Color.GRAY);
        tweetButton.setEnabled(false);

        if (tweetViewModel.getBitmap() == null && tweetViewModel.getUri() == null) {
            clearButton.setEnabled(false);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));

            tweetImage.setVisibility(View.GONE);
        }
        else {
            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));

            tweetImage.setVisibility(View.VISIBLE);

            if (tweetViewModel.getBitmap() != null) {
                tweetImage.setImageBitmap(tweetViewModel.getBitmap());
            }

            if (tweetViewModel.getUri() != null) {
                tweetImage.setImageURI(tweetViewModel.getUri());
            }
        }

        tweetField.addTextChangedListener(new TextChangedListener());
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
                    tweetViewModel.setBitmap(null);
                    tweetViewModel.setUri(null);

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
            Bitmap bitmap = (Bitmap) extras.get("data");
            tweetViewModel.setBitmap(bitmap);
            tweetImage.setImageBitmap(bitmap);
            tweetImage.setVisibility(View.VISIBLE);

            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));
        }
        else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            tweetViewModel.setUri(uri);
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
                        if (tweetViewModel.getBitmap() != null || tweetViewModel.getUri() != null) {
                            tweetViewModel.saveImage();
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

                        tweetViewModel.addTweet(new Tweet(tweetField.getText().toString().trim(),
                                        Utility.getCurrentDateTime(),
                                        tweetViewModel.getFilename(),
                                        tagString.toString(),
                                        Utility.getUserInfo(context).username),
                                new DatabaseListener() {
                                    @Override
                                    public void resultCallback(boolean result) {
                                        finish();
                                        Toast.makeText(context, R.string.tweet_saved, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void executeNo() {

                    }
                });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
