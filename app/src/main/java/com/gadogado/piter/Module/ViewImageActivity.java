package com.gadogado.piter.Module;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.gadogado.piter.R;
import com.jsibbold.zoomage.ZoomageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewImageActivity extends AppCompatActivity {

    @BindView(R.id.viewimage_image) ZoomageView image;

    public static final String INTENT_VIEWIMAGE = "intentViewImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ButterKnife.bind(this);

        Glide.with(this).load(getIntent().getStringExtra(INTENT_VIEWIMAGE)).into(image);
    }
}
