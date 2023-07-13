package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SeeProfilePhotoActivity extends AppCompatActivity {

    String userID;
    ImageView see_profile_photo, profile_image_circle;
    //define your database url as String DATABASE_URL = "database-url.com";
    //String DATABASE_URL = "gs://xxx.appspot.com/";
    private TextView usernameTV;
    private String username;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_profile_photo);

        usernameTV = findViewById(R.id.usernameTV);
        see_profile_photo = findViewById(R.id.see_profile_photo);
        profile_image_circle = findViewById(R.id.profile_image_circle);


        Intent intent = getIntent();
        //user has been clicked
        userID = intent.getStringExtra("userID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("profileDetail");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                username = snapshot.child(userID).child("username").getValue(String.class);
                //Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
                usernameTV.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        //showing profile photo on circle image
        showCircleProfileImage(userID);
        //showing profile photo big screen
        showProfilePhoto(userID);


    }


    private void showCircleProfileImage(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeProfilePhotoActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(profile_image_circle);
    }

    private void showProfilePhoto(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeProfilePhotoActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(see_profile_photo);
    }
}
