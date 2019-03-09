package com.openclassrooms.realestatemanager.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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
import com.openclassrooms.realestatemanager.Models.FirebaseRequestHandler;
import com.openclassrooms.realestatemanager.Models.HorizontalRecyclerViewItem;
import com.openclassrooms.realestatemanager.Models.MyHorizontalAdapter;
import com.openclassrooms.realestatemanager.Models.MyHorizontalPictureAdapter;
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

public class EditActivity extends AppCompatActivity {

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
    Button soldButton;
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
    Boolean mainPicGetsChanged = false;
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
    RecyclerView mRecyclerViewPOI;
    ArrayList<HorizontalRecyclerViewItem> listOfPicturesAndDesc = new ArrayList<>();
    ArrayList<String> arrayOfPics = new ArrayList<>();
    ArrayList<String> arrayOfDesc = new ArrayList<>();
    ArrayList<String> interestArrayFromDB = new ArrayList<>();
    Boolean sold = false;
    Boolean soldStatusChanged = false;
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private MyHorizontalPictureAdapter adapter;

    private CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("house");

    private CollectionReference mCollectionReference;

    int intId;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST_ADD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        fetchingAllViewFromXML();
        setActionDoneOnEditTexts();
        setInitialViews();
        setButtonAction();
        setImagePickers();



        soldButton.setOnClickListener(v -> {
            if (sold) {
                soldButton.setText("Sold?");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    soldButton.setBackgroundColor(Color.parseColor("#696969"));
                }
                soldStatusChanged = true;
                sold = false;

            } else {
                soldButton.setText("Sold!");
                soldButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                soldStatusChanged = true;
                sold = true;
            }
        });

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
            uploadTask.addOnSuccessListener(this, taskSnapshot -> taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {

                urlAdd = uri.toString();
                listOfPicturesAndDesc.add(new HorizontalRecyclerViewItem(urlAdd, pictureDescriptionET.getText().toString()));
                arrayOfPics.add(urlAdd);
                arrayOfDesc.add(pictureDescriptionET.getText().toString());
                addAPictureIV.setImageResource(0);
                pictureDescriptionET.setText("");

                adapter = new MyHorizontalPictureAdapter(this, listOfPicturesAndDesc);

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

                    if (mainPicGetsChanged) {


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


                                url = uri.toString();
                            });
                        });
                    }
                        // SAVE EVERYTHING TO FIRESTOOOORE HERE

                        DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("house").document();
                        Intent iin = getIntent();
                        Bundle b = iin.getExtras();
                        String id;

                        if (b != null) {
                            id = (String) b.get("id");
                        } else {
                            id = mPrefs.getString("id", null);
                        }
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (id.equals(documentSnapshot.get("id"))) {

                                Map<String, Object> dataToSave = new HashMap<>();

                                dataToSave.put("city", cityET.getText().toString());
                                dataToSave.put("description", descriptionET.getText().toString());
                                dataToSave.put("location", locationET.getText().toString());
                                dataToSave.put("numberOfBathrooms", String.valueOf(bathroomsNP.getValue()));
                                dataToSave.put("numberOfBedrooms", String.valueOf(bedroomsNP.getValue()));
                                dataToSave.put("numberOfRooms", String.valueOf(roomsNP.getValue()));
                                if (mainPicGetsChanged) {
                                    dataToSave.put("mainPicture", url);
                                }
                                dataToSave.put("pictures", arrayOfPics);
                                dataToSave.put("rooms", adapter.getUpdatedlist());

                                if (soldStatusChanged) {
                                    if (sold) {
                                        dataToSave.put("sold", dateFormat.format(date));
                                    } else {
                                        dataToSave.put("sold", null);
                                    }
                                }

                                if (mPrefs.getStringSet("interests", null) != null) {
                                    List<String> newArray = new ArrayList<>(mPrefs.getStringSet("interests", null));
                                    dataToSave.put("pointOfInterest", newArray);
                                    mPrefs.edit().putStringSet("interests", null).apply();
                                } else {
                                    dataToSave.put("pointOfInterest", interestsArray);
                                }
                                dataToSave.put("price", priceET.getText().toString());
                                dataToSave.put("surface", surfaceET.getText().toString());
                                dataToSave.put("type", typeET.getText().toString());


                                notebookRef.document(documentSnapshot.getId()).set(dataToSave, SetOptions.merge());
                            }
                        }
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
        locationIcon.setVisibility(View.INVISIBLE);
        descriptionIcon.setVisibility(View.INVISIBLE);
        roomsIcon.setVisibility(View.INVISIBLE);
        bedroomsIcon.setVisibility(View.INVISIBLE);
        bathroomsIcon.setVisibility(View.INVISIBLE);
        interestsIcon.setVisibility(View.INVISIBLE);
        surfaceIcon.setVisibility(View.INVISIBLE);
        soldButton.setVisibility(View.VISIBLE);

        typeTV.setVisibility(View.VISIBLE);
        typeET.setVisibility(View.VISIBLE);
        priceTV.setVisibility(View.VISIBLE);
        priceET.setVisibility(View.VISIBLE);
        cityTV.setVisibility(View.VISIBLE);
        cityET.setVisibility(View.VISIBLE);
        uploadFileTV.setVisibility(View.VISIBLE);
        mainPicImageView.setVisibility(View.VISIBLE);
        Picasso.get().load("https://maps.googleapis.com/maps/api/staticmap?center=Berkeley,CA&zoom=14&size=400x400&key=AIzaSyCK4wzbd9vuzvZ9DoIzQZv51TUs-MkQ7eI").into(mainPicImageView);
        chooseMainPicButton.setVisibility(View.VISIBLE);
        typeIcon.setVisibility(View.VISIBLE);
        priceIcon.setVisibility(View.VISIBLE);
        cityIcon.setVisibility(View.VISIBLE);
        sidePicturesTV.setVisibility(View.INVISIBLE);
        addAPictureButton.setVisibility(View.INVISIBLE);
        addAPictureIV.setVisibility(View.INVISIBLE);
        pictureDescriptionET.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.INVISIBLE);
        horizontalRecyclerViewAdd.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        nextButton.setText("Next (1/3)");

        //Room numberpickers

        bathroomsNP.setMinValue(0);
        bathroomsNP.setMaxValue(20);

        bedroomsNP.setMinValue(0);
        bedroomsNP.setMaxValue(25);

        roomsNP.setMinValue(0);
        roomsNP.setMaxValue(40);

        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {
            Intent iin = getIntent();
            Bundle b = iin.getExtras();
            String id;

            if (b != null) {
                id = (String) b.get("id");
            } else {
                id = mPrefs.getString("id", null);
            }

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                if (id.equals(documentSnapshot.get("id"))) {
                    typeET.setText((String) documentSnapshot.get("type"));
                    sold = documentSnapshot.get("sold") != null;
                    if (sold) {
                        soldButton.setText("Sold!");
                        soldButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        soldButton.setText("Sold?");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            soldButton.setBackgroundColor(Color.parseColor("#696969"));
                        }
                    }
                    priceET.setText((String) documentSnapshot.get("price"));
                    ArrayList<String> size = (ArrayList) documentSnapshot.get("pictures");
                    ArrayList<String> size2 = (ArrayList) documentSnapshot.get("rooms");
                    for (int i = 0; i<size.size(); i++){
                        listOfPicturesAndDesc.add(new HorizontalRecyclerViewItem(size.get(i), size2.get(i)));
                    }
                    adapter = new MyHorizontalPictureAdapter(this, listOfPicturesAndDesc);
                    horizontalRecyclerViewAdd.setAdapter(adapter);


                    cityET.setText((String) documentSnapshot.get("city"));

//                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl();

                    Picasso picassoInstance = new  Picasso.Builder(this.getApplicationContext())
                            .addRequestHandler(new FirebaseRequestHandler())
                            .build();
                    picassoInstance.load((String) documentSnapshot.get("mainPicture"))
                            .fit().centerInside()
                            .into(mainPicImageView);

                 //   Picasso.get().load((String) documentSnapshot.get("mainPicture")).into(mainPicImageView);
                    descriptionET.setText((String) documentSnapshot.get("description"));
                    locationET.setText((String) documentSnapshot.get("location"));
                    //RECYCLER
                    surfaceET.setText((String) documentSnapshot.get("surface"));
                    surfaceET.setText(surfaceET.getText().toString().replace("m²", ""));

                    bathroomsNP.setValue(Integer.valueOf((String) documentSnapshot.get("numberOfBathrooms")));
                    bedroomsNP.setValue(Integer.valueOf((String) documentSnapshot.get("numberOfBedrooms")));
                    roomsNP.setValue(Integer.valueOf((String) documentSnapshot.get("numberOfRooms")));
                    mRecyclerViewPOI.setAdapter(new SimpleRVAdapter(this, (List<String>) documentSnapshot.get("pointOfInterest")));


                }
            }
        });
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
        cityIcon = findViewById(R.id.cityIcon);
        priceIcon = findViewById(R.id.priceIcon);
        typeIcon = findViewById(R.id.houseTypeIcon);
        soldButton = findViewById(R.id.soldButton);

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
        pointOfInterestET = findViewById(R.id.pointsOfInterestET);
        mRecyclerViewPOI = findViewById(R.id.recyclerViewPOI);
        surfaceIcon = findViewById(R.id.surfaceIcon);
        roomsIcon = findViewById(R.id.roomsIcon);
        bedroomsIcon = findViewById(R.id.bedroomsIcon);
        bathroomsIcon = findViewById(R.id.bathroomsIcon);
        interestsIcon = findViewById(R.id.interestsIcon);

        mRecyclerViewPOI.setLayoutManager(new LinearLayoutManager(this));

        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {
            Intent iin = getIntent();
            Bundle b = iin.getExtras();
            String id;
            if (b != null) {
                id = (String) b.get("id");
            } else {
                SharedPreferences mPrefs = getSharedPreferences("SHARED", Context.MODE_PRIVATE);
                id = mPrefs.getString("id", null);
            }
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (id.equals(documentSnapshot.get("id"))) {
                    interestArrayFromDB = (ArrayList<String>) documentSnapshot.get("pointOfInterest");
                }
            }
        });

        pointOfInterestET.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (pointOfInterestET.getRight() - pointOfInterestET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (numberMaxPOI == 0) {
                        numberMaxPOI = interestArrayFromDB.size();
                        interestsArray = interestArrayFromDB;
                    }
                    numberMaxPOI++;

                    if (numberMaxPOI <= 5) {
                        interestsArray.add(pointOfInterestET.getText().toString());
                        pointOfInterestET.setText("");
                    } else {
                        Toast.makeText(this, "You have reached the limit", Toast.LENGTH_SHORT).show();
                        pointOfInterestET.setText("");
                    }
                    mRecyclerViewPOI.setAdapter(new SimpleRVAdapter(this, interestsArray));
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
                mRecyclerViewPOI.setAdapter(new SimpleRVAdapter(this, interestsArray));
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
            locationIcon.setVisibility(View.INVISIBLE);
            descriptionIcon.setVisibility(View.INVISIBLE);
            uploadFileTV.setVisibility(View.VISIBLE);
            mainPicImageView.setVisibility(View.VISIBLE);
            chooseMainPicButton.setVisibility(View.VISIBLE);
            roomsIcon.setVisibility(View.INVISIBLE);
            bedroomsIcon.setVisibility(View.INVISIBLE);
            bathroomsIcon.setVisibility(View.INVISIBLE);
            interestsIcon.setVisibility(View.INVISIBLE);
            surfaceIcon.setVisibility(View.INVISIBLE);
            soldButton.setVisibility(View.VISIBLE);
            mRecyclerViewPOI.setVisibility(View.INVISIBLE);

            typeTV.setVisibility(View.VISIBLE);
            typeET.setVisibility(View.VISIBLE);
            priceTV.setVisibility(View.VISIBLE);
            priceET.setVisibility(View.VISIBLE);
            cityTV.setVisibility(View.VISIBLE);
            cityET.setVisibility(View.VISIBLE);
            typeIcon.setVisibility(View.VISIBLE);
            priceIcon.setVisibility(View.VISIBLE);
            cityIcon.setVisibility(View.VISIBLE);
            sidePicturesTV.setVisibility(View.INVISIBLE);
            addAPictureButton.setVisibility(View.INVISIBLE);
            addAPictureIV.setVisibility(View.INVISIBLE);
            pictureDescriptionET.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.INVISIBLE);
            horizontalRecyclerViewAdd.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

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
            typeIcon.setVisibility(View.INVISIBLE);
            priceIcon.setVisibility(View.INVISIBLE);
            cityIcon.setVisibility(View.INVISIBLE);
            locationIcon.setVisibility(View.VISIBLE);
            descriptionIcon.setVisibility(View.VISIBLE);
            roomsIcon.setVisibility(View.INVISIBLE);
            bedroomsIcon.setVisibility(View.INVISIBLE);
            bathroomsIcon.setVisibility(View.INVISIBLE);
            interestsIcon.setVisibility(View.INVISIBLE);
            surfaceIcon.setVisibility(View.INVISIBLE);
            sidePicturesTV.setVisibility(View.VISIBLE);
            addAPictureButton.setVisibility(View.VISIBLE);
            addAPictureIV.setVisibility(View.VISIBLE);
            pictureDescriptionET.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            horizontalRecyclerViewAdd.setVisibility(View.VISIBLE);
            soldButton.setVisibility(View.INVISIBLE);
            mRecyclerViewPOI.setVisibility(View.INVISIBLE);

        } else if (mPrefs.getInt("addNumber", 1) == 3) {
            nextButton.setText("Edit listing");
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
            soldButton.setVisibility(View.INVISIBLE);
            bathroomsNP.setVisibility(View.VISIBLE);
            bedroomsNP.setVisibility(View.VISIBLE);
            roomsNP.setVisibility(View.VISIBLE);
            uploadFileTV.setVisibility(View.INVISIBLE);
            chooseMainPicButton.setVisibility(View.INVISIBLE);
            mainPicImageView.setVisibility(View.INVISIBLE);
            typeIcon.setVisibility(View.INVISIBLE);
            priceIcon.setVisibility(View.INVISIBLE);
            cityIcon.setVisibility(View.INVISIBLE);
            locationIcon.setVisibility(View.INVISIBLE);
            descriptionIcon.setVisibility(View.INVISIBLE);
            roomsIcon.setVisibility(View.VISIBLE);
            bedroomsIcon.setVisibility(View.VISIBLE);
            bathroomsIcon.setVisibility(View.VISIBLE);
            interestsIcon.setVisibility(View.VISIBLE);
            surfaceIcon.setVisibility(View.VISIBLE);
            sidePicturesTV.setVisibility(View.INVISIBLE);
            addAPictureButton.setVisibility(View.INVISIBLE);
            addAPictureIV.setVisibility(View.INVISIBLE);
            pictureDescriptionET.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.INVISIBLE);
            horizontalRecyclerViewAdd.setVisibility(View.INVISIBLE);
            mRecyclerViewPOI.setVisibility(View.VISIBLE);

        }
    }

    private void setImagePickers() {
        chooseMainPicButton.setOnClickListener(v -> {
            mainPicGetsChanged = true;
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

}
