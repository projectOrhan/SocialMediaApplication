package com.example.socialmedia;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmedia.model.ProfileDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisteryPage extends AppCompatActivity {
    private EditText usernameTV, ageTV, genderTV, emailTV, passwordTV;
    private TextView uyariMesaji;
    private Button olusturButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database ;
    private DatabaseReference myRef ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registery_page);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        ageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] ageSelectionList = new String[87];
                for(int i = 0 ; i < ageSelectionList.length; i++)
                {
                   ageSelectionList[i] = String.valueOf(i+14);
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(RegisteryPage.this);
                builder.setTitle("Yaşınızı seçiniz.");
                builder.setItems(ageSelectionList, new DialogInterface.OnClickListener() {@
                        Override
                public void onClick(DialogInterface dialog, int which) {
                    ageTV.setText(ageSelectionList[which]);
                    // the user clicked on colors[which]
                }
                });
                builder.show();
            }
        });

        genderTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] genders = {
                        "Erkek", "Kadın", "Diğer"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisteryPage.this);
                builder.setTitle("Cinsiyetinizi Seçiniz.");
                builder.setItems(genders, new DialogInterface.OnClickListener() {@
                        Override
                public void onClick(DialogInterface dialog, int which) {
                    genderTV.setText(genders[which]);
                    // the user clicked on colors[which]
                }
                });
                builder.show();
            }
        });

        olusturButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser(){
            String email, password, username, age, gender;
            email = emailTV.getText().toString();
            password = passwordTV.getText().toString();
            username = usernameTV.getText().toString();
            age = ageTV.getText().toString();
            gender = genderTV.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(age) || TextUtils.isEmpty(gender))
            {
                uyariMesaji.setText("Boş alan bırakmadan doldurunuz.");
                return;
            }

            if(!email.contains("@gmail.com") && !email.contains("@hotmail.com") && !email.contains("@outlook.com"))
            {
                 uyariMesaji.setText("Geçerli bir email adresi giriniz.");
                 return;
            }
            if(password.length() < 6)
            {
                 uyariMesaji.setText("Şifreniz en az 6 karakter uzunluğunda olmalıdır.");
                 return;
            }
            if(username.length() < 6)
            {
                uyariMesaji.setText("Kullanıcı adınız en az 6 karakter uzunluğunda olmalıdır.");
                return;
            }
            else {
                uyariMesaji.setText("");
            }
            progressBar.setVisibility(View.VISIBLE);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("profileDetail");
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisteryPage.this, "Kayıt başarılı!", Toast.LENGTH_LONG).show();
                                ProfileDetail newProfileDetail = new ProfileDetail();
                                String id = mAuth.getUid();
                                newProfileDetail.setUid(id);
                                newProfileDetail.setUsername(username);
                                newProfileDetail.setEmail(email);
                                newProfileDetail.setPassword(password);
                                newProfileDetail.setAge(Integer.parseInt(age));
                                newProfileDetail.setGender(gender);
                                newProfileDetail.setOnlineStatus("offline");
                                myRef.child(id).setValue(newProfileDetail);
                                progressBar.setVisibility(View.GONE);

                                //clear all
                                clearTextFields();
                            }
                            else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                //hata koduna göre kullanıcıya geri bildirim.
                                switch (errorCode)
                                {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        uyariMesaji.setText("Böyle bir email zaten kullanımda.");
                                        break;
                                }
                                Toast.makeText(RegisteryPage.this, "Kayıt başarısız!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }

    private void clearTextFields() {
        emailTV.setText("");
        passwordTV.setText("");
        usernameTV.setText("");
        ageTV.setText("");
        genderTV.setText("");
        uyariMesaji.setText("");
    }

    private void initializeUI() {
        uyariMesaji = findViewById(R.id.uyariMesaji);
        emailTV = findViewById(R.id.emailTV);
        passwordTV = findViewById(R.id.passwordTV);
        usernameTV = findViewById(R.id.usernameTV);
        ageTV = findViewById(R.id.ageTV);
        genderTV = findViewById(R.id.cinsiyetTV);
        olusturButton = findViewById(R.id.olusturButton);
        progressBar = findViewById(R.id.progressBar);
    }
}


