package com.openclassrooms.realestatemanager.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.openclassrooms.realestatemanager.Models.RecyclerWith3Items;
import com.openclassrooms.realestatemanager.Models.SimpleRVAdapter;
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
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    RelativeLayout basicRL;
    RelativeLayout detailRL;
    RelativeLayout charaRL;

    private RealEstateManagerDatabase database;

    TextView title;
    //Basics
    TextView typeTV;
    EditText typeET;
    TextView priceTV;
    EditText priceET;
    TextView cityTV;
    EditText cityET;
    ImageView typeIcon;
    ImageView priceIcon;
    ImageView cityIcon;
    Button chooseMainPicButton;
    TextView uploadFileTV;
    ImageView mainPicImageView;
    Uri mainImageUri;
    //Details
    TextView descriptionTV;
    EditText descriptionET;
    TextView locationTV;
    EditText locationET;
    ImageView descriptionIcon;
    ImageView locationIcon;
    TextView sidePicturesTV;
    Button addAPictureButton;
    ImageView addAPictureIV;
    EditText pictureDescriptionET;
    Button addButton;
    RecyclerView horizontalRecyclerViewAdd;
    //Cara
    TextView pointOfInterestTV;
    EditText pointOfInterestET;
    TextView surfaceTV;
    EditText surfaceET;
    TextView bathroomTV;
    TextView bedroomTV;
    TextView roomTV;
    ImageView surfaceIcon;
    ImageView bedroomsIcon;
    ImageView bathroomsIcon;
    ImageView roomsIcon;
    ImageView interestsIcon;
    ProgressBar progressBar;
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
    ArrayList<String> interestsArray = new ArrayList<>();
    ArrayList<HorizontalRecyclerViewItem> listOfPicturesAndDesc = new ArrayList<>();
    ArrayList<String> arrayOfPics = new ArrayList<>();
    ArrayList<String> arrayOfDesc = new ArrayList<>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private MyHorizontalAdapter adapter;


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

                adapter = new MyHorizontalAdapter(AddActivity.this, listOfPicturesAndDesc);
                horizontalRecyclerViewAdd.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                addAPictureButton.setEnabled(true);
                addButton.setEnabled(true);
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
                            Toast.makeText(AddActivity.this, url, Toast.LENGTH_SHORT).show();
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
                            for (int i = 0; i < adapter.getUpdatedlist().size(); i++) {
                                arrayOfPics.add(adapter.getUpdatedlist().get(i).getPictureUrl());
                                arrayOfDesc.add(adapter.getUpdatedlist().get(i).getRoom());
                            }
                            dataToSave.put("pictures", arrayOfPics);
                            dataToSave.put("rooms", arrayOfDesc);

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
                            dataToSave.put("realtor", mPrefs.getString("username", "Realtor"));

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = new Date();

                            dataToSave.put("onMarket", dateFormat.format(date));


                            mDocRef.set(dataToSave, SetOptions.merge());

                            ////////////////////////// SENDING DATA TO SQL DATABASE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

                            database = Room.databaseBuilder(this,
                                    RealEstateManagerDatabase.class, "MyDatabase.db")
                                    .allowMainThreadQueries()
                                    .build();

                            int biggestId = 0;

                            List<DatabaseHouseItem> listOfItems = database.itemDao().getItems();
                            if (listOfItems != null) {
                                for (int i = 0; i < listOfItems.size(); i++) {
                                    String id = listOfItems.get(i).getId();
                                    int idInt = Integer.parseInt(id);
                                    biggestId = 0;
                                    if (idInt > biggestId) {
                                        biggestId = Integer.parseInt(id);
                                    }
                                }
                            }

                            database.itemDao().insertItem(new DatabaseHouseItem(descriptionET.getText().toString(), surfaceET.getText().toString(),
                                    String.valueOf(biggestId + 1), String.valueOf(roomsNP.getValue()), String.valueOf(bedroomsNP.getValue()),
                                    String.valueOf(bathroomsNP.getValue()), locationET.getText().toString(),
                                    mPrefs.getString("username", "Realtor"), dateFormat.format(date),
                                    null, url, priceET.getText().toString().replace(",", ""), cityET.getText().toString(), typeET.getText().toString()));

                            List<DatabaseHouseItem> doesItWork = database.itemDao().getItems();


                        });
                    });

                });


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

    }

    @SuppressLint("ClickableViewAccessibility")
    private void fetchingAllViewFromXML() {

        //Fetching all TV and ET
        title = findViewById(R.id.title);
        basicRL = findViewById(R.id.basicRL);
        detailRL = findViewById(R.id.detailRL);
        charaRL = findViewById(R.id.charaRL);

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
        cityIcon = findViewById(R.id.cityIcon);
        priceIcon = findViewById(R.id.priceIcon);
        typeIcon = findViewById(R.id.houseTypeIcon);

        //Details
        descriptionTV = findViewById(R.id.descriptionTV);
        descriptionET = findViewById(R.id.descriptionET);
        locationTV = findViewById(R.id.locationTV);
        locationET = findViewById(R.id.locationET);
        locationIcon = findViewById(R.id.locationIcon);
        descriptionIcon = findViewById(R.id.descriptionIcon);
        sidePicturesTV = findViewById(R.id.sidePicturesTV);
        addAPictureButton = findViewById(R.id.addAPictureButton);
        addAPictureIV = findViewById(R.id.sidePicturesIV);
        pictureDescriptionET = findViewById(R.id.photoDescriptionET);
        addButton = findViewById(R.id.addButton);
        horizontalRecyclerViewAdd = findViewById(R.id.horizontalRecyclerViewAdd);
        progressBar = findViewById(R.id.progressBar);

        //Cara
        pointOfInterestTV = findViewById(R.id.pointsOfInterestTV);
        surfaceIcon = findViewById(R.id.surfaceIcon);
        roomsIcon = findViewById(R.id.roomsIcon);
        bedroomsIcon = findViewById(R.id.bedroomsIcon);
        bathroomsIcon = findViewById(R.id.bathroomsIcon);
        interestsIcon = findViewById(R.id.interestsIcon);
        restaurantCB = findViewById(R.id.restaurantCB);
        schoolCB = findViewById(R.id.schoolCB);
        parkCB = findViewById(R.id.parkCB);


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
            progressBar.setVisibility(View.INVISIBLE);
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

            Picasso.get().load(mainImageUri).into(mainPicImageView);
            setCorrectTVs();
        }
        if (requestCode == PICK_IMAGE_REQUEST_ADD && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).into(addAPictureIV);
            setCorrectTVs();
        }
    }

    public void adapterSendsList(int position) {
        listOfPicturesAndDesc = adapter.getUpdatedlist();
        listOfPicturesAndDesc.remove(position);
        adapter.notifyDataSetChanged();
    }
}


