package com.example.user.restaurantreviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.user.restaurantreviewapp.adapter.MyReviewsAdapter;
import com.example.user.restaurantreviewapp.helper.ContentLoader;
import com.example.user.restaurantreviewapp.helper.CustomDialog;
import com.example.user.restaurantreviewapp.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

    Resources resources;

    MyReviewsAdapter.onClickListener listener = new MyReviewsAdapter.onClickListener() {
        @Override
        public void OnDeleteClick(int position) {
            deleteReview(position);

        }

        @Override
        public void OnEditClick(int position) {
            editReview(position);
        }
    };


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
        resources = activity.getResources();

        myReviewsRCV.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        reviews.clear();
        Toolbar toolbar = activity.findViewById(R.id.toolbar_id);
        toolbar.setTitle(getResources().getString(R.string.my_reviews));
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

                adapter = new MyReviewsAdapter(reviews, activity, myRef, listener);




                myReviewsRCV.setAdapter(adapter);

            }

            @Override
            public void onFailure(String message) {

            }
        });



    }

    public void editReview(int position)
    {
        CustomDialog dialog = new CustomDialog(activity);
        dialog.setListener(new CustomDialog.onReviewEditSave() {
            @Override
            public void onSaveClick(String text, float rating) {
                reviews.get(position).setDescription(text);
                reviews.get(position).setRating(rating);

                myRef.child("reviews").child(reviews.get(position).getReviewID()).setValue(reviews.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, resources.getString(R.string.review_uploaded), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, resources.getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        dialog.show(activity.getSupportFragmentManager(), "edit review dialog");
    }

    public void deleteReview(int position)
    {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(activity.getResources().getString(R.string.deletion_alert));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                activity.getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        myRef.child("reviews").child(reviews.get(position).getReviewID()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                reviews.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(activity, resources.getString(R.string.deletion_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}