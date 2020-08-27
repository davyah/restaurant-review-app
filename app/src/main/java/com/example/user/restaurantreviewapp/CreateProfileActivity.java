package com.example.user.restaurantreviewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.example.user.restaurantreviewapp.customfonts.EditText_Roboto_Regular;
import com.example.user.restaurantreviewapp.helper.ImageUploader;
import com.example.user.restaurantreviewapp.model.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate,
        BSImagePicker.OnSelectImageCancelledListener {
    @BindView(R.id.pick_image)
    CircleImageView profileImage;

    @BindView(R.id.username)
    EditText_Roboto_Regular userNameEditText;

    @BindView(R.id.name)
    EditText_Roboto_Regular nameEditText;

    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference userRef;
    StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private String imageUrl;
    private Uri imageuri;
    private User user;
    private final String basePath = "profileImages/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = database.getReference().child("users").child(firebaseUser.getUid());
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            imageuri = data.getData();
            profileImage.setImageURI(imageuri);

//            //You can get File object from intent
//           File file = ImagePicker.Companion.getFile(data);
//           //uploadImage(file.getPath(),file);
//
//            //You can also get File Path from intent
//             String imageUri = ImagePicker.Companion.getFilePath(data);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.save_btn) void save(){
        new ImageUploader().uploadImage(imageuri, basePath, storageReference, firebaseUser.getUid(), CreateProfileActivity.this).setListener(new ImageUploader.ImageUploaderListener() {
            @Override
            public void onSuccess(Uri uri1) {
                imageUrl = uri1.toString();
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        if(user!=null) {
                            user.setUserID(firebaseUser.getUid());
                            user.setName(nameEditText.getText().toString().trim());
                            user.setUsername(userNameEditText.getText().toString().trim());
                            if (imageUrl != null) {
                                user.setImage_url(imageUrl);
                            }
                            System.out.println(user.toString());
                            userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    startActivity(new Intent(CreateProfileActivity.this,MainActivity.class));
                                }
                            });
                        }else {
                            user = new User();
                            user.setEmail(firebaseUser.getEmail());
                            user.setName(nameEditText.getText().toString().trim());
                            user.setUsername(userNameEditText.getText().toString().trim());
                            user.setUserID(firebaseUser.getUid());
                            if (imageUrl != null) {
                                user.setImage_url(imageUrl);
                            }
                            System.out.println(user.toString());
                            userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    startActivity(new Intent(CreateProfileActivity.this,MainActivity.class));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onFailure(String message) {

            }
        });

    }
    @OnClick(R.id.pick_image) void pickImage(){

        BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.yourdomain.yourpackage.fileprovider")
                .setMaximumDisplayingImages(24) //Default: Integer.MAX_VALUE. Don't worry about performance :)
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                .hideCameraTile() //Default: show. Set this if you don't want user to take photo.
//                .hideGalleryTile() //Default: show. Set this if you don't want to further let user select from a gallery app. In such case, I suggest you to set maximum displaying images to Integer.MAX_VALUE.
                .setTag("A request ID") //Default: null. Set this if you need to identify which picker is calling back your fragment / activity.
                .dontDismissOnSelect()
                .build();

        singleSelectionPicker.show(getSupportFragmentManager(), "picker");

//        ImagePicker.Companion.with(this)
//                .crop()	    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .start();
    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        ivImage.setImageURI(imageUri);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }

    @Override
    public void onCancelled(boolean isMultiSelecting, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        profileImage.setImageURI(uri);
        imageuri = uri;
    }
}
