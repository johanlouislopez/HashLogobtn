package com.example.hashlogobtn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity4 extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        mAuth = FirebaseAuth.getInstance();

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            goToHomePage();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Nobody logged in? Send them back to the Home Page.
        if (mAuth.getCurrentUser() == null) {
            goToHomePage();
        }
    }

    private void goToHomePage() {
        Intent intent = new Intent(MainActivity4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}