package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.restaurantreviewapp.adapter.GoogleReviewsAdapter;
import com.example.user.restaurantreviewapp.adapter.MealListAdapter;
import com.example.user.restaurantreviewapp.adapter.ViewPagerAdapter;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.helper.RestaurantLoader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.RestaurantDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailsFragment extends Fragment {

    @BindView(R.id.restaurantName)
    MyTextView_Roboto_Regular resName;

    @BindView(R.id.resAddress)
    MyTextView_Roboto_Regular resAddress;

    @BindView(R.id.RatingBar_Id)
    RatingBar ratingBar;

    @BindView(R.id.ReviewsNum_Id)
    MyTextView_Roboto_Regular reviewsNum;

    @BindView(R.id.hoursText)
    MyTextView_Roboto_Regular hours;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.menu_rcv)
    RecyclerView dishList;

    @BindView(R.id.add_meal)
    FloatingActionButton fab;

    @BindView(R.id.noMenuErrorView)
    TextView noMenuError;

    @BindView(R.id.navigateImageView)
    ImageView naviImageView;

    @BindView(R.id.googleReviews_rcv)
    RecyclerView googleReviewsRCV;

    ViewPagerAdapter viewPagerAdapter;
    RestaurantDetails restaurantDetails;
    DatabaseReference myRef;
    String userID;
    String placeID;

    GoogleReviewsAdapter googleReviewsAdapter;


    MealListAdapter dishesAdapter;



    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Gson gson = new Gson();
    String url;

    MainActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (MainActivity) context;
        }
    }


    private void loadMenuIfExists() {
        Query query = myRef.child("meals").orderByChild("placeID").equalTo(placeID);
        new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {

                ArrayList<Dish> menu = new ArrayList<>();
                Type hashmapType = new TypeToken<HashMap<String, Dish>>() {
                }.getType();

                HashMap<String, Dish> menuMap = gson.fromJson(json, hashmapType);

                menu.addAll(menuMap.values());

                dishesAdapter = new MealListAdapter(activity, menu, myRef, '$');
                dishList.setAdapter(dishesAdapter);
                System.out.println("we are in rdf: " + menu);
                dishesAdapter.setClickListener(new MealListAdapter.onClickListener() {
                                                   @Override
                                                   public void OnClick(int position) {

                                                       Bundle bundle = new Bundle();
                                                       bundle.putString("dish", gson.toJson(menu.get(position)));
                                                       activity.replaceFragments(MealDetailsFragment.class, bundle);
                                                   }
                                               }
                );
            }

            @Override
            public void onFailure(String message) {
                noMenuError.setText(message);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String whatTheDay()
    {
        Date now = new Date();

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        System.out.println(simpleDateformat.format(now));

        String dayHours =  restaurantDetails.getDayHours(simpleDateformat.format(now));
        if(dayHours != null) {
            String[] parts = dayHours.split(simpleDateformat.format(now) + ":");

            if (parts.length > 1)
                return parts[1];
            else
                return dayHours;
        }
        else return "NA";
    }

    public void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        } else {
            userID = user.getUid();
        }

        fab.setOnClickListener(v -> {
            String json = gson.toJson(restaurantDetails);
            Bundle args = new Bundle();
            args.putString("restaurant", json);
            activity.replaceFragments(AddMealFragment.class, args);
        });

        dishList.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));

        googleReviewsRCV.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));;
        activity.findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone(restaurantDetails.getFormatted_phone_number());
            }
        });
        if(getArguments() != null)
        {
            placeID = getArguments().getString("placeid");
        }

        naviImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?saddr=" + String.valueOf(activity.source_lat) + "," + String.valueOf(activity.source_long) + "&daddr=" + String.valueOf(restaurantDetails.getGeometry().getLocation().getLat()) + "," + String.valueOf(restaurantDetails.getGeometry().getLocation().getLng());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        if (placeID != null) {
            Log.w("fetch place", placeID);
            new RestaurantLoader().loadRestaurant(placeID).setListener(new RestaurantLoader.RestaurantLoaderListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(RestaurantDetails place) {
                    Log.w("render place", place.getFormatted_address());
                    restaurantDetails = place;
                    resName.setText(restaurantDetails.getName());
                    resAddress.setText(restaurantDetails.getFormatted_address());
                    ratingBar.setRating(restaurantDetails.getRating());
                    reviewsNum.setText(Integer.toString(restaurantDetails.getUser_ratings_total()) + " reviews");
                    hours.setText(whatTheDay());
                    viewPagerAdapter = new ViewPagerAdapter(activity, restaurantDetails.PhotosUrl());
                    viewPager.setAdapter(viewPagerAdapter);

                    //load google reviews
                    googleReviewsAdapter = new GoogleReviewsAdapter(place.getGoogleReviews(), activity);
                    googleReviewsRCV.setAdapter(googleReviewsAdapter);
                    loadMenuIfExists();
                }

                @Override
                public void onFailure(String message) {

                }
            });
        } else {
            Toast.makeText(activity, "some error occurred while getting information", Toast.LENGTH_SHORT).show();
           activity.getSupportFragmentManager().popBackStackImmediate();
        }
    }

}