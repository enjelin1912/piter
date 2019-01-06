package com.gadogado.piter.Module.Home;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {

    public interface HomeListListener {
        void viewImage(String imagePath);
        void searchHashtag(String hashtag);
    }

    private HomeListListener listener;
    private List<Tweet> list;
    private Context context;

    public HomeRecyclerViewAdapter(Context context, List<Tweet> list, HomeListListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Tweet tweet = list.get(position);

        holder.date.setText(Utility.getDateTimeFormat(tweet.date));

        holder.message.setText(Utility.setTags(context, tweet.message, new Utility.HashtagListener() {
            @Override
            public void performSearch(String hashtag) {
                listener.searchHashtag(hashtag);
            }
        }));

        holder.message.setMovementMethod(LinkMovementMethod.getInstance());

        if (tweet.image != null) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + tweet.image)
                    .into(holder.image);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.viewImage(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
                            + "/"
                            + tweet.image);
                }
            });
        }
        else {
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Tweet getCurrentTweet(int position) {
        return list.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_date) TextView date;
        @BindView(R.id.home_message) TextView message;
        @BindView(R.id.home_image) ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
