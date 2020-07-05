package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.utils.DBHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HighestWeightGraphScreen extends AppCompatActivity {

    private GraphView graph;
    private LineGraphSeries<DataPoint> points;
    private Intent intentReceived;
    private Bundle extrasReceived;
    private String chosenExercise;
    private TextView graphTitle;
    private DBHelper dbHelper;
    private Map<Date, Double> weightMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highest_weight_graph_screen);

        init();

        List<Date> dates = populateGraph();

        stylizeGraph(dates);

        listenForPointTaps();
    }

    /**
     * Listens for taps on points in the graph and displays an info message
     */
    private void listenForPointTaps() {
        points.setOnDataPointTapListener((series, dataPoint) -> {
            long dateLong = Double.valueOf(dataPoint.getX()).longValue();
            Date date = new Date(dateLong);
            String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date);
            Toast t = Toast.makeText(HighestWeightGraphScreen.this, dateString + ":\n" + dataPoint.getY() + "kg", Toast.LENGTH_SHORT);
            t.getView().setBackgroundColor(Color.parseColor("#990a1d"));
            TextView v = t.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.WHITE);
            t.show();
        });
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
        ArrayList<String> allWeights = dbHelper.getHighestWeightListGeneral(chosenExercise);
        Collections.sort(allWeights);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Double.parseDouble(allWeights.get(allWeights.size() - 1)));

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
     * Initializes the graph screen
     */
    private void init() {
        graphTitle = (TextView) findViewById(R.id.graphTitle);
        graph = (GraphView) findViewById(R.id.graph);

        dbHelper = new DBHelper(this, null, null, 1);

        intentReceived = getIntent();
        extrasReceived = intentReceived.getExtras();

        if (extrasReceived != null) {
            chosenExercise = extrasReceived.getString("chosenExercise");
        } else {
            Toast.makeText(this, "Could not get the chosen exercise from the previous screen. Please retry.", Toast.LENGTH_LONG).show();
            return;
        }

        points = new LineGraphSeries<>();

        graphTitle.setText("Highest weight history for " + chosenExercise);
    }

    /**
     * Populates the graph with dates and weights from the database
     *
     * @return A list of dates, to be used to format the graph
     */
    private List<Date> populateGraph() {
        weightMap = dbHelper.getWeightAndDateMap(chosenExercise);

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
}
