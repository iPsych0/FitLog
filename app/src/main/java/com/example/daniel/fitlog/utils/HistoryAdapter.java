package com.example.daniel.fitlog.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.daniel.fitlog.R;
import com.example.daniel.fitlog.com.example.daniel.fitlog.models.Set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<Set> sets;

    public HistoryAdapter(Context context, List<Set> sets) {
        this.context = context;
        this.sets = sets;
    }

    @Override
    public int getCount() {
        return formatExercises().size();
    }

    @Override
    public Object getItem(int i) {
        return sets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.layout, null);
        TextView topText = v.findViewById(R.id.topTextList);
        TextView subText = v.findViewById(R.id.subTextList);

        try {
            topText.setText(formatExercises().get(i));
            subText.setText(formatSets().get(i));
            v.setTag(sets.get(i).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    private List<String> formatExercises() {
        List<String> exercises = new ArrayList<>();

        for (Set s : sets) {
            if(!exercises.contains(s.getExercise())){
                exercises.add(s.getExercise());
            }
        }

        return exercises;
    }

    /**
     * Groups the exercises together in a nice way
     *
     * @return a sorted/grouped list of sets
     */
    private List<String> formatSets() {
        StringBuilder sb = new StringBuilder();
        String lastChecked = sets.get(0).getExercise();
        List<String> sets = new ArrayList<>();

        for (Set s : this.sets) {
            if (s.getExercise().equalsIgnoreCase(lastChecked)) {
                sb.append(s).append("\n");
            } else {
                sets.add(sb.toString());
                lastChecked = s.getExercise();
                sb.setLength(0);
                sb.append(s).append("\n");
            }
        }
        sets.add(sb.toString());

        return sets;
    }

    public void clear() {
        sets.clear();
    }

    public void addAll(Collection<? extends Set> list) {
        sets.addAll(list);
    }
}
