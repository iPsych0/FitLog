package com.example.daniel.fitlog.com.example.daniel.fitlog.models;

public class Set {

    private String muscleGroup, exercise, date;
    private int id, reps, weight;

    public Set(int id, String muscleGroup, String exercise, int reps, int weight, String date) {
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
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
        return reps + "x " + weight + "kg";
    }

}
