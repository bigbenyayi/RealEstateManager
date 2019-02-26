package com.openclassrooms.realestatemanager.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.openclassrooms.realestatemanager.R;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    TextView title;
    //Basics
    TextView typeTV;
    EditText typeET;
    TextView priceTV;
    EditText priceET;
    TextView cityTV;
    EditText cityET;
    //Details
    TextView descriptionTV;
    EditText descriptionET;
    TextView locationTV;
    EditText locationET;
    //Cara
    TextView pointOfInterestTV;
    EditText pointOfInterestET;
    TextView surfaceTV;
    EditText surfaceET;
    TextView bathroomTV;
    TextView bedroomTV;
    TextView roomTV;
    //Buttons
    Button backButton;
    Button nextButton;
    //Number pickers
    NumberPicker bathroomsNP;
    NumberPicker bedroomsNP;
    NumberPicker roomsNP;

    int intId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        fetchingAllViewFromXML();
        setActionDoneOnEditTexts();
        setInitialViews();
        setButtonAction();
    }

    private void setButtonAction() {
        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);

        backButton.setOnClickListener(v -> {
            int n = mPrefs.getInt("addNumber", 1);
            if (n == 1) {
                backButton.setText("Cancel");
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Are you sure? Your progress will be lost");
                builder1.setCancelable(true);

                builder1.setPositiveButton("Yes", (dialog, id1) -> finish());
                builder1.setNegativeButton("Cancel", (dialog, id1) -> dialog.cancel());
                AlertDialog alert11 = builder1.create();
                alert11.show();

            } else {
                n--;
                mPrefs.edit().putInt("addNumber", n).apply();
                setCorrectTVs();
            }
        });

        nextButton.setOnClickListener(v -> {
            int n = mPrefs.getInt("addNumber", 0);
            if (n == 3) {
                CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");
                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String id = (String) documentSnapshot.get("id");
                        int idInt = Integer.parseInt(id);
                        intId = 0;
                        if (idInt > intId){
                            intId = Integer.parseInt(id);
                        }


                    }
                });
                // SAVE EVERYTHING TO FIRESTOOOORE HERE
                DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("house").document();

                Map<String, Object> dataToSave = new HashMap<>();
                dataToSave.put("city", cityET.getText().toString());
                dataToSave.put("description", descriptionET.getText().toString());
                dataToSave.put("id", intId + 1);
                dataToSave.put("location", locationET.getText().toString());
                dataToSave.put("numberOfBathrooms", String.valueOf(bathroomsNP.getValue()));
                dataToSave.put("numberOfBedrooms", String.valueOf(bedroomsNP.getValue()));
                dataToSave.put("numberOfRooms", String.valueOf(roomsNP.getValue()));

                dataToSave.put("pointOfInterest", pointOfInterestET.getText().toString());
                dataToSave.put("price", priceET.getText().toString());
                dataToSave.put("surface", surfaceET.getText().toString());
                dataToSave.put("type", typeET.getText().toString());

                //onMarket
                //mainPic
                //sidePictures
                //sidePicturesDescription
                //realtor

                mDocRef.set(dataToSave, SetOptions.merge());
                finish();

            } else {
                n++;
                mPrefs.edit().putInt("addNumber", n).apply();
                setCorrectTVs();
            }
        });
    }

    private void setInitialViews() {
        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);


        mPrefs.edit().putInt("addNumber", 1).apply();

        backButton.setText("Cancel");

        title.setText("Basics");

        descriptionTV.setVisibility(View.INVISIBLE);
        descriptionET.setVisibility(View.INVISIBLE);
        locationTV.setVisibility(View.INVISIBLE);
        locationET.setVisibility(View.INVISIBLE);
        pointOfInterestTV.setVisibility(View.INVISIBLE);
        pointOfInterestET.setVisibility(View.INVISIBLE);
        surfaceTV.setVisibility(View.INVISIBLE);
        surfaceET.setVisibility(View.INVISIBLE);
        bathroomTV.setVisibility(View.INVISIBLE);
        bedroomTV.setVisibility(View.INVISIBLE);
        roomTV.setVisibility(View.INVISIBLE);
        bathroomsNP.setVisibility(View.INVISIBLE);
        bedroomsNP.setVisibility(View.INVISIBLE);
        roomsNP.setVisibility(View.INVISIBLE);

        typeTV.setVisibility(View.VISIBLE);
        typeET.setVisibility(View.VISIBLE);
        priceTV.setVisibility(View.VISIBLE);
        priceET.setVisibility(View.VISIBLE);
        cityTV.setVisibility(View.VISIBLE);
        cityET.setVisibility(View.VISIBLE);

        nextButton.setText("Next (1/3)");

        //Room numberpickers

        bathroomsNP.setMinValue(0);
        bathroomsNP.setMaxValue(20);

        bedroomsNP.setMinValue(0);
        bedroomsNP.setMaxValue(25);

        roomsNP.setMinValue(0);
        roomsNP.setMaxValue(40);

        bathroomsNP.setOnValueChangedListener(onValueChangeListener);
        bedroomsNP.setOnValueChangedListener(onValueChangeListener);
        roomsNP.setOnValueChangedListener(onValueChangeListener);

    }

    private void fetchingAllViewFromXML() {

        //Fetching all TV and ET
        title = findViewById(R.id.title);

        //Basics
        typeTV = findViewById(R.id.houseTypeTV);
        typeET = findViewById(R.id.houseTypeET);
        priceTV = findViewById(R.id.priceTV);
        priceET = findViewById(R.id.priceET);
        cityTV = findViewById(R.id.cityTV);
        cityET = findViewById(R.id.cityET);

        //Details
        descriptionTV = findViewById(R.id.descriptionTV);
        descriptionET = findViewById(R.id.descriptionET);
        locationTV = findViewById(R.id.locationTV);
        locationET = findViewById(R.id.locationET);

        //Cara
        pointOfInterestTV = findViewById(R.id.pointsOfInterestTV);
        pointOfInterestET = findViewById(R.id.pointsOfInterestET);
        surfaceTV = findViewById(R.id.surfaceTV);
        surfaceET = findViewById(R.id.surfaceET);
        bathroomTV = findViewById(R.id.bathroomsTV);
        bedroomTV = findViewById(R.id.bedroomsTV);
        roomTV = findViewById(R.id.roomsTV);

        //Buttons
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);

        //Number pickers
        bathroomsNP = findViewById(R.id.bathroomsET);
        bedroomsNP = findViewById(R.id.bedroomsET);
        roomsNP = findViewById(R.id.roomsET);
    }

    private void setActionDoneOnEditTexts() {
        typeET.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                priceET.requestFocus();

            }
            return true;
        });

        priceET.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                cityET.requestFocus();
            }
            return true;
        });

        cityET.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
            return true;
        });

        descriptionET.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                locationET.requestFocus();
            }
            return true;
        });
    }

    private void setCorrectTVs() {
        TextView title = findViewById(R.id.title);
        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        if (mPrefs.getInt("addNumber", 1) == 1) {
            backButton.setText("Cancel");
            nextButton.setText("Next (1/3)");
            title.setText("Basics");

            descriptionTV.setVisibility(View.INVISIBLE);
            descriptionET.setVisibility(View.INVISIBLE);
            locationTV.setVisibility(View.INVISIBLE);
            locationET.setVisibility(View.INVISIBLE);
            pointOfInterestTV.setVisibility(View.INVISIBLE);
            pointOfInterestET.setVisibility(View.INVISIBLE);
            surfaceTV.setVisibility(View.INVISIBLE);
            surfaceET.setVisibility(View.INVISIBLE);
            bathroomTV.setVisibility(View.INVISIBLE);
            bedroomTV.setVisibility(View.INVISIBLE);
            roomTV.setVisibility(View.INVISIBLE);
            bathroomsNP.setVisibility(View.INVISIBLE);
            bedroomsNP.setVisibility(View.INVISIBLE);
            roomsNP.setVisibility(View.INVISIBLE);


            typeTV.setVisibility(View.VISIBLE);
            typeET.setVisibility(View.VISIBLE);
            priceTV.setVisibility(View.VISIBLE);
            priceET.setVisibility(View.VISIBLE);
            cityTV.setVisibility(View.VISIBLE);
            cityET.setVisibility(View.VISIBLE);

        } else if (mPrefs.getInt("addNumber", 1) == 2) {
            backButton.setText("Back");
            nextButton.setText("Next (2/3)");

            title.setText("Details");
            descriptionTV.setVisibility(View.VISIBLE);
            descriptionET.setVisibility(View.VISIBLE);
            locationTV.setVisibility(View.VISIBLE);
            locationET.setVisibility(View.VISIBLE);
            pointOfInterestTV.setVisibility(View.INVISIBLE);
            pointOfInterestET.setVisibility(View.INVISIBLE);
            surfaceTV.setVisibility(View.INVISIBLE);
            surfaceET.setVisibility(View.INVISIBLE);
            bathroomTV.setVisibility(View.INVISIBLE);
            bedroomTV.setVisibility(View.INVISIBLE);
            roomTV.setVisibility(View.INVISIBLE);
            typeTV.setVisibility(View.INVISIBLE);
            typeET.setVisibility(View.INVISIBLE);
            priceTV.setVisibility(View.INVISIBLE);
            priceET.setVisibility(View.INVISIBLE);
            cityTV.setVisibility(View.INVISIBLE);
            cityET.setVisibility(View.INVISIBLE);
            bathroomsNP.setVisibility(View.INVISIBLE);
            bedroomsNP.setVisibility(View.INVISIBLE);
            roomsNP.setVisibility(View.INVISIBLE);
        } else if (mPrefs.getInt("addNumber", 1) == 3) {
            nextButton.setText("Create listing");
            backButton.setText("Back");

            title.setText("Characteristics");
            descriptionTV.setVisibility(View.INVISIBLE);
            descriptionET.setVisibility(View.INVISIBLE);
            locationTV.setVisibility(View.INVISIBLE);
            locationET.setVisibility(View.INVISIBLE);
            pointOfInterestTV.setVisibility(View.VISIBLE);
            pointOfInterestET.setVisibility(View.VISIBLE);
            surfaceTV.setVisibility(View.VISIBLE);
            surfaceET.setVisibility(View.VISIBLE);
            bathroomTV.setVisibility(View.VISIBLE);
            bedroomTV.setVisibility(View.VISIBLE);
            roomTV.setVisibility(View.VISIBLE);
            typeTV.setVisibility(View.INVISIBLE);
            typeET.setVisibility(View.INVISIBLE);
            priceTV.setVisibility(View.INVISIBLE);
            priceET.setVisibility(View.INVISIBLE);
            cityTV.setVisibility(View.INVISIBLE);
            cityET.setVisibility(View.INVISIBLE);
            bathroomsNP.setVisibility(View.VISIBLE);
            bedroomsNP.setVisibility(View.VISIBLE);
            roomsNP.setVisibility(View.VISIBLE);
        }
    }

    NumberPicker.OnValueChangeListener onValueChangeListener =
            (numberPicker, i, i1) -> Toast.makeText(AddActivity.this, "selected number " + numberPicker.getValue(), Toast.LENGTH_SHORT).show();
}


