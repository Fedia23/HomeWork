package com.pineapple.softgroup.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;


public class FragmentLogout extends Fragment {


    private static Button yes, no;
    FrameLayout layout;
    final String LOG_TAG = "myLogs";
    private FragmentLogin fragmentLogin;
    private FragmentContact fragmentContact;
    SharedPreferences sharedPreferences;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, null);

        ((MainActivity)getActivity()).toggle.setDrawerIndicatorEnabled(false);
        ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        mAuth = FirebaseAuth.getInstance();

        final SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final Boolean isGoogle;
        isGoogle = sharedPreferences.getBoolean("isGoogle", false);

        yes = (Button)v.findViewById(R.id.yes);
        no = (Button)v.findViewById(R.id.no);

        fragmentLogin = new FragmentLogin();
        fragmentContact = new FragmentContact();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.container, fragmentLogin).commit();

                if (isGoogle) {
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putBoolean("isGoogle", false);
                    ed.commit();
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.disconnect();
                    }
                    FirebaseAuth.getInstance().signOut();
                    mAuth.signOut();
                } else {
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putBoolean("isLocked", false);
                    ed.commit();
                }

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fragmentTransaction.replace(R.id.container, fragmentContact).commit();

            }
        });

        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Toast.makeText(getActivity(), "FirstFragment.onActivityCreated()",
//                Toast.LENGTH_LONG).show();
//        Log.d("Fragment 1", "onActivityCreated");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Toast.makeText(getActivity(), "FirstFragment.onPause()",
//                Toast.LENGTH_LONG).show();
//        Log.d("Fragment 1", "onPause");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Toast.makeText(getActivity(), "FirstFragment.onStop()",
//                Toast.LENGTH_LONG).show();
//        Log.d("Fragment 1", "onStop");
//    }
}


