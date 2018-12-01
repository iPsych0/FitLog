package com.example.daniel.fitlog.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkoutHistoryScreen extends AppCompatActivity {

    private TextView dateText;
    private Intent received;
    private Bundle bundle;
    private String chosenWorkout;
    private String selectedDate;
    private ListView setList;
    private ArrayAdapter<Set> setsAdapter;
    private DBHelper dbHelper = new DBHelper(this, null, null, 1);
    private EditText repsField, weightField;

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

        // Order the sets by exercise
//        List<String> exercises = formatExercises(sets);

        // Fill the ListView
        setsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sets);
        setList.setAdapter(setsAdapter);

        setList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutHistoryScreen.this);
                builder
                        .setMessage("What would you like to do?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Set selected = (Set) adapterView.getItemAtPosition(i);
                                dbHelper.deleteWorkout(selected.getId());
                                setsAdapter.clear();
                                setsAdapter.addAll(dbHelper.getAllSetsByExerciseAndDate(chosenWorkout, selectedDate));
                                setsAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(WorkoutHistoryScreen.this);
                                builder2
                                        .setView(inflateWindow())
                                        .setMessage("Please fill in the correct reps and weight:")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                if(repsField.getText().toString().isEmpty() || weightField.getText().toString().isEmpty()){
                                                    Toast.makeText(WorkoutHistoryScreen.this, "Please fill in both reps and weight.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                Set selected = (Set) adapterView.getItemAtPosition(i);
                                                dbHelper.editWorkout(selected.getId(), repsField.getText().toString(), weightField.getText().toString());
                                                setsAdapter.clear();
                                                setsAdapter.addAll(dbHelper.getAllSetsByExerciseAndDate(chosenWorkout, selectedDate));
                                                setsAdapter.notifyDataSetChanged();
                                                Toast.makeText(WorkoutHistoryScreen.this, "Updated set!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                        .show();
                            }
                        })
                        // Nothing is done when "No" is pressed
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            }
        });

    }

    /**
     * Inflates the two text fields to be populated in the dialogue
     * @return inflated layout to populate the dialogue
     */
    private LinearLayout inflateWindow() {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        repsField = new EditText(this);
        repsField.setHint("Reps:");
        repsField.setInputType(InputType.TYPE_CLASS_NUMBER);
        repsField.setPadding(20, 20, 20, 20);
        repsField.setGravity(Gravity.CENTER);
        repsField.setTextSize(20);

        weightField = new EditText(this);
        weightField.setHint("Weight:");
        weightField.setInputType(InputType.TYPE_CLASS_NUMBER);
        weightField.setPadding(20, 20, 20, 20);
        weightField.setGravity(Gravity.CENTER);
        weightField.setTextSize(20);

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(repsField, tv1Params);
        layout.addView(weightField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        return layout;
    }

    /**
     * Groups the exercises together in a nice way
     *
     * @param sets the total amount of sets
     * @return a sorted/grouped list of sets
     */
    private List<String> formatExercises(List<Set> sets) {
        StringBuilder sb = new StringBuilder();
        String lastChecked = sets.get(0).getExercise();
        List<String> exercises = new ArrayList<>();

        for (Set s : sets) {
            if (s.getExercise().equalsIgnoreCase(lastChecked)) {
                sb.append(s).append("\n");
            } else {
                exercises.add(sb.toString());
                lastChecked = s.getExercise();
                sb.setLength(0);
                sb.append(s).append("\n");
            }
        }
        exercises.add(sb.toString());

        return exercises;
    }

    public void goBack(View view){
        Intent intent = new Intent(view.getContext(), WorkoutOverviewScreen.class);
        startActivity(intent);
    }
}
