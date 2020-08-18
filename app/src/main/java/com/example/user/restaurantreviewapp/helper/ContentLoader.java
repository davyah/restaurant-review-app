package com.example.user.restaurantreviewapp.helper;

import android.app.DownloadManager;
import android.util.Log;

import com.example.user.restaurantreviewapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class ContentLoader {
    Gson gson = new Gson();

    ContentLoaderListener listener;

    public ContentLoaderListener getListener() {
        return listener;
    }

    public void setListener(ContentLoaderListener listener) {
        this.listener = listener;
    }

    public ContentLoader loadData(Query query)
    {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String json = gson.toJson(dataSnapshot.getValue());
                    if(listener != null)
                        listener.onSuccess(json);
                } else {
                    if(listener != null)
                        listener.onFailure("content not found!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("database error: ", databaseError.getMessage());
            }
        });
        return this;
    }

    public interface ContentLoaderListener{
        void onSuccess(String json);

        void onFailure(String message);
    }
}
