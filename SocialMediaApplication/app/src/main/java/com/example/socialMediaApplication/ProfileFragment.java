package com.example.socialMediaApplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialMediaApplication.model.ProfileDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    //put your database url as String DATABASE_URL = "database-url.com/";
    //String DATABASE_URL = "gs://xxx.appspot.com/";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth;

    ImageView profilePhoto, settingsIV;
    TextView seeProfileText,username,likes, chatsTV, followersTV;
    ProfileDetail profileDetail = new ProfileDetail();
    BottomNavigationView bottomNavigationView;
    Uri filePath;
    String USERID;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, null);

        seeProfileText = view.findViewById(R.id.seeProfileText);
        username = view.findViewById(R.id.usernameTV);
        likes = view.findViewById(R.id.likes_TV);
        chatsTV = view.findViewById(R.id.chats_TV);
        followersTV = view.findViewById(R.id.followers_TV);

        profilePhoto = view.findViewById(R.id.profilePhoto);
        settingsIV = view.findViewById(R.id.settingsIV);
        bottomNavigationView = view.findViewById(R.id.navigation);
        mAuth = FirebaseAuth.getInstance();
        //login işleminden sonra username çekilir.
        String intentGelenStringUID = getArguments().getString("sending_uid");
        USERID = intentGelenStringUID;
        new ArkaPlanTask(intentGelenStringUID).execute();
        fetchData(intentGelenStringUID);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = new String[3];
                options[0] = "Profil Fotoğrafını Değiştir";
                options[1] = "Profil Fotoğrafını Kaldır";
                options[2] = "Profil Fotoğrafını Gör";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(),options[which],Toast.LENGTH_SHORT).show();
                    if (which ==0)
                    {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        int PICK_IMAGE_REQUEST = 111;
                        startActivityForResult(Intent.createChooser(intent, "Fotoğraf seç"), PICK_IMAGE_REQUEST);
                    }
                    if (which ==1)
                    {
                        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+intentGelenStringUID+"/image");
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Toast.makeText(getActivity(),"Başarılı",Toast.LENGTH_SHORT).show();
                                fetchData(intentGelenStringUID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure( Exception exception) {
                                // Uh-oh, an error occurred!
                                Toast.makeText(getActivity(),"Başaarısız",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (which ==2)
                    {
                        Toast.makeText(getActivity(),options[which],Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), SeeProfilePhotoActivity.class);
                        intent.putExtra("userID",intentGelenStringUID);
                        startActivity(intent);
                    }

                    // the user clicked on colors[which]
                }
                });
                builder.show();
            }
        });

        seeProfileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //profili görmek için tıkla yazısına tıklandığında yapılacaklar,,
                Intent seeProfileIntent = new Intent(getContext(), SeeYourProfileActivity.class);
                String id = intentGelenStringUID;
                seeProfileIntent.putExtra("userID",id);
                startActivity(seeProfileIntent);

            }
        });


        settingsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private class ArkaPlanTask extends AsyncTask<List<String>, Void, List<String>> {
        String userID;
        public ArkaPlanTask(String userID) {
            this.userID = userID;
        }

        @Override
        protected List<String> doInBackground(List<String>... lists) {

            DatabaseReference ref = database.getReference("profileDetail");

            ref.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        //kullanıcı veritabanında bulunuyorsa.
                        profileDetail = dataSnapshot.getValue(ProfileDetail.class);
                        username.setText(profileDetail.getUsername());
                        likes.setText(profileDetail.getLikes());
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });

            //chat sayısını yazar.
            DatabaseReference chatdbRef = database.getReference("Chats").child(userID);
            chatdbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.getChildrenCount()> 0) {
                            String a = String.valueOf(snapshot.getChildrenCount());
                            chatsTV.setText(a);
                        }
                        else {
                            chatsTV.setText("0");
                        }
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
                        //Toast.makeText(getActivity().getApplicationContext(), a + "Takipçi var", Toast.LENGTH_SHORT).show();
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

            return null;
        }
    }

    private void fetchData(String stringProfileId) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(getActivity())
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int PICK_IMAGE_REQUEST = 111;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
        if(filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            //firebase storage referans urlsi griliecek. put your firebase storage url reference here
            //example             StorageReference storageRef = storage.getReferenceFromUrl("your-firebase-url/");
            //StorageReference storageRef = storage.getReferenceFromUrl("gs://xxx.appspot.com/");

            StorageReference childRef = storageRef.child(USERID+"/image");
            ProgressDialog pd;
            pd = new ProgressDialog(getContext());
            pd.setMessage("Yüklüyor...Lütfen bekleyin.");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);
            pd.show();
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Yükleme başarılı.", Toast.LENGTH_SHORT).show();
                    fetchData(USERID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Yükleme başarız oldu -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Bir fotoğraf seç", Toast.LENGTH_SHORT).show();
        }
    }
}
