package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.example.user.restaurantreviewapp.adapter.AutoSuggestAdapter;
import com.example.user.restaurantreviewapp.adapter.ImagepickerAdapter;
import com.example.user.restaurantreviewapp.customfonts.EditText_Roboto_Regular;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.helper.ImageUploader;
import com.example.user.restaurantreviewapp.helper.RestaurantLoader;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.RestaurantDetails;
import com.example.user.restaurantreviewapp.model.Review;
import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.madapps.placesautocomplete.PlaceAPI;
import in.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter;
import in.madapps.placesautocomplete.model.Place;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class AddOrReviewMealFragment extends Fragment implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate,
        BSImagePicker.OnSelectImageCancelledListener {

    RestaurantDetails restaurantDetails;
    String userID;
    Gson gson = new Gson();
    private ArrayList<Uri> uris;
    private FirebaseAuth mAuth;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference reviewRef = database.getReference("reviews");
    DatabaseReference mealsRef = database.getReference("meals");

    private DatabaseReference userRef;
    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;
    ArrayList<String> imagesUrl;
    String mealID;

    String placeID;

    boolean alreadyReviewed = false;


    AlertDialog alertDialog;

    private ImagepickerAdapter adapter;

    private AutoSuggestAdapter autoSuggestAdapter;

    @BindView(R.id.placeAutocompleteET)
    AutoCompleteTextView autoCompleteTextView;

    @BindView(R.id.meal_title)
    AppCompatAutoCompleteTextView title;

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

    @BindView(R.id.reviewDescription)
    EditText_Roboto_Regular reviewDescription;

    @BindView(R.id.ratingBar1)
    RatingBar ratingBar1;


    @BindView(R.id.images_recycler_view)
    RecyclerView imagesRCV;

    @BindView(R.id.submitBbtn)
    MyTextView_Roboto_Regular submitButton;

    Review review;

    String currency = "$";

    private final String basePath = "mealImages/";

    MainActivity activity;

    ArrayList<Dish> menuList = new ArrayList<>();


//    Dish currentDish;

    ArrayList<Review> reviews;

    Resources resources;

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
            Dish dish = new Dish(title.getText().toString(), description.getText().toString(), priceTextView.getAmount(), imagesUrl, placeID, mealID, user.getUid(), currency);
            //restaurantDetails.addDish(dish);

            mealsRef.child(dish.getDishID()).setValue(dish).

                    addOnCompleteListener(task ->
                    {
                        if (checkBox.isChecked()) {
                            reviewRef.child(review.getReviewID()).setValue(review).
                                    addOnCompleteListener(task1 -> {
                                        Toast.makeText(activity, resources.getString(R.string.review_uploaded), Toast.LENGTH_SHORT).show();
                                        activity.getSupportFragmentManager().popBackStackImmediate();
                            })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(activity, resources.getString(R.string.error), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.new_dish_uploaded), Toast.LENGTH_SHORT).show();
                            activity.getSupportFragmentManager().popBackStackImmediate();
                        }
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(activity, resources.getString(R.string.error), Toast.LENGTH_SHORT).show());
        }
    }


    public void submit(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        Dish similarDish = checkIfSimilar(title.getText().toString());
        Dish identicalDish = getIdentical(title.getText().toString());

        imagesUrl = new ArrayList<String>();
        mealID = UUID.randomUUID().toString();

        if (checkBox.isChecked()) {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
            Log.w("timehgdiugsd", currentTime);
            review = new Review(reviewDescription.getText().toString(), ratingBar1.getRating(), null, UUID.randomUUID().toString(), user.getUid(), mealID, currentTime);
        }
        else
            review = null;

        if(!checkInput() || (similarDish == null && uris.size() == 1))
        {
            builder1.setMessage(resources.getString(R.string.missing_fields));
            builder1.setCancelable(true);

            builder1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        else if(similarDish != null && identicalDish == null)
        {
            builder1.setMessage(resources.getString(R.string.similar_dish_alert));
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    resources.getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Bundle bundle = new Bundle();
                            bundle.putString("dish", gson.toJson(similarDish));
                            activity.replaceFragments(MealDetailsFragment.class, bundle);
                        }
                    });
            builder1.setNeutralButton(resources.getString(R.string.submit_anyawy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uploadContent();
                }
            });

            builder1.setNegativeButton(
                    resources.getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        else if(identicalDish != null && review != null)
        {
            //upload only review
            if(!checkIfReviewed())
            {
                review.setDishID(identicalDish.getDishID());
                myRef.child("reviews").child(review.getReviewID()).setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, resources.getString(R.string.review_uploaded), Toast.LENGTH_SHORT).show();
                        activity.getSupportFragmentManager().popBackStackImmediate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                builder1.setMessage(resources.getString(R.string.already_reviewed));
                builder1.setCancelable(true);

                builder1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                return;
            }
        }
        else if(identicalDish == null && similarDish == null)
        {
            //upload new dish and review
            uploadContent();
        }


    }


    public void uploadContent()
    {


        for (int i = 1; i < uris.size(); i++) {
            //File file = new File(uris.get(i).getPath());
            new ImageUploader().uploadImage(uris.get(i), basePath, storageReference, mealID, activity).setListener(new ImageUploader.ImageUploaderListener() {
                @Override
                public void onSuccess(Uri uri1) {
                    addImageUri(uri1.toString());
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(activity, "something is wrong, please try later", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private boolean checkInput()
    {
        boolean isFilled = true;
        if(autoCompleteTextView.getText() == null || placeID == null)
        {
            autoCompleteTextView.setError(resources.getString(R.string.restaurant_input));
            isFilled = false;
        }
         if(title.getText() == null || title.getText().toString().trim() == "")
         {
             title.setError(resources.getString(R.string.title_input_error));
             isFilled = false;
         }
         if(priceTextView.getAmount() <= 0)
         {
             priceEditText.setError(resources.getString(R.string.price_error));
             isFilled = false;
         }


        return isFilled;
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

        resources = activity.getResources();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideAndShowReviewPanel();
            }
        });

        priceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEditText.setVisibility(View.VISIBLE);
                getFocus(priceEditText);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        placeID = null;
        Toolbar toolbar = activity.findViewById(R.id.toolbar_id);
        toolbar.setTitle(getResources().getString(R.string.add_or_review));
        initiateFragment();
    }

    public void initiateFragment()
    {
        menuList.clear();
        placeID = getArguments().getString("placeID");

        if(placeID != null)
        {
            loadRestaurant(placeID);
        }
        getDishFromBundle();

        PlaceAPI placeAPI = new PlaceAPI.Builder().apiKey(ApiClient.GOOGLE_PLACE_API_KEY).build(activity);
        PlacesAutoCompleteAdapter placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(activity, placeAPI);
        autoCompleteTextView.setAdapter(placesAutoCompleteAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);
                Log.w("before fetching place",place.getDescription());
                placeID = place.getId();
                loadRestaurant(place.getId());
                autoCompleteTextView.setText(place.getDescription());
                menuList.clear();
                loadMenu();
            }
        });



        loadMenu();


        uris = new ArrayList<>();
        uris.add(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
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


    }

    private void getDishFromBundle()
    {
        String dishID = getArguments().getString("dishID");
        if(dishID != null)
        {
            Query query = myRef.child("meals").child(dishID);
            new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
                @Override
                public void onSuccess(String json) {
                    Dish dish = gson.fromJson(json, Dish.class);
                    renderDish(dish);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(activity, resources.getString(R.string.dish_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            return;
    }

    private void loadMenu()
    {
        Query query = myRef.child("meals").orderByChild("placeID").equalTo(placeID);
        new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {

                Type hashmapType = new TypeToken<HashMap<String, Dish>>() {}.getType();

                HashMap<String, Dish> menuMap = gson.fromJson(json, hashmapType);

                menuList.addAll(menuMap.values());

                autoSuggestAdapter = new AutoSuggestAdapter(activity, menuList);
                title.setAdapter(autoSuggestAdapter);

                autoSuggestAdapter.setOnClickListener(new AutoSuggestAdapter.onClickListener() {
                    @Override
                    public void OnClick(Dish dish) {
                        renderDish(dish);
                    }
                });
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void renderDish(Dish dish)
    {
        title.setText(dish.getTitle());
        description.setText(dish.getDescription());
        priceTextView.setAmount(dish.getPrice());
        checkBox.setChecked(true);
        title.dismissDropDown();
        hideAndShowReviewPanel();
        getFocus(reviewDescription);
        loadReviews(dish.getDishID());
    }

    private void loadReviews(String dishID) {
        Query query = myRef.child("reviews").orderByChild("dishID").equalTo(dishID);
        new ContentLoader().loadData(query).setListener(new ContentLoader.ContentLoaderListener() {
            @Override
            public void onSuccess(String json) {
                Type hashmapType = new TypeToken<HashMap<String, Review>>() {
                }.getType();

                HashMap<String, Review> reviewsMap = gson.fromJson(json, hashmapType);
                reviews = new ArrayList<>(reviewsMap.values());
            }

            @Override
            public void onFailure(String message) {
//                Toast.makeText(activity, resources.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRestaurant(String id)
    {
        new RestaurantLoader().loadRestaurant(id, resources.getString(R.string.language)).setListener(new RestaurantLoader.RestaurantLoaderListener() {
            @Override
            public void onSuccess(RestaurantDetails place) {
                restaurantDetails = place;
                autoCompleteTextView.setText(restaurantDetails.getName() + " " + restaurantDetails.getFormatted_address());
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(activity, resources.getString(R.string.restaurant_error), Toast.LENGTH_LONG).show();
//                    activity.getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }









    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        ivImage.setImageURI(imageUri);
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

    public void hideAndShowReviewPanel()
    {
        if (checkBox.isChecked() == true) {
            reviewLayout.setVisibility(View.VISIBLE);
        } else {
            reviewLayout.setVisibility(View.GONE);
        }
    }


    private void getFocus(View v)
    {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public Dish checkIfSimilar(String dishTitle)
    {
        for (Dish dish: menuList) {

            if(FuzzySearch.ratio(dish.getTitle(), dishTitle.trim().toLowerCase()) >= 85)
                return dish;
        }
        return null;
    }

    private Dish getIdentical(String dishTitle)
    {
        for (Dish dish: menuList) {

            if(dish.getTitle().equals(dishTitle.trim().toLowerCase()))
                return dish;
        }
        return null;
    }

    private boolean checkIfReviewed()
    {
        if(reviews != null)
        {
            for (Review r: reviews
            ) {
                if(r.getAuthorID().equals(userID))
                    return true;
            };
        }
        return false;
    }
}
