package com.example.user.restaurantreviewapp.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.model.Dish;

import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class AutoSuggestAdapter extends ArrayAdapter<Dish>{
    private List<Dish> mlistData, baseList;

    private AutoSuggestAdapter.onClickListener onClickListener;

    public void setOnClickListener(AutoSuggestAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public AutoSuggestAdapter(@NonNull Context context, ArrayList<Dish> menuList) {
        super(context, 0, menuList);
        baseList = new ArrayList<>(menuList);
        mlistData = new ArrayList<>();
    }

    public void setData(List<Dish> list) {
        mlistData = list;
    }

    @Override
    public int getCount() {
        return mlistData.size();
    }

    @Nullable
    @Override
    public Dish getItem(int position) {
        return mlistData.get(position);
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    public Dish getObject(int position) {
        return mlistData.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.layout_menu_autocomplete, parent,false);
        }

        MyTextView_Roboto_Regular textView = convertView.findViewById(R.id.optionTextView);

        Dish dish = getItem(position);
        if(dish != null)
        {
            textView.setText(dish.getTitle());
        }

       textView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(onClickListener != null)
                   onClickListener.OnClick(dish);
           }
       });
        return convertView;
    }


    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Dish> suggestions = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {

                suggestions.addAll(baseList);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Dish dish: baseList)
                {
//                        if(dish.getTitle().toLowerCase().contains(filterPattern))
//                        {
//                            Log.w("suggested: ", filterPattern + " is in: " + dish.getTitle());
//                            suggestions.add(dish);
//                        }
                    if(FuzzySearch.partialRatio(filterPattern, dish.getTitle()) >= 85)
                    {
                        suggestions.add(dish);
                    }
                }
            }
            filterResults.values = suggestions;
            filterResults.count = suggestions.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            Log.w("filter results: ", results.values.toString());
            mlistData.clear();
            mlistData.addAll((List)results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Dish)resultValue).getTitle();
        }
    };

    @NonNull
    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    public interface onClickListener{
        void OnClick(Dish dish);
    }
}