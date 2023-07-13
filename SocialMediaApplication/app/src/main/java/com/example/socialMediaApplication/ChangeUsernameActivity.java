package com.example.socialMediaApplication;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialMediaApplication.model.ProfileDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ChangeUsernameActivity extends AppCompatActivity {

    EditText set_usernameET, passwordET;
    Button btn_save;
    String new_username, password;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_username_layout);

        initializeUI();

        new AsyncTaskExampleTask().execute();

        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    if (passwordET.getText().toString().equals(""))
                    {
                        passwordET.setHint("Şifrenizi giriniz.");
                    }
                    if(!passwordET.getText().toString().equals(password) && !passwordET.getText().toString().equals(""))
                    {
                        passwordET.setText("");
                        passwordET.setHint("Şifrenizi doğru giriniz.");
                    }
                }
            }
        });


        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkTextFields();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkTextFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkTextFields();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordET.getText().toString().equals(password))
                {
                    Toast.makeText(ChangeUsernameActivity.this,"Şifrenizi doğru girmediniz.", LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChangeUsernameActivity.this,"Talebiniz alındı.Lütfen bekleyiniz...", LENGTH_SHORT).show();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),password);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete( Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        //yetkilendirme başarılı, şifre doğru girildi.
                                        //artık burada yeni kullanıcı adı güncellenecek.
                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("profileDetail");
                                        String newUsername = set_usernameET.getText().toString();
                                        db.child(user.getUid()).child("username").setValue(newUsername).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ChangeUsernameActivity.this,"Kullanıcı adı değiştirildi.", LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Yetkilendirme başarısız oldu.Şifrenizi doğru giriniz.", LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkTextFields() {
        if(!passwordET.getText().toString().equals("") && !set_usernameET.getText().toString().equals(""))
        {
            btn_save.setTextColor(getResources().getColor(R.color.white));
            btn_save.setEnabled(true);
        }
        else
        {
            btn_save.setTextColor(getResources().getColor(R.color.save_button_disabled));
            btn_save.setEnabled(false);
        }
    }

    private void initializeUI() {
        set_usernameET = findViewById(R.id.set_usernameET);
        passwordET = findViewById(R.id.passwordET);
        btn_save = findViewById(R.id.btn_save);
    }

    class AsyncTaskExampleTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userID = user.getUid();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("profileDetail");
            db.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    ProfileDetail profileDetail = dataSnapshot.getValue(ProfileDetail.class);
                    password = profileDetail.getPassword();

                    new_username = profileDetail.getUsername();
                    set_usernameET.setText(new_username);
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });

            return null;
        }
    }
}
