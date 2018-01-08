package com.tanuj.mehta.attender;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText username;
    EditText oldpassword;
    EditText newpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sett_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        username = (EditText) findViewById(R.id.sett_username);
        oldpassword = (EditText) findViewById(R.id.sett_oldpass);
        newpassword = (EditText) findViewById(R.id.sett_newpass);
    }

    public void change(View view) {
        SharedPreferences login_details = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if ((login_details.getString("username", "@#$%&*").equals(username.getText().toString())) && (login_details.getString("password", "@#$%&*").equals(oldpassword.getText().toString()))) {
            if (newpassword.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.pass_empty,Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences.Editor editor = login_details.edit();
            editor.putString("username", username.getText().toString());
            editor.putString("password", newpassword.getText().toString());
            editor.putString("hasAccount", "true");
            editor.apply();
            oldpassword.setText("");
            newpassword.setText("");
            username.setText("");
            Toast.makeText(this, R.string.pass_change_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            oldpassword.setText("");
            newpassword.setText("");
        }
    }
}
