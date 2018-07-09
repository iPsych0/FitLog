package com.example.daniel.fitlog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/*
 * DBHelper is a manager file that contains functions for the databases used in the AddExercises
 * Activity and has all workout data stored in a .db file.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Declaring context, database name and database version
    Context context;
    String name;
    int version;
    AddExercises addExercises;

    // Setting the database parameters
    private static final String DATABASE_NAME = "workouts.db";
    private static final int DATABASE_VERSION = 14;


    public static final String TABLE_MUSCLES = "musclesTable";
    public static final String COLUMN_MUSCLES_ID = "_id";
    public static final String COLUMN_WORKOUT = "workout";
    public static final String COLUMN_EXERCISE = "exercise";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE = "date";



    // DBHelper constructor
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        addExercises = new AddExercises();
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

        System.out.println(workout);
        System.out.println(exercise);
        System.out.println(reps);
        System.out.println(weight);

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
     * Function to delete the rows from a given condition
     */
    public void deleteWorkout(String workoutDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE * FROM " + TABLE_MUSCLES + " WHERE date "  + "=\"" + workoutDate + "\";");
        db.close();
    }

    /*
     * Function that queries over all unique list names and returns an ArrayList of all unique lists
     */
    public ArrayList<String> getWorkoutList() {
        ArrayList<String> workoutList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MUSCLES + ";";

        Cursor c = db.rawQuery(query, null);

        // Move cursor over the query
        if (c.moveToFirst()){
            do{
                // ids = 0, workout = 1, exercise = 2, reps = 3, weight = 4, date = 5
                workoutList.add(c.getString(1));
            } while (c.moveToNext());
        }
        // Close files to save memory and returns the list names
        db.close();
        c.close();

        return workoutList;
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