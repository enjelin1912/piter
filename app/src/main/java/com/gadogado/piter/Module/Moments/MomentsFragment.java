package com.gadogado.piter.Module.Moments;


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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.Module.DatabaseListener;
import com.gadogado.piter.Module.Moments.ViewModel.MomentsViewModel;
import com.gadogado.piter.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MomentsFragment extends Fragment {

    @BindView(R.id.moments_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.moments_add) FloatingActionButton addMomentButton;
    @BindView(R.id.moments_nomomentfound) TextView noMomentFoundText;

    private MomentsViewModel momentsViewModel;
    private MomentsRecyclerViewAdapter adapter;
    private List<Moment> momentList;

    public MomentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_moments, container, false);

        ButterKnife.bind(this, view);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(new ScrollListener());

        momentList = new ArrayList<>();

        momentsViewModel = ViewModelProviders.of(this).get(MomentsViewModel.class);
        momentsViewModel.getMoments().observe(this, new Observer<List<Moment>>() {
            @Override
            public void onChanged(@Nullable List<Moment> moments) {
                momentList = moments;
                adapter = assignAdapter();
                recyclerView.setAdapter(adapter);

                if (moments.size() == 0) {
                    noMomentFoundText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    noMomentFoundText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeRecylerView());
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                }
            }
        });

        addMomentButton.setVisibility(View.VISIBLE);
        addMomentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectTweetsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private MomentsRecyclerViewAdapter assignAdapter() {
        return new MomentsRecyclerViewAdapter(getActivity(), momentList);
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if (dy > 0 && addMomentButton.isShown()) {
                addMomentButton.hide();
            }

            if (dy < 0) {
                addMomentButton.show();
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
                                momentsViewModel.deleteMoment(adapter.getCurrentMoment(viewHolder.getAdapterPosition()),
                                        new DatabaseListener() {
                                            @Override
                                            public void resultCallback(boolean result) {
                                                Toast.makeText(getActivity(), R.string.moment_deleted, Toast.LENGTH_SHORT).show();
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
