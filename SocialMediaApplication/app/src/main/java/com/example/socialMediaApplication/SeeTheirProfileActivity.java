package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialMediaApplication.model.ProfileDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;

public class SeeTheirProfileActivity extends AppCompatActivity {


    String userID;
    ImageView profileImage;
    //define your database url as String DATABASE_URL = "database-url.com/";
    //String DATABASE_URL = "gs://xxx.appspot.com/";
    private ProfileDetail profileDetail ;
    private Button btn_chat,btn_follow;
    private TextView usernameTV,descriptionTV,likesTV,chatsTV,followersTV,onlineStatusTV;
    private String username,description;
    FirebaseAuth mAuth;

    //takip etme butonu değişkeni. false ise takip edebilir, true ise takip etme işlemi yapamz. zaten yapmış
    private Boolean canFollow;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_their_profile);

        profileImage = findViewById(R.id.profileIV);
        btn_chat = findViewById(R.id.btn_chat);
        btn_follow = findViewById(R.id.btn_follow);
        usernameTV = findViewById(R.id.usernameTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        chatsTV = findViewById(R.id.chats_TV);
        likesTV = findViewById(R.id.likes_TV);
        followersTV = findViewById(R.id.followers_TV);
        onlineStatusTV = findViewById(R.id.online_statusTV);
        Intent intent = getIntent();
        //user has been clicked
        userID = intent.getStringExtra("userID");
        //uygulamyı kullanan kişinin idsini almak için bu.
        mAuth = FirebaseAuth.getInstance();

        //girilen kullanıcının username i falan yazacak.

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("profileDetail");

        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                username = snapshot.child("username").getValue(String.class);
                usernameTV.setText(username);
                description = snapshot.child("description").getValue(String.class);
                descriptionTV.setText(description);
                likesTV.setText(snapshot.child("likes").getValue(String.class));


                if (snapshot.child("onlineStatus").getValue(String.class).equals("Offline")) {
                    onlineStatusTV.setText("Offline");
                }
                else if (snapshot.child("onlineStatus").getValue(String.class).equals("Online"))
                {
                    onlineStatusTV.setText("Online");
                }
                else{
                    onlineStatusTV.setText(sonGorulmeZamaniniHesapla(snapshot.child("onlineStatus").getValue(String.class)));
                }
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
                    //Eğer en az 1 takipcisi varsa bu kısım yapılıyor ve takipci sayısı yazılıyor.
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

        DatabaseReference followingRef = database.getReference("FollowingList").child(mAuth.getUid());
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //userID yani profiline baktığımız kişinin uidsini alıyoruz. eğer onu takip ediyorsak
                String uid = mAuth.getUid();
                if (snapshot.hasChild(userID)){
                    btn_follow.setText("Takip Ediyorsun");
                    canFollow = false;
                }
                else{
                    btn_follow.setText("Takip Et");
                    canFollow = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        //showing profile photo on circle image, profil fotoğrafını küçük daire içinde gösteriyor.
        showCircleProfileImage(userID);
        //showing profile photo big screen //profil fotoğrafını büyük ekranda gösterme işlemi.
        showProfilePhoto(userID);


        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chat yapmak için butona tıklandığında yapılacaklar

                Intent chatIntent = new Intent(SeeTheirProfileActivity.this, MessageActivity.class);
                chatIntent.putExtra("userID",userID);
                startActivity(chatIntent);
            }
        });

        //profil fotosuna tıklandığında büyük ekranda göstermesi.
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeTheirProfileActivity.this, SeeProfilePhotoActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = mAuth.getUid();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference followingListRef = db.getReference("FollowingList");
                DatabaseReference followersListRef = db.getReference("FollowersList");

                if (canFollow){
                    //yani kullanıcı takip etme işlemi yapabilir.
                    followingListRef.child(uid).child(userID).setValue("Takip Ediliyor.");
                    followersListRef.child(userID).child(uid).setValue("Takip Ediyor");
                }else
                {
                    followingListRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                //eğer takip ettiğimiz tek hesap bu profil ise,
                                if (snapshot.getChildrenCount()>1){
                                    //eğer tek hesap o değilse followinglist tablosundan o hesabı child olmaktan çıkart
                                    followingListRef.child(uid).child(userID).removeValue();
                                }else{
                                    //eğer tek hesap bu profil ise boş küme yap
                                    followingListRef.child(uid).setValue("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });

                    followersListRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                //eğer sadece biz bu hesabı takip ediyorsak,
                                if (snapshot.getChildrenCount()>1){
                                    //eğer sadece takip eden biz değilsek baska hesaplar da varsa bizi takipçi olmaktan çıkart sadece
                                    followersListRef.child(userID).child(uid).removeValue();
                                }else{
                                    //eğer bu hesabın tek takipcisi biz isek
                                    followersListRef.child(userID).setValue("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });


                }
            }
        });

    }


    private void showCircleProfileImage(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeTheirProfileActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage);
    }

    private void showProfilePhoto(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(SeeTheirProfileActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImage);
    }

    private String sonGorulmeZamaniniHesapla(String onlineStatus) {
        //convert timestamp to proper time
        String sonGorulmeZamani;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(onlineStatus));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
        sonGorulmeZamani = dateTime;

        return sonGorulmeZamani;
    }
}
