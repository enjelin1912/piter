package com.gadogado.piter.Module.Authentication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.Authentication.ViewModel.RegisterViewModel;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.layout_toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.layout_loading) RelativeLayout loadingLayout;

    @BindView(R.id.register_name) EditText nameField;
    @BindView(R.id.register_username) EditText usernameField;
    @BindView(R.id.register_password) EditText passwordField;

    @BindView(R.id.register_signup) Button signUpButton;

    private Context context = RegisterActivity.this;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        toolbarTitle.setText(R.string.sign_up);

        nameField.addTextChangedListener(new TextChangedListener());
        usernameField.addTextChangedListener(new TextChangedListener());
        passwordField.addTextChangedListener(new TextChangedListener());

        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleSignUp(View view) {
        final String name = nameField.getText().toString().trim();
        final String username = usernameField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();

        processLoading(true);

        registerViewModel.checkUserAvailability(username, new DatabaseListener() {
            @Override
            public void resultCallback(boolean result) {
                processLoading(false);

                if (!result) {
                    Utility.showAlertDialog(context, R.string.warning, R.string.username_used);
                }
                else {
                    registerViewModel.insert(new User(username, password, name), new DatabaseListener() {
                        @Override
                        public void resultCallback(boolean result) {
                            Utility.showActionAlertDialog(context, R.string.success, R.string.you_are_registered, false,
                                    new Utility.DialogListener() {
                                        @Override
                                        public void executeYes() {
                                            finish();
                                        }

                                        @Override
                                        public void executeNo() {

                                        }
                                    });
                        }
                    });
                }
            }
        });
    }

    private class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = nameField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (name.length() < 1 || username.length() < 6 || password.length() < 6) {
                signUpButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disable));
                signUpButton.setEnabled(false);
            }
            else {
                signUpButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                signUpButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void processLoading(boolean load) {
        nameField.setEnabled(!load);
        usernameField.setEnabled(!load);
        passwordField.setEnabled(!load);
        signUpButton.setEnabled(!load);
        loadingLayout.setVisibility(load ? View.VISIBLE : View.GONE);
    }
}
