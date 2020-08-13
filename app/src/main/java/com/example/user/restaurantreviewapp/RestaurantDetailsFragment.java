package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.user.restaurantreviewapp.adapter.MealListAdapter;
import com.example.user.restaurantreviewapp.adapter.ViewPagerAdapter;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.MenuLoader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.RestaurantDetails;
import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    ViewPagerAdapter viewPagerAdapter;
    RestaurantDetails restaurantDetails;
    DatabaseReference myRef;
    String userID;
    String placeID;



    MealListAdapter dishesAdapter;



    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Gson gson;
    String url;

    MainActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (MainActivity) context;
        }
    }



    public class fetchStore extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = IOUtils.toString(new URL(url), "UTF-8");
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    restaurantDetails = gson.fromJson(jsonObject.getString("result"), RestaurantDetails.class);
                    resName.setText(restaurantDetails.getName());
                    resAddress.setText(restaurantDetails.getFormatted_address());
                    ratingBar.setRating(restaurantDetails.getRating());
                    reviewsNum.setText(Integer.toString(restaurantDetails.getUser_ratings_total()) + " reviews");
                    hours.setText(whatTheDay());
                    viewPagerAdapter = new ViewPagerAdapter(activity, restaurantDetails.PhotosUrl());
                    viewPager.setAdapter(viewPagerAdapter);
                    loadMenuIfExists();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{

            }
        }
    }

    private void loadMenuIfExists() {
        new MenuLoader().loadMenu(myRef, placeID).setListener(new MenuLoader.MenuLoaderListener() {
            @Override
            public void onSuccess(ArrayList<Dish> menu) {

                dishesAdapter = new MealListAdapter(activity, menu);
                dishList.setAdapter(dishesAdapter);
                System.out.println("we are in rdf: " + menu);
                dishesAdapter.setClickListener(new MealListAdapter.onClickListener() {
                                                   @Override
                                                   public void OnClick(int position) {

                                                       Bundle bundle = new Bundle();
                                                       bundle.putString("mealID", menu.get(position).getDishID());
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
//        Query query = myRef.child("meals").orderByChild("placeID").equalTo(placeID);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    Log.w("datasnapshot",dataSnapshot.getValue().toString());
//
//                    String json = gson.toJson(dataSnapshot.getValue());
//                    Log.w("json",json);
//
//                    ArrayList<Dish> menu = new ArrayList<>();
//                    Type hashmapType = new TypeToken<HashMap<String, Dish>>(){}.getType();
//
//                    HashMap<String, Dish> menuMap = gson.fromJson(json, hashmapType);
//                    for (String key: menuMap.keySet()
//                         ) {
//                        System.out.println(menuMap.get(key));
//                        menu.add(menuMap.get(key));
//                    }
//
//                    dishesAdapter = new MealListAdapter(activity, menu);
//                    dishList.setAdapter(dishesAdapter);
//                    dishesAdapter.setClickListener(new MealListAdapter.onClickListener() {
//                        @Override
//                        public void OnClick(int position) {
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString("mealID", menu.get(position).getDishID());
//                            activity.replaceFragments(MealDetailsFragment.class, bundle);
//                        }
//                    }
//                );
//                }else {
//                    //the menu does not exist in firebase
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
            args.putString("userID", userID);
            activity.replaceFragments(AddMealFragment.class, args);
        });

        dishList.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));

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
            url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=" + ApiClient.GOOGLE_PLACE_API_KEY;
            Log.i("url", url);
            gson = new Gson();
            fetchStore task = new fetchStore();
            task.execute(url);
        } else {
            Toast.makeText(activity, "some error occurred while getting information", Toast.LENGTH_SHORT).show();
           activity.getSupportFragmentManager().popBackStackImmediate();
        }
    }

}