package com.example.user.restaurantreviewapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.restaurantreviewapp.R;

import java.util.ArrayList;

public class ImagepickerAdapter extends RecyclerView.Adapter<ImagepickerAdapter.ImagesViewHolder> {
    private final static int VIEW0 = 0;
    private final static int VIEW1 = 1;
    private ArrayList<Uri> images;
    private Context context;

    public void setOnClickListener(ImagepickerAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private ImagepickerAdapter.onClickListener onClickListener;

    public ImagepickerAdapter(ArrayList<Uri> images)
    {
        this.images = images;
    }
    @Override
    public int getItemViewType(int position) {

            Uri uri = images.get(position);
            if(uri==null||uri.toString().isEmpty())
                return VIEW0;
            else return VIEW1;


    }//get item view type


    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        View view;
        ImagesViewHolder viewholder;

        switch (viewType) {
            case VIEW0:
                layoutRes = R.layout.add_picture;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
                viewholder =  new ImagesViewHolder(view);
                break;

            case VIEW1:
            default:
                layoutRes = R.layout.item_imagepicker;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
                viewholder =  new ImagesViewHolder(view);
                break;
        }//switch

        return viewholder;

    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW0: {
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w("click","adapter");
                        if (onClickListener!=null)onClickListener.OnAddClick();
                    }
                });
            }
            break;

            case VIEW1: {
                Uri s = images.get(position);
                ((ImagesViewHolder)holder).imageView.setImageURI(s);
                ((ImagesViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickListener!=null)onClickListener.OnImageClick(position);
                    }
                });
                ((ImagesViewHolder)holder).deleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickListener!=null)onClickListener.OnImageRemovedClick(position);
                    }
                });
            }
            break;
        }//switch
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView deleteImageView;


        public ImagesViewHolder(final View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.itemimageview);
            this.deleteImageView = (ImageView) itemView.findViewById(R.id.removeimage);

        }



    }
    public interface onClickListener{
        void OnAddClick();
        void OnImageClick(int position);
        void OnImageRemovedClick(int position);
    }
}
