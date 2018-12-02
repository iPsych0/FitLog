package com.example.daniel.fitlog.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.utils.DBHelper;
import com.example.daniel.fitlog.utils.HistoryAdapter;

import java.util.List;

public class WorkoutHistoryScreen extends AppCompatActivity {

    private TextView dateText;
    private Intent received;
    private Bundle bundle;
    private String chosenWorkout;
    private String selectedDate;
    private ListView setList;
    private HistoryAdapter setsAdapter;
    private DBHelper dbHelper = new DBHelper(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        // Set the UI elements
        dateText = findViewById(R.id.dateText);
        setList = findViewById(R.id.setsList);

        // Get parameters
        received = getIntent();
        bundle = received.getExtras();

        // Get values
        if (bundle != null) {
            chosenWorkout = bundle.getString("chosenWorkout");
            selectedDate = bundle.getString("date");
        } else {
            Toast.makeText(this, "Could not retrieve the selected workout from the previous screen. Please retry.", Toast.LENGTH_LONG).show();
            return;
        }

        // Set the text at the top to the chosen values
        String topText = "All " + chosenWorkout + " exercises on: " + selectedDate;
        dateText.setText(topText);

        // Get the sets from the database
        List<Set> sets = dbHelper.getAllSetsByExerciseAndDate(chosenWorkout, selectedDate);

        // Fill the ListView
        setsAdapter = new HistoryAdapter(this, sets);
        setList.setAdapter(setsAdapter);

        setList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selected = (String)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(WorkoutHistoryScreen.this, EditWorkoutScreen.class);
                Bundle bundle = new Bundle();
                bundle.putString("exercise", selected);
                bundle.putString("workout", chosenWorkout);
                bundle.putString("date", selectedDate);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setList.setOnItemLongClickListener((adapterView, view, i, l) -> {

            String selected = (String)adapterView.getItemAtPosition(i);
            AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutHistoryScreen.this);
            builder
                    .setMessage("What would you like to do?")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        dbHelper.deleteWorkout(selected, selectedDate);
                        List<Set> temp = dbHelper.getAllSetsByExerciseAndDateAndExercise(chosenWorkout, selectedDate, selected);
                        if(temp.size() > 0) {
                            setsAdapter.clear();
                            setsAdapter.addAll(temp);
                            setsAdapter.notifyDataSetChanged();
                        }else{
                            Intent intent = new Intent(WorkoutHistoryScreen.this, WorkoutOverviewScreen.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("workout",chosenWorkout);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    })
                    // Nothing is done when "No" is pressed
                    .setNeutralButton("Cancel", (dialog, id) -> dialog.cancel())
                    .show();

            return true;
        });

    }

    public void goBack(View view) {
        Intent intent = new Intent(view.getContext(), WorkoutOverviewScreen.class);
        startActivity(intent);
    }
}
