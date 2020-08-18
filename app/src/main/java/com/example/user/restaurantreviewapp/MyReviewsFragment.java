package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.user.restaurantreviewapp.adapter.MyReviewsAdapter;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyReviewsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;

    MyReviewsAdapter adapter;

    @BindView(R.id.my_reviews_recycler_view)
    ShimmerRecyclerView myReviewsRCV;

    Gson gson = new Gson();

    ArrayList<Review> reviews = new ArrayList<>();

    MainActivity activity;
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
        View view = inflater.inflate(R.layout.fragment_my_reviews, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        }

        myReviewsRCV.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

        loadData();


    }

    public void loadData()
    {
        Query query = myRef.child("reviews").orderByChild("authorID").equalTo(user.getUid());
        new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {
                Type hashmapType = new TypeToken<HashMap<String, Review>>() {}.getType();

                HashMap<String, Review> reviewsMap = gson.fromJson(json, hashmapType);

                System.out.println(reviewsMap.toString());
                reviews.addAll(reviewsMap.values());
                System.out.println(reviews.toString());

                adapter = new MyReviewsAdapter(reviews, activity, myRef);


                myReviewsRCV.setAdapter(adapter);

            }

            @Override
            public void onFailure(String message) {

            }
        });



    }

    public void editReview(Review review)
    {

    }

    public void deleteReview(Review review)
    {
        myRef.child("reviews").child(review.getReviewID()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(activity, "your review has been deleted successfully!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}