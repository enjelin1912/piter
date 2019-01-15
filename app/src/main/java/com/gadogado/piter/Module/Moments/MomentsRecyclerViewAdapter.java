package com.gadogado.piter.Module.Moments;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gadogado.piter.Helper.Model.Moment;
import com.gadogado.piter.Helper.Utility;
import com.gadogado.piter.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MomentsRecyclerViewAdapter extends RecyclerView.Adapter<MomentsRecyclerViewAdapter.ViewHolder> {

    private List<Moment> list;
    private Context context;

    public MomentsRecyclerViewAdapter(Context context, List<Moment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_moments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Moment mo = list.get(position);
        holder.momentTitle.setText(mo.title);
        holder.momentDate.setText(Utility.getDateTimeFormat(mo.date));
        holder.momentDescription.setText(mo.description);

        if (mo.image != null) {
            holder.momentImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + mo.image)
                    .into(holder.momentImage);
        }
        else {
            holder.momentImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Moment getCurrentMoment(int position) {
        return list.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.moments_title) TextView momentTitle;
        @BindView(R.id.moments_date) TextView momentDate;
        @BindView(R.id.moments_descripiton) TextView momentDescription;
        @BindView(R.id.moments_image) ImageView momentImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
