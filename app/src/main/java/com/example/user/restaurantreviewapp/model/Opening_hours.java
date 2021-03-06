package com.example.user.restaurantreviewapp.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Opening_hours {
    @SerializedName("weekday_text")
    private ArrayList<String> weekday_text = new ArrayList<String>();

    Opening_hours(ArrayList<String> weekday_text){
        this.weekday_text = weekday_text;
      }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDayHours(String day)
    {
        Log.w("weekday", weekday_text.toString());
        Log.w("day hours", day);
        if(weekday_text != null || weekday_text.size() !=0 ) {
            List<String> matches = weekday_text.stream().filter(
                    it -> Pattern.compile(Pattern.quote(day), Pattern.CASE_INSENSITIVE).matcher(it).find()).collect(Collectors.toList());

            return matches.get(0);
        }
        else
            return "NA";
    }

    public ArrayList<String> getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(ArrayList<String> weekday_text) {
        this.weekday_text = weekday_text;
    }
}
