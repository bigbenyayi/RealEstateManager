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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.openclassrooms.realestatemanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Boolean newAccount = false;
    Boolean emailExists = false;
    String password;
    Boolean forgotYourPasswordBool = false;


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

        EditText usernameET = findViewById(R.id.usernameET);
        TextView usernameTV = findViewById(R.id.usernameTV);
        EditText passwordET = findViewById(R.id.passwordET);
        TextView passwordTV = findViewById(R.id.passwordTV);
        EditText emailET = findViewById(R.id.emailET);
        TextView emailTV = findViewById(R.id.emailTV);
        Button createButton = findViewById(R.id.createAccountButton);
        Button loginButton = findViewById(R.id.loginButton);
        Button validateButton = findViewById(R.id.validateButton);
        TextView forgotYourPassword = findViewById(R.id.forgotYourPassword);

        forgotYourPassword.setOnClickListener(v -> {
            forgotYourPasswordBool = true;
            passwordET.setVisibility(View.INVISIBLE);
            usernameET.setVisibility(View.INVISIBLE);
            passwordTV.setVisibility(View.INVISIBLE);
            usernameTV.setVisibility(View.INVISIBLE);

            emailET.setVisibility(View.VISIBLE);
            emailTV.setVisibility(View.VISIBLE);


        });

        emailET.setVisibility(View.VISIBLE);
        emailTV.setVisibility(View.VISIBLE);


        validateButton.setText("Login");

        createButton.setBackground(null);
        createButton.setTextColor(Color.parseColor("#696969"));

        createButton.setOnClickListener(v -> {
            forgotYourPasswordBool = false;

            loginButton.setBackground(null);
            loginButton.setTextColor(Color.parseColor("#696969"));

            emailET.setText("");
            passwordET.setText("");
            usernameET.setText("");

            createButton.setBackground(getDrawable(R.color.colorAccent));
            createButton.setTextColor(Color.parseColor("#000000"));

            emailET.setVisibility(View.VISIBLE);
            emailTV.setVisibility(View.VISIBLE);
            passwordTV.setVisibility(View.VISIBLE);
            passwordET.setVisibility(View.VISIBLE);
            usernameET.setVisibility(View.VISIBLE);
            usernameTV.setVisibility(View.VISIBLE);

            forgotYourPassword.setVisibility(View.INVISIBLE);

            validateButton.setText("Create account");

            newAccount = true;
        });

        loginButton.setOnClickListener(v -> {
            createButton.setBackground(null);
            createButton.setTextColor(Color.parseColor("#696969"));
            forgotYourPasswordBool = false;

            emailET.setText("");
            passwordET.setText("");
            usernameET.setText("");

            loginButton.setBackground(getDrawable(R.color.colorAccent));
            loginButton.setTextColor(Color.parseColor("#000000"));

            emailET.setVisibility(View.INVISIBLE);
            emailTV.setVisibility(View.INVISIBLE);
            passwordTV.setVisibility(View.VISIBLE);
            passwordET.setVisibility(View.VISIBLE);
            usernameET.setVisibility(View.VISIBLE);
            usernameTV.setVisibility(View.VISIBLE);

            forgotYourPassword.setVisibility(View.VISIBLE);

            validateButton.setText("Login");

            newAccount = false;
        });

        validateButton.setOnClickListener(v -> {
            if (newAccount) {
                CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("user");
                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.get("email").equals(emailET.getText().toString())) {
                            emailExists = true;
                        }
                    }
                    if (!emailExists) {
                        //Create new field in Firestore database with email usernam and password
                        DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("user").document();

                        Map<String, String> dataToSave = new HashMap<>();

                        dataToSave.put("username", usernameET.getText().toString());
                        dataToSave.put("password", passwordET.getText().toString());
                        dataToSave.put("email", emailET.getText().toString());

                        mDocRef.set(dataToSave, SetOptions.merge());

                        mPrefs.edit().putString("username", usernameET.getText().toString()).apply();
                        mPrefs.edit().putString("email", emailET.getText().toString()).apply();

                        Intent myIntent = new Intent(this, MainActivity.class);
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(this, "This email has already been used", Toast.LENGTH_SHORT).show();
                        emailET.setText("");
                        passwordET.setText("");
                        usernameET.setText("");
                    }
                });
            } else {
                CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("user");
                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (usernameET.getText().toString().equals(documentSnapshot.get("username"))) {

                            if (passwordET.getText().toString().equals(documentSnapshot.get("password"))) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                                mPrefs.edit().putString("username", usernameET.getText().toString()).apply();
                                mPrefs.edit().putString("email", emailET.getText().toString()).apply();

                                Intent myIntent = new Intent(this, MainActivity.class);
                                startActivity(myIntent);
                            }
                        }
                    }
                });
            }
            if (forgotYourPasswordBool) {

                CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("user");
                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (emailET.getText().toString().equals(documentSnapshot.get("email"))) {

                            password = (String) documentSnapshot.get("password");
                        }
                    }

                    String[] TO = {"corbenbenjamin@gmail.com"};
                    String[] CC = {emailET.getText().toString()};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");


                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                    emailIntent.putExtra(Intent.EXTRA_CC, CC);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Password");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Your password is: " + password);

                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        finish();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(this,
                                "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
