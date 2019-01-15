package com.gadogado.piter.Module.Moments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Module.Moments.Adapter.SelectTweetsRecyclerViewAdapter;
import com.gadogado.piter.Module.Moments.ViewModel.SelectTweetsViewModel;
import com.gadogado.piter.Module.ViewImageActivity;
import com.gadogado.piter.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectTweetsActivity extends AppCompatActivity {

    @BindView(R.id.layout_toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.moments_recycler_view) RecyclerView recyclerView;

    private Context context = SelectTweetsActivity.this;
    SelectTweetsViewModel selectTweetsViewModel;

    private SelectTweetsRecyclerViewAdapter adapter;
    private List<Tweet> tweetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tweets);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);
        toolbarTitle.setText(R.string.select_tweets);

        tweetList = new ArrayList<>();

        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        selectTweetsViewModel = ViewModelProviders.of(this).get(SelectTweetsViewModel.class);
        selectTweetsViewModel.getTweets().observe(this, new Observer<List<Tweet>>() {
            @Override
            public void onChanged(@Nullable List<Tweet> tweets) {
                tweetList = tweets;
                adapter = assignAdapter();
                recyclerView.setAdapter(adapter);
            }
        });

        ManageMomentActivity.listener = new SaveMomentListener() {
            @Override
            public void closeSelectTweet() {
                finish();
            }
        };
    }

    private SelectTweetsRecyclerViewAdapter assignAdapter() {
        return new SelectTweetsRecyclerViewAdapter(context, tweetList, selectTweetsViewModel.getSelectedTweets(), new SelectTweetsRecyclerViewAdapter.SelectTweetsListener() {
            @Override
            public void viewImage(String imagePath) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra(ViewImageActivity.INTENT_VIEWIMAGE, imagePath);
                startActivity(intent);
            }

            @Override
            public void tweetChecked(ArrayList<Integer> selectedTweets) {
                selectTweetsViewModel.setSelectedTweets(selectedTweets);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.item_next) {
            if (selectTweetsViewModel.getSelectedTweets().size() == 0) {
                Toast.makeText(context, R.string.no_tweet_selected, Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(SelectTweetsActivity.this, ManageMomentActivity.class);
                intent.putIntegerArrayListExtra(ManageMomentActivity.INTENT_MANAGEMOMENT, selectTweetsViewModel.getSelectedTweets());
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
