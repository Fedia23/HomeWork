package com.pineapple.softgroup.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pineapple.softgroup.DB.DBHelper;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.DB.model.User;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class FragmentLogin extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private EditText login, password;
    private Button sing_in, registration, google;
    SharedPreferences sharedPreferences;
    private FragmentContact fragmentContact;
    private static final String TAG = "GoogleActivity";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirechatUser;
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog mProgressDialog;
    CallbackManager callbackManager;
    public boolean googleStatus;
    DBHelper dbHelper;
    private FragmentRegestration fragmentRegestration;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_login, null);

        ((MainActivity)getActivity()).toggle.setDrawerIndicatorEnabled(false);
        ((MainActivity)getActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        dbHelper = new DBHelper(getActivity());

        final Intent intentMain = new Intent(getActivity(), MainActivity.class);

        login = (EditText)v.findViewById(R.id.login);
        password = (EditText)v.findViewById(R.id.password);
        login.setText("fedia@fedia.com");
        password.setText("fediafedia");

        sing_in = (Button)v.findViewById(R.id.sing_in);
        registration = (Button)v.findViewById(R.id.registration);
        google = (Button)v.findViewById(R.id.buttonSingInGoogle);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) v.findViewById(R.id.fb_login_btn);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.i(TAG, "goodFacebook");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity().getApplicationContext(),"internet_error",Toast.LENGTH_SHORT).show();
            }

        });

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

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    updateUI(true);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
               .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                   @Override
                   public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                       Toast.makeText(getActivity().getApplicationContext(),"Failed",Toast.LENGTH_SHORT);
                   }
               })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else {
                Toast.makeText(getActivity().getApplicationContext(), "error email",Toast.LENGTH_SHORT).show();
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()){
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    private  void handleFacebookAccessToken(AccessToken token){
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credital = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credital).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete" + task.isSuccessful());
                if (!task.isSuccessful()){
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_LONG).show();
                } else {
                    updateUI(true);
                }
            }
        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(false);
                    }
                });
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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    public void updateUI(boolean b) {
        if (b) {
            sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putBoolean("isGoogle", true);
            ed.commit();

            getFragmentManager().beginTransaction().replace(R.id.container, fragmentContact).commit();
        } else {
            Toast.makeText(getActivity(), "Error signing in", Toast.LENGTH_LONG).show();
            // googleStatus(false);
        }
    }

    private boolean isEmailValid(String email) { return email.contains("@") && email.length() > 6; }

    private boolean isPasswordValid(String password) { return password.length() > 6; }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
