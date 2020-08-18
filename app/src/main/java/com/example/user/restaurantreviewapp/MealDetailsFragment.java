package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.user.restaurantreviewapp.adapter.ReviewsListAdapter;
import com.example.user.restaurantreviewapp.adapter.ViewPagerAdapter;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MealDetailsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;

    Dish dish;

    float average;

    int numOfReviewers = 0;

    String userID;
    Gson gson = new Gson();

    ArrayList<Review> reviews = new ArrayList<>();

    MainActivity activity;

    @BindView(R.id.reviews_rcv)
    RecyclerView reviewsRcv;

    @BindView(R.id.dishNameTV)
    MyTextView_Roboto_Regular dishName;

    @BindView(R.id.priceTV)
    MoneyTextView priceTV;

    @BindView(R.id.dishDescriptionTV)
    MyTextView_Roboto_Regular dishDescription;

    @BindView(R.id.ReviewsNumTV)
    MyTextView_Roboto_Regular numOfReviews;

    @BindView(R.id.ratingBar_Id)
    RatingBar ratingBar;



    ReviewsListAdapter reviewsListAdapter;

    ViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.dishviewpager)
    ViewPager dishViewPager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (MainActivity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_meal_details, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        } else {
            userID = user.getUid();
        }
        dish = gson.fromJson(getArguments().getString("dish"), Dish.class);
        reviewsRcv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        loadReviews();
        viewPagerAdapter = new ViewPagerAdapter(activity, dish.getPhoto_url().toArray(new String[0]));
        dishViewPager.setAdapter(viewPagerAdapter);
        initializeUI();
    }


    public void loadReviews()
    {
        Query query = myRef.child("reviews").orderByChild("dishID").equalTo(dish.getDishID());
        new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {
                Type hashmapType = new TypeToken<HashMap<String, Review>>() {
                }.getType();

                HashMap<String, Review> reviewsMap = gson.fromJson(json, hashmapType);

                float avg = 0;
                reviews.addAll(reviewsMap.values());


                if(reviews.size() > 0)
                {
                    for (Review review: reviews
                    ) {
                        avg += review.getRating();
                    }
                    avg = avg/reviews.size();
                }

                reviewsListAdapter = new ReviewsListAdapter(reviews, activity, myRef);
                reviewsRcv.setAdapter(reviewsListAdapter);
                average = avg;
                numOfReviewers = reviews.size();
                initializeUI();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void initializeUI()
    {
        dishName.setText(dish.getTitle());
        ratingBar.setRating(average);
        priceTV.setAmount(dish.getPrice());
        dishDescription.setText(dish.getDescription());
        numOfReviews.setText(String.valueOf(numOfReviewers) + " reviews");
    }

}