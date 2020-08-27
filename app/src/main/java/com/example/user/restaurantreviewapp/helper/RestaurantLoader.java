package com.example.user.restaurantreviewapp.helper;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.user.restaurantreviewapp.adapter.ViewPagerAdapter;
import com.example.user.restaurantreviewapp.model.Dish;
import com.example.user.restaurantreviewapp.model.RestaurantDetails;
import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantLoader {

    private RestaurantLoaderListener listener;

    public RestaurantLoaderListener getListener() {
        return listener;
    }

    public void setListener(RestaurantLoaderListener listener) {
        this.listener = listener;
    }

    String baseUrl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";

    String placeID, language;

    Gson gson = new Gson();

    public RestaurantLoader loadRestaurant(String placeID, String language)
    {
        this.placeID = placeID;
        this.language = language;
        fetchStore task = new fetchStore();
        task.execute(this.placeID, this.language);
        return this;
    }

    public interface RestaurantLoaderListener{
        void onSuccess(RestaurantDetails place);
        void onFailure(String message);
    }

    public class fetchStore extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                String res = IOUtils.toString(new URL(baseUrl + urls[0] + "&language=" + urls[1] + "&key=" + ApiClient.GOOGLE_PLACE_API_KEY), "UTF-8");
                Log.w("result from google", res);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            RestaurantDetails restaurantDetails;
            try {
                if(s != null){
                    JSONObject jsonObject = new JSONObject(s);
                    restaurantDetails = gson.fromJson(jsonObject.getString("result"), RestaurantDetails.class);
                    if(listener != null)
                        listener.onSuccess(restaurantDetails);
                }
            } catch (JSONException e) {
                if(listener != null)
                    listener.onFailure(e.getMessage());
                else
                    e.printStackTrace();
            }
        }
    }
}
