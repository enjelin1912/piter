package com.gadogado.piter.Module;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gadogado.piter.Helper.Model.User;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.About.AboutFragment;
import com.gadogado.piter.Module.Authentication.LoginActivity;
import com.gadogado.piter.Module.Configuration.ConfigurationFragment;
import com.gadogado.piter.Module.ContactUs.ContactUsFragment;
import com.gadogado.piter.Module.Home.HomeFragment;
import com.gadogado.piter.Module.Moments.MomentsFragment;
import com.gadogado.piter.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.layout_toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.main_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_nav_view) NavigationView navigationView;
    @BindView(R.id.main_frame_layout) FrameLayout frameLayout;

    private MainViewModel mainViewModel;
    private Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        getSupportActionBar().setTitle(null);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        User user = Utility.getUserInfo(context);
        View drawerHeader = navigationView.getHeaderView(0);
        TextView headerName = drawerHeader.findViewById(R.id.drawer_header_name);
        TextView headerUsername = drawerHeader.findViewById(R.id.drawer_header_username);
        headerName.setText(user.name);
        headerUsername.setText("@".concat(user.username));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_signout) {
                    Utility.showOptionAlertDialog(context, R.string.sign_out, R.string.signout_areyousure, true,
                            new Utility.DialogListener() {
                                @Override
                                public void executeYes() {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void executeNo() {

                                }
                            });

                    return false;
                }

                item.setChecked(true);
                drawerLayout.closeDrawers();

                setFragment(item.getItemId());
                return true;
            }
        });

        if (mainViewModel.getCurrentID() == 0) {
            navigationView.setCheckedItem(R.id.nav_home);
            setFragment(R.id.nav_home);
        }
        else {
            navigationView.setCheckedItem(mainViewModel.getCurrentID());
            setFragment(mainViewModel.getCurrentID());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFragment(int id) {
        Fragment fragment;

        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                toolbarTitle.setText(R.string.app_name);
                break;

            case R.id.nav_moments:
                fragment = new MomentsFragment();
                toolbarTitle.setText(R.string.moments);
                break;

            case R.id.nav_configuration:
                fragment = new ConfigurationFragment();
                toolbarTitle.setText(R.string.configuration);
                break;

            case R.id.nav_about:
                fragment = new AboutFragment();
                toolbarTitle.setText(R.string.about);
                break;

            case R.id.nav_contactus:
                fragment = new ContactUsFragment();
                toolbarTitle.setText(R.string.contact_us);
                break;

            default:
                fragment = new HomeFragment();
                toolbarTitle.setText(R.string.app_name);
                break;
        }

        mainViewModel.setCurrentID(id);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.main_frame_layout, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (toolbarTitle.getText().toString().equals(getResources().getString(R.string.app_name))) {
            moveTaskToBack(true);
        }
        else {
            navigationView.setCheckedItem(R.id.nav_home);
            setFragment(R.id.nav_home);
        }
    }
}
