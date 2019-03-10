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
    RelativeLayout basicRL;
    RelativeLayout detailRL;
    RelativeLayout charaRL;

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


                            interestsArray.clear();
                            if (schoolCB.isChecked()){
                                interestsArray.add("School");
                            }if (restaurantCB.isChecked()){
                                interestsArray.add("Restaurant");
                            }if (parkCB.isChecked()){
                                interestsArray.add("Park");
                            }
                            dataToSave.put("pointOfInterest", interestsArray);
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

        basicRL.setVisibility(View.VISIBLE);
        charaRL.setVisibility(View.INVISIBLE);
        detailRL.setVisibility(View.INVISIBLE);

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
                    for (int i = 0; i < size.size(); i++) {
                        listOfPicturesAndDesc.add(new HorizontalRecyclerViewItem(size.get(i), size2.get(i)));
                    }
                    adapter = new MyHorizontalPictureAdapter(this, listOfPicturesAndDesc);
                    horizontalRecyclerViewAdd.setAdapter(adapter);


                    cityET.setText((String) documentSnapshot.get("city"));

//                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl();

                    Picasso picassoInstance = new Picasso.Builder(this.getApplicationContext())
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
                    interestsArray = (ArrayList<String>) documentSnapshot.get("pointOfInterest");

                    for (int i = 0; i < interestsArray.size(); i++){
                        if (interestsArray.get(i).equalsIgnoreCase("Park")){
                            parkCB.setChecked(true);
                        }
                        if (interestsArray.get(i).equalsIgnoreCase("Restaurant")){
                            restaurantCB.setChecked(true);
                        }
                        if (interestsArray.get(i).equalsIgnoreCase("School")){
                            schoolCB.setChecked(true);
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void fetchingAllViewFromXML() {

        //Fetching all TV and ET
        title = findViewById(R.id.title);
        basicRL = findViewById(R.id.basicRL);
        charaRL = findViewById(R.id.charaRL);
        detailRL = findViewById(R.id.detailRL);

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
        surfaceIcon = findViewById(R.id.surfaceIcon);
        roomsIcon = findViewById(R.id.roomsIcon);
        bedroomsIcon = findViewById(R.id.bedroomsIcon);
        bathroomsIcon = findViewById(R.id.bathroomsIcon);
        interestsIcon = findViewById(R.id.interestsIcon);
        restaurantCB = findViewById(R.id.restaurantCB);
        schoolCB = findViewById(R.id.schoolCB);
        parkCB = findViewById(R.id.parkCB);

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

            basicRL.setVisibility(View.VISIBLE);
            charaRL.setVisibility(View.INVISIBLE);
            detailRL.setVisibility(View.INVISIBLE);

        } else if (mPrefs.getInt("addNumber", 1) == 2) {
            backButton.setText("Back");
            nextButton.setText("Next (2/3)");

            title.setText("Details");
            progressBar.setVisibility(View.INVISIBLE);
            basicRL.setVisibility(View.INVISIBLE);
            charaRL.setVisibility(View.INVISIBLE);
            detailRL.setVisibility(View.VISIBLE);

        } else if (mPrefs.getInt("addNumber", 1) == 3) {
            nextButton.setText("Edit listing");
            backButton.setText("Back");

            title.setText("Characteristics");
            basicRL.setVisibility(View.INVISIBLE);
            charaRL.setVisibility(View.VISIBLE);
            detailRL.setVisibility(View.INVISIBLE);

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
