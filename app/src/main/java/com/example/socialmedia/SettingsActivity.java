package com.example.socialmedia;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.HashMap;

public class SettingsActivity  extends AppCompatActivity {

    Button btn_logout, btn_edit_account, btn_delete_account;
    FirebaseAuth fAuth;
    String password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btn_delete_account = findViewById(R.id.btn_delete_account);
        btn_logout = findViewById(R.id.btn_logout);
        btn_edit_account = findViewById(R.id.btn_editAccount);
        fAuth = FirebaseAuth.getInstance();

        new AsyncTaskExampleTask().execute();

        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), password);
                final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("profileDetail");
                final String DATABASE_URL = "place your firebase storage url";
                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myRef.child(user.getUid()).removeValue();
                                    FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+user.getUid()+"/image").delete();
                                    Toast.makeText(getApplicationContext(),"Hesabın silindi",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SettingsActivity.this,ActivityStart.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                });
            }
            });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();

            }
        });

        btn_edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditAccountActivity.class);
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
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
                    Intent intent = new Intent(SettingsActivity.this,ActivityStart.class);
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
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    password = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }
    }
}


