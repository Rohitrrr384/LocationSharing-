package com.example.locationsharing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText etUserId;
    private Button btnSharer, btnViewer;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        initViews();

        // Disable buttons until Firebase anonymous sign-in succeeds
        btnSharer.setEnabled(false);
        btnViewer.setEnabled(false);

        // Sign in anonymously
        signInAnonymously();

        // Setup button click listeners
        setupClickListeners();
    }

    private void initViews() {
        etUserId = findViewById(R.id.etUserId);
        btnSharer = findViewById(R.id.btnSharer);
        btnViewer = findViewById(R.id.btnViewer);
    }

    private void setupClickListeners() {
        btnSharer.setOnClickListener(v -> {
            String userId = etUserId.getText().toString().trim();
            if (!userId.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, SharerActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a User ID", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewer.setOnClickListener(v -> {
            String userId = etUserId.getText().toString().trim();
            if (!userId.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ViewerActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a User ID to track", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (task.isSuccessful() && user != null) {
                        Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                        btnSharer.setEnabled(true);
                        btnViewer.setEnabled(true);
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        btnSharer.setEnabled(false);
                        btnViewer.setEnabled(false);
                    }
                });
    }
}
