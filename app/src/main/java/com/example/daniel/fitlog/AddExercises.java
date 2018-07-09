package com.example.daniel.fitlog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AddExercises extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Spinner muscleGroups;
    Spinner exercises;
    EditText reps;
    EditText weight;
    Button addButton;
    Button dateButton;
    ImageView helpButton;
    String muscleGroupSelected;
    String exerciseSelected;
    DBHelper dbHelper;
    LocalDate selectedDate;
    RadioButton button30;
    RadioButton button60;
    RadioButton button90;
    RadioGroup buttonGroup;
    int checked;
    ImageView stopWatch;
    TextView countdownTimer;
    Calendar c;
    int timer;
    Timer t;
    Vibrator vib;


    ArrayAdapter<CharSequence> muscleGroupAdapter;
    ArrayAdapter<CharSequence> exerciseAdapter;

    public AddExercises(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercises);

        dbHelper = new DBHelper(this, null, null, 1);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        muscleGroups = (Spinner)findViewById(R.id.muscleGroups);
        exercises = (Spinner)findViewById(R.id.exercises);
        reps = (EditText)findViewById(R.id.reps);
        weight = (EditText)findViewById(R.id.weight);
        dateButton = (Button)findViewById(R.id.dateButton);
        addButton = (Button)findViewById(R.id.addButton);
        helpButton = (ImageView)findViewById(R.id.helpButton);
        button30 = (RadioButton)findViewById(R.id.button30);
        button60 = (RadioButton)findViewById(R.id.button60);
        button90 = (RadioButton)findViewById(R.id.button90);
        buttonGroup = (RadioGroup)findViewById(R.id.buttonGroup);
        stopWatch = (ImageView)findViewById(R.id.stopWatch);
        countdownTimer = (TextView)findViewById(R.id.countdownTimer);

        c = Calendar.getInstance();
        selectedDate = LocalDate.now();
        dateButton.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        muscleGroupAdapter = ArrayAdapter.createFromResource(this, R.array.muscleGroups,
                android.R.layout.simple_spinner_item);
        muscleGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroups.setAdapter(muscleGroupAdapter);

        muscleGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                muscleGroupSelected = parent.getItemAtPosition(position).toString();

                if(muscleGroupSelected.equals("Back")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercises.this, R.array.back,
                            android.R.layout.simple_spinner_dropdown_item);
                    exercises.setAdapter(exerciseAdapter);
                }
                if(muscleGroupSelected.equals("Chest")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercises.this, R.array.chest,
                            android.R.layout.simple_spinner_dropdown_item);
                    exercises.setAdapter(exerciseAdapter);
                }
                if(muscleGroupSelected.equals("Legs")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercises.this, R.array.legs,
                            android.R.layout.simple_spinner_dropdown_item);
                    exercises.setAdapter(exerciseAdapter);
                }
                if(muscleGroupSelected.equals("Shoulders")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercises.this, R.array.shoulders,
                            android.R.layout.simple_spinner_dropdown_item);
                    exercises.setAdapter(exerciseAdapter);
                }
                if(muscleGroupSelected.equals("Biceps")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercises.this, R.array.biceps,
                            android.R.layout.simple_spinner_dropdown_item);
                    exercises.setAdapter(exerciseAdapter);
                }
                if(muscleGroupSelected.equals("Triceps")) {
                    exerciseAdapter = ArrayAdapter.createFromResource(AddExercises.this, R.array.triceps,
                            android.R.layout.simple_spinner_dropdown_item);
                    exercises.setAdapter(exerciseAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the current date as the default date in the picker
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExercises.this, AddExercises.this, year, month, day);
                datePickerDialog.show();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddExercises.this, "Select an exercise and add reps and weight. " +
                        "Every time you press 'Add', you add ONE SET to that exercise.",
                        Toast.LENGTH_LONG).show();
            }
        });

        exercises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exerciseSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checked = buttonGroup.indexOfChild(findViewById(checkedId));
                switch(checked){
                    case 0:
                        countdownTimer.setText("30");
                        timer = Integer.parseInt(countdownTimer.getText().toString());
                        if(t != null){
                            t.cancel();
                        }
                        break;
                    case 1:
                        countdownTimer.setText("60");
                        timer = Integer.parseInt(countdownTimer.getText().toString());
                        if(t != null){
                            t.cancel();
                        }
                        break;
                    case 2:
                        countdownTimer.setText("90");
                        timer = Integer.parseInt(countdownTimer.getText().toString());
                        if(t != null){
                            t.cancel();
                        }
                        break;
                    default:
                        countdownTimer.setText("");
                        timer = 0;
                        if(t != null){
                            t.cancel();
                        }
                        break;
                }
            }
        });

        countdownTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (countdownTimer.getText().toString().isEmpty()) {
                        return;
                    }
                    if(t != null){
                        t.cancel();
                        t = null;
                        return;
                    }

                    t = new Timer();
                    t.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (timer <= 1) {
                                vib.vibrate(2500);
                                t.cancel();
                            }
                            timer--;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countdownTimer.setText(String.valueOf(timer));
                                }
                            });

                        }
                    }, 1000, 1000);



            }

        });
    }

    public void addExercisesToDB(View view){
        if(reps.getText().toString().isEmpty() || weight.getText().toString().isEmpty()){
            Toast.makeText(AddExercises.this, "Please enter the number of reps and weight", Toast.LENGTH_SHORT).show();
            return;
        }
        // Voeg hier de oefeningen toe aan de database
        AlertDialog.Builder builder = new AlertDialog.Builder(AddExercises.this);
        builder
                .setMessage("Do you want to add " + reps.getText().toString() + " reps of " +
                        weight.getText().toString() + "kg of " +
                        exercises.getSelectedItem().toString() + " to your workout?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.addWorkout(muscleGroups.getSelectedItem().toString().toLowerCase(),
                                exercises.getSelectedItem().toString(), reps.getText().toString(),
                                weight.getText().toString(), getSelectedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                        Toast.makeText(AddExercises.this, "You did " + reps.getText().toString() + " reps of " +
                                weight.getText().toString() + "kg of " + exercises.getSelectedItem().toString() + "s", Toast.LENGTH_LONG).show();
                    }
                })
                // Nothing is done when "No" is pressed
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void finishWorkout(View view){
        // Voeg hier de oefeningen toe aan de database
        AlertDialog.Builder builder = new AlertDialog.Builder(AddExercises.this);
        builder
                .setMessage("Are you finished with your workout?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent homeScreen = new Intent(AddExercises.this, HomeScreen.class);
                        startActivity(homeScreen);
                        finish();
                    }
                })
                // Nothing is done when "No" is pressed
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        setSelectedDate(date);
        dateButton.setText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    public LocalDate getSelectedDate(){
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selection){
        this.selectedDate = selection;
    }
}
