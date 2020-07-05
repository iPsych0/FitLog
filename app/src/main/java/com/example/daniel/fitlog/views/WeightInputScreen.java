package com.example.daniel.fitlog.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.utils.WeightDB;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeightInputScreen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private GraphView graph;
    private EditText weightInput;
    private Button addWeightButton;
    private Button returnButton;
    private Button selectDateButton;
    private LocalDate selectedDate;
    private LineGraphSeries<DataPoint> points;
    private WeightDB dbHelper;
    private Map<Date, Double> weightMap = new HashMap<>();
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_input_screen);

        init();
        List<Date> dates = populateGraph();
        if (!dates.isEmpty()) {
            stylizeGraph(dates);
        }

        listenForPointTaps();
        listenForDateChanges();
    }

    private void init() {
        graph = findViewById(R.id.weightGraph);
        weightInput = findViewById(R.id.weightInput);
        addWeightButton = findViewById(R.id.addWeightButton);
        selectDateButton = findViewById(R.id.dateSelectButton);
        returnButton = findViewById(R.id.returnButton5);

        // Set the calendar to today's date by default
        c = Calendar.getInstance();
        selectedDate = LocalDate.now();
        selectDateButton.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        dbHelper = new WeightDB(this, null, null, 1);
    }

    private void listenForDateChanges() {
        // Listen for when to open the calendar
        selectDateButton.setOnClickListener(v -> {
            // Use the current date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Open the calendar pop-up when pressed
            DatePickerDialog datePickerDialog = new DatePickerDialog(WeightInputScreen.this, WeightInputScreen.this, year, month, day);
            datePickerDialog.show();
        });
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
        selectDateButton.setText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    /**
     * Populates the graph with dates and weights from the database
     *
     * @return A list of dates, to be used to format the graph
     */
    private List<Date> populateGraph() {
        points = new LineGraphSeries<>();
        graph.removeAllSeries();

        weightMap = dbHelper.getWeightAndDateMap();

        ArrayList<Double> weights = new ArrayList<>();
        ArrayList<Date> dates = new ArrayList<>();
        for (Object o : weightMap.keySet()) {
            dates.add((Date) o);
        }
        for (int i = 0; i < dates.size(); i++) {
            weights.add(weightMap.get(dates.get(i)));
        }

        for (int i = 0; i < weightMap.size(); i++) {
            points.appendData(new DataPoint(dates.get(i), weights.get(i)), true,
                    weightMap.size());
        }

        return dates;
    }

    /**
     * Stylizes the graph
     *
     * @param dates The list of dates to be stylized
     */
    private void stylizeGraph(List<Date> dates) {
        points.setColor(Color.parseColor("#990a1d"));
        points.setDrawBackground(true);
        points.setBackgroundColor(Color.argb(56, 255, 0, 0));
        points.setDrawDataPoints(true);


        // Set min & max Y axis to the min and max weight values
        ArrayList<String> allWeights = dbHelper.getHighestWeightListGeneral();
        Collections.sort(allWeights);

        // Set highest point of graph to + 20% of your max weight
        double highestWeight = Double.parseDouble(allWeights.get(allWeights.size() - 1));
        int maxY = (int) (highestWeight * 1.2);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(50);
        graph.getViewport().setMaxY(maxY);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        if (dates.size() < 3)
            graph.getGridLabelRenderer().setNumHorizontalLabels(dates.size());
        else
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.getGridLabelRenderer().setPadding(32);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(dates.get(0).getTime());
        graph.getViewport().setMaxX(dates.get(dates.size() - 1).getTime());

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScalable(true);

        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.addSeries(points);
    }

    /**
     * Listens for taps on points in the graph and displays an info message
     */
    private void listenForPointTaps() {
        points.setOnDataPointTapListener((series, dataPoint) -> {
            long dateLong = Double.valueOf(dataPoint.getX()).longValue();
            Date date = new Date(dateLong);
            String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date);
            Toast t = Toast.makeText(WeightInputScreen.this, dateString + ":\n" + dataPoint.getY() + "kg", Toast.LENGTH_SHORT);
            t.getView().setBackgroundColor(Color.parseColor("#990a1d"));
            TextView v = t.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.WHITE);
            t.show();
        });
    }

    public void addWeight(View view) {
        if (weightInput.getText().toString().isEmpty()) {
            Toast.makeText(WeightInputScreen.this, "Please enter your current weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate date = getSelectedDate();
        String formatted = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        List<String> allDates = dbHelper.getAllDates();
        if (allDates.contains(formatted)) {
            dbHelper.editWeight(formatted, dbHelper.getWeightByDate(formatted), weightInput.getText().toString());
        } else {
            dbHelper.addWeight(weightInput.getText().toString(), formatted);
        }

        List<Date> dates = populateGraph();
        if (!dates.isEmpty()) {
            stylizeGraph(dates);
        }

        Toast.makeText(WeightInputScreen.this, "Weight added.", Toast.LENGTH_SHORT).show();
    }

    public void goBackHome(View view) {
        Intent intent = new Intent(WeightInputScreen.this, HomeScreen.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selection) {
        this.selectedDate = selection;
    }
}
