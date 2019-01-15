package com.gadogado.piter.Module.Moments;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.Module.Moments.ViewModel.ManageMomentViewModel;
import com.gadogado.piter.R;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageMomentActivity extends AppCompatActivity {

    public static SaveMomentListener listener;

    @BindView(R.id.layout_toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;

    @BindView(R.id.moments_title) EditText titleField;
    @BindView(R.id.moments_descripiton) EditText descriptionField;
    @BindView(R.id.moments_imagelayout) ConstraintLayout imageLayout;
    @BindView(R.id.moments_coverimage) ImageView coverImage;

    @BindView(R.id.moments_camera) ImageButton cameraButton;
    @BindView(R.id.moments_gallery) ImageButton galleryButton;
    @BindView(R.id.moments_clear) ImageButton clearButton;

    @BindView(R.id.moments_backgroundcolor) View backgroundColorView;

    ManageMomentViewModel manageMomentViewModel;
    private Context context = ManageMomentActivity.this;

    private final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private final int REQUEST_CODE_CAMERA = 1;
    private final int REQUEST_CODE_GALLERY = 2;
    private boolean accessCamera;

    public static final String INTENT_MANAGEMOMENT = "manageMoment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_moment);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(R.string.manage_moment);

        manageMomentViewModel = ViewModelProviders.of(this).get(ManageMomentViewModel.class);
        manageMomentViewModel.setSelectedTweets(getIntent().getIntegerArrayListExtra(INTENT_MANAGEMOMENT));

        backgroundColorView.setBackgroundColor(manageMomentViewModel.getSelectedColor());

        cameraButton.setOnClickListener(new ClickListener());
        galleryButton.setOnClickListener(new ClickListener());
        clearButton.setOnClickListener(new ClickListener());
        backgroundColorView.setOnClickListener(new ClickListener());

        if (manageMomentViewModel.getBitmap() == null && manageMomentViewModel.getUri() == null) {
            clearButton.setEnabled(false);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));

            coverImage.setVisibility(View.GONE);
        }
        else {
            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));

            coverImage.setVisibility(View.VISIBLE);

            if (manageMomentViewModel.getBitmap() != null) {
                coverImage.setImageBitmap(manageMomentViewModel.getBitmap());
            }

            if (manageMomentViewModel.getUri() != null) {
                coverImage.setImageURI(manageMomentViewModel.getUri());
            }
        }
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.moments_camera:
                    accessCamera = true;
                    getStoragePermission();

                    break;

                case R.id.moments_gallery:
                    accessCamera = false;
                    getStoragePermission();
                    break;

                case R.id.moments_clear:
                    manageMomentViewModel.setBitmap(null);
                    manageMomentViewModel.setUri(null);

                    coverImage.setVisibility(View.GONE);
                    clearButton.setEnabled(false);
                    clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.darker_gray));
                    break;

                case R.id.moments_backgroundcolor:
                    handleColorPicker();
                    break;
            }
        }
    }

    private void handleColorPicker() {
        int[] colorPickerColor = getResources().getIntArray(R.array.colorPicker);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colorPickerColor,
                manageMomentViewModel.getSelectedColor(),
                3,
                ColorPickerDialog.SIZE_SMALL,
                true,
                2,
                Color.BLACK
        );

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                manageMomentViewModel.setSelectedColor(color);
                backgroundColorView.setBackgroundColor(color);
            }
        });

        dialog.show(getFragmentManager(), "color_dialog_test");
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
            manageMomentViewModel.setBitmap(bitmap);
            coverImage.setImageBitmap(bitmap);
            coverImage.setVisibility(View.VISIBLE);

            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));
        }
        else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            manageMomentViewModel.setUri(uri);
            coverImage.setImageURI(uri);
            coverImage.setVisibility(View.VISIBLE);

            clearButton.setEnabled(true);
            clearButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorWarning));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.item_save) {
            doSave();
        }

        return super.onOptionsItemSelected(item);
    }

    private void doSave() {
        if (titleField.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, R.string.title_required, Toast.LENGTH_SHORT).show();
            return;
        }

        Utility.showOptionAlertDialog(context, R.string.save_moment, R.string.areyousure_savemoment, false,
                new Utility.DialogListener() {
                    @Override
                    public void executeYes() {
                        if (manageMomentViewModel.getBitmap() != null || manageMomentViewModel.getUri() != null) {
                            manageMomentViewModel.saveImage();
                        }

                        Collections.sort(manageMomentViewModel.getSelectedTweets());

                        StringBuilder tweetID = new StringBuilder();

                        for (int i = 0; i < manageMomentViewModel.getSelectedTweets().size(); i++) {
                            tweetID.append(manageMomentViewModel.getSelectedTweets().get(i));

                            if (i != manageMomentViewModel.getSelectedTweets().size() - 1) {
                                tweetID .append(", ");
                            }
                        }

                        manageMomentViewModel.addMoment(new Moment(titleField.getText().toString().trim(),
                                        descriptionField.getText().toString(),
                                        Utility.getCurrentDateTime(),
                                        tweetID.toString(),
                                        manageMomentViewModel.getFilename(),
                                        Utility.getColorHex(manageMomentViewModel.getSelectedColor()),
                                        Utility.getUserInfo(context).username),
                                new DatabaseListener() {
                                    @Override
                                    public void resultCallback(boolean result) {
                                        finish();
                                        listener.closeSelectTweet();
                                        Toast.makeText(context, R.string.moment_saved, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void executeNo() {

                    }
                });
    }
}
