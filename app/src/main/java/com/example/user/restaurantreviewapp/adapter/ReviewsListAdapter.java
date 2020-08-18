package com.example.user.restaurantreviewapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.model.Review;
import com.example.user.restaurantreviewapp.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.MyViewHolder> {

    ArrayList<Review> reviewsList;
    Context context;
    DatabaseReference myRef;
    Gson gson = new Gson();

    public ReviewsListAdapter(ArrayList<Review> reviewsList, Context context, DatabaseReference myRef) {
        this.reviewsList = reviewsList;
        this.context = context;
        this.myRef = myRef;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_layout, parent, false);

        return new ReviewsListAdapter.MyViewHolder(itemView,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Query query = myRef.child("users").child(reviewsList.get(position).getAuthorID());

        new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {
                User user = gson.fromJson(json, User.class);
                Picasso.with(context).load(user.getImage_url()).resize(38, 38).into(holder.authorImage);
                holder.authorName.setText(user.getName());
            }

            @Override
            public void onFailure(String message) {
                Uri imgUri= Uri.parse("android.resource://com.example.user.restaurantreviewapp/"+R.drawable.default_avatar);
                holder.authorImage.setImageURI(null);
                holder.authorImage.setImageURI(imgUri);

                holder.authorName.setText("user");
            }
        });

        holder.reviewRatingBar.setRating(reviewsList.get(position).getRating());
        holder.reviewDescription.setText(reviewsList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView authorImage;
        TextView authorName;
        RatingBar reviewRatingBar;
        TextView timeStamp;
        TextView reviewDescription;

        int view_type;

        public MyViewHolder(final View itemView, final int viewType) {
            super(itemView);



            view_type=1;

            this.authorImage = itemView.findViewById(R.id.authorImageView);
            this.authorName = itemView.findViewById(R.id.authorNameTV);
            this.reviewRatingBar = itemView.findViewById(R.id.reviewRatingBar);
            this.timeStamp = itemView.findViewById(R.id.timeStampTV);
            this.reviewDescription = itemView.findViewById(R.id.reviewDescriptionTV);
        }

    }
    public interface onClickListener{
        void OnClick(int position);
    }
}
