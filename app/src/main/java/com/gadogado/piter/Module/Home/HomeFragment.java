package com.gadogado.piter.Module.Home;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.Module.Home.ViewModel.HomeViewModel;
import com.gadogado.piter.Module.ViewImageActivity;
import com.gadogado.piter.R;

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
    private HomeViewModel homeViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(new ScrollListener());

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getTweets().observe(this, new Observer<List<Tweet>>() {
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

        tweetButton.setVisibility(View.VISIBLE);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TweetActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private HomeRecyclerViewAdapter assignAdapter(List<Tweet> tweetList) {
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

    private class ScrollListener extends RecyclerView.OnScrollListener {
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
                                homeViewModel.deleteTweet(adapter.getCurrentTweet(viewHolder.getAdapterPosition()),
                                        new DatabaseListener() {
                                            @Override
                                            public void resultCallback(boolean result) {
                                                Toast.makeText(getActivity(), R.string.tweet_deleted, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_search) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
