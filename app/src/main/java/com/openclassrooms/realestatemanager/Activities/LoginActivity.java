package com.openclassrooms.realestatemanager.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);


        if (mPrefs.getString("username", null) != null){
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        }

        EditText usernameET = findViewById(R.id.usernameET);
        EditText passwordET = findViewById(R.id.passwordET);
        EditText emailET = findViewById(R.id.emailET);
        TextView emailTV = findViewById(R.id.emailTV);
        Button createButton = findViewById(R.id.createAccountButton);
        Button loginButton = findViewById(R.id.loginButton);
        Button validateButton = findViewById(R.id.validateButton);

        emailET.setVisibility(View.INVISIBLE);
        emailTV.setVisibility(View.INVISIBLE);

        createButton.setBackground(null);
        createButton.setTextColor(Color.parseColor("#696969"));

        createButton.setOnClickListener(v -> {
            loginButton.setBackground(null);
            loginButton.setTextColor(Color.parseColor("#696969"));

            createButton.setBackground(getDrawable(R.color.colorAccent));
            createButton.setTextColor(Color.parseColor("#000000"));

            emailET.setVisibility(View.VISIBLE);
            emailTV.setVisibility(View.VISIBLE);

            newAccount = true;
        });

        loginButton.setOnClickListener(v -> {
            createButton.setBackground(null);
            createButton.setTextColor(Color.parseColor("#696969"));

            loginButton.setBackground(getDrawable(R.color.colorAccent));
            loginButton.setTextColor(Color.parseColor("#000000"));

            emailET.setVisibility(View.INVISIBLE);
            emailTV.setVisibility(View.INVISIBLE);

            newAccount = false;
        });

        validateButton.setOnClickListener(v -> {
            if (newAccount) {
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
                CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");
                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (usernameET.getText().toString().equals(documentSnapshot.get("username"))) {

                            if (passwordET.getText().toString().equals(documentSnapshot.get("password"))) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            }

                            mPrefs.edit().putString("username", usernameET.getText().toString()).apply();
                            mPrefs.edit().putString("email", emailET.getText().toString()).apply();

                            Intent myIntent = new Intent(this, MainActivity.class);
                            startActivity(myIntent);
                        }
                    }

                });
            }
        });


    }


}
