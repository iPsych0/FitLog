package com.example.daniel.fitlog.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class WeightDB extends SQLiteOpenHelper {
    // Declaring context, database name and database version
    private Context context;
    private String name;
    private int version;

    // Setting the database parameters
    private static final String DATABASE_NAME = "weights.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_WEIGHTS = "weightsTable";
    private static final String COLUMN_MUSCLES_ID = "_id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_DATE = "date";

    public WeightDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
        this.name = DATABASE_NAME;
        this.version = DATABASE_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_WEIGHTS + "(" +
                COLUMN_MUSCLES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WEIGHT + " TEXT, " +
                COLUMN_DATE + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        onCreate(db);
    }

    /*
     * Function to add the workouts to the database
     */
    public void addWeight(String weight, String date) {

        ContentValues content = new ContentValues();
        content.put(COLUMN_WEIGHT, weight);
        content.put(COLUMN_DATE, date);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_WEIGHTS, null, content);
        db.close();
    }

    public void editWeight(String date, String oldWeight, String newWeight){
        ContentValues content = new ContentValues();
        content.put(COLUMN_WEIGHT, newWeight);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_WEIGHTS, content, COLUMN_WEIGHT + "=?" + " AND " + COLUMN_DATE + "='" + date + "';", new String[]{String.valueOf(oldWeight)});
        db.close();
    }

    public Map<Date, Double> getWeightAndDateMap() {
        Map<Date, Double> weightList = new TreeMap<>(Date::compareTo);
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT date FROM " + TABLE_WEIGHTS + ";";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                String dateString = c.getString(c.getColumnIndex("date"));
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                try {
                    weightList.put(format.parse(dateString), getHighestDailyWeight(dateString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return weightList;
    }

    private double getHighestDailyWeight(String date) {
        ArrayList<Double> weightList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT weight FROM " + TABLE_WEIGHTS + " WHERE date = '" + date + "';";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                weightList.add(Double.parseDouble(c.getString(0)));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return weightList.get(weightList.size()-1);
    }

    public ArrayList<String> getHighestWeightListGeneral() {
        ArrayList<String> weightList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT weight FROM " + TABLE_WEIGHTS + ";";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                weightList.add(c.getString(0));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return weightList;
    }

    public ArrayList<String> getAllDates() {
        ArrayList<String> dateList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT date FROM " + TABLE_WEIGHTS + ";";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                dateList.add(c.getString(0));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return dateList;
    }

    public String getWeightByDate(String date) {
        String weight = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_WEIGHTS + " WHERE date = '" + date + "';";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                weight = c.getString(c.getColumnIndex("weight"));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return weight;
    }
}
