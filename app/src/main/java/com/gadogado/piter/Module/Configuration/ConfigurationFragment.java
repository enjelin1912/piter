package com.gadogado.piter.Module.Configuration;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigurationFragment extends Fragment {

    @BindView(R.id.configuration_name) EditText nameField;
    @BindView(R.id.configuration_savenamebutton) Button saveNameButton;

    @BindView(R.id.configuration_oldpassword) EditText oldPasswordField;
    @BindView(R.id.configuration_newpassword) EditText newPasswordField;
    @BindView(R.id.configuration_savepasswordbutton) Button savePasswordButton;

    @BindView(R.id.layout_loading) RelativeLayout loadingLayout;

    ConfigurationViewModel configurationViewModel;
    private User user;

    public ConfigurationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuration, container, false);

        ButterKnife.bind(this, view);

        configurationViewModel = ViewModelProviders.of(this).get(ConfigurationViewModel.class);

        nameField.addTextChangedListener(new TextChangedListener());
        oldPasswordField.addTextChangedListener(new TextChangedListener());
        newPasswordField.addTextChangedListener(new TextChangedListener());

        saveNameButton.setOnClickListener(new ClickListener());
        savePasswordButton.setOnClickListener(new ClickListener());


        return view;
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.configuration_savenamebutton:
                    handleSaveName();
                    break;

                case R.id.configuration_savepasswordbutton:
                    handleSavePassword();
                    break;
            }
        }
    }

    private void handleSaveName() {
        Utility.showOptionAlertDialog(getActivity(), R.string.change_name, R.string.areyousure_savename, true,
                new Utility.DialogListener() {
                    @Override
                    public void executeYes() {
                        processLoading(true);
                        user = Utility.getUserInfo(getActivity());
                        user.name = nameField.getText().toString().trim();

                        configurationViewModel.update(user,
                                new DatabaseListener() {
                                    @Override
                                    public void resultCallback(boolean result) {
                                        Utility.showActionAlertDialog(getActivity(), R.string.success, R.string.name_changed, false,
                                                new Utility.DialogListener() {
                                                    @Override
                                                    public void executeYes() {
                                                        LocalStorage.setItem(getActivity(), LocalStorage.USER_INFO,
                                                                new Gson().toJson(user));

                                                        processLoading(false);

                                                        nameField.setText("");
                                                        saveNameButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disable));
                                                        saveNameButton.setEnabled(false);
                                                    }

                                                    @Override
                                                    public void executeNo() {

                                                    }
                                                });
                                    }
                                });

                    }

                    @Override
                    public void executeNo() {

                    }
                });
    }

    private void handleSavePassword() {
        Utility.showOptionAlertDialog(getActivity(), R.string.change_password, R.string.areyousure_savepassword, true,
                new Utility.DialogListener() {
                    @Override
                    public void executeYes() {
                        user = Utility.getUserInfo(getActivity());

                        if (!oldPasswordField.getText().toString().trim().equals(user.password)) {
                            Utility.showAlertDialog(getActivity(), R.string.warning, R.string.wrong_password);
                            return;
                        }

                        user.password = newPasswordField.getText().toString().trim();

                        processLoading(true);


                        configurationViewModel.update(user,
                                new DatabaseListener() {
                                    @Override
                                    public void resultCallback(boolean result) {
                                        Utility.showActionAlertDialog(getActivity(), R.string.success, R.string.password_changed, false,
                                                new Utility.DialogListener() {
                                                    @Override
                                                    public void executeYes() {
                                                        LocalStorage.setItem(getActivity(), LocalStorage.USER_INFO,
                                                                new Gson().toJson(user));

                                                        processLoading(false);

                                                        oldPasswordField.setText("");
                                                        newPasswordField.setText("");
                                                        savePasswordButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disable));
                                                        savePasswordButton.setEnabled(false);
                                                    }

                                                    @Override
                                                    public void executeNo() {

                                                    }
                                                });
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
            String name = nameField.getText().toString().trim();
            String oldPassword = oldPasswordField.getText().toString().trim();
            String newPassword = newPasswordField.getText().toString().trim();

            if (name.isEmpty()) {
                saveNameButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disable));
                saveNameButton.setEnabled(false);
            }
            else {
                saveNameButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                saveNameButton.setEnabled(true);
            }

            if (oldPassword.length() < 6 || newPassword.length() < 6) {
                savePasswordButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_disable));
                savePasswordButton.setEnabled(false);
            }
            else {
                savePasswordButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                savePasswordButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void processLoading(boolean load) {
        nameField.setEnabled(!load);
        saveNameButton.setEnabled(!load);
        oldPasswordField.setEnabled(!load);
        newPasswordField.setEnabled(!load);
        savePasswordButton.setEnabled(!load);
        loadingLayout.setVisibility(load ? View.VISIBLE : View.GONE);
    }
}
