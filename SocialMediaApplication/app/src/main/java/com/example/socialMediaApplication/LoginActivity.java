package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUserAccount();
            }
        });
    }


    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Email adresinizi giriniz.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Şifrenizi giriniz.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Giriş başarılı", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);


                            //kullanıcının dashboard profil sayfasına gidecek.
                            //burada main acitivity e hangi kullanıcı ise onun uidsi gönderilecek.
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("sending_uid", mAuth.getUid());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Intent intentService = new Intent(LoginActivity.this,MyService.class);
                            startService(intentService);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Giriş başarız. Tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);
        loginBtn = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
    }

}
