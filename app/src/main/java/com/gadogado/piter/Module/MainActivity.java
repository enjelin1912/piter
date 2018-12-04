package com.gadogado.piter.Module;

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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gadogado.piter.Module.About.AboutFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        getSupportActionBar().setTitle(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                setFragment(item.getItemId());
                return true;
            }
        });

        navigationView.setCheckedItem(R.id.nav_home);
        setFragment(R.id.nav_home);
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
