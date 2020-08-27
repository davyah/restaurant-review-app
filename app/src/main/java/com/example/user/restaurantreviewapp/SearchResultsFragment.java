package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.adapter.HomepageAdapter;
import com.example.user.restaurantreviewapp.helper.PermissionHelper;
import com.example.user.restaurantreviewapp.placesApi.APIInterface;
import com.example.user.restaurantreviewapp.placesApi.AddressProvider;
import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.example.user.restaurantreviewapp.placesApi.LocationProvider;
import com.example.user.restaurantreviewapp.placesApi.Place;
import com.example.user.restaurantreviewapp.placesApi.PlaceProvider;
import com.example.user.restaurantreviewapp.placesApi.PlacesResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchResultsFragment extends Fragment{
    private HomepageAdapter homepageAdapter;
    APIInterface apiService;
    public String latLngString;


    private boolean Permission_is_granted = false;
    ArrayList<PlacesResponse.CustomA> results;

    protected Location mLastLocation;

    public List<Place> restaurantsList;


    private LocationProvider locationProvider;
    private long radius = 3 * 1000;

    public String mAddressOutput;

    String queryString;

    @BindView(R.id.search_results_view)
    ShimmerRecyclerView shimmerRecycler;

    @BindView(R.id.add_meal_or_review)
    FloatingActionButton fab;

    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (MainActivity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, view);
        Log.w("stack", String.valueOf(activity.getSupportFragmentManager().getBackStackEntryCount()));
        shimmerRecycler.showShimmerAdapter();
        shimmerRecycler.setLayoutManager(new GridLayoutManager(activity,3));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        apiService = ApiClient.getClient().create(APIInterface.class);
        locationProvider = new LocationProvider(activity);
        restaurantsList = Collections.synchronizedList(new ArrayList<Place>());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.replaceFragments(AddOrReviewMealFragment.class, new Bundle());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        restaurantsList.clear();
        queryString = getArguments().getString("queryText");
        Toolbar toolbar = activity.findViewById(R.id.toolbar_id);
        toolbar.setTitle(getResources().getString(R.string.results_title, queryString));
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
                    Toast.makeText(activity, error_retrieving_location, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchStores(String placeType) {
        PlaceProvider placeProvider = new PlaceProvider(placeType, latLngString, radius, queryString, activity.getResources().getString(R.string.language));
        placeProvider.setPlaceProviderListener(new PlaceProvider.PlaceProviderListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaceFetched(Place place) {
                addToList(place);
            }
        });
    }

    private void addToList(Place place) {
        restaurantsList.add(place);

        Collections.sort(restaurantsList, new Comparator<Place>() {
            @Override
            public int compare(Place lhs, Place rhs) {
                return lhs.distance.compareTo(rhs.distance);
            }
        });
        // progress.setVisibility(View.GONE);
        HomepageAdapter adapterStores = new HomepageAdapter(activity, restaurantsList, mAddressOutput);
        adapterStores.setOnClickListener(new HomepageAdapter.onClickListener() {
            @Override
            public void OnClick(int position) {
                Bundle args = new Bundle();
                args.putString("placeid", restaurantsList.get(position).placeid);
                activity.replaceFragments(RestaurantDetailsFragment.class, args);
            }
        });
        System.out.println(restaurantsList.toString());
//        recyclerView.setAdapter(adapterStores);
        shimmerRecycler.setAdapter(adapterStores);
    }


}