package com.tanuj.mehta.attender;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogIn.FragmentListener {
    public boolean IS_LOGGED_IN = false;
    String cor_username;
    String cor_password;
    Boolean hasAccount = false;
    String[] queryCols = new String[]{"_id", "Name"};
    MyDBHandler dbHandler;

    @Override
    public Boolean getVerifState(String username, String password) {
        SharedPreferences login_details = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (hasAccount) {
            if ((cor_username.equals(username)) && (cor_password.equals(password))) {
                IS_LOGGED_IN = true;
                Toast.makeText(this, R.string.pass_verified, Toast.LENGTH_SHORT).show();
            } else {
                IS_LOGGED_IN = false;
                Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            SharedPreferences.Editor editor = login_details.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("hasAccount", "true");
            editor.apply();
            IS_LOGGED_IN = true;
            Toast.makeText(this, R.string.acc_created, Toast.LENGTH_SHORT).show();
        }
        return IS_LOGGED_IN;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.log_in);
        setSupportActionBar(toolbar);

        //launch first frame
        Fragment fragment = new LogIn();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.base, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.menu_login);
        //Get saved log in details
        SharedPreferences login_details = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        cor_username = login_details.getString("username", "");
        cor_password = login_details.getString("password", "");
        if (login_details.getString("hasAccount", "false").equals("true")) {
            hasAccount = true;
        } else {
            hasAccount = false;
            //display instructions dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.inst_content)
                    .setTitle(R.string.inst_title)
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), R.string.hope_app, Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        dbHandler = new MyDBHandler(this, null, null, 1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_login) {
            // Handle the click on LogIn option
            if (IS_LOGGED_IN) {
                Toast.makeText(this, R.string.already_logged, Toast.LENGTH_SHORT).show();
                item.setCheckable(false);
            } else {
                Fragment fragment = new LogIn();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.base, fragment).commit();
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(R.string.log_in);
                item.setCheckable(true);
            }

        } else if (id == R.id.menu_attend) {
            Cursor c = dbHandler.getClassList(queryCols);
            if ((IS_LOGGED_IN) && (c.getCount() != 0)) {
                Fragment fragment = new Attendance();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.base, fragment).commit();
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(R.string.attendance);
                item.setCheckable(true);
            } else {
                if (!IS_LOGGED_IN) {
                    Toast.makeText(this, R.string.not_logged, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.no_class, Toast.LENGTH_SHORT).show();
                }
                item.setCheckable(false);
            }

        } else if (id == R.id.menu_students) {
            if (IS_LOGGED_IN) {
                Fragment fragment = new Students();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.base, fragment).commit();
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(R.string.students);
                item.setCheckable(true);
            } else {
                Toast.makeText(this, R.string.not_logged, Toast.LENGTH_SHORT).show();
                item.setCheckable(false);
            }

        } else if (id == R.id.menu_records) {
            Cursor c = dbHandler.getClassList(queryCols);
            if ((IS_LOGGED_IN) && (c.getCount() != 0)) {
                Fragment fragment = new Records();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.base, fragment).commit();
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(R.string.records);
                item.setCheckable(true);
            } else {
                if (!IS_LOGGED_IN) {
                    Toast.makeText(this, R.string.not_logged, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.no_class, Toast.LENGTH_SHORT).show();
                }
                item.setCheckable(false);
            }

        } else if (id == R.id.menu_share) {
            item.setCheckable(false);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_sub));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body));
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));

        } else if (id == R.id.menu_about) {
            Fragment fragment = new About();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.base, fragment).commit();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.about);

        } else if (id == R.id.menu_feedback) {
            item.setCheckable(false);
            ShareCompat.IntentBuilder.from(this)
                    .setType("message/rfc822")
                    .addEmailTo(getString(R.string.tanuj_email))
                    .setSubject(getString(R.string.feedback_sub))
                    .setText(getString(R.string.feedback_starting_text))
                    .setChooserTitle(getString(R.string.share_via_email))
                    .startChooser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
