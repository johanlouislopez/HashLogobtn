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
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity3 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etFullName, etEmail, etPassword, etConfirm;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mAuth = FirebaseAuth.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        ImageButton btnBack = findViewById(R.id.btnBack3);
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        // Eye icons: show / hide password and confirm password
        setupPasswordToggle(etPassword, (ImageButton) findViewById(R.id.btnTogglePassword3));
        setupPasswordToggle(etConfirm, (ImageButton) findViewById(R.id.btnToggleConfirm));

        // Arrow button -> Home Page (MainActivity)
        btnBack.setOnClickListener(v -> goToHomePage());

        // Phone's own back button/gesture -> Home Page (MainActivity)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToHomePage();
            }
        });

        // Bottom "Log in" -> Login (MainActivity2)
        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity3.this, MainActivity2.class));
            finish();
        });

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void setupPasswordToggle(EditText editText, ImageButton toggleButton) {
        final boolean[] visible = {false};
        toggleButton.setOnClickListener(v -> {
            visible[0] = !visible[0];
            if (visible[0]) {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                toggleButton.setImageResource(R.drawable.ic_eye_off);
            } else {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                toggleButton.setImageResource(R.drawable.ic_eye);
            }
            editText.setSelection(editText.getText().length());
        });
    }

    private void goToHomePage() {
        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }
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
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            etConfirm.setError("Passwords do not match");
            etConfirm.requestFocus();
            return;
        }

        btnSignUp.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profile).addOnCompleteListener(t -> {
                                Toast.makeText(this, "Registration successful",
                                        Toast.LENGTH_SHORT).show();
                                goToLoggedInHome();
                            });
                        }
                    } else {
                        btnSignUp.setEnabled(true);
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";
                        Toast.makeText(this, "Registration failed: " + msg,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToLoggedInHome() {
        Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}