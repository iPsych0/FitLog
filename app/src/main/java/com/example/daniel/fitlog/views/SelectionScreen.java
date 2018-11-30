package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.daniel.fitlog.R;

public class SelectionScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
    }

    public void startMaxWeights(View view){
        Intent maxWeights = new Intent(view.getContext(), HighestWeightHistoryScreen.class);
        startActivity(maxWeights);
    }
    
    public void startSelectMuscleGroup(View view){
        Intent intent = new Intent(view.getContext(), WorkoutOverviewScreen.class);
        startActivity(intent);
    }

    public void goBack(View view){
        Intent intent = new Intent(view.getContext(), HomeScreen.class);
        startActivity(intent);
    }
}
