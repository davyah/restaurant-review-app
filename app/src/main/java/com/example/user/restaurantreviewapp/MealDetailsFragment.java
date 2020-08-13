package com.example.user.restaurantreviewapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class MealDetailsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reviewRef = database.getReference("reviews");
    DatabaseReference mealsRef = database.getReference("meals");

    private DatabaseReference userRef;
    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;

    Dish dish;
    String dishID;
    String userID;
    Gson gson = new Gson();

    ArrayList<Review> reviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = mAuth.getCurrentUser();
        if (user == null) {
            //TODO handle logged out situation
//            startActivity(new Intent(RestaurantDetailsActivity.this, LoginActivity.class));
//            finish();
        } else {
            userID = user.getUid();
        }
        dishID = getArguments().getString("mealID");
        loadDish(dishID);
    }

    private void loadDish(String dishID)
    {
        Query query = mealsRef.orderByChild("dishID").equalTo(dishID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String json = gson.toJson(dataSnapshot.getValue());
//                dish = gson.fromJson(dataSnapshot.getValue().toString(), Dish.class);
                System.out.println("dish: " + json);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        Query reviewsQuery = reviewRef.orderByChild("dishID").equalTo(dishID);
        reviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String json = gson.toJson(dataSnapshot.getValue());
                    Type hashmapType = new TypeToken<HashMap<String, Review>>(){}.getType();

                    HashMap<String, Review> reviewsMap = gson.fromJson(json, hashmapType);
                    for (String key: reviewsMap.keySet()
                    ) {
                        System.out.println("review: " + reviewsMap.get(key));
                        reviews.add(reviewsMap.get(key));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}