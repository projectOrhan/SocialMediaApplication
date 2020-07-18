package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SeeProfilePhoto extends AppCompatActivity {

    String userID;
    ImageView see_profile_photo, profile_image_circle;
    String DATABASE_URL = "place your firebase storage url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_profile_photo);

        see_profile_photo = findViewById(R.id.see_profile_photo);
        profile_image_circle = findViewById(R.id.profile_image_circle);


        Intent intent = getIntent();
        //user has been clicked
        userID = intent.getStringExtra("userID");

        //showing profile photo on circle image
        showCircleProfileImage(userID);
        //showing profile photo big screen
        showProfilePhoto(userID);


    }


    private void showCircleProfileImage(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeProfilePhoto.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(profile_image_circle);
    }

    private void showProfilePhoto(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeProfilePhoto.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(see_profile_photo);
    }
}
