package com.example.user.restaurantreviewapp.helper;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.user.restaurantreviewapp.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class CurrentDay {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDayAsString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
//        String weekday = LocalDate.now().getDayOfWeek().name().toLowerCase();
//        System.out.println("language = " + Locale.getDefault().getDisplayLanguage());
//        if(Locale.getDefault().getDisplayLanguage() == "עברית")
//        {
//            System.out.println("converting weekdays");
//            switch (weekday)
//            {
//                case "sunday": return "יום ראשון";
//                case "monday": return "יום שני";
//                case "tuesday": return "יום שלישי";
//                case "wednesday": return "יום רביעי";
//                case "thursday": return "יום חמישי";
//                case "friday": return "יום שישי";
//                case "saturday": return "יום שבת";
//            }
//        }
        return dayOfTheWeek;
    }
}
