package com.pineapple.softgroup.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.pineapple.softgroup.DB.DBHelperContact;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.adapter.RecyclerAdapter;
import com.pineapple.softgroup.DB.model.Contacts;

import java.util.ArrayList;
import java.util.List;

public class AddNumberFragment extends Fragment {
    private Button add,cancel;
    private EditText name, number;
    DBHelperContact dbHelperContact;
    FragmentContact fragmentContact;
    private Context context;

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public List<Contacts> contactsList;
    ActionBarDrawerToggle toggle;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, null);

        ((MainActivity)getActivity()).toggle.setDrawerIndicatorEnabled(false);
        ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        add    = (Button) v.findViewById(R.id.addContact);
        cancel = (Button) v.findViewById(R.id.cancelFr);

        name  = (EditText) v.findViewById(R.id.nameFr);
        number = (EditText) v.findViewById(R.id.numberFr);

        dbHelperContact = new DBHelperContact(getActivity());
        fragmentContact = new FragmentContact();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(AddNumberFragment.this).commit();
                getFragmentManager().beginTransaction().add(R.id.container, fragmentContact).commit();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = name.getText().toString();
                String textNumber = number.getText().toString();

                if (!(validText(textName))) {
                    Toast.makeText(getActivity(), "short value",
                            Toast.LENGTH_LONG).show();
                } else {
                    dbHelperContact.addContact(new Contacts(textName, textNumber));

                    mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mAdapter = new RecyclerAdapter((ArrayList) contactsList, getActivity());
                    mAdapter.notifyDataSetChanged();
                    getFragmentManager().beginTransaction().remove(AddNumberFragment.this).commit();
                    getFragmentManager().beginTransaction().add(R.id.container, fragmentContact).commit();
                }
            }
        });
        return v;
    }

    public boolean validText(String text) {
            return text.length() > 3;
    }

    public boolean validNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
