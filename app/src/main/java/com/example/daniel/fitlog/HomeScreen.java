package com.example.daniel.fitlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    Button newWorkoutButton;
    Button workoutHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        newWorkoutButton = (Button)findViewById(R.id.newWorkoutButton);
        workoutHistoryButton = (Button)findViewById(R.id.workoutHistoryButton);
    }

    public void startAddExercises(View view){
        Intent addExercises = new Intent(view.getContext(), AddExercises.class);
        startActivity(addExercises);
    }

    public void startWorkoutHistory(View view){
        Intent workoutHistory = new Intent(view.getContext(), HistorySelectionScreen.class);
        startActivity(workoutHistory);
    }
}
