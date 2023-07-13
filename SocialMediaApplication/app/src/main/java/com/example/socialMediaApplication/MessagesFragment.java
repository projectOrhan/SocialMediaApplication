package com.example.socialMediaApplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialMediaApplication.model.Chat;
import com.example.socialMediaApplication.model.ProfileDetail;
import com.example.socialMediaApplication.model.ProfileList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessagesFragment extends Fragment {
    //define your database url as String DATABASE_URL = "database-url.com/";
    //String DATABASE_URL = "gs://xxx.appspot.com/";
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private View view;
    private BottomNavigationView bottomNavigationView;
    private ListView messagesListView;
    private List<String> mUsers = new ArrayList<>();
    private List<String> mDescription = new ArrayList<>();
    private List<String> mTimeStamp = new ArrayList<>();
    private List<Boolean> mIsSeen = new ArrayList<>();
    private List<StorageReference> refList  = new ArrayList<>();
    private ProfileList profileList = new ProfileList();
    private List<ProfileDetail> profileDetailList = new ArrayList<>();
    private MyAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //just change the fragment_messages
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_messages
        view = inflater.inflate(R.layout.fragment_messages, null);
        bottomNavigationView = view.findViewById(R.id.navigation);
        messagesListView = view.findViewById(R.id.messagesList);

        bringChatList();

        //now set item click on list view
        messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(),MessageActivity.class);
                intent.putExtra("userID",profileList.getProfileDetail().get(position).getUid());
                startActivity(intent);
            }
        });
        return view;
    }

    private void bringChatList() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        myRef.child("Chats").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                mUsers.clear();
                mDescription.clear();
                mIsSeen.clear();
                mTimeStamp.clear();
                refList.clear();
                profileDetailList.clear();
                profileList.setProfileDetail(null);
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    String uid = snap.getKey();

                    refList.add(FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+uid+"/image"));

                    myRef.child("profileDetail").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot dataSnapshot) {


                                ProfileDetail userDetail = dataSnapshot.getValue(ProfileDetail.class);
                                mUsers.add(userDetail.getUsername());
                                profileDetailList.add(userDetail);
                        }

                        @Override
                        public void onCancelled( DatabaseError databaseError) {

                        }
                    });

                    Query chatQuery = myRef.child("Chats").child(currentUserID).child(uid).orderByKey().limitToLast(1);
                    chatQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                Chat chat = ds.getValue(Chat.class);
                                mDescription.add(chat.getMessage());
                                mIsSeen.add(chat.getSeen());
                                //convert timestamp to proper time
                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(Long.parseLong(chat.getTimeStamp()));
                                String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                                mTimeStamp.add(dateTime);
                            }
                            adapter = new MyAdapter(view.getContext(), mUsers, mDescription, mTimeStamp, mIsSeen, refList);
                            messagesListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            profileList.setProfileDetail(profileDetailList);
                        }

                        @Override
                        public void onCancelled( DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }

        });
    }


    private void fetchData(StorageReference ref, ImageView images) {

        GlideApp.with(getActivity())
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(images);
    }


    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        List<String> rTitle;
        List<String> rDescription;
        List<String> rTimeStamp;
        List<Boolean> rIsSeen;
        List<StorageReference> refList;

        MyAdapter(Context c, List<String> title, List<String> description, List<String> timeStamp, List<Boolean> isSeen, List<StorageReference> refList) {
            super(c, R.layout.row_last_messages, R.id.usersUsernameTV,title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rTimeStamp = timeStamp;
            this.rIsSeen = isSeen;
            this.refList = refList;
        }


        @Override
        public View getView(int position,  View convertView,  ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_last_messages, parent,false);
            ImageView images = row.findViewById(R.id.usersImage);
            TextView myTitle = row.findViewById(R.id.usersUsernameTV);
            TextView myDescription = row.findViewById(R.id.usersDescriptionTV);
            TextView myTimeStamp = row.findViewById(R.id.timeStampTV);
            ImageView myIsSeenIV = row.findViewById(R.id.isSeenIV);

            fetchData(refList.get(position),images);
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));
            myTimeStamp.setText(rTimeStamp.get(position));
            if (rIsSeen.get(position).equals(true))
            {
                myIsSeenIV.setImageResource(R.drawable.seen_double_tick);
            }
            else
            {
                myIsSeenIV.setImageResource(R.drawable.delivered_double_tick);
            }
            return row;
        }
    }
}
