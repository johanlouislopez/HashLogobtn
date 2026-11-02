package com.example.hashlogobtn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ImageButton btnBack = findViewById(R.id.btnBack2);
        ImageButton btnTogglePassword = findViewById(R.id.btnTogglePassword);
        TextView tvGoSignUp = findViewById(R.id.tvGoSignUp);

        // Arrow button -> Home Page (MainActivity)
        btnBack.setOnClickListener(v -> goToHomePage());

        // Phone's own back button/gesture -> Home Page (MainActivity)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToHomePage();
            }
        });

        // Eye icon: show / hide password
        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Bottom "Sign up" -> Registration (MainActivity3)
        tvGoSignUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this, MainActivity3.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToLoggedInHome();
        }
    }

    private void goToHomePage() {
        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        goToLoggedInHome();
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";
                        Toast.makeText(this, "Login failed: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToLoggedInHome() {
        Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}