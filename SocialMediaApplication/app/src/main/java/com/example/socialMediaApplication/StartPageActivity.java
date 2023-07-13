package com.example.socialMediaApplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartPageActivity extends AppCompatActivity {

    private Button btn_register, btn_login;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartPageActivity.this, RegistryPageActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartPageActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
