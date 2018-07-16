package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Workout;
import com.example.daniel.fitlog.utils.DBHelper;

import java.util.ArrayList;

public class WorkoutOverview extends AppCompatActivity {

    String chosenWorkout = "back";
    Spinner dropDownWorkouts;
    ArrayAdapter<String> resultsLVadapter;
    ArrayAdapter<CharSequence> workoutAdapter;
    ListView scrollView;
    ArrayList<String> muscleGroups = new ArrayList<>();
    TextView topText;
    DBHelper dbHelper = new DBHelper(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_overview);

        dropDownWorkouts = findViewById(R.id.spinner);
        workoutAdapter = ArrayAdapter.createFromResource(this, R.array.muscleGroups,
                android.R.layout.simple_spinner_item);
        workoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownWorkouts.setAdapter(workoutAdapter);

        topText = findViewById(R.id.topText);
        scrollView = findViewById(R.id.workoutView);

        topText.setText("All " +chosenWorkout.toLowerCase() + " workouts");

        muscleGroups = dbHelper.getAllMuscleGroupWorkouts(chosenWorkout);
        resultsLVadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, muscleGroups);
        scrollView.setAdapter(resultsLVadapter);

        dropDownWorkouts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] muscleArray = getResources().getStringArray(R.array.muscleGroups);
                chosenWorkout = muscleArray[i];
                muscleGroups = dbHelper.getAllMuscleGroupWorkouts(chosenWorkout);
                resultsLVadapter = new ArrayAdapter<>(WorkoutOverview.this, android.R.layout.simple_list_item_1, muscleGroups);
                scrollView.setAdapter(resultsLVadapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}
