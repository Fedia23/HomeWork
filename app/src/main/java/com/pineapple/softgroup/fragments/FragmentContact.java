package com.pineapple.softgroup.fragments;

import android.support.v4.app.Fragment;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.pineapple.softgroup.DB.DBHelperContact;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.adapter.RecyclerAdapter;
import com.pineapple.softgroup.DB.model.Contacts;

import java.util.ArrayList;
import java.util.List;

public class FragmentContact extends Fragment {

    private static final String LOG_TAG = "User";
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ContentResolver contentResolver;

    private boolean animationFab = true;
    public FloatingActionButton fab;

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;

    FragmentLogout bFrag;
    AddNumberFragment addFruit;
    private FragmentWeater fragmentWeater;

    DBHelperContact dbHelperContact;
    public List<Contacts> contactsList;
    public List<Contacts> contactsSearch;

    private ProgressBar progressBar;

    public FragmentContact() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)  {
        View v = inflater.inflate(R.layout.fragment_walcome, null);

        ((MainActivity)getActivity()).toggle.setDrawerIndicatorEnabled(true);
        ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        addFruit = new AddNumberFragment();
        bFrag = new FragmentLogout();
        fragmentWeater = new FragmentWeater();

        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);

        dbHelperContact = new DBHelperContact(getActivity());

        contactsList = dbHelperContact.getAllContacts();

        //recycler
        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerStart();

        //fragment
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        //show fragment addContact
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatioFab(view);
                addFruit = new AddNumberFragment();
                fragmentManager.beginTransaction().remove(FragmentContact.this).commit();
                fragmentManager.beginTransaction().add(R.id.container, addFruit).commit();
                mAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    public void animatioFab(View view) {
        if (animationFab) {
            final Animation anim;
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
            view.startAnimation(anim);
            animationFab = false;
        } else {
            final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
            view.startAnimation(anim);
            animationFab = true;
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main2, menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(searchQueryListener);

    }

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            search(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            search(newText);
            return true;
        }

        public void search(String query) {
            contactsSearch = filter(contactsList, query);
            mAdapter = new RecyclerAdapter((ArrayList) contactsSearch, getActivity());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    };

    private static List<Contacts> filter(List<Contacts> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Contacts> filteredModelList = new ArrayList<>();
        for (Contacts model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                    logoutPress();
                return true;
            case R.id.getContact:
                new AsinkContact().execute();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutPress() {
        fragmentManager.beginTransaction().remove(FragmentContact.this).commit();
        fragmentManager.beginTransaction().add(R.id.container, bFrag).commit();
    }

    private boolean dublicatContact(String number) {
        boolean value = false;

        if (dbHelperContact.getAllContacts().contains(number)) { value = false; }
        else { value = true; }
        return value;
    }

    private void getContact(){
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {

            String name;
            String phone;

            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (dublicatContact(phone)) {
                    dbHelperContact.addContact(new Contacts(name, phone));
                } else {
                    Log.v(LOG_TAG, "Cursor is empty");
                }
            }
        } else {
            Log.v(LOG_TAG, "Cursor is empty");
        }
        cursor.close();
    }


    public void recyclerStart() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter((ArrayList) contactsList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public boolean validPrice(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private class AsinkContact extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            getContact();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    }
}
