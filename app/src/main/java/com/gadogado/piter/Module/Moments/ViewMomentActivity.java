package com.gadogado.piter.Module.Moments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Module.Moments.Adapter.ViewMomentViewPagerAdapter;
import com.gadogado.piter.Module.Moments.ViewModel.ViewMomentViewModel;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMomentActivity extends AppCompatActivity {

    @BindView(R.id.moments_viewpager) ViewPager viewPager;

    private FragmentPagerAdapter adapter;
    ViewMomentViewModel viewMomentViewModel;
    private Context context = ViewMomentActivity.this;

    public static final String INTENT_VIEWMOMENT = "viewMoment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_moment);

        ButterKnife.bind(this);
        hideSystemUI();

        viewMomentViewModel = ViewModelProviders.of(this).get(ViewMomentViewModel.class);
        viewMomentViewModel.setMoment(new Gson().fromJson(getIntent().getStringExtra(INTENT_VIEWMOMENT), Moment.class));
        viewMomentViewModel.getTweets().observe(this, new Observer<List<Tweet>>() {
            @Override
            public void onChanged(@Nullable List<Tweet> tweetList) {
                List<String> tweetID = Arrays.asList(viewMomentViewModel.getMoment().tweetID.split(", "));
                List<Tweet> selectedTweet = new ArrayList<>();

                for (Tweet i : tweetList) {
                    if (tweetID.contains(String.valueOf(i.id))) {
                        selectedTweet.add(i);
                    }
                }

                viewMomentViewModel.setSelectedTweets(selectedTweet);

                adapter = new ViewMomentViewPagerAdapter(getSupportFragmentManager(), viewMomentViewModel.getMoment(),
                        viewMomentViewModel.getSelectedTweets());
                viewPager.setAdapter(adapter);
            }
        });
    }

    public void hideSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
