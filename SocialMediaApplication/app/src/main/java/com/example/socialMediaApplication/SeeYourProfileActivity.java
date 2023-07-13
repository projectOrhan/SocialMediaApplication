package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class SeeYourProfileActivity extends AppCompatActivity {


    String userID;
    ImageView profileImage,profileImageCircle;
    //define your database url as String DATABASE_URL = "database-url.com/";
    //String DATABASE_URL = "gs://xxx.appspot.com/";
    private String username,description;
    private TextView usernameTV,descriptionTV,likesTV,chatsTV,followersTV;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_your_profile);

        usernameTV = findViewById(R.id.usernameTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        likesTV = findViewById(R.id.likes_TV);
        chatsTV = findViewById(R.id.chats_TV);
        followersTV = findViewById(R.id.followers_TV);

        profileImage = findViewById(R.id.profileIV);
        profileImageCircle = findViewById(R.id.profile_IV_circle);

        Intent intent = getIntent();
        //user has been clicked
        userID = intent.getStringExtra("userID");

        //girilen kullanıcının username i falan yazacak.

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("profileDetail");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                username = snapshot.child(userID).child("username").getValue(String.class);
                description = snapshot.child(userID).child("description").getValue(String.class);
                //Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
                usernameTV.setText(username);
                descriptionTV.setText(description);
                likesTV.setText(snapshot.child(userID).child("likes").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //chat sayısını yazar.
        DatabaseReference chatdbRef = database.getReference("Chats").child(userID);
        chatdbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()> 0) {
                    String a = String.valueOf(snapshot.getChildrenCount());
                    chatsTV.setText(a);
                }
                else {
                    chatsTV.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //followers-takipci sayısını yazar.
        DatabaseReference followersRef = database.getReference("FollowersList").child(userID);
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()> 0) {
                    String a = String.valueOf(snapshot.getChildrenCount());
                    followersTV.setText(a);
                }
                else {
                    followersTV.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //showing profile photo on circle image
        showCircleProfileImage(userID);
        //showing profile photo big screen
        showProfilePhoto(userID);

        //profil fotosuna tıklandığında profil fotoğrafını büyük ekranda görmek için aktivitenin başlatılması.
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeYourProfileActivity.this, SeeProfilePhotoActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

    }


    private void showCircleProfileImage(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeYourProfileActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImageCircle);
    }

    private void showProfilePhoto(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeYourProfileActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImage);
    }
}
