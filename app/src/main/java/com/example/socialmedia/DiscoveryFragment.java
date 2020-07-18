package com.example.socialmedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialmedia.model.ProfileDetail;
import com.example.socialmedia.model.ProfileList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.List;

public class DiscoveryFragment extends Fragment {
    String DATABASE_URL = "place your Storage database url";
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    ListView usersListView;
    List<String> mUsers = new ArrayList<>();
    List<String> mDescripton = new ArrayList<>();
    List<StorageReference> refList = new ArrayList<>();
    private ProfileList profileList = new ProfileList();
    private List<ProfileDetail> profileDetailList = new ArrayList<>();
    private String receiverUserID, currentUserID;
    MyAdapter adapter;
    View view;
    BottomNavigationView bottomNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        //just change the fragment_messages
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_messages
        //return inflater.inflate(R.layout.fragment_home, null);

        view = inflater.inflate(R.layout.fragment_discovery, null);
        bottomNavigationView = view.findViewById(R.id.navigation);
        usersListView = view.findViewById(R.id.discoverList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        updateList(mUsers, mDescripton, refList);
        //

        //now set item click on list view
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getContext(), "Mesaj isteği gönderildi.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),MessageActivity.class);
                intent.putExtra("userid",profileList.getProfileDetail().get(position).getUid());
                startActivity(intent);
            }
        });
    }

    private void updateList(List<String> mUsers, List<String> mDescripton, List<StorageReference> refList) {
        FirebaseUser fUser;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef.child("profileDetail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                mDescripton.clear();
                refList.clear();
                profileDetailList.clear();
                profileList.setProfileDetail(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Toast.makeText(getActivity(),"veriler geliyor",Toast.LENGTH_SHORT).show();
                    ProfileDetail userDetail = snapshot.getValue(ProfileDetail.class);
                    //dont show users own profile on discovery page
                    if (!userDetail.getUid().matches(fUser.getUid())){
                        profileDetailList.add(userDetail);
                        mUsers.add(snapshot.child("username").getValue(String.class));
                        mDescripton.add(snapshot.child("uid").getValue(String.class));
                        refList.add(FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ snapshot.getKey()+"/image"));
                    }
                }
                profileList.setProfileDetail(profileDetailList);
                adapter = new MyAdapter(view.getContext(), mUsers, mDescripton, refList);
                adapter.notifyDataSetChanged();
                usersListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void fetchData(StorageReference ref,ImageView images) {

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
        List<StorageReference> refList;

        MyAdapter(Context c, List<String> title, List<String> description, List<StorageReference> refList) {
            super(c, R.layout.row, R.id.usersUsernameTV,title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.refList = refList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent,false);
            ImageView images = row.findViewById(R.id.usersImage);
            ImageView online_statusIV = row.findViewById(R.id.online_statusIV);
            TextView myTitle = row.findViewById(R.id.usersUsernameTV);
            TextView myDescription = row.findViewById(R.id.usersDescriptionTV);

            fetchData(refList.get(position),images);
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));

            //green ball when online
            if (profileList.getProfileDetail().get(position).getOnlineStatus().equals("Online"))
            {
                online_statusIV.setVisibility(View.VISIBLE);
            }

            images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),profileDetailList.get(position).getUid(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),SeeProfilePhoto.class);
                    intent.putExtra("userID",profileDetailList.get(position).getUid());
                    startActivity(intent);
                }
            });

            return row;
        }
    }
}