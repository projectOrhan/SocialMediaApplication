package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityStart extends AppCompatActivity {

    private Button btn_kayitOl, btn_girisYap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_kayitOl = findViewById(R.id.btn_kayitOl);
        btn_girisYap = findViewById(R.id.btn_girisYap);

        btn_kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityStart.this, RegisteryPage.class);
                startActivity(intent);
            }
        });

        btn_girisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityStart.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
