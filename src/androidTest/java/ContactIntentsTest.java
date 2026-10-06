package com.example.android.testing.espresso.IntentsBasicSample;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.net.Uri;
import java.util.Collections;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ContactIntentsTest {
    @Rule public GrantPermissionRule phonePermission =
            GrantPermissionRule.grant(Manifest.permission.CALL_PHONE);
    private ActivityScenario<DialerActivity> screen;

    @Before public void openDialer() {
        Intents.init();
        screen = ActivityScenario.launch(DialerActivity.class);
        intended(allOf(hasAction(Intent.ACTION_MAIN),
                hasCategories(Collections.singleton(Intent.CATEGORY_LAUNCHER)),
                hasComponent(DialerActivity.class.getName())));
        intending(hasAction(Intent.ACTION_CALL)).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @After public void closeDialer() {
        if (screen != null) screen.close();
        Intents.release();
    }

    @Test public void dialUsesTheExactTypedNumber() {
        enterNumber("16502530000");
        onView(withId(R.id.button_call_number)).perform(click());
        intended(allOf(hasAction(Intent.ACTION_CALL), hasData(Uri.parse("tel:16502530000"))));
        assertNoUnverifiedIntents();
    }

    @Test public void realContactActivityReturnsItsSampleNumber() {
        onView(withId(R.id.button_pick_contact)).perform(click());
        intended(hasComponent(ContactsActivity.class.getName()));
        onView(withId(R.id.edit_text_caller_number)).check(matches(withText("896-745-231")));
        assertNoUnverifiedIntents();
    }

    @Test public void selectedContactReplacesPreviousInput() {
        enterNumber("1112223333");
        Intent result = new Intent().putExtra("key_phone_number", "2025550147");
        intending(hasComponent(ContactsActivity.class.getName()))
                .respondWith(new ActivityResult(Activity.RESULT_OK, result));
        onView(withId(R.id.button_pick_contact)).perform(click());
        onView(withId(R.id.edit_text_caller_number)).check(matches(withText("2025550147")));
        intended(hasComponent(ContactsActivity.class.getName()));
        assertNoUnverifiedIntents();
    }

    @Test public void cancelledContactSelectionPreservesTypedNumber() {
        enterNumber("2025550182");
        intending(hasComponent(ContactsActivity.class.getName()))
                .respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
        onView(withId(R.id.button_pick_contact)).perform(click());
        onView(withId(R.id.edit_text_caller_number)).check(matches(withText("2025550182")));
        intended(hasComponent(ContactsActivity.class.getName()));
        assertNoUnverifiedIntents();
    }

    @Test public void activityRecreationPreservesUnsubmittedInput() {
        enterNumber("2025550163");
        screen.recreate();
        onView(withId(R.id.edit_text_caller_number)).check(matches(withText("2025550163")));
        assertNoUnverifiedIntents();
    }

    private void enterNumber(String number) {
        onView(withId(R.id.edit_text_caller_number)).perform(replaceText(number), closeSoftKeyboard());
    }
}
