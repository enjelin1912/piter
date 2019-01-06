package com.gadogado.piter.Module.Authentication;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.Authentication.ViewModel.LoginViewModel;
import com.gadogado.piter.Module.MainActivity;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.layout_loading) RelativeLayout loadingLayout;

    @BindView(R.id.login_username) EditText usernameField;
    @BindView(R.id.login_password) EditText passwordField;
    @BindView(R.id.login_signin) Button signInButton;
    @BindView(R.id.login_signup) TextView signUpButton;

    private Context context = LoginActivity.this;
    private LoginViewModel loginViewModel;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        gson = new Gson();

        usernameField.addTextChangedListener(new TextChangedListener());
        passwordField.addTextChangedListener(new TextChangedListener());

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    public void handleSignIn(View view) {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        processLoading(true);

        loginViewModel.checkUserAvailability(new User(username, password, ""), new LoginListener() {
            @Override
            public void getUser(User user) {
                processLoading(false);

                if (user == null) {
                    Utility.showAlertDialog(context, R.string.warning, R.string.username_password_invalid);
                }
                else {
                    LocalStorage.setItem(context, LocalStorage.USER_INFO, gson.toJson(user));
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (username.length() < 6 || password.length() < 6) {
                signInButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disable));
                signInButton.setEnabled(false);
            }
            else {
                signInButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                signInButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void processLoading(boolean load) {
        usernameField.setEnabled(!load);
        passwordField.setEnabled(!load);
        signInButton.setEnabled(!load);
        signUpButton.setEnabled(!load);
        loadingLayout.setVisibility(load ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
