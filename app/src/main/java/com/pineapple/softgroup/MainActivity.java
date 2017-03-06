package com.pineapple.softgroup;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.pineapple.softgroup.DB.DBHelper;
import com.pineapple.softgroup.fragments.FragmentContact;
import com.pineapple.softgroup.fragments.FragmentLogin;
import com.pineapple.softgroup.fragments.FragmentLogout;
import com.pineapple.softgroup.fragments.FragmentMap;
import com.pineapple.softgroup.fragments.FragmentWeater;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Pineapple";
    private static final int RC_SIGN_IN = 9001;

    SharedPreferences sharedPreferences;
    private FragmentLogin fragmentLogin;
    private FragmentContact fragmentWallcome;
    private FragmentWeater fragmentWeater;
    private FragmentLogout fragmentLogout;
    private FragmentMap fragmentMap;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    private TextView userEmail;
    DBHelper dbHelper;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //retrofit

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentLogin = new FragmentLogin();
        fragmentWallcome = new FragmentContact();
        fragmentWeater = new FragmentWeater();
        fragmentLogout = new FragmentLogout();
        fragmentMap = new FragmentMap();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Boolean isLocked;
        isLocked = sharedPreferences.getBoolean("isLocked", false);

        if (isLocked) {
            fragmentTransaction.add(R.id.container, fragmentWallcome).commit();
        } else {
            fragmentTransaction.add(R.id.container, fragmentLogin).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutD:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragmentLogout);
                fragmentTransaction.commit();
                return true;
            case R.id.contact_item:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragmentWallcome);
                fragmentTransaction.commit();
                return true;
            case R.id.weater_item:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragmentWeater);
                fragmentTransaction.commit();
                return true;
            case R.id.map_item:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragmentMap);
                fragmentTransaction.commit();

            default:

                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
