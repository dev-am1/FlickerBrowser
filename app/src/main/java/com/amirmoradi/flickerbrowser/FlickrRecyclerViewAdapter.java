package com.amirmoradi.flickerbrowser;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Dev_am1 on 11/24/2019
 */
class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotosList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photosList) {
        mPhotosList = photosList;
        mContext = context;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Called by layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        //Called by the layout manager when it wants new data in existing row

        if ((mPhotosList.size() == 0) || (mPhotosList == null)) {
            holder.thumbnail.setImageResource(R.drawable.brokenplaceholder);
            holder.title.setText(R.string.null_photoList_message);
        } else {
            Photo photoItem = mPhotosList.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " -->" + position);
            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.brokenplaceholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);
            holder.title.setText(photoItem.getTitle());
        }
    }


    @Override
    public int getItemCount() {
        return ((mPhotosList != null) && (mPhotosList.size() != 0) ? mPhotosList.size() : 1);
    }

    void loadNewData(List<Photo> newPhotos) {
        this.mPhotosList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position) {
        return ((mPhotosList != null) && (mPhotosList.size() != 0)) ? mPhotosList.get(position) : null;
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrImageViewHolder";

        ImageView thumbnail;
        TextView title;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");

            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.title = itemView.findViewById(R.id.title);

        }
    }

}
