package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;

public class OneRepMaxScreen extends AppCompatActivity {

    private EditText repsText, weightsText;
    private Button calculateButton;
    private TextView resultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_rep_max_screen);

        repsText = findViewById(R.id.repsET);
        weightsText = findViewById(R.id.weightET);
        calculateButton = findViewById(R.id.calculateButton);
        resultTV = findViewById(R.id.resultTV);
    }

    public void calculateOneRepMax(View view) {
        if(repsText.getText().toString().isEmpty() || weightsText.getText().toString().isEmpty()){
            Toast.makeText(view.getContext(), "Please fill in both reps and weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        int reps, weight;
        try {
            reps = Integer.parseInt(repsText.getText().toString());
            weight = Integer.parseInt(weightsText.getText().toString());
        }catch (Exception e){
            Toast.makeText(view.getContext(), "Reps or weight must be a number.", Toast.LENGTH_SHORT).show();
            return;
        }

        double result = (double)weight * (1 + ((double)reps / 30));
        double[] coefficients = {1.000, 1.066, 1.099, 1.132, 1.165, 1.198, 1.231, 1.264, 1.297, 1.330};

        StringBuilder sb = new StringBuilder("Your max rep/weight stats, based on " + String.valueOf(reps) + "x " + String.valueOf(weight) + "kg are:\n");
        for(int i = 0; i < 10; i++) {
            sb.append(String.valueOf(i+1)).append("x ").append((int)(result / coefficients[i])).append("kg\n");
        }

        resultTV.setText(sb.toString());
    }

    public void goBack(View view) {
        Intent homeScreen = new Intent(view.getContext(), HomeScreen.class);
        startActivity(homeScreen);
    }
}
