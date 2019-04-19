package com.openclassrooms.realestatemanager;

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.Activities.AddActivity;
import com.openclassrooms.realestatemanager.Activities.EditActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EditActivityUITest {
    @Rule
    public ActivityTestRule<EditActivity> editActivityActivityTestRule =
            new ActivityTestRule<>(EditActivity.class);

    private EditActivity mActivity = null;

    private Instrumentation.ActivityMonitor displayEditActivityMonitor =
            getInstrumentation().addMonitor(
                    EditActivity.class.getName(),
                    null,
                    false);

    Button nextButton;
    Button backButton;
    TextView title;


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.openclassrooms.realestatemanager", appContext.getPackageName());
    }

    @Test
    public void backAndNextButtons() throws Exception {

        mActivity = editActivityActivityTestRule.getActivity();

        title = mActivity.findViewById(R.id.title);

        //When the activity opens the title should start with the basics
        assertEquals(title.getText(), "Basics");

        //When the user clicks on the next Button the title should show the characteristics
        onView(withId(R.id.nextButton)).perform(click()).check(matches(isDisplayed()));
        assertEquals(title.getText(), "Details");

        //When the user clicks on the next Button the title should show the details
        onView(withId(R.id.nextButton)).perform(click()).check(matches(isDisplayed()));
        assertEquals(title.getText(), "Characteristics");

        //When the user clicks on the next Button the title should show the details
        onView(withId(R.id.backButton)).perform(click()).check(matches(isDisplayed()));
        assertEquals(title.getText(), "Details");

    }
    @Test
    public void correctAndFunctionalEditTextsForEverySection() throws Exception {
        //This should work if all the fields have values
        mActivity = editActivityActivityTestRule.getActivity();

        EditText houseTypeET = mActivity.findViewById(R.id.houseTypeET);
        EditText cityET = mActivity.findViewById(R.id.cityET);
        EditText descriptionET = mActivity.findViewById(R.id.descriptionET);
        EditText locationET = mActivity.findViewById(R.id.locationET);
        EditText photoDescriptionET = mActivity.findViewById(R.id.photoDescriptionET);
        EditText surfaceET = mActivity.findViewById(R.id.surfaceET);

        SystemClock.sleep(2000); //Waiting a little bit just to load data from Firestore/Room
        assertNotEquals("", cityET.getText().toString());
        assertNotEquals("", descriptionET.getText().toString());
        assertNotEquals("", locationET.getText().toString());
        assertNotEquals("", surfaceET.getText().toString());
        assertNotEquals("", houseTypeET.getText().toString());

        cityET.setText("");
        descriptionET.setText("");
        locationET.setText("");
        surfaceET.setText("");
        houseTypeET.setText("");

        //1st section
        onView(withId(R.id.houseTypeET)).perform(typeText("Big Mansion")).check(matches(isDisplayed()));
        onView(withId(R.id.cityET)).perform(typeText("Gotham City")).check(matches(isDisplayed()));
        assertEquals("Big Mansion", houseTypeET.getText().toString());
        assertEquals("Gotham City", cityET.getText().toString());
        Espresso.closeSoftKeyboard();

        //2nd section
        onView(withId(R.id.nextButton)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionET)).perform(typeText("Description blah blah blah")).check(matches(isDisplayed()));
        onView(withId(R.id.locationET)).perform(typeText("3310 Vine St, Calabasas, CA 45004, US")).check(matches(isDisplayed()));
        onView(withId(R.id.photoDescriptionET)).perform(typeText("kitchen")).check(matches(isDisplayed()));
        assertEquals("Description blah blah blah", descriptionET.getText().toString());
        assertEquals("3310 Vine St, Calabasas, CA 45004, US", locationET.getText().toString());
        assertEquals("kitchen", photoDescriptionET.getText().toString());
        Espresso.closeSoftKeyboard();

        //3rd section |||||| Here we are trying to right a letter in an number only ET so the m should not be displayed
        onView(withId(R.id.nextButton)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.surfaceET)).perform(typeText("225m")).check(matches(isDisplayed()));
        assertEquals("225", surfaceET.getText().toString());

    }

    @Test
    public void priceEditTextWithComas() throws Exception {
        //We'll do the price ET separately as it is a bit more delicate

        mActivity = editActivityActivityTestRule.getActivity();
        EditText priceET = mActivity.findViewById(R.id.priceET);

        SystemClock.sleep(2000); //Waiting a little bit just to load data from Firestore/Room
        assertNotEquals("", priceET.getText().toString());

        priceET.setText("");
        onView(withId(R.id.priceET)).perform(typeText("111")).check(matches(isDisplayed()));
        assertEquals("111", priceET.getText().toString());
        priceET.setText("");
        onView(withId(R.id.priceET)).perform(typeText("11122")).check(matches(isDisplayed()));
        assertEquals("11,122", priceET.getText().toString());
        priceET.setText("");
        onView(withId(R.id.priceET)).perform(typeText("111222333")).check(matches(isDisplayed()));
        assertEquals("111,222,333", priceET.getText().toString());
        priceET.setText("");

        //And once again, no letters
        onView(withId(R.id.priceET)).perform(typeText("111222333abcdefghijklmnopqrstuvwxyz")).check(matches(isDisplayed()));
        assertEquals("111,222,333", priceET.getText().toString());


    }
}
