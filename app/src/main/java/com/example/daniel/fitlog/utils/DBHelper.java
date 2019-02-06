package com.example.daniel.fitlog.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;

import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;
import com.example.daniel.fitlog.views.AddExercisesScreen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/*
 * DBHelper is a manager file that contains functions for the databases used in the AddExercisesScreen
 * Activity and has all workout data stored in a .db file.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Declaring context, database name and database version
    private Context context;
    private String name;
    private int version;

    // Setting the database parameters
    private static final String DATABASE_NAME = "workouts.db";
    private static final int DATABASE_VERSION = 14;

    private static final String TABLE_MUSCLES = "musclesTable";
    private static final String COLUMN_MUSCLES_ID = "_id";
    private static final String COLUMN_WORKOUT = "workout";
    private static final String COLUMN_EXERCISE = "exercise";
    private static final String COLUMN_REPS = "reps";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_DATE = "date";

    // DBHelper constructor
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
        this.name = DATABASE_NAME;
        this.version = DATABASE_VERSION;
    }

    /*
     * onCreate method that creates table on first launch
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_MUSCLES + "(" +
                COLUMN_MUSCLES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORKOUT + " TEXT, " + COLUMN_EXERCISE + " TEXT, " +
                COLUMN_REPS + " INTEGER, " + COLUMN_WEIGHT + " INTEGER, " +
                COLUMN_DATE + " TEXT" + ");";
        db.execSQL(query);
    }

    /*
     * If the database is updated, drop the previous version(s) of the table and create a new one
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSCLES);
        onCreate(db);
    }

    /*
     * Function to add the workouts to the database
     */
    public void addWorkout(String workout, String exercise, String reps, String weight, String date){

        ContentValues content = new ContentValues();
        content.put(COLUMN_WORKOUT, workout);
        content.put(COLUMN_EXERCISE, exercise);
        content.put(COLUMN_REPS, reps);
        content.put(COLUMN_WEIGHT, weight);
        content.put(COLUMN_DATE, date);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MUSCLES, null, content);
        db.close();
    }

    /*
     * Function to edit the workouts in the database
     */
    public void editWorkout(int id, String reps, String weight){

        ContentValues content = new ContentValues();
        content.put(COLUMN_REPS, reps);
        content.put(COLUMN_WEIGHT, weight);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_MUSCLES, content, COLUMN_MUSCLES_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /*
     * Function to delete the rows from a given condition
     */
    public void deleteWorkout(String exercise, String date){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_MUSCLES, COLUMN_EXERCISE + "=? AND " + COLUMN_DATE + "=?", new String[]{exercise, date});
        db.close();
    }

    /*
     * Function to delete the rows from a given condition
     */
    public void deleteSet(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_MUSCLES, COLUMN_MUSCLES_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<String> getExerciseList(String workout) {
        ArrayList<String> exerciseList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT exercise FROM " + TABLE_MUSCLES + " WHERE workout = '" + workout + "';";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                exerciseList.add(c.getString(0));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return exerciseList;
    }

    public ArrayList<Set> getAllSetsByWorkoutAndDate(String workout, String date){
        ArrayList<Set> setList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MUSCLES + " WHERE workout = '" + workout.toLowerCase() + "' AND date = '" + date + "';";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                Set set = new Set(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5));
                setList.add(set);
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return setList;
    }

    public ArrayList<Set> getAllSetsByWorkoutAndDateAndExercise(String workout, String date, String exercise){
        ArrayList<Set> setList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MUSCLES + " WHERE workout = '" + workout.toLowerCase() + "' AND date = '" + date + "' AND exercise = '" + exercise +  "';";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                Set set = new Set(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5));
                setList.add(set);
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return setList;
    }

    public ArrayList<String> getAllMuscleGroupWorkouts(String workout) {
        ArrayList<String> exerciseList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT date FROM " + TABLE_MUSCLES + " WHERE workout = '" + workout.toLowerCase() + "'";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                exerciseList.add(c.getString(0));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        exerciseList.sort(new Comparator<String>() {
            @Override
            public int compare(String first, String next) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate currentDate = LocalDate.parse(first, formatter);
                LocalDate nextDate = LocalDate.parse(next, formatter);

                if(currentDate.isBefore(nextDate))
                    return 1;
                else if(currentDate.isAfter(nextDate))
                    return -1;
                else
                    return 0;
            }
        });

        return exerciseList;
    }

    public ArrayList<String> getRepsList() {
        ArrayList<String> repsList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MUSCLES + ";";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                repsList.add(c.getString(3));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return repsList;
    }

    public ArrayList<String> getWeightList() {
        ArrayList<String> weightList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MUSCLES + ";";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                weightList.add(c.getString(4));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return weightList;
    }

    public ArrayList<String> getHighestWeightList(String exercise) {
        ArrayList<String> weightList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT weight FROM " + TABLE_MUSCLES + " WHERE exercise = '" + exercise + "' ORDER BY weight DESC;";

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

    public double getHighestDailyWeight(String exercise, String date) {
        ArrayList<Double> weightList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT weight FROM " + TABLE_MUSCLES + " WHERE exercise = '" + exercise + "' AND date = '" + date + "' ORDER BY weight DESC;";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                weightList.add(Double.parseDouble(c.getString(0)));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        Collections.sort(weightList);
        return weightList.get(weightList.size()-1);
    }

    public HashMap<Double, Date> getWeightAndDateMap(String exercise) {
        HashMap<Double, Date> weightList = new HashMap<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MUSCLES + " WHERE exercise = '" + exercise + "'";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                String dateString = c.getString(c.getColumnIndex("date"));
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                try {
                    weightList.put(getHighestDailyWeight(exercise, dateString), format.parse(dateString));
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

    public ArrayList<String> getHighestWeightListGeneral(String exercise) {
        ArrayList<String> weightList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT weight FROM " + TABLE_MUSCLES + " WHERE exercise = '" + exercise + "';";

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

    public ArrayList<String> getDateListByExercise(String exercise) {
        ArrayList<String> dateList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT date FROM " + TABLE_MUSCLES + " WHERE exercise = '" + exercise + "';";

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
}