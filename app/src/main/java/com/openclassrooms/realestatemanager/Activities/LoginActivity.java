package com.openclassrooms.realestatemanager.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.openclassrooms.realestatemanager.Models.UserHelper;
import com.openclassrooms.realestatemanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Boolean newAccount = false;
    Boolean emailExists = false;
    String password;
    Boolean forgotYourPasswordBool = false;
    private static final int RC_SIGN_IN = 123;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        if (mPrefs.getString("username", null) != null) {
            Intent myIntent = new Intent(this, MainActivity.class);
            Toast.makeText(this, "Welcome back, " + mPrefs.getString("username", null), Toast.LENGTH_LONG).show();
            startActivity(myIntent);
        }

        Button validateButton = findViewById(R.id.validateButton);
        validateButton.setOnClickListener(v -> startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build())) //EMAIL
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.background_navview)
                        .build(),
                RC_SIGN_IN));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                startWelcomeActivity();
            }
        }
    }

    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    private void createUserInFirestore(){

        if (this.getCurrentUser() != null){

            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, username);
        }
    }

    private void startWelcomeActivity(){

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("user/" + currentFirebaseUser.getUid());

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("username", currentFirebaseUser.getDisplayName());
        dataToSave.put("email", currentFirebaseUser.getEmail());
        mDocRef.set(dataToSave, SetOptions.merge());

        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        mPrefs.edit().putString("username", currentFirebaseUser.getDisplayName()).apply();
        mPrefs.edit().putString("email", currentFirebaseUser.getEmail()).apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
