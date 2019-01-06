package com.gadogado.piter.Module.Authentication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Module.MainActivity;
import com.gadogado.piter.R;
import com.google.gson.Gson;

public class SplashActivity extends AppCompatActivity {

    private Context context = SplashActivity.this;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (LocalStorage.getItem(context, LocalStorage.USER_INFO) == null) {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(intent);
    }
}
