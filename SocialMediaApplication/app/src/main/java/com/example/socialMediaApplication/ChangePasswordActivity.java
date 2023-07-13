package com.example.socialMediaApplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import static android.widget.Toast.LENGTH_SHORT;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView current_passwordTV, new_passwordTV, new_password_againTV;
    EditText current_password, new_password, new_password_again;
    Button btn_save;
    String newPassword, password;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

        initializeUI();

        new AsyncTaskExampleTask().execute();

        current_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    current_passwordTV.setText("Mevcut Şifre");
                    new_passwordTV.setText("");
                    new_password_againTV.setText("");
                    if (current_password.getHint().toString().equals("Mevcut Şifre") || current_password.getHint().toString().equals("Şifrenizi doğru giriniz."))
                    {
                        current_password.setHint("");
                    }
                }
                else
                {
                    if (current_password.getHint().toString().equals(""))
                    {

                        current_password.setHint("Mevcut Şifre");
                    }
                    if(!current_password.getText().toString().equals(password) && !current_password.getText().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"girdi", LENGTH_SHORT).show();
                        current_password.setText("");
                        current_password.setHint("Şifrenizi doğru giriniz.");
                    }
                }
            }
        });

        new_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    new_passwordTV.setText("Yeni Şifre");
                    current_passwordTV.setText("");
                    new_password_againTV.setText("");
                    if (new_password.getHint().toString().equals("Yeni Şifre"))
                    {
                        new_password.setHint("");
                    }
                }
                else
                {
                    if (new_password.getHint().toString().equals(""))
                    {
                        new_password.setHint("Yeni Şifre");
                    }
                    if (!new_password.getText().toString().equals(""))
                    {
                        newPassword = new_password.getText().toString();
                        Toast.makeText(getApplicationContext(), newPassword, LENGTH_SHORT).show();
                    }
                }
            }
        });

        new_password_again.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    new_password_againTV.setText("Yeni Şifre (tekrar)");
                    current_passwordTV.setText("");
                    new_passwordTV.setText("");
                    if (new_password_again.getHint().toString().equals("Yeni Şifre (tekrar)"))
                    {
                        new_password_again.setHint("");
                    }
                }
                else
                {
                    if (new_password_again.getHint().toString().equals(""))
                    {
                        new_password_again.setHint("Yeni Şifre (tekrar)");
                    }
                }
            }
        });

        current_password.addTextChangedListener(new TextWatcher() {
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

        new_password.addTextChangedListener(new TextWatcher() {
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

        new_password_again.addTextChangedListener(new TextWatcher() {
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
                if (!new_password_again.getText().toString().equals(newPassword))
                {
                    Toast.makeText(ChangePasswordActivity.this,"Yeni şifreler eşleşmiyor.", LENGTH_SHORT).show();
                }
                else if (!current_password.getText().toString().equals(password))
                {
                    Toast.makeText(ChangePasswordActivity.this,"Şifrenizi doğru girmediniz.", LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChangePasswordActivity.this,"Talebiniz alındı.Lütfen bekleyiniz...", LENGTH_SHORT).show();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),password);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete( Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        user.updatePassword(new_password_again.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete( Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(ChangePasswordActivity.this,"Şifre başarıyla değiştirildi.", Toast.LENGTH_LONG).show();
                                                    FirebaseDatabase.getInstance().getReference("profileDetail").child(user.getUid()).child("password").setValue(new_password_again.getText().toString());
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(),"Şifre güncellenemedi.Tekrar Deneyiniz.", LENGTH_SHORT).show();
                                                }
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
        if(!current_password.getText().toString().equals("") && !current_password.getText().toString().equals("Mevcut Şifre")
        && !new_password.getText().toString().equals("") && !new_password.getText().toString().equals("Yeni Şifre")
        && !new_password_again.getText().toString().equals("") && !new_password_again.getText().toString().equals("Yeni Şifre (tekrar)"))
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
        current_passwordTV = findViewById(R.id.current_passwordTV);
        new_passwordTV = findViewById(R.id.new_passwordTV);
        new_password_againTV = findViewById(R.id.new_password_againTV);

        current_password = findViewById(R.id.set_usernameET);
        new_password = findViewById(R.id.passwordET);
        new_password_again = findViewById(R.id.new_password_again);

        btn_save = findViewById(R.id.btn_save);
    }

    class AsyncTaskExampleTask extends AsyncTask<Void, Void, Void>{

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
