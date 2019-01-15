package com.gadogado.piter.Module.Moments.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Module.Moments.ViewMomentFragment;
import com.google.gson.Gson;

import java.util.List;

public class ViewMomentViewPagerAdapter extends FragmentPagerAdapter {

    private List<Tweet> tweetList;
    private Moment moment;
    private Gson gson;

    public ViewMomentViewPagerAdapter(FragmentManager fragmentManager, Moment moment, List<Tweet> tweetList) {
        super(fragmentManager);
        this.moment = moment;
        this.tweetList = tweetList;
        gson = new Gson();
    }

    @Override
    public int getCount() {
        return tweetList.size() + 2;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ViewMomentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ViewMomentFragment.BUNDLE_MOMENT, gson.toJson(moment));

        if (position == 0) {
            bundle.putString(ViewMomentFragment.BUNDLE_MOMENTTWEET, ViewMomentFragment.MOMENT_START);
        }
        else if (position == tweetList.size() + 1) {
            bundle.putString(ViewMomentFragment.BUNDLE_MOMENTTWEET, ViewMomentFragment.MOMENT_FINISH);
        }
        else {
            bundle.putString(ViewMomentFragment.BUNDLE_MOMENTTWEET, gson.toJson(tweetList.get(position - 1)));
        }

        fragment.setArguments(bundle);
        return fragment;
    }
}
