package com.example.to_dolist;  // Defines the package (namespace) this class belongs to

import android.content.Intent;  // Used to start a new activity
import android.os.Bundle;
import android.os.Handler;  // Allows us to delay the transition using a timer

import androidx.activity.EdgeToEdge;  // Enables edge-to-edge layout (Android 13+)
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// SplashScreen is the first screen shown when the app launches
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Call superclass to initialize activity

        EdgeToEdge.enable(this);  // Enable edge-to-edge layout (makes UI look more immersive)

        setContentView(R.layout.activity_splash_screen);  // Set the layout XML for this screen

        // Handle padding for system bars (like status bar or navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Get sizes of system UI (top bar, bottom nav)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply padding so content doesn't overlap with system bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay for 3 seconds (3000 milliseconds) before starting MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create intent to move from SplashScreen to MainActivity
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);  // Launch MainActivity
                finish();  // Close SplashScreen so user can't go back to it
            }
        }, 3000);  // 3 seconds delay
    }
}
