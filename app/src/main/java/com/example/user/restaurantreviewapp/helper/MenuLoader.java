package com.example.user.restaurantreviewapp.helper;

import android.util.Log;

import com.example.user.restaurantreviewapp.model.Dish;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuLoader {

    private MenuLoaderListener listener;

    private Gson gson = new Gson();

    public MenuLoaderListener getListener() {
        return listener;
    }

    public void setListener(MenuLoaderListener listener) {
        this.listener = listener;
    }

    public MenuLoader loadMenu(DatabaseReference myRef, String placeID) {
        Query query = myRef.child("meals").orderByChild("placeID").equalTo(placeID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.w("datasnapshot", dataSnapshot.getValue().toString());

                    String json = gson.toJson(dataSnapshot.getValue());
                    Log.w("json", json);

                    ArrayList<Dish> menu = new ArrayList<>();
                    Type hashmapType = new TypeToken<HashMap<String, Dish>>() {
                    }.getType();

                    HashMap<String, Dish> menuMap = gson.fromJson(json, hashmapType);
                    for (String key : menuMap.keySet()
                    ) {
                        System.out.println(menuMap.get(key));
                        menu.add(menuMap.get(key));
                    }

                    if(listener != null)
                        listener.onSuccess(menu);
                } else {
                    if(listener != null)
                        listener.onFailure("no menu found for this restaurant!!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("database error: ", databaseError.getMessage());
            }
        });
        return this;
    }

    public interface MenuLoaderListener {
        void onSuccess(ArrayList<Dish> menu);

        void onFailure(String message);
    }
}
