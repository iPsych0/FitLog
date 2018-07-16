package com.example.daniel.fitlog.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.daniel.fitlog.utils.DBHelper;
import com.example.daniel.fitlog.R;

import java.util.ArrayList;

public class WorkoutHistory extends AppCompatActivity {

    Spinner muscleGroups;
    ListView resultsLV;
    ArrayAdapter<CharSequence> muscleGroupAdapter;
    ArrayAdapter<String> resultsLVadapter;
    ArrayList<String> allWorkouts;
    DBHelper dbHelper;
    ImageView helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        dbHelper = new DBHelper(this, null, null, 1);
        resultsLV = (ListView)findViewById(R.id.resultsLV);
        helpButton = (ImageView)findViewById(R.id.helpButton);

        muscleGroups = (Spinner)findViewById(R.id.muscleGroups);
        muscleGroupAdapter = ArrayAdapter.createFromResource(this, R.array.muscleGroups,
                android.R.layout.simple_spinner_item);
        muscleGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroups.setAdapter(muscleGroupAdapter);


        allWorkouts = dbHelper.getExerciseList(muscleGroups.getSelectedItem().toString().toLowerCase());

        resultsLVadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allWorkouts);
        resultsLV.setAdapter(resultsLVadapter);

        resultsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String chosenExercise = String.valueOf(parent.getItemAtPosition(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutHistory.this);
                builder
                        .setMessage("Your current highest weight for " + chosenExercise + " is " + dbHelper.getHighestWeightList(chosenExercise).get(0) + "kg")
                        .setPositiveButton("Show statistics",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent graphScreen = new Intent(WorkoutHistory.this, GraphScreen.class);

                                Bundle bundle = new Bundle();
                                bundle.putString("chosenExercise", chosenExercise);
                                graphScreen.putExtras(bundle);

                                startActivity(graphScreen);
                            }
                        })
                        // Nothing is done when "No" is pressed
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();

            }
        });

        muscleGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                allWorkouts = dbHelper.getExerciseList(muscleGroups.getSelectedItem().toString().toLowerCase());
                resultsLVadapter = new ArrayAdapter<>(WorkoutHistory.this, android.R.layout.simple_list_item_1, allWorkouts);
                resultsLV.setAdapter(resultsLVadapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void helpMessage(View view){
        Toast.makeText(view.getContext(), "Select a muscle group and click on an exercise to see your highest " +
                "weight. Click on 'Show statistics' to see a more detailed graph of your progress.",
                Toast.LENGTH_LONG).show();
    }

    public void goBack(View view){
        Intent intent = new Intent(view.getContext(), HistorySelectionScreen.class);
        startActivity(intent);
    }
}
