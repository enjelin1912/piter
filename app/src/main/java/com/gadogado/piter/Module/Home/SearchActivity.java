package com.gadogado.piter.Module.Home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Database.DBConstant;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.Module.Home.ViewModel.SearchViewModel;
import com.gadogado.piter.Module.ViewImageActivity;
import com.gadogado.piter.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.search_notweetfound) TextView noTweetFoundText;

    private HomeRecyclerViewAdapter adapter;

    private Context context = SearchActivity.this;
    private SearchViewModel searchViewModel;

    public static final String INTENT_SEARCHTAG = "intentSearchTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        searchViewModel.setTag(getIntent().getStringExtra(INTENT_SEARCHTAG));
        searchViewModel.getTweets().observe(this, new Observer<List<Tweet>>() {
            @Override
            public void onChanged(@Nullable List<Tweet> tweets) {
                adapter = assignAdapter(tweets);
                recyclerView.setAdapter(adapter);

                if (tweets.size() == 0) {
                    noTweetFoundText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    noTweetFoundText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeRecylerView());
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                }
            }
        });
    }

    private HomeRecyclerViewAdapter assignAdapter(List<Tweet> tweetList) {
        return new HomeRecyclerViewAdapter(context, tweetList, new HomeRecyclerViewAdapter.HomeListListener() {
            @Override
            public void viewImage(String imagePath) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra(ViewImageActivity.INTENT_VIEWIMAGE, imagePath);
                startActivity(intent);
            }

            @Override
            public void searchHashtag(String hashtag) {
                if (hashtag.equals(searchViewModel.getTag())) {
                    Toast.makeText(context, R.string.already_viewing_tag, Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra(SearchActivity.INTENT_SEARCHTAG, hashtag);
                    startActivity(intent);
                }
            }
        });
    }

    private class SwipeRecylerView extends ItemTouchHelper.SimpleCallback {

        private SwipeRecylerView() {
            super(0, ItemTouchHelper.START);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getBottom() - itemView.getTop();

            ColorDrawable background = new ColorDrawable();
            background.setColor(ContextCompat.getColor(context, R.color.colorWarning));
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            Drawable deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24dp);

            int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconMargin = deleteIcon.getIntrinsicHeight();
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.START) {
                Utility.showOptionAlertDialog(context, R.string.delete_tweet, R.string.areyousure_deletetweet, false,
                        new Utility.DialogListener() {
                            @Override
                            public void executeYes() {
                                searchViewModel.deleteTweet(adapter.getCurrentTweet(viewHolder.getAdapterPosition()),
                                        new DatabaseListener() {
                                            @Override
                                            public void resultCallback(boolean result) {
                                                Toast.makeText(context, R.string.tweet_deleted, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void executeNo() {
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        });
            }
        }
    }
}
