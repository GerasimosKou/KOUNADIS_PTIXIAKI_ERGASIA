package com.example.ptixiakiergasia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }



    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public ImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }



    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
      //  Log.wtf("TO CURRENT EINAI", String.valueOf(uploadCurrent.getImageUrl()));
        holder.datetext.setText(uploadCurrent.getDate().toString());
       Glide.with(mContext).load(uploadCurrent.getImageUrl()).apply(RequestOptions.circleCropTransform())
               .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }




    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView datetext;
        public ImageView imageView;

        public ImageViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            datetext = itemView.findViewById(R.id.datetext);

            imageView = itemView.findViewById(R.id.image_view_upload);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }


    }


}