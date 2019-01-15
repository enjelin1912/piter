package com.gadogado.piter.Module.Moments;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gadogado.piter.Helper.Model.Tweet;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectTweetsRecyclerViewAdapter extends RecyclerView.Adapter<SelectTweetsRecyclerViewAdapter.ViewHolder> {

    public interface SelectTweetsListener {
        void viewImage(String imagePath);
        void tweetChecked(ArrayList<Integer> selectedTweets);
    }

    private SelectTweetsListener listener;
    private List<Tweet> list;
    private Context context;
    private ArrayList<Integer> selectedTweets;

    public SelectTweetsRecyclerViewAdapter(Context context, List<Tweet> list, ArrayList<Integer> selectedTweets, SelectTweetsListener listener) {
        this.context = context;
        this.list = list;
        this.selectedTweets = selectedTweets;
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

        holder.message.setText(Utility.setTags(context, tweet.message, null));

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

        holder.checkBox.setVisibility(View.VISIBLE);

        if (selectedTweets.contains(tweet.id)) {
            holder.checkBox.setChecked(true);
        }
        else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (selectedTweets.contains(tweet.id)) {
                    selectedTweets.remove(selectedTweets.indexOf(tweet.id));
                }
                else {
                    selectedTweets.add(tweet.id);
                }

                listener.tweetChecked(selectedTweets);
            }
        });
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
        @BindView(R.id.home_tweetcheck) CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
