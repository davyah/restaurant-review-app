package com.example.user.restaurantreviewapp.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ImageUploader {
    private static String imageUrl;
    private  ImageUploaderListener listener;

    public void setListener(ImageUploaderListener listener) {
        this.listener = listener;
    }

    public ImageUploader uploadImage(Uri filePath, String basePath, StorageReference storageReference, String key, Context context)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
//            StorageReference ref = storageReference.child(basePath + UUID.randomUUID().toString());
            StorageReference ref = storageReference.child(basePath).child(key).child(UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
// percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(uri -> {
                                    if(listener!=null)listener.onSuccess(uri);
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                });
                                // Image uploaded successfully
                                // Dismiss dialog



                            })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        if(listener!=null)listener.onFailure(e.getMessage());
                        imageUrl = null;
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int)progress + "%");
                            });
        }
       return this;
    }
 public interface ImageUploaderListener{
        void onSuccess(Uri uri1);
        void onFailure(String message);
 }
}
