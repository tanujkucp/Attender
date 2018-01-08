package com.tanuj.mehta.attender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Records extends Fragment {
    Context thisContext;
    ListView listclass;
    ListView liststudents;
    TextView student_name;
    TextView student_percent;
    TextView student_total;
    MyDBHandler myDBHandler;
    String sel_class;
    String sel_student;
    ArrayList<String> data;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisContext = activity;
        myDBHandler = new MyDBHandler(thisContext, null, null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.records, container, false);
        listclass = (ListView) view.findViewById(R.id.listclass);
        liststudents = (ListView) view.findViewById(R.id.liststudents);
        student_name = (TextView) view.findViewById(R.id.student_name);
        student_percent = (TextView) view.findViewById(R.id.student_percent);
        student_total = (TextView) view.findViewById(R.id.student_total);

        //Populating class list with values
        final String[] queryCols = new String[]{"_id", "Name"};
        final Cursor classlist = myDBHandler.getClassList(queryCols);
        String[] adapterCols = new String[]{"Name"};
        int[] adapterRowViews = new int[]{android.R.id.text1};
        SimpleCursorAdapter classAdapter = new SimpleCursorAdapter(thisContext, android.R.layout.simple_list_item_1, classlist, adapterCols, adapterRowViews, 0);
        listclass.setAdapter(classAdapter);
        listclass.setSelection(0);

        //Populating student list with values
        classlist.moveToFirst();
        String firstclass = classlist.getString(classlist.getColumnIndex("Name"));
        sel_class = firstclass;
        Cursor studentlist = myDBHandler.getStudentList(queryCols, firstclass);
        final SimpleCursorAdapter studentAdapter = new SimpleCursorAdapter(thisContext, android.R.layout.simple_list_item_1, studentlist, adapterCols, adapterRowViews, 0);
        studentAdapter.changeCursor(studentlist);
        liststudents.setAdapter(studentAdapter);

        listclass.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (classlist.getCount() == 0) {
                        } else {
                            classlist.moveToPosition(position);
                            sel_class = classlist.getString(classlist.getColumnIndex("Name"));
                            Cursor c = myDBHandler.getStudentList(queryCols, sel_class);
                            studentAdapter.changeCursor(c);
                            liststudents.setAdapter(studentAdapter);
                        }
                    }
                }
        );
        liststudents.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor c = studentAdapter.getCursor();
                        c.moveToPosition(position);
                        sel_student = c.getString(c.getColumnIndex("Name"));
                        String temp = sel_student.replace('_', ' ');
                        student_name.setText(temp);
                        data = myDBHandler.getStudentInfo(sel_class, sel_student);
                        int attended=Integer.parseInt(data.get(0));
                        int total=Integer.parseInt(data.get(1));
                        student_total.setText(attended + "/" + total);
                        float percent = 0;
                        if (total != 0) {
                            percent = (float) attended / total;
                        }
                        student_percent.setText(percent * 100 + " %");
                    }
                }
        );
        student_name.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (student_name.length()==0){
                            return;
                        }
                        String body="";
                        for (int i=2;i<data.size();i++){
                            body+=(i-1)+". "+data.get(i)+"\n";
                        }
                        //display Absent dates dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                        builder.setMessage(body)
                                .setTitle(R.string.absence_title)
                                .setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
        );
        return view;
    }
}
