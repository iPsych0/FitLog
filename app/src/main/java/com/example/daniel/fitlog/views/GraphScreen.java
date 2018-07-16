package com.example.daniel.fitlog.views;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.fitlog.utils.DBHelper;
import com.example.daniel.fitlog.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphScreen extends AppCompatActivity{

    GraphView graph;
    LineGraphSeries<DataPoint> points;
    Intent intentReceived;
    Bundle extrasReceived;
    String chosenExercise;
    TextView graphTitle;
    DBHelper dbHelper;
    HashMap<Double, Date> weightMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_screen);

        graphTitle = (TextView)findViewById(R.id.graphTitle);

        dbHelper = new DBHelper(this, null, null, 1);

        intentReceived = getIntent();
        extrasReceived = intentReceived.getExtras();
        chosenExercise = extrasReceived.getString("chosenExercise");

        graphTitle.setText("Graph for " + chosenExercise);

        graph = (GraphView) findViewById(R.id.graph);
        points = new LineGraphSeries<>();

        graphTitle.setText("Weight history for " + chosenExercise);

        weightMap = dbHelper.getWeightAndDateMap(chosenExercise);

        Map result = weightMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        ArrayList<Double> weights = new ArrayList<>();
        ArrayList<Date> dates = new ArrayList<>();
        for(Object o : result.keySet()){
            weights.add((Double)o);
        }
        for(int i = 0; i < weights.size(); i++){
            dates.add((Date)result.get(weights.get(i)));
        }

        for(int i = 0; i < result.size(); i++){
            points.appendData(new DataPoint(dates.get(i), weights.get(i)), true,
                    dbHelper.getDateListByExercise(chosenExercise).size());
        }

        points.setColor(Color.parseColor("#990a1d"));
        points.setDrawBackground(true);
        points.setBackgroundColor(Color.argb(56, 255, 0, 0));
        points.setDrawDataPoints(true);

        // Set min & max Y axis to the min and max weight values
        ArrayList<String> allWeights = dbHelper.getHighestWeightListGeneral(chosenExercise);
        Collections.sort(allWeights);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Double.parseDouble(allWeights.get(allWeights.size()-1)));

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        if(dates.size() < 3)
            graph.getGridLabelRenderer().setNumHorizontalLabels(dates.size());
        else
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.getGridLabelRenderer().setPadding(32);


        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(dates.get(0).getTime());
        graph.getViewport().setMaxX(dates.get(dates.size() - 1).getTime());

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.addSeries(points);

        points.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                long dateLong = Double.valueOf(dataPoint.getX()).longValue();
                Date date = new Date(dateLong);
                String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date);
                Toast t = Toast.makeText(GraphScreen.this, dateString+":\n"+dataPoint.getY()+"kg", Toast.LENGTH_SHORT);
                t.getView().setBackgroundColor(Color.parseColor("#990a1d"));
                TextView v = t.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.WHITE);
                t.show();
            }
        });
    }
}
