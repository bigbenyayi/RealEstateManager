package com.openclassrooms.realestatemanager.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.openclassrooms.realestatemanager.Models.SimpleRVAdapter;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    Button chooseMainPicButton;
    TextView uploadFileTV;
    ImageView mainPicImageView;
    Uri mainImageUri;
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
    int numberMaxPOI = 0;
    ArrayList<String> interestsArray = new ArrayList<>();
    RecyclerView mRecyclerViewPOI;

    private CollectionReference mCollectionReference;

    int intId;
    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        fetchingAllViewFromXML();
        setActionDoneOnEditTexts();
        setInitialViews();
        setButtonAction();
        setImagePickers();
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
                mCollectionReference = FirebaseFirestore.getInstance().collection("house");

                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String id = (String) documentSnapshot.get("id");
                        int idInt = Integer.parseInt(id);
                        intId = 0;
                        if (idInt > intId) {
                            intId = Integer.parseInt(id);
                        }


                    }
                });
                // SAVE EVERYTHING TO FIRESTOOOORE HERE
                DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("house").document();

                Map<String, Object> dataToSave = new HashMap<>();

                dataToSave.put("city", cityET.getText().toString());
                dataToSave.put("description", descriptionET.getText().toString());
                dataToSave.put("id", "6");
                dataToSave.put("location", locationET.getText().toString());
                dataToSave.put("numberOfBathrooms", String.valueOf(bathroomsNP.getValue()));
                dataToSave.put("numberOfBedrooms", String.valueOf(bedroomsNP.getValue()));
                dataToSave.put("numberOfRooms", String.valueOf(roomsNP.getValue()));

                dataToSave.put("pointOfInterest", interestsArray.addAll());
                dataToSave.put("price", priceET.getText().toString());
                dataToSave.put("surface", surfaceET.getText().toString());
                dataToSave.put("type", typeET.getText().toString());
                dataToSave.put("mainPicture", mainImageUri + "");


                mDocRef.set(dataToSave, SetOptions.merge());


                //onMarket
                //mainPic
                //sidePictures
                //sidePicturesDescription
                //realtor

                //uploadFile();


                finish();

            } else {
                n++;
                mPrefs.edit().putInt("addNumber", n).apply();
                setCorrectTVs();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
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
        uploadFileTV.setVisibility(View.VISIBLE);
        mainPicImageView.setVisibility(View.VISIBLE);
        chooseMainPicButton.setVisibility(View.VISIBLE);


        nextButton.setText("Next (1/3)");

        //Room numberpickers

        bathroomsNP.setMinValue(0);
        bathroomsNP.setMaxValue(20);

        bedroomsNP.setMinValue(0);
        bedroomsNP.setMaxValue(25);

        roomsNP.setMinValue(0);
        roomsNP.setMaxValue(40);

    }

    @SuppressLint("ClickableViewAccessibility")
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
        chooseMainPicButton = findViewById(R.id.choosePictureButton);
        uploadFileTV = findViewById(R.id.mainPictureTV);
        mainPicImageView = findViewById(R.id.mainPicIV);

        //Details
        descriptionTV = findViewById(R.id.descriptionTV);
        descriptionET = findViewById(R.id.descriptionET);
        locationTV = findViewById(R.id.locationTV);
        locationET = findViewById(R.id.locationET);

        //Cara
        pointOfInterestTV = findViewById(R.id.pointsOfInterestTV);
        pointOfInterestET = findViewById(R.id.pointsOfInterestET);
        mRecyclerViewPOI = findViewById(R.id.recyclerViewPOI);

        mRecyclerViewPOI.setLayoutManager(new LinearLayoutManager(this));

        pointOfInterestET.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (pointOfInterestET.getRight() - pointOfInterestET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    numberMaxPOI++;

                    if (numberMaxPOI <= 5) {
                        interestsArray.add(pointOfInterestET.getText().toString());
                        pointOfInterestET.setText("");
                    } else {
                        Toast.makeText(this, "You have reached the limit", Toast.LENGTH_SHORT).show();
                        pointOfInterestET.setText("");
                    }
                    mRecyclerViewPOI.setAdapter(new SimpleRVAdapter(interestsArray));
                    Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
                    return true;
                }

            }

            return false;
        });

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

        pointOfInterestET.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                numberMaxPOI++;

                if (numberMaxPOI <= 5) {
                    interestsArray.add(pointOfInterestET.getText().toString());
                    pointOfInterestET.setText("");
                } else {
                    Toast.makeText(this, "You have reached the limit", Toast.LENGTH_SHORT).show();
                    pointOfInterestET.setText("");
                }
                mRecyclerViewPOI.setAdapter(new SimpleRVAdapter(interestsArray));
                Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
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
            uploadFileTV.setVisibility(View.VISIBLE);
            mainPicImageView.setVisibility(View.VISIBLE);
            chooseMainPicButton.setVisibility(View.VISIBLE);

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
            uploadFileTV.setVisibility(View.INVISIBLE);
            chooseMainPicButton.setVisibility(View.INVISIBLE);
            mainPicImageView.setVisibility(View.INVISIBLE);

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
            uploadFileTV.setVisibility(View.INVISIBLE);
            chooseMainPicButton.setVisibility(View.INVISIBLE);
            mainPicImageView.setVisibility(View.INVISIBLE);

        }
    }

    private void setImagePickers() {
        chooseMainPicButton.setOnClickListener(v -> {
            Intent myIntent = new Intent();
            myIntent.setType("image/*");
            myIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(myIntent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mainImageUri = data.getData();

            Picasso.get().load(mainImageUri).into(mainPicImageView);
            setCorrectTVs();
        }
    }
}


