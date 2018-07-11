package com.example.daniel.fitlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectMuscleGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_muscle_group);
    }

    public void startWorkoutOverview(View view){
        Intent intent = new Intent(view.getContext(), WorkoutOverview.class);
        Bundle bundle = new Bundle();
        bundle.putString("chosenWorkout", ((Button)view).getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void goBack(View view){
        Intent intent = new Intent(view.getContext(), HistorySelectionScreen.class);
        startActivity(intent);
    }
}
