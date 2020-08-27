package com.example.user.restaurantreviewapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.restaurantreviewapp.MyReviewsFragment;
import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.helper.RestaurantLoader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.RestaurantDetails;
import com.example.user.restaurantreviewapp.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.MyViewHolder> {
    ArrayList<Review> myReviews;
    Context context;
    DatabaseReference myRef;
    Gson gson = new Gson();
    RestaurantDetails restaurantDetails;
    String language;

    MyReviewsAdapter.onClickListener onClickListener;

    public MyReviewsAdapter.onClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(MyReviewsAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MyReviewsAdapter(ArrayList<Review> myReviews, Context context, DatabaseReference myRef, onClickListener listener) {
        this.myReviews = myReviews;
        this.context = context;
        this.myRef = myRef;
        this.language = context.getResources().getString(R.string.language);
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_review_item, parent, false);

        return new MyReviewsAdapter.MyViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Review temp = myReviews.get(position);

        new ContentLoader().loadData(myRef.child("meals").child(temp.getDishID())).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {
                Dish dish = gson.fromJson(json, Dish.class);
                new RestaurantLoader().loadRestaurant(dish.getPlaceID(), context.getResources().getString(R.string.language)).setListener(new RestaurantLoader.RestaurantLoaderListener() {
                    @Override
                    public void onSuccess(RestaurantDetails place) {
                        holder.restaurantName.setText(place.getName());
                        holder.restaurantAddress.setText(place.getFormatted_address());
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        //TODO handle error
                    }


                });

                holder.dishName.setText(dish.getTitle());
                if(temp.getDescription() != null)
                    holder.reviewText.setText(temp.getDescription());
                holder.ratingBar.setRating(temp.getRating());
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(context, holder.editButton);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.review_popup_menu);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.editReview:
                                        Log.w("item selected", "edit review");
                                        if(onClickListener != null)
                                            onClickListener.OnEditClick(position);
                                        return true;
                                    case R.id.deleteReview:
                                        Log.w("item selected", "delete review");
                                        if(onClickListener != null)
                                            onClickListener.OnDeleteClick(position);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();
                    }
                });
            }

            @Override
            public void onFailure(String message) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return myReviews.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        MyTextView_Roboto_Regular restaurantName, restaurantAddress, dishName, time, reviewText;
        RatingBar ratingBar;
        ImageButton editButton;

        int view_type;

        public MyViewHolder(final View itemView, final int viewType) {
            super(itemView);



            view_type=1;

           this.restaurantName = itemView.findViewById(R.id.myReviewRestaurant);
           this.restaurantAddress = itemView.findViewById(R.id.myReviewAddress);
           this.dishName = itemView.findViewById(R.id.myReviewDish);
           this.ratingBar = itemView.findViewById(R.id.myReviewRatingBar);
           this.reviewText = itemView.findViewById(R.id.myReviewText);
           this.time = itemView.findViewById(R.id.myReviewTime);
           this.editButton = itemView.findViewById(R.id.editButton);
        }


    }


    public interface onClickListener{
        void OnDeleteClick(int position);
        void OnEditClick(int position);
    }

}
