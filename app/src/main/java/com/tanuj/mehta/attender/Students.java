package com.tanuj.mehta.attender;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Students extends Fragment {
    Button deleteclass;
    Button deletestudent;
    Button addstudent;
    EditText createclass;
    EditText num_students;
    EditText createstudent;
    Spinner classspinner;
    TextView class_textView;
    Button addclass;
    Switch new_old;
    TextView num_added;
    String classname;
    int classsize;
    int num = 0;
    ArrayList<String> names = new ArrayList<String>();
    Context thisContext;
    MyDBHandler dbHandler;
    Cursor c;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisContext = activity;
        dbHandler = new MyDBHandler(thisContext, null, null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.students, container, false);
        deleteclass = (Button) view.findViewById(R.id.deleteclass);
        deleteclass.setEnabled(false);
        deletestudent = (Button) view.findViewById(R.id.deletestudent);
        deletestudent.setEnabled(false);
        addclass = (Button) view.findViewById(R.id.addclass);
        addstudent = (Button) view.findViewById(R.id.addstudent);
        addstudent.setEnabled(false);
        createclass = (EditText) view.findViewById(R.id.createclass);
        createstudent = (EditText) view.findViewById(R.id.createstudent);
        num_students = (EditText) view.findViewById(R.id.num_students);
        classspinner = (Spinner) view.findViewById(R.id.classspinner);
        num_added = (TextView) view.findViewById(R.id.num_added);
        new_old = (Switch) view.findViewById(R.id.switch_newold);
        class_textView = (TextView) view.findViewById(R.id.class_textView);
        classspinner.setVisibility(View.INVISIBLE);
        addclass.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addClass();
                    }
                }
        );
        addstudent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addStudent();
                    }
                }
        );
        deleteclass.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String x = createclass.getText().toString();
                        if (!x.isEmpty()) {
                            dbHandler.deleteClass(x);
                            createclass.setText("");
                        } else {
                            Toast.makeText(thisContext, R.string.name_empty, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        deletestudent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = classspinner.getSelectedItemPosition();
                        c.moveToPosition(pos);
                        String name = c.getString(c.getColumnIndex("Name"));
                        dbHandler.deleteStudent(name, createstudent.getText().toString());
                    }
                }
        );

        new_old.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            addclass.setEnabled(false);
                            num_students.setEnabled(false);
                            deleteclass.setEnabled(true);
                            deletestudent.setEnabled(true);
                            addstudent.setEnabled(false);
                            //Populating spinner with Names of classes
                            class_textView.setVisibility(View.INVISIBLE);
                            classspinner.setVisibility(View.VISIBLE);
                            String[] queryCols = new String[]{"_id", "Name"};
                            String[] adapterCols = new String[]{"Name"};
                            int[] adapterRowViews = new int[]{android.R.id.text1};
                            c = dbHandler.getClassList(queryCols);
                            SimpleCursorAdapter sca = new SimpleCursorAdapter(thisContext, android.R.layout.simple_spinner_item, c, adapterCols, adapterRowViews, 0);
                            sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            classspinner.setAdapter(sca);
                            if (c.getCount() == 0) {
                                deletestudent.setEnabled(false);
                                deleteclass.setEnabled(false);
                                Toast.makeText(thisContext, R.string.no_class, Toast.LENGTH_SHORT).show();
                            } else {
                                deletestudent.setEnabled(true);
                                deleteclass.setEnabled(true);
                            }
                        } else {
                            deleteclass.setEnabled(false);
                            deletestudent.setEnabled(false);
                            addclass.setEnabled(true);
                            addstudent.setEnabled(false);
                            num_students.setEnabled(true);
                            classspinner.setVisibility(View.INVISIBLE);
                            class_textView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        return view;
    }

    public void addClass() {
        classname = createclass.getText().toString();
        String number = num_students.getText().toString();
        if (!(classname.isEmpty()) && !(number.isEmpty())) {
            for (int i = 0; i < classname.length(); i++) {
                char c = classname.charAt(i);
                if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || (c == '_') || ((c >= '0') && (c <= '9'))) {
                } else {
                    Toast.makeText(thisContext, R.string.invalid_string, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Boolean class_exists = false;
            String[] queryCols = new String[]{"_id", "Name"};
            Cursor list = dbHandler.getClassList(queryCols);
            list.moveToFirst();
            while (!list.isAfterLast()) {
                String s = list.getString(list.getColumnIndex("Name"));
                if (s.equals(classname)) {
                    class_exists = true;
                }
                list.moveToNext();
            }
            if (class_exists) {
                Toast.makeText(thisContext, R.string.class_exists, Toast.LENGTH_SHORT).show();
            } else {
                classsize = Integer.parseInt(number);
                class_textView.setText(classname);
                num = 0;
                names.clear();
                addstudent.setEnabled(true);
                Toast.makeText(thisContext, R.string.class_created, Toast.LENGTH_SHORT).show();
                num_added.setText("0/" + classsize);
            }
        } else {
            Toast.makeText(thisContext, R.string.class_empty, Toast.LENGTH_SHORT).show();
        }
        createclass.setText("");
        num_students.setText("");
    }

    public void addStudent() {
        String student_name = createstudent.getText().toString();
        createstudent.setText("");
        if (!(student_name.isEmpty()) && (num < classsize)) {
            for (int i = 0; i < student_name.length(); i++) {
                char c = student_name.charAt(i);
                if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || (c == '_') || ((c >= '0') && (c <= '9'))) {
                } else {
                    Toast.makeText(thisContext, R.string.invalid_string, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            names.add(student_name);
            num_added.setText((num + 1) + "/" + classsize);
            Toast.makeText(thisContext, names.get(num)+" Added!", Toast.LENGTH_SHORT).show();
            if (num == classsize - 1) {
                dbHandler.addClass(classname, names);
                class_textView.setText("");
                num_added.setText("");
            }
            num++;
        } else {
            if (student_name.isEmpty()) {
                Toast.makeText(thisContext, R.string.name_empty, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(thisContext, R.string.list_completed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
