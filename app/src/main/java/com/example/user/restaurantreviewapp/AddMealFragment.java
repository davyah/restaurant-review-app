package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.example.user.restaurantreviewapp.adapter.ImagepickerAdapter;
import com.example.user.restaurantreviewapp.customfonts.EditText_Roboto_Regular;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ImageUploader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.RestaurantDetails;
import com.example.user.restaurantreviewapp.model.Review;
import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.madapps.placesautocomplete.PlaceAPI;
import in.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter;
import in.madapps.placesautocomplete.listener.OnPlacesDetailsListener;
import in.madapps.placesautocomplete.model.Place;
import in.madapps.placesautocomplete.model.PlaceDetails;

public class AddMealFragment extends Fragment implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate,
        BSImagePicker.OnSelectImageCancelledListener {

    RestaurantDetails restaurantDetails;
    String userID;
    Gson gson;
    private ArrayList<Uri> uris;
    private FirebaseAuth mAuth;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reviewRef = database.getReference("reviews");
    DatabaseReference mealsRef = database.getReference("meals");

    private DatabaseReference userRef;
    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayList<String> imagesUrl;
    String mealID;

    String placeID;

    private ImagepickerAdapter adapter;

    @BindView(R.id.placeAutocompleteET)
    AutoCompleteTextView autoCompleteTextView;

    @BindView(R.id.meal_title)
    EditText_Roboto_Regular title;

    @BindView(R.id.mealDescription)
    EditText_Roboto_Regular description;

    @BindView(R.id.ratingCheckbox)
    CheckBox checkBox;

    @BindView(R.id.priceTextView)
    MoneyTextView priceTextView;

    @BindView(R.id.priceEditText)
    EditText_Roboto_Regular priceEditText;

    @BindView(R.id.reviewLayout)
    ConstraintLayout reviewLayout;

    @BindView(R.id.ratingBar1)
    RatingBar ratingBar1;

    @BindView(R.id.ratingBar2)
    RatingBar ratingBar2;

    @BindView(R.id.images_recycler_view)
    RecyclerView imagesRCV;

    @BindView(R.id.submitBbtn)
    MyTextView_Roboto_Regular submitButton;

    Review review;

    String currency = "$";

    private final String basePath = "mealImages/";

    MainActivity activity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (MainActivity) context;
        }
    }

    public synchronized void addImageUri(String path) {

        imagesUrl.add(path);
        if (imagesUrl.size() == uris.size() - 1) {
            Dish dish = new Dish(title.getText().toString(), description.getText().toString(), 100, imagesUrl, placeID, mealID, user.getUid(), currency);
            //restaurantDetails.addDish(dish);

            mealsRef.child(dish.getDishID()).setValue(dish).

                    addOnCompleteListener(task ->
                    {
                        if (checkBox.isChecked()) {
                            reviewRef.child(review.getReviewID()).setValue(review).addOnCompleteListener(task1 -> {
                                Toast.makeText(activity, "your dish and review has uploaded successfully", Toast.LENGTH_SHORT).show();
                                activity.getSupportFragmentManager().popBackStackImmediate();
                            }).addOnFailureListener(e -> Toast.makeText(activity, "There was an error saving your data please retry later", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(activity, "your dish has uploaded successfully", Toast.LENGTH_SHORT).show();
                            activity.getSupportFragmentManager().popBackStackImmediate();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(activity, "There was an error saving your data please retry later", Toast.LENGTH_SHORT).show());


        }
    }


    public void submit(View view) {
        //TODO check if no images chosen
        imagesUrl = new ArrayList<String>();
        mealID = UUID.randomUUID().toString();
        if (uris.size() == 1) {
            Toast.makeText(activity, "select at least 1 image to submit!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(priceTextView.getAmount() <= 0)
        {
            Toast.makeText(activity, "please enter the price fro the meal", Toast.LENGTH_SHORT).show();
            return;
        }

        float totalRating = (float) (0.7 * ratingBar1.getRating() + 0.3 * ratingBar2.getRating());
        if (checkBox.isChecked()) {
            review = new Review(title.getText().toString(), totalRating, null, user.getUid(), mealID, UUID.randomUUID().toString());
        }


        for (int i = 1; i < uris.size(); i++) {
            //File file = new File(uris.get(i).getPath());
            new ImageUploader().uploadImage(uris.get(i), basePath, storageReference, mealID, activity).setListener(new ImageUploader.ImageUploaderListener() {
                @Override
                public void onSuccess(Uri uri1) {
                    addImageUri(uri1.toString());
                }

                @Override
                public void onFailure(String message) {

                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_meal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        gson = new Gson();
        String json = getArguments().getString("restaurant");

//        myRef = FirebaseDatabase.getInstance().getReference();
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

        restaurantDetails = gson.fromJson(json, RestaurantDetails.class);

        placeID = restaurantDetails.getPlace_id();

        autoCompleteTextView.setText(restaurantDetails.getName());

        PlaceAPI placeAPI = new PlaceAPI.Builder().apiKey(ApiClient.GOOGLE_PLACE_API_KEY).build(activity);
        PlacesAutoCompleteAdapter placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(activity, placeAPI);
        autoCompleteTextView.setAdapter(placesAutoCompleteAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);
                placeAPI.fetchPlaceDetails(place.getId(), new OnPlacesDetailsListener() {
                    @Override
                    public void onPlaceDetailsFetched(PlaceDetails placeDetails) {
                        Log.w("mdf actv place name", placeDetails.getName());
                        placeID = placeDetails.getPlaceId();
                        autoCompleteTextView.setText(placeDetails.getName());
                    }

                    @Override
                    public void onError(String s) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                        activity.getSupportFragmentManager().popBackStackImmediate();
                    }
                });
            }
        });


        uris = new ArrayList<>();
        uris.add(null);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        imagesRCV.setLayoutManager(layoutManager);
        adapter = new ImagepickerAdapter(uris);
        imagesRCV.setAdapter(adapter);
        adapter.setOnClickListener(new ImagepickerAdapter.onClickListener() {
            @Override
            public void OnAddClick() {
                Log.w("click", "add");
                BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.yourdomain.yourpackage.fileprovider")
                        .isMultiSelect() //Set this if you want to use multi selection mode.
                        .setMaximumMultiSelectCount(6) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                        .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                        .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                        .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                        .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                        .disableOverSelectionMessage() //You can also decide not to show this over select message.
                        .build();
//                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                multiSelectionPicker.show(getChildFragmentManager(), null);
            }

            @Override
            public void OnImageClick(int position) {
                //open full image popup
            }

            @Override
            public void OnImageRemovedClick(int position) {
                Log.w("removed", String.valueOf(position));
                uris.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox.isChecked() == true) {
                    reviewLayout.setVisibility(View.VISIBLE);
                } else {
                    reviewLayout.setVisibility(View.GONE);
                }
            }
        });

        priceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEditText.setVisibility(View.VISIBLE);
                priceEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(priceEditText, InputMethodManager.SHOW_IMPLICIT);
                priceTextView.setVisibility(View.INVISIBLE);
            }
        });

        priceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String price = priceEditText.getText().toString();
                    if (!price.isEmpty()) {
                        priceTextView.setAmount(Float.valueOf(price));
                    } else
                        Toast.makeText(activity, "please enter price", Toast.LENGTH_SHORT).show();

                    priceEditText.setVisibility(View.INVISIBLE);
                    priceTextView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        Log.w("list", uriList.toString());
        uris.addAll(uriList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(boolean isMultiSelecting, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

    }

}