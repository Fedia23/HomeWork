package com.pineapple.softgroup.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pineapple.softgroup.DB.DBHelper;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.DB.model.User;

import java.util.List;

public class FragmentLogin extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private EditText login, password;
    private Button sing_in, registration, google, facebook;
    SharedPreferences sharedPreferences;
    private FragmentContact fragmentContact;
    private static final String TAG = "GoogleActivity";

    public boolean googleStatus;
    DBHelper dbHelper;
    private FragmentRegestration fragmentRegestration;

    private MainActivity main;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_login, null);

        ((MainActivity)getActivity()).toggle.setDrawerIndicatorEnabled(false);
        ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        dbHelper = new DBHelper(getActivity());
        main = new MainActivity();

        final Intent intentMain = new Intent(getActivity(), MainActivity.class);

        login = (EditText)v.findViewById(R.id.login);
        password = (EditText)v.findViewById(R.id.password);
        login.setText("fedia@fedia.com");
        password.setText("fediafedia");

        sing_in = (Button)v.findViewById(R.id.sing_in);
        registration = (Button)v.findViewById(R.id.registration);
        google = (Button)v.findViewById(R.id.buttonSingInGoogle);
        facebook = (Button)v.findViewById(R.id.buttonSingInFacebook);


        GoogleApiClient.OnConnectionFailedListener connectionListener;

        fragmentContact = new FragmentContact();

        sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Semail    = login.getText().toString();
                final String Spassword = password.getText().toString();
                if (validation(Semail, Spassword)) {
                    getFragmentManager().beginTransaction().replace(R.id.container, fragmentContact).commit();
                    sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putBoolean("isLocked", true);
                    ed.commit();
                } else {
                    Toast.makeText(getActivity(), "Wrong email/password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRegestration = new FragmentRegestration();
                getFragmentManager().beginTransaction().replace(R.id.container, fragmentRegestration).commit();
            }
        });



        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).signIn();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).facebookGo(facebook);
            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private boolean validation(String name, String pass) {
        boolean value = false;
        if (isEmailValid(name) && isPasswordValid(pass)) {
            List<User> listUser = dbHelper.getAllContacts();
            for (User cn : listUser) {
                if (name.equals(cn.getLogin()) && pass.equals(cn.getPass())) {
                    value = true;
                } else {
                    value = false;
                }
            }
        }
        return value;
    }

    private boolean isEmailValid(String email) { return email.contains("@") && email.length() > 6; }

    private boolean isPasswordValid(String password) { return password.length() > 6; }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {

    }
}
