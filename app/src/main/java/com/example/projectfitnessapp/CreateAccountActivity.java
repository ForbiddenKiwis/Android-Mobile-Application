package com.example.projectfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.User;
import model.Person;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // UI Elements
    private EditText etUsername, etPassword, etConfirmPassword, etName, etAge, etHeight, etWeight;
    private Button btnCreateAccount, btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acount);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnGoBack = findViewById(R.id.btnGoBack);

        // Set up Create Account button
        btnCreateAccount.setOnClickListener(v -> createAccount());

        // Set up Go Back button to navigate to LoginActivity
        btnGoBack.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createAccount() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String name = etName.getText().toString();
        int age = Integer.parseInt(etAge.getText().toString());
        float height = Float.parseFloat(etHeight.getText().toString());
        float weight = Float.parseFloat(etWeight.getText().toString());

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String firebaseUserId = mAuth.getCurrentUser().getUid();
                        int userId = firebaseUserId.hashCode();

                        // Create User object for authentication
                        User user = new User(userId, password);
                        mDatabase.child("Users").child(firebaseUserId).setValue(user);

                        // Create Person object for user profile without BMI
                        Person person = new Person(userId, userId, name, age, weight, height, 0);
                        mDatabase.child("Persons").child(firebaseUserId).setValue(person)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Failed to save user profile!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Account Creation Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
