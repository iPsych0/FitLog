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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.utils.DBHelper;

import java.util.List;

public class EditWorkoutScreen extends AppCompatActivity {

    private ListView exerciseList;
    private TextView topText;
    private DBHelper dbHelper;
    private ArrayAdapter<Set> setsAdapter;
    private EditText repsField, weightField;
    private Intent received;
    private Bundle bundle;
    private String selectedExercise, chosenWorkout, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout_screen);

        exerciseList = findViewById(R.id.exerciseLV);
        topText = findViewById(R.id.exerciseText);
        dbHelper = new DBHelper(this, null, null, 1);

        received = getIntent();
        bundle = received.getExtras();
        if(bundle != null) {
            selectedExercise = (String) bundle.get("exercise");
            chosenWorkout = (String) bundle.get("workout");
            date = (String) bundle.get("date");
            topText.setText("Edit sets for: '" + selectedExercise + "'");
        } else {
            Toast.makeText(this, "Could not retrieve the selected exercise from the previous screen. Please retry.", Toast.LENGTH_LONG).show();
            return;
        }

        // Get the sets from the database
        List<Set> sets = dbHelper.getAllSetsByExerciseAndDateAndExercise(chosenWorkout, date, selectedExercise);
        setsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sets);
        exerciseList.setAdapter(setsAdapter);

        exerciseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditWorkoutScreen.this);
                builder
                        .setMessage("What would you like to do?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Set selected = (Set)adapterView.getItemAtPosition(i);
                                dbHelper.deleteSet(selected.getId());
                                setsAdapter.clear();
                                setsAdapter.addAll(dbHelper.getAllSetsByExerciseAndDateAndExercise(chosenWorkout, date, selectedExercise));
                                setsAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                Set selected = (Set) adapterView.getItemAtPosition(i);
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(EditWorkoutScreen.this);
                                builder2
                                        .setView(inflateDialogue(selected))
                                        .setMessage("Please fill in the correct reps and weight:")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (repsField.getText().toString().isEmpty() || weightField.getText().toString().isEmpty()) {
                                                    Toast.makeText(EditWorkoutScreen.this, "Please fill in both reps and weight.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                dbHelper.editWorkout(selected.getId(), repsField.getText().toString(), weightField.getText().toString());
                                                setsAdapter.clear();
                                                setsAdapter.addAll(dbHelper.getAllSetsByExerciseAndDateAndExercise(chosenWorkout, date, selectedExercise));
                                                setsAdapter.notifyDataSetChanged();
                                                Toast.makeText(EditWorkoutScreen.this, "Updated set!", Toast.LENGTH_SHORT).show();
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
    private LinearLayout inflateDialogue(Set selected) {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        repsField = new EditText(this);
        repsField.setHint("Reps:");
        repsField.setText(String.valueOf(selected.getReps()));
        repsField.setInputType(InputType.TYPE_CLASS_NUMBER);
        repsField.setPadding(20, 20, 20, 20);
        repsField.setGravity(Gravity.CENTER);
        repsField.setTextSize(16);

        weightField = new EditText(this);
        weightField.setHint("Weight:");
        weightField.setText(String.valueOf(selected.getWeight()));
        weightField.setInputType(InputType.TYPE_CLASS_NUMBER);
        weightField.setPadding(20, 20, 20, 20);
        weightField.setGravity(Gravity.CENTER);
        weightField.setTextSize(16);

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(repsField, tv1Params);
        layout.addView(weightField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        return layout;
    }
}
