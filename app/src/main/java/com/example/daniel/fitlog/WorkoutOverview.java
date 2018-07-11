package com.example.daniel.fitlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WorkoutOverview extends AppCompatActivity {

    Intent intentReceived;
    Bundle extrasReceived;
    String chosenWorkout;
    TextView topText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_overview);

        intentReceived = getIntent();
        extrasReceived = intentReceived.getExtras();
        chosenWorkout = extrasReceived.getString("chosenWorkout");

        topText = findViewById(R.id.topText);

        topText.setText("All "+chosenWorkout.toLowerCase() + " workouts");

    }
}
