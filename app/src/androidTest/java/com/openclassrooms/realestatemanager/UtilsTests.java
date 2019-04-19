package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.realestatemanager.Models.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UtilsTests {
    private boolean internetConnection;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.openclassrooms.realestatemanager", appContext.getPackageName());
    }

    @Test
    public void doDollarsConvertToEuros() throws Exception {

        int convertedValue = Utils.convertDollarToEuro(1000);
        int convertedValue1 = Utils.convertDollarToEuro(1500);
        int convertedValue2 = Utils.convertDollarToEuro(300);
        int convertedValue3 = Utils.convertDollarToEuro(12000);
        assertEquals(convertedValue, 884);
        assertEquals(convertedValue1, 1325);
        assertEquals(convertedValue2, 265);
        assertEquals(convertedValue3, 10602);

    }

    @Test
    public void doEurosConvertToDollars() throws Exception {

        int convertedValue = Utils.convertEuroToDollar(1000);
        int convertedValue1 = Utils.convertEuroToDollar(1500);
        int convertedValue2 = Utils.convertEuroToDollar(300);
        int convertedValue3 = Utils.convertEuroToDollar(12000);
        assertEquals(convertedValue, 1132);
        assertEquals(convertedValue1, 1698);
        assertEquals(convertedValue2, 340);
        assertEquals(convertedValue3, 13583);

    }

    @Test
    public void doesInternetWork() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        internetConnection = false;



        //Task that needs internet (if nothing then API call but cba) then internetConnection = true

        SystemClock.sleep(1000); //Waiting for internet check
        assertEquals(Utils.isInternetAvailable(appContext), internetConnection);

    }

    @Test
    public void isTodaysDateExact() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();

        //or write today's date manually to really check

        assertEquals(Utils.getTodayDate(), dateFormat.format(date));

    }




}
