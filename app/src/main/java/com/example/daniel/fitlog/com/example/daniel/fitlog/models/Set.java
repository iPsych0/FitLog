package com.example.daniel.fitlog.com.example.daniel.fitlog.models;

import java.util.Locale;

public class Set {

    private String muscleGroup, exercise, date;
    private int id, reps;
    private double weight;

    public Set(int id, String muscleGroup, String exercise, int reps, double weight, String date) {
        this.id = id;
        this.muscleGroup = muscleGroup;
        this.exercise = exercise;
        this.date = date;
        this.reps = reps;
        this.weight = weight;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return reps + "x " + formatDouble(weight) + "kg";
    }

    private String formatDouble(double weight) {
        if (weight == (long) weight) {
            return String.format(Locale.ENGLISH, "%d", (long) weight);
        }
        return String.format(Locale.ENGLISH, "%s", weight);
    }

}
