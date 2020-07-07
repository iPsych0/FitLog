package com.example.daniel.fitlog.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.utils.DBHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExercisesScreen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner muscleGroups;
    private Spinner exercises;
    private EditText reps;
    private EditText weight;
    private Button addButton;
    private Button dateButton;
    private String muscleGroupSelected;
    private String exerciseSelected;
    private Button missingExerciseButton;
    private Button currentWorkoutButton;
    private DBHelper dbHelper;
    private LocalDate selectedDate;
    private Calendar c;
    private ArrayAdapter<CharSequence> muscleGroupAdapter;
    private ArrayAdapter<CharSequence> exerciseAdapter;
    private List<CharSequence> chestList, backList, bicepsList, tricepsList, shouldersList, legsList, absList;
    private EditText exerciseInput;
    private TextView setsTV;
    private static final String DEFAULT_SETS_MESSAGE = "Current sets:\n";
    private StringBuilder currentSets = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercises);

        loadConfigurations();

        initializeScreen();

        listenForMuscleGroupChanges();

        listenForDateChanges();

        listenForExerciseChanges();

        updateCurrentSets();

        setsTV.setText(DEFAULT_SETS_MESSAGE + currentSets.toString());

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

                    updateCurrentSets();

                    Toast.makeText(AddExercisesScreen.this, "You did " + reps.getText().toString() + " reps of " +
                            weight.getText().toString() + "kg of " + exercises.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                })

                // Nothing is done when "No" is pressed
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void updateCurrentSets() {
        List<Set> sets = dbHelper.getAllSetsByWorkoutAndDateAndExercise(muscleGroupSelected, selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), exerciseSelected);
        currentSets.delete(0, currentSets.length());
        for (Set s : sets) {
            currentSets.append(s.getReps()).append("x ").append(formatDouble(s.getWeight())).append("kg\n");
        }
        setsTV.setText(DEFAULT_SETS_MESSAGE + currentSets.toString());
    }

    private String formatDouble(double weight) {
        if (weight == (long) weight) {
            return String.format(Locale.ENGLISH, "%d", (long) weight);
        }
        return String.format(Locale.ENGLISH, "%s", weight);
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
        updateCurrentSets();
    }

    /*
     * Listeners and initialization
     */

    private void initializeScreen() {
        // Get the DB
        dbHelper = new DBHelper(this, null, null, 1);

        // Get all UI elements
        muscleGroups = (Spinner) findViewById(R.id.muscleGroups);
        exercises = (Spinner) findViewById(R.id.exercises);
        missingExerciseButton = findViewById(R.id.missingExerciseButton);
        reps = (EditText) findViewById(R.id.reps);
        weight = (EditText) findViewById(R.id.weight);
        dateButton = (Button) findViewById(R.id.dateButton);
        addButton = (Button) findViewById(R.id.addButton);
        setsTV = findViewById(R.id.setsTV);

        // Set the calendar to today's date by default
        c = Calendar.getInstance();
        selectedDate = LocalDate.now();
        dateButton.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        // Populate the muscle group drop down
        muscleGroupAdapter = ArrayAdapter.createFromResource(this, R.array.muscleGroups,
                android.R.layout.simple_spinner_item);
        muscleGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroups.setAdapter(muscleGroupAdapter);
        muscleGroupSelected = muscleGroups.getSelectedItem().toString();

        exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, backList);
        exercises.setAdapter(exerciseAdapter);
        exerciseSelected = exercises.getSelectedItem().toString();

    }

    private void listenForMuscleGroupChanges() {
        muscleGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                muscleGroupSelected = parent.getItemAtPosition(position).toString();

                if (muscleGroupSelected.equals("Back")) {
                    backList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, backList);
                } else if (muscleGroupSelected.equals("Chest")) {
                    chestList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, chestList);
                } else if (muscleGroupSelected.equals("Legs")) {
                    legsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, legsList);
                } else if (muscleGroupSelected.equals("Shoulders")) {
                    shouldersList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, shouldersList);
                } else if (muscleGroupSelected.equals("Biceps")) {
                    bicepsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, bicepsList);
                } else if (muscleGroupSelected.equals("Triceps")) {
                    tricepsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, tricepsList);
                } else if (muscleGroupSelected.equals("Abs")) {
                    absList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                    exerciseAdapter = new ArrayAdapter<>(AddExercisesScreen.this, android.R.layout.simple_spinner_dropdown_item, absList);
                }

                exercises.setAdapter(exerciseAdapter);
                updateCurrentSets();
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

        final Handler actionHandler = new Handler();
        final Runnable runnable = () -> {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(AddExercisesScreen.this);
            builder2
                    .setMessage("Are you sure you want to remove this exercise?")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        switch (muscleGroupSelected.toLowerCase()) {
                            case "back":
                                backList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(backList);
                                break;
                            case "chest":
                                chestList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(chestList);
                                break;
                            case "biceps":
                                bicepsList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(bicepsList);
                                break;
                            case "triceps":
                                tricepsList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(tricepsList);
                                break;
                            case "shoulders":
                                shouldersList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(shouldersList);
                                break;
                            case "legs":
                                legsList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(legsList);
                                break;
                            case "abs":
                                absList.removeIf((o1) -> o1.equals(exerciseSelected));
                                updateList(absList);
                                break;
                        }
                        exerciseAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Edit", (dialog, id) -> {
                        editExerciseName(this.getCurrentFocus());
                        exerciseAdapter.notifyDataSetChanged();
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        };

        exercises.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    actionHandler.postDelayed(runnable, 1000);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    actionHandler.removeCallbacks(runnable);
                }
                return false;

            }
        });

        // Listen for changes to the exercise dropdown
        exercises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exerciseSelected = parent.getItemAtPosition(position).toString();
                updateCurrentSets();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selection) {
        this.selectedDate = selection;
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

        exerciseInput = new EditText(this);
        exerciseInput.setHint("Exercise name");
        exerciseInput.setInputType(InputType.TYPE_CLASS_TEXT);
        exerciseInput.setPadding(20, 20, 20, 20);
        exerciseInput.setGravity(Gravity.CENTER);
        exerciseInput.setTextSize(16);

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(exerciseInput, tv1Params);

        return layout;
    }

    private void renameListEntry(List<CharSequence> list, String oldName, String newExercise) {
        list.set(list.indexOf(oldName), newExercise);
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : list) {
            sb.append(s).append("\n");
        }

        String filename = muscleGroupSelected.toLowerCase() + ".txt";
        String fileContents = sb.toString();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            updateCurrentSets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateList(List<CharSequence> list, String newExercise) {
        list.add(newExercise);
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : list) {
            sb.append(s).append("\n");
        }

        String filename = muscleGroupSelected.toLowerCase() + ".txt";
        String fileContents = sb.toString();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            updateCurrentSets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateList(List<CharSequence> list) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : list) {
            sb.append(s).append("\n");
        }

        String filename = muscleGroupSelected.toLowerCase() + ".txt";
        String fileContents = sb.toString();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            updateCurrentSets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMissingExercise(View view) {

        AlertDialog.Builder builder2 = new AlertDialog.Builder(AddExercisesScreen.this);
        builder2
                .setView(inflateDialogue())
                .setMessage("Fill in the name of the missing exercise:")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (exerciseInput.getText().toString().isEmpty()) {
                            Toast.makeText(AddExercisesScreen.this, "Please fill in the missing exercise name!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String newExercise = exerciseInput.getText().toString();
                        exerciseAdapter.notifyDataSetChanged();
                        Toast.makeText(AddExercisesScreen.this, "Added " + exerciseInput.getText().toString(), Toast.LENGTH_SHORT).show();

                        switch (muscleGroupSelected.toLowerCase()) {
                            case "back":
                                updateList(backList, newExercise);
                                backList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "chest":
                                updateList(chestList, newExercise);
                                chestList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "biceps":
                                updateList(bicepsList, newExercise);
                                bicepsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "triceps":
                                updateList(tricepsList, newExercise);
                                tricepsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "shoulders":
                                updateList(shouldersList, newExercise);
                                shouldersList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "legs":
                                updateList(legsList, newExercise);
                                legsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "abs":
                                updateList(absList, newExercise);
                                absList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                        }
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

    public void editExerciseName(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(AddExercisesScreen.this);
        builder2
                .setView(inflateDialogue())
                .setMessage("Fill in the correct name of the exercise:")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (exerciseInput.getText().toString().isEmpty()) {
                            Toast.makeText(AddExercisesScreen.this, "Please fill in the new exercise name!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String newExercise = exerciseInput.getText().toString();
                        exerciseAdapter.notifyDataSetChanged();
                        Toast.makeText(AddExercisesScreen.this, "Renamed '" + exerciseSelected + "' to: " + exerciseInput.getText().toString(), Toast.LENGTH_SHORT).show();
                        updateCurrentSets();

                        dbHelper.editExercise(exerciseSelected, newExercise);

                        switch (muscleGroupSelected.toLowerCase()) {
                            case "back":
                                renameListEntry(backList, exerciseSelected, newExercise);
                                backList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "chest":
                                renameListEntry(chestList, exerciseSelected, newExercise);
                                chestList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "biceps":
                                renameListEntry(bicepsList, exerciseSelected, newExercise);
                                bicepsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "triceps":
                                renameListEntry(tricepsList, exerciseSelected, newExercise);
                                tricepsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "shoulders":
                                renameListEntry(shouldersList, exerciseSelected, newExercise);
                                shouldersList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "legs":
                                renameListEntry(legsList, exerciseSelected, newExercise);
                                legsList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                            case "abs":
                                renameListEntry(absList, exerciseSelected, newExercise);
                                absList.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
                                break;
                        }
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

    private boolean fileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private void loadConfigurations() {
        chestList = new ArrayList<>();
        backList = new ArrayList<>();
        bicepsList = new ArrayList<>();
        tricepsList = new ArrayList<>();
        shouldersList = new ArrayList<>();
        absList = new ArrayList<>();
        legsList = new ArrayList<>();

        // Chest
        if (!fileExists("chest.txt")) {
            String filename = "chest.txt";
            String fileContents = "Flat Bench Press\n" +
                    "Incline Bench Press\n" +
                    "Decline Bench Press\n" +
                    "Flat Dumbell Press\n" +
                    "Incline Dumbell Press\n" +
                    "Decline Dumbell Press\n" +
                    "Flat Cable Fly\n" +
                    "Incline Cable Fly\n" +
                    "Decline Cable Fly\n" +
                    "Pec Machine\n" +
                    "Flat Bench Machine\n" +
                    "Incline Machine\n" +
                    "Push-up";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("chest.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                chestList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Back
        if (!fileExists("back.txt")) {
            String filename = "back.txt";
            String fileContents = "Close Grip Lat Pulldown\n" +
                    "Wide Grip Lat Pulldown\n" +
                    "Seated Row\n" +
                    "Barbell Row\n" +
                    "Dumbell Row\n" +
                    "T-Bar Row\n" +
                    "Deadlift\n" +
                    "Pull-up";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("back.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                backList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Legs
        if (!fileExists("legs.txt")) {
            String filename = "legs.txt";
            String fileContents = "Back Squat\n" +
                    "Front Squat\n" +
                    "Leg Press\n" +
                    "Leg Extension\n" +
                    "Hamstring Curl\n" +
                    "Calf Machine";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("legs.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                legsList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Biceps
        if (!fileExists("biceps.txt")) {
            String filename = "biceps.txt";
            String fileContents = "Dumbell Hammer Curl\n" +
                    "Dumbell Preacher Curl\n" +
                    "EZ-Bar Preacher Curl\n" +
                    "Rope Cur\n" +
                    "Barbell Curl\n" +
                    "Machine Preacher Curl";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("biceps.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bicepsList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Triceps
        if (!fileExists("triceps.txt")) {
            String filename = "triceps.txt";
            String fileContents = "Close Grip Pushdown\n" +
                    "Rope Pushdown\n" +
                    "Overhead Rope Raise\n" +
                    "Overhead Dumbell Raise\n" +
                    "Dips Machine\n" +
                    "Dips Bodyweight\n" +
                    "Dumbell Skull Crusher\n" +
                    "EZ-Bar Skull Crusher";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("triceps.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tricepsList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Shoulders
        if (!fileExists("shoulders.txt")) {
            String filename = "shoulders.txt";
            String fileContents = "Overhead Dumbell Press\n" +
                    "Overhead Barbell Press\n" +
                    "Overhead Machine Press\n" +
                    "Dumbell Lateral Raises\n" +
                    "Machine Lateral Raises\n" +
                    "Rear Delt Cable Pull\n" +
                    "Rear Delt Machine\n" +
                    "Hex Bar Shrugs\n" +
                    "Dumbell Shrugs";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("shoulders.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                shouldersList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Abs
        if (!fileExists("abs.txt")) {
            String filename = "abs.txt";
            String fileContents = "Sit ups\n" +
                    "Crunches";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = openFileInput("abs.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                absList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void goBack(View view) {
        Intent intent = new Intent(AddExercisesScreen.this, HomeScreen.class);
        startActivity(intent);
        super.onBackPressed();

    }

    public void viewCurrentWorkout(View view) {
        Intent intent = new Intent(view.getContext(), WorkoutHistoryScreen.class);
        Bundle bundle = new Bundle();
        bundle.putString("chosenWorkout", muscleGroupSelected);
        bundle.putString("date", selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        bundle.putString("lastScreen", "addExercises");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
