package com.example.user.restaurantreviewapp.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.user.restaurantreviewapp.R;
import com.example.user.restaurantreviewapp.customfonts.EditText_Roboto_Regular;

public class CustomDialog extends DialogFragment {
    Activity activity;
    EditText_Roboto_Regular description;
    RatingBar ratingBar;

    onReviewEditSave listener;

    public onReviewEditSave getListener() {
        return listener;
    }

    public void setListener(onReviewEditSave listener) {
        this.listener = listener;
    }

    public CustomDialog(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_review_dialog, null);
        description = view.findViewById(R.id.edit_review_ET);
        ratingBar = view.findViewById(R.id.edit_review_RB);

        builder.setView(view).setTitle(activity.getResources().getString(R.string.edit_review_title))
                .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(activity.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null)
                            listener.onSaveClick(description.getText().toString(), ratingBar.getRating());
                    }
                });


        return builder.create();
    }

    public interface onReviewEditSave{
        public void onSaveClick(String text, float rating);
    }
}
