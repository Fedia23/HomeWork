package com.pineapple.softgroup.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pineapple.softgroup.DB.DBHelper;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.DB.model.User;

import java.util.List;
import java.util.regex.Pattern;

public class FragmentRegestration extends Fragment {

    private EditText editNameRegistration, editEmailRegistration, editPassRegistration;
    private Button сheck_in, cancel;
    DBHelper dbHelper;
    private FragmentLogin fragmentLogin;
    private List<User> list;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_regestration, null);

        ((MainActivity)getActivity()).toggle.setDrawerIndicatorEnabled(false);
        ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        сheck_in = (Button) v.findViewById(R.id.сheck_in);
        cancel = (Button)v.findViewById(R.id.cancel);

        editNameRegistration = (EditText)v.findViewById(R.id.editNameRegistration);
        editEmailRegistration = (EditText)v.findViewById(R.id.editEmailRegistration);
        editPassRegistration  = (EditText)v.findViewById(R.id.editPassRegistration);

        fragmentLogin = new FragmentLogin();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentManager().beginTransaction().replace(R.id.container, fragmentLogin).commit();
            }
        });
        dbHelper = new DBHelper(getActivity());

        сheck_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name     = editNameRegistration.getText().toString();
                final String email    = editEmailRegistration.getText().toString();
                final String password = editPassRegistration.getText().toString();

                if (isPasswordValid(password) && isNameValid(name)) {
                    User ct = new User(dbHelper.countUser, name, email, password);
                    dbHelper.addContact(ct);
                    Toast.makeText(getActivity(), "\n" +
                            "Are you registered",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Wrong email/password",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    public boolean isEmailValid(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private boolean isDublicateEmail(String email) {
        list = dbHelper.getAllContacts();
        boolean value = false;
        if (list.size() > 1) {
            for (User cn : list) {
                if (email.equals(cn.getLogin())) {
                    value = false;
                } else {
                    value = true;
                }
            }
        } else { value = true; }

        return value;
    }


    private boolean isNameValid(String name) {

        return name.length() >= 3;
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 6;
    }
}