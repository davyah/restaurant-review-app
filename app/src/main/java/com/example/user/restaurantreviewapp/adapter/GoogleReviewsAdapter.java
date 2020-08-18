package com.example.user.restaurantreviewapp.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.model.GoogleReview;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoogleReviewsAdapter extends RecyclerView.Adapter<GoogleReviewsAdapter.MyViewHolder> {
    ArrayList<GoogleReview> googleReviews;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GoogleReviewsAdapter(ArrayList<GoogleReview> googleReviews, Context context) {
        this.googleReviews = googleReviews;
        this.googleReviews.sort(new Comparator<GoogleReview>() {
            @Override
            public int compare(GoogleReview o1, GoogleReview o2) {
                return (int) (o2.getTime() - o1.getTime());
            }
        });
        this.context = context;
    }

    @NonNull
    @Override
    public GoogleReviewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_layout, parent, false);

        return new GoogleReviewsAdapter.MyViewHolder(itemView,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GoogleReview temp = googleReviews.get(position);
        holder.authorName.setText(temp.getAuthor_name());
        Picasso.with(context).load(temp.getProfile_photo_url()).resize(38, 38).into(holder.authorImage);
        holder.reviewDescription.setText(temp.getText());
        holder.reviewRatingBar.setRating(temp.getRating());
        holder.timeStamp.setText(temp.getRelative_time_description());
    }


    @Override
    public int getItemCount() {
        return googleReviews.size();
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
