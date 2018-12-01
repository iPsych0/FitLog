package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class WorkoutOverviewScreen extends AppCompatActivity {

    String chosenWorkout = "back";
    String topTextString;
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
        topTextString = "All " +chosenWorkout.toLowerCase() + " workouts";
        scrollView = findViewById(R.id.workoutView);

        topText.setText(topTextString);

        muscleGroups = dbHelper.getAllMuscleGroupWorkouts(chosenWorkout);
        resultsLVadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, muscleGroups);
        scrollView.setAdapter(resultsLVadapter);

        dropDownWorkouts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenWorkout = dropDownWorkouts.getSelectedItem().toString();
                topTextString = "All " +chosenWorkout.toLowerCase() + " workouts";
                topText.setText(topTextString);
                muscleGroups = dbHelper.getAllMuscleGroupWorkouts(chosenWorkout);
                resultsLVadapter = new ArrayAdapter<>(WorkoutOverviewScreen.this, android.R.layout.simple_list_item_1, muscleGroups);
                scrollView.setAdapter(resultsLVadapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        scrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), WorkoutHistoryScreen.class);
                Bundle bundle = new Bundle();
                bundle.putString("chosenWorkout",chosenWorkout);
                bundle.putString("date",adapterView.getItemAtPosition(i).toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }

    public void goBack(View view) {
        Intent intent = new Intent(view.getContext(), SelectionScreen.class);
        startActivity(intent);
    }
}
