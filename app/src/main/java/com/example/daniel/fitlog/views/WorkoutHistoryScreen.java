package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryScreen extends AppCompatActivity {

    TextView dateText;
    Intent received;
    Bundle bundle;
    String chosenWorkout;
    String selectedDate;
    ListView setList;
    ArrayAdapter<String> setsAdapter;
    DBHelper dbHelper = new DBHelper(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        dateText = findViewById(R.id.dateText);
        setList = findViewById(R.id.setsList);

        // Get parameters
        received = getIntent();
        bundle = received.getExtras();

        // Get values
        if (bundle != null) {
            chosenWorkout = bundle.getString("chosenWorkout");
            selectedDate = bundle.getString("date");
        }

        // Set the text at the top to the chosen values
        String topText = "All " + chosenWorkout + " exercises on: " + selectedDate;
        dateText.setText(topText);

        // Get the sets from the database
        List<Set> sets = dbHelper.getAllSetsByExerciseAndDate(chosenWorkout, selectedDate);

        // Order the sets by exercise
        List<String> exercises = formatExercises(sets);

        setsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exercises);
        setList.setAdapter(setsAdapter);

    }

    /**
     * Groups the exercises together in a nice way
     * @param sets the total amount of sets
     * @return a sorted/grouped list of sets
     */
    private List<String> formatExercises(List<Set> sets) {
        StringBuilder sb = new StringBuilder();
        String lastChecked = sets.get(0).getExercise();
        List<String> exercises = new ArrayList<>();

        for(Set s : sets){
            if(s.getExercise().equalsIgnoreCase(lastChecked)){
                sb.append(s).append("\n");
            }else{
                exercises.add(sb.toString());
                lastChecked = s.getExercise();
                sb.setLength(0);
                sb.append(s).append("\n");
            }
        }
        exercises.add(sb.toString());

        return exercises;
    }
}
