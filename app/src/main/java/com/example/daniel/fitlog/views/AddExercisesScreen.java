package com.example.daniel.fitlog.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.utils.DBHelper;
import com.example.daniel.fitlog.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AddExercisesScreen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner muscleGroups;
    private Spinner exercises;
    private EditText reps;
    private EditText weight;
    private Button addButton;
    private Button dateButton;
    private ImageView helpButton;
    private String muscleGroupSelected;
    private String exerciseSelected;
    private DBHelper dbHelper;
    private LocalDate selectedDate;
    private RadioButton button30;
    private RadioButton button60;
    private RadioButton button90;
    private RadioGroup buttonGroup;
    private int checked;
    private ImageView stopWatch;
    private TextView countdownTimer;
    private Calendar c;
    private int timer;
    private Timer t;
    private Vibrator vib;
    private ArrayAdapter<CharSequence> muscleGroupAdapter;
    private ArrayAdapter<CharSequence> exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercises);

        initializeScreen();

        listenForMuscleGroupChanges();

        listenForDateChanges();

        listenForExerciseChanges();

        checkStopWatch();

        // Show help text when pressing the help button
        helpButton.setOnClickListener(v -> Toast.makeText(AddExercisesScreen.this,
                "Select an exercise and add reps and weight. " +
                        "Every time you press 'Add', you add ONE SET to that exercise.",
                Toast.LENGTH_LONG).show());
    }

    /**
     * Adds a set of a given exercise to the database
     *
     * @param view The context
     */
    public void addExercisesToDB(View view) {
        if (reps.getText().toString().isEmpty() || weight.getText().toString().isEmpty()) {
            Toast.makeText(AddExercisesScreen.this, "Please enter both reps and weight", Toast.LENGTH_SHORT).show();
            return;
        }

        // Open a confirmation window before adding to the database
        AlertDialog.Builder builder = new AlertDialog.Builder(AddExercisesScreen.this);
        builder
                .setMessage("Do you want to add " + reps.getText().toString() + " reps of " +
                        weight.getText().toString() + "kg of " +
                        exercises.getSelectedItem().toString() + " to your workout?")

                // If affirmative, add to the database
                .setPositiveButton("Yes", (dialog, id) -> {
                    dbHelper.addWorkout(muscleGroups.getSelectedItem().toString().toLowerCase(),
                            exercises.getSelectedItem().toString(), reps.getText().toString(),
                            weight.getText().toString(), getSelectedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                    Toast.makeText(AddExercisesScreen.this, "You did " + reps.getText().toString() + " reps of " +
                            weight.getText().toString() + "kg of " + exercises.getSelectedItem().toString() + "s", Toast.LENGTH_LONG).show();
                })

                // Nothing is done when "No" is pressed
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }

    /**
     * Opens confirmation dialogue to return home
     *
     * @param view The context
     */
    public void finishWorkout(View view) {
        // Open dialogue to confirm return to home
        AlertDialog.Builder builder = new AlertDialog.Builder(AddExercisesScreen.this);
        builder
                .setMessage("Are you finished with your workout?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent homeScreen = new Intent(AddExercisesScreen.this, HomeScreen.class);
                    startActivity(homeScreen);
                    finish();
                })
                // Nothing is done when "No" is pressed
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }

    /**
     * Changes the selected date
     *
     * @param view       The DatePicker instance
     * @param year       Year of the workout
     * @param month      Month of the workout
     * @param dayOfMonth Day of the month of the workout
     */
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        setSelectedDate(date);
        dateButton.setText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    /*
     * Listeners and initialization
     */

    private void initializeScreen() {
        // Get the DB
        dbHelper = new DBHelper(this, null, null, 1);

        // Get all UI elements
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        muscleGroups = (Spinner) findViewById(R.id.muscleGroups);
        exercises = (Spinner) findViewById(R.id.exercises);
        reps = (EditText) findViewById(R.id.reps);
        weight = (EditText) findViewById(R.id.weight);
        dateButton = (Button) findViewById(R.id.dateButton);
        addButton = (Button) findViewById(R.id.addButton);
        helpButton = (ImageView) findViewById(R.id.helpButton);
        button30 = (RadioButton) findViewById(R.id.button30);
        button60 = (RadioButton) findViewById(R.id.button60);
        button90 = (RadioButton) findViewById(R.id.button90);
        buttonGroup = (RadioGroup) findViewById(R.id.buttonGroup);
        stopWatch = (ImageView) findViewById(R.id.stopWatch);
        countdownTimer = (TextView) findViewById(R.id.countdownTimer);

        // Set the calendar to today's date by default
        c = Calendar.getInstance();
        selectedDate = LocalDate.now();
        dateButton.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        // Populate the muscle group drop down
        muscleGroupAdapter = ArrayAdapter.createFromResource(this, R.array.muscleGroups,
                android.R.layout.simple_spinner_item);
        muscleGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroups.setAdapter(muscleGroupAdapter);
    }

    private void listenForMuscleGroupChanges() {
        muscleGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                muscleGroupSelected = parent.getItemAtPosition(position).toString();

                if (muscleGroupSelected.equals("Back")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.back,
                            android.R.layout.simple_spinner_dropdown_item);
                } else if (muscleGroupSelected.equals("Chest")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.chest,
                            android.R.layout.simple_spinner_dropdown_item);
                } else if (muscleGroupSelected.equals("Legs")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.legs,
                            android.R.layout.simple_spinner_dropdown_item);
                } else if (muscleGroupSelected.equals("Shoulders")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.shoulders,
                            android.R.layout.simple_spinner_dropdown_item);
                } else if (muscleGroupSelected.equals("Biceps")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.biceps,
                            android.R.layout.simple_spinner_dropdown_item);
                } else if (muscleGroupSelected.equals("Triceps")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.triceps,
                            android.R.layout.simple_spinner_dropdown_item);
                } else if (muscleGroupSelected.equals("Abs")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercisesScreen.this, R.array.abs,
                            android.R.layout.simple_spinner_dropdown_item);
                }

                exercises.setAdapter(exerciseAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void listenForDateChanges() {
        // Listen for when to open the calendar
        dateButton.setOnClickListener(v -> {
            // Use the current date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Open the calendar pop-up when pressed
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddExercisesScreen.this, AddExercisesScreen.this, year, month, day);
            datePickerDialog.show();
        });
    }

    private void listenForExerciseChanges() {
        // Listen for changes to the exercise dropdown
        exercises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exerciseSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void checkStopWatch() {
        // Change the timer cooldown
        buttonGroup.setOnCheckedChangeListener((group, checkedId) -> {
            checked = buttonGroup.indexOfChild(findViewById(checkedId));
            switch (checked) {
                case 0:
                    countdownTimer.setText("30");
                    timer = Integer.parseInt(countdownTimer.getText().toString());
                    if (t != null) {
                        t.cancel();
                    }
                    break;
                case 1:
                    countdownTimer.setText("60");
                    timer = Integer.parseInt(countdownTimer.getText().toString());
                    if (t != null) {
                        t.cancel();
                    }
                    break;
                case 2:
                    countdownTimer.setText("90");
                    timer = Integer.parseInt(countdownTimer.getText().toString());
                    if (t != null) {
                        t.cancel();
                    }
                    break;
                default:
                    countdownTimer.setText("");
                    timer = 0;
                    if (t != null) {
                        t.cancel();
                    }
                    break;
            }
        });

        countdownTimer.setOnClickListener((View v) -> {
            if (countdownTimer.getText().toString().isEmpty()) {
                return;
            }
            if (t != null) {
                t.cancel();
                t = null;
                return;
            }

            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (timer <= 1) {
                        vib.vibrate(2000);
                        t.cancel();
                    }
                    timer--;
                    runOnUiThread(() -> countdownTimer.setText(String.valueOf(timer)));

                }
            }, 1000, 1000);
        });
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selection) {
        this.selectedDate = selection;
    }
}
