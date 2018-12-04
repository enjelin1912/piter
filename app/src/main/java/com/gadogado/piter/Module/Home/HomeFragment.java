package com.gadogado.piter.Module.Home;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Database.Database;
import com.gadogado.piter.Helper.Database.DatabaseConstant;
import com.gadogado.piter.Helper.Database.DatabaseHelper;
import com.gadogado.piter.Helper.LocalStorage;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.ViewImageActivity;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.home_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.home_add) FloatingActionButton tweetButton;
    @BindView(R.id.home_notweetfound) TextView noTweetFoundText;

    private HomeRecyclerViewAdapter adapter;
    private List<Tweet> tweetList;

    private DatabaseHelper dbHelper;
    private Cursor cursor;
    private RetrieveTweets retrieveTweetsTask;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 && tweetButton.isShown()) {
                    tweetButton.hide();
                }

                if (dy < 0) {
                    tweetButton.show();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        tweetButton.setVisibility(View.VISIBLE);

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetActivity.listener = new TweetActivity.TweetListener() {
                    @Override
                    public void refreshList() {
                        Toast.makeText(getActivity(), R.string.tweet_saved, Toast.LENGTH_SHORT).show();
                        retrieveTweetsTask = new RetrieveTweets();
                        retrieveTweetsTask.execute();
                    }
                };

                Intent intent = new Intent(getActivity(), TweetActivity.class);
                startActivity(intent);
            }
        });

        retrieveTweetsTask = new RetrieveTweets();
        retrieveTweetsTask.execute();

        return view;
    }

    private HomeRecyclerViewAdapter assignAdapter() {
        return new HomeRecyclerViewAdapter(getActivity(), tweetList, new HomeRecyclerViewAdapter.HomeListListener() {
            @Override
            public void viewImage(String imagePath) {
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.putExtra(ViewImageActivity.INTENT_VIEWIMAGE, imagePath);
                startActivity(intent);
            }

            @Override
            public void searchHashtag(String hashtag) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(SearchActivity.INTENT_SEARCHTAG, hashtag);
                startActivity(intent);
            }
        });
    }

    private class RetrieveTweets extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbHelper = new DatabaseHelper(getActivity());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            tweetList = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            cursor = dbHelper.getAllTweets();

            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    tweetList.add(new Tweet(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstant.COL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstant.COL_MESSAGE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstant.COL_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstant.COL_IMAGE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstant.COL_HASHTAG))));
                }

                adapter = assignAdapter();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dbHelper.close();
            if (tweetList.size() != 0) {
                recyclerView.setAdapter(adapter);

                noTweetFoundText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeRecylerView());
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
            else {
                noTweetFoundText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
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
            background.setColor(ContextCompat.getColor(getActivity(), R.color.colorWarning));
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            Drawable deleteIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_24dp);

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
                Utility.showOptionAlertDialog(getActivity(), R.string.delete_tweet, R.string.areyousure_deletetweet, false,
                        new Utility.DialogListener() {
                            @Override
                            public void executeYes() {
                                dbHelper = new DatabaseHelper(getActivity());
                                dbHelper.deleteTweet(adapter.getCurrentTweet(viewHolder.getAdapterPosition()).getTweetID());
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            }

                            @Override
                            public void executeNo() {
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        });
            }
        }
    }

    @Override
    public void onStop() {
        if (cursor != null) {
            cursor.close();
        }

        if (retrieveTweetsTask != null && retrieveTweetsTask.getStatus() == AsyncTask.Status.RUNNING) {
            retrieveTweetsTask.cancel(true);
        }

        retrieveTweetsTask = null;

        super.onStop();
    }
}
