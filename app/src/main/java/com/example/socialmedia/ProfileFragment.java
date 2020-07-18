package com.example.socialmedia;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialmedia.model.ProfileDetail;
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
    String DATABASE_URL = "place your firebase storage url";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("profileDetail");
    FirebaseAuth mAuth;

    ImageView profilePhoto, settingsIV;
    EditText editName;
    TextView userAddress,editAge,editUrl,editLocation,username;
    ProfileDetail profileDetail = new ProfileDetail();
    BottomNavigationView bottomNavigationView;
    Uri filePath;
    String USERID;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //just change the fragment_messages
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_messages
        //return inflater.inflate(R.layout.fragment_profile, null);
        //loolololoolo
        view = inflater.inflate(R.layout.fragment_profile, null);
        Button updateButton = view.findViewById(R.id.buttonUpd);
        Button deleteButton = view.findViewById(R.id.buttonDel);

        userAddress = view.findViewById(R.id.userAddress);
        editName = view.findViewById(R.id.editName);
        editAge = view.findViewById(R.id.editAge);
        editUrl = view.findViewById(R.id.editURL);
        editLocation = view.findViewById(R.id.editLocation);
        username = view.findViewById(R.id.username);
        profilePhoto = view.findViewById(R.id.profilePhoto);
        settingsIV = view.findViewById(R.id.settingsIV);
        bottomNavigationView = view.findViewById(R.id.navigation);
        mAuth = FirebaseAuth.getInstance();
        //login işleminden sonra username çekilir.
        String intentGelenStringId = getArguments().getString("sending_uid");
        USERID = intentGelenStringId;
        new ArkaPlanTask(intentGelenStringId).execute();
        fetchData(intentGelenStringId);

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
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                    }
                    if (which ==1)
                    {
                        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+intentGelenStringId+"/image");
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Toast.makeText(getActivity(),"Başarılı",Toast.LENGTH_SHORT).show();
                                fetchData(intentGelenStringId);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Toast.makeText(getActivity(),"BAŞARISIZ",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (which ==2)
                    {
                        Toast.makeText(getActivity(),options[which],Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), SeeProfilePhoto.class);
                        intent.putExtra("userID",intentGelenStringId);
                        startActivity(intent);
                    }

                    // the user clicked on colors[which]
                }
                });
                builder.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editName.getText().toString().matches("") || editAge.getText().toString().matches("") || editUrl.getText().toString().matches("") || editLocation.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Fill all the information", Toast.LENGTH_SHORT).show();
                } else {
                    //update işlemini sorunsuz gerçekleştiriyor. lakin password ve email gibi mauth için ayrıca mauth ile değişiklik yapmak gerekiyor.
                    String id = intentGelenStringId;
                    ProfileDetail newProfileDetail = new ProfileDetail();
                    newProfileDetail.setEmail(editName.getText().toString());
                    newProfileDetail.setAge(Integer.parseInt(editAge.getText().toString()));
                    newProfileDetail.setPassword(editUrl.getText().toString());
                    newProfileDetail.setGender(editLocation.getText().toString());
                    newProfileDetail.setUsername(username.getText().toString());
                    myRef.child(id).setValue(newProfileDetail);
                }
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

            DatabaseReference ref = database.getReference("profileDetail").child(userID);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        //kullanıcı veritabanında bulunuyorsa.
                        profileDetail = dataSnapshot.getValue(ProfileDetail.class);
                        username.setText(profileDetail.getUsername());
                        editName.setText(profileDetail.getEmail());
                        editAge.setText(profileDetail.getAge().toString());
                        editUrl.setText(profileDetail.getPassword());
                        editLocation.setText(profileDetail.getGender());
                        Toast.makeText(getActivity(),"Exist",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"not exist",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int PICK_IMAGE_REQUEST = 111;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
        if(filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(DATABASE_URL);
            StorageReference childRef = storageRef.child(USERID+"/image");
            ProgressDialog pd;
            pd = new ProgressDialog(getContext());
            pd.setMessage("Uploading.Please Wait...");

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);
            pd.show();
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    fetchData(USERID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getActivity(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }
}
