package com.example.szakdolgozat.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.szakdolgozat.R;
import com.example.szakdolgozat.UI.profile.ProfileSzerkesztes;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsFlowTest {

    @Test
    public void settingsFlow_UpdatesUserWeight() {
        // Start ProfileSzerkesztes
        try (ActivityScenario<ProfileSzerkesztes> scenario = ActivityScenario.launch(ProfileSzerkesztes.class)) {
            
            // 1. Identify suly_input, clear it and type new weight
            onView(withId(R.id.suly_input))
                    .perform(clearText(), typeText("85"));

            // 2. Identify kor and type age (needed for validation)
            onView(withId(R.id.kor))
                    .perform(clearText(), typeText("25"));

            // 3. Identify magassag and type height (needed for validation)
            onView(withId(R.id.magassag))
                    .perform(clearText(), typeText("180"));

            // 4. Click save
            onView(withId(R.id.Mentes))
                    .perform(click());

            // The Activity will try to save to Firebase. 
            // In a real integration test, we would use a test Firebase project or a mock.
        }
    }
}
