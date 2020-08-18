package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.user.restaurantreviewapp.adapter.HomepageAdapter;
import com.example.user.restaurantreviewapp.helper.PermissionHelper;
import com.example.user.restaurantreviewapp.placesApi.APIInterface;
import com.example.user.restaurantreviewapp.placesApi.AddressProvider;
import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.example.user.restaurantreviewapp.placesApi.LocationProvider;
import com.example.user.restaurantreviewapp.placesApi.Place;
import com.example.user.restaurantreviewapp.placesApi.PlaceProvider;
import com.example.user.restaurantreviewapp.placesApi.PlacesResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.restaurantreviewapp.helper.PermissionHelper.MY_PERMISION_CODE;

public class RestaurantsFragment extends Fragment {
    private HomepageAdapter homepageAdapter;
    APIInterface apiService;
    public String latLngString;


    ArrayList<PlacesResponse.CustomA> results;

    protected Location mLastLocation;

    public List<Place> restaurantsList;


    private LocationProvider locationProvider;
    private long radius = 3 * 1000;


    private boolean Permission_is_granted = false;
    public String mAddressOutput;
    private ActionBarDrawerToggle actionBarDrawerToggle;
//    @BindView(R.id.recycler_view)
//    RecyclerView recyclerView;
    @BindView(R.id.shimmer_recycler_view)
    ShimmerRecyclerView shimmerRecycler;

    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        ButterKnife.bind(this, view);

        //shimmer RecyclerView thing

        shimmerRecycler.showShimmerAdapter();
        shimmerRecycler.setLayoutManager(new GridLayoutManager(activity,3));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        apiService = ApiClient.getClient().create(APIInterface.class);
        locationProvider = new LocationProvider(activity);

//        GridLayoutManager layoutManager = new GridLayoutManager(activity, 3);
        loadData();
    }


    private void loadData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            showSnack(true);
        } else {
            //progress.setVisibility(View.GONE);
            showSnack(false);
        }
    }

    private void fetchStores(String placeType) {
        restaurantsList = Collections.synchronizedList(new ArrayList<Place>());
        PlaceProvider placeProvider = new PlaceProvider(placeType, latLngString, radius);
        placeProvider.setPlaceProviderListener(new PlaceProvider.PlaceProviderListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                // progress.setVisibility(View.GONE);
            }

            @Override
            public void onPlaceFetched(Place place) {
                addToList(place);
            }
        });
    }


    /**
     * add the place object to the list and display it on the screen
     * @param place
     */
    private void addToList(Place place) {
        restaurantsList.add(place);

        //Log.i("details : ", info.name + "  " + address);

        Collections.sort(restaurantsList, new Comparator<Place>() {
            @Override
            public int compare(Place lhs, Place rhs) {
                return lhs.distance.compareTo(rhs.distance);
            }
        });
        Log.w("add", place.address);
        // progress.setVisibility(View.GONE);
        Log.w("places", restaurantsList.toString());
        HomepageAdapter adapterStores = new HomepageAdapter(activity, restaurantsList, mAddressOutput);
        adapterStores.setOnClickListener(new HomepageAdapter.onClickListener() {
            @Override
            public void OnClick(int position) {
                Bundle args = new Bundle();
                args.putString("placeid", restaurantsList.get(position).placeid);
                activity.replaceFragments(RestaurantDetailsFragment.class, args);
            }
        });
//        recyclerView.setAdapter(adapterStores);
        shimmerRecycler.setAdapter(adapterStores);

    }


    private void getUserLocation() {

        if (!PermissionHelper.isPermissionsGranted(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionHelper.askPermissions(activity);
            }
            else Permission_is_granted = true;
        } else {

            locationProvider.connectApiClient();
            locationProvider.setLocationProviderListener(new LocationProvider.LocationProviderListener() {
                @Override
                public void onLocationRetrieved(Location location) {
                    mLastLocation = location;
                    activity.source_lat = location.getLatitude();
                    activity.source_long = location.getLongitude();
                    latLngString = location.getLatitude() + "," + location.getLongitude();
                    new AddressProvider(latLngString, new AddressProvider.AddressListener() {
                        @Override
                        public void onAddressFetched(String message) {
                            mAddressOutput = message;
                        }
                    });
                    fetchStores("restaurant");
                }

                @Override
                public void onLocationError(String error_retrieving_location) {
                    //  progress.setVisibility(View.GONE);
                    Toast.makeText(activity, error_retrieving_location, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("On request permiss", "executed");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Permission_is_granted = true;
                    getUserLocation();
                } else {
                    PermissionHelper.askPermissions(activity);
                    Permission_is_granted = false;
                    Toast.makeText(activity, "Please switch on GPS to access the features", Toast.LENGTH_LONG).show();
                    //progress.setVisibility(View.GONE);

                }
                break;
        }
    }
    /**
     * this function is to tell the user uf the connection succeeded or failed
     * @param isConnected
     */
    public void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            getUserLocation();
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(a);
//        textView.setTextColor(color);
        snackbar.show();
    }

}