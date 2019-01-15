package com.gadogado.piter.Module.Moments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewMomentFragment extends Fragment {

    @BindView(R.id.moments_close) ImageView momentCloseButton;
    @BindView(R.id.moments_layout) RelativeLayout momentLayout;
    @BindView(R.id.moments_background) ImageView momentBackground;
    @BindView(R.id.moments_tweetlayout) LinearLayout momentTweetLayout;
    @BindView(R.id.moments_date) TextView tweetDate;
    @BindView(R.id.moments_tweet) TextView tweetMessage;

    public static final String BUNDLE_MOMENTTWEET = "momentTweet";
    public static final String BUNDLE_MOMENT = "moment";

    public static final String MOMENT_START = "0";
    public static final String MOMENT_FINISH = "1";

    public ViewMomentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_moment, container, false);

        ButterKnife.bind(this, view);

        Moment moment = new Gson().fromJson(getArguments().getString(BUNDLE_MOMENT), Moment.class);

        if (getArguments().getString(BUNDLE_MOMENTTWEET).equals(MOMENT_START)) {
            tweetDate.setText(moment.title);
            tweetMessage.setText(moment.description);

            if (moment.image != null) {
                momentBackground.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + moment.image)
                        .into(momentBackground);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) momentTweetLayout.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            else {
                momentBackground.setVisibility(View.GONE);
                momentLayout.setBackgroundColor(Color.parseColor(moment.color));
            }

        }
        else if (getArguments().getString(BUNDLE_MOMENTTWEET).equals(MOMENT_FINISH)) {
            momentBackground.setVisibility(View.GONE);
            momentLayout.setBackgroundColor(Color.parseColor(moment.color));

            tweetMessage.setText(R.string.finish);
        }
        else {
            Tweet tweet = new Gson().fromJson(getArguments().getString(BUNDLE_MOMENTTWEET), Tweet.class);
            tweetMessage.setText(tweet.message);
            tweetDate.setText(Utility.getDateTimeFormat(tweet.date));

            if (tweet.image != null) {
                momentBackground.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + tweet.image)
                        .into(momentBackground);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) momentTweetLayout.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            else {
                momentBackground.setVisibility(View.GONE);
                momentLayout.setBackgroundColor(Color.parseColor(moment.color));
            }
        }

        momentCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

}
