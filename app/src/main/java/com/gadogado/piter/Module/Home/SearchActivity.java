package com.gadogado.piter.Module.Home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Database.DatabaseConstant;
import com.gadogado.piter.Helper.Database.DatabaseHelper;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
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
    private List<Tweet> tweetList;

    private DatabaseHelper dbHelper;
    private Cursor cursor;
    private RetrieveTweets retrieveTweetsTask;

    private Context context = SearchActivity.this;

    public static final String INTENT_SEARCHTAG = "intentSearchTag";
    private String searchedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        searchedTag = getIntent().getStringExtra(INTENT_SEARCHTAG);

        retrieveTweetsTask = new RetrieveTweets();
        retrieveTweetsTask.execute();
    }

    private HomeRecyclerViewAdapter assignAdapter() {
        return new HomeRecyclerViewAdapter(context, tweetList, new HomeRecyclerViewAdapter.HomeListListener() {
            @Override
            public void viewImage(String imagePath) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra(ViewImageActivity.INTENT_VIEWIMAGE, imagePath);
                startActivity(intent);
            }

            @Override
            public void searchHashtag(String hashtag) {
                if (hashtag.equals(searchedTag)) {
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

    private class RetrieveTweets extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbHelper = new DatabaseHelper(context);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            tweetList = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            cursor = dbHelper.getTweetsByTag(searchedTag);

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
                                dbHelper = new DatabaseHelper(context);
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
