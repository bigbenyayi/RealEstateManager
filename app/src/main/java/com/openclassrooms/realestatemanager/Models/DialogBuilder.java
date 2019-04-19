package com.openclassrooms.realestatemanager.Models;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.openclassrooms.realestatemanager.Activities.MainActivity;
import com.openclassrooms.realestatemanager.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DialogBuilder extends AppCompatDialogFragment {

    //Crystal Range Seekbars and their Textviews
    CrystalRangeSeekbar surfaceCRS;
    TextView surfaceIndicator;
    CrystalRangeSeekbar roomsCRS;
    TextView roomIndicator;
    CrystalRangeSeekbar photosCRS;
    TextView photosIndicator;
    CrystalRangeSeekbar priceCRS;
    TextView priceTV;

    //Edittext
    EditText cityET;

    //Checkboxes
    CheckBox availbleCB;
    CheckBox soldCB;
    CheckBox parkCB;
    CheckBox restaurantCB;
    CheckBox schoolCB;

    //Date related
    EditText beginDateET;
    EditText endDateET;
    String beginDate;
    String endDateString;

    //Global for methods
    View view;
    AlertDialog.Builder builder;

    //Pass data via interface
    private AlertDialogListener mListener;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflate = Objects.requireNonNull(getActivity()).getLayoutInflater();
        view = inflate.inflate(R.layout.layout_dialog, null);

        builder = new AlertDialog.Builder(getActivity());

        ///////////////////////////////////////////////////////////

        setupCrystalRangeSeekbars();
        setupAlertDialogBasics();
        setupCheckBoxes();
        setupDatePicker();

        ///////////////////////////////////////////////////////////

        return builder.create();
    }

    private void setupDatePicker() {
        beginDateET = view.findViewById(R.id.editTextBeginDate);
        endDateET = view.findViewById(R.id.editTextEndDate);
        Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                beginDateET.setText(sdf.format(myCalendar.getTime()));
                beginDate = sdf.format(myCalendar.getTime());

            }
        };

        beginDateET.setOnClickListener(v -> {
            new DatePickerDialog(Objects.requireNonNull(getContext()), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                endDateET.setText(sdf.format(myCalendar.getTime()));
                endDateString = sdf.format(myCalendar.getTime());

            }
        };

        endDateET.setOnClickListener(v -> {
            new DatePickerDialog(Objects.requireNonNull(getContext()), endDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


    }

    private void setupCheckBoxes() {
        availbleCB = view.findViewById(R.id.availableCB);
        soldCB = view.findViewById(R.id.soldCB);
        parkCB = view.findViewById(R.id.parkCB);
        restaurantCB = view.findViewById(R.id.restaurantCB);
        schoolCB = view.findViewById(R.id.schoolCB);
    }

    private void setupAlertDialogBasics() {

        builder.setView(view).setTitle("Search")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    mListener.fetchData(null, 80, 80, false, false, null, null, 80, 80, false,
                            false, false, 80, 80, 80, 80);
                    SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", MODE_PRIVATE);
                    mPrefs.edit().putInt("roomsMax", 80).apply();
                    Intent myIntent = new Intent(getContext(), MainActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(myIntent);

                })
                .setPositiveButton("Search", (dialog, which) -> {
                    fetchAllData();
                    Intent myIntent = new Intent(getContext(), MainActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(myIntent);
                });
    }

    private void setupCrystalRangeSeekbars() {

        View surfaceIncludeView = view.findViewById(R.id.card_view_surface_area_id);
        View roomsIncludeView = view.findViewById(R.id.card_view_rooms_id);
        View photosIncludeView = view.findViewById(R.id.card_view_photos_id);
        View priceIncludeView = view.findViewById(R.id.card_view_price_id);

        //////////////////////////

        surfaceCRS = surfaceIncludeView.findViewById(R.id.surfaceCRS);
        surfaceIndicator = surfaceIncludeView.findViewById(R.id.surfaceTV);
        surfaceCRS.setOnRangeSeekbarChangeListener((minValue, maxValue) -> surfaceIndicator.setText("Surface in m² (" + minValue + " - " + maxValue + ")"));

        //////////////////////////

        roomsCRS = roomsIncludeView.findViewById(R.id.roomCRS);
        roomIndicator = roomsIncludeView.findViewById(R.id.roomsTV);
        roomsCRS.setOnRangeSeekbarChangeListener((minValue, maxValue) -> roomIndicator.setText("Rooms (" + minValue + " - " + maxValue + ")"));

        //////////////////////////

        photosCRS = photosIncludeView.findViewById(R.id.photosCRS);
        photosIndicator = photosIncludeView.findViewById(R.id.photosTV);
        photosCRS.setOnRangeSeekbarChangeListener((minValue, maxValue) -> photosIndicator.setText("Number of pictures available (" + minValue + " - " + maxValue + ")"));

        //////////////////////////

        priceCRS = priceIncludeView.findViewById(R.id.priceCRS);
        priceTV = priceIncludeView.findViewById(R.id.priceTV);
        priceCRS.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            String minValueString = String.format("%,d", minValue);
            String maxValueString = String.format("%,d", maxValue);
            priceTV.setText("Price in $ (" + minValueString + " - " + maxValueString + ")");
        });
    }

    private void fetchAllData() {
        cityET = view.findViewById(R.id.cityAlertET);

        mListener.fetchData(cityET.getText().toString(),
                roomsCRS.getSelectedMinValue().intValue(),
                roomsCRS.getSelectedMaxValue().intValue(),
                soldCB.isChecked(),
                availbleCB.isChecked(),
                beginDate,
                endDateString,
                photosCRS.getSelectedMinValue().intValue(),
                photosCRS.getSelectedMaxValue().intValue(),
                parkCB.isChecked(),
                restaurantCB.isChecked(),
                schoolCB.isChecked(),
                surfaceCRS.getSelectedMinValue().intValue(),
                surfaceCRS.getSelectedMaxValue().intValue(),
                priceCRS.getSelectedMinValue().intValue(),
                priceCRS.getSelectedMaxValue().intValue());

    }

    public interface AlertDialogListener {
        void fetchData(String city, int roomsMin, int roomsMax, boolean sold, boolean available,
                       String beginDate, String endDate, int photosMin, int photosMax, boolean park,
                       boolean school, boolean restaurant, int surfaceMin, int surfaceMax, int priceMin, int priceMax);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (AlertDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AlertDialogListener");
        }
    }
}
