package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity  extends AppCompatActivity {

    Button btn_logout, btn_changePassword, btn_delete_account, btn_changeUsername;
    FirebaseAuth fAuth;
    String password;
    final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("profileDetail");
    final DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
    final DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("FollowersList");
    final DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("FollowingList");

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btn_delete_account = findViewById(R.id.btn_delete_account);
        btn_changePassword = findViewById(R.id.btn_changePassword);
        btn_changeUsername = findViewById(R.id.btn_changeUsername);

        btn_logout = findViewById(R.id.btn_logout);

        fAuth = FirebaseAuth.getInstance();

        new AsyncTaskExampleTask().execute();


        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), password);

                //your database url for deletion of account. example: String DATABASE_URL = "your-database-url/";
                //final String DATABASE_URL = "gs://xxx.appspot.com/";
                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete( Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myRef.child(user.getUid()).removeValue();
                                    //chatsRef.child(user.getUid()).removeValue();

                                    followingRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            //Önce silinecek hesabın takip ettiği kullanıcıları bul.
                                            if (snapshot.exists()){
                                                if (snapshot.getChildrenCount()>0){
                                                    //en az 1 kişiyi takip ediyorsa o kiişileri bul
                                                    for (DataSnapshot data : snapshot.getChildren()){
                                                        //takip ettiğimiz kişinin follower listesinden bu hesabı kaldırıyoruz.
                                                        followersRef.child(data.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot snapshot) {
                                                                //eğer takip ettiğimiz kişinin sadece tek takipcisi biz isek,
                                                                if (snapshot.exists()){
                                                                    if (snapshot.getChildrenCount()>1){
                                                                        followersRef.child(data.getKey()).child(user.getUid()).removeValue();
                                                                    }else{
                                                                        //eğer tek takipcisi biz isek, boş küme olarak yap. takipcilistesini.
                                                                        followersRef.child(data.getKey()).setValue("");
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError error) {

                                                            }
                                                        });
                                                        //ardından  takip ettğimiz hesaplar listemizden de bu hesapları kaldırıyoruz. hesap silinince followinglistte bir uidmiz bulunmamalı.
                                                        followingRef.child(user.getUid()).child(data.getKey()).removeValue();
                                                    }
                                                }else{
                                                    //bu kulllanıcı hiç kimseyi takip etmiyorsa. takip ettikleri listesindeki kaydını sil.
                                                    followingRef.child(user.getUid()).removeValue();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });

                                    followersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            //hesabı takip eden kullanıcıları bul, onları takipçi olmaktan çıkarmadan önce o kişilerin takiplistesini bul ve oradan sil.
                                            if (snapshot.exists()){
                                                if (snapshot.getChildrenCount()>0){
                                                    //en az 1 takipçisi varsa onları bul.

                                                    for (DataSnapshot data : snapshot.getChildren()){

                                                        followingRef.child(data.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot snapshot) {
                                                                if (snapshot.exists()){
                                                                    //eğer bu hesabı takip eden kişi sadece bu hesabı takip ediyorsa,
                                                                    if (snapshot.getChildrenCount()>1){
                                                                        //eğer takip ettiği başka hesaplar da var ise sadece bu hesabı followinglistesinden sil.
                                                                        //bu hesabı takip eden kişinin following listesinden bu hesabı kaldırıyoruz. böylece hesap sildiğimizde diğer kullanıcı halen bu hesabı takip ediyor olmayacak.
                                                                        followingRef.child(data.getKey()).child(user.getUid()).removeValue();
                                                                    }else{
                                                                        //eğer tek takip ettiği hesap bu silinen hesap ise
                                                                        followingRef.child(data.getKey()).setValue("");
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError error) {

                                                            }
                                                        });
                                                        //ardından bizim takipçiler listemizden de bu hesapları kaldırıyoruz.
                                                        followersRef.child(user.getUid()).child(data.getKey()).removeValue();
                                                    }
                                                }else{
                                                    followersRef.child(user.getUid()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });

                                    chatsRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            //önce kullanıcının chat yaptığı kullanıcılar bulunacak.
                                            if (snapshot.exists()){
                                                if (snapshot.getChildrenCount()>0){
                                                    //eğer en az bir kişiyle bile sohbet etmişse
                                                    //sohbet ettiği kullanıcıları bulacağız.

                                                    for (DataSnapshot data : snapshot.getChildren()){

                                                        followingRef.child(data.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot snapshot) {
                                                                if (snapshot.exists()){
                                                                    //eğer bu hesabla chat yapan kişi sadece bu hesapla chat yapmışsa,
                                                                    if (snapshot.getChildrenCount()>1){
                                                                        //eğer chat yaptığı başka hesaplar da var ise sadece bu hesabı chats tablosundan sil.
                                                                        //bu hesabı takip eden kişinin following listesinden bu hesabı kaldırıyoruz. böylece hesap sildiğimizde diğer kullanıcı halen bu hesabı takip ediyor olmayacak.
                                                                        chatsRef.child(data.getKey()).child(user.getUid()).removeValue();
                                                                    }else{
                                                                        //eğer chat yaptığı tek hesap bu silinecek olan hesap ise
                                                                        chatsRef.child(data.getKey()).setValue("");
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError error) {

                                                            }
                                                        });
                                                        //ardından bu hesabın chats listesinden de bu hesapları kaldırıyoruz.
                                                        chatsRef.child(user.getUid()).child(data.getKey()).removeValue();
                                                    }


                                                }else{
                                                    //eğer sohbet ettiği kimse yoksa chat tablosunu direkt tamamen sileceğiz.
                                                    chatsRef.child(user.getUid()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });


                                    FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+user.getUid()+"/image").delete();
                                }

                            }
                        });
                    }
                });

                Toast.makeText(getApplicationContext(),"Hesabın silindi",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, StartPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });



        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();

            }
        });

        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        btn_changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangeUsernameActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logOutUser() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = fUser.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("profileDetail").child(myUid);

        fAuth.signOut();
        fAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    String timeStampStatus = String.valueOf(System.currentTimeMillis());
                    //set offline with last seen time stamp
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("onlineStatus", timeStampStatus);
                    //update value of onlineStatus of current user
                    dbRef.updateChildren(hashMap);
                    Intent intentService = new Intent(SettingsActivity.this,MyService.class);
                    stopService(intentService);
                    Toast.makeText(SettingsActivity.this,"Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this, StartPageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    class AsyncTaskExampleTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userID = user.getUid();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("profileDetail");
            db.child(userID).child("password").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    password = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });

            return null;
        }
    }
}


