package com.gadogado.piter.Module;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private Context context = SplashActivity.this;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        gson = new Gson();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
