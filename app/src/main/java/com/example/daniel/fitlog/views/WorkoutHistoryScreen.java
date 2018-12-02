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

        // Fill the ListView
        setsAdapter = new HistoryAdapter(this, sets);
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
                                        .setView(inflateDialogue())
                                        .setMessage("Please fill in the correct reps and weight:")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (repsField.getText().toString().isEmpty() || weightField.getText().toString().isEmpty()) {
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
     *
     * @return inflated layout to populate the dialogue
     */
    private LinearLayout inflateDialogue() {
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

    public void goBack(View view) {
        Intent intent = new Intent(view.getContext(), WorkoutOverviewScreen.class);
        startActivity(intent);
    }
}
