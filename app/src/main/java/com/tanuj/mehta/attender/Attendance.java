package com.tanuj.mehta.attender;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Attendance extends Fragment {
    TextView rollno;
    TextView name;
    MyDBHandler dbHandler;
    Context thisContext;
    Spinner spinner;
    ProgressBar progressBar;
    TextView progress_num;
    String sel_class;
    Cursor studentlist;
    int classsize;
    int num = 0;
    ContentValues values = new ContentValues();
    ArrayList<String> names = new ArrayList<String>();
    String date=new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisContext = activity;
        dbHandler = new MyDBHandler(thisContext, null, null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendence, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        rollno = (TextView) view.findViewById(R.id.rollno);
        name = (TextView) view.findViewById(R.id.name);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progress_num = (TextView) view.findViewById(R.id.progress_num);
        Button present = (Button) view.findViewById(R.id.present);
        Button absent = (Button) view.findViewById(R.id.absent);
        final String[] queryCols = new String[]{"_id", "Name"};

        //Populating spinner with class names;
        final Cursor classlist = dbHandler.getClassList(queryCols);
        String[] adapterCols = new String[]{"Name"};
        int[] adapterRowViews = new int[]{android.R.id.text1};
        final SimpleCursorAdapter classAdapter = new SimpleCursorAdapter(thisContext, android.R.layout.simple_list_item_1, classlist, adapterCols, adapterRowViews, 0);
        spinner.setAdapter(classAdapter);
        classlist.moveToFirst();
        sel_class = classlist.getString(classlist.getColumnIndex("Name"));

        //Gettings students list
        studentlist = dbHandler.getStudentList(queryCols, sel_class);
        studentlist.moveToFirst();
        String firststudent = studentlist.getString(studentlist.getColumnIndex("Name"));
        name.setText(firststudent.replace('_', ' '));
        rollno.setText("1.");
        classsize = studentlist.getCount();

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        classlist.moveToPosition(position);
                        sel_class = classlist.getString(classlist.getColumnIndex("Name"));
                        studentlist = dbHandler.getStudentList(queryCols, sel_class);
                        studentlist.moveToFirst();
                        String s = studentlist.getString(studentlist.getColumnIndex("Name"));
                        classsize = studentlist.getCount();
                        name.setText(s.replace('_', ' '));
                        rollno.setText("1.");
                        num = 0;
                        values.clear();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        present.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (num < classsize) {
                            String key = studentlist.getString(studentlist.getColumnIndex("Name"));
                            values.put(key, 1);
                            if (num == classsize - 1) {
                                values.put("Date",date);
                                dbHandler.attend(sel_class, values);
                                num++;
                                name.setText("");
                                rollno.setText("");
                            } else {
                                studentlist.moveToNext();
                                num++;
                                name.setText(studentlist.getString(studentlist.getColumnIndex("Name")).replace('_', ' '));
                                rollno.setText("" + (num + 1) + ". ");
                            }
                            progress_num.setText(num + "/" + classsize);
                            float per = (float) num / classsize;
                            int range = progressBar.getMax();
                            progressBar.setProgress((int) (per * range));
                        } else {
                            Toast.makeText(thisContext, R.string.another_class, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        absent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (num < classsize) {
                            String key = studentlist.getString(studentlist.getColumnIndex("Name"));
                            values.put(key, 0);
                            if (num == classsize - 1) {
                                values.put("Date",date);
                                dbHandler.attend(sel_class, values);
                                num++;
                                name.setText("");
                                rollno.setText("");
                            } else {
                                studentlist.moveToNext();
                                num++;
                                name.setText(studentlist.getString(studentlist.getColumnIndex("Name")).replace('_', ' '));
                                rollno.setText("" + (num + 1) + ". ");
                            }
                            progress_num.setText(num + "/" + classsize);
                            float per = (float) num / classsize;
                            int range = progressBar.getMax();
                            progressBar.setProgress((int) (per * range));
                        } else {
                            Toast.makeText(thisContext, R.string.another_class, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        return view;
    }

}
