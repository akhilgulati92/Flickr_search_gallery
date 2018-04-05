package com.akhil.appstreet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.akhil.appstreet.R;
import com.akhil.appstreet.activities.ImageViewActivity;
import com.akhil.appstreet.constants.Constants;
import com.akhil.appstreet.utils.CommonUtils;
import com.akhil.appstreet.model.FlickrImage;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by Akhil on 26/3/2018.
 */

public class FlickrImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //This is a recyclerview adapter to bind data to recyclerview

    Context context;
    public ArrayList<FlickrImage> allDataList = new ArrayList<>();
    final String TAG = "MyLog " + this.getClass().getSimpleName();
    public static final int ITEM_VIEW_TYPE_BASIC = 0;
    public static final int ITEM_VIEW_TYPE_FOOTER = 1;
    private boolean isLoading;

    public FlickrImageAdapter(Activity context, ArrayList<FlickrImage> pdataList1) {

        this.allDataList = pdataList1;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

         if (isPositionFooter(position)) {
            return ITEM_VIEW_TYPE_FOOTER;
         }

        return ITEM_VIEW_TYPE_BASIC;
    }

    private boolean isPositionFooter(int position) {
        return position == allDataList.size();
    }


    @Override
    public int getItemCount() {
        return allDataList.size() + 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ContactViewHolder) {
            FlickrImage flickrImage = allDataList.get(position);
            String url = CommonUtils.constructURL(flickrImage);
            //Glide library is used for faster loading of pictures, first half of quality is loaded then full quality is loaded, placeholder image is not shown for neatness, loading indicator is not supported in Glide hence it is not used.
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .crossFade()
                    .placeholder(R.drawable.place_holder)
                    .thumbnail(0.5f)
                    .into(((ContactViewHolder)holder).ivThumb);

            ((ContactViewHolder)holder).ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent( ((Activity) context), ImageViewActivity.class );
                    Bundle bundle= new Bundle();
                    bundle.putParcelableArrayList( "dataList", allDataList);
                    bundle.putInt( "position", position );
                    intent.putExtra("bundle", bundle);
                    ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_FULL_SCREEN);

                }
            });
            //set data here...
        } else {
            //check data completion
            //if value is true, change the visibility of progressbar to gone to identify the user
            if (isLoading) {
                ((ProgressViewHolder)holder).progressBar.setVisibility(
                        View.VISIBLE);
                ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
            } else ((ProgressViewHolder)holder).progressBar.setVisibility(
                    View.GONE);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        //create the view for each corresponding viewtype
        if (viewType == ITEM_VIEW_TYPE_BASIC) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_image, parent, false);
            vh = new ContactViewHolder(v);
        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_loading_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected ImageView ivThumb;
        protected Context context;

        public ContactViewHolder(View v) {
            super(v);
            ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
            context = ivThumb.getContext();

        }



    }//ViewHolder


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
    }

}


