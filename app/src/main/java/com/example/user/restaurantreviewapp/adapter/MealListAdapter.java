package com.example.user.restaurantreviewapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.Review;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.MyViewHolder> {

    private List<Dish> menu;
    private Context context;
    private MealListAdapter.onClickListener onClickListener;
    DatabaseReference myRef;
    char currencySymbol;
    Gson gson = new Gson();

    public void setClickListener(MealListAdapter.onClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public MealListAdapter(Context context ,List<Dish> menu, DatabaseReference reference, char currencySymbol)
    {
        this.context = context;
        this.menu = menu;
        this.myRef = reference;
        this.currencySymbol = currencySymbol;
    }


    @Override
    public MealListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_menu, parent, false);

        return new MealListAdapter.MyViewHolder(itemView,viewType);

    }



    @Override
    public void onBindViewHolder(MealListAdapter.MyViewHolder holder, int position)
    {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null){
                    onClickListener.OnClick(position);
                }
            }
        });
        holder.meal_title.setText(menu.get(position).getTitle());
        Log.w("link",menu.get(position).getPhoto_url().get(0));
        Picasso.with(context).load(menu.get(position).getPhoto_url().get(0))
                .resize(512, 512).into(holder.meal_image);


        holder.meal_price.setText(String.valueOf(menu.get(position).getPrice()));

        new ContentLoader().loadData(myRef.child("reviews").orderByChild("dishID").equalTo(menu.get(position).getDishID())).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {
                ArrayList<Review> reviewsList = new ArrayList<>();
                Type hashmapType = new TypeToken<HashMap<String, Review>>() {
                }.getType();

                HashMap<String, Review> reviewsMap = gson.fromJson(json, hashmapType);

                float avg = 0;
                reviewsList.addAll(reviewsMap.values());


                if(reviewsList.size() > 0)
                {
                    for (Review review: reviewsList
                    ) {
                        avg += review.getRating();
                    }
                    avg = avg/reviewsList.size();

                }
                holder.meal_rating.setRating(avg);

            }

            @Override
            public void onFailure(String message) {
                holder.meal_rating.setRating(0);
            }
        });



    }



    @Override
    public int getItemCount() {

        return  menu.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        MyTextView_Roboto_Regular meal_title;
        RatingBar meal_rating;
        MyTextView_Roboto_Regular meal_price;
        ImageView meal_image;

        int view_type;

        public MyViewHolder(final View itemView, final int viewType) {
            super(itemView);



            view_type=1;
            this.meal_title = (MyTextView_Roboto_Regular) itemView.findViewById(R.id.meal_title);
            this.meal_rating = (RatingBar) itemView.findViewById(R.id.meal_rating);
            this.meal_price = (MyTextView_Roboto_Regular) itemView.findViewById(R.id.meal_price);
            this.meal_image = (ImageView) itemView.findViewById(R.id.meal_img);
        }


    }
    public interface onClickListener{
        void OnClick(int position);
    }
}