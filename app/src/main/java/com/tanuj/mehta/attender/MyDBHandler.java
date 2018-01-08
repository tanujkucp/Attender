package com.tanuj.mehta.attender;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.widget.Toast;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Attender.db";
    public static final String COLUMN_ID = "_id";
    Context con;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE class_name ( _id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "SHOW TABLES";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getString(1) != null) {
                db.execSQL("DROP TABLE " + c.getString(1));
                c.moveToNext();
            }
        }
        db.close();
        onCreate(db);
    }

    public void addClass(String table_name, ArrayList<String> names) {
        char c = table_name.charAt(0);
        String newtablename = table_name;
        if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || (c == '_')) {
        } else {
            newtablename = "_" + table_name;
        }
        String temp = newtablename.replace(' ', '_');
        SQLiteDatabase db = getWritableDatabase();
        int size = names.size();
        ContentValues class_name = new ContentValues();
        class_name.put("Name", temp);
        db.insert("class_name", null, class_name);
        String query2 = "CREATE TABLE name_" + temp + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , Name TEXT )";
        db.execSQL(query2);
        String query = "CREATE TABLE " + temp + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT ";
        for (int i = 0; i < size; i++) {
            String newcolumnname = names.get(i).replace(' ', '_');
            String n = newcolumnname;
            char digit = newcolumnname.charAt(0);
            if (((digit >= 'A') && (digit <= 'Z')) || ((digit >= 'a') && (digit <= 'z')) || (digit == '_')) {
            } else {
                n = "_" + newcolumnname;
            }
            query += ", " + n + " INTEGER";
            ContentValues val = new ContentValues();
            val.put("Name", n);
            db.insert("name_" + temp, null, val);
        }
        query += ")";
        db.execSQL(query);
        db.close();
        Toast.makeText(con, R.string.all_add, Toast.LENGTH_SHORT).show();
    }

    public void deleteClass(String table_name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        db.execSQL("DROP TABLE IF EXISTS name_" + table_name);
        db.execSQL("DELETE FROM class_name WHERE Name ='" + table_name + "'");
        db.close();
        Toast.makeText(con, R.string.class_deleted, Toast.LENGTH_SHORT).show();
    }

    public void deleteStudent(String table_name, String student_name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM name_" + table_name + " WHERE Name='" + student_name + "'");
        db.close();
        Toast.makeText(con, R.string.stud_deleted, Toast.LENGTH_SHORT).show();
    }

    public void attend(String table_name, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table_name, null, values);
        Toast.makeText(con, R.string.attend_success, Toast.LENGTH_SHORT).show();
        db.close();
    }

    public Cursor getClassList(String[] queryCols) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(true, "class_name", queryCols, null, null, null, null, null, null);
        return c;
    }

    public Cursor getStudentList(String[] queryCols, String class_name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(true, "name_" + class_name, queryCols, null, null, null, null, null, null);
        return c;
    }

    public ArrayList<String> getStudentInfo(String table_name, String student_name) {
        ArrayList<String> data = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String[] queryCols = new String[]{"_id", "Date", student_name};
        Cursor c = db.query(true, table_name, queryCols, null, null, null, null, null, null);
        c.moveToFirst();
        data.add(0, "");
        data.add(1, "");
        int attended = 0, total = 0;
        int i = 2;
        while (!c.isAfterLast()) {
            if (c.getInt(c.getColumnIndex(student_name)) == 1) {
                attended++;
            } else {
                data.add(i, c.getString(c.getColumnIndex("Date")));
                i++;
            }
            total++;
            c.moveToNext();
        }
        data.set(0, "" + attended);
        data.set(1, "" + total);
        return data;
    }
}
