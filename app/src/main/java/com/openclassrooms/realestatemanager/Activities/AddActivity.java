package com.openclassrooms.realestatemanager.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openclassrooms.realestatemanager.Models.DatabaseHouseItem;
import com.openclassrooms.realestatemanager.Models.HorizontalRecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.MyHorizontalAdapter;
import com.openclassrooms.realestatemanager.Models.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.Models.Utils;
import com.openclassrooms.realestatemanager.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    RelativeLayout basicRL;
    RelativeLayout detailRL;
    RelativeLayout charaRL;

    private RealEstateManagerDatabase database;

    TextView title;
    //Basics
    EditText typeET;
    EditText priceET;
    EditText cityET;

    Button chooseMainPicButton;
    ImageView mainPicImageView;
    Uri mainImageUri;
    //Details
    EditText descriptionET;
    EditText locationET;
    Button addAPictureButton;
    ImageView addAPictureIV;
    EditText pictureDescriptionET;
    Button addButton;
    RecyclerView horizontalRecyclerViewAdd;
    //Cara
    EditText surfaceET;
    CheckBox restaurantCB;
    CheckBox schoolCB;
    CheckBox parkCB;
    //Buttons
    Button backButton;
    Button nextButton;
    //Number pickers
    NumberPicker bathroomsNP;
    NumberPicker bedroomsNP;
    NumberPicker roomsNP;
    int numberMaxPOI = 0;
    String url;
    String urlAdd;
    ProgressBar progressBar;
    List<String> interestsArray = new ArrayList<>();
    ArrayList<HorizontalRecyclerViewItem> listOfPicturesAndDesc = new ArrayList<>();
    List<String> arrayOfPics = new ArrayList<>();
    List<String> arrayOfDesc = new ArrayList<>();
    List<String> arrayOfDescOffline = new ArrayList<>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private MyHorizontalAdapter adapter;

    List<String> localPathsArray = new ArrayList();
    String dataPathForMainPicture;

    int finalJ;
    int finalI;


    private CollectionReference mCollectionReference;

    int intId;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST_ADD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        fetchingAllViewFromXML();
        setActionDoneOnEditTexts();
        setInitialViews();
        setButtonAction();
        setImagePickers();

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerViewAdd.setLayoutManager(horizontalLayoutManager);

        addAPictureButton.setOnClickListener(v -> {
            Intent myIntent = new Intent();
            myIntent.setType("image/*");
            myIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(myIntent, PICK_IMAGE_REQUEST_ADD);
        });

        addButton.setOnClickListener(v -> {
            addAPictureIV.getDrawable();
            pictureDescriptionET.getText().toString();

            addAPictureIV.setDrawingCacheEnabled(true);
            addAPictureIV.buildDrawingCache();
            Bitmap bitmap = addAPictureIV.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            addAPictureIV.setDrawingCacheEnabled(false);
            byte[] data = baos.toByteArray();

            String path = "firememes/" + UUID.randomUUID() + ".png";
            StorageReference firememeRef = storage.getReference(path);
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("text", descriptionET.getText().toString())
                    .build();

            addAPictureButton.setEnabled(false);
            addButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            UploadTask uploadTask = firememeRef.putBytes(data, metadata);
            uploadTask.addOnSuccessListener(AddActivity.this, taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {

                urlAdd = uri.toString();
                listOfPicturesAndDesc.add(new HorizontalRecyclerViewItem(urlAdd, pictureDescriptionET.getText().toString()));

                addAPictureIV.setImageResource(0);
                pictureDescriptionET.setText("");

                localPathsArray.add(urlAdd);

                adapter = new MyHorizontalAdapter(AddActivity.this, listOfPicturesAndDesc);
                horizontalRecyclerViewAdd.setAdapter(adapter);

                addAPictureButton.setEnabled(true);
                addButton.setEnabled(false);

                progressBar.setVisibility(View.INVISIBLE);

            }));
        });
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
            ProgressDialog dialog;
            int n = mPrefs.getInt("addNumber", 0);
            if (n == 3 && localPathsArray.size() > 0) {

                dialog = ProgressDialog.show(AddActivity.this, "Editing house listing",
                        "Loading... Please wait", true);

                CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");
                mCollectionReference = FirebaseFirestore.getInstance().collection("house");

                notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {
                    intId = 0;

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String id = (String) documentSnapshot.get("id");
                        int idInt = Integer.parseInt(Objects.requireNonNull(id));
                        if (idInt >= intId) {
                            intId = Integer.parseInt(id);
                        }
                    }
                    mainPicImageView.setDrawingCacheEnabled(true);
                    mainPicImageView.buildDrawingCache();
                    Bitmap bitmap = mainPicImageView.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    mainPicImageView.setDrawingCacheEnabled(false);
                    byte[] data = baos.toByteArray();

                    String path = "firememes/" + UUID.randomUUID() + ".png";
                    StorageReference firememeRef = storage.getReference(path);
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setCustomMetadata("text", typeET.getText().toString())
                            .build();

                    UploadTask uploadTask = firememeRef.putBytes(data, metadata);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {

                            // SAVE EVERYTHING TO FIRESTOOOORE HERE

                            url = uri.toString();
                            DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("house").document();

                            Map<String, Object> dataToSave = new HashMap<>();

                            dataToSave.put("city", cityET.getText().toString());
                            dataToSave.put("description", descriptionET.getText().toString());
                            dataToSave.put("id", String.valueOf(intId + 1));
                            dataToSave.put("location", locationET.getText().toString());
                            dataToSave.put("numberOfBathrooms", String.valueOf(bathroomsNP.getValue()));
                            dataToSave.put("numberOfBedrooms", String.valueOf(bedroomsNP.getValue()));
                            dataToSave.put("numberOfRooms", String.valueOf(roomsNP.getValue()));
                            dataToSave.put("mainPicture", url);
                            if (adapter.getUpdatedlist().size() > 0) {

//                        if (localPathsArray.size() > 0) {
//                            Picasso.get().load(adapter.getUpdatedlist().get(0).getPictureUrl()).into(addAPictureIV);
//                        }
                                for (int j = 0; j < adapter.getUpdatedlist().size(); j++) {

                                    arrayOfPics.add(adapter.getUpdatedlist().get(j).getPictureUrl());
                                    arrayOfDesc.add(adapter.getUpdatedlist().get(j).getRoom());

                                }

                                dataToSave.put("pictures", arrayOfPics);
                                dataToSave.put("rooms", arrayOfDesc);
                            }

                            interestsArray.clear();
                            if (schoolCB.isChecked()) {
                                interestsArray.add("School");
                            }
                            if (restaurantCB.isChecked()) {
                                interestsArray.add("Restaurant");
                            }
                            if (parkCB.isChecked()) {
                                interestsArray.add("Park");
                            }
                            dataToSave.put("pointOfInterest", interestsArray);

                            dataToSave.put("price", priceET.getText().toString().replace(",", ""));
                            dataToSave.put("surface", surfaceET.getText().toString());
                            dataToSave.put("type", typeET.getText().toString());
                            dataToSave.put("realtor", Objects.requireNonNull(mPrefs.getString("username", "Realtor")));

                            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = new Date();

                            dataToSave.put("onMarket", dateFormat.format(date));

                            mDocRef.set(dataToSave, SetOptions.merge());

                            if (adapter.getUpdatedlist().size() == 0) {
                                dialog.dismiss();
                                Toast.makeText(this, "Please add at least one additional picture", Toast.LENGTH_SHORT).show();

                            } else if (priceET.getText().toString().equals("")) {
                                dialog.dismiss();
                                Toast.makeText(this, "Please put in a price", Toast.LENGTH_SHORT).show();
                            }
                            Intent myIntent = new Intent(AddActivity.this, MainActivity.class);
                            startActivity(myIntent);
                            finish();

                        });
                    });

                });
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

        detailRL.setVisibility(View.INVISIBLE);
        charaRL.setVisibility(View.INVISIBLE);
        basicRL.setVisibility(View.VISIBLE);

        nextButton.setText("Next (1/3)");

        //Room numberpickers

        bathroomsNP.setMinValue(0);
        bathroomsNP.setMaxValue(20);

        bedroomsNP.setMinValue(0);
        bedroomsNP.setMaxValue(25);

        roomsNP.setMinValue(0);
        roomsNP.setMaxValue(40);

        addButton.setEnabled(false);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void fetchingAllViewFromXML() {

        //Fetching all TV and ET
        title = findViewById(R.id.title);
        basicRL = findViewById(R.id.basicRL);
        detailRL = findViewById(R.id.detailRL);
        charaRL = findViewById(R.id.charaRL);

        //Basics
        typeET = findViewById(R.id.houseTypeET);
        priceET = findViewById(R.id.priceET);
        cityET = findViewById(R.id.cityET);
        chooseMainPicButton = findViewById(R.id.choosePictureButton);
        mainPicImageView = findViewById(R.id.mainPicIV);
        progressBar = findViewById(R.id.progressBar);


        //Details
        descriptionET = findViewById(R.id.descriptionET);
        locationET = findViewById(R.id.locationET);
        addAPictureButton = findViewById(R.id.addAPictureButton);
        addAPictureIV = findViewById(R.id.sidePicturesIV);
        pictureDescriptionET = findViewById(R.id.photoDescriptionET);
        addButton = findViewById(R.id.addButton);
        horizontalRecyclerViewAdd = findViewById(R.id.horizontalRecyclerViewAdd);

        //Cara
        restaurantCB = findViewById(R.id.restaurantCB);
        schoolCB = findViewById(R.id.schoolCB);
        parkCB = findViewById(R.id.parkCB);

        surfaceET = findViewById(R.id.surfaceET);

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

        priceET.addTextChangedListener(new TextWatcher() {

            boolean isManualChange = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isManualChange) {
                    isManualChange = false;
                    return;
                }
                try {
                    String value = s.toString().replace(",", "");
                    String reverseValue = new StringBuilder(value).reverse()
                            .toString();
                    StringBuilder finalValue = new StringBuilder();
                    for (int i = 1; i <= reverseValue.length(); i++) {
                        char val = reverseValue.charAt(i - 1);
                        finalValue.append(val);
                        if (i % 3 == 0 && i != reverseValue.length() && i > 0) {
                            finalValue.append(",");
                        }
                    }
                    isManualChange = true;
                    priceET.setText(finalValue.reverse());
                    priceET.setSelection(finalValue.length());
                } catch (Exception e) {
                    // Do nothing since not a number
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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
        progressBar.setVisibility(View.INVISIBLE);
        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        if (mPrefs.getInt("addNumber", 1) == 1) {
            backButton.setText("Cancel");
            nextButton.setText("Next (1/3)");
            title.setText("Basics");


            basicRL.setVisibility(View.VISIBLE);
            charaRL.setVisibility(View.INVISIBLE);
            detailRL.setVisibility(View.INVISIBLE);


        } else if (mPrefs.getInt("addNumber", 1) == 2) {
            backButton.setText("Back");
            nextButton.setText("Next (2/3)");

            title.setText("Details");

            basicRL.setVisibility(View.INVISIBLE);
            charaRL.setVisibility(View.INVISIBLE);
            detailRL.setVisibility(View.VISIBLE);

        } else if (mPrefs.getInt("addNumber", 1) == 3) {
            nextButton.setText("Create listing");
            backButton.setText("Back");

            title.setText("Characteristics");

            basicRL.setVisibility(View.INVISIBLE);
            charaRL.setVisibility(View.VISIBLE);
            detailRL.setVisibility(View.INVISIBLE);

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
            dataPathForMainPicture = mainImageUri.toString();
            Picasso.get().load(mainImageUri).into(mainPicImageView);
            setCorrectTVs();
        }
        if (requestCode == PICK_IMAGE_REQUEST_ADD && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).into(addAPictureIV);
            urlAdd = data.getData().toString();
            addButton.setEnabled(true);
            setCorrectTVs();
        }
    }

    public void adapterSendsList(int position) {
        listOfPicturesAndDesc = adapter.getUpdatedlist();
        listOfPicturesAndDesc.remove(position);
        adapter.notifyDataSetChanged();
    }
}


