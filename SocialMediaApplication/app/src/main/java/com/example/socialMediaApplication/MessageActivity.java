package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialMediaApplication.model.Chat;
import com.example.socialMediaApplication.model.ProfileDetail;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {
    //put your database url as String DATABASE_URL = "database-url.com/";
    //String DATABASE_URL = "gs://xxx.appspot.com/";
    TextView username, online_statusTV;
    ImageView profile_image;

    RecyclerView recyclerView;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    List<Chat> chatList  = new ArrayList<>();
    AdapterChat adapterChat;
    String userID;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recycler_view);
        profile_image = findViewById(R.id.profile_IV_circle);
        username = findViewById(R.id.usernameTV);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        online_statusTV = findViewById(R.id.online_statusTV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterChat);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");


        reference = FirebaseDatabase.getInstance().getReference();

            reference.child("profileDetail").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    ProfileDetail userDetail = dataSnapshot.getValue(ProfileDetail.class);
                    username.setText(userDetail.getUsername());
                    fetchData(userDetail.getUid());
                    String onlineStatus = userDetail.getOnlineStatus();
                    if (onlineStatus.equals("Online") || onlineStatus.equals("Offline")){
                        online_statusTV.setText(onlineStatus);
                    }

                    else{
                        //convert timestamp to proper time
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(onlineStatus));
                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                        online_statusTV.setText("Last Seen: "+dateTime);
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = text_send.getText().toString();
                if(!message.equals("")){
                    sendMessage(fuser.getUid(), userID, message);

                }else{
                    Toast.makeText(MessageActivity.this,"Boş mesaj gönderemezsiniz.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, SeeTheirProfileActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        readMessages();
        seenMessage();
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userID);

        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userID)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("seen", true);
                        ds.getRef().updateChildren(hasSeenHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete( Task<Void> task) {
                                if (task.isSuccessful()){
                                    //will be checked.
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats").child(userID).child(fuser.getUid());

        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userID)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("seen", true);
                        ds.getRef().updateChildren(hasSeenHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete( Task<Void> task) {
                                if (task.isSuccessful()){
                                    //will be checked
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    private void fetchData(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(MessageActivity.this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(profile_image);
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timeStamp",timestamp);
        hashMap.put("seen",false);
        reference.child(sender).child(receiver).push().setValue(hashMap);
        reference.child(receiver).child(sender).push().setValue(hashMap);

        text_send.setText("");
    }

    private void readMessages() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.child(fuser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Chat chat = ds.getValue(Chat.class);
                    chatList.add(chat);
                }
                adapterChat = new AdapterChat(MessageActivity.this, chatList, userID);
                adapterChat.notifyDataSetChanged();
                recyclerView.setAdapter(adapterChat);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
